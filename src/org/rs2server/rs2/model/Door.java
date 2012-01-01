package org.rs2server.rs2.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.rs2server.rs2.model.region.Region;
import org.rs2server.util.XMLController;

public class Door {
	
	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(Door.class.getName());
	
	/**
	 * The open game object.
	 */
	private GameObject object;
	
	/**
	 * The closed game object.
	 */
	private GameObject replacement;
	
	/**
	 * The second id.
	 */
	private int secondId;
	
	/**
	 * The location of the second door, if this is a double door.
	 */
	private Location secondLocation;
	
	/**
	 * The open flag.
	 */
	private boolean replaced = false;
	
	private GameObject removedGameObject;

	public void open(boolean openSecond) {
		if(!isReplaced()) {
			boolean locationsEqual = getObject().getLocation().equals(getReplacement().getLocation());
			World.getWorld().unregister(getObject(), false);
			World.getWorld().register(getReplacement());
			if(!locationsEqual) {
				removedGameObject = new GameObject(getObject().getLocation(), -1, getObject().getType(), getObject().getDirection() + 1, false);
				World.getWorld().register(removedGameObject);
			}
			replaced = true;
		} else {
			if(removedGameObject != null) {
				World.getWorld().unregister(removedGameObject, false);
				removedGameObject = null;
			}
			boolean locationsEqual = getObject().getLocation().equals(getReplacement().getLocation());
			World.getWorld().unregister(getReplacement(), !locationsEqual);
			World.getWorld().register(getObject());
			replaced = false;
		}
		if(openSecond && secondLocation != null) {
			Door secondDoor = replacement.getRegion().doorForLocation(secondLocation, secondId);
			secondDoor.open(false);
		}
	}

	/**
	 * Loads the door definitions.
	 * @throws IOException if an I/O error occurs.
	 */
	public static void init() {
		logger.info("Loading doors...");
		try {
			/**
			 * Load doors.
			 */
			File file = new File("data/doors.xml");
			List<Door> doors = new ArrayList<Door>();
//			doors.add(new Door(new GameObject(Location.create(2949, 3379, 0), 11707, 0, 2, true), new GameObject(Location.create(2949, 3379, 0), 11707, 0, 3, false), null));
//			XMLController.writeXML(doors, file);
			if(file.exists()) {
				doors = XMLController.readXML(file);
				for(Door door : doors) {
					door.getObject().setRegion(World.getWorld().getRegionManager().getRegionByLocation(door.getObject().getLocation()));
//					door.getReplacement().setRegion(World.getWorld().getRegionManager().getRegionByLocation(door.getReplacement().getLocation()));
					Region r = World.getWorld().getRegionManager().getRegionByLocation(door.getObject().getLocation());
					r.addDoor(door);
				}
				logger.info("Loaded " + doors.size() + " doors.");
			} else {
				logger.info("Doors not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GameObject getObject() {
		return object;
	}

	public GameObject getReplacement() {
		return replacement;
	}

	public void setObject(GameObject object) {
		this.object = object;
	}

	public void setReplacement(GameObject replacement) {
		this.replacement = replacement;
	}

	public boolean isReplaced() {
		return replaced;
	}

	public boolean isDoubleDoor() {
		return secondLocation != null && secondLocation != object.getLocation() && secondLocation != replacement.getLocation();
	}
}