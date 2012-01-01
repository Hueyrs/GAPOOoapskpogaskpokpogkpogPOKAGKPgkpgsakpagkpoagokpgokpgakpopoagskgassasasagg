package org.rs2server.rs2.model.combat;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.combat.CombatState.CombatStyle;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.combat.impl.MeleeCombatAction;
import org.rs2server.rs2.model.combat.impl.RangeCombatAction;
import org.rs2server.rs2.model.combat.impl.RangeCombatAction.BowType;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.equipment.EquipmentDefinition;

/**
 * A utility class which contains combat-related formulae.
 * @author Scu11
 * @author Graham Edgecombe
 *
 */
public final class CombatFormulae {
	
	/**
	 * Used in defence formulae.
	 */
	public static final double DEFENCE_MODIFIER = 0.895;
	
	/**
	 * Default private constructor.
	 */
	private CombatFormulae() {
		
	}

	/**
	 * Calculates a mob's melee max hit.
	 */
	public static int calculateMeleeMaxHit(Mob mob, boolean special) {
		if(mob.isNPC()) {
			NPC npc = (NPC) mob;
			return npc.getCombatDefinition().getMaxHit();
		}
		int maxHit = 0;
		double specialMultiplier = 1;
		double prayerMultiplier = 1;
		double otherBonusMultiplier = 1; //TODO: void melee = 1.2, slayer helm = 1.15, salve amulet = 1.15, salve amulet(e) = 1.2
		int strengthLevel = mob.getSkills().getLevel(Skills.STRENGTH);
		int combatStyleBonus = 0;
		
		if(mob.getCombatState().getPrayer(Prayers.BURST_OF_STRENGTH)) {
			prayerMultiplier = 1.05;
		} else if(mob.getCombatState().getPrayer(Prayers.SUPERHUMAN_STRENGTH)) {
			prayerMultiplier = 1.1;
		} else if(mob.getCombatState().getPrayer(Prayers.ULTIMATE_STRENGTH)) {
			prayerMultiplier = 1.15;
		} else if(mob.getCombatState().getPrayer(Prayers.CHIVALRY)) {
			prayerMultiplier = 1.18;
		} else if(mob.getCombatState().getPrayer(Prayers.PIETY)) {
			prayerMultiplier = 1.23;
		}
		
		switch(mob.getCombatState().getCombatStyle()) {
		case AGGRESSIVE_1:
		case AGGRESSIVE_2:
			combatStyleBonus = 3;
			break;
		case CONTROLLED_1:
		case CONTROLLED_2:
		case CONTROLLED_3:
			combatStyleBonus = 1;
			break;
		}
		
		if(fullVoidMelee(mob)) {
			otherBonusMultiplier = 1.1;
		}
		
		int effectiveStrengthDamage = (int) ((strengthLevel * prayerMultiplier * otherBonusMultiplier) + combatStyleBonus);
		double baseDamage = 1.3 + (effectiveStrengthDamage / 10) + (mob.getCombatState().getBonus(10) / 80) + ((effectiveStrengthDamage * mob.getCombatState().getBonus(10)) / 640);
		
		if(special) {
			if(mob.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
				switch(mob.getEquipment().get(Equipment.SLOT_WEAPON).getId()) {
				case 11694:
					specialMultiplier = 1.34375;
					break;
				case 11696:
					specialMultiplier = 1.1825;
					break;
				case 11698:
				case 11700:
					specialMultiplier = 1.075;
					break;
				case 3101:
				case 3204:
				case 1215:
				case 1231:
				case 5680:
				case 5698:
					specialMultiplier = 1.1;
					break;
				case 1305:
					specialMultiplier = 1.15;
					break;
				case 1434:
					specialMultiplier = 1.45;
					break;
				case 7158:
					specialMultiplier = 1.17;
					break;
				}
			}
		}
		
		maxHit = (int) (baseDamage * specialMultiplier);
		
		if(fullDharok(mob)) {
			int hpLost = mob.getSkills().getLevelForExperience(Skills.HITPOINTS) - mob.getSkills().getLevel(Skills.HITPOINTS);
			maxHit += hpLost * 0.35;
		}		
		return maxHit;
	}

	/**
	 * Calculates a mob's range max hit.
	 */
	public static int calculateRangeMaxHit(Mob mob, boolean special) {
		if(mob.isNPC()) {
			NPC npc = (NPC) mob;
			return npc.getCombatDefinition().getMaxHit();
		}
		int maxHit = 0;
		double specialMultiplier = 1;
		double prayerMultiplier = 1;
		double otherBonusMultiplier = 1;	
		int rangedStrength = mob.getCombatState().getBonus(12);
		Item weapon = mob.getEquipment().get(Equipment.SLOT_WEAPON);
		BowType bow = weapon.getEquipmentDefinition().getBowType();
		
		if(bow == BowType.CRYSTAL_BOW) {
			/**
			 * Crystal Bow does not use arrows, so we don't use the arrows range strength bonus.
			 */
			rangedStrength = mob.getEquipment().get(Equipment.SLOT_WEAPON).getEquipmentDefinition().getBonus(12);
		}
		
		int rangeLevel = mob.getSkills().getLevel(Skills.RANGE);
		int combatStyleBonus = 0;
		
		switch(mob.getCombatState().getCombatStyle()) {
		case ACCURATE:
			combatStyleBonus = 3;
			break;
		}
		
		if(fullVoidRange(mob)) {
			otherBonusMultiplier = 1.1;
		}
		
		int effectiveRangeDamage = (int) ((rangeLevel * prayerMultiplier * otherBonusMultiplier) + combatStyleBonus);
		double baseDamage = 1.3 + (effectiveRangeDamage / 10) + (rangedStrength / 80) + ((effectiveRangeDamage * rangedStrength) / 640);
		
		if(special) {
			if(mob.getEquipment().get(Equipment.SLOT_ARROWS) != null) {
				switch(mob.getEquipment().get(Equipment.SLOT_ARROWS).getId()) {
				case 11212:
					specialMultiplier = 1.5;
					break;
				case 9243:
					specialMultiplier = 1.15;
					break;
				case 9244:
					specialMultiplier = 1.45;
					break;
				case 9245:
					specialMultiplier = 1.15;
					break;
				case 9236:
					specialMultiplier = 1.25;
					break;
				case 882:
				case 884:
				case 886:
				case 888:
				case 890:
				case 892:
					if(mob.getEquipment().get(Equipment.SLOT_WEAPON) != null && mob.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 11235) {
						specialMultiplier = 1.3;
					}
					break;
				}
			}
		}
		
		maxHit = (int) (baseDamage * specialMultiplier);
		
		return maxHit;
	}

	public static boolean fullVoidMelee(Mob mob) {
		return mob.getEquipment() != null
				&& mob.getEquipment().contains(8839)
				&& mob.getEquipment().contains(8840)
				&& mob.getEquipment().contains(8842)
				&& mob.getEquipment().contains(11665);
	}

	public static boolean fullVoidRange(Mob mob) {
		return mob.getEquipment() != null
				&& mob.getEquipment().contains(8839)
				&& mob.getEquipment().contains(8840)
				&& mob.getEquipment().contains(11664)
				&& mob.getEquipment().contains(8097);
	}

	public static boolean fullVoidMage(Mob mob) {
		return mob.getEquipment() != null
				&& mob.getEquipment().contains(8839)
				&& mob.getEquipment().contains(8840)
				&& mob.getEquipment().contains(11663)
				&& mob.getEquipment().contains(8098);
	}

	public static boolean fullGuthan(Mob mob) {
		return mob.getEquipment() != null
				&& mob.getEquipment().contains(4724)
				&& mob.getEquipment().contains(4726)
				&& mob.getEquipment().contains(4728)
				&& mob.getEquipment().contains(4730);
	}

	public static boolean fullTorag(Mob mob) {
		return mob.getEquipment() != null
				&& mob.getEquipment().contains(4745)
				&& mob.getEquipment().contains(4747)
				&& mob.getEquipment().contains(4749)
				&& mob.getEquipment().contains(4751);
	}

	public static boolean fullKaril(Mob mob) {
		return mob.getEquipment() != null
				&& mob.getEquipment().contains(4732)
				&& mob.getEquipment().contains(4734)
				&& mob.getEquipment().contains(4736)
				&& mob.getEquipment().contains(4738);
	}

	public static boolean fullAhrim(Mob mob) {
		return mob.getEquipment() != null
				&& mob.getEquipment().contains(4708)
				&& mob.getEquipment().contains(4710)
				&& mob.getEquipment().contains(4712)
				&& mob.getEquipment().contains(4714);
	}

	public static boolean fullDharok(Mob mob) {
		return mob.getEquipment() != null
				&& mob.getEquipment().contains(4716)
				&& mob.getEquipment().contains(4718)
				&& mob.getEquipment().contains(4720)
				&& mob.getEquipment().contains(4722);
	}

	public static boolean fullVerac(Mob mob) {
		return mob.getEquipment() != null
				&& mob.getEquipment().contains(4753)
				&& mob.getEquipment().contains(4755)
				&& mob.getEquipment().contains(4757)
				&& mob.getEquipment().contains(4759);
	}
	
	/**
	 * The percentage of the hit reducted by antifire.
	 */
	public static double dragonfireReduction(Mob mob) {
		boolean dragonfireShield = mob.getEquipment() != null && (mob.getEquipment().contains(1540) || mob.getEquipment().contains(11283) || mob.getEquipment().contains(11284) || mob.getEquipment().contains(11285));
		boolean dragonfirePotion = false;
		boolean protectPrayer = mob.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC);
		if(dragonfireShield && dragonfirePotion) {
			if(mob.getActionSender() != null) {
				mob.getActionSender().sendMessage("You shield absorbs most of the dragon fire!");
				mob.getActionSender().sendMessage("Your potion protects you from the heat of the dragon's breath!");				
			}
			return 1;
		} else if(dragonfireShield) {
			if(mob.getActionSender() != null) {
				mob.getActionSender().sendMessage("You shield absorbs most of the dragon fire!");
			}
			return 0.8; //80%
		} else if(dragonfirePotion) {
			if(mob.getActionSender() != null) {
				mob.getActionSender().sendMessage("Your potion protects you from the heat of the dragon's breath!");
			}
			return 0.8; //80%
		} else if(protectPrayer) {
			if(mob.getActionSender() != null) {
				mob.getActionSender().sendMessage("Your prayers resist some of the dragon fire.");
			}
			return 0.6; //60%
		}
		return /*mob.getEquipment() != null*/0;		
	}

	/**
	 * Get the attackers' weapon speed.
	 * 
	 * @param player The player for whose weapon we are getting the speed value.
	 * @return A <code>long</code>-type value of the weapon speed.
	 */
	public static int getCombatCooldownDelay(Mob mob) {
		int extra = 0;
		if(getActiveCombatAction(mob) == RangeCombatAction.getAction()) {
			if(mob.getCombatState().getCombatStyle() != CombatStyle.AGGRESSIVE_1) {
				/**
				 * If we are ranging and are not on rapid, combat speed is increased by 1 cycle
				 */
				extra = 1;
			}
		}
		return (mob.getEquipment() != null && mob.getEquipment().get(3) != null) ? mob.getEquipment().get(3).getEquipmentDefinition().getSpeed() + extra : 4;
	}

	public static CombatAction getActiveCombatAction(Mob mob) {
		if(mob.getDefaultCombatAction() != null) {
			return mob.getDefaultCombatAction();
		}
		if(mob.getCombatState().getQueuedSpell() != null || (mob.getAutocastSpell() != null && (mob.getCombatState().getCombatStyle() == CombatStyle.AUTOCAST || mob.getCombatState().getCombatStyle() == CombatStyle.DEFENSIVE_AUTOCAST))) {
			return MagicCombatAction.getAction();
		}
		Item weapon = mob.getEquipment().get(Equipment.SLOT_WEAPON);
		if(weapon != null) {
			EquipmentDefinition weaponEquipDef = weapon.getEquipmentDefinition();
			if(weaponEquipDef.getBowType() != null || weaponEquipDef.getRangeWeaponType() != null) {
				return RangeCombatAction.getAction();				
			}
		}
		return MeleeCombatAction.getAction();
	}

}
