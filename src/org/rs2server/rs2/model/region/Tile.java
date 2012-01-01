package org.rs2server.rs2.model.region;

import java.util.ArrayList;
import java.util.List;

import org.rs2server.rs2.model.GroundItem;


public class Tile {

	/**
	 * A list of ground items on this tile.
	 */
	private List<GroundItem> groundItems = new ArrayList<GroundItem>();

	/**
	 * Gets the ground items on this tile.
	 * @return the groundItems
	 */
	public List<GroundItem> getGroundItems() {
		return groundItems;
	}
}
