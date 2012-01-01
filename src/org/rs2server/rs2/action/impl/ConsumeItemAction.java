package org.rs2server.rs2.action.impl;

import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.Consumables.Drink;
import org.rs2server.rs2.model.Consumables.Food;
import org.rs2server.rs2.model.Consumables.PotionType;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

public class ConsumeItemAction extends Action {

	/**
	 * The item we are consuming
	 */
	private Item item;
	
	/**
	 * The item's slot.
	 */
	private int slot;
	
	public ConsumeItemAction(Mob mob, Item item, int slot) {
		super(mob, 0);
		this.item = item;
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
		if(getMob().getCombatState().isDead()) {
			return;
		}
		final Food food = Food.forId(item.getId());
		final Drink drink = Drink.forId(item.getId());

		boolean inventoryFiringEvents = getMob().getInventory().isFiringEvents();
		getMob().getInventory().setFiringEvents(false);
		try {
			/**
			 * Food
			 */
			if(food != null && getMob().getCombatState().canEat()) {
				getMob().playAnimation(Animation.create(829));
				if(!food.equals(Food.PURPLE_SWEETS)) {
				getMob().getInventory().remove(item, slot);
				} else {
					getMob().getInventory().remove(new Item(4561));
				}
				/**
				 * Stops the mob from eating for 3 cycles (1.8 secs).
				 */
				getMob().getCombatState().setCanEat(false);
				World.getWorld().submit(new Tickable(3) {
					public void execute() {
						getMob().getCombatState().setCanEat(true);
						this.stop();
					}
				});
				
				/**
				 * Increases the mob's attack delay by 3 cycles
				 * if the current timer is below its max cooldown + 3 cycles.
				 */
				if(getMob().getCombatState().getAttackDelay() + 3 <= getMob().getCombatCooldownDelay() + 3) {
					getMob().getCombatState().increaseAttackDelay(3);
				}
				if(!food.equals(Food.PURPLE_SWEETS)) {
				/**
				 * Send the confirmation message and start the animation.
				 */
				getMob().getActionSender().sendMessage("You eat the " + item.getDefinition().getName().toLowerCase() + ".");
				
				int increasedBy = 0;
				int beforeHitpoints = getMob().getSkills().getLevel(Skills.HITPOINTS);
				
				getMob().getSkills().increaseLevelToMaximum(Skills.HITPOINTS, food.getHeal());
				
				increasedBy = getMob().getSkills().getLevel(Skills.HITPOINTS) - beforeHitpoints;
				if(increasedBy > 0) {
					/**
					 * Only show the message of healing if we actually healed.
					 */
					getMob().getActionSender().sendMessage("It heals some health.");
				}
				/**
				 * If the item has a new id, add it (e.g. cakes decreasing in amount).
				 */
				if(food.getNewId() != -1) {
					getMob().getInventory().add(new Item(food.getNewId(), 1), slot);
				}
				} else {
					
					if(getMob().getWalkingQueue().getEnergy() < 100) {
						int restoreEnergy = getMob().getWalkingQueue().getEnergy() + 20;
						if(restoreEnergy  > 100) {
							restoreEnergy = 100;
						}
						getMob().getWalkingQueue().setEnergy(restoreEnergy);
						if(getMob().getActionSender() != null) {
							getMob().getActionSender().sendRunEnergy();
						}
					}
					int modification = 10;
					switch(Misc.random(1, 3)) {
					case 2:
						modification = 20;
						break;
					case 3:
						modification = 30;
						break;
					}
					getMob().getSkills().increaseLevelToMaximum(Skills.HITPOINTS, modification);
					
					getMob().getActionSender().sendMessage("The sugary goodness heals some energy.");
				}
				
			} else if(drink != null && getMob().getCombatState().canDrink()) {
				/**
				 * Drink
				 */

				for(Hit hit : getMob().getHitQueue()) {
					hit.setDelay(hit.getDelay() + 3);
				}
				getMob().playAnimation(Animation.create(829));
				getMob().getInventory().remove(item, slot);
				
				/**
				 * Stops the mob from drinking for 3 cycles (1.8 secs).
				 */
				getMob().getCombatState().setCanDrink(false);
				World.getWorld().submit(new Tickable(3) {
					public void execute() {
						getMob().getCombatState().setCanDrink(true);
						this.stop();
					}
				});
				
				/**
				 * Potion Types.
				 */
				String potionName = item.getDefinition().getName().toLowerCase().substring(0, item.getDefinition().getName().length() - 3).replaceAll(" potion", "");
				switch(drink.getPotionType()) {
				case NORMAL_POTION:
					getMob().getActionSender().sendMessage("You drink some of your " + potionName + " potion.");
					for(int i = 0; i < drink.getSkills().length; i++) {
						int skill = drink.getSkill(i);
						int modification = (int) Math.floor((drink == Drink.RANGE_POTION ? 4 : 3) + (getMob().getSkills().getLevelForExperience(skill) * 0.1));
						getMob().getSkills().increaseLevelToMaximumModification(skill, modification);
					}
					break;
				case SUPER_POTION:
					getMob().getActionSender().sendMessage("You drink some of your " + potionName + " potion.");
					for(int i = 0; i < drink.getSkills().length; i++) {
						int skill = drink.getSkill(i);
						int modification = (int) Math.floor(5 + (getMob().getSkills().getLevelForExperience(skill) * 0.15));
						getMob().getSkills().increaseLevelToMaximumModification(skill, modification);
					}
					break;
				case PRAYER_POTION:
					getMob().getActionSender().sendMessage("You drink some of your restore prayer potion.");
					for(int i = 0; i < drink.getSkills().length; i++) {
						int skill = drink.getSkill(i);
						int modification = (int) Math.floor(7 + (getMob().getSkills().getLevelForExperience(skill) * 0.25));
						/**
						 * Holy wrench increases prayer restoration.
						 */
						if(skill == Skills.PRAYER) {
							if(getMob().getInventory().contains(6714)) {
								modification++;
								if(getMob().getSkills().getLevelForExperience(Skills.PRAYER) >= 40) {
									modification++;
								}
								if(getMob().getSkills().getLevelForExperience(Skills.PRAYER) >= 70) {
									modification++;
								}
							}
							getMob().getSkills().increasePrayerPoints(modification);
						} else {
							getMob().getSkills().increaseLevelToMaximum(skill, modification);
						}
					}
					break;
				case RESTORE:
				case SUPER_RESTORE:
					getMob().getActionSender().sendMessage("You drink some of your " + potionName + " potion.");
					for(int i = 0; i < drink.getSkills().length; i++) {
						int skill = drink.getSkill(i);
						int modification = (int) (getMob().getSkills().getLevelForExperience(skill) / 3);
						/**
						 * Holy wrench increases prayer restoration.
						 */
						if(skill == Skills.PRAYER) {
							if(getMob().getInventory().contains(6714)) {
								modification++;
								if(getMob().getSkills().getLevelForExperience(Skills.PRAYER) >= 40) {
									modification++;
								}
								if(getMob().getSkills().getLevelForExperience(Skills.PRAYER) >= 70) {
									modification++;
								}
							}
							getMob().getSkills().increasePrayerPoints(modification);
						} else {
							getMob().getSkills().increaseLevelToMaximum(skill, modification);
						}
					}
					break;
				case PLUS_5:
					getMob().getActionSender().sendMessage("You drink some of your " + potionName + " potion.");
					for(int i = 0; i < drink.getSkills().length; i++) {
						int skill = drink.getSkill(i);
						int modification = 5;
						getMob().getSkills().increaseLevelToMaximumModification(skill, modification);
					}
					break;
				case SARADOMIN_BREW:
					getMob().getActionSender().sendMessage("You drink some of the foul liquid.");
					for(int i = 0; i < drink.getSkills().length; i++) {
						int skill = drink.getSkill(i);
						if(skill == Skills.HITPOINTS) {
							int hitpointsModification = (int) (getMob().getSkills().getLevelForExperience(Skills.HITPOINTS) * 0.15);
							getMob().getSkills().increaseLevelToMaximumModification(skill, hitpointsModification);
						} else if(skill == Skills.DEFENCE) {
							int defenceModification = (int) (getMob().getSkills().getLevelForExperience(Skills.DEFENCE) * 0.25);
							getMob().getSkills().increaseLevelToMaximumModification(skill, defenceModification);
						} else {
							int modification = (int) (getMob().getSkills().getLevelForExperience(skill) * 0.10);
							getMob().getSkills().decreaseLevelOnce(skill, modification);
						}
					}
					break;
				case ZAMORAK_BREW:
					getMob().getActionSender().sendMessage("You drink some of the foul liquid.");
					for(int i = 0; i < drink.getSkills().length; i++) {
						int skill = drink.getSkill(i);
						if(skill == Skills.ATTACK) {
							int attackModification = (int) Math.floor(2 + (getMob().getSkills().getLevelForExperience(Skills.ATTACK)) * 0.20);
							getMob().getSkills().increaseLevelToMaximumModification(skill, attackModification);
						} else if(skill == Skills.STRENGTH) {
							int strengthModification = (int) Math.floor(2 + (getMob().getSkills().getLevelForExperience(Skills.STRENGTH) * 0.12));
							getMob().getSkills().increaseLevelToMaximumModification(skill, strengthModification);
						} else if(skill == Skills.PRAYER) {
							int prayerModification = (int) Math.floor(getMob().getSkills().getLevelForExperience(Skills.STRENGTH) * 0.10);
							getMob().getSkills().increaseLevelToMaximum(skill, prayerModification);
						} else if(skill == Skills.DEFENCE) {
							int defenceModification = (int) Math.floor(2 + (getMob().getSkills().getLevelForExperience(Skills.DEFENCE) * 0.10));
							getMob().getSkills().decreaseLevelToZero(skill, defenceModification);
						} else if(skill == Skills.HITPOINTS) {
							World.getWorld().submit(new Tickable(3) {
								@Override
								public void execute() {
									int hitpointsModification = (int) Math.floor(2 + (getMob().getSkills().getLevel(Skills.HITPOINTS) * 0.10));
									if(getMob().getSkills().getLevel(Skills.HITPOINTS) - hitpointsModification < 0) {
										hitpointsModification = getMob().getSkills().getLevel(Skills.HITPOINTS);
									}
									getMob().inflictDamage(new Hit(hitpointsModification), null);
									this.stop();
								}							
							});
						}
					}
					break;
				case ANTIPOISON:
				case SUPER_ANTIPOISON:
					getMob().getActionSender().sendMessage("You drink some of your " + item.getDefinition().getName().toLowerCase().substring(0, item.getDefinition().getName().length() - 3) + ".");
					if(getMob().getCombatState().canBePoisoned()) {
						getMob().getCombatState().setCanBePoisoned(false);
						World.getWorld().submit(new Tickable(drink.getPotionType() == PotionType.ANTIPOISON ? 150 : 1000) {
							public void execute() {
								getMob().getCombatState().setCanBePoisoned(true);
								this.stop();
							}
						});
					}
					if(getMob().getCombatState().getPoisonDamage() > 0) {
						getMob().getCombatState().setPoisonDamage(0, null);
					}
					break;
				case BEER:
					getMob().getActionSender().sendMessage("You drink the beer. You feel slightly reinvigorated...");
					getMob().getActionSender().sendMessage("...and slightly dizzy too.");
					for(int i = 0; i < drink.getSkills().length; i++) {
						int skill = drink.getSkill(i);
						if(skill == Skills.ATTACK) {
							int attackModification = (int) (getMob().getSkills().getLevelForExperience(Skills.STRENGTH) * 0.07);
							getMob().getSkills().decreaseLevelToZero(Skills.ATTACK, attackModification);							
						} else if(skill == Skills.STRENGTH) {
							int strengthModification = (int) (getMob().getSkills().getLevelForExperience(Skills.STRENGTH) * 0.04);
							getMob().getSkills().increaseLevelToMaximumModification(Skills.STRENGTH, strengthModification);							
						}
					}
					break;
				case WINE:
					getMob().getActionSender().sendMessage("You drink the wine. You feel slightly reinvigorated...");
					getMob().getActionSender().sendMessage("...and slightly dizzy too.");
					for(int i = 0; i < drink.getSkills().length; i++) {
						int skill = drink.getSkill(i);
						if(skill == Skills.ATTACK) {
							int attackModification = 2;
							getMob().getSkills().decreaseLevelToZero(Skills.ATTACK, attackModification);
						} else if(skill == Skills.HITPOINTS) {
							getMob().getSkills().increaseLevelToMaximum(Skills.HITPOINTS, 11);
						}
					}
					break;
				}
				int currentPotionDose = 0;
				for(int i = 0; i < drink.getIds().length; i++) {
					if(item.getId() == drink.getId(i)) {
						currentPotionDose = i + 1;
						break;
					}
				}
				if(drink.getPotionType() != PotionType.BEER && drink.getPotionType() != PotionType.WINE) {
					getMob().getActionSender().sendMessage(currentPotionDose > 1 ? ("You have " + (currentPotionDose - 1) + " dose" + (currentPotionDose > 2 ? "s" : "") + " of potion left.") : "You have finished your potion.");
				}
				int newPotion = 229;
				if(currentPotionDose > 1) {
					newPotion = drink.getId(currentPotionDose - 2);
				}
				getMob().getInventory().add(new Item(newPotion), slot);
			}
			getMob().getInventory().fireItemsChanged();
		} finally {
			getMob().getInventory().setFiringEvents(inventoryFiringEvents);
		}
	}

}
