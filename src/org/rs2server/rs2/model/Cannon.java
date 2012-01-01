package org.rs2server.rs2.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.tickable.Tickable;

/**
 * Represents a cannon in game
 * @author Michael
 *
 */
public class Cannon {
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	/**
	 * The player who owns this cannon.
	 */
	private Player player;
	
	/**
	 * The game object for the cannon.
	 */
	private GameObject gameObject;
	
	/**
	 * The parts added to this cannon.
	 */
	private List<Item> partsAdded;
	
	/**
	 * The facing state of this cannon.
	 */
	private FacingState facingState;
	
	/**
	 * The running tick.
	 */
	private Tickable runningTick;
	
	/**
	 * The amount of cannon balls currently loaded.
	 */
	private int cannonBalls = 0;
	
	/**
	 * Represents the states that this cannon can face.
	 * @author Michael
	 *
	 */
	private enum FacingState {
		NORTH(0, 515),
		NORTH_EAST(1, 516),
		EAST(2, 517),
		SOUTH_EAST(3, 518),
		SOUTH(4, 519),
		SOUTH_WEST(5, 520),
		WEST(6, 521),
		NORTH_WEST(7, 514);
		
		/**
		 * A map of ids to facing states.
		 */
		private static List<FacingState> facingStates = new ArrayList<FacingState>();
		
		/**
		 * Populates the facing state list.
		 */
		static {
			for(FacingState facingState : FacingState.values()) {
				facingStates.add(facingState);
			}
		}
		
		public static FacingState forId(int id) {
			for(FacingState facingState : facingStates) {
				if(facingState.getId() == id) {
					return facingState;
				}
			}
			return null;
		}
		
		/**
		 * The id of this facing state.
		 */
		private int id;
		
		/**
		 * The animation id this face performs.
		 */
		private int animationId;
		
		FacingState(int id, int animationId) {
			this.id = id;
			this.animationId = animationId;
		}

		public int getId() {
			return id;
		}

		public int getAnimationId() {
			return animationId;
		}
	}
	
	public static Item CANNON_BASE = new Item(6);
	
	public Cannon(Player player, Location location) {
		this.player = player;
		this.facingState = FacingState.NORTH;
		this.gameObject = new GameObject(location, 7, 10, 0, false);
		this.partsAdded = new ArrayList<Item>();
		partsAdded.add(CANNON_BASE);
		player.getInventory().remove(CANNON_BASE);
		World.getWorld().register(this.gameObject);
	}
	
	public void destroy() {
		World.getWorld().unregister(gameObject, true);
		for(Item item : partsAdded) {
			if(!player.getInventory().add(item)) {
				if(player.getBank().add(item)) {
					player.getActionSender().sendMessage("You don't have enough inventory space to pick up the " + item.getDefinition().getName().toLowerCase() + " so");
					player.getActionSender().sendMessage("it has been deposited into your bank.");
				}
				//uh oh
			}
		}
		if(cannonBalls > 0) {
			Item item = new Item(2, cannonBalls);
			if(!player.getInventory().add(item)) {
				if(player.getBank().add(item)) {
					player.getActionSender().sendMessage("You don't have enough inventory space to pick up the " + item.getDefinition().getName().toLowerCase() + " so");
					player.getActionSender().sendMessage("it has been deposited into your bank.");
				}
				//uh oh
			}
		}
		if(runningTick != null) {
			runningTick.stop();
		}
		player.setAttribute("cannon", null);
		player.getAttributes().remove("cannon");
	}
	
	public void fire() {
		if(runningTick != null) {
			//already running
			return;
		}
		if(cannonBalls < 1) {
			player.getActionSender().sendMessage("There are no cannonballs currently loaded.");
			return;
		}
		runningTick = new Tickable(1) {
			@Override
			public void execute() {
				if(cannonBalls < 1) {
					this.stop();
					runningTick = null;
					player.getActionSender().sendMessage("Your cannon has run out of ammunition.");
					return;
				}
				for(Region r : gameObject.getRegion().getSurroundingRegions()) {
					for(Player player : r.getPlayers()) {
						player.getActionSender().animateObject(gameObject, facingState.getAnimationId());
					}
				}
				
				int id = facingState.getId();
				if(id == 7) {
					id = -1;
				}				
				facingState = FacingState.forId(id + 1);
				

				int delay = 2;
				for(Region r : gameObject.getRegion().getSurroundingRegions()) {
					for(final NPC npc : r.getNpcs()) {
						if(cannonBalls < 1) {
							break;
						}
						if(delay > 3) {
							break;
						}
						int newDist = gameObject.getLocation().distanceToEntity(gameObject, npc);
						if(newDist <= 5 && newDist >= 1) {
							boolean canHit = false;
							int myX = gameObject.getCentreLocation().getX();
							int myY = gameObject.getCentreLocation().getY();
							int theirX = npc.getCentreLocation().getX();
							int theirY = npc.getCentreLocation().getY();
							switch(facingState) {
							case NORTH:
								if(theirY > myY && theirX >= myX - 1 && theirX <= myX + 1) {
									canHit = true;
								}
								break;
							case NORTH_EAST:
								if(theirX >= myX + 1 && theirY >= myY + 1) {
									canHit = true;
								}
								break;
							case EAST:
								if(theirX > myX && theirY >= myY - 1 && theirY <= myY + 1) {
									canHit = true;
								}
								break;
							case SOUTH_EAST:
								if(theirY <= myY - 1 && theirX >= myX + 1) {
									canHit = true;
								}
								break;
							case SOUTH:
								if(theirY < myY && theirX >= myX - 1 && theirX <= myX + 1) {
									canHit = true;
								}
								break;
							case SOUTH_WEST:
								if(theirX <= myX - 1 && theirY <= myY - 1) {
									canHit = true;
								}
								break;
							case WEST:
								if(theirX < myX && theirY >= myY - 1 && theirY <= myY + 1) {
									canHit = true;
								}
								break;
							case NORTH_WEST:
								if(theirX <= myX - 1 && theirY >= myY + 1) {
									canHit = true;
								}
								break;
							}
							if(!canHit) {
								continue;
							}
							if(player.getActiveCombatAction().canHit(player, npc, false, true)) {
								gameObject.playProjectile(Projectile.create(gameObject.getCentreLocation(), npc.getCentreLocation(), 53, 15 + (delay * 10), 50, 50 + (newDist * 10), 37, 37, npc.getProjectileLockonIndex(), 0, 96));
								cannonBalls--;
								delay += 1;
								World.getWorld().submit(new Tickable(delay) {
									@Override
									public void execute() {
										int damage = random.nextInt(30);
										player.getSkills().addExperience(Skills.RANGE, (4 * damage) * Constants.EXP_MODIFIER);
										npc.inflictDamage(new Hit(damage), player);
										npc.getActiveCombatAction().defend(player, npc, true);
										this.stop();
									}			
								});
							}
						}
					}
				}
			}			
		};
		World.getWorld().submit(runningTick);
	}
	
	public void addPart(Item item) {
		int id = -1;
		switch(item.getId()) {
		case 8:
			id = 8;
			break;
		case 10:
			id = 9;
			break;
		case 12:
			id = 6;
			break;
		}
		if(id != -1) {
			player.getInventory().remove(item);
			World.getWorld().unregister(gameObject, true);
			this.gameObject = new GameObject(gameObject.getLocation(), id, 10, 0, false);
			World.getWorld().register(this.gameObject);
			partsAdded.add(item);
		}
	}

	public GameObject getGameObject() {
		return gameObject;
	}

	public int getCannonBalls() {
		return cannonBalls;
	}

	public void addCannonBalls(int cannonBalls) {
		this.cannonBalls += cannonBalls;
	}
}
