package org.rs2server.rs2.content;

import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

/**
 * Used for mob to mob following
 * @author Canownueasy
 *
 */
public class Following {
	
	private Mob mob, victim;
	
	private Following(Mob mob, Mob victim) {
		this.mob = mob;
		this.victim = victim;
	}
	
	public static Following create(Mob mob, Mob victim) {
		if(mob == null || victim == null) {
			return null;
		}
		return new Following(mob, victim);
	}
	
	public void follow() {
		if(mob == null || victim == null) {
			return;
		}
		World.getWorld().submit(new Tickable(1) {
			public void execute() {
				if(!step()) {
					this.stop();
				}
			}
		});
	}
	
	private boolean clipped(Location location, Mob mob) {
		int protectedTypes[] = { 22, 5 };
		for(GameObject object : mob.getRegion().getGameObjects()) {
			if(object != null && object.getLocation().equals(location)) {
				int type = object.getType();
				if(type != protectedTypes[0] && type != protectedTypes[1]) {
					return true;
				}
			}
		}
		for(Location loc : World.getWorld().getLandscapes()) {
			if(loc != null && loc.equals(location)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean step() {
		if(mob == null || victim == null) {
			return false;
		}
		if(mob.getInteractingEntity() != victim && mob.getInteractingEntity() != null) {
			return false;
		}
		if(mob.getSkills().getLevel(Skills.HITPOINTS) < 1 || 
				victim.getSkills().getLevel(Skills.HITPOINTS) < 1) {
			return false;
		}
		if(mob instanceof NPC) {
			int id = ((NPC) mob).getDefinition().getId();
			switch(id) {
			case 3943:
			case 3847:
				return false;
			}
		}
		if(Misc.getDistance(mob.getLocation(), victim.getLocation()) > 12) {
			return false;
		}
		if(Misc.getDistance(mob.getLocation(), victim.getLocation()) <= 1) {
			return true;
		}
		if(!mob.getCombatState().canMove()) {
			return true;
		}
		int x = 0, y = 0;
		if(mob.getLocation().getX() < victim.getLocation().getX()) {
			x++;
		} else
		if(mob.getLocation().getX() > victim.getLocation().getX()) {
			x--;
		}
		if(mob.getLocation().getY() < victim.getLocation().getY()) {
			y++;
		} else
		if(mob.getLocation().getY() > victim.getLocation().getY()) {
			y--;
		}
		Location newLocation = Location.create((mob.getLocation().getX() + x), 
				(mob.getLocation().getY() + y));
		if(clipped(newLocation, mob)) {
			return true;
		}
		/*if(clipped(newLocation, mob)) {
			newLocation = Location.create((mob.getLocation().getX() - 1), (mob.getLocation().getY() + 1));
			if(clipped(newLocation, mob)) {
				newLocation = Location.create((mob.getLocation().getX() + 1), mob.getLocation().getY());
				if(clipped(newLocation, mob)) {
					newLocation = Location.create((mob.getLocation().getX() + 1), (mob.getLocation().getY() + 1));
					if(clipped(newLocation, mob)) {
						newLocation = Location.create((mob.getLocation().getX() - 1), (mob.getLocation().getY() - 1));
						if(clipped(newLocation, mob)) {
							newLocation = Location.create((mob.getLocation().getX() + 1), (mob.getLocation().getY() - 1));
							if(clipped(newLocation, mob)) {
								newLocation = Location.create(mob.getLocation().getX(), (mob.getLocation().getY() - 1));
								if(clipped(newLocation, mob)) {
									newLocation = Location.create((mob.getLocation().getX() - 1), mob.getLocation().getY());
									if(clipped(newLocation, mob)) {
										newLocation = Location.create(mob.getLocation().getX(), (mob.getLocation().getY() + 1));
										if(clipped(newLocation, mob)) {
											return true;
										}
									}
								}
							}
						}
					}
				}
			}
		}*/
		mob.getWalkingQueue().addStep(newLocation.getX(), newLocation.getY());
		mob.getWalkingQueue().finish();
		if(mob instanceof Player) {
			mob.face(victim.getLocation());
			mob.getWalkingQueue().setRunningToggled(true);
			mob.getWalkingQueue().setRunningQueue(true);
		}
		return true;
	}
}