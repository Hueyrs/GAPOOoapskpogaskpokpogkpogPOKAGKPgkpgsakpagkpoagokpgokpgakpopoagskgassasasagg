package org.rs2server.rs2.packet;

import java.io.File;
import java.util.logging.Logger;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.action.impl.WieldItemAction;
import org.rs2server.rs2.content.Canoes;
import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.ItemDefinition;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.NPCDefinition;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Shop;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.Animation.Emote;
import org.rs2server.rs2.model.PrivateChat.EntryRank;
import org.rs2server.rs2.model.PrivateChat.KickRank;
import org.rs2server.rs2.model.PrivateChat.TalkRank;
import org.rs2server.rs2.model.Shop.ShopType;
import org.rs2server.rs2.model.Skills.SkillCape;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.CombatState.CombatStyle;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.Spell;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.SpellBook;
import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.container.Trade;
import org.rs2server.rs2.model.container.impl.InterfaceContainerListener;
import org.rs2server.rs2.model.minigame.impl.CastleWars;
import org.rs2server.rs2.model.minigame.impl.FightPits;
import org.rs2server.rs2.model.skills.Agility;
import org.rs2server.rs2.model.skills.Agility.Obstacle;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.NameUtils;


/**
 * Handles clicking on most buttons in the interface.
 * @author Graham Edgecombe
 *
 */
public class ActionButtonPacketHandler implements PacketHandler {

	/**
	 * The logger instance.
	 */
	private static final Logger logger = Logger.getLogger(ActionButtonPacketHandler.class.getName());
	
	@Override
	public void handle(final Player player, Packet packet) {
		final int interfaceId = packet.getShort() & 0xFFFF;
		final int button = packet.getShort() & 0xFFFF;
		final int childButton;
		if (packet.getLength() >= 6) {
			int child = packet.getShort() & 0xFFFF;
			childButton = (child == 65535 ? 0 : child);
		} else {
			childButton = 0;
		}

		player.getActionSender().sendDebugPacket(packet.getOpcode(), "ActionButton", new Object[] { "Interface: " + interfaceId, "Button: " + button, "ChildButton: " + childButton });
		
		switch(interfaceId) {
		case 382:
			switch(button) {
			case 18:
				player.sawWildJump = true;
				player.getActionSender().removeAllInterfaces();
				Agility.tackleObstacle(player, Obstacle.WILDERNESS_DITCH, player.wildDitch);
				break;
			}
			break;
		/*
		 * Canoe locations
		 */
		case 53:
			switch(button) {
			case 39:
				Canoes.sail(player, (NPC) player.getInteractingEntity(), Location.create(3132, 3510, 0), "Edgeville");
				break;
			case 34:
				Canoes.sail(player, (NPC) player.getInteractingEntity(), Location.create(3112, 3411, 0), "Barbarian Village");
				break;
			case 33:
				Canoes.sail(player, (NPC) player.getInteractingEntity(), Location.create(3202, 3343, 0), "Champion's Guild");
				break;
			}
			break;
		/*
		 * Login screen
		 */
		case 378:
			switch(button) {
			case 6:
				player.getActionSender().sendWindowPane(Constants.MAIN_WINDOW);
				File f = new File("data/savedGames/" + NameUtils.formatNameForProtocol(player.getName()) + ".dat.gz");
				if(!f.exists()) {
					NPC npc = new NPC(NPCDefinition.forId(606), Location.create(2965, 3367, player.getIndex() * 4), null, null, 6);
					World.getWorld().register(npc);
					player.setAttribute("squire", npc);
					player.getWalkingQueue().reset();
					player.getWalkingQueue().addStep(2965, 3365);
					player.getWalkingQueue().finish();
					World.getWorld().submit(new Tickable(20) {
						@Override
						public void execute() {
							DialogueManager.openDialogue(player, 121);
							this.stop();
						}						
					});
				}
				return;
			}
			return;
		}
		if(player.getAttribute("cutScene") != null) {
			return;
		}
		if(SpellBook.forId(player.getCombatState().getSpellBook()).getInterfaceId() == interfaceId) {
			Spell spell = Spell.forId(button, SpellBook.forId(player.getCombatState().getSpellBook()));
			if(spell != null) {
				MagicCombatAction.executeSpell(spell, player, player);
				return;
			}
		}
		Item item = null;
		Player partner = player.getRequestManager().getAcquaintance();
		Shop shop = player.getInterfaceState().getOpenShop() != -1 ? Shop.forId(player.getInterfaceState().getOpenShop()) : null;
		Container[] itemsKeptOnDeath = null;
		boolean defensive = (player.getInterfaceState().getOpenAutocastType() == 1);
		int config = 45 + (button * 2);
		switch(interfaceId) {
		/*
		 * Game frame buttons
		 */
		case 548:
			switch(button) {
			case 47:
				//Combat options
				break;
			case 48:
				//Stats
				break;
			case 49:
				//Quest List
				break;
			case 50:
				//Inventory
				break;
			case 51:
				//Worn Equipment
				break;
			case 52:
				//Prayer
				break;
			case 53:
				//Magic
				break;
			case 30:
				//Clan Chat
				break;
			case 31:
				//Friends List
				break;
			case 32:
				//Ignore List
				break;
			case 33:
				//Logout
				break;
			case 34:
				//Options
				break;
			case 35:
				//Emotes
				break;
			case 36:
				//Music Player
				break;
			case 3:
				//All
				break;
			case 6:
				//Game
				break;
			case 9:
				//Public
				break;
			case 13:
				//Private
				break;
			case 17:
				//Clan
				break;
			case 21:
				//Trade
				break;
			case 24:
				player.getActionSender().sendCommandsMenu();
				//Report Abuse
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Interface button click
		 */
		case 182:
			switch(button) {
			case 6:
				player.getActionSender().sendLogout();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Emotes tab
		 */
		case 464:
			if(!player.canAnimate() || !player.canEmote()) {
				player.getActionSender().sendMessage("You can't perform that emote right now.");
				return;
			}
			Tickable emotes = new Tickable(2) { //why a tickable? because if you run, then perform a skillcape emote, the timing is off ;)
				@Override
				public void execute() {
					Emote emote = Emote.forId(button);
					if(emote != null) {
						if(emote.getAnimation() != null) {
							player.playAnimation(emote.getAnimation());
						}
						if(emote.getGraphic() != null) {
							player.playGraphics(emote.getGraphic());
						}
						player.setEmote(true);
					} else {
						switch(button) {
						case 39:
							if(player.getEquipment().get(Equipment.SLOT_CAPE) == null) {
								player.setEmote(true);
								player.getActionSender().sendMessage("You aren't wearing a cape of achievement.");
								return;
							}
							SkillCape skillCape = SkillCape.forId(player.getEquipment().get(Equipment.SLOT_CAPE));
							if(skillCape == null) {
								player.setEmote(true);
								player.getActionSender().sendMessage("You aren't wearing a cape of achievement.");
							} else {
								if(skillCape.getAnimation() != null) {
									player.playAnimation(skillCape.getAnimation());
								}
								if(skillCape.getGraphic() != null) {
									player.playGraphics(skillCape.getGraphic());
								}
								player.canEmoteWait = skillCape.getAnimateTimer();
								World.getWorld().submit(new Tickable(player.canEmoteWait) {
									@Override
									public void execute() {
										player.setEmote(true);
										this.stop();
									}							
								});
							}
							break;
						default:
							player.setEmote(true);
							logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
							break;
						}		
					}
					this.stop();
				}				
			};
			player.setEmote(false);
			if(player.getSprites().getPrimarySprite() != -1 || player.getSprites().getSecondarySprite() != -1) {
				player.getWalkingQueue().reset();
				World.getWorld().submit(emotes);
			} else {
				emotes.execute();
			}
			break;
		/*
		 * Settings tab
		 */
		case 261:
			switch(button) {
			case 0:
				player.getWalkingQueue().setRunningToggled(!player.getWalkingQueue().isRunningToggled());
				player.getActionSender().updateRunningConfig();
				break;
			case 1:
				player.getSettings().setChatEffects(!player.getSettings().chatEffects());
				player.getActionSender().updateChatEffectsConfig();
				break;
			case 2:
				player.getSettings().setSplitPrivateChat(!player.getSettings().splitPrivateChat());
				player.getActionSender().updateSplitPrivateChatConfig();
				break;
			case 3:
				player.getSettings().setTwoMouseButtons(!player.getSettings().twoMouseButtons());
				player.getActionSender().updateMouseButtonsConfig();
				break;
			case 4:
				player.getSettings().setAcceptAid(!player.getSettings().isAcceptingAid());
				player.getActionSender().updateAcceptAidConfig();
				break;
			case 7:
			case 8:
			case 9:
			case 10:
				player.getSettings().setBrightnessSetting(button - 6);
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Items on death interface
		 */
		case 102:
			itemsKeptOnDeath = player.getItemsKeptOnDeath();
			switch(button) {
			case 18: //Kept items
				if(childButton >= 0 && childButton < itemsKeptOnDeath[0].size()) {
					item = itemsKeptOnDeath[0].get(childButton);
    				if(item != null && item.getDefinition() != null) {
    					player.getActionSender().sendMessage(item.getDefinition().getDescription());
    				}
				}
				break;
			case 21: //Lost items
				if(childButton >= 0 && childButton < itemsKeptOnDeath[1].size()) {
					item = itemsKeptOnDeath[1].get(childButton);
    				if(item != null && item.getDefinition() != null) {
    					player.getActionSender().sendMessage(item.getDefinition().getDescription());
    				}
				}
				break;
			}
			break;
		/*
		 * Equipment interface
		 */
		case Equipment.INTERFACE:
			if(!player.canEmote()) {
				return;
			}
			switch(button) {
			case 50:
				itemsKeptOnDeath = player.getItemsKeptOnDeath();
				int count = 3;
				if(player.getCombatState().getPrayer(Prayers.PROTECT_ITEM)) { //protect item
					count++;
				}
				if(player.getCombatState().getSkullTicks() > 0) {
					count -= 3;
				}
				

				Object[] keptItems = new Object[] {-1, -1, "null", 0, 0, (itemsKeptOnDeath[0].size() >= 4 && itemsKeptOnDeath[0].get(3) != null) ? itemsKeptOnDeath[0].get(3).getId() : -1,
																		(itemsKeptOnDeath[0].size() >= 3 && itemsKeptOnDeath[0].get(2) != null) ? itemsKeptOnDeath[0].get(2).getId() : -1,
																		(itemsKeptOnDeath[0].size() >= 2 && itemsKeptOnDeath[0].get(1) != null) ? itemsKeptOnDeath[0].get(1).getId() : -1,
																		(itemsKeptOnDeath[0].size() >= 1 && itemsKeptOnDeath[0].get(0) != null) ? itemsKeptOnDeath[0].get(0).getId() : -1,
																						count, 0};
				player.getActionSender().sendAccessMask(210, 18, 102, 0, 28)	
										.sendAccessMask(210, 21, 102, 0, 28)	
										.sendAccessMask(211, 3, 102, 0, 4)	
										.sendRunScript(118, keptItems, "iiooooiisii").sendInterface(102, true);
				break;
			case 51:
				player.getActionSender().sendBonuses();
				player.getWalkingQueue().reset();
				player.getActionSender().sendInterface(Equipment.SCREEN, true);
				player.getActionSender().sendRunScript(150, Constants.EQUIPMENT_PARAMETERS, Constants.EQUIPMENT_TYPE_STRING).sendAccessMask(1278, 0, 336, 0, 28);
				player.getActionSender().sendInterfaceInventory(336);
				player.getInterfaceState().addListener(player.getInventory(), new InterfaceContainerListener(player, -1, 1, 98));
				player.getInterfaceState().addListener(player.getEquipment(), new InterfaceContainerListener(player, Equipment.SCREEN, 103, 95));
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Unarmed weapon interface
		 */
		case 92:
			switch(button) {
			case 2: //Punch
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				break;
			case 3: //Kick
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				break;
			case 4: //Block
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				break;
			case 24: //Auto retaliate
				player.getSettings().setAutoRetaliate(!player.getSettings().isAutoRetaliating());
				player.getActionSender().updateAutoRetaliateConfig();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Whip weapon interface
		 */
		case 93:
			switch(button) {
			case 2: //Flick
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				break;
			case 3: //Lash
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_2);
				break;
			case 4: //Deflect
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				break;
			case 8: //Special bar
				player.getCombatState().inverseSpecial();
				player.getActionSender().updateSpecialConfig();
				if(player.getActiveCombatAction() != null && player.getActiveCombatAction().canSpecial(player, player.getInteractingEntity())) {
					player.getActiveCombatAction().special(player, player.getEquipment().get(Equipment.SLOT_WEAPON));
				}
				break;
			case 24: //Auto retaliate
				player.getSettings().setAutoRetaliate(!player.getSettings().isAutoRetaliating());
				player.getActionSender().updateAutoRetaliateConfig();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Scythe weapon interface
		 */
		case 86:
			switch(button) {
			case 2: //Reap
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				break;
			case 3: //Chop
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				break;
			case 4: //Jab
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_2);
				break;
			case 5: //Block
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				break;
			case 11: //Auto retaliate
				player.getSettings().setAutoRetaliate(!player.getSettings().isAutoRetaliating());
				player.getActionSender().updateAutoRetaliateConfig();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Bow weapon interface
		 */
		case 77:
		/*
		 * Crossbow weapon interface
		 */
		case 79:
			switch(button) {
			case 2: //Accurate
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				break;
			case 3: //Longrange
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				break;
			case 4: //Rapid
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				break;
			case 8: //Special bar
				player.getCombatState().inverseSpecial();
				player.getActionSender().updateSpecialConfig();
				if(player.getActiveCombatAction() != null && player.getActiveCombatAction().canSpecial(player, player.getInteractingEntity())) {
					player.getActiveCombatAction().special(player, player.getEquipment().get(Equipment.SLOT_WEAPON));
				}
				break;
			case 24: //Auto retaliate
				player.getSettings().setAutoRetaliate(!player.getSettings().isAutoRetaliating());
				player.getActionSender().updateAutoRetaliateConfig();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Sword weapon interface
		 */
		case 81:
			switch(button) {
			case 2: //Chop
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				break;
			case 3: //Slash
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				break;
			case 4: //Lunge
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_3);
				break;
			case 5: //Block
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				break;
			case 10: //Special bar
				player.getCombatState().inverseSpecial();
				player.getActionSender().updateSpecialConfig();
				if(player.getActiveCombatAction() != null && player.getActiveCombatAction().canSpecial(player, player.getInteractingEntity())) {
					player.getActiveCombatAction().special(player, player.getEquipment().get(Equipment.SLOT_WEAPON));
				}
				break;
			case 26: //Auto retaliate
				player.getSettings().setAutoRetaliate(!player.getSettings().isAutoRetaliating());
				player.getActionSender().updateAutoRetaliateConfig();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Staff weapon interface
		 */
		case 90:
			switch(button) {
			case 1: //Bash
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				MagicCombatAction.setAutocast(player, null, -1, false);
				break;
			case 2: //Pound
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				MagicCombatAction.setAutocast(player, null, -1, false);
				break;
			case 3: //Rapid
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				MagicCombatAction.setAutocast(player, null, -1, false);
				break;
			case 4:
				player.getCombatState().setQueuedSpell(null);
				if(player.getAutocastSpell() != null) {
					MagicCombatAction.setAutocast(player, null, -1, false);
					return;
				}
				if(player.getEquipment().get(Equipment.SLOT_WEAPON) == null) {
					player.getActionSender().sendMessage("You must be wielding a staff in order to autocast.");
					return;
				}
				switch(SpellBook.forId(player.getCombatState().getSpellBook())) {
				case MODERN_MAGICS:
					if(player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 4675) {
						player.getActionSender().sendMessage("You can only autocast Ancient Magicks with that.");
						return;
					}
					if(player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 4170) {
						player.getActionSender().sendSidebarInterface(99, 310);						
					} else {
						player.getActionSender().sendSidebarInterface(99, 319);
					}
					break;
				case ANCIENT_MAGICKS:
					if(player.getEquipment().get(Equipment.SLOT_WEAPON).getId() != 4675) {
						player.getActionSender().sendMessage("You cannot autocast Ancient Magicks with that.");
						return;
					}
					player.getActionSender().sendSidebarInterface(99, 388);
					break;
				case LUNAR_MAGICS:
					player.getActionSender().sendMessage("You cannot autocast Lunar Magics with that.");
					break;
				}
				player.getInterfaceState().setOpenAutocastType(1);
				break;
			case 5:
				player.getCombatState().setQueuedSpell(null);
				if(player.getAutocastSpell() != null) {
					MagicCombatAction.setAutocast(player, null, -1, false);
					return;
				}
				if(player.getEquipment().get(Equipment.SLOT_WEAPON) == null) {
					player.getActionSender().sendMessage("You must be wielding a staff in order to autocast.");
					return;
				}
				switch(SpellBook.forId(player.getCombatState().getSpellBook())) {
				case MODERN_MAGICS:
					if(player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 4675) {
						player.getActionSender().sendMessage("You can only autocast Ancient Magicks with that.");
						return;
					}
					if(player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 4170) {
						player.getActionSender().sendSidebarInterface(99, 310);						
					} else {
						player.getActionSender().sendSidebarInterface(99, 319);
					}
					break;
				case ANCIENT_MAGICKS:
					if(player.getEquipment().get(Equipment.SLOT_WEAPON).getId() != 4675) {
						player.getActionSender().sendMessage("You cannot autocast Ancient Magicks with that.");
						return;
					}
					player.getActionSender().sendSidebarInterface(99, 388);
					break;
				case LUNAR_MAGICS:
					player.getActionSender().sendMessage("You cannot autocast Lunar Magics with that.");
					break;
				}
				player.getInterfaceState().setOpenAutocastType(0);
				break;
			case 9: //Auto retaliate
				player.getSettings().setAutoRetaliate(!player.getSettings().isAutoRetaliating());
				player.getActionSender().updateAutoRetaliateConfig();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Thrown weapon interface
		 */
		case 91:
			switch(button) {
			case 2: //Accurate
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				break;
			case 3: //Rapid
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				break;
			case 4: //Longrange
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				break;
			case 8: //Special bar
				player.getCombatState().inverseSpecial();
				player.getActionSender().updateSpecialConfig();
				if(player.getActiveCombatAction() != null && player.getActiveCombatAction().canSpecial(player, player.getInteractingEntity())) {
					player.getActiveCombatAction().special(player, player.getEquipment().get(Equipment.SLOT_WEAPON));
				}
				break;
			case 24: //Auto retaliate
				player.getSettings().setAutoRetaliate(!player.getSettings().isAutoRetaliating());
				player.getActionSender().updateAutoRetaliateConfig();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Mace weapon interface
		 */
		case 88:
			switch(button) {
			case 2: //Pound
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				break;
			case 3: //Pummel
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				break;
			case 4: //Spike
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_3);
				break;
			case 5: //Block
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				break;
			case 10: //Special bar
				player.getCombatState().inverseSpecial();
				player.getActionSender().updateSpecialConfig();
				if(player.getActiveCombatAction() != null && player.getActiveCombatAction().canSpecial(player, player.getInteractingEntity())) {
					player.getActiveCombatAction().special(player, player.getEquipment().get(Equipment.SLOT_WEAPON));
				}
				break;
			case 26: //Auto retaliate
				player.getSettings().setAutoRetaliate(!player.getSettings().isAutoRetaliating());
				player.getActionSender().updateAutoRetaliateConfig();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Claw weapon interface
		 */
		case 78:
			switch(button) {
			case 2: //Chop
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				break;
			case 3: //Block
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				break;
			case 4: //Lunge
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_2);
				break;
			case 5: //Slash
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState()	.setCombatStyle(CombatStyle.AGGRESSIVE_1);
				break;
			case 10: //Special bar
				player.getCombatState().inverseSpecial();
				player.getActionSender().updateSpecialConfig();
				if(player.getActiveCombatAction() != null && player.getActiveCombatAction().canSpecial(player, player.getInteractingEntity())) {
					player.getActiveCombatAction().special(player, player.getEquipment().get(Equipment.SLOT_WEAPON));
				}
				break;
			case 26: //Auto retaliate
				player.getSettings().setAutoRetaliate(!player.getSettings().isAutoRetaliating());
				player.getActionSender().updateAutoRetaliateConfig();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Dagger weapon interface
		 */
		case 89:
			switch(button) {
			case 2: //Stab
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				break;
			case 3: //Lunge
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				break;
			case 4: //Slash
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_2);
				break;
			case 5: //Block
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				break;
			case 10: //Special bar
				player.getCombatState().inverseSpecial();
				player.getActionSender().updateSpecialConfig();
				if(player.getActiveCombatAction() != null && player.getActiveCombatAction().canSpecial(player, player.getInteractingEntity())) {
					player.getActiveCombatAction().special(player, player.getEquipment().get(Equipment.SLOT_WEAPON));
				}
				break;
			case 26: //Auto retaliate
				player.getSettings().setAutoRetaliate(!player.getSettings().isAutoRetaliating());
				player.getActionSender().updateAutoRetaliateConfig();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Pickaxe weapon interface
		 */
		case 83:
			switch(button) {
			case 2: //Spike
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				break;
			case 3: //Impale
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				break;
			case 4: //Smash
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_2);
				break;
			case 5: //Block
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				break;
			case 26: //Auto retaliate
				player.getSettings().setAutoRetaliate(!player.getSettings().isAutoRetaliating());
				player.getActionSender().updateAutoRetaliateConfig();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Hammer weapon interface
		 */
		case 76:
			switch(button) {
			case 2: //Pound
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				break;
			case 3: //Block
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				break;
			case 4: //Pummel
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				break;
			case 8: //Special bar
				player.getCombatState().inverseSpecial();
				player.getActionSender().updateSpecialConfig();
				if(player.getActiveCombatAction() != null && player.getActiveCombatAction().canSpecial(player, player.getInteractingEntity())) {
					player.getActiveCombatAction().special(player, player.getEquipment().get(Equipment.SLOT_WEAPON));
				}
				break;
			case 24: //Auto retaliate
				player.getSettings().setAutoRetaliate(!player.getSettings().isAutoRetaliating());
				player.getActionSender().updateAutoRetaliateConfig();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Two-hand weapon interface
		 */
		case 82:
			switch(button) {
			case 2: //Chop
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				break;
			case 3: //Slash
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				break;
			case 4: //Smash
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_2);
				break;
			case 5: //Block
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				break;
			case 10: //Special bar
				player.getCombatState().inverseSpecial();
				player.getActionSender().updateSpecialConfig();
				if(player.getActiveCombatAction() != null && player.getActiveCombatAction().canSpecial(player, player.getInteractingEntity())) {
					player.getActiveCombatAction().special(player, player.getEquipment().get(Equipment.SLOT_WEAPON));
				}
				break;
			case 26: //Auto retaliate
				player.getSettings().setAutoRetaliate(!player.getSettings().isAutoRetaliating());
				player.getActionSender().updateAutoRetaliateConfig();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Axe weapon interface
		 */
		case 75:
			switch(button) {
			case 2: //Chop
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				break;
			case 3: //Block
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				break;
			case 4: //Smash
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_2);
				break;
			case 5: //Hack
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				break;
			case 10: //Special bar
				player.getCombatState().inverseSpecial();
				player.getActionSender().updateSpecialConfig();
				if(player.getActiveCombatAction() != null && player.getActiveCombatAction().canSpecial(player, player.getInteractingEntity())) {
					player.getActiveCombatAction().special(player, player.getEquipment().get(Equipment.SLOT_WEAPON));
				}
				break;
			case 26: //Auto retaliate
				player.getSettings().setAutoRetaliate(!player.getSettings().isAutoRetaliating());
				player.getActionSender().updateAutoRetaliateConfig();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Halberd weapon interface
		 */
		case 84:
			switch(button) {
			case 2: //Jab
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_1);
				break;
			case 3: //Swipe
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				break;
			case 4: //Fend
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				break;
			case 8: //Special bar
				player.getCombatState().inverseSpecial();
				player.getActionSender().updateSpecialConfig();
				if(player.getActiveCombatAction() != null && player.getActiveCombatAction().canSpecial(player, player.getInteractingEntity())) {
					player.getActiveCombatAction().special(player, player.getEquipment().get(Equipment.SLOT_WEAPON));
				}
				break;
			case 24: //Auto retaliate
				player.getSettings().setAutoRetaliate(!player.getSettings().isAutoRetaliating());
				player.getActionSender().updateAutoRetaliateConfig();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Spear weapon interface
		 */
		case 87:
			switch(button) {
			case 2: //Lunge
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_1);
				break;
			case 3: //Swipe
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_2);
				break;
			case 4: //Pound
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_3);
				break;
			case 5: //Block
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				break;
			case 24: //Auto retaliate
				player.getSettings().setAutoRetaliate(!player.getSettings().isAutoRetaliating());
				player.getActionSender().updateAutoRetaliateConfig();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Bow-sword weapon interface
		 */
		case 80:
			switch(button) {
			case 2: //Aim and Fire
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				break;
			case 3: //Kick
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				break;
			case 7: //Auto retaliate
				player.getSettings().setAutoRetaliate(!player.getSettings().isAutoRetaliating());
				player.getActionSender().updateAutoRetaliateConfig();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Skills tab
		 */
		case 320:
			int skillMenu = -1;
			switch (button) {
			case 123:
				/*
				 * Attack
				 */
				skillMenu = 0;
				break;
			case 126:
				/*
				 * Strength.
				 */
				skillMenu = 1;
				break;
			case 129:
				/*
				 * Defence.
				 */
				skillMenu = 2;
				break;
			case 132:
				/*
				 * Ranged
				 */
				skillMenu = 3;
				break;
			case 135:
				/*
				 * Prayer
				 */
				skillMenu = 4;
				break;
			case 142:
				/*
				 * Magic
				 */
				skillMenu = 5;
				break;
			case 145:
				/*
				 * Runecrafting
				 */
				skillMenu = 18;
				break;
			case 148:
				/*
				 * Construction
				 */
				skillMenu = 21;
				break;
			case 124:
				/*
				 * Hitpoints
				 */
				skillMenu = 6;
				break;
			case 127:
				/*
				 * Agility
				 */
				skillMenu = 7;
				break;
			case 130:
				/*
				 * Herblore
				 */
				skillMenu = 8;
				break;
			case 133:
				/*
				 * Thieving
				 */
				skillMenu = 9;
				break;
			case 136:
				/*
				 * Crafting
				 */
				skillMenu = 10;
				break;
			case 143:
				/*
				 * Fletching
				 */
				skillMenu = 11;
				break;
			case 146:
				/*
				 * Slayer
				 */
				skillMenu = 19;
				break;
			case 149:
				/*
				 * Hunter
				 */
				skillMenu = 22;
				break;
			case 125:
				/*
				 * Mining
				 */
				skillMenu = 12;
				break;
			case 128:
				/*
				 * Smithing
				 */
				skillMenu = 13;
				break;
			case 131:
				/*
				 * Fishing
				 */
				skillMenu = 14;
				break;
			case 134:
				/*
				 * Cooking
				 */
				skillMenu = 15;
				break;
			case 137:
				/*
				 * Firemaking
				 */
				skillMenu = 16;
				break;
			case 144:
				/*
				 * Woodcutting
				 */
				skillMenu = 17;
				break;
			case 147:
				/*
				 * Farming
				 */
				skillMenu = 20;
				break;
			}
			if(skillMenu != -1) {
				player.getActionSender().sendConfig(965, skillMenu);
				player.getActionSender().sendInterface(499, true);
				player.setInterfaceAttribute("skillMenu", skillMenu);
			}
			break;
		/*
		 * Skill information
		 */
		case 499:
			skillMenu = player.getInterfaceAttribute("skillMenu");
			if(skillMenu == -1) {
				return;
			}
			switch (button) {
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
				player.getActionSender().sendConfig(965, (1024 * (button - 10)) + skillMenu);
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Second trade screen
		 */
		case 334:
			switch(button) {
			case 20:
				Trade.acceptTrade(player, 2);
				break;
			case 21:
				player.getActionSender().removeAllInterfaces();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;			
			}
			break;
		/*
		 * Item on interface
		 */
		case 335:
			switch(packet.getOpcode()) {
			case 156: //Examine option
				switch(button) {
				case 48:
	    			if (childButton >= 0 && childButton < Trade.SIZE) {
	    				item = player.getTrade().get(childButton);
	    				if(item != null && item.getDefinition() != null) {
	    					player.getActionSender().sendMessage(item.getDefinition().getDescription());
	    				}	
	    			}
	    			break;
				case 50:
	    			if (childButton >= 0 && childButton < Trade.SIZE) {
	    				if(partner == null) {
	    					return;
	    				}
	    				item = partner.getTrade().get(childButton);
	    				if(item != null && item.getDefinition() != null) {
	    					player.getActionSender().sendMessage(item.getDefinition().getDescription());
	    				}	
	    			}
	    			break;
				default:
	    			if (childButton >= 0 && childButton < Inventory.SIZE) {
	    				item = player.getInventory().get(childButton);
	    				if(item != null && item.getDefinition() != null) {
	    					player.getActionSender().sendMessage(item.getDefinition().getDescription());
	    				}    				
	    			}
					break;
				}
				break;
			default:
				item = null;
				switch(player.getInterfaceState().getCurrentInterface()) {
				case Trade.TRADE_INVENTORY_INTERFACE:
					switch(button) {
					case 48: //Items on the interface
		    			if (childButton >= 0 && childButton < Trade.SIZE) {
		    				item = player.getTrade().get(childButton);
		    				if(item != null && item.getDefinition() != null) {
			    				switch(packet.getOpcode()) {
			    				case 234:
			    					Trade.removeItem(player, childButton, item.getId(), 1);
			    					break;
			    				case 87:
			    					Trade.removeItem(player, childButton, item.getId(), 5);
			    					break;
			    				case 37:
			    					Trade.removeItem(player, childButton, item.getId(), 10);
			    					break;
			    				case 3:
			    					Trade.removeItem(player, childButton, item.getId(), player.getTrade().getCount(item.getId()));
			    					break;
			    				case 70:
			    					player.getInterfaceState().openEnterAmountInterface(Trade.TRADE_INVENTORY_INTERFACE, childButton, item.getId());
			    					break;
			    				}
		    				}
		    			}
						break;
					case 17:
						Trade.acceptTrade(player, 1);
						break;
					case 18:
						player.getActionSender().removeAllInterfaces();
						break;
					default:
						logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
						break;		
					}
					break;
				default:
					logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
					break;				
    			}
				break;
			}
			break;
		/*
		 * Item on inventory interface
		 */
		case 336:
			switch(packet.getOpcode()) {
			case 156: //Examine option
    			if (childButton >= 0 && childButton < Inventory.SIZE) {
    				item = player.getInventory().get(childButton);
    				if(item != null && item.getDefinition() != null) {
    					player.getActionSender().sendMessage(item.getDefinition().getDescription());
    				}    				
    			}
				break;
			default:
				switch(player.getInterfaceState().getCurrentInterface()) {
				case Equipment.SCREEN:
	    			if (childButton >= 0 && childButton < Inventory.SIZE) {
	    				item = player.getInventory().get(childButton);
	    				if(item != null && item.getDefinition() != null) {
	    					for(int id : CastleWars.access().items) {
	    						if(item.getDefinition().getId() == id) {
	    							player.getActionSender().sendMessage("You need to represent your team!");
	    							return;
	    						}
	    					}
							WieldItemAction action = new WieldItemAction(player, item.getId(), childButton, 0);
							action.execute();
	    				}
	    			}
					break;
				case Trade.TRADE_INVENTORY_INTERFACE:
					switch(button) {
					case 0: //Items on the interface
						if (childButton >= 0 && childButton < Inventory.SIZE) {
		    				item = player.getInventory().get(childButton);
		    				if(item != null && item.getDefinition() != null) {
			    				switch(packet.getOpcode()) {
			    				case 234:
			    					Trade.offerItem(player, childButton, item.getId(), 1);
			    					break;
			    				case 87:
			    					Trade.offerItem(player, childButton, item.getId(), 5);
			    					break;
			    				case 37:
			    					Trade.offerItem(player, childButton, item.getId(), 10);
			    					break;
			    				case 3:
			    					Trade.offerItem(player, childButton, item.getId(), player.getInventory().getCount(item.getId()));
			    					break;
			    				case 70:
			    					player.getInterfaceState().openEnterAmountInterface(Trade.PLAYER_INVENTORY_INTERFACE, childButton, item.getId());
			    					break;
			    				}
		    				}
		    			}
						break;
					default:
						logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
						break;		
					}	    								
					break;
				default:
					logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
					break;				
    			}
				break;
			}
			break;
		/*
		 * Bank interface
		 */
		case Bank.BANK_INVENTORY_INTERFACE:
			switch(button) {
			case 92:
				player.getSettings().setWithdrawAsNotes(true);
				break;
			case 93:
				player.getSettings().setWithdrawAsNotes(false);
				break;
			case 98:
				player.getSettings().setSwapping(true);
				break;
			case 99:
				player.getSettings().setSwapping(false);
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Shop interface
		 */
		case Shop.SHOP_INVENTORY_INTERFACE:
			switch(button) {
			case 23: //Main stock
				if (shop != null && childButton >= 0 && childButton < Shop.SIZE) {
    				item = shop.getMainStock().get(childButton);
    				if(item != null && item.getDefinition() != null) {
    					switch(packet.getOpcode()) {
    					case 234:
        					Shop.costItem(player, childButton, item.getId());
    						break;
    					case 87:
    						Shop.buyItem(player, childButton, item.getId(), 1);
    						break;
    					case 37:
    						Shop.buyItem(player, childButton, item.getId(), 5);
    						break;
    					case 3:
    						Shop.buyItem(player, childButton, item.getId(), 10);
    						break;
	    				case 70:
	    					player.getInterfaceState().openEnterAmountInterface(Shop.SHOP_INVENTORY_INTERFACE, childButton, item.getId());
	    					break;
	    				case 29:
	    					player.getActionSender().sendMessage(item.getDefinition().getDescription());
	    					break;
    					default:
    						logger.info("Unhandled action button : " + packet.getOpcode() + " - "+ interfaceId + " - " + button + " - " + childButton);
    						break;    					
    					}
    				}
				}				
				break;
			case 24: //Player stock
				if (shop != null && childButton >= 0 && childButton < Shop.SIZE) {
    				item = shop.getPlayerStock().get(childButton);
    				if(item != null && item.getDefinition() != null) {
    					switch(packet.getOpcode()) {
    					case 234:
        					Shop.costItem(player, childButton, item.getId());
    						break;
    					case 87:
    						Shop.buyItem(player, childButton, item.getId(), 1);
    						break;
    					case 37:
    						Shop.buyItem(player, childButton, item.getId(), 5);
    						break;
    					case 3:
    						Shop.buyItem(player, childButton, item.getId(), 10);
    						break;
	    				case 70:
	    					player.getInterfaceState().openEnterAmountInterface(Shop.SHOP_INVENTORY_INTERFACE, childButton, item.getId());
	    					break;
	    				case 156:
	    					player.getActionSender().sendMessage(item.getDefinition().getDescription());
	    					break;
    					default:
    						logger.info("Unhandled action button : " + packet.getOpcode() + " - "+ interfaceId + " - " + button + " - " + childButton);
    						break;    					
    					}
    				}
				}				
				break;
			case 25: //Main stock
			case 29: //Main stock
				if(shop != null) {
					if(shop.getMainStock().size() < 1) {
						player.getActionSender().sendMessage("This shop doesn't have a main stock.");
					}
					Shop.open(player, player.getInterfaceState().getOpenShop(), 1);
				}
				break;
			case 26: //Player stock
			case 27: //Player stock
				if(shop != null) {
					if(shop.getPlayerStock().size() < 1) {
						player.getActionSender().sendMessage("This shop doesn't have a player stock.");
					}
					Shop.open(player, player.getInterfaceState().getOpenShop(), 2);
				}
				break;
			case 7: //Close
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		case Shop.PLAYER_INVENTORY_INTERFACE:
			switch(button) {
			case 0:
				if (shop != null && childButton >= 0 && childButton < Inventory.SIZE) {
    				item = player.getInventory().get(childButton);
    				if(item != null && item.getDefinition() != null) {
    					switch(packet.getOpcode()) {
    					case 234:
    						for(int i = 0; i < ItemDefinition.untradableItems.length; i++) {
    							if(item.getId() == ItemDefinition.untradableItems[i]) {
    								player.getActionSender().sendMessage("You cannot sell this item.");
    								return;
    							}
    						}
    						boolean canSell = false;
    						if(shop.getShopType() == ShopType.SPECIALIST_STORE_BUY)  {
    							if(shop.getMainStock().contains(item.getId()) || shop.getDefaultStock().contains(item.getId())) {
    								canSell = true;
    							}
    						}
    						if(shop.getShopType() == ShopType.GENERAL_STORE)  {
    							canSell = true;
    						}
    						if(item.getId() == shop.getCurrency()) {
    							canSell = false;
    						}
    						if(shop.getPlayerStock().size() < 1) {
    							canSell = false;
    						}
    						if(canSell) {
            					Shop.valueItem(player, childButton, item.getId());
    						} else {
    							player.getActionSender().sendMessage("This shop will not buy that item.");
    						}
    						break;
    					case 87:
    						Shop.sellItem(player, childButton, item.getId(), 1);
    						break;
    					case 37:
    						Shop.sellItem(player, childButton, item.getId(), 5);
    						break;
    					case 3:
    						Shop.sellItem(player, childButton, item.getId(), 10);
    						break;
	    				case 70:
	    					player.getInterfaceState().openEnterAmountInterface(Shop.PLAYER_INVENTORY_INTERFACE, childButton, item.getId());
	    					break;
	    				case 156:
	    					player.getActionSender().sendMessage(item.getDefinition().getDescription());
	    					break;
    					default:
    						logger.info("Unhandled action button : " + packet.getOpcode() + " - "+ interfaceId + " - " + button + " - " + childButton);
    						break;    					
    					}
    				}
				}				
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Prayer tab
		 */
		case 271:
			switch(button) {
			case 5:
			case 7:
			case 9:
			case 11:
			case 13:
			case 15:
			case 17:
			case 19:
			case 21:
			case 23:
			case 25:
			case 27:
			case 29:
			case 31:
			case 33:
			case 35:
			case 37:
			case 39:
			case 41:
			case 43:
			case 45:
			case 47:
			case 49:
			case 51:
			case 53:
			case 55:
				int buttonId = button - 5;
				int prayerButton = buttonId > 0 ? buttonId / 2 : buttonId;				
				Prayers.activatePrayer(player, prayerButton);
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Autocast normal magics
		 */
		case 319:
			switch(button) {
			case 0:
				MagicCombatAction.setAutocast(player, Spell.WIND_STRIKE, config, defensive);
				break;
			case 1:
				MagicCombatAction.setAutocast(player, Spell.WATER_STRIKE, config, defensive);
				break;
			case 2:
				MagicCombatAction.setAutocast(player, Spell.EARTH_STRIKE, config, defensive);
				break;
			case 3:
				MagicCombatAction.setAutocast(player, Spell.FIRE_STRIKE, config, defensive);
				break;
			case 4:
				MagicCombatAction.setAutocast(player, Spell.WIND_BOLT, config, defensive);
				break;
			case 5:
				MagicCombatAction.setAutocast(player, Spell.WATER_BOLT, config, defensive);
				break;
			case 6:
				MagicCombatAction.setAutocast(player, Spell.EARTH_BOLT, config, defensive);
				break;
			case 7:
				MagicCombatAction.setAutocast(player, Spell.FIRE_BOLT, config, defensive);
				break;
			case 8:
				MagicCombatAction.setAutocast(player, Spell.WIND_BLAST, config, defensive);
				break;
			case 9:
				MagicCombatAction.setAutocast(player, Spell.WATER_BLAST, config, defensive);
				break;
			case 10:
				MagicCombatAction.setAutocast(player, Spell.EARTH_BLAST, config, defensive);
				break;
			case 11:
				MagicCombatAction.setAutocast(player, Spell.FIRE_BLAST, config, defensive);
				break;
			case 12:
				MagicCombatAction.setAutocast(player, Spell.WIND_WAVE, config, defensive);
				break;
			case 13:
				MagicCombatAction.setAutocast(player, Spell.WATER_WAVE, config, defensive);
				break;
			case 14:
				MagicCombatAction.setAutocast(player, Spell.EARTH_WAVE, config, defensive);
				break;
			case 15:
				MagicCombatAction.setAutocast(player, Spell.FIRE_WAVE, config, defensive);
				break;
			case 174: //Cancel
				player.getEquipment().fireItemChanged(Equipment.SLOT_WEAPON);
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Autocast slayer's staff
		 */
		case 310:
			switch(button) {
			case 61:
				MagicCombatAction.setAutocast(player, Spell.CRUMBLE_UNDEAD, 77, defensive);
				break;
			case 0:
				MagicCombatAction.setAutocast(player, Spell.MAGIC_DART, 79, defensive);
				break;
			case 1:
				MagicCombatAction.setAutocast(player, Spell.WIND_WAVE, 69, defensive);
				break;
			case 2:
				MagicCombatAction.setAutocast(player, Spell.WATER_WAVE, 71, defensive);
				break;
			case 3:
				MagicCombatAction.setAutocast(player, Spell.EARTH_WAVE, 73, defensive);
				break;
			case 4:
				MagicCombatAction.setAutocast(player, Spell.FIRE_WAVE, 75, defensive);
				break;
			case 49: //Cancel
				player.getEquipment().fireItemChanged(Equipment.SLOT_WEAPON);
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Autocast ancient magics
		 */
		case 388:
			switch(button) {
			case 93:
				MagicCombatAction.setAutocast(player, Spell.SMOKE_RUSH, 13, defensive);
				break;
			case 145:
				MagicCombatAction.setAutocast(player, Spell.SHADOW_RUSH, 15, defensive);
				break;
			case 51:
				MagicCombatAction.setAutocast(player, Spell.BLOOD_RUSH, 17, defensive);
				break;
			case 7:
				MagicCombatAction.setAutocast(player, Spell.ICE_RUSH, 19, defensive);
				break;
			case 119:
				MagicCombatAction.setAutocast(player, Spell.SMOKE_BURST, 21, defensive);
				break;
			case 171:
				MagicCombatAction.setAutocast(player, Spell.SHADOW_BURST, 23, defensive);
				break;
			case 71:
				MagicCombatAction.setAutocast(player, Spell.BLOOD_BURST, 25, defensive);
				break;
			case 29:
				MagicCombatAction.setAutocast(player, Spell.ICE_BURST, 27, defensive);
				break;
			case 106:
				MagicCombatAction.setAutocast(player, Spell.SMOKE_BLITZ, 29, defensive);
				break;
			case 158:
				MagicCombatAction.setAutocast(player, Spell.SHADOW_BLITZ, 31, defensive);
				break;
			case 62:
				MagicCombatAction.setAutocast(player, Spell.BLOOD_BLITZ, 33, defensive);
				break;
			case 18:
				MagicCombatAction.setAutocast(player, Spell.ICE_BLITZ, 35, defensive);
				break;
			case 132:
				MagicCombatAction.setAutocast(player, Spell.SMOKE_BARRAGE, 37, defensive);
				break;
			case 184:
				MagicCombatAction.setAutocast(player, Spell.SHADOW_BARRAGE, 39, defensive);
				break;
			case 82:
				MagicCombatAction.setAutocast(player, Spell.BLOOD_BARRAGE, 41, defensive);
				break;
			case 40:
				MagicCombatAction.setAutocast(player, Spell.ICE_BARRAGE, 43, defensive);
				break;
			case 6: //Cancel
				player.getEquipment().fireItemChanged(Equipment.SLOT_WEAPON);
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Clan chat interface
		 */
		case 589:
			switch(button) {
			case 9:
				if(World.getWorld().clanIsRegistered(player.getName())) {
					player.getActionSender().sendString(590, 32, player.getPrivateChat().getChannelName());
				}
				player.getActionSender().sendString(590, 33, player.getPrivateChat().getEntryRank().getText());
				player.getActionSender().sendString(590, 34, player.getPrivateChat().getTalkRank().getText());
				player.getActionSender().sendString(590, 35, player.getPrivateChat().getKickRank().getText());
				player.getActionSender().sendString(590, 36, "");
				player.getActionSender().sendString(590, 41, "");
				player.getActionSender().sendInterfaceConfig(590, 31, true);
				player.getActionSender().sendInterfaceConfig(590, 36, true);
				player.getActionSender().sendInterface(590, true);
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Clan chat setup
		 */
		case 590:
			switch(button) {
			case 32:
				switch(packet.getOpcode()) {
				case 234:
					player.getInterfaceState().openEnterTextInterface(590, "Enter chat prefix:");
					break;
				case 87:
					player.getPrivateChat().setChannelName("");
					player.getActionSender().sendString(590, 32, "Chat disabled");
					break;
				}
				break;
			case 33:
				switch(packet.getOpcode()) {
				case 234:
					player.getPrivateChat().setEntryRank(EntryRank.ANYONE);
					break;
				case 87:
					player.getPrivateChat().setEntryRank(EntryRank.ANY_FRIENDS);
					break;
				case 37:
					player.getPrivateChat().setEntryRank(EntryRank.RECRUIT);
					break;
				case 3:
					player.getPrivateChat().setEntryRank(EntryRank.CORPORAL);
					break;
				case 70:
					player.getPrivateChat().setEntryRank(EntryRank.SERGEANT);
					break;
				case 29:
					player.getPrivateChat().setEntryRank(EntryRank.LIEUTENANT);
					break;
				case 192:
					player.getPrivateChat().setEntryRank(EntryRank.CAPTAIN);
					break;
				case 154:
					player.getPrivateChat().setEntryRank(EntryRank.GENERAL);
					break;
				case 65:
					player.getPrivateChat().setEntryRank(EntryRank.ONLY_ME);
					break;
				}
				player.getActionSender().sendString(590, 33, player.getPrivateChat().getEntryRank().getText());
				break;
			case 34:
				switch(packet.getOpcode()) {
				case 234:
					player.getPrivateChat().setTalkRank(TalkRank.ANYONE);
					break;
				case 87:
					player.getPrivateChat().setTalkRank(TalkRank.ANY_FRIENDS);
					break;
				case 37:
					player.getPrivateChat().setTalkRank(TalkRank.RECRUIT);
					break;
				case 3:
					player.getPrivateChat().setTalkRank(TalkRank.CORPORAL);
					break;
				case 70:
					player.getPrivateChat().setTalkRank(TalkRank.SERGEANT);
					break;
				case 29:
					player.getPrivateChat().setTalkRank(TalkRank.LIEUTENANT);
					break;
				case 192:
					player.getPrivateChat().setTalkRank(TalkRank.CAPTAIN);
					break;
				case 154:
					player.getPrivateChat().setTalkRank(TalkRank.GENERAL);
					break;
				case 65:
					player.getPrivateChat().setTalkRank(TalkRank.ONLY_ME);
					break;
				}
				player.getActionSender().sendString(590, 34, player.getPrivateChat().getTalkRank().getText());
				break;		
			case 35:
				switch(packet.getOpcode()) {
				case 234:
				case 87:
				case 37:
				case 3:
					player.getPrivateChat().setKickRank(KickRank.CORPORAL);
					break;
				case 70:
					player.getPrivateChat().setKickRank(KickRank.SERGEANT);
					break;
				case 29:
					player.getPrivateChat().setKickRank(KickRank.LIEUTENANT);
					break;
				case 192:
					player.getPrivateChat().setKickRank(KickRank.CAPTAIN);
					break;
				case 154:
					player.getPrivateChat().setKickRank(KickRank.GENERAL);
					break;
				case 65:
					player.getPrivateChat().setKickRank(KickRank.ONLY_ME);
					break;
				}
				player.getActionSender().sendString(590, 35, player.getPrivateChat().getKickRank().getText());
				break;				
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Beard hair dressers
		 */
		case 199:
			switch(button) {
			case 139:
				player.setInterfaceAttribute("newBeard", 11);
				break;
			case 140:
				player.setInterfaceAttribute("newBeard", 10);
				break;
			case 141:
				player.setInterfaceAttribute("newBeard", 13);
				break;
			case 142:
				player.setInterfaceAttribute("newBeard", 15);
				break;
			case 143:
				player.setInterfaceAttribute("newBeard", 17);
				break;
			case 144:
				player.setInterfaceAttribute("newBeard", 12);
				break;
			case 145:
				player.setInterfaceAttribute("newBeard", 14);
				break;
			case 146:
				player.setInterfaceAttribute("newBeard", 16);
				break;
			case 127:
			case 128:
			case 129:
			case 130:
			case 131:
			case 132:
			case 133:
			case 134:
			case 135:
			case 136:
			case 137:
			case 138:
				player.setInterfaceAttribute("newHairColour", button - 127);
				break;
			case 104:
				if(player.getInterfaceAttribute("newBeard") != null) {
					player.getAppearance().setLook(12, (Integer) player.getInterfaceAttribute("newBeard"));
				}
				if(player.getInterfaceAttribute("newHairColour") != null) {
					player.getAppearance().setLook(1, (Integer) player.getInterfaceAttribute("newHairColour"));
				}
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				player.getActionSender().removeAllInterfaces();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Thessalia's Makeovers (female legs)
		 */
		case 201:
			switch(button) {
			case 131:
			case 132:
			case 133:
			case 134:
			case 135:
				player.setInterfaceAttribute("newLegs", button - 61);
				break;
			case 136:
			case 137:
				player.setInterfaceAttribute("newLegs", button - 60);
				break;
			case 105:
				player.setInterfaceAttribute("newLegsColour", button - 105);
				break;
			case 90:
			case 91:
			case 92:
			case 93:
			case 94:
			case 95:
			case 96:
			case 97:
			case 98:
			case 99:
			case 100:
			case 101:
			case 102:
			case 103:
			case 104:	
				player.setInterfaceAttribute("newLegsColour", button - 89);
				break;
			case 106:
				if(player.getInterfaceAttribute("newLegs") != null) {
					player.getAppearance().setLook(10, (Integer) player.getInterfaceAttribute("newLegs"));
				}
				if(player.getInterfaceAttribute("newLegsColour") != null) {
					player.getAppearance().setLook(3, (Integer) player.getInterfaceAttribute("newLegsColour"));
				}
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				player.getActionSender().removeAllInterfaces();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Thessalia's Makeovers (female body)
		 */
		case 202:
			switch(button) {
			case 146:
			case 147:
			case 148:
			case 149:
			case 150:
				player.setInterfaceAttribute("newBody", button - 90);
				break;
			case 151:
			case 152:
			case 153:
			case 154:
			case 155:
				player.setInterfaceAttribute("newArms", button - 90);
				break;
			case 90:
			case 91:
			case 92:
			case 93:
			case 94:
			case 95:
			case 96:
			case 97:
			case 98:
			case 99:
			case 100:
			case 101:
			case 102:
			case 103:
			case 104:
			case 105:	
				player.setInterfaceAttribute("newBodyColour", button - 90);
				break;
			case 106:
				if(player.getInterfaceAttribute("newBody") != null) {
					player.getAppearance().setLook(7, (Integer) player.getInterfaceAttribute("newBody"));
				}
				if(player.getInterfaceAttribute("newArms") != null) {
					player.getAppearance().setLook(8, (Integer) player.getInterfaceAttribute("newArms"));
				}
				if(player.getInterfaceAttribute("newBodyColour") != null) {
					player.getAppearance().setLook(2, (Integer) player.getInterfaceAttribute("newBodyColour"));
				}
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				player.getActionSender().removeAllInterfaces();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Female hair dressers
		 */
		case 203:
			switch(button) {
			case 136:
			case 137:
			case 138:
			case 139:
			case 140:
			case 141:
			case 142:
			case 143:
			case 144:
			case 145:
				player.setInterfaceAttribute("newHair", button - 91);
				break;
			case 124:
			case 125:
			case 126:
			case 127:
			case 128:
			case 129:
			case 130:
			case 131:
			case 132:
			case 133:
			case 134:
			case 135:
				player.setInterfaceAttribute("newHairColour", button - 124);
				break;
			case 99:
				if(player.getInterfaceAttribute("newHair") != null) {
					player.getAppearance().setLook(6, (Integer) player.getInterfaceAttribute("newHair"));
				}
				if(player.getInterfaceAttribute("newHairColour") != null) {
					player.getAppearance().setLook(1, (Integer) player.getInterfaceAttribute("newHairColour"));
				}
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				player.getActionSender().removeAllInterfaces();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Male hair dressers
		 */
		case 204:
			switch(button) {
			case 134:
			case 135:
			case 136:
			case 137:
			case 138:
			case 139:
			case 140:
			case 141:
			case 142:
				player.setInterfaceAttribute("newHair", button - 134);
				break;
			case 122:
			case 123:
			case 124:
			case 125:
			case 126:
			case 127:
			case 128:
			case 129:
			case 130:
			case 131:
			case 132:
			case 133:
				player.setInterfaceAttribute("newHairColour", button - 122);
				break;
			case 98:
				if(player.getInterfaceAttribute("newHair") != null) {
					player.getAppearance().setLook(6, (Integer) player.getInterfaceAttribute("newHair"));
				}
				if(player.getInterfaceAttribute("newHairColour") != null) {
					player.getAppearance().setLook(1, (Integer) player.getInterfaceAttribute("newHairColour"));
				}
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				player.getActionSender().removeAllInterfaces();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Make over mage
		 */
		case 205:
			switch(button) {
			case 112:
			case 113:
				player.setInterfaceAttribute("newGender", button - 112);
				break;
			case 93:
			case 94:
			case 95:
			case 96:
			case 97:
			case 98:
			case 99:
			case 100:
				player.setInterfaceAttribute("newSkinColour", button - 93);
				break;
			case 88:
				if(player.getInventory().getCount(995) < 3000) {
					player.getActionSender().sendMessage("You don't have enough coins for a make-over.");
					player.getActionSender().removeAllInterfaces();
					break;
				}
				player.getInventory().remove(new Item(995, 3000));
				int[] look = new int[13];
				if(player.getInterfaceAttribute("newSkinColour") != null) {
					player.getAppearance().setLook(5, (Integer) player.getInterfaceAttribute("newSkinColour"));
				}
				if(player.getInterfaceAttribute("newGender") != null) {
					player.getAppearance().setLook(0, (Integer) player.getInterfaceAttribute("newGender"));
				}
				switch(player.getAppearance().getGender()) {
				case 0: //Male
					look[0] = 0;
					look[1] = 0;
					look[2] = 0;
					look[3] = 0;
					look[4] = 0;
					look[5] = player.getAppearance().getSkinColour();
					look[6] = 0;
					look[7] = 18;
					look[8] = 26;
					look[9] = 33;
					look[10] = 36;
					look[11] = 42;
					look[12] = 10;
					break;
				case 1: //Female
					look[0] = 1;
					look[1] = 0;
					look[2] = 0;
					look[3] = 0;
					look[4] = 0;
					look[5] = player.getAppearance().getSkinColour();
					look[6] = 45;
					look[7] = 56;
					look[8] = 61;
					look[9] = 67;
					look[10] = 70;
					look[11] = 79;
					look[12] = -1;
					break;
				}
				player.getAppearance().setLook(look);
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				player.getActionSender().removeAllInterfaces();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Thessalia's Makeovers (male legs)
		 */
		case 206:
			switch(button) {
			case 128:
			case 129:
			case 130:
			case 131:
				player.setInterfaceAttribute("newLegs", button - 92);
				break;
			case 105:
				player.setInterfaceAttribute("newLegsColour", button - 105);
				break;
			case 90:
			case 91:
			case 92:
			case 93:
			case 94:
			case 95:
			case 96:
			case 97:
			case 98:
			case 99:
			case 100:
			case 101:
			case 102:
			case 103:
			case 104:	
				player.setInterfaceAttribute("newLegsColour", button - 89);
				break;
			case 106:
				if(player.getInterfaceAttribute("newLegs") != null) {
					player.getAppearance().setLook(10, (Integer) player.getInterfaceAttribute("newLegs"));
				}
				if(player.getInterfaceAttribute("newLegsColour") != null) {
					player.getAppearance().setLook(3, (Integer) player.getInterfaceAttribute("newLegsColour"));
				}
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				player.getActionSender().removeAllInterfaces();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		/*
		 * Thessalia's Makeovers (male body)
		 */
		case 207:
			switch(button) {
			case 157:
			case 158:
				player.setInterfaceAttribute("newBody", button - 139);
				break;
			case 159:
			case 161:
			case 163:
				player.setInterfaceAttribute("newBody", button - 138);
				break;
			case 160:
			case 162:
				player.setInterfaceAttribute("newBody", button - 140);
				break;
			case 164:
			case 165:
			case 166:
			case 167:
			case 168:
			case 169:
				player.setInterfaceAttribute("newArms", button - 138);
				break;
			case 90:
			case 91:
			case 92:
			case 93:
			case 94:
			case 95:
			case 96:
			case 97:
			case 98:
			case 99:
			case 100:
			case 101:
			case 102:
			case 103:
			case 104:
			case 105:	
				player.setInterfaceAttribute("newBodyColour", button - 90);
				break;
			case 106:
				if(player.getInterfaceAttribute("newBody") != null) {
					player.getAppearance().setLook(7, (Integer) player.getInterfaceAttribute("newBody"));
				}
				if(player.getInterfaceAttribute("newArms") != null) {
					player.getAppearance().setLook(8, (Integer) player.getInterfaceAttribute("newArms"));
				}
				if(player.getInterfaceAttribute("newBodyColour") != null) {
					player.getAppearance().setLook(2, (Integer) player.getInterfaceAttribute("newBodyColour"));
				}
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				player.getActionSender().removeAllInterfaces();
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		case 374:
			switch(button) {
			case 5:
				World.getWorld().getFightPits().addWaitingPlayer(player);
				player.setPnpc(-1);
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				player.getActionSender().removeAllInterfaces();
				player.setTeleportTarget(FightPits.ORB_OBJECT);
				break;
			case 11:
				player.setTeleportTarget(FightPits.CENTRE_ORB);
				break;
			case 12:
				player.setTeleportTarget(FightPits.NORTH_WEST);
				break;
			case 13:
				player.setTeleportTarget(FightPits.NORTH_EAST);
				break;
			case 14:
				player.setTeleportTarget(FightPits.SOUTH_EAST);
				break;
			case 15:
				player.setTeleportTarget(FightPits.SOUTH_WEST);
				break;
			default:
				logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
				break;
			}
			break;
		default:
			logger.info("Unhandled action button : " + interfaceId + " - " + button + " - " + childButton);
			break;
		}
	}

}
