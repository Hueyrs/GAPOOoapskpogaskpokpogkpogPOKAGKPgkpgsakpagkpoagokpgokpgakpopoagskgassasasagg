package org.rs2server.rs2.model;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.rs2server.util.XMLController;


public class NPCSpawn {

	
	/**
	 * The logger instance.
	 */
	private static final Logger logger = Logger.getLogger(NPCSpawn.class.getName());
	
	/**
	 * The ID of the NPC this spawn represents.
	 */
	private int npcId;
	
	/**
	 * The spawn location.
	 */
	private Location spawnLocation;
	
	/**
	 * The minimum location of this NPCSpawn.
	 */
	private Location minLocation;
	
	/**
	 * The maximum location of this NPCSpawn.
	 */
	private Location maxLocation;
	
	/**
	 * The direction the spawned npc should face.
	 */
	private int direction;
	
	/**
	 * Loads the NPC spawn list from xml, and spawns all the NPC's.
	 * @throws IOException 
	 */
	public static void init() throws IOException {
		logger.info("Loading NPC spawns...");
		File file = new File("data/npcSpawns.xml");
		if(file.exists()) {
			List<NPCSpawn> spawns = XMLController.readXML(file);
			for(NPCSpawn spawn : spawns) {
				NPCDefinition def = NPCDefinition.forId(spawn.getId());
				if(def != null) {
					NPC npc = new NPC(def, spawn.getLocation(), spawn.getMinimumLocation(), spawn.getMaximumLocation(), spawn.getDirection());
					World.getWorld().register(npc);
				} else {
//					logger.info("Missing NPC Definition " + spawn.getId() + ".");
				}
			}
			logger.info("Loaded " + spawns.size() + " NPC spawns.");
		} else {
			logger.info("NPC spawns not found.");
		}
	}

	/**
	 * Gets the location associated with this NPCSpawn.
	 * @return The spawn location.
	 */
	private Location getLocation() {
		return spawnLocation;
	}

	/**
	 * Gets the id of this NPCSpawn.
	 * @return The NPC id.
	 */
	private int getId() {
		return npcId;
	}

	/**
	 * Gets the lowest location the NPC can walk into.
	 * @return The minLocation.
	 */
	public Location getMinimumLocation() {
		return minLocation;
	}

	/**
	 * Gets the highest location the NPC can walk into.
	 * @return The maxLocation.
	 */
	public Location getMaximumLocation() {
		return maxLocation;
	}

	/**
	 * @return The direction.
	 */
	public int getDirection() {
		return direction;
	}
}
