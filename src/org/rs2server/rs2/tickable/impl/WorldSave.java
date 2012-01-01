package org.rs2server.rs2.tickable.impl;

import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.tickable.*;

public class WorldSave extends Tickable {

	public WorldSave() {
		super(500);
	}
	
	public void execute() {
		for(final Player p : World.getWorld().getPlayers()) {
			if(p != null && p.finishedTutorialIsland) {
				World.getWorld().save(p);
			}
		}
	}

}
