package org.rs2server.rs2.util;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Player;

public class LocationChecker {
	
	public static void refreshDog(NPC dog) {
		dog.getWalkingQueue().reset();
	}

	/**
	 * Checks the location a npc.
	 * @param npc The NPC.
	 * @param player The player shooing.
	 * @param squares The amount of squares the NPC will travel.
	 * @return The new location the npc will travel to.
	 */
	public static Location npcLocation(NPC npc, Player player, int squares) {
		//West
		if(player.getLocation().getX() > npc.getLocation().getX()) {
			if(Constants.DEBUGGING) {
				System.out.println("West");
			}
			return Location.create((npc.getLocation().getX() - squares), npc.getLocation().getY(), npc.getHeight());
		}
		//East
		if(player.getLocation().getX() < npc.getLocation().getX()) {
			if(Constants.DEBUGGING) {
				System.out.println("East");
			}
			return Location.create((npc.getLocation().getX() + squares), npc.getLocation().getY(), npc.getHeight());
		}
		//South
		if(player.getLocation().getY() > npc.getLocation().getY()) {
			if(Constants.DEBUGGING) {
				System.out.println("South");
			}
			return Location.create(npc.getLocation().getX(), (npc.getLocation().getY() - squares), npc.getHeight());
		}
		//North
		if(player.getLocation().getX() < npc.getLocation().getY()) {
			if(Constants.DEBUGGING) {
				System.out.println("North");
			}
			return Location.create(npc.getLocation().getX(), (npc.getLocation().getY() + squares), npc.getHeight());
		}
		//Otherwise return null and throw an exception.
		System.out.println("Dog location could not be found.");
		return null;
	}
}
