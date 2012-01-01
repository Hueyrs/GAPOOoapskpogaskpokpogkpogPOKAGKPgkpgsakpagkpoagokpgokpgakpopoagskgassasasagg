package org.rs2server.rs2.model;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.rs2server.rs2.tickable.impl.ItemSpawnTick;
import org.rs2server.util.XMLController;


public class ItemSpawn {

	
	/**
	 * The logger instance.
	 */
	private static final Logger logger = Logger.getLogger(ItemSpawn.class.getName());
	
	/**
	 * A list of item spawns.
	 */
	private static List<ItemSpawn> spawns;
	
	public static List<ItemSpawn> getSpawns() {
		return spawns;
	}
	
	/**
	 * The item.
	 */
	private Item item;
	
	/**
	 * The location of the item.
	 */
	private Location location;
	
	/**
	 * The ground item, set after the item is registered.
	 */
	private GroundItem groundItem;
	
	private ItemSpawn(Item item, Location location) {
		this.item = item;
		this.location = location;
	}
	
	/**
	 * Loads the item spawn list from xml, and spawns all the item's.
	 * @throws IOException 
	 */
	public static void init() throws IOException {
		logger.info("Loading item spawns...");
		File file = new File("data/itemSpawns.xml");
//		spawns = new ArrayList<ItemSpawn>();
//		spawns.add(new ItemSpawn(new Item(223, 1), Location.create(2832, 9585, 0)));
//		XMLController.writeXML(spawns, file);
		if(file.exists()) {
			spawns = XMLController.readXML(file);
			for(ItemSpawn spawn : spawns) {
				GroundItem groundItem = new GroundItem("", spawn.getItem(), spawn.getLocation());
				World.getWorld().register(groundItem, null);
				spawn.setGroundItem(groundItem);
			}
			World.getWorld().submit(new ItemSpawnTick());
			logger.info("Loaded " + spawns.size() + " item spawns.");
		} else {
			logger.info("Item spawns not found.");
		}
	}

	/**
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @return the groundItem
	 */
	public GroundItem getGroundItem() {
		return groundItem;
	}

	/**
	 * @param groundItem the groundItem to set
	 */
	public void setGroundItem(GroundItem groundItem) {
		this.groundItem = groundItem;
	}
		
}
