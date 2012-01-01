package org.rs2server.rs2.event.impl;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;

public class SecondTick extends Event {

	public SecondTick() {
		super(1000);
	}
	
	public void execute() {
		for(Player p : World.getWorld().getPlayers()) {
			if(p != null) {
				timers(p);
			}
		}
	}
		
	public void timers(final Player p) {
		if(p.barrelWait > 0) {
			p.barrelWait--;
		}
		if(p.dfsWait > 0) {
			p.dfsWait--;
		}
		if(p.borkWait > 0) {
			p.borkWait--;
		}
	}
}
