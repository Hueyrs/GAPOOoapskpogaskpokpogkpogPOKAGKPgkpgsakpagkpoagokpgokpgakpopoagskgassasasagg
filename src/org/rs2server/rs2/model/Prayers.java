package org.rs2server.rs2.model;

import java.util.HashMap;
import java.util.Map;

import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.net.ActionSender.DialogueType;
import org.rs2server.rs2.tickable.impl.PrayerUpdateTick;


public class Prayers {
	
	/**
	 * Represents types of prayers.
	 * @author Scu11
	 *
	 */
	public static enum Prayer {

		/**
		 * Thick skin.
		 */
		THICK_SKIN(Prayers.THICK_SKIN, "Thick Skin", 1, 0, 83, 12),

		/**
		 * Burst of Strength.
		 */
		BURST_OF_STRENGTH(Prayers.BURST_OF_STRENGTH, "Burst of Strength", 4, 0, 84, 12),
		
		/**
		 * Clarity of Thought.
		 */
		CLARITY_OF_THOUGHT(Prayers.CLARITY_OF_THOUGHT, "Clarity of Thought", 7, 0, 85, 12),
		
		/**
		 * Rock Skin.
		 */
		ROCK_SKIN(Prayers.ROCK_SKIN, "Rock Skin", 10, 0, 86, 6),
		
		/**
		 * Superhuman Strength.
		 */
		SUPERHUMAN_STRENGTH(Prayers.SUPERHUMAN_STRENGTH, "Superhuman Strength", 13, 0, 87, 6),
		
		/**
		 * Improved Reflexes.
		 */
		IMPROVED_REFLEXES(Prayers.IMPROVED_REFLEXES, "Improved Reflexes", 16, 0, 88, 6),
		
		/**
		 * Rapid Restore.
		 */
		RAPID_RESTORE(Prayers.RAPID_RESTORE, "Rapid Restore", 19, 0, 89, 26),
		
		/**
		 * Rapid Heal.
		 */
		RAPID_HEAL(Prayers.RAPID_HEAL, "Rapid Heal", 22, 0, 90, 18),
		
		/**
		 * Protect Items.
		 */
		PROTECT_ITEMS(Prayers.PROTECT_ITEM, "Protect Items", 25, 0, 91, 18),
		
		/**
		 * Steel Skin.
		 */
		STEEL_SKIN(Prayers.STEEL_SKIN, "Steel Skin", 28, 0, 92, 3),
		
		/**
		 * Ultimate Strength.
		 */
		ULTIMATE_STRENGTH(Prayers.ULTIMATE_STRENGTH, "Ultimate Strength", 31, 0, 93, 3),
		
		/**
		 * Incredible Reflexes.
		 */
		INCREDIBLE_REFLEXES(Prayers.INCREDIBLE_REFLEXES, "Incredible Reflexes", 34, 0, 94, 3),
		
		/**
		 * Protect from Magic.
		 */
		PROTECT_FROM_MAGIC(Prayers.PROTECT_FROM_MAGIC, "Protect from Magic", 37, 2, 95, 3),
		
		/**
		 * Protect from Missiles.
		 */
		PROTECT_FROM_MISSILES(Prayers.PROTECT_FROM_MISSILES, "Protect from Missiles", 40, 1, 96, 3),
		
		/**
		 * Protect from Melee.
		 */
		PROTECT_FROM_MELEE(Prayers.PROTECT_FROM_MELEE, "Protect from Melee", 43, 0, 97, 3),
		
		/**
		 * Retribution.
		 */
		RETRIBUTION(Prayers.RETRIBUTION, "Retribution", 46, 3, 98, 12),
		
		/**
		 * Redemption.
		 */
		REDEMPTION(Prayers.REDEMPTION, "Redemption", 49, 5, 99, 6),
		
		/**
		 * Smite.
		 */
		SMITE(Prayers.SMITE, "Smite", 52, 4, 100, 2),
		
		/**
		 * Sharp Eye
		 */
		SHARP_EYE(Prayers.SHARP_EYE, "Sharp Eye", 8, 0, 862, 12),
		
		/**
		 * Mystic Will
		 */
		MYSTIC_WILL(Prayers.MYSTIC_WILL, "Mystic Will", 9, 0, 863, 12),
		
		/**
		 * Hawk Eye
		 */
		HAWK_EYE(Prayers.HAWK_EYE, "Hawk Eye", 26, 0, 864, 6),
		
		/**
		 * Mystic Lore
		 */
		MYSTIC_LORE(Prayers.MYSTIC_LORE, "Mystic Lore", 27, 0, 865, 6),
		
		/**
		 * Eagle Eye
		 */
		EAGLE_EYE(Prayers.EAGLE_EYE, "Eagle Eye", 44, 0, 866, 3),
		
		/**
		 * Mystic Might
		 */
		MYSTIC_MIGHT(Prayers.MYSTIC_MIGHT, "Mystic Might", 45, 0, 867, 3),
		
		/**
		 * Chivalry
		 */
		CHIVALRY(Prayers.CHIVALRY, "Chivalry", 60, 0, 1052, 1.5),
		
		/**
		 * Piety
		 */
		PIETY(Prayers.PIETY, "Piety", 70, 0, 1053, 1.5)
		
		
		;
		
		
		/**
		 * A map of prayer IDs.
		 */
		private static Map<Integer, Prayer> prayers = new HashMap<Integer, Prayer>();
		
		/**
		 * Gets a prayer by its ID.
		 * @param prayer The prayer id.
		 * @return The prayer, or <code>null</code> if the id is not a prayer.
		 */
		public static Prayer forId(int prayer) {
			return prayers.get(prayer);
		}
		
		/**
		 * Populates the prayer map.
		 */
		static {
			for(Prayer prayer : Prayer.values()) {
				prayers.put(prayer.id, prayer);
			}
		}

		/**
		 * The id of this prayer.
		 */
		private int id;


		/**
		 * The name of this prayer.
		 */
		private String name;
		
		/**
		 * The required level for this prayer.
		 */
		private int level;

		/**
		 * The client configuration for this prayer.
		 */
		private int config;

		/**
		 * The head icon for this prayer.
		 */
		private int icon;
		
		/**
		 * The amount of seconds it takes to drain one prayer point.
		 */
		private double drain;

		/**
		 * Creates the prayer.
		 * @param prayer The prayer id.
		 * @return 
		 */
		private Prayer(int id, String name, int level, int icon, int config, double drain) {
			this.id = id;
			this.name = name;
			this.level = level;
			this.config = config;
			this.icon = icon;
			this.drain = drain;
		}

		/**
		 * Gets the prayer id.
		 * @return The prayer id.
		 */
		public int getPrayerId() {
			return id;
		}

		/**
		 * Gets the prayer name.
		 * @return The prayer name.
		 */
		public String getPrayerName() {
			return name;
		}
		
		/**
		 * Gets the level required for this prayer.
		 * @return The level required for this prayer.
		 */
		public int getLevelRequired() {
			return level;
		}

		/**
		 * Gets the client configuration for this prayer.
		 * @return The client configuration for this prayer.
		 */
		public int getClientConfiguration() {
			return config;
		}

		/**
		 * Gets the head icon for this prayer.
		 * @return The head icon for this prayer.
		 */
		public int getHeadIcon() {
			return icon;
		}
		
		/**
		 * Gets the amount of prayer points this prayer drains every tick.
		 * @return The amount of prayer points this prayer drains every tick.
		 */
		public double getDrain() {
			return drain;
		}
	}

	/**
	 * Constants for the prayer numbers.
	 */
	public static final int THICK_SKIN = 0, BURST_OF_STRENGTH = 1, CLARITY_OF_THOUGHT = 2, SHARP_EYE = 3, MYSTIC_WILL = 4,
							ROCK_SKIN = 5, SUPERHUMAN_STRENGTH = 6, IMPROVED_REFLEXES = 7, RAPID_RESTORE = 8, RAPID_HEAL = 9,
							PROTECT_ITEM = 10, HAWK_EYE = 11, MYSTIC_LORE = 12, STEEL_SKIN = 13, ULTIMATE_STRENGTH = 14,
							INCREDIBLE_REFLEXES = 15, PROTECT_FROM_MAGIC = 16, PROTECT_FROM_MISSILES = 17, PROTECT_FROM_MELEE = 18,
							EAGLE_EYE = 19, MYSTIC_MIGHT = 20, RETRIBUTION = 21, REDEMPTION = 22, SMITE = 23, CHIVALRY = 24, PIETY = 25,
							NO_PRAYERS = 26;
	

	public static void activatePrayer(Player player, int id) {
		if(player.getCombatState().isDead()) {
			player.getActionSender().sendConfig(Prayer.forId(id).getClientConfiguration(), player.getCombatState().getPrayer(id) ? 1 : 0);
			return;
		}
		if(!player.canPray) {
			player.getActionSender().sendConfig(Prayer.forId(id).getClientConfiguration(), 0);
			player.getActionSender().sendMessage("Your prayers have been disabled.");
			return;
		}
		if(player.getSkills().getPrayerPoints() < 1) {
			return;
		}
		ActionSender action = player.getActionSender();
		if(BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "Fremennik Trials")) {
			player.getActionSender().sendConfig(Prayer.forId(id).getClientConfiguration(), 0);
			action.sendDialogue("", DialogueType.MESSAGE, -1, FacialAnimation.DEFAULT, "Skulgrimen's magic prevents you from using prayers.");
			return;
		}
		Prayer prayer = Prayer.forId(id);
		action.removeAllInterfaces();
		if(player.getSkills().getLevelForExperience(Skills.PRAYER) < prayer.getLevelRequired()) {
			action.sendDialogue("", DialogueType.MESSAGE, -1, FacialAnimation.DEFAULT, "You need a <col=FF>Prayer level of " + prayer.getLevelRequired() + " to use " + prayer.getPrayerName() + ".");
			return;
		}
		if(id == CHIVALRY) {
			if(player.getSkills().getLevelForExperience(Skills.DEFENCE) < 65) {
				action.sendDialogue("", DialogueType.MESSAGE, -1, FacialAnimation.DEFAULT, "You need a <col=FF>Defence level of 65 to use " + prayer.getPrayerName() + ".");
				return;
			}
		}
		if(id == PIETY) {
			if(player.getSkills().getLevelForExperience(Skills.DEFENCE) < 70) {
				action.sendDialogue("", DialogueType.MESSAGE, -1, FacialAnimation.DEFAULT, "You need a <col=FF>Defence level of 70 to use " + prayer.getPrayerName() + ".");
				return;
			}
		}
		player.getCombatState().setPrayer(id, !player.getCombatState().getPrayer(id));
		int[] deactivatePrayers = new int[0];
		if(player.getCombatState().getPrayer(id)) {
			if(player.getPrayerUpdateTick() == null) {
				player.setPrayerUpdateTick(new PrayerUpdateTick(player));
				World.getWorld().submit(player.getPrayerUpdateTick());
			}
			switch(id) {
			case Prayers.THICK_SKIN:
				deactivatePrayers = new int[] {
					Prayers.ROCK_SKIN, Prayers.STEEL_SKIN, Prayers.CHIVALRY, Prayers.PIETY			
				};
				break;
			case Prayers.BURST_OF_STRENGTH:
				deactivatePrayers = new int[] {
					Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.SUPERHUMAN_STRENGTH, Prayers.HAWK_EYE, 
					Prayers.MYSTIC_LORE, Prayers.ULTIMATE_STRENGTH, Prayers.EAGLE_EYE, Prayers.MYSTIC_MIGHT,
					Prayers.CHIVALRY, Prayers.PIETY
				};
				break;
			case Prayers.CLARITY_OF_THOUGHT:
				deactivatePrayers = new int[] {
					Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.IMPROVED_REFLEXES, Prayers.HAWK_EYE, 
					Prayers.MYSTIC_LORE, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE, Prayers.MYSTIC_MIGHT,
					Prayers.CHIVALRY, Prayers.PIETY
				};
				break;
			case Prayers.SHARP_EYE:
				deactivatePrayers = new int[] {
					Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT, Prayers.MYSTIC_WILL, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES,
					Prayers.HAWK_EYE, Prayers.MYSTIC_LORE, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE, Prayers.MYSTIC_MIGHT,
					Prayers.CHIVALRY, Prayers.PIETY
				};
				break;
			case Prayers.MYSTIC_WILL:
				deactivatePrayers = new int[] {
					Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT, Prayers.SHARP_EYE, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES,
					Prayers.HAWK_EYE, Prayers.MYSTIC_LORE, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE, Prayers.MYSTIC_MIGHT,
					Prayers.CHIVALRY, Prayers.PIETY
				};
				break;
			case Prayers.ROCK_SKIN:
				deactivatePrayers = new int[] {
					Prayers.THICK_SKIN, Prayers.STEEL_SKIN, Prayers.CHIVALRY, Prayers.PIETY			
				};
				break;
			case Prayers.SUPERHUMAN_STRENGTH:
				deactivatePrayers = new int[] {
					Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.BURST_OF_STRENGTH, Prayers.HAWK_EYE, 
					Prayers.MYSTIC_LORE, Prayers.ULTIMATE_STRENGTH, Prayers.EAGLE_EYE, Prayers.MYSTIC_MIGHT,
					Prayers.CHIVALRY, Prayers.PIETY
				};
				break;
			case Prayers.IMPROVED_REFLEXES:
				deactivatePrayers = new int[] {
					Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.CLARITY_OF_THOUGHT, Prayers.HAWK_EYE, 
					Prayers.MYSTIC_LORE, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE, Prayers.MYSTIC_MIGHT,
					Prayers.CHIVALRY, Prayers.PIETY
				};
				break;
			case Prayers.HAWK_EYE:
				deactivatePrayers = new int[] {
					Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT, Prayers.MYSTIC_WILL, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES,
					Prayers.SHARP_EYE, Prayers.MYSTIC_LORE, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE, Prayers.MYSTIC_MIGHT,
					Prayers.CHIVALRY, Prayers.PIETY
				};
				break;
			case Prayers.MYSTIC_LORE:
				deactivatePrayers = new int[] {
					Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT, Prayers.SHARP_EYE, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES,
					Prayers.HAWK_EYE, Prayers.MYSTIC_WILL, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE, Prayers.MYSTIC_MIGHT,
					Prayers.CHIVALRY, Prayers.PIETY
				};
				break;
			case Prayers.STEEL_SKIN:
				deactivatePrayers = new int[] {
					Prayers.THICK_SKIN, Prayers.ROCK_SKIN, Prayers.CHIVALRY, Prayers.PIETY			
				};
				break;
			case Prayers.ULTIMATE_STRENGTH:
				deactivatePrayers = new int[] {
					Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.BURST_OF_STRENGTH, Prayers.HAWK_EYE, 
					Prayers.MYSTIC_LORE, Prayers.SUPERHUMAN_STRENGTH, Prayers.EAGLE_EYE, Prayers.MYSTIC_MIGHT,
					Prayers.CHIVALRY, Prayers.PIETY
				};
				break;
			case Prayers.INCREDIBLE_REFLEXES:
				deactivatePrayers = new int[] {
					Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.CLARITY_OF_THOUGHT, Prayers.HAWK_EYE, 
					Prayers.MYSTIC_LORE, Prayers.IMPROVED_REFLEXES, Prayers.EAGLE_EYE, Prayers.MYSTIC_MIGHT,
					Prayers.CHIVALRY, Prayers.PIETY
				};
				break;
			case Prayers.EAGLE_EYE:
				deactivatePrayers = new int[] {
					Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT, Prayers.MYSTIC_WILL, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES,
					Prayers.SHARP_EYE, Prayers.MYSTIC_LORE, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.HAWK_EYE, Prayers.MYSTIC_MIGHT,
					Prayers.CHIVALRY, Prayers.PIETY
				};
				break;
			case Prayers.MYSTIC_MIGHT:
				deactivatePrayers = new int[] {
					Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT, Prayers.SHARP_EYE, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES,
					Prayers.HAWK_EYE, Prayers.MYSTIC_WILL, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE, Prayers.MYSTIC_LORE,
					Prayers.CHIVALRY, Prayers.PIETY
				};
				break;
			case Prayers.CHIVALRY:
				deactivatePrayers = new int[] {
					Prayers.THICK_SKIN, Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT, Prayers.SHARP_EYE, Prayers.MYSTIC_WILL,
					Prayers.ROCK_SKIN, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.HAWK_EYE, Prayers.MYSTIC_LORE,
					Prayers.STEEL_SKIN, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE, Prayers.MYSTIC_MIGHT,
					Prayers.PIETY
				};
				break;
			case Prayers.PIETY:
				deactivatePrayers = new int[] {
					Prayers.THICK_SKIN, Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT, Prayers.SHARP_EYE, Prayers.MYSTIC_WILL,
					Prayers.ROCK_SKIN, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.HAWK_EYE, Prayers.MYSTIC_LORE,
					Prayers.STEEL_SKIN, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE, Prayers.MYSTIC_MIGHT,
					Prayers.CHIVALRY
				};
				break;
			case Prayers.PROTECT_FROM_MAGIC:
				player.getCombatState().setPrayerHeadIcon(prayer.getHeadIcon());
				deactivatePrayers = new int[] {
					Prayers.PROTECT_FROM_MISSILES, Prayers.PROTECT_FROM_MELEE, Prayers.RETRIBUTION, Prayers.REDEMPTION, Prayers.SMITE
				};
				break;
			case Prayers.PROTECT_FROM_MISSILES:
				player.getCombatState().setPrayerHeadIcon(prayer.getHeadIcon());
				deactivatePrayers = new int[] {
					Prayers.PROTECT_FROM_MAGIC, Prayers.PROTECT_FROM_MELEE, Prayers.RETRIBUTION, Prayers.REDEMPTION, Prayers.SMITE
				};
				break;
			case Prayers.PROTECT_FROM_MELEE:
				player.getCombatState().setPrayerHeadIcon(prayer.getHeadIcon());
				deactivatePrayers = new int[] {
					Prayers.PROTECT_FROM_MAGIC, Prayers.PROTECT_FROM_MISSILES, Prayers.RETRIBUTION, Prayers.REDEMPTION, Prayers.SMITE
				};
				break;
			case Prayers.RETRIBUTION:
				player.getCombatState().setPrayerHeadIcon(prayer.getHeadIcon());
				deactivatePrayers = new int[] {
					Prayers.PROTECT_FROM_MAGIC, Prayers.PROTECT_FROM_MISSILES, Prayers.PROTECT_FROM_MELEE, Prayers.REDEMPTION, Prayers.SMITE
				};
				break;
			case Prayers.REDEMPTION:
				player.getCombatState().setPrayerHeadIcon(prayer.getHeadIcon());
				deactivatePrayers = new int[] {
					Prayers.PROTECT_FROM_MAGIC, Prayers.PROTECT_FROM_MISSILES, Prayers.PROTECT_FROM_MELEE, Prayers.RETRIBUTION, Prayers.SMITE
				};
				break;
			case Prayers.SMITE:
				player.getCombatState().setPrayerHeadIcon(prayer.getHeadIcon());
				deactivatePrayers = new int[] {
					Prayers.PROTECT_FROM_MAGIC, Prayers.PROTECT_FROM_MISSILES, Prayers.PROTECT_FROM_MELEE, Prayers.RETRIBUTION, Prayers.REDEMPTION
				};
				break;
			}
			for(int i : deactivatePrayers) {
				if(i != id) {
					deActivatePrayer(player, i);		
				}
			}
			for(int i = 0; i < player.getCombatState().getPrayers().length; i++) {
				Prayer prayer2 = Prayer.forId(i);
				if(player.getCombatState().getPrayer(i)) {
					player.getActionSender().sendConfig(prayer2.getClientConfiguration(), 1);
				} else {
					player.getActionSender().sendConfig(prayer2.getClientConfiguration(), 0);
				}
			}
		} else {
			switch(id) {
			case Prayers.PROTECT_FROM_MAGIC:
			case Prayers.PROTECT_FROM_MISSILES:
			case Prayers.PROTECT_FROM_MELEE:
			case Prayers.RETRIBUTION:
			case Prayers.REDEMPTION:
			case Prayers.SMITE:
				player.getCombatState().setPrayerHeadIcon(-1);
				break;
			}
			boolean prayersFound = false;
			for(int i = 0; i < player.getCombatState().getPrayers().length; i++) {
				if(player.getCombatState().getPrayer(i)) {
					prayersFound = true;
					break;
				}
			}
			if(!prayersFound) {
				if(player.getPrayerUpdateTick() != null) {
					player.getPrayerUpdateTick().stop();
					player.setPrayerUpdateTick(null);
				}
			}
			player.getActionSender().sendConfig(prayer.getClientConfiguration(), 0);
		}		
	}
	
	public static void deActivatePrayer(Player player, int id) {
		player.getCombatState().setPrayer(id, false);
		player.getActionSender().sendConfig(Prayer.forId(id).getClientConfiguration(), 0);
	}
	
	public static int prayers[] = { Prayers.BURST_OF_STRENGTH, Prayers.CHIVALRY, Prayers.CLARITY_OF_THOUGHT, Prayers.EAGLE_EYE,
			Prayers.HAWK_EYE, Prayers.IMPROVED_REFLEXES, Prayers.INCREDIBLE_REFLEXES, Prayers.MYSTIC_LORE,
			Prayers.MYSTIC_MIGHT, Prayers.MYSTIC_WILL, Prayers.PIETY, Prayers.PROTECT_FROM_MAGIC, Prayers.PROTECT_FROM_MELEE,
			Prayers.PROTECT_FROM_MISSILES, Prayers.PROTECT_ITEM, Prayers.RAPID_HEAL, Prayers.RAPID_RESTORE, Prayers.REDEMPTION,
			Prayers.RETRIBUTION, Prayers.ROCK_SKIN, Prayers.SHARP_EYE, Prayers.SMITE, Prayers.STEEL_SKIN,
			Prayers.SUPERHUMAN_STRENGTH, Prayers.THICK_SKIN, Prayers.ULTIMATE_STRENGTH };
}
