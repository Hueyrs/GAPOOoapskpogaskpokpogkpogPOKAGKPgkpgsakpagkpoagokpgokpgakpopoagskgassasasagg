package org.rs2server.rs2.content;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.minigame.impl.Barrows;
import org.rs2server.rs2.tickable.Tickable;

/**
 * For digging with spades.
 * @author Canownueasy
 *
 */
public class SpadeDig {

	public static void dig(final Player player) {
		player.playAnimation(Animation.create(831));
		player.getActionSender().sendMessage("You dig into the ground...");
		World.getWorld().submit(new Tickable(1) {
			public void execute() {
				digEffects(player);
				stop();
			}
		});
	}
	
	public static void digEffects(final Player player) {
		player.playAnimation(Animation.create(-1));
		/*
		 * Mole holes.
		 */
		if(player.getLocation().equals(Location.create(2996, 3377)) || 
				player.getLocation().equals(Location.create(2999, 3375))) {
			player.setTeleportTarget(Location.create(1752, 5237));
			player.getActionSender().sendMessage("You drop into deep underground!");
			return;
		}
		Barrows barrows = new Barrows(player);
		if(barrows.handleSpade()) {
			return;
		}
		player.getActionSender().sendMessage("And nothing interesting happens.");
		//Heres where digging checks and stuff would go.
	}
}
