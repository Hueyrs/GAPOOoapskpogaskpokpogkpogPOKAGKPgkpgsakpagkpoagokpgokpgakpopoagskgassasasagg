package org.rs2server.rs2.packet;

import org.rs2server.rs2.ScriptManager;
import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.action.Action.AnimationPolicy;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Cannon;
import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Door;
import org.rs2server.rs2.model.Entity;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.GameObjectDefinition;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.ItemDefinition;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.content.ItemPicking;
import org.rs2server.rs2.content.MilkCow;
import org.rs2server.rs2.content.Obelisks;
import org.rs2server.rs2.content.SOSDoors;
import org.rs2server.rs2.content.ShantayPass;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.Mob.InteractionMode;
import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.minigame.impl.Barrows;
import org.rs2server.rs2.model.minigame.impl.WarriorsGuild;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.model.skills.Agility;
import org.rs2server.rs2.model.skills.Mining;
import org.rs2server.rs2.model.skills.Smithing;
import org.rs2server.rs2.model.skills.Woodcutting;
import org.rs2server.rs2.model.skills.Agility.Obstacle;
import org.rs2server.rs2.model.skills.Mining.Rock;
import org.rs2server.rs2.model.skills.Smithing.Bar;
import org.rs2server.rs2.model.skills.Woodcutting.Tree;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

/**
 * Object option packet handler.
 * @author Graham Edgecombe
 *
 */
@SuppressWarnings("unused")
public class ObjectOptionPacketHandler implements PacketHandler {

	private static final int OPTION_1 = 31, OPTION_2 = 203, ITEM_ON_OBJECT = 134;

	@Override
	public void handle(Player player, Packet packet) {
		if(player.getAttribute("busy") != null) {
			return;
		}
		player.getActionQueue().clearRemovableActions();
		switch(packet.getOpcode()) {
		case OPTION_1:
			handleOption1(player, packet);
			break;
		case OPTION_2:
			handleOption2(player, packet);
			break;
		case ITEM_ON_OBJECT:
			handleOptionItem(player, packet);
			break;
		}
	}

	/**
	 * Handles the option 1 packet.
	 * @param player The player.
	 * @param packet The packet.
	 */
	private void handleOption1(final Player player, Packet packet) {
		final int x = packet.getLEShortA();
		final int id = packet.getShort();
		final int y = packet.getShort();
		int z = player.getLocation().getZ();
		if(player.getAttribute("temporaryHeight") != null) {
			z = player.getAttribute("temporaryHeight");
		}
		final Location loc = Location.create(x, y, z);
		Region r = player.getRegion();
		System.out.println("Loc " + loc+"     obj "+id+"     reg "+r.getCoordinates().toString());
		final Door door = r.doorForLocation(loc, id);
		final GameObject obj = r.getGameObject(loc, id);
		World.getWorld().submit(new Tickable(2) {
			public void execute() {
				if(Misc.getDistance(player.getLocation(), loc) <= 1) {
					switch(id) {
					case 16123:
					case 16124:
						SOSDoors.create(player, loc).door();
						break;
					case 15638:
						player.setTeleportTarget(Location.create(2839, 3539, 0));
						break;
					case 1738:
						if(loc.getX() == 2839 && loc.getY() == 3537) {
							player.setTeleportTarget(Location.create(2840, 3539, 2));
						} else {
							door.open(true);
						}
						break;
					case 15641:
						if(loc.getX() == 2847 && loc.getY() == 3540) {
							WarriorsGuild.create(player).enterCyclopes();
						} else {
							door.open(true);
						}
						break;
					case 15644:
						if(loc.getX() == 2847 && loc.getY() == 3541) {
							WarriorsGuild.create(player).enterCyclopes();
						} else {
							door.open(true);
						}
						break;
					case 409:
						player.getSkills().getPrayer().prayAltar(loc);
						return;
					}
					this.stop();	
				}
			}
		});
		/*switch(id) {
		case 15638:
			player.setTeleportTarget(Location.create(2839, 3539, 0));
			break;
		case 1738:
			if(loc.getX() == 2839 && loc.getY() == 3537) {
				player.setTeleportTarget(Location.create(2840, 3539, 2));
			}
			break;
		case 15641:
			if(loc.getX() == 2847 && loc.getY() == 3540) {
				WarriorsGuild.create(player).enterCyclopes();
			}
			return;
		case 15644:
			if(loc.getX() == 2847 && loc.getY() == 3541) {
				WarriorsGuild.create(player).enterCyclopes();
			}
			return;
		}*/
		if(obj == null && id != 23271) {
			return;
		}
		
		GameObjectDefinition def = GameObjectDefinition.forId(id);
		int width = 1;
		int height = 1;
		if(def != null) {
			width = def.getSizeX();
			height = def.getSizeY();
		}
		if(obj != null && id != 2302) {
			player.face(player.getLocation().oppositeTileOfEntity(obj));
			player.getActionSender().sendDebugPacket(packet.getOpcode(), "ObjOpt1", new Object[] { "ID: " + id, "Loc: " + loc, "X: " + obj.getWidth(), "Y: " + obj.getHeight(), "Direction: " + obj.getDirection(), "Type: " + obj.getType() });
		}
		
		int distance = 1;
		/*if(obj.getLocation().getX() == 3099 && obj.getLocation().getY() == 3095 && obj.getDefinition().getId() == 3033) {
			player.tutorialStep = 6;
			DialogueManager.openDialogue(player, 569);
		}*/
		Barrows barrows = new Barrows(player);
		barrows.handleObjectOption(id);
		switch(id) {
		}
		if(id == 8689 || id == 12111) {
			MilkCow.milkCow(player, obj);
			return;
		}
		/*if(obj.getDefinition().getName().trim().toLowerCase().contains("altar") || obj.getDefinition().getName().trim().toLowerCase().contains("alter")) {
			player.getSkills().getPrayer().prayAltar();
			return;
		}*/
		
		if(id == 4031) {
			ShantayPass.enterPass(player);
		}
		
		if(id == 2282) {
			distance = 5;
		}
		
		Action action = null;
		Tree tree = Tree.forId(id);
		Rock rock = Rock.forId(id);
		final Obstacle obstacle = Obstacle.forLocation(loc);
		if(tree != null) {
			action = new Woodcutting(player, obj);
		} else if(rock != null) {
			action = new Mining(player, obj);
		} else if(obstacle != null) {
			action = new Action(player, 0) {
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
					Agility.tackleObstacle(player, obstacle, obj);
				}			
			};
		} else {
			action = new Action(player, 0) {
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
				@SuppressWarnings("null")
				@Override
				public void execute() {
					this.stop();
					if(Obelisks.handle(player, id, loc)) {
						return;
					}
					if(door != null) {
						int doorX = door.getObject().getLocation().getX();
						int doorY = door.getObject().getLocation().getY();
						/*if(doorX == 3098 && doorY == 3014) {
							if(player.tutorialStep != 2) {
								return;
							}
							player.tutorialStep = 3;
							NPC survivalExpert = null;
							for(NPC npcs : World.getWorld().getNPCs()) {
								if(npcs != null) {
									if(npcs.getDefinition().getId() == 943) {
										survivalExpert = npcs;
									}
								}
							}
							player.getActionSender().sendHintArrow(survivalExpert, 0, 1);
						}*/
						door.open(true);
					} else {
						switch(id) {
						case 5859:
						case 5858:
							World.getWorld().getBarrelchest().leaveGame(player);
							break;
						case 16148:
							player.getActionSender().sendMessage("You climb up the ladder to the surface.");
							player.setTeleportTarget(Location.create(3081, 3421));
							break;
						case 8966:
							player.setTeleportTarget(Entity.DEFAULT_LOCATION);
							break;
						case 2465:
							player.portal = obj;
							DialogueManager.openDialogue(player, 693);
							break;
						case 15638:
							player.setTeleportTarget(Location.create(2839, 3539, 0));
							break;
						case 1738:
							if(loc.getX() == 2839 && loc.getY() == 3537) {
								player.setTeleportTarget(Location.create(2840, 3539, 2));
							} else {
								door.open(true);
							}
							break;
						case 15641:
							if(loc.getX() == 2847 && loc.getY() == 3540) {
								WarriorsGuild.create(player).enterCyclopes();
							} else {
								door.open(true);
							}
							break;
						case 15644:
							if(loc.getX() == 2847 && loc.getY() == 3541) {
								WarriorsGuild.create(player).enterCyclopes();
							} else {
								door.open(true);
							}
							break;
						case 7321:
							if(!player.getInventory().hasItem(new Item(6792))) {
								player.getActionSender().sendMessage("A rare keys binds the portal closed.");
								return;
							}
							//the place with lucien and ect.
							player.magesRevenge = 6;
							player.setTeleportTarget(Location.create(2384, 4708));
							break;
						case 23271:
							if(player.getLocation().getY() < 3522 && !player.sawWildJump) {
								player.wildDitch = obj;
								player.getActionSender().sendInterface(382, true);
							} else {
								Agility.tackleObstacle(player, Obstacle.WILDERNESS_DITCH, obj);
							}
							break;
						case 6:
						case 7:
						case 8:
						case 9:
							if(player.getAttribute("cannon") != null) {
								Cannon cannon = (Cannon) player.getAttribute("cannon");
								if(cannon.getGameObject().getLocation().equals(loc)) {
									if(id == 6) {
										cannon.fire();
									} else {
										cannon.destroy();
									}
								} else {
									player.getActionSender().sendMessage("This is not your cannon.");									
								}
							} else {
								player.getActionSender().sendMessage("This is not your cannon.");
							}
							break;
						case 450:
						case 451:
						case 452:
						case 453:
							player.getActionSender().sendMessage("There is no ore currently available in this rock.");
							return;
						case 26384:
							if(player.getSkills().getLevel(Skills.STRENGTH) < 70) {
								player.getActionSender().sendMessage("You need a Strength level of 70 to bang this door down.");
							} else if(player.getInventory().getCount(2347) < 1) {
								player.getActionSender().sendMessage("You need a hammer to bang this door down.");					
							} else {
								player.getActionQueue().addAction(new Action(player, 3) {
									@Override
									public void execute() {
										if(player.getLocation().getX() == 2851) {
											player.setTeleportTarget(Location.create(player.getLocation().getX() - 1, player.getLocation().getY(), player.getLocation().getZ()));
										} else if(player.getLocation().getX() == 2850) {
											player.setTeleportTarget(Location.create(player.getLocation().getX() + 1, player.getLocation().getY(), player.getLocation().getZ()));											
										}
										this.stop();
									}
									@Override
									public AnimationPolicy getAnimationPolicy() {
										return AnimationPolicy.RESET_NONE;
									}
									@Override
									public CancelPolicy getCancelPolicy() {
										return CancelPolicy.ALWAYS;
									}
									@Override
									public StackPolicy getStackPolicy() {
										return StackPolicy.NEVER;
									}									
								});
								player.playAnimation(Animation.create(7002));
							}
							break;
						default:
							if(obj.getDefinition() != null) {
								if(obj.getDefinition().getName().toLowerCase().contains("banana")) {
									player.getActionSender().sendMessage("You reach out to the tree...");
									player.getActionQueue().addAction(new Action(player, 3) {
										@Override
										public void execute() {
											player.getActionSender().sendMessage("...and grab a banana.");
											player.getInventory().add(new Item(1963));
											this.stop();
										}
										@Override
										public AnimationPolicy getAnimationPolicy() {
											return AnimationPolicy.RESET_ALL;
										}
										@Override
										public CancelPolicy getCancelPolicy() {
											return CancelPolicy.ALWAYS;
										}
										@Override
										public StackPolicy getStackPolicy() {
											return StackPolicy.NEVER;
										}									
									});		
									return;						
								} else if(obj.getDefinition().getName().toLowerCase().contains("bank")) {
									NPC closestBanker = null;
									int closestDist = 10;
									for (NPC banker : World.getWorld().getRegionManager().getLocalNpcs(player)) {
										if (banker.getDefinition().getName().toLowerCase().contains("banker")) {
											if (obj.getLocation().distanceToPoint(banker.getLocation()) < closestDist) {
												closestDist = obj.getLocation().distanceToPoint(banker.getLocation());
												closestBanker = banker;
											}
										}
									}
									if (closestBanker != null) {
										player.setInteractingEntity(InteractionMode.TALK, closestBanker);
										closestBanker.setInteractingEntity(InteractionMode.TALK, player);
										DialogueManager.openDialogue(player, 0);
									}
									return;
								}								
							}
							String scriptName = "objectOptionOne" + id;
							if(!ScriptManager.getScriptManager().invokeWithFailTest(scriptName, player, obj)) {
								player.getActionSender().sendMessage("Nothing interesting happens.");	
								System.out.println("[OPT1]: Loc " + loc+"     obj "+id);
							}
							break;
						}
					}
				}			
			};
		}
		if(action != null) {
			player.addCoordinateAction(player.getWidth(), player.getHeight(), loc, width, height, distance, action);
		}
	}

	/**
     * Handles the option 2 packet.
     * @param player The player.
     * @param packet The packet.
     */
    private void handleOption2(final Player player, Packet packet) {        
        final int x = packet.getShort() & 0xFFFF;
        final int id = packet.getLEShort() & 0xFFFF;
        final int y = packet.getShortA() & 0xFFFF;
		int z = player.getLocation().getZ();
		if(player.getAttribute("temporaryHeight") != null) {
			z = player.getAttribute("temporaryHeight");
		}
		final Location loc = Location.create(x, y, z);
        
		final GameObject obj = player.getRegion().getGameObject(loc, id);
		if(obj == null) {
			return;
		}
		player.face(player.getLocation().oppositeTileOfEntity(obj));

		player.getActionSender().sendDebugPacket(packet.getOpcode(), "ObjOpt2", new Object[] { "ID: " + id, "Loc: " + loc });
		//ItemPicking picker = new ItemPicking(player);
		Animation PICKING = Animation.create(7270);
		switch(id) {
		case 1161: //Cabbages
			ItemPicking.pickItem(player, new Item(1965), PICKING, "You pick a cabbage.");
			return;
		case 5584: //Wheat
		case 5585:
		case 313:
			ItemPicking.pickItem(player, new Item(1947), PICKING, "You pluck some grain.");
			return;
		case 312: //Potatoes
			ItemPicking.pickItem(player, new Item(1942), PICKING, "You grab a potato.");
			return;
		}
		if(obj.getDefinition().getName().toLowerCase().contains("bank")) {
			Bank.open(player);
		}
		Action action = null;
		Action prospectAction = null;
		final Rock rock = Rock.forId(id);
		if(rock != null || (id == 450 || id == 451 || id == 452 || id == 453)) {
			prospectAction = new Action(player, 4) {
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
					if(id == 450 || id == 451 || id == 452 || id == 453) {
						player.getActionSender().sendMessage("This rock has no current ore available.");
					} else {
						player.getActionSender().sendMessage("This rock contains " + ItemDefinition.forId(rock.getOreId()).getName().toLowerCase() + ".");
					}
					this.stop();
				}				
			};
		}
		final Action finalProspectAction = prospectAction;
		action = new Action(player, 0) {
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
				if(rock != null && finalProspectAction != null) {
					player.getActionSender().sendMessage("You examine the rock for ores...");
					player.getActionQueue().addAction(finalProspectAction);
				} else {
					
					switch(id) {
					case 6:
						if(player.getAttribute("cannon") != null) {
							Cannon cannon = (Cannon) player.getAttribute("cannon");
							if(loc.equals(cannon.getGameObject().getLocation())) {
								cannon.destroy();
							}
						}
						break;
					default:
						String scriptName = "objectOptionTwo" + id;
						if(!ScriptManager.getScriptManager().invokeWithFailTest(scriptName, player, obj)) {
							player.getActionSender().sendMessage("Nothing interesting happens.");
							System.out.println("[OPT2]: Loc " + loc+"     obj "+id);
						}
						break;
					}
				}
				this.stop();
			}			
		};
		if(action != null) {
			player.addCoordinateAction(player.getWidth(), player.getHeight(), obj.getLocation(), obj.getWidth(), obj.getHeight(), 1, action);
		}
    }

	/**
     * Handles the item on object packet.
     * @param player The player.
     * @param packet The packet.
     */
    private void handleOptionItem(final Player player, Packet packet) {
        final int x = packet.getShortA() & 0xFFFF;
        final int slot = packet.getShortA() & 0xFFFF;
        final int id = packet.getLEShort() & 0xFFFF;
        final int y = packet.getLEShortA() & 0xFFFF;
		int z = player.getLocation().getZ();
		if(player.getAttribute("temporaryHeight") != null) {
			z = player.getAttribute("temporaryHeight");
		}
		final Location loc = Location.create(x, y, z);
        
        final Item item = player.getInventory().get(slot);
        if(item == null) {
        	return;
        }
        switch(id) {
        case 15621:
        	WarriorsGuild.create(player).armorOnPlatform();
        	break;
        }
		final GameObject obj = player.getRegion().getGameObject(loc, id);
		if (obj == null) {
			return;
		}
		System.out.println("Id: " + id + " Itemid: " + item.getDefinition().getId() + " Loc: " + loc.getX() + "," + loc.getY() + "," + loc.getZ());
		player.face(player.getLocation().oppositeTileOfEntity(obj));

		player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemOnObject", new Object[] { "ID: " + id, "Loc: " + loc });
		
		Action action = null;
		action = new Action(player, 0) {
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
				if(obj.getDefinition().getName().equalsIgnoreCase("Anvil")) {
					Bar bar = Bar.forId(item.getId());
					if(bar != null) {
						Smithing.openSmithingInterface(player, bar);
					}
				} else {
					if(obj.getDefinition().getName().contains("altar") || obj.getDefinition().getName().contains("Altar")) {
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
							player.getSkills().getPrayer().altarBone(new Item(id));
							break;
						}
					}
					switch(id) {
					case 6:
						if(player.getAttribute("cannon") != null) {
							Cannon cannon = (Cannon) player.getAttribute("cannon");
							if(loc.equals(cannon.getGameObject().getLocation())) {
								if(item.getId() == 2) {
									int cannonBalls = cannon.getCannonBalls();
									if(cannonBalls >= 30) {
										player.getActionSender().sendMessage("Your cannon is already full.");
										return;
									}
									int newCannonBalls = item.getCount();
									if(newCannonBalls > 30) {
										newCannonBalls = 30;
									}
									if(newCannonBalls + cannonBalls > 30) {
										newCannonBalls = 30 - cannonBalls;
									}
									if(newCannonBalls < 1) {
										return;
									}
									player.getInventory().remove(new Item(2, newCannonBalls));
									cannon.addCannonBalls(newCannonBalls);
									player.getActionSender().sendMessage("You load " + newCannonBalls + " cannonball" + (newCannonBalls > 1 ? "s" : "") + " into your cannon.");									
								}
							}
						}
						break;
					case 7:
						if(player.getAttribute("cannon") != null) {
							Cannon cannon = (Cannon) player.getAttribute("cannon");
							if(loc.equals(cannon.getGameObject().getLocation())) {
								if(item.getId() == 8) {
									cannon.addPart(new Item(8, 1));
								}
							}
						}
						break;
					case 8:
						if(player.getAttribute("cannon") != null) {
							Cannon cannon = (Cannon) player.getAttribute("cannon");
							if(loc.equals(cannon.getGameObject().getLocation())) {
								if(item.getId() == 10) {
									cannon.addPart(new Item(10, 1));
								}
							}
						}
						break;
					case 9:
						if(player.getAttribute("cannon") != null) {
							Cannon cannon = (Cannon) player.getAttribute("cannon");
							if(loc.equals(cannon.getGameObject().getLocation())) {
								if(item.getId() == 12) {
									cannon.addPart(new Item(12, 1));
								}
							}
						}
						break;
					}
				}
				this.stop();
			}			
		};
		if(action != null) {
			player.addCoordinateAction(player.getWidth(), player.getHeight(), obj.getLocation(), obj.getWidth(), obj.getHeight(), 1, action);
		}
    }


}
