package org.rs2server.rs2.content;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.net.ActionSender.DialogueType;

/**
 * For the canoeing system.
 * @author Canownueasy
 */

public class Canoes {
	
	public static void sail(final Player player, final NPC npc, final Location location, final String locationName) {
		player.setTeleportTarget(location);
		player.getActionSender().sendDialogue("Sailing", DialogueType.MESSAGE_MODEL_LEFT, 795, FacialAnimation.DEFAULT,
					npc.getDefinition().getName() + " sails you to " + locationName + ".");
		player.getInterfaceState().setNextDialogueId(0, 97);
	}

}
