package org.rs2server.rs2.action.impl;

import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;

/**
 * An action for arriving to a location before performing an action.
 *
 */
public class CoordinateAction extends Action {
	
	/**
	 * The action.
	 */
	private Action action;
	
	/**
	 * The distance required to be near the location.
	 */
	private int distance;
	
	/**
	 * The allocated width.
	 */
	private int width;

	/**
	 * The allocated height.
	 */
	private int height;
	
	/**
	 * The required location.
	 */
	private Location otherLocation;
	
	/**
	 * The other locations width.
	 */
	private int otherWidth;
	
	/**
	 * The other locations height.
	 */
	private int otherHeight;

	/**
	 * Creates the action.
	 * @param mob The mob.
	 * @param action The action.
	 * @param location The required location.
	 */
	public CoordinateAction(Mob mob, int width, int height, Location otherLocation, int otherWidth, int otherHeight, int distance, Action action) {
		super(mob, 0);
		this.action = action;
		this.distance = distance;
		this.width = width;
		this.height = height;
		this.otherLocation = otherLocation;
		this.otherWidth = otherWidth;
		this.otherHeight = otherHeight;
	}

	@Override
	public void execute() {
		if (getMob().getLocation().isWithinDistance(width, height, otherLocation, otherWidth, otherHeight, distance)) {
			getMob().getWalkingQueue().reset();
			getMob().getActionQueue().addAction(action);
			this.stop();
		}
	}

	@Override
	public AnimationPolicy getAnimationPolicy() {
		return AnimationPolicy.RESET_NONE;
	}

	@Override
	public CancelPolicy getCancelPolicy() {
		return CancelPolicy.ALWAYS;
	}

	@Override
	public StackPolicy getStackPolicy() {
		return StackPolicy.NEVER;
	}
}
