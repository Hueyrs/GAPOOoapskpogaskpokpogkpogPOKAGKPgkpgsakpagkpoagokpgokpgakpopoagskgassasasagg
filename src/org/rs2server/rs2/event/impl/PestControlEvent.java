package org.rs2server.rs2.event.impl;

import org.rs2server.rs2.event.Event;

/**
 * Pest control related timings.
 * @author Canownueasy
 * @author Haxifix
 */

public class PestControlEvent extends Event {

	public PestControlEvent() {
		super(1000); //1 second is a nice steady rate
	}

	@Override
	public void execute() {
		
	}
	
}
