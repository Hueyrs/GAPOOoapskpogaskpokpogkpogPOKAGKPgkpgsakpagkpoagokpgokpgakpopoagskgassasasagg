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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.action.ActionQueue;
import org.rs2server.rs2.action.impl.CoordinateAction;
import org.rs2server.rs2.model.Hit.HitPriority;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatFormulae;
import org.rs2server.rs2.model.combat.CombatState;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.Spell;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.tickable.impl.DeathTick;
import org.rs2server.rs2.tickable.impl.PoisonDrainTick;
import org.rs2server.rs2.tickable.impl.SkillsUpdateTick;
import org.rs2server.rs2.tickable.impl.SpecialEnergyRestoreTick;


/**
 * Represents a character in the game world, i.e. a <code>Player</code> or
 * an <code>NPC</code>.
 * @author Graham Edgecombe
 *
 */
public abstract class Mob extends Entity {
	
	private boolean canDamaged = true;
	public boolean canBeDamaged() {
		return canDamaged;
	}
	public void setCanBeDamaged(boolean b) {
		canDamaged = b;
	}
	
	public ArrayList<Integer> delayEmoteHits = new ArrayList<Integer>();
	
	public boolean canAttack = false;
	
	/**
	 * The interaction mode.
	 * @author Graham Edgecombe
	 *
	 */
	public enum InteractionMode {
		ATTACK,
		
		TALK,
		
//		FOLLOW,
		
		REQUEST
	}
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();
	
	/**
	 * Gets the random number generator.
	 * @return The random number generator.
	 */
	public Random getRandom() {
		return random;
	}

	/**
	 * Gets this mob instance.
	 * @return This mob instance.
	 */
	public Mob getMob() {
		return this;
	}
	
	/**
	 * The index in the <code>EntityList</code>.
	 */
	private int index;
	
	/**
	 * The temporary interface attributes map. items set here are removed when interfaces are closed.
	 */
	private Map<String, Object> interfaceAttributes = new HashMap<String, Object>();
	
	/**
	 * The permanent attributes map. Items set here are only removed when told to.
	 */
	private Map<String, Object> attributes = new HashMap<String, Object>();
	
	/**
	 * The teleportation target.
	 */
	private Location teleportTarget = null;
	
	/**
	 * The update flags.
	 */
	private final UpdateFlags updateFlags = new UpdateFlags();
	
	/**
	 * The list of local players.
	 */
	private final List<Player> localPlayers = new LinkedList<Player>();
	
	/**
	 * The list of local npcs.
	 */
	private final List<NPC> localNpcs = new LinkedList<NPC>();
	
	/**
	 * The teleporting flag.
	 */
	private boolean teleporting = false;
	
	/**
	 * Destroyed flag.
	 */
	private boolean destroyed = false;
	
	/**
	 * The walking queue.
	 */
	private final WalkingQueue walkingQueue = new WalkingQueue(this);
	
	/**
	 * The mob's equipment.
	 */
	private final Container equipment = new Container(Container.Type.STANDARD, Equipment.SIZE);
	
	/**
	 * The sprites i.e. walk directions.
	 */
	private final Sprites sprites = new Sprites();
	
	/**
	 * A queue of actions.
	 */
	private final ActionQueue actionQueue = new ActionQueue(this);
	
	/**
	 * The combat state.
	 */
	private final CombatState combatState = new CombatState(this);
	
	/**
	 * A set of hits done in this entity during the current update cycle.
	 */
	private final List<Hit> hits = new LinkedList<Hit>();
	
	/**
	 * The primary hit to display this update cycle.
	 */
	private Hit primaryHit;
	
	/**
	 * The secondary hit to display this update cycle.
	 */
	private Hit secondaryHit;
	
	/**
	 * The skill restoration tick.
	 */
	private Tickable skillsUpdateTick;
	
	/**
	 * The prayer restoration tick.
	 */
	private Tickable prayerUpdateTick;
	
	/**
	 * The mob's poison drain tick.
	 */
	private PoisonDrainTick poisonDrainTick;
	
	/**
	 * The mob's special energy update tick.
	 */
	private SpecialEnergyRestoreTick specialUpdateTick;

	/**
	 * The last known map region.
	 */
	private Location lastKnownRegion = this.getLocation();
	
	/**
	 * Map region changing flag.
	 */
	private boolean mapRegionChanging = false;
	
	/**
	 * The current animation.
	 */
	private Animation currentAnimation;
	
	/**
	 * The current graphic.
	 */
	private Graphic currentGraphic;
	
	/**
	 * The force walk variables.
	 */
	private int[] forceWalk;
	
	/**
	 * The interaction mode.
	 */
	private InteractionMode interactionMode;
	
	/**
	 * The interacting entity.
	 */
	private Mob interactingEntity;
	
	/**
	 * The face location.
	 */
	private Location face;
	
	/**
	 * The attack drained flag.
	 */
	private boolean attackDrained = false;

	/**
	 * The strength drained flag.
	 */
	private boolean strengthDrained = false;

	/**
	 * The defence drained flag.
	 */
	private boolean defenceDrained = false;

	/**
	 * The text to display with the force chat mask.
	 */
	private String forcedChat;
	
	/**
	 * The mob's emote flag.
	 */
	private boolean emote = true;
	
	/**
	 * The mob's animatable flag.
	 */
	private boolean animate = true;
	
	/**
	 * The players energy restore tickable.
	 */
	private Tickable energyRestoreTick;

	/**
	 * Creates the entity.
	 */
	public Mob() {
		setLocation(DEFAULT_LOCATION);
		this.lastKnownRegion = getLocation();
		World.getWorld().submit(skillsUpdateTick = new SkillsUpdateTick(this));
		if(combatState.getPoisonDamage() > 0) {
			World.getWorld().submit(this.poisonDrainTick = new PoisonDrainTick(this));
		}
		if(this.combatState.getSpecialEnergy() < 100) {
			World.getWorld().submit(this.specialUpdateTick = new SpecialEnergyRestoreTick(this));
		}
	}
	
	/**
	 * Resets misc information about the mob.
	 */
	public void resetVariousInformation() {
		getCombatState().setSpecialEnergy(100);
		getCombatState().setPoisonDamage(0, null);
		getCombatState().getDamageMap().reset();
		getCombatState().setCurrentSpell(null);
		getCombatState().setQueuedSpell(null);
		getCombatState().setVengeance(false);
		getCombatState().setSkullTicks(0);
		getCombatState().resetPrayers();
		getActionQueue().clearAllActions();
		getSkills().resetStats();
		getWalkingQueue().setEnergy(100);
		setAutocastSpell(null);
		if(isPlayer()) {
			Player player = (Player) this;
			player.setFightPitsWinner(false);
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		}
	}
	
	/**
	 * Gets the action queue.
	 * @return The action queue.
	 */
	public ActionQueue getActionQueue() {
		return actionQueue;
	}
	
	public int canEmoteWait = 0;
	
	/**
	 * Gets the combat state.
	 * @return The combat state.
	 */
	public CombatState getCombatState() {
		return combatState;
	}

	/**
	 * Makes this entity face a location.
	 * @param location The location to face.
	 */
	public void face(Location location) {
		this.face = location;
		this.updateFlags.flag(UpdateFlag.FACE_COORDINATE);
	}
	
	/**
	 * Checks if this entity is facing a location.
	 * @return The entity face flag.
	 */
	public boolean isFacing() {
		return face != null;
	}
	
	/**
	 * Resets the facing location.
	 */
	public void resetFace() {
		this.face = null;
		this.updateFlags.flag(UpdateFlag.FACE_COORDINATE);
	}
	
	/**
	 * Gets the face location.
	 * @return The face location, or <code>null</code> if the entity is not
	 * facing.
	 */
	public Location getFaceLocation() {
		return face;
	}
	
	/**
	 * Checks if this entity is interacting with another entity.
	 * @return The entity interaction flag.
	 */
	public boolean isInteracting() {
		return interactingEntity != null;
	}
	
	/**
	 * Gets the interaction mode.
	 * @return The interaction mode.
	 */
	public InteractionMode getInteractionMode() {
		return interactionMode;
	}
	
	/**
	 * Sets the interacting entity.
	 * @param mode The interaction mode.
	 * @param mob The new entity to interact with.
	 */
	public void setInteractingEntity(InteractionMode mode, Mob mob) {
		this.interactionMode = mode;
		this.interactingEntity = mob;
		this.updateFlags.flag(UpdateFlag.FACE_ENTITY);
	}
	
	/**
	 * Resets the interacting entity.
	 */
	public void resetInteractingEntity() {
		if(this.getInteractingEntity() != null) {
			if(this.getInteractionMode() == InteractionMode.TALK && this.getInteractingEntity().getInteractionMode() == InteractionMode.TALK && this.getInteractingEntity().getInteractingEntity() == this) {
				this.getInteractingEntity().setInteractingEntity(null, null); //stop an infinite for loop of each other cancelling
				this.getInteractingEntity().resetInteractingEntity();
				//this will be used for an NPC, as NPC's do not walk whilst in dialogue with someone.
			}
		}
		this.interactionMode = null;
		this.interactingEntity = null;
		this.updateFlags.flag(UpdateFlag.FACE_ENTITY);
	}
	
	/**
	 * Gets the interacting entity.
	 * @return The entity to interact with.
	 */
	public Mob getInteractingEntity() {
		return interactingEntity;
	}
	
	/**
	 * Gets this mob in entity form.
	 * @return This mob in entity form.
	 */
	public Entity getEntity() {
		return this;
	}
	
	/**
	 * Gets the current animation.
	 * @return The current animation;
	 */
	public Animation getCurrentAnimation() {
		return currentAnimation;
	}
	
	/**
	 * Gets the current graphic.
	 * @return The current graphic.
	 */
	public Graphic getCurrentGraphic() {
		return currentGraphic;
	}
	
	public int[] getForceWalk() {
		return forceWalk;
	}

	public void setForceWalk(final int[] forceWalk, final boolean removeAttribute) {
		this.forceWalk = forceWalk;
		if(forceWalk.length > 0) {
			World.getWorld().submit(new Tickable(forceWalk[7]) {
				@Override
				public void execute() {
					setTeleportTarget(getLocation().transform(forceWalk[2], forceWalk[3], 0));
					if(removeAttribute) {
						removeAttribute("busy");
					}
					this.stop();
				}
			});
		}
	}

	/**
	 * Resets attributes after an update cycle.
	 */
	public void reset() {
		this.currentAnimation = null;
		this.currentGraphic = null;
	}
	
	/**
	 * Animates the entity.
	 * @param animation The animation.
	 */
	public void playAnimation(Animation animation) {
		this.currentAnimation = animation;
		this.getUpdateFlags().flag(UpdateFlag.ANIMATION);

	}
	
	/**
	 * Plays graphics.
	 * @param graphic The graphics.
	 */
	public void playGraphics(Graphic graphic) {
		this.currentGraphic = graphic;
		this.getUpdateFlags().flag(UpdateFlag.GRAPHICS);
	}
	
	/**
	 * Gets the walking queue.
	 * @return The walking queue.
	 */
	public WalkingQueue getWalkingQueue() {
		return walkingQueue;
	}
	
	/**
	 * Sets the last known map region.
	 * @param lastKnownRegion The last known map region.
	 */
	public void setLastKnownRegion(Location lastKnownRegion) {
		this.lastKnownRegion = lastKnownRegion;
	}
	
	/**
	 * Gets the last known map region.
	 * @return The last known map region.
	 */
	public Location getLastKnownRegion() {
		return lastKnownRegion;
	}
	
	/**
	 * Checks if the map region has changed in this cycle.
	 * @return The map region changed flag.
	 */
	public boolean isMapRegionChanging() {
		return mapRegionChanging;
	}
	
	/**
	 * Sets the map region changing flag.
	 * @param mapRegionChanging The map region changing flag.
	 */
	public void setMapRegionChanging(boolean mapRegionChanging) {
		this.mapRegionChanging = mapRegionChanging;
	}
	
	/**
	 * Checks if this entity has a target to teleport to.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean hasTeleportTarget() {
		return teleportTarget != null;
	}
	
	/**
	 * Gets the teleport target.
	 * @return The teleport target.
	 */
	public Location getTeleportTarget() {
		return teleportTarget;
	}
	
	/**
	 * Sets the teleport target.
	 * @param teleportTarget The target location.
	 */
	public void setTeleportTarget(Location teleportTarget) {
		getWalkingQueue().reset();
		this.teleportTarget = teleportTarget;
	}
	
	/**
	 * Resets the teleport target.
	 */
	public void resetTeleportTarget() {
		this.teleportTarget = null;
	}
	
	/**
	 * Gets the sprites.
	 * @return The sprites.
	 */
	public Sprites getSprites() {
		return sprites;
	}
	
	/**
	 * Checks if this player is teleporting.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isTeleporting() {
		return teleporting;
	}
	
	/**
	 * Sets the teleporting flag.
	 * @param teleporting The teleporting flag.
	 */
	public void setTeleporting(boolean teleporting) {
		this.teleporting = teleporting;
	}
	
	/**
	 * Gets the list of local players.
	 * @return The list of local players.
	 */
	public List<Player> getLocalPlayers() {
		return localPlayers;
	}
	
	/**
	 * Gets the list of local npcs.
	 * @return The list of local npcs.
	 */
	public List<NPC> getLocalNPCs() {
		return localNpcs;
	}
	
	/**
	 * Sets the entity's index.
	 * @param index The index.
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
	/**
	 * Gets the entity's index.
	 * @return The index.
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Sets the current location.
	 * @param location The current location.
	 */
	@Override
	public void setLocation(Location location) {
		Location before = getLocation();
		super.setLocation(location);
		if(isPlayer()) {
			Player player = (Player) this;
			if(player.isActive()) {
				getActionSender().sendAreaInterface(before, getLocation());
				if(player.getMinigame() != null) {
					player.getMinigame().movementHook(player);
				}
			}
		}
	}
	
	/**
	 * Destroys this entity.
	 */
	public void destroy() {
		if(!destroyed) {
			if(energyRestoreTick != null) {
				energyRestoreTick.stop();
				energyRestoreTick = null;
			}
			if(skillsUpdateTick != null) {
				skillsUpdateTick.stop();
				skillsUpdateTick = null;
			}
			if(prayerUpdateTick != null) {
				prayerUpdateTick.stop();
				prayerUpdateTick = null;
			}
			if(poisonDrainTick != null) {
				poisonDrainTick.stop();
				poisonDrainTick = null;
			}
			if(specialUpdateTick != null) {
				specialUpdateTick.stop();
				specialUpdateTick = null;
			}
			resetInteractingEntity(); //so that if someone logs in dialogue, it resets the interacting entity allowing them to move again.
			destroyed = true;
			removeFromRegion(getRegion());
		}
	}

	/**
	 * Gets the current combat cooldown delay in milliseconds.
	 * @return The current combat cooldown delay.
	 */
	public abstract int getCombatCooldownDelay();
	
	/**
	 * Gets the mob's default combat action.
	 * @return The mob's default combat action.
	 */
	public abstract CombatAction getDefaultCombatAction();
	
	/**
	 * Gets the mob's autocast spell.
	 * @return The mob's autocast spell.
	 */
	public abstract Spell getAutocastSpell();
	
	/**
	 * Sets the mob's autocast spell.
	 * @param spell The spell to set.
	 */
	public abstract void setAutocastSpell(Spell spell);
	
	/**
	 * Gets the hit flag defined by the entity type (EG Players wilderness level)
	 * @param victim The victim.
	 * @return The hit flag.
	 */
	public abstract boolean canHit(Mob victim, boolean messages);
	
	/**
	 * Checks if this entity will auto retaliate to any attacks.
	 * @return <code>true</code> if the entity will auto retaliate,
	 * <code>false</code> if not.
	 */
	public abstract boolean isAutoRetaliating();
	
	/**
	 * Gets the mob's attack animation.
	 * @return The mob's attack animation.
	 */
	public abstract Animation getAttackAnimation();
	
	/**
	 * Gets the mob's defend animation.
	 * @return The mob's defend animation.
	 */
	public abstract Animation getDefendAnimation();
	
	/**
	 * Gets the mob's death animation.
	 * @return The mob's death animation.
	 */
	public abstract Animation getDeathAnimation();
	
	/**
	 * Gets the projectile lockon index of this mob.
	 * @return The projectile lockon index of this mob.
	 */
	public abstract int getProjectileLockonIndex();
	
	/**
	 * The protection prayer modifier. EG: NPCs = 1, players = 0.6.
	 */
	public abstract double getProtectionPrayerModifier();
	
	/**
	 * Gets the mob's defined name.
	 * @return The mob's defined name.
	 */
	public abstract String getDefinedName();
	
	/**
	 * Gets the mob's undefined name (Players).
	 * @return The mob's undefined name (Players).
	 */
	public abstract String getUndefinedName();
	
	/**
	 * Resets the mob's animations.
	 * @return Resets the mob's animations.
	 */
	public abstract void setDefaultAnimations();
	
	/**
	 * Drops the loot for the killer.
	 * @param mob The killer to drop the items for.
	 */
	public abstract void dropLoot(Mob mob);
	
	/**
	 * Gets the default drawback graphic for this mob's range attack.
	 * @return The default drawback graphic for this mob's range attack.
	 */
	public abstract Graphic getDrawbackGraphic();
	
	/**
	 * Gets the default projectile id for this mob's range attack.
	 * @return The default projectile id for this mob's range attack.
	 */
	public abstract int getProjectileId();

	/**
	 * Gets the update flags.
	 * @return The update flags.
	 */
	public UpdateFlags getUpdateFlags() {
		return updateFlags;
	}
	
	/**
	 * Gets the player's equipment.
	 * @return The player's equipment.
	 */
	public Container getEquipment() {
		return equipment;
	}

	/**
	 * Checks if this entity has been destroyed.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isDestroyed() {
		return destroyed;
	}

	/**
	 * Gets the hit queue.
	 * @return The hit queue.
	 */
	public List<Hit> getHitQueue() {
		return hits;
	}
	
	/**
	 * Sets the primary hit.
	 * @param hit The primary hit.
	 */
	public void setPrimaryHit(Hit hit) {
		this.primaryHit = hit;
	}

	/**
	 * Sets the secondary hit.
	 * @param hit The secondary hit.
	 */
	public void setSecondaryHit(Hit hit) {
		this.secondaryHit = hit;
	}
	
	/**
	 * Gets the primary hit.
	 * @return The primary hit.
	 */
	public Hit getPrimaryHit() {
		return primaryHit;
	}
	
	/**
	 * Gets the secondary hit.
	 * @return The secondary hit.
	 */
	public Hit getSecondaryHit() {
		return secondaryHit;
	}
	
	/**
	 * Resets the primary and secondary hits.
	 */
	public void resetHits() {
		primaryHit = null;
		secondaryHit = null;
	}
	
	/**
	 * Gets the mob's action sender.
	 * @return The mob's action sender.
	 */
	public abstract ActionSender getActionSender();
	
	/**
	 * Gets the mob's interface state.
	 * @return The mob's interface state.
	 */
	public abstract InterfaceState getInterfaceState();
	
	/**
	 * Gets a mob's inventory.
	 * @return The mob's inventory.
	 */
	public abstract Container getInventory();
	
	/**
	 * Gets the mob's skill levels.
	 * @return The mob's skill levels.
	 */
	public abstract Skills getSkills();

	/**
	 * Creates the force chat mask.
	 * @param message
	 */
	public void forceChat(String message){
		forcedChat = message;
		updateFlags.flag(UpdateFlag.FORCED_CHAT);
	}

	/**
	 * Creates the force chat mask.
	 * @param message
	 */
	public void setForceChat(String message){
		forcedChat = message;
	}
	
	/**
	 * Gets the message to display with the force chat mask.
	 * @return The message to display with the force chat mask.
	 */
	public String getForcedChatMessage() {
		return forcedChat;
	}

	/**
	 * Gets the emote flag.
	 * @return The emote flag.
	 */
	public boolean canEmote() {
		return emote;
	}

	/**
	 * Sets the emote flag.
	 * @param animate The emote flag to set.
	 */
	public void setEmote(boolean emote) {
		this.emote = emote;
	}

	/**
	 * Gets the animatable flag.
	 * @return The animatable flag.
	 */
	public boolean canAnimate() {
		return animate;
	}

	/**
	 * Sets the animatable flag.
	 * @param animate The animatable flag to set.
	 */
	public void setAnimate(boolean animate) {
		this.animate = animate;
	}

	/**
	 * @return the energyRestoreTick
	 */
	public Tickable getEnergyRestoreTick() {
		return energyRestoreTick;
	}
	
	/**
	 * @param energyRestoreTick the energyRestoreTick to set
	 */
	public void setEnergyRestoreTick(Tickable energyRestoreTick) {
		this.energyRestoreTick = energyRestoreTick;
	}

	/**
	 * @return the prayerUpdateTick
	 */
	public Tickable getPrayerUpdateTick() {
		return prayerUpdateTick;
	}

	/**
	 * @param prayerUpdateTick the prayerUpdateTick to set
	 */
	public void setPrayerUpdateTick(Tickable prayerUpdateTick) {
		this.prayerUpdateTick = prayerUpdateTick;
	}
	
	/**
	 * @param poisonDrainTick the poisonDrainTick to set
	 */
	public void setPoisonDrainTick(PoisonDrainTick poisonDrainTick) {
		this.poisonDrainTick = poisonDrainTick;
	}

	/**
	 * Gets the mob's poison drain tick.
	 * @return The mob's poisonDrainTick.
	 */
	public PoisonDrainTick getPoisonDrainTick() {
		return poisonDrainTick;
	}
	
	/**
	 * @param specialUpdateTick The specialUpdateTick to set.
	 */
	public void setSpecialUpdateTick(SpecialEnergyRestoreTick specialUpdateTick) {
		this.specialUpdateTick = specialUpdateTick;
	}

	/**
	 * Gets the mob's special energy update tick.
	 * @return The mob's prayer energy tick.
	 */
	public SpecialEnergyRestoreTick getSpecialUpdateTick() {
		return specialUpdateTick;
	}
	
	/**
	 * Inflicts damage to the mob.
	 * @param hit The hit to deal.
	 * @param mob The damage dealer.
	 */
	public void inflictDamage(Hit hit, Mob attacker) {
		
		if(!canDamaged) {
			return;
		}
		
//		if(!combatState.canTeleport() && !(getActionSender() != null && ((Player) this) != null 
//					&& ((Player) this).getRequestManager().getRequestType() == RequestType.DUEL
//					&& ((Player) this).getRequestManager().getState() == RequestState.ACTIVE)) {
//			return;
//		}
		
		if(getActionSender() != null) {
			getActionSender().removeAllInterfaces();
		}
		
		if(hit.getDamage() > getSkills().getLevel(Skills.HITPOINTS)) {
			hit = new Hit(getSkills().getLevel(Skills.HITPOINTS));
		}
		
		if(getSkills().getLevel(Skills.HITPOINTS) < 1 && hit.getDamage() > 0) {
			return;
		}
		
		if(combatState.isDead() && hit.getDamage() > 0) { //If its a double hitting spec, we still want to see their 0 damage
			return;
		}
		
		getSkills().decreaseLevel(Skills.HITPOINTS, hit.getDamage());
		if(combatState.getPrayer(Prayers.REDEMPTION) && getSkills().getLevel(Skills.HITPOINTS) > 0) {
			if(getSkills().getLevel(Skills.HITPOINTS) < (getSkills().getLevelForExperience(Skills.HITPOINTS) * 0.10)) {
				combatState.resetPrayers();
				if(getActionSender() != null) {
					getActionSender().sendMessage("You have run out of prayer points; you must recharge at an altar.");
				}
				getSkills().setPrayerPoints(0, true);
				getSkills().increaseLevel(Skills.HITPOINTS, (int) (getSkills().getLevelForExperience(Skills.PRAYER) * 0.25));
				playGraphics(Graphic.create(436));
			}			
		}
//		if(getEquipment().contains(2570) && getSkills().getLevel(Skills.HITPOINTS) > 0) {
//			if(combatState.canTeleport() && getSkills().getLevel(Skills.HITPOINTS) <= (getSkills().getLevelForExperience(Skills.HITPOINTS) * 0.10)) {
//				initiateTeleport(TeleportType.NORMAL_TELEPORT, Location.create(3225 + random.nextInt(1), 3218 + random.nextInt(1), 0));
//				getEquipment().remove(new Item(2570, 1));
//				if(getActionSender() != null) {
//					getActionSender().sendMessage("Your Ring of Life saves you and is destroyed in the process.");
//				}
//			}
//		}
		if(getHitQueue().size() >= 4) {
			hit = new Hit(hit.getDamage(), HitPriority.LOW_PRIORITY);//if multiple people are hitting on an opponent, this prevents hits from stacking up for a long time and looking off-beat
		}
		getHitQueue().add(hit);
		if(attacker != null) {
			getCombatState().getDamageMap().incrementTotalDamage(attacker, hit.getDamage());
			if(!BoundaryManager.isWithinBoundaryNoZ(attacker.getLocation(), World.getWorld().getFightPits().getBoundary()) && !attacker.getCombatState().getDamageMap().getTotalDamages().containsKey(this) && attacker.isPlayer() && isPlayer()) {
				attacker.getCombatState().setSkullTicks(100 * 10); //10 * 1 min
			}
		}
		if(getSkills().getLevel(Skills.HITPOINTS) <= 0 && !combatState.isDead()) {
			combatState.setDead(true);
			getActionQueue().clearRemovableActions();
			resetInteractingEntity();
			getWalkingQueue().reset();
			final Mob thisMob = this;
			if(combatState.getPrayer(Prayers.RETRIBUTION)) {
				playGraphics(Graphic.create(437, 40));
				final ArrayList<Location> locationsUsed = new ArrayList<Location>();
				World.getWorld().submit(new Tickable(2) {
					@Override
					public void execute() {
						for(Mob mob : getRegion().getMobs()) {
							if(!mob.getCombatState().isDead()) {
								if(combatState.getLastHitTimer() > (System.currentTimeMillis() + 4000)) { //10 cycles for tagging timer
									if(combatState.getLastHitBy() != null && mob != combatState.getLastHitBy()) {
										continue;
									}
								}
								if(mob.getCombatState().getLastHitTimer() > (System.currentTimeMillis() + 4000)) { //10 cycles for tagging timer
									if(mob.getCombatState().getLastHitBy() != null && getMob() != mob.getCombatState().getLastHitBy()) {
										continue;
									}
								}
								if(mob.getLocation().isNextTo(getLocation()) && !locationsUsed.contains(mob.getLocation())) {
									locationsUsed.add(mob.getLocation());
									int dmg = random.nextInt((int) (getSkills().getLevelForExperience(Skills.PRAYER) * 0.25)); // +1 as its exclusive
									mob.inflictDamage(new Hit(dmg), thisMob);
								}
							}
						}
						this.stop();
					}					
				});
			}
			if(isNPC()) {
				if (attacker != null) {
					attacker.getCombatState().setLastHitBy(null);
					attacker.getCombatState().setLastHitTimer(0);
				}
			}
			World.getWorld().submit(new Tickable(3) {
				public void execute() {
					playAnimation(getDeathAnimation());
					this.stop();
				}
			});
			int ticks = 8;
			if(isNPC()) {
				NPC npc = (NPC) this;
				if(npc.getDefinition().getName().equalsIgnoreCase("kolodion")) {
					ticks = 5;// ?
				}
			}			World.getWorld().submit(new DeathTick(this, 8));
		}
	}
	
	/**
	 * Gets the active combat action.
	 * @return The active combat action.
	 */
	public CombatAction getActiveCombatAction() {
		return CombatFormulae.getActiveCombatAction(this);
	}
	
	/**
	 * Checks if the mob can teleport.
	 * @return If a mob can teleport.
	 */
	public boolean canTeleport() {
		if(!combatState.canTeleport()) {
			return false;
		} else if(combatState.isTeleblocked()) {
			getActionSender().sendMessage("A magical force stops you from teleporting.");
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * @return the attackDrained
	 */
	public boolean isAttackDrained() {
		return attackDrained;
	}

	/**
	 * @param attackDrained the attackDrained to set
	 */
	public void setAttackDrained(boolean attackDrained) {
		this.attackDrained = attackDrained;
	}
	
	/**
	 * @return the strengthDrained
	 */
	public boolean isStrengthDrained() {
		return strengthDrained;
	}

	/**
	 * @param strengthDrained the strengthDrained to set
	 */
	public void setStrengthDrained(boolean strengthDrained) {
		this.strengthDrained = strengthDrained;
	}

	/**
	 * @return the defenceDrained
	 */
	public boolean isDefenceDrained() {
		return defenceDrained;
	}

	/**
	 * @param defenceDrained the defenceDrained to set
	 */
	public void setDefenceDrained(boolean defenceDrained) {
		this.defenceDrained = defenceDrained;
	}
	
	/**
	 * Adds an action that requires to be within distance to it.
	 * @param distance The distance.
	 * @param width The width.
	 * @param height The height.
	 * @param location The location.
	 * @param action The action.
	 */
	public void addCoordinateAction(int width, int height, Location location, int otherWidth, int otherHeight, int distance, Action action) {
		actionQueue.clearAllActions();
		if (getLocation().isWithinDistance(width, height, location, otherWidth, otherHeight, distance)) {
			actionQueue.addAction(action);
		} else {
			actionQueue.addAction(new CoordinateAction(this, width, height, location, otherWidth, otherHeight, distance, action));
		}
	}
	
	/**
	 * Removes an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 * @param <T> The type of the value.
	 * @param key The key.
	 * @return The old value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T removeInterfaceAttribute(String key) {
		return (T) interfaceAttributes.remove(key);
	}
	
	/**
	 * Removes an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 * @param <T> The type of the value.
	 * @param key The key.
	 * @return The old value.
	 */
	public void removeAllInterfaceAttributes() {
		if(interfaceAttributes != null && interfaceAttributes.size() > 0 && interfaceAttributes.keySet().size() > 0) {
			interfaceAttributes = new HashMap<String, Object>();
		}
	}
	
	/**
	 * Sets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 * @param <T> The type of the value.
	 * @param key The key.
	 * @param value The value.
	 * @return The old value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T setInterfaceAttribute(String key, T value) {
		return (T) interfaceAttributes.put(key, value);
	}
	
	/**
	 * Gets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 * @param <T> The type of the value.
	 * @param key The key.
	 * @return The value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getInterfaceAttribute(String key) {
		return (T) interfaceAttributes.get(key);
	}
	
	/**
	 * Gets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 * @param <T> The type of the value.
	 * @param key The key.
	 * @return The value.
	 */
	public Map<String, Object> getInterfaceAttributes() {
		return interfaceAttributes;
	}
	
	/**
	 * Removes an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 * @param <T> The type of the value.
	 * @param key The key.
	 * @return The old value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T removeAttribute(String key) {
		return (T) attributes.remove(key);
	}
	
	/**
	 * Removes an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 * @param <T> The type of the value.
	 * @param key The key.
	 * @return The old value.
	 */
	public void removeAllAttributes() {
		if(attributes != null && attributes.size() > 0 && attributes.keySet().size() > 0) {
			attributes = new HashMap<String, Object>();
		}
	}
	
	/**
	 * Sets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 * @param <T> The type of the value.
	 * @param key The key.
	 * @param value The value.
	 * @return The old value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T setAttribute(String key, T value) {
		return (T) attributes.put(key, value);
	}
	
	/**
	 * Gets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 * @param <T> The type of the value.
	 * @param key The key.
	 * @return The value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key) {
		return (T) attributes.get(key);
	}
	
	/**
	 * Gets an attribute.<br />
	 * WARNING: unchecked cast, be careful!
	 * @param <T> The type of the value.
	 * @param key The key.
	 * @return The value.
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	/**
	 * Sends the mob a sound, if they have an action sender.
	 * @param id The id.
	 * @param volume The volume.
	 * @param delay The delay.
	 */
	public void playSound(Sound sound) {
		if(getActionSender() != null) {
			getActionSender().playSound(sound);
		}
	}

}
