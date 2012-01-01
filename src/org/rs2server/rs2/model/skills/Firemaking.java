package org.rs2server.rs2.model.skills;

import java.util.ArrayList;
import java.util.Random;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.GroundItem;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.tickable.Tickable;

/**
 * For the firemaking skill.
 * @author Canownueasy <tgpn1996@hotmail.com>
 */
public class Firemaking {
	
	private Player player;
	
	/**
	 * The player who is firemaking.
	 * @return player
	 */
	public Player getPlayer() {
		return player;
	}
	
	public Firemaking(Player player) {
		this.player = player;
	}
	
	private enum Log {
		
		NORMAL(1511, 1, 30),
		OAK(1521, 15, 60),
		WILLOW(1519, 30, 90),
		TEAK(6333, 35, 105),
		MAPLE(1517, 45, 135),
		MAHOGANY(6332, 50, 157.5),
		YEW(1515, 60, 202.5),
		MAGIC(1513, 75, 303.8);
		
		private int id, reqLevel;
		private double exp;
		
		/**
		 * The log item.
		 * @return new Item(id)
		 */
		public Item getItem() {
			return new Item(id);
		}
		
		/**
		 * The required level to light the log.
		 * @return reqLevel
		 */
		public int getRequiredLevel() {
			return reqLevel;
		}
		
		/**
		 * The experience one gains from lighting the log.
		 * @return exp
		 */
		public double getExperience() {
			return exp;
		}
		
		/**
		 * Constructs a new log.
		 * @param id The item id of the log.
		 * @param reqLevel The required level to light the log.
		 * @param exp The experience one gains from lighting the log.
		 */
		Log(int id, int reqLevel, double exp) {
			this.id = id;
			this.reqLevel = reqLevel;
			this.exp = exp;
		}

	}
	
	/**
	 * Lights a log.
	 * @param log The log to light.
	 */
	public void light(final Log log) {
		//First off, we grab the item from the log type.
		Item item = log.getItem();
		//Next we check the log's lighting requirements.
		if(log.getRequiredLevel() > player.getSkills().getLevel(Skills.FIREMAKING)) {
			String vowels[] = { "a", "e", "i", "o", "u" };
			for(String vowel : vowels) {
				String itemName = item.getDefinition().getName().trim().replaceAll("_", " ");
				player.getActionSender().sendMessage("You must have a Firemaking level of " + log.getRequiredLevel() + " to light " + (itemName.startsWith(vowel) ? "an" : "a") + " " + itemName + ".");
			}
			return;
		}
		for(GameObject obj : player.getRegion().getGameObjects()) {
			//We loop through all the objects in the player's region.
			if(obj != null && obj.getType() != 22 && obj.getLocation().equals(player.getLocation())) {
				//Here we make sure that the user is not on an object.
				//We exclude the objects in the type group 22 because they are decorations (grass, flowers, ect.)
				player.getActionSender().sendMessage("You cannot light a fire here.");
				//If the player is on top of an object, we send a server message to notify them.
				//Then we return back to stop any other code in the method from executing.
				return;
			}
		}
		//We remove the item from the player's inventory first.
		//This is done to prevent adding items without space.
		player.getInventory().remove(item);
		//Then the player does the lighting logs animation.
		player.playAnimation(Animation.create(733));
		//We construct a new ground item of the player's log, belonging to the player, at the player's location.
		final GroundItem groundItem = new GroundItem(player.getName(), item, player.getLocation());
		//We then register the ground item to the world for the player.
		World.getWorld().createGroundItem(groundItem, player);
		//To notify the player that he/she is attempting to light a fire, we send a server message.
		player.getActionSender().sendMessage("You attempt to light the logs.");
		//Now, we create a new event with the amount of time it takes to light the log as it's wait.
		World.getWorld().submit(new Event(lightDelay(log)) {
			@Override
			public void execute() {
				//First of all, we reset the player's animation.
				//This is done to stop the player from doing the firemaking emote.
				player.playAnimation(Animation.create(-1));
				//We check if the ground item is null.
				//If it is null, then that means the player has picked up the log.
				if(groundItem.equals(null)) {
					//We stop the event and return back to stop other code in the method from executing.
					this.stop();
					return;
				}
				//We unregister the ground item.
				World.getWorld().unregister(groundItem);
				//We construct a game object for the fire.
				final GameObject fire = new GameObject(player.getLocation(), 2732, 10, 0, false);
				//We now register the fire object to the world.
				World.getWorld().register(fire);
				//Because the fire is lit, we notify the player in a server message.
				player.getActionSender().sendMessage("The fire catches and the logs begin to burn.");
				//Next, we add the experience to the player.
				//We use an if statement that declares the method addExperience.
				//It will return back true (and code inside our if statement will execute) if the player gains a level.
				if(addExperience(log)) {
					//Since the player gained a level, we play the "fireworks" graphic.
					player.playGraphics(Graphic.create(199));
					//We then congratulate them via a server message.
					player.getActionSender().sendMessage("Congratulations! You have advanced a level in Firemaking to " 
							+ player.getSkills().getLevelForExperience(Skills.FIREMAKING) + ".");
				}
				//Now, we create a new tickable of 120 (which is near a minute & a half)
				//We do this to remove the fire, and add ashes.
				World.getWorld().submit(new Tickable(180) {
					@Override
					public void execute() {
						//We loop through all the players in the world because we want to register an ash for every player.
						for(Player players : World.getWorld().getPlayers()) {
							//We make sure the players of the world aren't disconnected or are facing other problems.
							if(players != null) {
								//We register an ash ground item, in the same location as the fire.
								World.getWorld().register(new GroundItem(players.getName(), new Item(592), fire.getLocation()), players);	
							}
						}
						//We unregister the fire from the world.
						World.getWorld().unregister(fire, true);
						this.stop();
					}
				});
				//We create a new arraylist to hold the bad direction names.
				ArrayList<String> badDirections = new ArrayList<String>();
				//We loop through all the objects in the player's region.
				for(GameObject obj : player.getRegion().getGameObjects()) {
					//We now make sure the object isn't type 22 (decorations) or type 0 (walls, ect)
					if(obj != null && obj.getType() != 22 && obj.getType() != 0) { 
						if(obj.getLocation().equals(Location.create(player.getLocation().getX() - 1, player.getLocation().getY())) && obj.getHeight() == player.getHeight()) {
							//We can't walk West, so we add the location to the list.
							badDirections.add("West");
						} else
						if(obj.getLocation().equals(Location.create(player.getLocation().getX(), player.getLocation().getY())) && obj.getHeight() == player.getHeight()) {
							//We can't walk North, so we add the location to the list.
							badDirections.add("North");
						} else
						if(obj.getLocation().equals(Location.create(player.getLocation().getX(), player.getLocation().getY())) && obj.getHeight() == player.getHeight()) {
							//We can't walk South, so we add the location to the list.
							badDirections.add("South");
						} else
						if(obj.getLocation().equals(Location.create(player.getLocation().getX(), player.getLocation().getY())) && obj.getHeight() == player.getHeight()) {
							//We can't walk East, so we add the location to the list.
							badDirections.add("East");
						}
					}
					//We create a new location and set it's value to null (which we will check later).
					Location newLocation = null;
					//If the bad directions list doesn't contain West, we set the location to one step West of the player's location.
					//We do this because West is the first location we want to walk to.
					if(!badDirections.contains("West")) {
						newLocation = Location.create(player.getLocation().getX() - 1, player.getLocation().getY());
					} else
					//If the bad directions list doesn't contain North, we set the location to one step North of the player's location.
					//We do this because North is the second location we want to walk to.
					if(!badDirections.contains("North")) {
						newLocation = Location.create(player.getLocation().getX(), player.getLocation().getY() + 1);
					} else
					//If the bad directions list doesn't contain South, we set the location to one step South of the player's location.
					//We do this because South is the third location we want to walk to.
					if(!badDirections.contains("South")) {
						newLocation = Location.create(player.getLocation().getX(), player.getLocation().getY() - 1);
					} else
					//If the bad directions list doesn't contain East, we set the location to one step East of the player's location.
					//We do this because East is the last location we want to walk to.
					if(!badDirections.contains("East")) {
						newLocation = Location.create(player.getLocation().getX() + 1, player.getLocation().getY());
					}
					//We clear the bad directions arraylist, why not?
					badDirections.clear();
					//We cannot walk anywhere. We know this because the newLocation location was not set.
					//We stop the event and return back to prevent other code in this method from executing.
					if(newLocation.equals(null)) {
						this.stop();
						return;
					}
					//We now walk the player to the new location's coordinates.
					player.getWalkingQueue().addStep(newLocation.getX(), newLocation.getY());
					player.getWalkingQueue().finish();
				}
				//We end the event, finishing the light task.
				this.stop();
			}
		});
		
	}
	
	/**
	 * Adds experience to the player for a specific log.
	 * @param log The log we will add experience for.
	 * @return true if the player has gained a level.
	 */
	private boolean addExperience(Log log) {
		int beforeLevel = player.getSkills().getLevelForExperience(Skills.FIREMAKING);
		player.getSkills().addExperience(Skills.FIREMAKING, log.getExperience());
		int afterLevel = player.getSkills().getLevelForExperience(Skills.FIREMAKING);
		if(afterLevel > beforeLevel) {
			return true;
		}
		return false;
	}
	
	/**
	 * Light delay for a specific log.
	 * @param log The log.
	 * @return The light delay.
	 */
	private int lightDelay(Log log) {
		return random(400, (int) ((Math.sqrt(log.getRequiredLevel() * 1000) * (99 - player.getSkills().getLevel(Skills.FIREMAKING)))));
	}
	
	/**
	 * Finds the log for an item.
	 * @param item The log item.
	 * @return The log for the item.
	 */
	public Log findLog(Item item) {
		switch(item.getDefinition().getId()) {
		case 1511:
			return Log.NORMAL;
		case 1521:
			return Log.OAK;
		case 1519:
			return Log.WILLOW;
		case 6333:
			return Log.TEAK;
		case 1517:
			return Log.MAPLE;
		case 6332:
			return Log.MAHOGANY;
		case 1515:
			return Log.YEW;
		case 1513:
			return Log.MAGIC;
		}
		return null;
	}
	
	/**
     * Returns a random integer with min as the inclusive
     * lower bound and max as the exclusive upper bound.
     *
     * @param min The inclusive lower bound.
     * @param max The exclusive upper bound.
     * @return Random integer min <= n < max.
     */
	private int random(int min, int max) {
		Random random = new Random();
		int n = Math.abs(max - min);
		return Math.min(min, max) + (n == 0 ? 0 : random.nextInt(n));
	}

}