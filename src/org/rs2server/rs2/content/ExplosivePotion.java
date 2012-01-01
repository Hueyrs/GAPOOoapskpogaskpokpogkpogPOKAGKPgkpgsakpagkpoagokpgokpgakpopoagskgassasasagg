package org.rs2server.rs2.content;

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.tickable.Tickable;

/**
 * For explosive potions.
 * @author Canownueasy
 *
 */
public class ExplosivePotion {
	
	public static void execute(final Player player, final int slot) {
		player.getInventory().remove(new Item(4045), slot);
		player.playAnimation(Animation.create(7270));
		player.getActionSender().sendMessage("You drop the potion...");
		World.getWorld().submit(new Tickable(2) {
			public void execute() {
				player.forceChat("Ouch! That hurt.");
				player.getActionSender().sendMessage("It explodes and you are wounded!");
				player.inflictDamage(new Hit(15), null);
				player.playGraphics(Graphic.create(571));
				this.stop();
			}
		});
	}

}
