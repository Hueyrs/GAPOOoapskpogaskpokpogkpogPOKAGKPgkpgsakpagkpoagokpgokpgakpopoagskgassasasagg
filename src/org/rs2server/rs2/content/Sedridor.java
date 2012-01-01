package org.rs2server.rs2.content;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.tickable.Tickable;

/**
 * For the Seridor teleportation system.
 * @author Canownueasy
 */

public class Sedridor {
	
	public static void teleport(final Player player, final NPC seridor, final Location location, final String placeName) {
		player.getActionSender().removeChatboxInterface();
		seridor.face(player.getLocation());
		seridor.forceChat("Saradomin, guide " + player.getName() + " to " + placeName + "!");
		seridor.playAnimation(Animation.create(1818));
		player.playAnimation(Animation.create(1816));
		player.playGraphics(Graphic.create(342));
		World.getWorld().submit(new Tickable(3) {
			@Override
			public void execute() {
				player.setTeleportTarget(location);
				player.playAnimation(Animation.create(715));
				stop();
			}
		});
	}

}
