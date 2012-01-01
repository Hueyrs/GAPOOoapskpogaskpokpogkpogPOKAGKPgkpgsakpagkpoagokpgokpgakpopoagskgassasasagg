package org.rs2server.rs2.model.skills;

import java.util.HashMap;
import java.util.Map;

import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.ItemDefinition;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;


public class Fletching extends ProductionAction {

	/**
	 * The amount of items to produce.
	 */
	private int productionCount;
	
	/**
	 * The log index.
	 */
	private int logIndex;
	
	/**
	 * The log we are fletching.
	 */
	private Log log;
	
	public Fletching(Mob mob, int productionCount, int logIndex, Log log) {
		super(mob);
		this.productionCount = productionCount;
		this.logIndex = logIndex;
		this.log = log;
	}
	
	/**
	 * Represents a type of log that is able to be fletched.
	 * @author Michael
	 *
	 */
	public static enum Log {
		
		NORMAL(1511, new int[] { /*52, */53, 841, 839, 9174 }, new int[] { 1, 5, 10, 9 }, new double[] { 5, 10, 20, 24 }),
		
		OAK(1521, new int[] { 843, 9177, 845 }, new int[] { 20, 24, 25 }, new double[] { 16.5, 88, 25 }),
		
		WILLOW(1519, new int[] { 849, 9181, 847 }, new int[] { 35, 39, 40 }, new double[] { 66.6, 128, 83}),
		
		MAPLE(1517, new int[] { 853, 9183, 851 }, new int[] { 50, 54, 55 }, new double[] { 100, 164, 116.6 }),
		
		YEW(1515, new int[] { 857, 9185, 855 }, new int[] { 65, 69, 70 }, new double[] { 135, 200, 150 }),
		
		MAGIC(1513, new int[] { 861, 859 }, new int[] { 80, 85 }, new double[] { 166.6, 183 });
		
		/**
		 * The id of the logs
		 */
		private int logId;
		
		/**
		 * @return the logId
		 */
		public int getLogId() {
			return logId;
		}

		/**
		 * The first item displayed on the fletching interface.
		 */
		private int[] item;

		/**
		 * The level required to fletch the first item on the fletching interface.
		 */
		private int[] level;

		/**
		 * The experience granted for the first item on the flteching interface.
		 */
		private double[] experience;

		/**
		 * A map of item ids to logs.
		 */
		private static Map<Integer, Log> logs = new HashMap<Integer, Log>();

		/**
		 * Gets a log by an item id.
		 * @param item The item id.
		 * @return The Log, or <code>null</code> if the object is not a log.
		 */
		public static Log forId(int item) {
			return logs.get(item);
		}

		/**
		 * Populates the log map.
		 */
		static {
			for (Log log : Log.values()) {
				logs.put(log.logId, log);
			}
		}
		
		private Log(int logId, int[] item, int[] level, double[] experience) {
			this.logId = logId;
			this.item = item;
			this.level = level;
			this.experience = experience;
		}

		/**
		 * @return the item
		 */
		public int[] getItem() {
			return item;
		}

		/**
		 * @return the level
		 */
		public int[] getLevel() {
			return level;
		}

		/**
		 * @return the experience
		 */
		public double[] getExperience() {
			return experience;
		}
	}
	
	public static enum ArrowTip {
		BRONZE(39, 882, 1, 2.6),
		
		IRON(40, 884, 15, 3.8),
		
		STEEL(41, 886, 30, 6.3),
		
		MITHRIL(42, 888, 45, 8.8),
		
		ADAMANT(43, 890, 60, 11.3),
		
		RUNE(44, 892, 75, 13.8)
		;
		
		/**
		 * The id 
		 */
		private int id;
		
		/**
		 * The reward;
		 */
		private int reward;
		
		/**
		 * The level required.
		 */
		private int levelRequired;
		
		/**
		 * The experience granted.
		 */
		private double experience;

		/**
		 * A map of item ids to arrow tips.
		 */
		private static Map<Integer, ArrowTip> arrowtips = new HashMap<Integer, ArrowTip>();

		/**
		 * Gets an arrow tip by an item id.
		 * @param item The item id.
		 * @return The ArrowTip, or <code>null</code> if the object is not a arrow tip.
		 */
		public static ArrowTip forId(int item) {
			return arrowtips.get(item);
		}

		/**
		 * Populates the log map.
		 */
		static {
			for (ArrowTip arrowtip : ArrowTip.values()) {
				arrowtips.put(arrowtip.id, arrowtip);
			}
		}
		
		private ArrowTip(int id, int reward, int levelRequired, double experience) {
			this.id = id;
			this.reward = reward;
			this.levelRequired = levelRequired;
			this.experience = experience;
		}

		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}

		/**
		 * @return the reward
		 */
		public int getReward() {
			return reward;
		}

		/**
		 * @return the levelRequired
		 */
		public int getLevelRequired() {
			return levelRequired;
		}

		/**
		 * @return the experience
		 */
		public double getExperience() {
			return experience;
		}
		
		
		
	}

	@Override
	public boolean canProduce() {
		return true;
	}

	@Override
	public Animation getAnimation() {
		return Animation.create(1248);
	}

	@Override
	public Item[] getConsumedItems() {
		return new Item[] { new Item(log.getLogId()) };
	}

	@Override
	public int getCycleCount() {
		return 3;
	}

	@Override
	public double getExperience() {
		return log.getExperience()[logIndex];
	}

	@Override
	public Graphic getGraphic() {
		return null;
	}

	@Override
	public String getLevelTooLowMessage() {
		return "You need a Fletching level of " + getRequiredLevel() + " to fletch this.";
	}

	@Override
	public int getProductionCount() {
		return productionCount;
	}

	@Override
	public int getRequiredLevel() {
		return log.getLevel()[logIndex];
	}

	@Override
	public Item[] getRewards() {
		return new Item[] { new Item(log.getItem()[logIndex], log.getItem()[logIndex] == 53 ? 15 : 1) };
	}

	@Override
	public int getSkill() {
		return Skills.FLETCHING;
	}

	@Override
	public String getSuccessfulProductionMessage() {
		String prefix = "a";
		String suffix = "";
		char first = ItemDefinition.forId(log.getItem()[logIndex]).getName().toLowerCase().charAt(0);
		if(first == 'a' || first == 'e' || first == 'i' || first == 'o' || first == 'u') {
			prefix = "an";
		}
		if(log.getItem()[logIndex] == 52) {
			prefix = "some";
			suffix = "s";
		}
		return "You successfully fletch " + prefix + " " + ItemDefinition.forId(log.getItem()[logIndex]).getName().toLowerCase() + "" + suffix + ".";
	}

}
