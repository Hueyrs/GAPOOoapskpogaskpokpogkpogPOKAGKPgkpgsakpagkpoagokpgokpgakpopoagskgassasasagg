package org.rs2server.rs2.tickable.impl;

import org.rs2server.rs2.model.GroundItem;
import org.rs2server.rs2.model.ItemSpawn;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.tickable.Tickable;

public class ItemSpawnTick extends Tickable {
	
	/**
	 * Creates the tickable to run every 60 seconds.
	 */
	public ItemSpawnTick() {
		super(100);
	}

	@Override
	public void execute() {
		for(ItemSpawn itemSpawn : ItemSpawn.getSpawns()) {
			Region r = itemSpawn.getGroundItem().getRegion();
			if(!r.getTile(itemSpawn.getLocation()).getGroundItems().contains(itemSpawn.getGroundItem())) {
				GroundItem newItem = new GroundItem("", itemSpawn.getItem(), itemSpawn.getLocation());
				World.getWorld().register(newItem, null);
				itemSpawn.setGroundItem(newItem);
			}
		}
	}

}
