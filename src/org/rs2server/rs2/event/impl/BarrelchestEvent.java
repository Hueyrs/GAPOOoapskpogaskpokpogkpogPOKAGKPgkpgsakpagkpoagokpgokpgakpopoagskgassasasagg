package org.rs2server.rs2.event.impl;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.World;

/**
 * Event for ticking Barrelchest game
 * @author Canownueasy
 *
 */
public class BarrelchestEvent extends Event {
	
	public BarrelchestEvent() {
		super(1000);
	}
	
	@Override
	public void execute() {
		World.getWorld().getBarrelchest().update();
	}

}