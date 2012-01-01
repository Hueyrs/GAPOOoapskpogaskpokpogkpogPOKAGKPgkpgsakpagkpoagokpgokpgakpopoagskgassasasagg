package org.rs2server.rs2.model.skills;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.NPCDefinition;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.net.ActionSender.DialogueType;
import org.rs2server.util.Misc;

/**
 * Created by Jesse.
 */
public class SlayerTasks {

	/**
	 * The player.
	 */
	public Player player;

	/**
	 * Class constructor.
	 * 
	 * @param player
	 *            .
	 */
	public SlayerTasks(Player player) {
		this.player = player;
	}

	/**
	 * Slayer levels to attack npc.
	 */
	public static final int[][] SLAYER_LEVELS_TO_ATTACK_NPCS = {{1615, 85}, {1637, 52}, {1624, 65}};

	/**
	 * Low slayer tasks variables.
	 * 
	 * (npc id, amount, xp)
	 */
										//zombie		men			cow				goblin			skeleton	hill giant		 ghost
	private static final int[][] LOW = {{74, 34, 140}, {1, 13, 66}, {81, 18, 79}, {101, 38, 85}, {90, 46, 110}, {117, 43, 169}, {103, 25, 74}};
	private static final int LEVEL_FOR_LOW_TASK = 1;// u'll get these tasks when
													// starting slayer.

	/**
	 * Medium slayer tasks variables.
	 */
	private static final int[][] MEDIUM = {{9, 27, 89}, {941, 64, 120}, {82, 54, 140},{112, 36, 130},{6006, 49, 117},{4381, 30, 140}};
	private static final int LEVEL_FOR_MEDIUM_TASK = 40;

	/**
	 * High slayer tasks variables.
	 */
	private static final int[][] HIGH = {};
	private static final int LEVEL_FOR_HIGH_TASK = 70;

	/**
	 * Slayer message used for vannake npc.
	 */
	private String message = "";

	public void getNPCTask() {
		if (player.getCurrentTaskId() == -1 || player.getCurrentTaskId() == 0 || player.getCurrentTaskLeftToKill() == -1 || player.getCurrentTaskLeftToKill() == 0) {
			calculateTask();
			NPCDefinition npcDef = NPCDefinition.forId(player.getCurrentTaskId());
			setSlayerMessage("You need to slay " + npcDef.getName() + ", amount " + player.getCurrentTaskLeftToKill());
		} else {
			NPCDefinition npcDef = NPCDefinition.forId(player.getCurrentTaskId());
			setSlayerMessage("You are still hunting " + npcDef.getName() + ", amount " + player.getCurrentTaskLeftToKill());
		}
	}

	public void setSlayerMessage(String message) {
		this.message = message;
	}

	public String getSlayerMessage() {
		return message;
	}

	public void resetTask(NPC npc) {
		if (player.getInventory().getCount(995) >= 50000) {
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT, "There you go.");
			player.getInventory().remove(new Item(995, 50000));
			player.setCurrentTaskId(-1);
			player.setCurrentTaskLeftToKill(-1);
		} else {
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT, "You don't have 50,000 coins.");
		}
	}

	public void updateTask(NPC npc) {
		if (npc.getDefinition().getId() == player.getCurrentTaskId()) {
			if (player.getCurrentTaskLeftToKill() > 0) {
				player.setCurrentTaskLeftToKill(player.getCurrentTaskLeftToKill() - 1);
				if (player.getSkills().getLevel(18) < LEVEL_FOR_LOW_TASK) {
					for (int[] element : LOW) {
						if (npc.getDefinition().getId() == element[0]) {
							player.getSkills().addExperience(18, element[2]);
						}
					}
				} else if (player.getSkills().getLevel(18) < LEVEL_FOR_HIGH_TASK) {
					for (int[] element : MEDIUM) {
						if (npc.getDefinition().getId() == element[0]) {
							player.getSkills().addExperience(18, element[2]);
						}
					}
				} else if (player.getSkills().getLevel(18) >= LEVEL_FOR_HIGH_TASK) {
					for (int[] element : HIGH) {
						if (npc.getDefinition().getId() == element[0]) {
							player.getSkills().addExperience(18, element[2]);
						}
					}
				}
				if (player.getCurrentTaskLeftToKill() == 0) {
					player.setCurrentTaskId(-1);
					player.setCurrentTaskLeftToKill(-1);
					player.getActionSender().sendMessage("You've finished your task, talk to vannaka to get other one.");
					return;
				}
			}
		}
	}

	public void calculateTask() {
		if (player.getSkills().getLevel(18) < LEVEL_FOR_MEDIUM_TASK) {
			for (int i = 0; i < LOW.length; i++) {
				player.setCurrentTaskId(LOW[Misc.random(i)][0]);
				player.setCurrentTaskLeftToKill(Misc.randomSlayer(LOW[i][1]));
			}
		} else if (player.getSkills().getLevel(18) < LEVEL_FOR_HIGH_TASK) {
			for (int i = 0; i < MEDIUM.length; i++) {
				player.setCurrentTaskId(MEDIUM[Misc.random(i)][0]);
				player.setCurrentTaskLeftToKill(Misc.randomSlayer(MEDIUM[i][1]));
			}
		} else if (player.getSkills().getLevel(18) >= LEVEL_FOR_HIGH_TASK) {
			for (int i = 0; i < HIGH.length; i++) {
				player.setCurrentTaskId(HIGH[Misc.random(i)][0]);
				player.setCurrentTaskLeftToKill(Misc.randomSlayer(HIGH[i][1]));
			}
		}
	}

}
