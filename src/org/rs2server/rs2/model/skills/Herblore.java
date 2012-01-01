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


public class Herblore extends ProductionAction {
	
	/**
	 * The type of herblore action we are using.
	 */
	private HerbloreType herbloreType;
	
	/**
	 * The amount of times to produce this item.
	 */
	private int productionCount;

	/**
	 * The ingredient type.
	 */
	private PrimaryIngredient primaryIngredient;
	
	/**
	 * The secondary ingredient type.
	 */
	private SecondaryIngredient secondaryIngredient;

	public static enum HerbloreType {
		PRIMARY_INGREDIENT,
		SECONDARY_INGREDIENT
	}

	public Herblore(Mob mob, int productionCount, PrimaryIngredient primaryIngredient, SecondaryIngredient secondaryIngredient, HerbloreType herbloreType) {
		super(mob);
		this.herbloreType = herbloreType;
		this.productionCount = productionCount;
		this.primaryIngredient = primaryIngredient;
		this.secondaryIngredient = secondaryIngredient;
	}
	
	public static enum Herb {
		
		GUAM(199, 249, 3, 2.5),
		
		MARRENTILL(201, 251, 5, 3.8),
		
		TARROMIN(203, 253, 11, 5),
		
		HARRALANDER(205, 255, 20, 6.3),
		
		RANARR(207, 257, 25, 7.5),
		
		IRIT(209, 259, 40, 8.8),
		
		AVANTOE(211, 261, 48, 10),
		
		KWUARM(213, 263, 54, 11.3),
		
		CADANTINE(215, 265, 65, 12.5),
		
		DWARF_WEED(217, 267, 70, 13.8),
		
		TORSTOL(219, 269, 75, 15);
		
		/**
		 * The id of the herb
		 */
		private int id;

		/**
		 * The reward for identifying the herb.
		 */
		private int reward;

		/**
		 * The level required to identify this herb.
		 */
		private int level;

		/**
		 * The experience granted for identifying the herb.
		 */
		private double experience;

		/**
		 * A map of item ids to herbs.
		 */
		private static Map<Integer, Herb> herbs = new HashMap<Integer, Herb>();

		/**
		 * Gets a herb by an item id.
		 * @param item The item id.
		 * @return The <code>Herb</code> or <code>null</code> if the item is not a herb.
		 */
		public static Herb forId(int item) {
			return herbs.get(item);
		}

		/**
		 * Populates the herb map.
		 */
		static {
			for (Herb herb : Herb.values()) {
				herbs.put(herb.id, herb);
			}
		}
		
		private Herb(int id, int reward, int level, double experience) {
			this.id = id;
			this.reward = reward;
			this.level = level;
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
		 * @return the level
		 */
		public int getRequiredLevel() {
			return level;
		}

		/**
		 * @return the experience
		 */
		public double getExperience() {
			return experience;
		}
	}
	


	/**
	 * Represents types of primary ingredients.
	 * 
	 * @author Michael (Scu11).
	 * 
	 */
	public static enum PrimaryIngredient {
		/**
		 * Guam leaf.
		 */
		GUAM(249, 91, 3),
		
		/**
		 * Marrentill leaf.
		 */
		MARRENTILL(251, 93, 5),
		
		/**
		 * Tarromin leaf.
		 */
		TARROMIN(253, 95, 12),
		
		/**
		 * Harralander leaf.
		 */
		HARRALANDER(255, 97, 22),
		
		/**
		 * Ranarr leaf.
		 */
		RANARR(257, 99, 30),
		
		/**
		 * Irit leaf.
		 */
		IRIT(259, 101, 45),
		
		/**
		 * Avantoe leaf.
		 */
		AVANTOE(261, 103, 50),
		
		/**
		 * Kwuarm leaf.
		 */
		KWUARM(263, 105, 55),
		
		/**
		 * Cadantine leaf.
		 */
		CADANTINE(265, 107, 66),
		
		/**
		 * Dwarf Weed leaf.
		 */
		DWARF_WEED(267, 109, 72),
		
		/**
		 * Torstol leaf.
		 */
		TORSTOL(269, 111, 78),
		
		/**
		 * Toadflax leaf.
		 */
		TOADFLAX(2998, 3002, 34),
		
		/**
		 * Snapdragon leaf.
		 */
		SNAPDRAGON(3000, 3004, 63)
		;

		/**
		 * The id.
		 */
		private int id;

		/**
		 * The reward.
		 */
		private int reward;
		
		/**
		 * The level.
		 */
		private int level;

		/**
		 * A map of object ids to primary ingredients.
		 */
		private static Map<Integer, PrimaryIngredient> ingredients = new HashMap<Integer, PrimaryIngredient>();

		/**
		 * Gets a log by an item id.
		 * 
		 * @param item
		 *            The item id.
		 * @return The PrimaryIngredient, or <code>null</code> if the object is not a PrimaryIngredient.
		 */
		public static PrimaryIngredient forId(int item) {
			return ingredients.get(item);
		}

		/**
		 * Populates the log map.
		 */
		static {
			for (PrimaryIngredient ingredient : PrimaryIngredient.values()) {
				ingredients.put(ingredient.id, ingredient);
			}
		}
		
		private PrimaryIngredient(int id, int reward, int level) {
			this.id = id;
			this.level = level;
			this.reward = reward;
		}

		/**
		 * Gets the id.
		 * 
		 * @return The id.
		 */
		public int getId() {
			return id;
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
		 * Gets the reward.
		 * 
		 * @return The reward.
		 */
		public int getReward() {
			return reward;
		}

	}

	/**
	 * Represents types of secondary ingredients.
	 * 
	 * @author Michael (Scu11).
	 * 
	 */
	public static enum SecondaryIngredient {
		/**
		 * Eye of Newt - Attack Potion.
		 */
		EYE_OF_NEWT(1, 221, new Item(91, 1), 121, 3, 25),
		
		/**
		 * Unicorn Horn Dust - Antipoison.
		 */
		UNICORN_HORN_DUST(2, 235, new Item(93, 1), 175, 5, 37),
		
		/**
		 * Limpwurt Root - Strength Potion.
		 */
		LIMPWURT_ROOT(3, 225, new Item(95, 1), 115, 12, 50),
		
		/**
		 * Red Spider's Eggs - Restore Potion.
		 */
		RED_SPIDERS_EGGS(4, 223, new Item(97, 1), 121, 22, 62),
		
		/**
		 * Chocolate Dust - Energy Potion.
		 */
		CHOCOLATE_DUST(5, 1975, new Item(97, 1), 3010, 26, 67),
		
		/**
		 * White Berries - Defence Potion.
		 */
		WHITE_BERRIES(6, 239, new Item(99, 1), 133, 30, 75),
		
		/**
		 * Snape Grass - Prayer Potion.
		 */
		SNAPE_GRASS(7, 231, new Item(99, 1), 139, 38, 87),
		
		/**
		 * Eye of Newt - Super Attack Potion.
		 */
		EYE_OF_NEWT_2(8, 221, new Item(101, 1), 145, 45, 100),
		
		/**
		 * Mort Myre Fungi - Super Energy Potion.
		 */
		MORT_MYRE_FUNGI(9, 2970, new Item(103, 1), 3018, 52, 117),
		
		/**
		 * Limpwurt Root - Super Strength Potion.
		 */
		LIMPWURT_ROOT_2(10, 255, new Item(105, 1), 157, 55, 125),
		
		/**
		 * Red Spider's Eggs - Super Restore Potion.
		 */
		RED_SPIDERS_EGGS_2(11, 223, new Item(127, 1), 3026, 63, 142),
		
		/**
		 * White Berries - Super Defence Potion.
		 */
		WHITE_BERRIES_2(12, 239, new Item(107, 1), 163, 66, 150),
		
		/**
		 * Wine of Zamorak - Ranging Potion.
		 */
		WINE_OF_ZAMORAK(13, 245, new Item(109, 1), 169, 72, 162),
		
		/**
		 * Jangerberries - Zamorak Brew.
		 */
		JANGERBERRIES(14, 247, new Item(111, 1), 189, 78, 175),
		
		/**
		 * Crushed Bird Nest - Saradomin Brew.
		 */
		CRUSHED_BIRD_NEST(15, 6693, new Item(2998, 1), 6687, 81, 180)
		;

		/**
		 * The id.
		 */
		private int id;

		/**
		 * The index.
		 */
		private int index;
		
		/**
		 * The requiredItem.
		 */
		private Item requiredItem;

		/**
		 * The reward.
		 */
		private int reward;
		
		/**
		 * The level.
		 */
		private int level;
		
		/**
		 * The experience.
		 */
		private int exp;

		/**
		 * A map of object ids to primary ingredients.
		 */
		private static Map<Integer, SecondaryIngredient> ingredients = new HashMap<Integer, SecondaryIngredient>();

		/**
		 * Gets a log by an item id.
		 * 
		 * @param item
		 *            The item id.
		 * @return The PrimaryIngredient, or <code>null</code> if the object is not a PrimaryIngredient.
		 */
		public static SecondaryIngredient forId(int item) {
			return ingredients.get(item);
		}

		/**
		 * Populates the log map.
		 */
		static {
			for (SecondaryIngredient ingredient : SecondaryIngredient.values()) {
				ingredients.put(ingredient.index, ingredient);
			}
		}

		private SecondaryIngredient(int index, int id, Item requiredItem, int reward, int level, int exp) {
			this.index = index;
			this.id = id;
			this.requiredItem = requiredItem;
			this.level = level;
			this.reward = reward;
			this.exp = exp;
		}

		/**
		 * Gets the id.
		 * 
		 * @return The id.
		 */
		public int getId() {
			return id;
		}

		/**
		 * Gets the index.
		 * 
		 * @return The index.
		 */
		public int getIndex() {
			return index;
		}

		/**
		 * Gets the required id.
		 * 
		 * @return The required id.
		 */
		public Item getRequiredItem() {
			return requiredItem;
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
		 * Gets the reward.
		 * 
		 * @return The reward.
		 */
		public int getReward() {
			return reward;
		}

		/**
		 * Gets the exp.
		 * 
		 * @return The exp.
		 */
		public int getExperience() {
			return exp;
		}

	}

	@Override
	public boolean canProduce() {
		return true;
	}

	@Override
	public Animation getAnimation() {
		return Animation.create(363);
	}

	@Override
	public Item[] getConsumedItems() {
		switch(herbloreType) {
		case PRIMARY_INGREDIENT:
			return new Item[] { new Item(primaryIngredient.getId()), new Item(227) };
		case SECONDARY_INGREDIENT:
			return new Item[] { new Item(secondaryIngredient.getId()), secondaryIngredient.getRequiredItem() };
		}
		return null;
	}

	@Override
	public int getCycleCount() {
		return 4;
	}

	@Override
	public double getExperience() {
		switch(herbloreType) {
		case PRIMARY_INGREDIENT:
			return 0;
		case SECONDARY_INGREDIENT:
			return secondaryIngredient.getExperience();
		}
		return 0;
	}

	@Override
	public Graphic getGraphic() {
		return null;
	}

	@Override
	public String getLevelTooLowMessage() {
		return "You need a " + Skills.SKILL_NAME[getSkill()] + " level of " + getRequiredLevel() + " to combine these ingredients.";
	}

	@Override
	public int getProductionCount() {
		return productionCount;
	}

	@Override
	public int getRequiredLevel() {
		return herbloreType == HerbloreType.PRIMARY_INGREDIENT ? primaryIngredient.getRequiredLevel() : secondaryIngredient.getRequiredLevel();
	}

	@Override
	public Item[] getRewards() {
		switch(herbloreType) {
		case PRIMARY_INGREDIENT:
			return new Item[] { new Item(primaryIngredient.getReward()) };
		case SECONDARY_INGREDIENT:
			return new Item[] { new Item(secondaryIngredient.getReward()) };
		}
		return null;
	}

	@Override
	public int getSkill() {
		return Skills.HERBLORE;
	}

	@Override
	public String getSuccessfulProductionMessage() {
		switch(herbloreType) {
		case PRIMARY_INGREDIENT:
			return "You put the " + ItemDefinition.forId(primaryIngredient.getId()).getName().replaceAll(" clean", "").replaceAll(" leaf", "").toLowerCase() + " into the vial of water.";
		case SECONDARY_INGREDIENT:
			return "You mix the " + ItemDefinition.forId(secondaryIngredient.getId()).getName().toLowerCase() + " into your potion.";
		}
		return "";
	}

}
