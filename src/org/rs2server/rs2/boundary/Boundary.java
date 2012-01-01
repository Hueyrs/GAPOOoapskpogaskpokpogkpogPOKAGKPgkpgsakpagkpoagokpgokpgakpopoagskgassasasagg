package org.rs2server.rs2.boundary;

import org.rs2server.rs2.model.Location;

/**
 * The bounds for setting certain areas such as pk zones
 * @author Sir Sean
 *
 */
public final class Boundary {

	/**
	 * The boundary name
	 */
	private final String name;
	
	/**
	 * The bottom left location
	 */
	private final Location bottomLeft;
	
	/**
	 * The top right location
	 */
	private final Location topRight;
	
	/**
	 * Sets the boundaries in the constructor
	 * @param buttonLeft The bottom left coordinates
	 * @param topRight The top right coordinates
	 */
	public Boundary(String name, Location bottonLeft, Location topRight) {
		this.name = name;
		this.bottomLeft = bottonLeft;
		this.topRight = topRight;
	}

	/**
	 * The boundary name
	 * @return The boundary name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the button left location
	 * @return the bottom left
	 */
	public Location getBottomLeft() {
		return bottomLeft;
	}

	/**
	 * Gets the top left location
	 * @return the top right
	 */
	public Location getTopRight() {
		return topRight;
	}

}
