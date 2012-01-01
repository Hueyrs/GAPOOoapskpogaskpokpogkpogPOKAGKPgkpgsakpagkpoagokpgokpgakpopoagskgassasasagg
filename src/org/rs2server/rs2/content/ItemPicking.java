package org.rs2server.rs2.content;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Player;

/**
 * For item picking like flax, grain, potatoes, ect.
 * @author Canownueasy
 *
 */
public class ItemPicking {
	
	public static void pickItem(final Player player, final Item item, final Animation animation, final String message) {
		if(!player.getInventory().hasRoomFor(item)) {
			player.getActionSender().sendMessage("You don't have enough inventory space.");
			return;
		}
		if(player.inEvent) {
			return;
		}
		player.inEvent = true;
		player.playAnimation(animation);
		player.getInventory().add(item);
		player.getActionSender().sendMessage(message);
	}

}
