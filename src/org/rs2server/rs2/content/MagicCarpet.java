package org.rs2server.rs2.content;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;

/**
 * For magic carpet st00f.
 * @author Canownueasy
 *
 */
public class MagicCarpet {
	
	private static Animation TAKE_OFF = Animation.create(2262);
	
	private static Animation LANDING = Animation.create(2263);
	
	private static Animation FLYING = Animation.create(2261);

	/**
	 * Flys a player to a location
	 * @param location The location to fly to.
	 */
	public static void flyTo(final Player player, final Location location) {
		player.playAnimation(TAKE_OFF);
		World.getWorld().submit(new Event(2000) {
			public void execute() {
				player.playAnimation(FLYING);
				setAllAnimations(player, FLYING);
				player.getWalkingQueue().addStep(location.getX(), location.getY());
				World.getWorld().submit(new Event(1000) {
					public void execute() {
						if(player.getLocation() == location) {
							player.playAnimation(LANDING);
							World.getWorld().submit(new Event(1000) {
								public void execute() {
									setAllAnimations(player, Animation.create(999));
									stop();
								}
							});
							stop();
							return;
						}
					}
				});
				stop();
			}
		});
	}
	
	public static void setAllAnimations(Player player, Animation animation) {
		player.setWalkAnimation(animation);
		player.setRunAnimation(animation);
		player.setStandAnimation(animation);
		player.setStandTurnAnimation(animation);
		player.setTurn180Animation(animation);
		player.setTurn90ClockwiseAnimation(animation);
		player.setTurn90CounterClockwiseAnimation(animation);
	}
}
