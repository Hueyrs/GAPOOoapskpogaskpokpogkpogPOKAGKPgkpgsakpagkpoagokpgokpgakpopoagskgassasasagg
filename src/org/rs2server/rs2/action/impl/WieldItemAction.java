package org.rs2server.rs2.action.impl;

import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.container.Equipment.EquipmentType;
import org.rs2server.rs2.model.equipment.EquipmentDefinition.Skill;

public class WieldItemAction extends Action {
	
	/**
	 * The item's id.
	 */
	private int id;
	
	/**
	 * The item's slot.
	 */
	private int slot;

	public WieldItemAction(Mob mob, int id, int slot, int ticks) {
		super(mob, ticks);
		this.id = id;
		this.slot = slot;
	}

	@Override
	public CancelPolicy getCancelPolicy() {
		return CancelPolicy.ALWAYS;
	}

	@Override
	public StackPolicy getStackPolicy() {
		return StackPolicy.NEVER;
	}

	@Override
	public AnimationPolicy getAnimationPolicy() {
		return AnimationPolicy.RESET_ALL;
	}

	@Override
	public void execute() {
		this.stop();
		Item item = getMob().getInventory() != null ? getMob().getInventory().get(slot) : null;
		if(item == null || item.getId() != id) {
			return;
		}
		if(!getMob().canEmote()) {
			return;
		}
		if(item.getEquipmentDefinition() == null || item.getEquipmentDefinition().getType() == null) {
			if(getMob().getActionSender() != null) {
				getMob().getActionSender().sendMessage("You can't wear that.");
			}
			return;
		}
		EquipmentType type = Equipment.getType(item);
		
		if(BoundaryManager.isWithinBoundaryNoZ(getMob().getLocation(), "Fremennik Trials")) {
			if(type.getSlot() != Equipment.SLOT_WEAPON && type.getSlot() != Equipment.SLOT_SHIELD && type.getSlot() != Equipment.SLOT_ARROWS) {
				getMob().getActionSender().sendMessage("You can only wield a weapon, shield or ammunition inside the Fremennik Trials minigame.");
				return;
			}
		}
		if(item.getEquipmentDefinition() != null && item.getEquipmentDefinition().getSkillRequirements() != null) {
			for(Skill skill : item.getEquipmentDefinition().getSkillRequirements().keySet()) {
				if(getMob().getSkills().getLevelForExperience(skill.getId()) < item.getEquipmentDefinition().getSkillRequirement(skill.getId())) {
					if(getMob().getActionSender() != null) {
						getMob().getActionSender().sendMessage("You are not high enough level to use this item.");
						String level = "a ";
						if(Skills.SKILL_NAME[skill.getId()].toLowerCase().startsWith("a")) {
							level = "an ";
						}
						level += Skills.SKILL_NAME[skill.getId()].toLowerCase();
						getMob().getActionSender().sendMessage("You need to have " + level + " level of " + item.getEquipmentDefinition().getSkillRequirement(skill.getId()) + ".");
					}
					return;
				}
			}
		}
		long itemCount = item.getCount();
		long equipCount = getMob().getEquipment().getCount(item.getId());
		long totalCount = (itemCount + equipCount);
		if(totalCount > Integer.MAX_VALUE) {
			getMob().getActionSender().sendMessage("Not enough equipment space.");
			return;
		}
		boolean inventoryFiringEvents = getMob().getInventory().isFiringEvents();
		getMob().getInventory().setFiringEvents(false);
		try {
			if(type.getSlot() == 3 || type.getSlot() == 5) {
				if(type == EquipmentType.WEAPON_2H) {
					if(getMob().getEquipment().get(Equipment.SLOT_WEAPON) != null && getMob().getEquipment().get(Equipment.SLOT_SHIELD) != null) {
						if(getMob().getInventory().freeSlots() < 1) {
							if(getMob().getActionSender() != null) {
								getMob().getActionSender().sendMessage("Not enough space in your inventory.");
							}
							return;
						}
						getMob().getInventory().remove(item, slot);
						if(getMob().getEquipment().get(Equipment.SLOT_WEAPON) != null) {
							getMob().getInventory().add(getMob().getEquipment().get(Equipment.SLOT_WEAPON), slot);
							getMob().getEquipment().set(Equipment.SLOT_WEAPON, null);
						}
						if(getMob().getEquipment().get(Equipment.SLOT_SHIELD) != null) {
							getMob().getInventory().add(getMob().getEquipment().get(Equipment.SLOT_SHIELD), slot - 1);
							getMob().getEquipment().set(Equipment.SLOT_SHIELD, null);
						}
						getMob().getEquipment().set(type.getSlot(), item);
						getMob().getCombatState().calculateBonuses();
						if(getMob().getActionSender() != null) {
							getMob().getActionSender().sendBonuses();
						}
						getMob().getInventory().fireItemsChanged();
						return;	
					} else if(getMob().getEquipment().get(Equipment.SLOT_SHIELD) != null && getMob().getEquipment().get(Equipment.SLOT_WEAPON) == null) {
						getMob().getInventory().remove(item, slot);
						getMob().getEquipment().set(Equipment.SLOT_WEAPON, item);
						getMob().getInventory().add(getMob().getEquipment().get(Equipment.SLOT_SHIELD), slot);
						getMob().getEquipment().set(Equipment.SLOT_SHIELD, null);
						getMob().getCombatState().calculateBonuses();
						if(getMob().getActionSender() != null) {
							getMob().getActionSender().sendBonuses();
						}
						getMob().getInventory().fireItemsChanged();
						return;
					}
				}
				if(type.getSlot() == Equipment.SLOT_SHIELD && getMob().getEquipment().get(Equipment.SLOT_WEAPON) != null
						&& getMob().getEquipment().get(Equipment.SLOT_WEAPON).getEquipmentDefinition() != null
						&& getMob().getEquipment().get(Equipment.SLOT_WEAPON).getEquipmentDefinition().getType() == EquipmentType.WEAPON_2H) {
					getMob().getInventory().remove(item, slot);
					getMob().getInventory().add(getMob().getEquipment().get(Equipment.SLOT_WEAPON), slot);
					getMob().getEquipment().set(Equipment.SLOT_WEAPON, null);
					getMob().getEquipment().set(Equipment.SLOT_SHIELD, item);
					getMob().getCombatState().calculateBonuses();
					if(getMob().getActionSender() != null) {
						getMob().getActionSender().sendBonuses();
					}
					getMob().getInventory().fireItemsChanged();
					return;
				}
			}
			getMob().getInventory().remove(item, slot);
			if(getMob().getEquipment().get(type.getSlot()) != null) {
				if(getMob().getEquipment().get(type.getSlot()).getId() == item.getId() 
						&& item.getDefinition().isStackable()) {
					item = new Item(item.getId(), getMob().getEquipment().get(type.getSlot()).getCount() + item.getCount());
				} else {
					if(getMob().getEquipment().get(type.getSlot()).getEquipmentDefinition() != null) {
						for(int i = 0; i < getMob().getEquipment().get(type.getSlot()).getEquipmentDefinition().getBonuses().length; i++) {
							getMob().getCombatState().setBonus(i, getMob().getCombatState().getBonus(i) - getMob().getEquipment().get(type.getSlot()).getEquipmentDefinition().getBonus(i));
						}
					}
					getMob().getInventory().add(getMob().getEquipment().get(type.getSlot()), slot);
				}
			}
			getMob().getEquipment().set(type.getSlot(), item);
			if(item.getEquipmentDefinition() != null) {
				for(int i = 0; i < item.getEquipmentDefinition().getBonuses().length; i++) {
					getMob().getCombatState().setBonus(i, getMob().getCombatState().getBonus(i) + item.getEquipmentDefinition().getBonus(i));
				}
			}
			if(getMob().getActionSender() != null) {
				getMob().getActionSender().sendBonuses();
			}
			getMob().getInventory().fireItemsChanged();
		} finally {
			getMob().getInventory().setFiringEvents(inventoryFiringEvents);
		}
	}

}
