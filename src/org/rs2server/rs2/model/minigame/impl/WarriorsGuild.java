package org.rs2server.rs2.model.minigame.impl;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.NPCDefinition;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.net.ActionSender.DialogueType;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

/**
 * For the warrior's guild minigame.
 * @author Canownueasy
 *
 */
public class WarriorsGuild {
	
	public void throwShot() {
		player.getWalkingQueue().addStep(2861, 3553);
		player.getWalkingQueue().finish();
		World.getWorld().submit(new Tickable(2) {
			public void execute() {
				if(player.getLocation().getX() == 2861 && player.getLocation().getY() == 3553) {
					startThrow();
					stop();
				}
			}
		});
	}
	
	public void startThrow() {
		player.face(Location.create(2861, 3554, 1));
		player.playAnimation(Animation.create(7270));
		World.getWorld().submit(new Tickable(2) {
			public void execute() {
				player.getActionSender().sendDialogue(
						"Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
						"Standing throw",
						"Step and throw",
						"Spin and throw");
				player.getInterfaceState().setNextDialogueId(0, 684);
				player.getInterfaceState().setNextDialogueId(1, 685);
				player.getInterfaceState().setNextDialogueId(2, 686);
				stop();
			}
		});
	}
	
	public void throwShot(Animation animation) {
		player.face(Location.create((player.getLocation().getX() + 1), player.getLocation().getY(), 1));
		player.playAnimation(animation);
		World.getWorld().submit(new Tickable(4) {
			public void execute() {
				//player.getActionSender().sendProjectile(player.getLocation(), getThrowFinish(), 53, 60, 55, 60, 50, 50, -1, 0, 96);
				World.getWorld().submit(new Tickable(6) {
					public void execute() {
						player.getInventory().add(new Item(8851, distance));
						player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
							"Well done! You threw the shot " + distance + " yards!");
						player.getInterfaceState().setNextDialogueId(0, 683);
						distance = 0;
						stop();
					}
				});
				stop();
			}
		});
	}
	
	public int distance = 0;
	
	public Location getThrowFinish() {
		distance = Misc.random(1, 12);
		return Location.create((player.getLocation().getX() + distance), player.getLocation().getY(), 2);
	}
	
	public void rewardPlayer() {
		player.getInventory().add(new Item(8851, player.warriorTokens));
		player.warriorTokens = 0;
	}
	
	public boolean containsItem(Item item) {
		return player.getInventory().hasItem(item) || player.getEquipment().hasItem(item);
	}
	public boolean containsItem(int id) {
		Item item = Item.create(id);
		return containsItem(item);
	}
	
	public Item getDefender() {
		int id = 8850;
		if(containsItem(8849) || containsItem(8850)) {
			id = 8850;
		} else
		if(containsItem(8848)) {
			id = 8849;
		} else
		if(containsItem(8847)) {
			id = 8848;
		} else
		if(containsItem(8846)) {
			id = 8847;
		} else
		if(containsItem(8845)) {
			id = 8846;
		} else
		if(containsItem(8844)) {
			id = 8845;
		} else {
			id = 8844;
		}
		return Item.create(id);
	}
	
	public void leaveGame(boolean outOfTokens) {
		player.setTeleportTarget(Location.create(2846, 3540, 2));
		player.guildWaits = 0;
		if(outOfTokens) {
			player.getActionSender().sendMessage("You've ran out of tokens!");
		}
		World.getWorld().submit(new Tickable(1) {
			public void execute() {
				World.getWorld().warriorGuildPlayers.remove(player);
				stop();
			}
		});
	}
	
	public void enterCyclopes() {
		if(World.getWorld().warriorGuildPlayers.contains(player)) {
			//System.out.println("Already contained player: " + player.getName());
			leaveCyclopes();
			return;
		}
		if(player.getInventory().getCount(8851) < 100) {
			DialogueManager.openDialogue(player, 627);
			return;
		}
		World.getWorld().warriorGuildPlayers.add(player.getName().trim().toLowerCase());
		player.getInventory().remove(new Item(8851, 10));
		player.setTeleportTarget(Location.create(2847, 3541, 2));
		DialogueManager.openDialogue(player, 628);
	}
	
	public String getDefenderName() {
		return getDefender().getDefinition().getName().replace("defender","");
	}
	
	public void leaveCyclopes() {
		leaveGame(false);
		World.getWorld().warriorGuildPlayers.remove(player);
	}
	
	/**
	 * The player we're doing stuff for.
	 */
	private Player player;
	
	public WarriorsGuild(Player player) {
		this.player = player;
	}
	
	public static WarriorsGuild create(Player player) {
		return new WarriorsGuild(player);
	}
	
	public void armorOnPlatform() {
		final ArmorType armorType = getArmorType();
		if(armorType == null) {
			player.getActionSender().sendMessage("That's not armour!");
			return;
		}
		if(getAnimatedArmor(armorType) == null) {
			player.getActionSender().sendMessage("Please contact Canownueasy if this happened.");
			return;
		}
		Item[] armors = { armorType.helm, armorType.body, armorType.legs };
		for(Item items : armors) {
			if(player.getInventory().hasItem(items)) {
				player.getInventory().remove(items);
			} else {
				player.getActionSender().sendMessage("You don't have enough armour to place.");
				return;
			}
		}
		player.playAnimation(Animation.create(827));
		player.getActionSender().sendDialogue(player.getName(), DialogueType.MESSAGE_MODEL_LEFT, armorType.body.getId(), FacialAnimation.DEFAULT,
				"You place your armor on the platform where it disappears...",
				"");
		player.getInterfaceState().setNextDialogueId(0, 97);
		final Location oldLoc = player.getLocation();
		World.getWorld().submit(new Tickable(4) {
			public void execute() {
				final NPC n = getAnimatedArmor(armorType);
				World.getWorld().submit(new Tickable(3) {
					public void execute() {
						player.getActionSender().sendDialogue("", DialogueType.MESSAGE, -1, FacialAnimation.DEFAULT,
								"The animator hums, something appears to be working.",
								"You stand back.");
						player.getInterfaceState().setNextDialogueId(0, 97);
						player.getWalkingQueue().addStep(player.getLocation().getX(), (player.getLocation().getY() + 5));
						player.getWalkingQueue().finish();
						player.face(oldLoc);
						stop();
					}
				});
				World.getWorld().submit(new Tickable(6) {
					public void execute() {
						World.getWorld().register(n);
						player.getActionSender().sendHintArrow(n, 0, 1);
						n.forceChat("I'm ALIVE!");
						n.getUpdateFlags().flag(UpdateFlag.CHAT);
						n.playAnimation(Animation.create(4166));
						final Location faceLoc = Location.create(n.getLocation().getX(), (n.getLocation().getY() + 6));
						n.face(faceLoc);
						World.getWorld().submit(new Tickable(2) {
							public void execute() {
								n.getWalkingQueue().addStep(faceLoc.getX(), (faceLoc.getY() - 1));
								World.getWorld().submit(new Tickable(6) {
									public void execute() {
										n.getCombatState().startAttacking(player, false);
										stop();
									}
								});
								stop();
							}
						});
						stop();
					}
				});
				stop();
			}
		});
	}
	
	private NPC getAnimatedArmor(ArmorType armorType) {
		int npcId = 4284;
		switch(armorType) {
		case RUNE:
			npcId = 4284;
			break;
		case ADAMANT:
			npcId -= 1;
			break;
		case MITHRIL:
			npcId -= 2;
			break;
		case BLACK:
			npcId -= 3;
			break;
		case STEEL:
			npcId -= 4;
			break;
		case IRON:
			npcId -= 5;
			break;
		case BRONZE:
			npcId -= 6;
			break;
		}
		NPCDefinition def = new NPCDefinition(npcId, "", "", 1, 1, null);
		Location loc = Location.create(player.getLocation().getX(), (player.getLocation().getY() - 1), player.getLocation().getZ());
		NPC npc = new NPC(def, loc, loc, loc, 1);
		return npc;
	}
	
	private ArmorType getArmorType() {
		Item[] rune = { ArmorType.RUNE.helm, ArmorType.RUNE.body, ArmorType.RUNE.legs };
		for(Item items : rune) {
			if(player.getInventory().hasItem(items)) {
				return ArmorType.RUNE;
			}
		}
		Item[] adamant = { ArmorType.ADAMANT.helm, ArmorType.ADAMANT.body, ArmorType.ADAMANT.legs };
		for(Item items : adamant) {
			if(player.getInventory().hasItem(items)) {
				return ArmorType.ADAMANT;
			}
		}
		Item[] mithril = { ArmorType.MITHRIL.helm, ArmorType.MITHRIL.body, ArmorType.MITHRIL.legs };
		for(Item items : mithril) {
			if(player.getInventory().hasItem(items)) {
				return ArmorType.MITHRIL;
			}
		}
		Item[] black = { ArmorType.BLACK.helm, ArmorType.BLACK.body, ArmorType.BLACK.legs };
		for(Item items : black) {
			if(player.getInventory().hasItem(items)) {
				return ArmorType.BLACK;
			}
		}
		Item[] steel = { ArmorType.STEEL.helm, ArmorType.STEEL.body, ArmorType.STEEL.legs };
		for(Item items : steel) {
			if(player.getInventory().hasItem(items)) {
				return ArmorType.STEEL;
			}
		}
		Item[] iron = { ArmorType.IRON.helm, ArmorType.IRON.body, ArmorType.IRON.legs };
		for(Item items : iron) {
			if(player.getInventory().hasItem(items)) {
				return ArmorType.IRON;
			}
		}
		Item[] bronze = { ArmorType.BRONZE.helm, ArmorType.BRONZE.body, ArmorType.BRONZE.legs };
		for(Item items : bronze) {
			if(player.getInventory().hasItem(items)) {
				return ArmorType.BRONZE;
			}
		}
		return null;
	}

}
