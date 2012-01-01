package org.rs2server.rs2.tickable.impl;

import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.tickable.Tickable;

public class EnergyRestoreTick extends Tickable {

	/**
	 * The mob whos energy we are restoring.
	 */
	private Mob mob;
	
	public EnergyRestoreTick(Mob mob) {
		super(4);
		this.mob = mob;
	}

	@Override
	public void execute() {
		if(mob.getWalkingQueue().getEnergy() < 100) {
			mob.getWalkingQueue().setEnergy(mob.getWalkingQueue().getEnergy() + 1);
			if(mob.getActionSender() != null) {
				mob.getActionSender().sendRunEnergy();
			}
		} else {
			mob.getEnergyRestoreTick().stop();
			mob.setEnergyRestoreTick(null);
		}
	}

}
