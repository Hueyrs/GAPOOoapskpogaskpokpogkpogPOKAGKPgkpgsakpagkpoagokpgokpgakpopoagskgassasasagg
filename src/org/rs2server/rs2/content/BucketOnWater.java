package org.rs2server.rs2.content;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.tickable.Tickable;

/**
 * When a person uses bucket on water.
 * @author Canownueasy
 *
 */
public class BucketOnWater {
	
	public static void go(final Player player) {
		World.getWorld().submit(new Tickable(1) {
			public void execute() {
				if(!player.getInventory().hasItem(Item.create(1925))) {
					player.getActionSender().sendMessage("You have no more empty buckets left.");
					player.playAnimation(Animation.create(-1));
					stop();
					return;
				}
				player.playAnimation(Animation.create(832));
				player.getInventory().remove(Item.create(1925));
				player.getInventory().add(Item.create(1929));
			}
		});
	}

}
