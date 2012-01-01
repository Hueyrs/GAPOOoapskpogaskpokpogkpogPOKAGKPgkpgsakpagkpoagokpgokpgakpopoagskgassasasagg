package org.rs2server.rs2.content;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

public class SOSDoors {
	
	private Player player;
	private Location loc;
	
	private SOSDoors(Player player, Location loc) {
		this.player = player;
		this.loc = loc;
	}
	
	public static SOSDoors create(Player player, Location loc) {
		return new SOSDoors(player, loc);
	}
	
	public void door() {
		final int px = player.getLocation().getX(), py = player.getLocation().getY();
		final int dx = loc.getX(), dy = loc.getY();
		int x = 0, y = 0;
		if(py > dy) {
			y = -1;
		} else
		if(py < dy) {
			y = 1;
		} else
		if(px > dx) {
			x = -1;
		} else
		if(px < dx) {
			x = 1;
		} else
		if(px == dx) {
			x = Misc.random(1) == 1 ? 1 : -1;
		} else
		if(py == dy) {
			y = Misc.random(1) == 1 ? 1 : -1;
		}
		final Location newLocation = Location.create((px + x), (py + y));
		System.out.println(newLocation);
		player.playAnimation(Animation.create(4282));
		World.getWorld().submit(new Tickable(1) {
			public void execute() {
				player.setTeleportTarget(newLocation);
				player.playAnimation(Animation.create(4283));
				this.stop();
			}
		});
	}

}
