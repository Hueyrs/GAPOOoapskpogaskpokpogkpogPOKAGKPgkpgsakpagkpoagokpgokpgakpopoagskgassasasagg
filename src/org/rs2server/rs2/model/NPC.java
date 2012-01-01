package org.rs2server.rs2.model;

/*
 * IMPORTANT MESSAGE - READ BEFORE ADDING NEW METHODS/FIELDS TO THIS CLASS
 * 
 * Before you create a field (variable) or method in this class, which is specific to a particular
 * skill, quest, minigame, etc, THINK! There is almost always a better way (e.g. attribute system,
 * helper methods in other classes, etc.)
 * 
 * We don't want this to turn into another client.java! If you need advice on alternative methods,
 * feel free to discuss it with me.
 * 
 * Graham
 */

import java.util.Random;

import org.rs2server.rs2.GameEngine;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.Spell;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.task.Task;
import org.rs2server.rs2.util.Misc;
import org.rs2server.rs2.Constants;


/**
 * <p>Represents a non-player character in the in-game world.</p>
 * @author Graham Edgecombe
 * @author Canownueasy
 */
public class NPC extends Mob {
	
	public int completedRapidAttacks = 0;
	
	public int spawnedAssistants = 0;
	
	private int aggDistance = 5;
	
	public int getAggressiveDistance() {
		return aggDistance;
	}
	
	public void setAggressiveDistance(int distance) {
		this.aggDistance = distance;
	}
	
	public void takeStep(final Mob mob) {
		if(!getCombatState().canMove()) {
			return;
		}
		if(mob == null || mob.getSkills().getLevel(Skills.HITPOINTS) < 1) {
			return;
		}
		if(getSkills().getLevel(3) < 1) {
			return;
		}
		int i = getDefinition().getId();
		if(i == 2892 || i == 3847) {
			return;
		}
		int x = 0, y = 0;
		if(mob.getLocation().getX() > getLocation().getX()) {
			x++;
		} else
		if(mob.getLocation().getX() < getLocation().getX()) {
			x--;
		}
		if(mob.getLocation().getY() > getLocation().getY()) {
			y++;
		} else
		if(mob.getLocation().getY() < getLocation().getY()) {
			y--;
		}
		//String name = getDefinition().getName().trim().toLowerCase();
		Location newLocation = Location.create((getLocation().getX() + x), (getLocation().getY() + y));
		if(getCombatDefinition().getAttackType() == AttackType.MAGIC ||
				getCombatDefinition().getAttackType() == AttackType.RANGE) {
			//Because we are a mager/ranger we can be about 3 space away.
			if(Misc.getDistance(getLocation(), mob.getLocation()) <= 3) {
				return;
			}
		} else {
			//If we use melee we can be 1 space away.
			if(Misc.getDistance(getLocation(), mob.getLocation()) <= 1) {
				return;
			}
		}
		/*Location abstractLoc = null;
		for(GameObject obj : getRegion().getGameObjects()) {
			if((obj.getType() == 0 || obj.getType() == 1 || obj.getType() == 2) && obj.getLocation().equals(getLocation())) {
				System.out.println("No move: " + obj.getLocation());
				String dir = "";
				int myX = getLocation().getX();
				int myY = getLocation().getY();
				int theirX = newLocation.getX();
				int theirY = newLocation.getY();
				if(theirY > myY && theirX >= myX - Misc.getDistance(getLocation(), newLocation) && theirX <= myX + Misc.getDistance(getLocation(), newLocation)) {
					dir = "N";
				} else
				if(theirX > myX && theirY >= myY - Misc.getDistance(getLocation(), newLocation) && theirY <= myY + Misc.getDistance(getLocation(), newLocation)) {
					dir = "E";
				} else
				if(theirY < myY && theirX >= myX - Misc.getDistance(getLocation(), newLocation) && theirX <= myX + Misc.getDistance(getLocation(), newLocation)) {
					dir = "S";
				} else {
				if(theirX < myX && theirY >= myY - Misc.getDistance(getLocation(), newLocation) && theirY <= myY + Misc.getDistance(getLocation(), newLocation)) {
					dir = "W";
				}
				System.out.println(dir);
				if(dir.equals("N")) {
					abstractLoc = Location.create(getLocation().getX(), (getLocation().getY() + 1));
				} else
				if(dir.equals("S")) {
					abstractLoc = Location.create(getLocation().getX(), (getLocation().getY() - 1));
				} else
				if(dir.equals("E")) {
					abstractLoc = Location.create((getLocation().getX() + 1), getLocation().getY());
				} else
				if(dir.equals("W")) {
					abstractLoc = Location.create((getLocation().getX() - 1), getLocation().getY());
				}
				if(!newLocation.equals(abstractLoc)) {
					if(abstractLoc != null) {
						for(GameObject objs : getRegion().getGameObjects()) {
							if(objs != null && abstractLoc != null && objs.getType() != 22 && obj.getType() != 5 && objs.getType() != 0 && objs.getType() != 1 && objs.getType() != 2 && objs.getLocation().getZ() == getLocation().getZ() && objs.getLocation().equals(abstractLoc)) {
								System.out.println("Clipped abstract obj: type " + objs.getType() + " loc " + objs.getLocation());
								return;
							}
						}
						for(Location locs : World.getWorld().getLandscapes()) {
							if(locs != null && locs.equals(abstractLoc)) {
								System.out.println("Clipped abstract landscape: " + locs);
								return;
							}
						}
						getWalkingQueue().addStep(abstractLoc.getX(), abstractLoc.getY());
						getWalkingQueue().finish();
					}
					return;
				}
			}
		}*/
		for(Location loc : World.getWorld().getLandscapes()) {
			if(loc != null && loc.equals(newLocation)) {
				System.out.println("Clipped landscape: " + loc);
				return;
			}
		}
		for(GameObject objz : getRegion().getGameObjects()) {
			if(objz != null && objz.getType() != 22 && objz.getType() != 0 && objz.getType() != 1 && objz.getType() != 2 && objz.getType() != 5 && objz.getLocation().getZ() == getLocation().getZ() && objz.getLocation().equals(newLocation)) {
				System.out.println("Clipped obj: type " + objz.getType() + " loc " + objz.getLocation());
				return;
			}
		}
		getWalkingQueue().addStep(newLocation.getX(), newLocation.getY());
		getWalkingQueue().finish();
	}
	
	/*Location abstractLoc = null;
	for(GameObject obj : getRegion().getGameObjects()) {
		if((obj.getType() == 0 || obj.getType() == 1 || obj.getType() == 2) && obj.getLocation().equals(getLocation())) {
			System.out.println("No move: " + obj.getLocation());
			String dir = "";
			int myX = getLocation().getX();
			int myY = getLocation().getY();
			int theirX = newLocation.getX();
			int theirY = newLocation.getY();
			if(theirY > myY && theirX >= myX - Misc.getDistance(getLocation(), newLocation) && theirX <= myX + Misc.getDistance(getLocation(), newLocation)) {
				dir = "N";
			} else
			if(theirX >= myX + Misc.getDistance(getLocation(), newLocation) && theirY >= myY + Misc.getDistance(getLocation(), newLocation)) {
				dir = "NE";
			} else
			if(theirX > myX && theirY >= myY - Misc.getDistance(getLocation(), newLocation) && theirY <= myY + Misc.getDistance(getLocation(), newLocation)) {
				dir = "E";
			} else
			if(theirY <= myY - Misc.getDistance(getLocation(), newLocation) && theirX >= myX + Misc.getDistance(getLocation(), newLocation)) {
				dir = "SE";
			} else
			if(theirY < myY && theirX >= myX - Misc.getDistance(getLocation(), newLocation) && theirX <= myX + Misc.getDistance(getLocation(), newLocation)) {
				dir = "S";
			} else
			if(theirX <= myX - Misc.getDistance(getLocation(), newLocation) && theirY <= myY - Misc.getDistance(getLocation(), newLocation)) {
				dir = "SW";
			} else
			if(theirX < myX && theirY >= myY - Misc.getDistance(getLocation(), newLocation) && theirY <= myY + Misc.getDistance(getLocation(), newLocation)) {
				dir = "W";
			} else
			if(theirX <= myX - Misc.getDistance(getLocation(), newLocation) && theirY >= myY + Misc.getDistance(getLocation(), newLocation)) {
				dir = "NW";
			} else {
				dir = "WTF";
			}
			System.out.println(dir);
			if(dir.equals("N")) {
				abstractLoc = Location.create(getLocation().getX(), (getLocation().getY() + 1));
			} else
			if(dir.equals("S")) {
				abstractLoc = Location.create(getLocation().getX(), (getLocation().getY() - 1));
			} else
			if(dir.equals("E")) {
				abstractLoc = Location.create((getLocation().getX() + 1), getLocation().getY());
			} else
			if(dir.equals("W")) {
				abstractLoc = Location.create((getLocation().getX() - 1), getLocation().getY());
			}
			if(!newLocation.equals(abstractLoc)) {
				if(abstractLoc != null) {
					for(GameObject objs : getRegion().getGameObjects()) {
						if(objs != null && abstractLoc != null && objs.getType() != 22 && obj.getType() != 5 && objs.getType() != 0 && objs.getType() != 1 && objs.getType() != 2 && objs.getLocation().getZ() == getLocation().getZ() && objs.getLocation().equals(abstractLoc)) {
							System.out.println("Clipped abstract obj: type " + objs.getType() + " loc " + objs.getLocation());
							return;
						}
					}
					for(Location locs : World.getWorld().getLandscapes()) {
						if(locs != null && locs.equals(abstractLoc)) {
							System.out.println("Clipped abstract landscape: " + locs);
							return;
						}
					}
					getWalkingQueue().addStep(abstractLoc.getX(), abstractLoc.getY());
					getWalkingQueue().finish();
				}
				return;
			}
		}
	}*/
	
	private int transformationId = 2;
	
	public void transform(int id) {
		if(id < 1) {
			System.out.println("Transformation id must be >= 1");
			return;
		}
		transformationId = id;
		definition = NPCDefinition.forId(id);
		combatDefinition = CombatNPCDefinition.forId(id);
		setTeleportTarget(getLocation());
		getUpdateFlags().flag(UpdateFlag.TRANSFORM);
	}

	public int getTransformationId() {
		return transformationId;
	}
	
	/**
	 * The player who summoned this npc.
	 */
	private Player summoner;
	
	public void setSummoner(Player player) {
		summoner = player;
	}

	public Player getSummoner() {
		return summoner;
	}
	
	/**
	 * Transforms the NPC into another.
	 * @param newNpc The other NPC.
	 */
	public void transformTo(NPC newNpc) {
		if(Constants.DEBUGGING) {
			System.out.println("Transformed NPC " + getDefinition().getId() + " into " + newNpc.getDefinition().getId());
		}
		World.getWorld().register(newNpc);
		World.getWorld().unregister(this);
	}
	
	/**
	 * The definition.
	 */
	private NPCDefinition definition;
	
	/**
	 * The combat definition.
	 */
	private CombatNPCDefinition combatDefinition = null;
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();
	
	/**
	 * The npc's skill levels.
	 */
	private final Skills skills = new Skills(this);

	/**
	 * The minimum coordinate for this npc.
	 */
	private Location minLocation;

	/**
	 * The maximum coordinate for this npc.
	 */
	private Location maxLocation;

	/**
	 * The spawn coordinate for this npc.
	 */
	private Location spawnLocation;
	
	/**
	 * The spawn direction for this npc.
	 */
	private int spawnDirection;

	/**
	 * The combat cooldown delay.
	 */
	private int combatCooldownDelay = 4;

	/**
	 * Creates the NPC with the specified definition.
	 * @param combatDefinition The definition.
	 */
	public NPC(NPCDefinition definition, Location spawnLocation, Location minLocation, Location maxLocation, int direction) {
		this.definition = definition;
		this.minLocation = minLocation;
		this.maxLocation = maxLocation;
		this.spawnLocation = spawnLocation;
		this.spawnDirection = direction;
		this.setDirection(direction);
	}
	
	/**
	 * Gets the NPC definition.
	 * @return The NPC definition.
	 */
	public NPCDefinition getDefinition() {
		return definition;
	}
	
	/**
	 * Gets the NPC combat definition.
	 * @return The NPC combat definition.
	 */
	public CombatNPCDefinition getCombatDefinition() {
		return combatDefinition;
	}

	/**
	 * @param combatDefinition the combatDefinition to set
	 */
	public void setCombatDefinition(CombatNPCDefinition combatDefinition) {
		this.combatDefinition = combatDefinition;
	}

	/**
	 * @return the minLocation
	 */
	public Location getMinLocation() {
		return minLocation;
	}
	
	/**
	 * @return the maxLocation
	 */
	public Location getMaxLocation() {
		return maxLocation;
	}
	
	public boolean canMove() {
		return minLocation != null && maxLocation != null && !(minLocation == spawnLocation && maxLocation == spawnLocation);
	}
	
	/**
	 * @return the spawnLocation
	 */
	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public int getSpawnDirection() {
		return spawnDirection;
	}

	@Override
	public void addToRegion(Region region) {
		region.addNpc(this);
		region.addMob(this);
	}

	@Override
	public void removeFromRegion(Region region) {
		region.removeNpc(this);
		region.removeMob(this);
	}

	@Override
	public int getClientIndex() {
		return this.getIndex();
	}
	
	@Override
	public Skills getSkills() {
		return skills;
	}

	@Override
	public int getHeight() {
		return definition.getSize();
	}

	@Override
	public int getWidth() {
		return definition.getSize();
	}

	@Override
	public ActionSender getActionSender() {
		return null;
	}

	@Override
	public InterfaceState getInterfaceState() {
		return null;
	}

	@Override
	public Container getInventory() {
		return null;
	}

	@Override
	public boolean isNPC() {
		return true;
	}

	@Override
	public boolean isPlayer() {
		return false;
	}

	@Override
	public int getCombatCooldownDelay() {
		return combatCooldownDelay;
	}

	/**
	 * @param combatCooldownDelay the combatCooldownDelay to set
	 */
	public void setCombatCooldownDelay(int combatCooldownDelay) {
		this.combatCooldownDelay = combatCooldownDelay;
	}

	@Override
	public CombatAction getDefaultCombatAction() {
		return combatDefinition.getCombatAction();
	}

	@Override
	public Location getCentreLocation() {
		return Location.create(getLocation().getX() + (int) Math.floor(getWidth() / 2), getLocation().getY() + (int) Math.floor(getHeight() / 2), getLocation().getZ());
	}

	@Override
	public boolean canHit(Mob victim, boolean messages) {
		return combatDefinition != null;
	}

	@Override
	public boolean isAutoRetaliating() {
		return true;
	}

	@Override
	public int getProjectileLockonIndex() {
		return getIndex() + 1;
	}

	@Override
	public double getProtectionPrayerModifier() {
		return 0; //* 0 to remove the entire hit
	}

	@Override
	public String getDefinedName() {
		return definition.getName();
	}

	@Override
	public String getUndefinedName() {
		return "";
	}

	@Override
	public Animation getAttackAnimation() {
		return combatDefinition.getAttack();
	}

	@Override
	public Animation getDeathAnimation() {
		return combatDefinition.getDeath();
	}

	@Override
	public Animation getDefendAnimation() {
		return combatDefinition.getDefend();
	}

	@Override
	public Spell getAutocastSpell() {
		return combatDefinition.getSpell();
	}

	@Override
	public void setAutocastSpell(Spell spell) {
		if(spell != null) {
			combatDefinition.setSpell(spell);
		}
	}

	@Override
	public void setDefaultAnimations() {
	}

	@Override
	/**
	 * @author Joe.melsha@live.com (Killer 99)
	 */

	public void dropLoot(final Mob mob) {
		World.getWorld().submit(new Task() {
			@Override
			public void execute(GameEngine context) {
				long startTime = System.nanoTime();
				long startTimeMS = System.currentTimeMillis();
				NPCDrop[] constantDrops = combatDefinition.getConstantDrops();
				if (constantDrops == null)
					return;
				NPCDrop[] randomDrops = combatDefinition.getRandomDrops();
				if (randomDrops == null)
					return;
			
				try {
				for(int i = 0; i < constantDrops.length; i++) {
					if(constantDrops[i].getFrequency() >= 1) {
						World.getWorld().createGroundItem(new GroundItem(mob.getUndefinedName(), constantDrops[i].getItem(), getLocation()), (Player) mob);	
						System.out.println("Dropped item " +  constantDrops[i].getItem().toString() + " at frequency " +  constantDrops[i].getFrequency());
					}
				}
		
				if(randomDrops.length > 0) {
					NPCDrop drop = null;
					while(drop == null) {
						NPCDrop randomDrop = randomDrops[random.nextInt(randomDrops.length)];
						boolean alwaysDrop = randomDrop.getFrequency() >= 1;
						if(!alwaysDrop && random.nextDouble() < randomDrop.getFrequency()) {
							drop = randomDrop;
						}
					}
					System.out.println("Dropped item " + drop.getItem().toString() + " at frequency " + drop.getFrequency());
					World.getWorld().createGroundItem(new GroundItem(mob.getUndefinedName(), drop.getItem(), getLocation()), (Player) mob);
				}
				} catch(Exception e) {};
				System.out.println("Took " + (System.nanoTime() - startTime)+" nanoseconds (" + (System.currentTimeMillis() - startTimeMS) +" milliseconds)");
			}
		});
	}


	@Override
	public boolean isObject() {
		return false;
	}

	@Override
	public Graphic getDrawbackGraphic() {
		return combatDefinition.getDrawbackGraphic();
	}

	@Override
	public int getProjectileId() {
		return combatDefinition.getProjectileId();
	}
	
	/**
	 * Creates a new NPC which is never added to the game.
	 */
	public static NPC create(NPCDefinition definition) {
		return create(definition, null, null, null);
	}
	/**
	 * Creates the NPC with the specified definition.
	 * @param definition The definition.
	 */
	public NPC(NPCDefinition definition) {
		super();
		this.definition = definition;
	}

	/**
	 * Creates a new NPC with a location, as well as a min/max location.
	 */
	public static NPC create(NPCDefinition definition, Location location, Location minLocation, Location maxLocation) {
		switch(definition.getId()) {
		}
		return location == null ? new NPC(definition) : new NPC(definition, location, minLocation, maxLocation);
	}
	/**
	 * Creates the NPC with the specified definition, minimum and maximum Locations.
	 * @param definition The definition.
	 * @param minLocation The lowest location the NPC can walk in.
	 * @param maxLocation The highest location the NPC can walk in.
	 */
	public NPC(NPCDefinition definition, Location location, Location minLocation, Location maxLocation) {
		super();
		this.definition = definition;
		this.minLocation = minLocation;
		this.maxLocation = maxLocation;
		spawnLocation = location;
		setLocation(location);
		setLastKnownRegion(location);
	}

}
