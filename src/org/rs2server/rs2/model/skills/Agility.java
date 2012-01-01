package org.rs2server.rs2.model.skills;

import java.util.ArrayList;
import java.util.List;

import org.rs2server.rs2.ScriptManager;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.tickable.Tickable;

public class Agility {

	/**
	 * Represents an agility obstacle.
	 * @author Michael
	 * @author Canownueasy
	 */
	public enum Obstacle {
		
		/**
		 * Al-Karid
		 */
		SHANTAY_PASS(4031, Location.create(3302, 3116, 0), 1, 0, "shantayPass"),
		
		/**
		 * Lumbridge shortcuts
		 */
		LUMBRIDGE_FENCE_JUMP(9300, Location.create(3240, 3335, 0), 1, 0.6, "jumpFence"),
		LUMBRIDGE_GRAVEYARD_GATE(9299, Location.create(3240, 3190, 0), 1, 0.6, "lumbridgeGate"),
		
		/**
		 * Edgeville shortcuts
		 */
		WILDERNESS_DITCH(23271, Location.create(0, 0, 0), 1, 0, "wildernessDitch"),
		
		/**
		 * Falador shortcuts
		 */
		
		FALADOR_WESTERN_CRUMBLING_WALL(11844, Location.create(2935, 3355, 0), 5, 0.5, "faladorCrumblingWall"),
		
		FALADOR_SOUTHERN_UNDERWALL_TUNNEL(9309, Location.create(2948, 3310, 0), 26, 0.8, "faladorUnderwallTunnel"),
		FALADOR_NORTHERN_UNDERWALL_TUNNEL(9310, Location.create(2948, 3312, 0), 26, 0.8, "faladorUnderwallTunnel"),
		
		/**
		 * Gnome obstacle course
		 */
		
		GNOME_COURSE_LOG_BALANCE(2295, Location.create(2474, 3435, 0), 1, 7, "gnomeLogBalance"),
		
		GNOME_COURSE_OBSTACLE_NET_1(2285, Location.create(2471, 3425, 0), 1, 8, "gnomeObstacleNet"),
		
		GNOME_COURSE_OBSTACLE_NET_2(2285, Location.create(2473, 3425, 0), 1, 8, "gnomeObstacleNet"),
		
		GNOME_COURSE_OBSTACLE_NET_3(2285, Location.create(2475, 3425, 0), 1, 8, "gnomeObstacleNet"),
		
		GNOME_COURSE_TREE_BRANCH(2313, Location.create(2473, 3422, 1), 1, 5, "gnomeTreeBranch"),
		
		GNOME_COURSE_BALANCE_ROPE(2312, Location.create(2478, 3420, 2), 1, 7, "gnomeBalanceRope"),
		
		GNOME_COURSE_TREE_BRANCH_2(2314, Location.create(2486, 3419, 2), 1, 5, "gnomeTreeBranch2"),
		
		GNOME_COURSE_OBSTACLE_NET_4(2286, Location.create(2483, 3426, 0), 1, 8, "gnomeObstacleNet2"),
		
		GNOME_COURSE_OBSTACLE_NET_5(2286, Location.create(2485, 3426, 0), 1, 8, "gnomeObstacleNet2"),
		
		GNOME_COURSE_OBSTACLE_NET_6(2286, Location.create(2487, 3426, 0), 1, 8, "gnomeObstacleNet2"),
		
		GNOME_COURSE_OBSTACLE_PIPE_1(154, Location.create(2484, 3431, 0), 1, 8, "gnomeObstaclePipe"),
		
		GNOME_COURSE_OBSTACLE_PIPE_2(154, Location.create(2487, 3431, 0), 1, 8, "gnomeObstaclePipe"),
	
		
		/**
		 * Barbarian agility course
		 */
		
		BARBARIAN_COURSE_OBSTACLE_PIPE(2287, Location.create(2552, 3559, 0), 35, 0, "barbarianObstaclePipe"),
		
		BARBARIAN_COURSE_ROPE_SWING(2282, Location.create(2551, 3550, 0), 35, 22, "barbarianRopeSwing"),
		
		BARBARIAN_COURSE_LOG_BALANCE(2294, Location.create(2550, 3546, 0), 35, 13.7, "barbarianLogBalance"),
		
		BARBARIAN_COURSE_OBSTACLE_NET(2284, Location.create(2538, 3545, 0), 35, 8.2, "barbarianObstacleNet"),
		
		BARBARIAN_COURSE_LEDGE(2302, Location.create(2535, 3547, 1), 35, 22, "barbarianLedge"),
		
		BARBARIAN_COURSE_CRUMBLING_WALL_1(1948, Location.create(2536, 3553, 0), 35, 13.7, "barbarianCrumblingWall1"),
		
		BARBARIAN_COURSE_CRUMBLING_WALL_2(1948, Location.create(2539, 3553, 0), 35, 13.7, "barbarianCrumblingWall2"),
		
		BARBARIAN_COURSE_CRUMBLING_WALL_3(1948, Location.create(2542, 3553, 0), 35, 13.7, "barbarianCrumblingWall3"),
		
		;
		
		/**
		 * The list of obstacles.
		 */
		private static List<Obstacle> obstacles = new ArrayList<Obstacle>();
		
		/**
		 * Populates the obstacle list
		 */
		static {
			for(Obstacle obstacle : Obstacle.values()) {
				obstacles.add(obstacle);
			}
		}
		
		public Obstacle forId(int id) {
			for(Obstacle obstacle : obstacles) {
				if(obstacle.getId() == id) {
					return obstacle;
				}
			}
			return null;
		}
		
		public static Obstacle forLocation(Location location) {
			for(Obstacle obstacle : obstacles) {
				if(obstacle.getLocation().equals(location)) {
					return obstacle;
				}
			}
			return null;
		}
		
		/**
		 * Object id.
		 */
		private int id;
		
		/**
		 * The location of this obstacle.
		 */
		private Location location;
		
		/**
		 * The level required to use this obstacle.
		 */
		private int levelRequired;
		
		/**
		 * The experience granted for tackling this obstacle.
		 */
		private double experience;
		
		/**
		 * The script that is executed for this obstacle.
		 */
		private String scriptString;

		private Obstacle(int id, Location location, int levelRequired, double experience, String scriptString) {
			this.id = id;
			this.location = location;
			this.levelRequired = levelRequired;
			this.experience = experience;
			this.scriptString = scriptString;
		}

		public int getId() {
			return id;
		}

		public Location getLocation() {
			return location;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public double getExperience() {
			return experience;
		}

		public String getScriptString() {
			return scriptString;
		}
	}
	
	public static void tackleObstacle(Player player, Obstacle obstacle, GameObject object) {
		if(player.getSkills().getLevelForExperience(Skills.AGILITY) < obstacle.getLevelRequired()) {
			player.getActionSender().removeAllInterfaces();
			player.getActionSender().sendString(210, 0, "You need an Agility level of " + obstacle.getLevelRequired() + " to tackle this obstacle.");
			player.getActionSender().sendChatboxInterface(210);
			player.playAnimation(Animation.create(-1));
			return;
		};
		player.setAttribute("busy", true);
		if(ScriptManager.getScriptManager().invokeWithFailTest(obstacle.getScriptString(), player, obstacle, object)) {
		} else {;
			player.removeAttribute("busy");
			player.getActionSender().sendMessage("Nothing interesting happens.");
		}
	}
	
	public static void forceMovement(final Player player, final Animation animation, final int[] forceMovement, int ticks, final boolean removeAttribute) {
		World.getWorld().submit(new Tickable(ticks) {
			@Override
			public void execute() {
				player.playAnimation(animation);
				player.setForceWalk(forceMovement, removeAttribute);
				player.getUpdateFlags().flag(UpdateFlag.FORCE_MOVEMENT);
				this.stop();
			}
		});
	}
	
	public static void forceTeleport(final Player player, final Animation animation, final Location newLocation, int ticksBeforeAnim, int ticks) {
		if(animation != null) {
			if(ticksBeforeAnim < 1) {
				player.playAnimation(animation);
			} else {
				World.getWorld().submit(new Tickable(ticksBeforeAnim) {
					@Override
					public void execute() {
						player.playAnimation(animation);
						this.stop();
					}
				});			
			}
		}
		World.getWorld().submit(new Tickable(ticks) {
			@Override
			public void execute() {
				player.setTeleportTarget(newLocation);
				player.removeAttribute("busy");
				this.stop();
			}
		});
	}
	
	public static void forceWalkingQueue(final Player player, final Animation animation, final int x, final int y, int delayBeforeMovement, final int ticks, final boolean removeAttribute) {
		final Animation originalWalkAnimation = player.getWalkAnimation();
		final Animation originalRunAnimation = player.getRunAnimation();
		final Animation originalStandAnimation = player.getStandAnimation();
		final Animation originalStandTurn = player.getStandTurnAnimation();
		final Animation originalTurn90cw = player.getTurn90ClockwiseAnimation();
		final Animation originalTurn90ccw = player.getTurn90CounterClockwiseAnimation();
		final Animation originalTurn180 = player.getTurn90CounterClockwiseAnimation();
		Tickable tick = new Tickable(delayBeforeMovement) {
			@Override
			public void execute() {
				
				if(animation != null) {
					player.setWalkAnimation(animation);
					player.setRunAnimation(animation);
					player.setStandAnimation(animation);
					player.setStandTurnAnimation(animation);
					player.setTurn90ClockwiseAnimation(animation);
					player.setTurn90CounterClockwiseAnimation(animation);
					player.setTurn180Animation(animation);
					player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				}
				
				player.getWalkingQueue().setRunningToggled(false);
				
				
				player.getWalkingQueue().reset();
				player.getWalkingQueue().addStep(x, y);
				player.getWalkingQueue().finish();
				
				World.getWorld().submit(new Tickable(ticks) {
					@Override
					public void execute() {
						player.setWalkAnimation(originalWalkAnimation);
						player.setRunAnimation(originalRunAnimation);
						player.setStandAnimation(originalStandAnimation);
						player.setTurn90ClockwiseAnimation(originalTurn90cw);
						player.setTurn90CounterClockwiseAnimation(originalTurn90ccw);
						player.setTurn180Animation(originalTurn180);
						player.setStandTurnAnimation(originalStandTurn);
						player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
						if(removeAttribute) {
							player.removeAttribute("busy");
						}
						this.stop();
					}
				});
				this.stop();
			}
		};
		if(delayBeforeMovement < 1) {
			tick.execute();
		} else {
			World.getWorld().submit(tick);
		}
	}
	
	public static void animateObject(final GameObject gameObject, final Animation animation, int ticks) {		
		Tickable tick = new Tickable(ticks) {
			@Override
			public void execute() {
				for(Region r : gameObject.getRegion().getSurroundingRegions()) {
					for(Player player : r.getPlayers()) {
						player.getActionSender().animateObject(gameObject, animation.getId());
					}
				}
				this.stop();
			}
		};
		if(tick.getTickDelay() >= 1) {
			World.getWorld().submit(tick);
		} else {
			tick.execute();
		}
	}
	
	public static void setRunningToggled(final Player player, boolean toggled, int ticks) {
		final boolean originalToggledState = player.getWalkingQueue().isRunningToggled();
		player.getWalkingQueue().setRunningToggled(toggled);
		Tickable tick = new Tickable(ticks) {
			@Override
			public void execute() {
				player.getWalkingQueue().setRunningToggled(originalToggledState);
				this.stop();
			}
		};
		if(tick.getTickDelay() >= 1) {
			World.getWorld().submit(tick);
		} else {
			tick.execute();
		}
	}
	
	public static void damage(final Player player, final int damage, int ticks) {		
		Tickable tick = new Tickable(ticks) {
			@Override
			public void execute() {
				int dmg = damage;
				if(dmg > player.getSkills().getLevel(Skills.HITPOINTS)) {
					dmg = player.getSkills().getLevel(Skills.HITPOINTS);
				}
				player.inflictDamage(new Hit(damage), player);
				this.stop();
			}
		};
		if(tick.getTickDelay() >= 1) {
			World.getWorld().submit(tick);
		} else {
			tick.execute();
		}
	}
}
