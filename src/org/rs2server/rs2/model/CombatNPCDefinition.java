package org.rs2server.rs2.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.CombatState.CombatStyle;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.combat.impl.MeleeCombatAction;
import org.rs2server.rs2.model.combat.impl.RangeCombatAction;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.Spell;
import org.rs2server.rs2.util.NameUtils;
import org.rs2server.util.XMLController;


/**
 * <p>Represents a type of combat NPC.</p>
 * @author Graham Edgecombe
 *
 */
public class CombatNPCDefinition {
	
	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(CombatNPCDefinition.class.getName());
	
	/**
	 * The <code>NPCDefinition</code> map.
	 */
	private static Map<Integer, CombatNPCDefinition> definitions;
	
	/**
	 * @return the definitions
	 */
	public static Map<Integer, CombatNPCDefinition> getDefinitions() {
		return definitions;
	}

	/**
	 * Gets a definition for the specified id.
	 * @param id The id.
	 * @return The definition.
	 */
	public static CombatNPCDefinition forId(int id) {
		return definitions.get(id);
	}

	/**
	 * Loads the npc combat definitions.
	 * @throws IOException if an I/O error occurs.
	 * @throws IllegalStateException if the definitions have been loaded already.
	 */
	public static void init() throws IOException {
		if(definitions != null) {
			throw new IllegalStateException("NPC definitions already loaded.");
		}
		logger.info("Loading combat NPC definitions...");
		try {
			/**
			 * Load NPC definitions.
			 */
			definitions = new HashMap<Integer, CombatNPCDefinition>(NPCDefinition.getDefinitions().length);
			File file = new File("./data/npcCombatDefinition.xml");
			if(file.exists()) {
				definitions = XMLController.readXML(file);
				for(int i : definitions.keySet()) {
					CombatNPCDefinition def = definitions.get(i);
					if(def == null) {
						continue;
					}
					switch(def.getCombatActionType()) {
					case MELEE:
						def.setCombatAction(MeleeCombatAction.getAction());
						break;
					case RANGE:
						def.setCombatAction(RangeCombatAction.getAction());
						break;
					case MAGE:
						def.setCombatAction(MagicCombatAction.getAction());
						break;
					case CUSTOM:
						String packageName = "org.rs2server.rs2.model.combat.npcs.";
						File[] files = new File("./data/npcs/").listFiles();
						for (File f : files) {
							if (!f.getName().endsWith(".class")) {
								continue;
							}
							String fileName = packageName + f.getName().substring(0, f.getName().length() - 6);
							String requiredName = packageName + NameUtils.formatName(NPCDefinition.forId(i).getName()).replace(" ", "").replace("'","").replace("`","");
							if (!fileName.equals(requiredName)) {
								continue;
							}
							Class<?> fileClass = Class.forName(fileName);
							if (fileClass.getSuperclass() != AbstractCombatAction.class) {
								continue;
							}
							AbstractCombatAction combatAction = (AbstractCombatAction) fileClass.newInstance();
							def.setCombatAction(combatAction);
						}
						break;
					}
				}
				logger.info("Loaded " + definitions.size() + " combat NPC definitions.");
			} else {
				logger.info("NPC combat definitions not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		Map<Integer, CombatNPCDefinition> defs = new HashMap<Integer, CombatNPCDefinition>(NPCDefinition.getDefinitions().length);
//		Map<Skill, Integer> skills = new HashMap<Skill, Integer>();
//		skills.put(Skill.ATTACK, 25);
//		skills.put(Skill.DEFENCE, 25);
//		skills.put(Skill.HITPOINTS, 53);
//		defs.put(19, new CombatNPCDefinition(36, 6, 6, skills, new int[] { -4, 27, 21, -64, -20, 81, 82, 70, -11, 71, 26, 6, 0 }, false, Animation.create(7042), Animation.create(7050), Animation.create(836), AttackType.SLASH, CombatStyle.AGGRESSIVE_1, Spell.WIND_STRIKE, -1, -1, new NPCDrop[] { new NPCDrop(1, new Item(526)) }, new NPCDrop[0], 50, CombatActionType.CUSTOM));
//		XMLController.writeXML(defs, new File("data/newcombat.xml"));
	}
	
	/**
	 * The npc's max hit.
	 */
	private int maxHit;
	
	/**
	 * Is this npc agressive.
	 */
	private boolean aggressive = false;
	
	/**
	 * The autocast spell id, if the attack style is magic.
	 */
	private Spell spell;
	
	/**
	 * The combat cooldown delay.
	 */
	private int combatCooldownDelay;

	/**
	 * The drawback graphic, for range attacks.
	 */
	private Graphic drawbackGraphic;
	
	/**
	 * The projectile id, for range attacks.
	 */
	private int projectileId;
	
	/**
	 * The amount of ticks before this npc respawns.
	 */
	private int respawnTicks;
	
	/**
	 * The attack animation.
	 */
	private Animation attack;

	/**
	 * The defend animation.
	 */
	private Animation defend;

	/**
	 * The death animation.
	 */
	private Animation death;
	
	/**
	 * The attack type.
	 */
	private AttackType attackType;
	
	/**
	 * The combat style.
	 */
	private CombatStyle combatStyle;

	/**
	 * The combat action.
	 */
	private CombatAction combatAction;
	
	/**
	 * The combat action type.
	 */
	private CombatActionType combatActionType;

	/**
	 * The npc's skill levels.
	 */
	private Map<Skill, Integer> skills;
	
	/**
	 * The npc's bonuses.
	 */
	private int[] bonuses = new int[13];

	/**
	 * The list of items this npc drops.
	 */
	private NPCDrop[] constantDrops;

	/**
	 * The list of items this npc drops.
	 */
	private NPCDrop[] randomDrops;
	
	/**
	 * The godwars team this npc is on.
	 */
	private GodWarsMinion godWarsTeam;

	/**
	 * Creates the definition.
	 * @param id The id.
	 */
	private CombatNPCDefinition(int combatLevel, int maxHit, int combatCooldownDelay, Map<Skill, Integer> skills, int[] bonuses, boolean aggressive, Animation attack, Animation defend, Animation death, AttackType attackType, CombatStyle combatStyle, Spell spell, NPCDrop[] constantDrops, NPCDrop[] randomDrops, int respawnTicks, CombatActionType combatActionType) {
		this.combatCooldownDelay = combatCooldownDelay;
		this.maxHit = maxHit;
		this.skills = skills;
		this.bonuses = bonuses;
		this.aggressive = aggressive;
		this.attack = attack;
		this.defend = defend;
		this.attackType = attackType;
		this.combatStyle = combatStyle;
		this.death = death;
		this.spell = spell;
		this.constantDrops = constantDrops;
		this.randomDrops = randomDrops;
		this.respawnTicks = respawnTicks;
		this.combatActionType = combatActionType;
	}
	
	/**
	 * The combat action enum.
	 * @author Michael
	 */
	private enum CombatActionType {
		MELEE,
		RANGE,
		MAGE,
		CUSTOM
	}
	
	/**
	 * The skill statistic enum.
	 * @author Michael
	 */
	public enum Skill {
		
		ATTACK(Skills.ATTACK),
		
		DEFENCE(Skills.DEFENCE),
		
		HITPOINTS(Skills.HITPOINTS),
		
		RANGE(Skills.RANGE),
		
		MAGIC(Skills.MAGIC);
		
		/**
		 * The list of skills.
		 */
		private static Map<Integer, Skill> skills = new HashMap<Integer, Skill>();
		
		public static Skill skillForId(int skill) {
			return skills.get(skill);
		}
		
		/**
		 * Populates the skill list.
		 */
		static {
			for(Skill skill : Skill.values()) {
				skills.put(skill.getId(), skill);
			}
		}
		
		/**
		 * The id of the skill.
		 */
		private int id;
		
		private Skill(int id) {
			this.id = id;
		}

		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}
	}

	/**
	 * @return the maxHit
	 */
	public int getMaxHit() {
		return maxHit;
	}

	/**
	 * Gets the combat cooldown delay.
	 * @return The combat cooldown delay.
	 */
	public int getCombatCooldownDelay() {
		return combatCooldownDelay;
	}
	
	/**
	 * @return the skills
	 */
	public Map<Skill, Integer> getSkills() {
		return skills;
	}

	/**
	 * Gets the bonuses.
	 * @return The bonuses.
	 */
	public int[] getBonuses() {
		return this.bonuses;
	}
	
	/**
	 * Gets a bonus by its index.
	 * @return The bonus.
	 */
	public int getBonus(int index) {
		return this.bonuses[index];
	}
	
	/**
	 * @return the attack
	 */
	public Animation getAttack() {
		return attack;
	}
	
	/**
	 * @return the defend
	 */
	public Animation getDefend() {
		return defend;
	}
	
	/**
	 * @return the death
	 */
	public Animation getDeath() {
		return death;
	}
	
	/**
	 * @param attackAction the attackAction to set
	 */
	public void setCombatAction(CombatAction combatAction) {
		this.combatAction = combatAction;
	}

	/**
	 * @return the attackAction
	 */
	public CombatAction getCombatAction() {
		return combatAction;
	}

	/**
	 * @return the combatActionType
	 */
	public CombatActionType getCombatActionType() {
		return combatActionType;
	}

	/**
	 * @return the attackType
	 */
	public AttackType getAttackType() {
		return attackType;
	}

	/**
	 * @return the combatStyle
	 */
	public CombatStyle getCombatStyle() {
		return combatStyle;
	}
	
	/**
	 * @return the aggressive
	 */
	public boolean isAggressive() {
		return aggressive;
	}

	/**
	 * @return the spell
	 */
	public Spell getSpell() {
		return spell;
	}

	/**
	 * @return the constantDrops
	 */
	public NPCDrop[] getConstantDrops() {
		return constantDrops;
	}

	/**
	 * @return the randomDrops
	 */
	public NPCDrop[] getRandomDrops() {
		return randomDrops;
	}

	/**
	 * @return the respawnTicks
	 */
	public int getRespawnTicks() {
		return respawnTicks;
	}

	/**
	 * @param spell the spell to set
	 */
	public void setSpell(Spell spell) {
		this.spell = spell;
	}
	
	public GodWarsMinion getGodWarsTeam() {
		return godWarsTeam;
	}

	/**
	 * @return the drawbackGraphic
	 */
	public Graphic getDrawbackGraphic() {
		return drawbackGraphic;
	}

	/**
	 * @return the projectileId
	 */
	public int getProjectileId() {
		return projectileId;
	}

	public enum GodWarsMinion {
		SARADOMIN,
		ZAMORAK,
		BANDOS,
		ARMADYL		
	}

}