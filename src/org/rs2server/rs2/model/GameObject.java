package org.rs2server.rs2.model;

import org.rs2server.rs2.model.region.Region;

/**
 * Represents a single game object.
 * @author Graham Edgecombe
 *
 */
public class GameObject extends Entity {
	
	/**
	 * The definition.
	 */
	private GameObjectDefinition definition;
	
	/**
	 * The object id.
	 */
	private int id;
	
	/**
	 * The object's spawn location.
	 */
	private Location spawnLocation;

	/**
	 * The type.
	 */
	private int type;
	
	/**
	 * The loaded in landscape flag.
	 */
	private boolean loadedInLandscape;
	
	/**
	 * The maximum amount of health this object has (for trees).
	 */
	private int maxHealth = 0;

	/**
	 * The current health this object has (for trees).
	 */
	private int currentHealth = 0;

	/**
	 * Creates the game object.
	 * @param definition The definition.
	 * @param location The location.
	 * @param type The type.
	 * @param rotation The rotation.
	 */
	public GameObject(Location location, int id, int type, int direction, boolean loadedInLandscape) {
		super();
		if(id != -1 && id < GameObjectDefinition.getDefinitions().length) {
			this.definition = GameObjectDefinition.forId(id);
		}
		this.id = id;
		this.spawnLocation = location;
		this.type = type;
		this.setDirection(direction);
		this.loadedInLandscape = loadedInLandscape;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Gets the definition.
	 * @return The definition.
	 */
	public GameObjectDefinition getDefinition() {
		return definition;
	}
	
	/**
	 * Gets the type.
	 * @return The type.
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * @return the loadedInLandscape
	 */
	public boolean isLoadedInLandscape() {
		return loadedInLandscape;
	}
	
	public void setLoadedInLandscape(boolean loadedInLandscape) {
		this.loadedInLandscape = loadedInLandscape;
	}

	/**
	 * @return the maxHealth
	 */
	public int getMaxHealth() {
		return maxHealth;
	}

	/**
	 * @param maxHealth the maxHealth to set
	 */
	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
		this.currentHealth = maxHealth;
	}

	/**
	 * @return the currentHealth
	 */
	public int getCurrentHealth() {
		return currentHealth;
	}

	/**
	 * @param currentHealth the currentHealth to set
	 */
	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
	}

	/**
	 * @param currentHealth the currentHealth to set
	 */
	public void decreaseCurrentHealth(int amount) {
		this.currentHealth -= amount;
	}

	@Override
	public Location getCentreLocation() {
		return Location.create(getLocation().getX() + (getWidth() / 2), getLocation().getY() + (getHeight() / 2), getLocation().getZ());
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	@Override
	public int getClientIndex() {
		return 0;
	}

	@Override
	public int getHeight() {
		if(definition == null) {
			return 1;
		}
		return definition.getSizeY();
	}

	@Override
	public int getWidth() {
		if(definition == null) {
			return 1;
		}
		return definition.getSizeX();
	}

	@Override
	public boolean isNPC() {
		return false;
	}

	@Override
	public boolean isObject() {
		return true;
	}

	@Override
	public boolean isPlayer() {
		return false;
	}

	@Override
	public void addToRegion(Region region) {
		region.addObject(this);
	}

	@Override
	public void removeFromRegion(Region region) {
		try {
			region.removeObject(this);
			this.setRegion(null);
		} catch(Exception e) {}
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof GameObject)) {
			return false;
		}
		GameObject obj = (GameObject) other;
		return obj.getLocation().equals(this.getLocation()) && obj.getId() == this.getId() && obj.getType() == this.getType();
	}

}
