package org.rs2server.rs2.content;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;

/**
 * For cow milking...
 * @author Canownueasy
 *
 */
public class MilkCow {
	
	public static Animation MILKING = Animation.create(2305);
	
	public static void milkCow(final Player player, final GameObject object) {
		player.getWalkingQueue().reset();
		if(!player.getInventory().hasItem(new Item(1925))) {
			player.playAnimation(Animation.create(-1));
			player.getActionSender().sendMessage("You have no buckets to fill.");
			player.getWalkingQueue().reset();
			player.inEvent = false;
			return;
		}
		if(player.inEvent == true) {
			//player.getActionSender().sendMessage("COWSS");
			return;
		}
		player.inEvent = true;
		player.face(object.getLocation());
		player.playAnimation(MILKING);
		World.getWorld().submit(new Event(2000) {
			public void execute() {
				if(!player.getInventory().hasItem(new Item(1925))) {
					player.getActionSender().sendMessage("You have no more buckets to fill.");
					player.inEvent = false;
					stop();
					return;
				}
				player.playAnimation(MILKING);
				player.getInventory().remove(new Item(1925));
				player.getInventory().add(new Item(1927));
				player.inEvent = true;
			}
		});
	}

}
