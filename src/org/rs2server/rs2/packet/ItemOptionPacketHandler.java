package org.rs2server.rs2.packet;

import java.util.logging.Logger;

import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.action.impl.ConsumeItemAction;
import org.rs2server.rs2.boundary.BoundaryManager;
import org.rs2server.rs2.model.Cannon;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.GroundItem;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.ItemDefinition;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.content.DragonfireShield;
import org.rs2server.rs2.content.ExplosivePotion;
import org.rs2server.rs2.content.SpadeDig;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.Consumables.Drink;
import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.region.Tile;
import org.rs2server.rs2.model.skills.Firemaking;
import org.rs2server.rs2.model.skills.Herblore;
import org.rs2server.rs2.model.skills.Smithing;
import org.rs2server.rs2.model.skills.Fletching.ArrowTip;
import org.rs2server.rs2.model.skills.Fletching.Log;
import org.rs2server.rs2.model.skills.Herblore.Herb;
import org.rs2server.rs2.model.skills.Herblore.HerbloreType;
import org.rs2server.rs2.model.skills.Herblore.PrimaryIngredient;
import org.rs2server.rs2.model.skills.Herblore.SecondaryIngredient;
import org.rs2server.rs2.model.skills.Smithing.Bar;
import org.rs2server.rs2.net.Packet;


/**
 * Remove item options.
 * @author Graham Edgecombe
 *
 */
public class ItemOptionPacketHandler implements PacketHandler {

	/**
	 * The logger instance.
	 */
	private static final Logger logger = Logger.getLogger(ItemOptionPacketHandler.class.getName());
	
	/**
	 * Option drop/destroy opcode.
	 */
	private static final int OPTION_DROP_DESTROY = 71;
	
	/**
	 * Option pickup opcode.
	 */
	private static final int OPTION_PICKUP = 105;
	
	/**
	 * Option examine opcode.
	 */
	private static final int OPTION_EXAMINE = 47;
	
	/**
	 * Option 1 opcode.
	 */
	private static final int OPTION_1 = 241;
	
	/**
	 * Option 2 opcode.
	 */
	private static final int OPTION_2 = 107;
	private static final int OPTION_2_2 = 141;
	
	/**
	 * Option 3 opcode.
	 */
	private static final int OPTION_3 = 102;
	
	/**
	 * Option 4 opcode.
	 */
	private static final int OPTION_4 = 55;
	
	/**
	 * Option 5 opcode.
	 */
	private static final int OPTION_5 = 125;
	
	/**
	 * Click 1 opcode.
	 */
	private static final int CLICK_1 = 89;
	
	/**
	 * Item on item opcode.
	 */
	private static final int ITEM_ON_ITEM = 206;

	@Override
	public void handle(Player player, Packet packet) {
		if(player.getAttribute("cutScene") != null) {
			return;
		}
		switch(packet.getOpcode()) {
		case OPTION_DROP_DESTROY:
			handleItemOptionDrop(player, packet);
			break;
		case OPTION_PICKUP:
			handleItemOptionPickup(player, packet);
			break;
		case OPTION_EXAMINE:
			handleItemOptionExamine(player, packet);
			break;
		case OPTION_1:
			handleItemOption1(player, packet);
			break;
		case OPTION_2:
		case OPTION_2_2:
			handleItemOption2(player, packet);
			break;
		case OPTION_3:
			handleItemOption3(player, packet);
			break;
		case OPTION_4:
			handleItemOption4(player, packet);
			break;
		case OPTION_5:
			handleItemOption5(player, packet);
			break;
		case CLICK_1:
			handleItemOptionClick1(player, packet);
			break;
		case ITEM_ON_ITEM:
			handleItemOnItem(player, packet);
			break;
		}
	}

	/**
	 * Handles item option drop.
	 * @param player The player.
	 * @param packet The packet.
	 */
	private void handleItemOptionDrop(Player player, Packet packet) {
		int interfaceId = packet.getInt2();
		int id = packet.getLEShortA();
		int slot = packet.getLEShort();
		if(player.getCombatState().isDead()) {
			return;
		}
		player.getActionQueue().clearRemovableActions();

		player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemDrop", new Object[] { "ID: " + id });
		
		player.getActionSender().removeAllInterfaces();
		switch(interfaceId) {
		case Inventory.INTERFACE:
			if(slot >= 0 && slot < Inventory.SIZE) {
				Item item = player.getInventory().get(slot);
				if(item != null && item.getId() != id) {
					return;
				}
				/*if(item.getDefinition().getName().toLowerCase().contains("cat")) {
					Cats catManager = new Cats(player);
					catManager.dropCat(item, slot);
					return;
				}*/
				if(item.getDefinition().getId() == 4045) {
					ExplosivePotion.execute(player, slot);
					return;
				}
				player.getInventory().remove(item, slot);
				World.getWorld().createGroundItem(new GroundItem(player.getName(), item, player.getLocation()), player);
			}
			break;
		default:
			logger.info("Unhandled item drop option : " + interfaceId + " - " + id + " - " + slot);
			break;
		}
	}

	/**
	 * Handles item option pickup.
	 * @param player The player.
	 * @param packet The packet.
	 */
	private void handleItemOptionPickup(final Player player, Packet packet) {
		final int id = packet.getShortA();
		int x = packet.getLEShort();
		int y = packet.getLEShort();
		if(player.getCombatState().isDead()) {
			return;
		}
		player.getActionQueue().clearRemovableActions();
		
		final Location location = Location.create(x, y, player.getLocation().getZ());

		player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemPickup", new Object[] { "ID: " + id, "Loc: " + location });

		player.getActionSender().removeAllInterfaces();
		Action action = new Action(player, 0) {
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
				Tile tile = player.getRegion().getTile(location);
				for (GroundItem g : tile.getGroundItems()) {
					if (g.getItem().getId() == id && g.isOwnedBy(player.getName())) {
						if(player.getInventory().add(player.checkForSkillcape(g.getItem()))) {
							World.getWorld().unregister(g);
						} else {
							player.getActionSender().sendMessage("Not enough space in inventory.");
						}
						break;
					}
				}
				this.stop();
			}
		};
		player.addCoordinateAction(player.getWidth(), player.getHeight(), location, 0, 0, 0, action);
	}
	
	/**
	 * Handles item option 1.
	 * @param player The player.
	 * @param packet The packet.
	 */
	private void handleItemOption1(Player player, Packet packet) {
		int slot = packet.getShort() & 0xFFFF;
		int interfaceValue = packet.getLEInt();
		int interfaceId = interfaceValue >> 16;
		int childId = interfaceValue & 0xFFFF;
		int id = packet.getLEShortA() & 0xFFFF;
		if(player.getCombatState().isDead()) {
			return;
		}
		player.getActionQueue().clearRemovableActions();

		player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemOpt1", new Object[] { "ID: " + id, "Interface: " + interfaceId });
		
		Item item = null;
		
		switch (interfaceId) {
			case Equipment.INTERFACE:
			case Equipment.SCREEN:
    			if (slot >= 0 && slot < Equipment.SIZE) {
    				item = player.getEquipment().get(slot);
    				if(!player.canEmote()) {//stops people unequipping during a skillcape emote.
    					return;
    				}
    				if (!Container.transfer(player.getEquipment(), player.getInventory(), slot, id)) {
    					player.getActionSender().sendMessage("Not enough space in inventory.");
    				} else {
    					if (item != null && item.getEquipmentDefinition() != null) {
    						for (int i = 0; i < item.getEquipmentDefinition().getBonuses().length; i++) {
    							player.getCombatState().setBonus(i, player.getCombatState().getBonus(i) - item.getEquipmentDefinition().getBonus(i));
    						}
    						player.getActionSender().sendBonuses();
    						if (slot == Equipment.SLOT_WEAPON) {
    							player.setDefaultAnimations();
    						}
    					}
    				}
    			}
				break;
			case Bank.PLAYER_INVENTORY_INTERFACE:
				if (slot >= 0 && slot < Inventory.SIZE) {
					Bank.deposit(player, slot, id, 1);
				}
				break;
			case Bank.BANK_INVENTORY_INTERFACE:
				if (slot >= 0 && slot < Bank.SIZE) {
					Bank.withdraw(player, slot, id, 1);
				}
				break;
			case Smithing.INTERFACE:
				if(player.getInterfaceAttribute("smithing_bar") != null) {
					Bar bar = (Bar) player.getInterfaceAttribute("smithing_bar");
					int row = -1;
					for(int i = 0; i < bar.getItems(childId - 146).length; i++) {
						Item newItem = bar.getItems(childId - 146)[i];
						if(newItem == null) {
							continue;
						}
						if(newItem.getId() == id) {
							item = newItem;
							row = i;
						}
					}
					if(item == null || row == -1) {
						return;
					}
					player.getActionQueue().addAction(new Smithing(player, 1, item, childId - 146, row, bar));
				}
				break;
			default:
				logger.info("Unhandled item option 1 : " + id + " - " + slot + " - " + interfaceId + ".");
				break;
		}
	}
	
	/**
	 * Handles item option 2.
	 * @param player The player.
	 * @param packet The packet.
	 */
	private void handleItemOption2(Player player, Packet packet) {		
		int slot = packet.getShortA() & 0xFFFF;
		int interfaceValue = packet.getInt();
		int interfaceId = (interfaceValue >> 16) & 0xFFFF;
		int childId = interfaceValue & 0xFFFF;
		int id = packet.getShort() & 0xFFFF;
		
		if(player.getCombatState().isDead()) {
			return;
		}
		player.getActionQueue().clearRemovableActions();

		player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemOpt2", new Object[] { "ID: " + id, "Interface: " + interfaceId });
		
		Item item = null;
		
		switch (interfaceId) {
			case Equipment.INTERFACE:
				if (slot >= 0 && slot < Equipment.SIZE) {
					item = player.getEquipment().get(slot);
					if(item != null) {
						switch(item.getId()) {
						case 11283:
						case 11284:
							if(player.getInteractingEntity() == null) {
								return;
							}
							DragonfireShield.dfsSpec(player, player.getInteractingEntity());
							break;
						case 2550:
							player.getActionSender().sendMessage("Your Ring of Recoil can deal " + player.getCombatState().getRingOfRecoil() + " more points of damage before shattering.");
							break;
						default:
							player.getActionSender().sendMessage("There is no way to operate that item.");
							break;
						}
					}
				}
				break;
			case Bank.PLAYER_INVENTORY_INTERFACE:
				if (slot >= 0 && slot < Inventory.SIZE) {
					Bank.deposit(player, slot, id, 5);
				}
				break;
			case Bank.BANK_INVENTORY_INTERFACE:
				if (slot >= 0 && slot < Bank.SIZE) {
					Bank.withdraw(player, slot, id, 5);
				}
				break;
			case Smithing.INTERFACE:
				if(player.getInterfaceAttribute("smithing_bar") != null) {
					Bar bar = (Bar) player.getInterfaceAttribute("smithing_bar");
					int row = -1;
					for(int i = 0; i < bar.getItems(childId - 146).length; i++) {
						Item newItem = bar.getItems(childId - 146)[i];
						if(newItem == null) {
							continue;
						}
						if(newItem.getId() == id) {
							item = newItem;
							row = i;
						}
					}
					if(item == null || row == -1) {
						return;
					}
					player.getActionQueue().addAction(new Smithing(player, 5, item, childId - 146, row, bar));
				}
				break;
			default:
				logger.info("Unhandled item option 2 : " + id + " - " + slot + " - " + interfaceId + ".");
				break;
		}
	}
	
	/**
	 * Handles item option 3.
	 * @param player The player.
	 * @param packet The packet.
	 */
	private void handleItemOption3(Player player, Packet packet) {
		int id = packet.getLEShortA() & 0xFFFF;
		int interfaceValue = packet.getInt();
		int childId = (interfaceValue >> 16) & 0xFFFF;
		int interfaceId = interfaceValue & 0xFFFF;		
		int slot = packet.getShort() & 0xFFFF;
		
		if(player.getCombatState().isDead()) {
			return;
		}
		player.getActionQueue().clearRemovableActions();

		player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemOpt3", new Object[] { "ID: " + id, "Interface: " + interfaceId });

		Item item = null;
		
		switch (interfaceId) {
			case Bank.PLAYER_INVENTORY_INTERFACE:
				if (slot >= 0 && slot < Inventory.SIZE) {
					Bank.deposit(player, slot, id, 10);
				}
				break;
			case Bank.BANK_INVENTORY_INTERFACE:
				if (slot >= 0 && slot < Bank.SIZE) {
					Bank.withdraw(player, slot, id, 10);
				}
				break;
			case Smithing.INTERFACE:
				if(player.getInterfaceAttribute("smithing_bar") != null) {
					Bar bar = (Bar) player.getInterfaceAttribute("smithing_bar");
					int row = -1;
					for(int i = 0; i < bar.getItems(childId - 146).length; i++) {
						Item newItem = bar.getItems(childId - 146)[i];
						if(newItem == null) {
							continue;
						}
						if(newItem.getId() == id) {
							item = newItem;
							row = i;
						}
					}
					if(item == null || row == -1) {
						return;
					}
					player.getActionQueue().addAction(new Smithing(player, 10, item, childId - 146, row, bar));
				}
				break;
			default:
				logger.info("Unhandled item option 3 : " + id + " - " + slot + " - " + interfaceId + ".");
				break;
		}
	}
	
	/**
	 * Handles item option 4.
	 * @param player The player.
	 * @param packet The packet.
	 */
	private void handleItemOption4(Player player, Packet packet) {
		int slot = packet.getShort() & 0xFFFF;
		int interfaceId = packet.getLEShort() & 0xFFFF;
		packet.getLEShort();
	   	int id = packet.getLEShort() & 0xFFFF;
		if(player.getCombatState().isDead()) {
			return;
		}
		player.getActionQueue().clearRemovableActions();

		player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemOpt4", new Object[] { "ID: " + id, "Interface: " + interfaceId });
		
		switch (interfaceId) {
			case Bank.PLAYER_INVENTORY_INTERFACE:
				if (slot >= 0 && slot < Inventory.SIZE) {
					Bank.deposit(player, slot, id, player.getInventory().getCount(id));
				}
				break;
			case Bank.BANK_INVENTORY_INTERFACE:
				if (slot >= 0 && slot < Bank.SIZE) {
					Bank.withdraw(player, slot, id, player.getBank().getCount(id));
				}
				break;
			default:
				logger.info("Unhandled item option 4 : " + id + " - " + slot + " - " + interfaceId + ".");
				break;
		}
	}
	
	/**
	 * Handles item option 5.
	 * @param player The player.
	 * @param packet The packet.
	 */
	private void handleItemOption5(Player player, Packet packet) {
		int interfaceId = packet.getLEInt() >> 16;
		int slot = packet.getShortA() & 0xFFFF;
	   	int id = packet.getShortA() & 0xFFFF;
		if(player.getCombatState().isDead()) {
			return;
		}
		player.getActionQueue().clearRemovableActions();

		player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemOpt5", new Object[] { "ID: " + id, "Interface: " + interfaceId });
		
		switch (interfaceId) {
			case Bank.PLAYER_INVENTORY_INTERFACE:
				if(slot >= 0 && slot < Inventory.SIZE) {
					player.getInterfaceState().openEnterAmountInterface(interfaceId, slot, id);
				}
				break;
			case Bank.BANK_INVENTORY_INTERFACE:
				if(slot >= 0 && slot < Bank.SIZE) {
					player.getInterfaceState().openEnterAmountInterface(interfaceId, slot, id);
				}
				break;
			default:
				logger.info("Unhandled item option 5 : " + id + " - " + slot + " - " + interfaceId + ".");
				break;
		}
	}
	
	/**
	 * Handles item option examine.
	 * @param player The player.
	 * @param packet The packet.
	 */
	private void handleItemOptionExamine(Player player, Packet packet) {
		int id = packet.getLEShortA() & 0xFFFF;

		player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemExamine", new Object[] { "ID: " + id });
		
		Item item = new Item(id);
		if(item.getDefinition() != null) {
			player.getActionSender().sendMessage(item.getDefinition().getDescription());
		}
	}
	
	/**
	 * Handles item option 1.
	 * @param player The player.
	 * @param packet The packet.
	 */
	private void handleItemOptionClick1(final Player player, Packet packet) {
		int id = packet.getLEShortA();
		int interfaceId = packet.getInt() >> 16;
		int slot = packet.getShortA();
		if(player.getCombatState().isDead()) {
			return;
		}
		player.getActionQueue().clearRemovableActions();

		player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemClick1", new Object[] { "ID: " + id, "Interface: " + interfaceId });
		
		Item item = null;

		switch(interfaceId) {
		case Inventory.INTERFACE:
			if(slot >= 0 && slot < Inventory.SIZE) {
				item = player.getInventory().get(slot);
				if(item == null || id != item.getId()) {
					return;
				}
				Herb herb = Herb.forId(id);
				if(herb != null) {
					if(player.getSkills().getLevelForExperience(Skills.HERBLORE) < herb.getRequiredLevel()) {
						player.getActionSender().sendMessage("You cannot clean this herb. You need a Herblore level of " + herb.getRequiredLevel() + " to attempt this.");
						return;
					}
					player.getActionSender().sendMessage("You clean the dirt from the " + ItemDefinition.forId(herb.getReward()).getName().toLowerCase() + ".");
					player.getInventory().remove(new Item(herb.getId()), slot);
					player.getInventory().add(new Item(herb.getReward()), slot);
					return;
				}
				if(item.getDefinition().getName().trim().toLowerCase().equals("spade")) {
					SpadeDig.dig(player);
				}
				switch(item.getId()) {
				case 526:
				case 528:
				case 530:
				case 532:
				case 534:
				case 536:
				case 2530:
				case 2859:
				case 3125:
				case 3123:
				case 6812:
				case 6729:
				case 4834:
					player.getSkills().getPrayer().buryBone(new Item(item.getId()), slot);
					break;
				case 6:
					if(BoundaryManager.isWithinBoundary(player.getLocation(), "Zombie") || 
							World.getWorld().getBarrelchest().players.contains(player)) {
						player.getActionSender().sendMessage("You cannot set up a cannon here.");
						return;
					}
					for(GameObject obj : player.getRegion().getGameObjects()) {
						if(obj != null && obj.getType() == 10 && obj.getLocation().equals(player.getLocation())) {
							player.getActionSender().sendMessage("You cannot set up a cannon here.");
							return;
						}
					}
					if(player.getAttributes().containsKey("cannon")) {
						player.getActionSender().sendMessage("You cannot set up more than one cannon at a time!");
						return;
					}
					player.setAttribute("cannon", new Cannon(player, player.getLocation()));
					break;
				default:
					Action action = new ConsumeItemAction(player, item, slot);
					action.execute();
					break;
				}
				break;
			}
		}
	}
	
	/**
	 * Handles item on item option.
	 * @param player The player.
	 * @param packet The packet.
	 */
	private void handleItemOnItem(final Player player, Packet packet) {
		int slot = packet.getShort();
		int interfaceId = packet.getInt();
		int id = packet.getLEShortA();
		int usedWithSlot = packet.getLEShort();
		if(player.getCombatState().isDead()) {
			return;
		}
		player.getActionQueue().clearRemovableActions();

		player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemOnItem", new Object[] { "ID: " + id, "Interface: " + interfaceId });
		
		Item usedItem = null;
		Item withItem = null;

		switch(interfaceId) {
		case Inventory.INTERFACE:
			if(slot >= 0 && slot < Inventory.SIZE) {
				usedItem = player.getInventory().get(slot);
				withItem = player.getInventory().get(usedWithSlot);
				if(id != usedItem.getId()) {
					return;
				}
				if(player.getCombatState().isDead()) {
					return;
				}
				if(usedItem.getId() == 11286 || withItem.getId() == 11286) {
					Item dfs = null;
					if(usedItem.getId() == 11286) {
						dfs = withItem;
					} else {
						dfs = usedItem;
					}
					if(dfs != null) {
						player.getActionSender().sendMessage("You combine your Anti-Dragon shield with the visage...");
						player.getInventory().remove(new Item(11286));
						player.getInventory().remove(new Item(1540));
						player.getActionSender().sendMessage("It creates the legendary Dragonfire shield.");
						player.getInventory().add(new Item(11283));
						return;
					}
				}
				if(usedItem.getId() == 590 || withItem.getId() == 590) {
					Item logItem = null;
					if(usedItem.getId() == 590) {
						logItem = withItem;
					} else {
						logItem = usedItem;
					}
					if(logItem != null) {
						Firemaking firemaking = new Firemaking(player);
						firemaking.light(firemaking.findLog(logItem));
					}
					return;
				}
				if(usedItem.getId() == 946 || withItem.getId() == 946) {
					Item logItem = null;
					if(usedItem.getId() == 946) {
						logItem = withItem;
					} else {
						logItem = usedItem;
					}
					Log log = Log.forId(logItem.getId());
					if(log != null) {
						interfaceId = 309;
						if(log.getItem().length > 1) {
							interfaceId = 301 + log.getItem().length;
						}
						for(int i = 0; i < log.getItem().length; i++) {
							player.getActionSender().sendInterfaceModel(interfaceId, 2 + i, 160, log.getItem()[i]);
							player.getActionSender().sendString(interfaceId, (interfaceId - 296) + (i * 4), "<br><br><br><br>" + ItemDefinition.forId(log.getItem()[i]).getName());
						}
						player.getActionSender().sendChatboxInterface(interfaceId);
						player.setInterfaceAttribute("fletching_log", log);
						return;
					}
				}
				if(usedItem.getId() == 53 || withItem.getId() == 53) {
					Item arrowTips = null;
					if(usedItem.getId() == 53) {
						arrowTips = withItem;
					} else {
						arrowTips = usedItem;
					}
					ArrowTip tips = ArrowTip.forId(arrowTips.getId());
					if(tips != null) {
						if(player.getSkills().getLevelForExperience(Skills.FLETCHING) < tips.getLevelRequired()) {
							player.getActionSender().sendMessage("You need a Fletching level of " + tips.getLevelRequired() + " to make these arrows.");
							return;
						}
						int lowestAmount = player.getInventory().getCount(53) < player.getInventory().getCount(arrowTips.getId()) ? player.getInventory().getCount(53) : player.getInventory().getCount(arrowTips.getId());
						if(player.getInventory().add(new Item(tips.getReward(), lowestAmount))) {
							player.getInventory().remove(new Item(tips.getId(), lowestAmount));
							player.getInventory().remove(new Item(53, lowestAmount));
							player.getSkills().addExperience(Skills.FLETCHING, tips.getExperience() * lowestAmount);
							player.getActionSender().sendMessage("You attach the arrow tips to the headless arrows.");
						}
						return;
					}
				}
				if(usedItem.getId() == 227 || withItem.getId() == 227) {
					Item primaryIngredient = null;
					if(usedItem.getId() == 227) {
						primaryIngredient = withItem;
					} else {
						primaryIngredient = usedItem;
					}
					PrimaryIngredient ingredient = PrimaryIngredient.forId(primaryIngredient.getId());
					if(ingredient != null) {
						if(player.getInventory().getCount(227) > 1 && player.getInventory().getCount(primaryIngredient.getId()) > 1) {
							player.getActionSender().sendInterfaceModel(309, 2, 130, ingredient.getReward());
							String itemName = "";
							String leafName = ItemDefinition.forId(ingredient.getId()).getName().replaceAll(" leaf", "").replaceAll(" clean", "");
							itemName = leafName + " potion (unf)";
							player.getActionSender().sendString(309, 6, "<br><br><br><br>" + itemName);
							player.getActionSender().sendChatboxInterface(309);
							player.setInterfaceAttribute("herblore_type", HerbloreType.PRIMARY_INGREDIENT);
							player.setInterfaceAttribute("herblore_index", ingredient.getId());
						} else {
							player.getActionQueue().addAction(new Herblore(player, 1, ingredient, null, HerbloreType.PRIMARY_INGREDIENT));
						}
						return;
					}
				}
				SecondaryIngredient ingredient = null;
				for(SecondaryIngredient sIngredient : SecondaryIngredient.values()) {
					if(sIngredient.getId() == withItem.getId() && sIngredient.getRequiredItem().getId() == usedItem.getId() || sIngredient.getId() == usedItem.getId() && sIngredient.getRequiredItem().getId() == withItem.getId()) {
						ingredient = sIngredient;
					}
				}
				if(ingredient != null) {
					if(player.getInventory().getCount(ingredient.getId()) > 1 && player.getInventory().getCount(ingredient.getRequiredItem().getId()) > 1) {
						player.getActionSender().sendInterfaceModel(309, 2, 130, ingredient.getReward());
						player.getActionSender().sendString(309, 6, "<br><br><br><br>" + ItemDefinition.forId(ingredient.getReward()).getName());
						player.getActionSender().sendChatboxInterface(309);
						player.setInterfaceAttribute("herblore_type", HerbloreType.SECONDARY_INGREDIENT);
						player.setInterfaceAttribute("herblore_index", ingredient.getIndex());						
					} else {
						player.getActionQueue().addAction(new Herblore(player, 1, null, ingredient, HerbloreType.SECONDARY_INGREDIENT));					
					}
					return;
				}
				
				
				Drink drink1 = Drink.forId(usedItem.getId());
				Drink drink2 = Drink.forId(withItem.getId());
				if(drink1 != null && drink2 != null) {
					if(drink1 != drink2) {
						player.getActionSender().sendMessage("You can't combine these two potions.");
						return;
					}
					int index1 = -1;
					int index2 = -1;
					for(int i = 0; i < drink1.getIds().length; i++) {
						if(drink1.getId(i) == usedItem.getId()) {
							index1 = i + 1;
							break;
						}
					}
					for(int i = 0; i < drink2.getIds().length; i++) {
						if(drink2.getId(i) == withItem.getId()) {
							index2 = i + 1;
							break;
						}
					}
					int doses = index1 + index2;
					int amount = 0;
					Item endPotion1 = null;
					Item endPotion2 = null;
					if(doses < 5) {
						endPotion1 = new Item(drink1.getId(doses - 1), 1);
						endPotion2 = new Item(229, 1);		
						amount = doses;
					} else {
						endPotion1 = new Item(drink1.getId(3), 1);
						amount = 4;
						doses -= 4;
						endPotion2 = new Item(drink1.getId(doses - 1), 1);									
					}
					player.getInventory().remove(usedItem);
					player.getInventory().remove(withItem);
					player.getInventory().add(endPotion1, usedWithSlot);
					player.getInventory().add(endPotion2, slot);
					player.getActionSender().sendMessage("You have combined the liquid into " + amount + " doses.");
					return;
				}
				player.getActionSender().sendMessage("Nothing interesting happens.");
				break;
			}
		}
	}

}
