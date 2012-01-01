package org.rs2server.rs2.model.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rs2server.rs2.action.impl.HarvestingAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;


public class Mining extends HarvestingAction {

	/**
	 * The rock we are mining.
	 */
	private GameObject object;
	
	/**
	 * The pick axe we are using.
	 */
	private PickAxe pickaxe;
	
	/**
	 * The rock we are mining.
	 */
	private Rock rock;
	
	public Mining(Mob mob, GameObject object) {
		super(mob);
		this.object = object;
		this.rock = Rock.forId(object.getId());
	}
	
	/**
	 * Represents types of pick axes.
	 * @author Michael (Scu11)
	 *
	 */
	public static enum PickAxe {

		/**
		 * Rune pickaxe.
		 */
		RUNE(1275, 41, Animation.create(624)),

		/**
		 * Adamant pickaxe.
		 */
		ADAMANT(1271, 31, Animation.create(628)),

		/**
		 * Mithril pickaxe.
		 */
		MITHRIL(1273, 21, Animation.create(629)),

		/**
		 * Steel pickaxe.
		 */
		STEEL(1269, 11, Animation.create(627)),

		/**
		 * Iron pickaxe.
		 */
		IRON(1267, 5, Animation.create(626)),

		/**
		 * Bronze pickaxe.
		 */
		BRONZE(1265, 1, Animation.create(625));
		
		/**
		 * The item id of this pick axe.
		 */
		private int id;

		/**
		 * The level required to use this pick axe.
		 */
		private int level;
		
		/**
		 * The animation performed when using this pick axe.
		 */
		private Animation animation;

		/**
		 * A list of pick axes.
		 */
		private static List<PickAxe> pickaxes = new ArrayList<PickAxe>();
		
		/**
		 * Gets the list of pick axes.
		 * @return The list of pick axes.
		 */
		public static List<PickAxe> getPickaxes() {
			return pickaxes;
		}

		/**
		 * Populates the pick axe map.
		 */
		static {
			for(PickAxe pickaxe : PickAxe.values()) {
				pickaxes.add(pickaxe);
			}
		}
		
		private PickAxe(int id, int level, Animation animation) {
			this.id = id;
			this.level = level;
			this.animation = animation;
		}

		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}
		
		/**
		 * @return the level
		 */
		public int getRequiredLevel() {
			return level;
		}
		
		/**
		 * @return the animation
		 */
		public Animation getAnimation() {
			return animation;
		}
	}
	
	/**
	 * Represents types of rocks.
	 * @author Michael
	 *
	 */
	public static enum Rock {
		
		/**
		 * Clay ore.
		 */
		CLAY(434, 1, 5, 2, 1, new int[] { 2108, 2109 }, new int[] { 450, 451 }),		
		
		/**
		 * Copper ore.
		 */
		COPPER(436, 1, 17.5, 6, 1, new int[] { 2090, 2091}, new int[] { 450, 451 }),
		
		/**
		 * Tin ore.
		 */
		TIN(438, 1, 17.5, 6, 1, new int[] { 2094, 2095}, new int[] { 450, 451 }),
		
		/**
		 * Iron ore.
		 */
		IRON(440, 15, 35, 15, 1, new int[] { 2092, 2093 }, new int[] { 450, 451 }),
		
		/**
		 * Silver ore.
		 */
		SILVER(442, 20, 40, 100, 1, new int[] {2100, 2101 }, new int[] { 450, 451 }),
		
		/**
		 * Gold ore.
		 */
		GOLD(444, 40, 65, 100, 1, new int[] { 2098, 2099 }, new int[] { 450, 451 }),
		
		/**
		 * Coal ore.
		 */
		COAL(453, 30, 50, 50, 1, new int[] { 2096, 2097 }, new int[] { 450, 451 }),
		
		/**
		 * Mithril ore.
		 */
		MITHRIL(447, 55, 80, 200, 1, new int[] { 2102, 2103 }, new int[] { 450, 451 }),
		
		/**
		 * Adamantite ore.
		 */
		ADAMANTITE(449, 70, 95, 400, 1, new int[] { 2104, 2105 }, new int[] { 450, 451 }),
		
		/**
		 * Rune ore.
		 */
		RUNE(451, 85, 215, 1000, 1, new int[] { 2106, 2107 }, new int[] { 450, 451 })
		
		;
		
		/**
		 * The object ids of this rock.
		 */
		private int[] objects;
		
		/**
		 * The level required to mine this rock.
		 */
		private int level;
		
		/**
		 * The ore rewarded for each cut of the tree.
		 */
		private int log;
		
		/**
		 * The time it takes for this rock to respawn.
		 */
		private int respawnTimer;

		/**
		 * The amount of ores this rock contains.
		 */
		private int oreCount;

		/**
		 * The experience granted for mining this rock.
		 */
		private double experience;
		
		/**
		 * The rocks to replace.
		 */
		private int[] replacementRocks;
		
		/**
		 * A map of object ids to rocks.
		 */
		private static Map<Integer, Rock> rocks = new HashMap<Integer, Rock>();
		
		/**
		 * Gets a rock by an object id.
		 * @param object The object id.
		 * @return The rock, or <code>null</code> if the object is not a rock.
		 */
		public static Rock forId(int object) {
			return rocks.get(object);
		}
		
		static {
			for(Rock rock : Rock.values()) {
				for(int object : rock.objects) {
					rocks.put(object, rock);
				}
			}
		}

		/**
		 * Creates the tree.
		 * @param log The log id.
		 * @param level The required level.
		 * @param experience The experience per log.
		 * @param objects The object ids.
		 */
		private Rock(int log, int level, double experience, int respawnTimer, int oreCount, int[] objects, int[] replacementRocks) {
			this.objects = objects;
			this.level = level;
			this.experience = experience;
			this.respawnTimer = respawnTimer;
			this.oreCount = oreCount;
			this.log = log;
			this.replacementRocks = replacementRocks;
		}

		/**
		 * @return the replacementRocks
		 */
		public int[] getReplacementRocks() {
			return replacementRocks;
		}

		/**
		 * Gets the log id.
		 * 
		 * @return The log id.
		 */
		public int getOreId() {
			return log;
		}

		/**
		 * Gets the object ids.
		 * 
		 * @return The object ids.
		 */
		public int[] getObjectIds() {
			return objects;
		}

		/**
		 * Gets the required level.
		 * 
		 * @return The required level.
		 */
		public int getRequiredLevel() {
			return level;
		}

		/**
		 * Gets the experience.
		 * 
		 * @return The experience.
		 */
		public double getExperience() {
			return experience;
		}
		
		/**
		 * @return the respawnTimer
		 */
		public int getRespawnTimer() {
			return respawnTimer;
		}
		
		/**
		 * @return the oreCount
		 */
		public int getOreCount() {
			return oreCount;
		}
	}

	@Override
	public Animation getAnimation() {
		return pickaxe.getAnimation();
	}

	@Override
	public int getCycleCount() {
		int skill = getMob().getSkills().getLevel(getSkill());
		int level = rock.getRequiredLevel();
		int modifier = pickaxe.getRequiredLevel();
		double cycleCount = 1;
		cycleCount = Math.ceil((level * 50 - skill * 10) / modifier * 0.0625 * 4);
		if (cycleCount < 1) {
			cycleCount = 1;
		}
		return (int) cycleCount;
	}

	@Override
	public double getExperience() {
		return rock.getExperience();
	}

	@Override
	public GameObject getGameObject() {
		return object;
	}

	@Override
	public int getGameObjectMaxHealth() {
		return rock.getOreCount();
	}

	@Override
	public String getHarvestStartedMessage() {
		return "You swing your pick at the rock.";
	}

	@Override
	public String getLevelTooLowMessage() {
		return "You need a " + Skills.SKILL_NAME[getSkill()] + " level of " + rock.getRequiredLevel() + " to mine this rock.";
	}

	@Override
	public int getObjectRespawnTimer() {
		return rock.getRespawnTimer();
	}

	@Override
	public GameObject getReplacementObject() {
		int index = 0;
		for(int i = 0; i < rock.getObjectIds().length; i++) {
			if(rock.getObjectIds()[i] == getGameObject().getDefinition().getId()) {
				index = i;
				break;
			}
		}
		return new GameObject(getGameObject().getLocation(), rock.getReplacementRocks()[index], getGameObject().getType(), getGameObject().getDirection(), false);
	}

	@Override
	public int getRequiredLevel() {
		return rock.getRequiredLevel();
	}

	@Override
	public Item getReward() {
		return new Item(rock.getOreId(), 1);
	}

	@Override
	public int getSkill() {
		return Skills.MINING;
	}

	@Override
	public String getSuccessfulHarvestMessage() {
		return "You manage to mine some " + getReward().getDefinition().getName().toLowerCase().replaceAll(" ore", "") + ".";
	}

	@Override
	public boolean canHarvest() {
		for(PickAxe pickaxe : PickAxe.values()) {
			if((getMob().getInventory().contains(pickaxe.getId()) || getMob().getEquipment().contains(pickaxe.getId()))
							&& getMob().getSkills().getLevelForExperience(getSkill()) >= pickaxe.getRequiredLevel()) {
				this.pickaxe = pickaxe;
				break;
			}
		}
		if(pickaxe == null) {
			getMob().getActionSender().sendMessage("You do not have a pickaxe that you can use.");
			return false;
		}
		return true;
	}
	
	@Override
	public String getInventoryFullMessage() {
		return "Your inventory is too full to hold any more " + getReward().getDefinition().getName().toLowerCase().replaceAll(" ore", "") + ".";
	}

}
