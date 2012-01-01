package org.rs2server.rs2.event.impl;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.minigame.impl.CastleWars;

/**
 * Event for castle wars ^.^
 * @author Canownueasy
 *
 */
public class CastleWarsEvent extends Event {
	
	public CastleWarsEvent() {
		super(1000);
	}
	
	@Override
	public void execute() {
		CastleWars.access().tick();
	}

}
