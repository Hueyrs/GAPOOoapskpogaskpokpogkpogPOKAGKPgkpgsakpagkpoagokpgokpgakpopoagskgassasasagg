package org.rs2server.rs2.model.combat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rs2server.rs2.model.Mob;


/**
 * Maintains a count of the total damage and last hit time of attackers for the
 * current entity.
 * @author Graham Edgecombe
 *
 */
public final class DamageMap {
	
	/**
	 * The time after which the total hit expires (10 minutes).
	 */
	private static final long HIT_EXPIRE_TIME = 600000;
	
	/**
	 * The total damage map.
	 */
	private Map<Mob, TotalDamage> totalDamages = new HashMap<Mob, TotalDamage>();
	
	/**
	 * @return the totalDamages
	 */
	public Map<Mob, TotalDamage> getTotalDamages() {
		return totalDamages;
	}

	/**
	 * Resets the map (e.g. if the entity dies).
	 */
	public void reset() {
		totalDamages.clear();
	}
	
	/**
	 * Gets the highest damage dealer to this entity.
	 * @return The highest damage dealer to this entity.
	 */
	public Mob highestDamage() {
		int highestDealt = 0;
		Mob killer = null;
		for(Mob mob : totalDamages.keySet()) {
			if(totalDamages.get(mob).getDamage() > highestDealt) {
				highestDealt = totalDamages.get(mob).getDamage();
				killer = mob;
			}
		}
		return killer;
	}
	
	/**
	 * Removes entities that have been destroyed from the map, or entries where
	 * the player has not hit for 10 minutes.
	 */
	public void removeInvalidEntries() {
		// copy the map OR face concurrent modification exceptions ;)
		Set<Mob> mobs = new HashSet<Mob>();
		mobs.addAll(totalDamages.keySet());
		for(Mob e : mobs) {
			if(e.isDestroyed()) {
				totalDamages.remove(e);
			} else {
				TotalDamage d = totalDamages.get(e);
				long delta = System.currentTimeMillis() - d.getLastHitTime();
				if(delta >= HIT_EXPIRE_TIME) {
					totalDamages.remove(e);
				}
			}
		}
	}
	
	/**
	 * Increments the total damage dealt by an attacker.
	 * @param attacker
	 * @param amount
	 */
	public void incrementTotalDamage(Mob attacker, int amount) {
		TotalDamage totalDamage = totalDamages.get(attacker);
		if(totalDamage == null) {
			totalDamage = new TotalDamage();
			totalDamages.put(attacker, totalDamage);
		}
		totalDamage.increment(amount);
	}
	
}
