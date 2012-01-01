package org.rs2server.rs2.content;

import java.util.HashMap;
import java.util.Map;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;

/**
 * 
 * @author Jesse
 * Still need to finish resetwalking
 */
public class Levers {
	
	private static Map<Location, Lever> LEVERS = new HashMap<Location, Lever>();
	private static final Animation LEVER_ANIMATION = Animation.create(2140);
	

	public static boolean handle(final Player player, final Location loc, final int objectId) {
		final Lever lever = LEVERS.get(loc);
		if(lever == null || objectId == 5961 /*You've already clicked the lever.*/) {
			return false;
		}
		if(objectId == 5959 && player.getLocation().getX() != 3090) {
			return false;
		}
		/*
		 * Prevents mass clicking them.
		 */
		if(player.getCombatState().isTeleblocked()) {
        	player.getActionSender().sendMessage("A magical force stops you from teleporting.");
        	return true;
        }
    	if(player.getMob().canTeleport()) {
    		return true;
    	}
		player.playAnimation(LEVER_ANIMATION);
		player.getActionSender().sendMessage("You pull the lever...");
		player.getActionSender().sendCreateObject(4, 5961, lever.getDirection1(), loc);
		//player.setCanWalk(false);
		World.getWorld().submit(new Event(1500) {

			@Override
			public void execute() {
				player.getActionSender().sendCreateObject(4, objectId, lever.getDirection2(), loc);
				player.playAnimation(Animation.create(714));
				player.playGraphics(Graphic.create(301, (100 << 16)));
				World.getWorld().submit(new Event(1800) {

					@Override
					public void execute() {
						player.setTeleportTarget(lever.getTargetLocation());
						player.playAnimation(Animation.create(-1));
						if(objectId == 5960) {
							player.getActionSender().sendMessage("...and teleport out of the mage's cave.");
						}
						if(objectId == 5959) {
							player.getActionSender().sendMessage("...and teleport into the mage's cave.");
						}
						//player.setCanWalk(true);
						this.stop();
					}
					
				});
				this.stop();
			}
			
		});		
		return true;
	
	}
	private static class Lever {
		
		private final Location targetLocation;
		private final int direction1;
		private final int direction2;
		
		public Location getTargetLocation() {
			return targetLocation;
		}

		public int getDirection1() {
			return direction1;
		}
		
		public int getDirection2() {
			return direction2;
		}
		
		public Lever(Location target, int direction1, int direction2) {
			this.targetLocation = target;
			this.direction1 = direction1;
			this.direction2 = direction2;
		}
	}
	
	/**
	 * This populates the map.
	 */
	static {
		/*
		 * King Black Dragon levers.
		 */
		LEVERS.put(Location.create(3067,10253,0), new Lever(Location.create(2271, 4680, 0), 3, 3));
		LEVERS.put(Location.create(2271, 4680, 0), new Lever(Location.create(3067, 10253, 0), 3, 3));
		
		/*
		 * Ardy Lever to edge
		 */
		/*
		 * Mb inside to outside
		 */
		LEVERS.put(Location.create(3090, 3956, 0), new Lever(Location.create(2539, 4712, 0), 0, 0));
		LEVERS.put(Location.create(2539,4712,0), new Lever(Location.create(3090, 3956, 0), 3, 3));

		//magebank back to edgeville
		//player.getActionAssistant().pullLever(player, x, y, 5961, 0, 4, 3079, 3489, 0);
	}

}
