package org.rs2server.rs2.tickable.impl;

import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.tickable.Tickable;

/**
 * To heal health of players about every 6 seconds.
 * @author Canownueasy
 *
 */
public class HealthRestoreTick extends Tickable {

	public HealthRestoreTick() {
		super(100);
	}
	
	public void execute() {
		for(Player p : World.getWorld().getPlayers()) {
			if(p != null) {
				restore(p);
			}
		}
	}
	
	private void restore(final Player player) {
		if(player.getSkills().getLevel(Skills.HITPOINTS) >= player.getSkills().getLevelForExperience(Skills.HITPOINTS)) {
			return;
		}
		int restoreRate = 1;
		if(player.getCombatState().getPrayer(Prayers.RAPID_HEAL)) {
			restoreRate++;
		}
		player.getSkills().increaseLevel(Skills.HITPOINTS, restoreRate);
	}
}
