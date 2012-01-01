package org.rs2server.rs2.content;

import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.skills.Agility;

/**
 * For shantay pass thing.
 * @author Canownueasy
 *
 */
public class ShantayPass {

	public static void enterPass(Player player) {
		boolean out = true;
		if(player.getLocation().getY() >= 3117) {
			out = false;
		}
		if(!out) {
			if(player.getInventory().getCount(1854) > 0) {
				int y = 3;
				int dir = 0;
				if(player.getLocation().getY() >= 3117) {
					y = -3;
					dir = 2;
				}
				int[] forceMovementVars =  { 0, 0, 0, y, 10, 90, dir, 2 };
				if(player.getWalkingQueue().isRunning()) {
					Agility.forceMovement(player, player.getRunAnimation(), forceMovementVars, 1, true);
				} else {
					Agility.forceMovement(player, player.getWalkAnimation(), forceMovementVars, 1, true);
				}
			} else {
				DialogueManager.openDialogue(player, 504);
			}
			return;
		}
		player.getWalkingQueue().addStep(3304, 3117);
	}
}
