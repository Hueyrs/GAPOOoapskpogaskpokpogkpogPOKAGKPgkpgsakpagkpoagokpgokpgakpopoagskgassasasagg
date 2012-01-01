package org.rs2server.rs2.action;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.tickable.Tickable;


/**
 * Stores a queue of pending actions.
 * @author blakeman8192
 * @author Graham Edgecombe
 *
 */
public class ActionQueue {
	
	/**
	 * The mob who's action queue this is.
	 */
	private Mob mob;
	
	/**
	 * The tickable.
	 */
	private Tickable cycleTick;
	
	/**
	 * The maximum number of actions allowed to be queued at once, deliberately
	 * set to the size of the player's inventory.
	 */
	public static final int MAXIMUM_SIZE = Inventory.SIZE;
	
	/**
	 * A queue of <code>Action</code> objects.
	 */
	private Queue<Action> actions = new LinkedList<Action>();

	public Queue<Action> getActions() {
		return actions;
	}

	/**
	 * The default constructor.
	 * @param mob The mob.
	 */
	public ActionQueue(Mob mob) {
		this.mob = mob;
	}
	
	/**
	 * Adds an <code>Action</code> to the queue.
	 * @param action The action.
	 */
	public void addAction(Action action) {
		if(actions.size() >= MAXIMUM_SIZE) {
			return;
		}
		switch(action.getStackPolicy()) {
		case ALWAYS:
			break;
		case NEVER:
			clearAllActions();
			break;
		}
	    switch(action.getAnimationPolicy()) {
	    case RESET_NONE:
	    	break;
	    case RESET_ALL:
			mob.playAnimation(Animation.create(-1));
			mob.playGraphics(Graphic.create(-1));
			break;			    	
	    }
		if(actions.size() > 0) {
			Iterator<Action> it = actions.iterator();
			while(it.hasNext()) {
			    Action actionTickable = it.next();
			    switch(actionTickable.getStackPolicy()) {
			    case ALWAYS:
			    	break;
			    case NEVER:
			    	actionTickable.stop();
			    	it.remove();
			    	break;
			    }
			}
		}
		actions.add(action);
		/*
		 * This is now converted as a tickable. Why? Because before, the system would register a tickable
		 * that would execute a certain amount of ticks later. What did this mean? E.g.
		 * 
		 * Home teleport started. First emote begins, and the second emote is added as an Action, however it is
		 * already submitted as a tickable, but you should be able to cancel out of it before the emote begins.
		 * This system fixes that.
		 */
		if(cycleTick == null) {
			cycleTick = new Tickable(1) {
				@Override
				public void execute() {
					try {
					if(actions.size() > 0) {
						Iterator<Action> it = actions.iterator();
						while(it.hasNext()) {
						    Action actionTickable = it.next();
						    if(actionTickable.getCurrentTicks() > 0) {
						    	actionTickable.decreaseTicks(1);
						    }
						    if(actionTickable.getCurrentTicks() == 0) {
						    	actionTickable.execute();
						    	if(!actionTickable.isRunning()) {
						    		it.remove();
						    	} else {
						    		actionTickable.setCurrentTicks(actionTickable.getTicks());
						    	}
						    }
						}
					} else {
						cycleTick = null;
						this.stop();
					}
					} catch(Exception e) {}
				}				
			};
			World.getWorld().submit(cycleTick);
		}
	}
	
	/**
	 * Purges actions in the queue with a <code>WalkablePolicy</code> of <code>NON_WALKABLE</code>.
	 */
	public void clearRemovableActions() {
		boolean resetAnimations = false;
		if(actions.size() > 0) {
			Iterator<Action> it = actions.iterator();
			while(it.hasNext()) {
			    Action actionTickable = it.next();
				switch(actionTickable.getCancelPolicy()) {
				case ONLY_ON_WALK:
					break;
				case ALWAYS:
		    		actionTickable.stop();
					it.remove();
					break;
				}
				switch(actionTickable.getAnimationPolicy()) {
				case RESET_NONE:
					break;
				case RESET_ALL:
					resetAnimations = true;
					break;
				}
			}
		}
		if(actions.size() < 0) {
			if(cycleTick != null) {
				cycleTick.stop();
			}
			cycleTick = null;
		}
		if(resetAnimations) {
			mob.playAnimation(Animation.create(-1));
			mob.playGraphics(Graphic.create(-1));
		}
	}
	
	/**
	 * Clears the action queue and current action.
	 */
	public void clearAllActions() {
		if(cycleTick != null) {
			cycleTick.stop();
		}
		cycleTick = null;
		boolean resetAnimations = false;
		if(actions.size() > 0) {
			for(Action action : actions) {
				switch(action.getAnimationPolicy()) {
				case RESET_NONE:
					break;
				case RESET_ALL:
					resetAnimations = true;
					break;
				}
				action.stop();
			}
			actions = new LinkedList<Action>();
		}
		if(resetAnimations) {
			mob.playAnimation(Animation.create(-1));
			mob.playGraphics(Graphic.create(-1));
		}
	}

}
