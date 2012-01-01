package org.rs2server.rs2.model;

import java.util.ArrayList;
import java.util.List;

import org.rs2server.rs2.model.CombatNPCDefinition.Skill;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.skills.Prayer;


/**
 * Represents a mob's skill and experience levels.
 * @author Graham Edgecombe
 *
 */
public class Skills {
	
	/**
	 * The number of skills.
	 */
	public static final int SKILL_COUNT = 23;
	
	/**
	 * The largest allowed experience.
	 */
	public static final double MAXIMUM_EXP = 200000000;
	
	/**
	 * The skill names.
	 */
	public static final String[] SKILL_NAME	= { "Attack", "Defence",
		"Strength", "Hitpoints", "Range", "Prayer",
		"Magic", "Cooking", "Woodcutting", "Fletching",
		"Fishing", "Firemaking", "Crafting", "Smithing",
		"Mining", "Herblore", "Agility", "Thieving",
		"Slayer", "Farming", "Runecrafting", "Hunter", "Construction" };
	
	/**
	 * Constants for the skill numbers.
	 */
	public static final int	ATTACK	= 0, DEFENCE = 1, STRENGTH = 2,
		HITPOINTS = 3, RANGE = 4, PRAYER = 5, MAGIC = 6,
		COOKING = 7, WOODCUTTING = 8, FLETCHING = 9,
		FISHING = 10, FIREMAKING = 11, CRAFTING = 12,
		SMITHING = 13, MINING = 14, HERBLORE = 15,
		AGILITY = 16, THIEVING = 17, SLAYER = 18,
		FARMING = 19, RUNECRAFTING = 20, HUNTER = 21, CONSTRUCTION = 22;
	
	public Prayer getPrayer() {
		return new Prayer((Player) mob);
	}
	
	/**
	 * The mob object.
	 */
	private Mob mob;
	
	/**
	 * The levels array.
	 */
	private int[] levels = new int[SKILL_COUNT];
	
	/**
	 * The experience array.
	 */
	private double[] exps = new double[SKILL_COUNT];
	
	/**
	 * Prayer points are stored in a double, so to avoid making every skill a double, we simply use prayerPoints.
	 */
	private double prayerPoints;

	/**
	 * Creates a skills object.
	 * @param mob The mob whose skills this object represents.
	 */
	public Skills(Mob mob) {
		this.mob = mob;
		for(int i = 0; i < SKILL_COUNT; i++) {
			levels[i] = 1;
			exps[i] = 0;
		}
		levels[3] = 10;
		exps[3] = 1184;
		setPrayerPoints(1);
	}
	
	/**
	 * Gets the total level.
	 * @return The total level.
	 */
	public int getTotalLevel() {
		int total = 0;
		for(int i = 0; i < levels.length; i++) {
			total += getLevelForExperience(i);
		}
		return total;
	}
	
	/**
	 * Gets the experience for a requested level.
	 * @param lvl The level we're getting the experience amount for.
	 * @return Minimum experience required for that level.
	 */
	
	/**
	 * Gets the combat level.
	 * @return The combat level.
	 */
	public int getCombatLevel() {
		if(mob.isNPC()) {
			NPC npc = (NPC) mob;
			return npc.getDefinition().getCombatLevel();
		}
		final int attack = getLevelForExperience(0);
		final int defence = getLevelForExperience(1);
		final int strength = getLevelForExperience(2);
		final int hp = getLevelForExperience(3);
		final int prayer = getLevelForExperience(5);
		final int ranged = getLevelForExperience(4);
		final int magic = getLevelForExperience(6);
		int combatLevel = 3;	
		combatLevel = (int) ((defence + hp + Math.floor(prayer / 2)) * 0.2535) + 1;
		final double melee = (attack + strength) * 0.325;
		final double ranger = Math.floor(ranged * 1.5) * 0.325;
		final double mage = Math.floor(magic * 1.5) * 0.325;
		if (melee >= ranger && melee >= mage) {
			combatLevel += melee;
		} else if (ranger >= melee && ranger >= mage) {
			combatLevel += ranger;
		} else if (mage >= melee && mage >= ranger) {
			combatLevel += mage;
		}
		if(combatLevel <= 126) {
			return combatLevel;
		} else {
			return 126;
		}
	}
	
	/**
	 * Sets a skill.
	 * @param skill The skill id.
	 * @param level The level.
	 * @param exp The experience.
	 */
	public void setSkill(int skill, int level, double exp) {
		levels[skill] = level;
		exps[skill] = exp;
		if(skill == Skills.PRAYER) {
			setPrayerPoints(level);
		}
	}
	
	/**
	 * Sets a level.
	 * @param skill The skill id.
	 * @param level The level.
	 */
	public void setLevel(int skill, int level) {
		levels[skill] = level;
		if(mob.getActionSender() != null) {
			mob.getActionSender().sendSkill(skill);
		}
	}
	/**
	 * Sets experience.
	 * @param skill The skill id.
	 * @param exp The experience.
	 */
	public void setExperience(int skill, double exp) {
		int oldLvl = getLevelForExperience(skill);
		exps[skill] = exp;
		if(mob.getActionSender() != null) {
			mob.getActionSender().sendSkill(skill);
		}
		int newLvl = getLevelForExperience(skill);
		if(oldLvl != newLvl) {
			//TODO: Implement level-up notifications
			mob.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			if(mob.isPlayer()) {
				Player player = (Player) mob;
				player.checkForSkillcapes();
				int dialogueId = skill + 76;
				if(dialogueId != -1) {
					DialogueManager.openDialogue(player, dialogueId);
				}
			}
		}
	}
	
	/**
	 * Increments a level.
	 * @param skill The skill to increment.
	 */
	public void incrementLevel(int skill) {
		levels[skill]++;
		if(mob.getActionSender() != null) {
			mob.getActionSender().sendSkill(skill);
		}
	}
	
	/**
	 * Increases a level.
	 * @param skill The skill to increase.
	 * @param amount The amount to increase the skill by.
	 */
	public void increaseLevel(int skill, int amount) {
		levels[skill] += amount;
		if(mob.getActionSender() != null) {
			mob.getActionSender().sendSkill(skill);
		}
	}
	
	/**
	 * Decrements a level.
	 * @param skill The skill to decrement.
	 */
	public void decrementLevel(int skill) {
		levels[skill]--;
		if(mob.getActionSender() != null) {
			mob.getActionSender().sendSkill(skill);
		}
	}
	
	/**
	 * Decreases a level.
	 * @param skill The skill to decrease.
	 * @param amount The amount to decrease the skill by.
	 */
	public void decreaseLevel(int skill, int amount) {
		levels[skill] -= amount;
		if(mob.getActionSender() != null) {
			mob.getActionSender().sendSkill(skill);
		}
	}
	
	/**
	 * Decreases a level.
	 * @param skill The skill to decrease.
	 * @param amount The amount to decrease the skill by.
	 */
	public void decreaseLevelOnce(int skill, int amount) {
		if(levels[skill] > (getLevelForExperience(skill) - amount)) { //this stops resetting levels. EG if I have 87/95 str and it decreases 5, normally it would reset it to 90/95, however this line prevents it!
			if(levels[skill] - amount <= (getLevelForExperience(skill) - amount)) {
				levels[skill] = (getLevelForExperience(skill) - amount);
			} else {
				levels[skill] -= amount;			
			}
			if(mob.getActionSender() != null) {
				mob.getActionSender().sendSkill(skill);
			}
		}
	}
	
	/**
	 * Detracts a given level a given amount.
	 * @param skill The level to detract.
	 * @param amount The amount to detract from the level.
	 */
	public void detractLevel(int skill, int amount) {
		if(levels[skill] == 0) {
			amount = 0;
		}
		if(amount > levels[skill]) {
			amount = levels[skill];
		}
		levels[skill] = levels[skill] - amount;
		if(mob.getActionSender() != null) {
			mob.getActionSender().sendSkill(skill);
		}
	}
	
	/**
	 * Normalizes a level (adjusts it until it is at its normal value).
	 * @param skill The skill to normalize.
	 */
	public void normalizeLevel(int skill) {
		int norm = getLevelForExperience(skill);
		if(levels[skill] > norm) {
			levels[skill]--;
			if(mob.getActionSender() != null) {
				mob.getActionSender().sendSkill(skill);
			}
		} else if(levels[skill] < norm) {
			levels[skill]++;
			if(mob.getActionSender() != null) {
				mob.getActionSender().sendSkill(skill);
			}
		}
	}
	
	/**
	 * Gets a level.
	 * @param skill The skill id.
	 * @return The level.
	 */
	public int getLevel(int skill) {
		return levels[skill];
	}
	
	/**
	 * Gets a level by experience.
	 * @param skill The skill id.
	 * @return The level.
	 */
	public int getLevelForExperience(int skill) {
		if(mob.isNPC()) {
			NPC npc = (NPC) mob;
			Skill skillLvl = Skill.skillForId(skill);
			if(skillLvl != null) {
				if(npc.getCombatDefinition() != null) {
					if(npc.getCombatDefinition().getSkills() != null) {
						if(npc.getCombatDefinition().getSkills().containsKey(skillLvl)) {
							return npc.getCombatDefinition().getSkills().get(skillLvl);
						}
					}
				}
			}
		}
		double exp = exps[skill];
		int points = 0;
		int output = 0;

		for (int lvl = 1; lvl <= 99; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			output = (int) Math.floor(points / 4);
			if (output > exp)
				return lvl;
		}
		return 99;
	}
	
	/**
	 * Gets a experience from the level.
	 * @param level The level.
	 * @return The experience.
	 */
	public double getExperienceForLevel(int level) {
		double points = 0;
		double output = 0;
		for (int lvl = 1; lvl <= level; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			if (lvl >= level) {
				return output;
			}
			output = (int) Math.floor(points / 4);
		}
		return 0;
	}
	
	/**
	 * Gets experience.
	 * @param skill The skill id.
	 * @return The experience.
	 */
	public double getExperience(int skill) {
		return exps[skill];
	}
	
	/**
	 * Adds experience.
	 * @param skill The skill.
	 * @param exp The experience to add.
	 */
	public void addExperience(int skill, double exp) {
		int oldLevel = getLevelForExperience(skill);
		exps[skill] += exp;
		if(exps[skill] > MAXIMUM_EXP) {
			exps[skill] = MAXIMUM_EXP;
		}
		int newLevel = getLevelForExperience(skill);
		int levelDiff = newLevel - oldLevel;
		if(levelDiff > 0) {
			levels[skill] += levelDiff;
			mob.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		}
		if(mob.getActionSender() != null) {
			mob.getActionSender().sendSkill(skill);
		}
	}
	
	/**
	 * Resets all the statistics to their default level.
	 */
	public void resetStats() {
		for(int i = 0; i < levels.length; i++) {
			levels[i] = getLevelForExperience(i);
		}
		setPrayerPoints(getLevelForExperience(Skills.PRAYER));
		if(mob.getActionSender() != null) {
			mob.getActionSender().sendSkills();
		}
	}
	
	/**
	 * Checks if a level is below it's normal level for experience.
	 * This is used for consumables, e.g. 130/99hp, then eating a manta ray and it returning to 99/99hp.
	 * @param skill The skill id.
	 * @return If the level is below it's normal level.
	 */
	public boolean isLevelBelowOriginal(int skill) {
		return levels[skill] < getLevelForExperience(skill);
	}
	
	/**
	 * Checks if a level is below it's normal level for experience + a certain modification.
	 * This is used for consumables, e.g. 130/99 str, then drinking a super strength potion and it returning to 118/99.
	 * @param skill The skill id.
	 * @param modification The modification amount.
	 * @return If the level is below it's normal level + a certain modification.
	 */
	public boolean isLevelBelowOriginalModification(int skill, int modification) {
		return levels[skill] < (getLevelForExperience(skill) + modification);
	}
	
	/**
	 * Increases a level to its level for experience, depending on the modification amount.
	 * @param skill The skill id.
	 * @param modification The modification amount.
	 */
	public void increaseLevelToMaximum(int skill, int modification) {
		if(isLevelBelowOriginal(skill)) {
			setLevel(skill, levels[skill] + modification >= getLevelForExperience(skill) ? getLevelForExperience(skill) : levels[skill] + modification);
		}
	}
	
	/**
	 * Increases a level to its level for experience + modification amount.
	 * @param skill The skill id.
	 * @param modification The modification amount.
	 */
	public void increaseLevelToMaximumModification(int skill, int modification) {
		if(isLevelBelowOriginalModification(skill, modification)) {
			setLevel(skill, levels[skill] + modification >= (getLevelForExperience(skill) + modification) ? (getLevelForExperience(skill) + modification) : levels[skill] + modification);
		}
	}
	
	/**
	 * Decreases a level to its minimum.
	 * @param skill The skill id.
	 * @param modification The modification amount.
	 */
	public void decreaseLevelToMinimum(int skill, int modification) {
		if(levels[skill] > 1) {
			setLevel(skill, levels[skill] - modification <= 1 ? 1 : levels[skill] - modification);
		}
	}
	
	/**
	 * Decreases a level to 0.
	 * @param skill The skill id.
	 * @param modification The modification amount.
	 */
	public void decreaseLevelToZero(int skill, int modification) {
		if(levels[skill] > 0) {
			setLevel(skill, levels[skill] - modification <= 0 ? 0 : levels[skill] - modification);
		}
	}
	
	/**
	 * Gets the mob's prayer points.
	 * @return The mob's prayer points.
	 */
	public double getPrayerPoints() {
		return prayerPoints;
	}

	/**
	 * Sets the mob's prayer points.
	 * @param prayerPoints The amount of prayer points to set.
	 */
	public void setPrayerPoints(double prayerPoints, boolean update) {
		int lvlBefore = (int) Math.ceil(this.prayerPoints);
		this.prayerPoints = prayerPoints;
		int lvlAfter = (int) Math.ceil(this.prayerPoints);
		if(update && (lvlBefore - lvlAfter >= 1 || lvlAfter - lvlBefore >= 1) && mob.getActionSender() != null) {
			mob.getActionSender().sendSkill(Skills.PRAYER);
		}
	}
	
	/**
	 * Increases the mob's prayer points to its maxmimum.
	 * @param modification The amount to increase by.
	 */
	public void increasePrayerPoints(double modification) {
		if(getPrayerPoints() < getLevelForExperience(Skills.PRAYER)) {
			setPrayerPoints(getPrayerPoints() + modification >= getLevelForExperience(Skills.PRAYER) ? getLevelForExperience(Skills.PRAYER) : getPrayerPoints() + modification, true);
		}
	}
	
	/**
	 * Decreases the mob's prayer points to its minimum.
	 * @param modification The amount to increase by.
	 */
	public void decreasePrayerPoints(double modification) {
		if(getPrayerPoints() > 0) {
			setPrayerPoints(getPrayerPoints() - modification <= 0 ? 0 : getPrayerPoints() - modification, true);
		}
	}

	public void setPrayerPoints(double prayerPoints) {
		this.prayerPoints = prayerPoints;
	}

	/**
	 * Represents a skill cape of achievement.
	 * @author Michael
	 *
	 */
	public enum SkillCape {

		ATTACK_CAPE(Skills.ATTACK, new Item(9747), new Item(9748), new Item(9749), Animation.create(4959), Graphic.create(823), 6),
		
		STRENGTH_CAPE(Skills.STRENGTH, new Item(9750), new Item(9751), new Item(9752), Animation.create(4981), Graphic.create(828), 17),
		
		DEFENCE_CAPE(Skills.DEFENCE, new Item(9753), new Item(9754), new Item(9755), Animation.create(4961), Graphic.create(824), 10),
		
		HITPOINTS_CAPE(Skills.HITPOINTS, new Item(9768), new Item(9769), new Item(9770), Animation.create(4971), Graphic.create(833), 7),
		
		RANGE_CAPE(Skills.RANGE, new Item(9756), new Item(9757), new Item(9758), Animation.create(4973), Graphic.create(832), 9),
		
		PRAYER_CAPE(Skills.PRAYER, new Item(9759), new Item(9760), new Item(9761), Animation.create(4979), Graphic.create(829), 10),
		
		MAGIC_CAPE(Skills.MAGIC, new Item(9762), new Item(9763), new Item(9764), Animation.create(4939), Graphic.create(813), 6),
		
		COOKING_CAPE(Skills.COOKING, new Item(9801), new Item(9802), new Item(9803), Animation.create(4955), Graphic.create(821), 25),
		
		WOODCUTTING_CAPE(Skills.WOODCUTTING, new Item(9807), new Item(9808), new Item(9809), Animation.create(4957), Graphic.create(822), 21),
		
		FLETCHING_CAPE(Skills.FLETCHING, new Item(9783), new Item(9784), new Item(9785), Animation.create(4937), Graphic.create(812), 13),
		
		FISHING_CAPE(Skills.FISHING, new Item(9798), new Item(9799), new Item(9800), Animation.create(4951), Graphic.create(819), 12),
		
		FIREMAKING_CAPE(Skills.FIREMAKING, new Item(9804), new Item(9805), new Item(9806), Animation.create(4975), Graphic.create(831), 8),
		
		CRAFTING_CAPE(Skills.CRAFTING, new Item(9780), new Item(9781), new Item(9782), Animation.create(4949), Graphic.create(818), 14),
		
		SMITHING_CAPE(Skills.SMITHING, new Item(9795), new Item(9796), new Item(9797), Animation.create(4943), Graphic.create(815), 19),
		
		MINING_CAPE(Skills.MINING, new Item(9792), new Item(9793), new Item(9794), Animation.create(4941), Graphic.create(814), 8),
		
		HERBLORE_CAPE(Skills.HERBLORE, new Item(9774), new Item(9775), new Item(9776), Animation.create(4969), Graphic.create(835), 15),
		
		AGILITY_CAPE(Skills.AGILITY, new Item(9771), new Item(9772), new Item(9773), Animation.create(4977), Graphic.create(830), 7),
		
		THIEVING_CAPE(Skills.THIEVING, new Item(9777), new Item(9778), new Item(9779), Animation.create(4965), Graphic.create(826), 6),
		
		SLAYER_CAPE(Skills.SLAYER, new Item(9786), new Item(9787), new Item(9788), Animation.create(4967), Graphic.create(827), 5),
		
		FARMING_CAPE(Skills.FARMING, new Item(9810), new Item(9811), new Item(9812), Animation.create(4963), Graphic.create(825), 12),
		
		RUNECRAFTING_CAPE(Skills.RUNECRAFTING, new Item(9765), new Item(9766), new Item(9767), Animation.create(4947), Graphic.create(817), 11),
		
		HUNTER_CAPE(Skills.HUNTER, new Item(9948), new Item(9949), new Item(9950), Animation.create(5158), Graphic.create(907), 12),
		
		CONSTRUCTION_CAPE(Skills.CONSTRUCTION, new Item(9789), new Item(9790), new Item(9791), Animation.create(4953), Graphic.create(820), 12),
		
		QUEST_POINT_CAPE(-1, new Item(9813), null, new Item(9814), Animation.create(4945), Graphic.create(816), 15)
		;
		
		public static List<SkillCape> skillCapes = new ArrayList<SkillCape>();
		
		public static SkillCape forId(Item item) {
			for(SkillCape skillCape : skillCapes) {
				if((skillCape.getCape() != null && skillCape.getCape().getId() == item.getId()) 
								|| (skillCape.getCapeTrim() != null && skillCape.getCapeTrim().getId() == item.getId())) {
					return skillCape;
				}
			}
			return null;
		}
		
		/**
		 * Checks only untrimmed skillcapes.
		 */
		public static SkillCape forUntrimmedId(Item item) {
			for(SkillCape skillCape : skillCapes) {
				if(skillCape.getCape() != null && skillCape.getCape().getId() == item.getId()) {
					return skillCape;
				}
			}
			return null;
		}
		
		/**
		 * Checks only trimmed skillcapes.
		 */
		public static SkillCape forTrimmedId(Item item) {
			for(SkillCape skillCape : skillCapes) {
				if(skillCape.getCapeTrim() != null && skillCape.getCapeTrim().getId() == item.getId()) {
					return skillCape;
				}
			}
			return null;
		}
		
		static {
			for(SkillCape skillCape : SkillCape.values()) {
				skillCapes.add(skillCape);
			}
		}
		
		/**
		 * The skill this cape uses.
		 */
		private int skill;

		/**
		 * The cape item.
		 */
		private Item cape;

		/**
		 * The cape item trimmed.
		 */
		private Item capeTrim;

		/**
		 * The hood item.
		 */
		private Item hood;

		/**
		 * The animation performed for the skillcape emote.
		 */
		private Animation animation;

		/**
		 * The graphic displayed for the skillcape emote.
		 */
		private Graphic graphic;
		
		/**
		 * The amount of cycles it takes to perform this animation.
		 */
		private int animateTimer;

		SkillCape(int skill, Item cape, Item capeTrim, Item hood, Animation animation, Graphic graphic, int animateTimer) {
			this.skill = skill;
			this.cape = cape;
			this.capeTrim = capeTrim;
			this.hood = hood;
			this.animation = animation;
			this.graphic = graphic;
			this.animateTimer = animateTimer;
		}
		
		/**
		 * @return the skill
		 */
		public int getSkill() {
			return skill;
		}
		
		/**
		 * @return the hood
		 */
		public Item getHood() {
			return hood;
		}
		
		/**
		 * @return the cape
		 */
		public Item getCape() {
			return cape;
		}
		
		/**
		 * @return the capeTrim
		 */
		public Item getCapeTrim() {
			return capeTrim;
		}
		
		/**
		 * @return the animation
		 */
		public Animation getAnimation() {
			return animation;
		}
		
		/**
		 * @return the graphic
		 */
		public Graphic getGraphic() {
			return graphic;
		}

		/**
		 * @return the animateTimer
		 */
		public int getAnimateTimer() {
			return animateTimer;
		}
	}
}
