package org.rs2server.rs2.content;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.tickable.Tickable;

/**
 * For the teletabs.
 * @author Canownueasy
 *
 */
public class Teletabs {

	public static Animation TELE = Animation.create(4071);
	public static Graphic TELE_GFX = Graphic.create(678);
	
	public static void teleport(final Player player, final Location location) {
		player.playAnimation(TELE);
		player.playGraphics(TELE_GFX);
		World.getWorld().submit(new Tickable(3) {
			public void execute() {
				player.setTeleportTarget(location);
				player.playAnimation(Animation.create(-1));
				stop();
			}
		});
	}
}
