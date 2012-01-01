package org.rs2server.rs2.model.combat.impl;

import java.util.Random;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Projectile;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.Hit.HitPriority;
import org.rs2server.rs2.model.Mob.InteractionMode;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatFormulae;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.CombatState.CombatStyle;
import org.rs2server.rs2.model.combat.impl.RangeCombatAction.BowType;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;


/**
 * Provides a skeletal implementation for <code>CombatAction</code>s on which
 * other code should base their code off.
 * <p>
 * This implementation contains code common to ALL implementations, e.g. the
 * 'canHit' method checks wilderness levels of the players.
 * @author Graham Edgecombe
 *
 */
public abstract class AbstractCombatAction implements CombatAction {
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	@Override
	public boolean canHit(Mob attacker, Mob victim, boolean messages, boolean cannon) {
		if(attacker.getCombatState().isDead() || victim.getCombatState().isDead()) {
			return false;
		}
		if(!attacker.canHit(victim, messages)) {
			return false;
		}
		if(!victim.canHit(attacker, messages)) {
			return false;
		}
		if(!BoundaryManager.isWithinBoundaryNoZ(attacker.getLocation(), "MultiCombat")
				|| !BoundaryManager.isWithinBoundaryNoZ(victim.getLocation(), "MultiCombat")) {
			if(attacker.getCombatState().getLastHitTimer() > (System.currentTimeMillis() + 4000)) { //10 cycles for tagging timer
				if(attacker.getCombatState().getLastHitBy() != null && victim != attacker.getCombatState().getLastHitBy()) {
					if(messages && attacker.getActionSender() != null) {
						attacker.getActionSender().sendMessage("I'm already under attack.");
					}
					return false;
				}
			}
			if(victim.getCombatState().getLastHitTimer() > (System.currentTimeMillis() + 4000)) { //10 cycles for tagging timer
				if(victim.getCombatState().getLastHitBy() != null && attacker != victim.getCombatState().getLastHitBy()) {
					if(messages && attacker.getActionSender() != null) {
						attacker.getActionSender().sendMessage("Someone else is already fighting your opponent.");
					}
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public void hit(final Mob attacker, Mob victim) {
		/**
		 * This is to prevent immediate teaming, EG: someone walking into Edgeville(1v1) and 2 people casting spells at the same time, usually it wouldn't set the timer till the damage had been inflicted
		 */
		victim.getCombatState().setLastHitTimer(10000);
		victim.getCombatState().setLastHitBy(attacker);
		victim.getActionQueue().clearRemovableActions();
		/**
		 * Stops other emotes from overlapping important ones.
		 */
		attacker.getCombatState().setWeaponSwitchTimer(2);
		attacker.getCombatState().setCanAnimate(false);
		World.getWorld().submit(new Tickable(1) {
			public void execute() {
				attacker.getCombatState().setCanAnimate(true);
				this.stop();
			}
		});
	}

	@Override
	public void defend(Mob attacker, Mob victim, boolean blockAnimation) {
		if(victim.isAutoRetaliating()) {
			if(victim.getCombatState().getAttackEvent() == null || victim.getInteractingEntity() != attacker) {
				victim.getCombatState().setAttackDelay(3);
			}
			victim.getCombatState().startAttacking(attacker, true);
		}
		if(blockAnimation && victim.getCombatState().canAnimate()) {
			Animation defend = Animation.create(404);
			Item shield = victim.getEquipment().get(Equipment.SLOT_SHIELD);
			Item weapon = victim.getEquipment().get(Equipment.SLOT_WEAPON);
			String shieldName = shield != null ? shield.getDefinition().getName() : "";

		
			if(shieldName.contains("shield") || shieldName.contains("ket-xil") || shieldName.contains("defender")) {
				defend = shield.getEquipmentDefinition().getAnimation().getDefend();
			} else if(weapon != null) {
				defend = weapon.getEquipmentDefinition().getAnimation().getDefend();				
			} else if(shield != null) {
				defend = shield.getEquipmentDefinition().getAnimation().getDefend();				
			} else {
				defend = victim.getDefendAnimation();
			}
			victim.playAnimation(defend);
		}
	}

	@Override
	public boolean canSpecial(final Mob attacker, final Mob victim) {
		Item weapon = attacker.getEquipment().get(Equipment.SLOT_WEAPON);
		if(weapon != null && weapon.getEquipmentDefinition() != null) {
			if(victim != null && !victim.getCombatState().isDead()
							&& attacker.getActiveCombatAction().canHit(attacker, victim, true, false)
							&& attacker.getLocation().isWithinDistance(attacker, victim, attacker.getActiveCombatAction().distance(attacker))) {
				if(attacker.getCombatState().getSpecialEnergy() >= weapon.getEquipmentDefinition().getSpecialConsumption()) {
					if(attacker.getCombatState().getAttackDelay() == 0) {
						switch(weapon.getId()) {
						case 861:
						case 11235:
							if(attacker.getEquipment().get(Equipment.SLOT_ARROWS).getCount() < 2) {
								attacker.getActionSender().sendMessage("You need atleast 2 arrows to perform this special.");
								attacker.getCombatState().setSpecial(false);
								return false;						
							}
							break;
						}
					} else {
						switch(weapon.getId()) {
						case 4153:
							if(!attacker.canAnimate() || attacker.getActiveCombatAction() != MeleeCombatAction.getAction()
											|| !attacker.getActiveCombatAction().canHit(attacker, victim, true, false)
											|| !attacker.getLocation().isWithinDistance(attacker, victim, attacker.getActiveCombatAction().distance(attacker))) {
								attacker.getCombatState().setSpecial(false);
								return false;
							}
							break;
						}
					}
					return true;
				} else {
					attacker.getActionSender().sendMessage("You do not have enough special energy left.");
					attacker.getCombatState().setSpecial(false);
					return false;
				}
			} else {
				switch(weapon.getId()) {
				case 4153:
					if(attacker.getActionSender() != null && attacker.getCombatState().isSpecialOn()) {
						attacker.getActionSender().sendMessage("Warning: Since the maul's special is an instant attack, it will be wasted when used on a");
						attacker.getActionSender().sendMessage("first strike.");
					}
					break;
				case 1377:
					if(attacker.getActionSender() != null && attacker.getCombatState().isSpecialOn()) {
						if(!(attacker.getCombatState().getSpecialEnergy() >= 100)) {
							return false;
						}
						attacker.playAnimation(Animation.create(1056));
						attacker.playGraphics(Graphic.create(246));
						attacker.forceChat("Raarrrrrgggggghhhhhhh!");
						int boost = (int) (attacker.getSkills().getLevelForExperience(Skills.STRENGTH) * 0.20);
						int newStat = (attacker.getSkills().getLevel(Skills.STRENGTH) + boost);
						if(newStat > (attacker.getSkills().getLevelForExperience(Skills.STRENGTH) + boost)) {
							newStat = (attacker.getSkills().getLevelForExperience(Skills.STRENGTH) + boost);
						}
						attacker.getSkills().setLevel(Skills.STRENGTH, newStat);
						int[] stats = {Skills.ATTACK, Skills.DEFENCE, Skills.MAGIC, Skills.RANGE};
						for(int stat : stats) {
							int dec = (int) (attacker.getSkills().getLevelForExperience(stat) * 0.20);
							newStat = (attacker.getSkills().getLevel(stat) - dec);
							if(newStat > (attacker.getSkills().getLevelForExperience(stat) - dec)) {
								newStat = (attacker.getSkills().getLevelForExperience(stat) - dec);
							}
							attacker.getSkills().setLevel(stat, newStat);
						}
						attacker.getCombatState().decreaseSpecial(weapon.getEquipmentDefinition().getSpecialConsumption());
					}
					break;
				}
				return false;
			}
		}
		return false;
	}

	@Override
	public void special(final Mob attacker, final Mob victim, int damage) {
		Item weapon = attacker.getEquipment().get(Equipment.SLOT_WEAPON);
		BowType bow = weapon.getEquipmentDefinition().getBowType();
		if(bow == BowType.BRONZE_CBOW || bow == BowType.IRON_CBOW || bow == BowType.STEEL_CBOW
		|| bow == BowType.MITH_CBOW || bow == BowType.ADAMANT_CBOW || bow == BowType.RUNE_CBOW) {
			weapon = attacker.getEquipment().get(Equipment.SLOT_ARROWS);
		}
		attacker.getCombatState().inverseSpecial();
		switch(weapon.getId()) {
		case 4587: //dscim
			attacker.playGraphics(Graphic.create(347, (100 << 16)));
			attacker.playAnimation(Animation.create(1872));
			if(victim instanceof Player) {
				final Player pVictim = (Player) victim;
				for(int id : Prayers.prayers) {
					if(pVictim.getCombatState().getPrayer(id)) {
						Prayers.deActivatePrayer(pVictim, id);
					}
				}
				pVictim.getActionSender().sendMessage("Your prayers were removed!");
				pVictim.canPray = false;
				World.getWorld().submit(new Event(5000) {
				    public void execute() {
				    	pVictim.canPray = true;
				    	this.stop();
				    }
				});
			}
			break;
		case 1305: //dlong
			attacker.playAnimation(Animation.create(1058));
			attacker.playGraphics(Graphic.create(248, (100 << 16)));
			break;
		case 3204: //dhally
			attacker.playAnimation(Animation.create(1203));
			attacker.playGraphics(Graphic.create(282, (100 << 16)));
			if(victim instanceof NPC) {
				NPC npc = (NPC) victim;
				if(npc.getDefinition().getName().equalsIgnoreCase("kolodion")) {
					if(!(attacker.getActiveCombatAction() instanceof MagicCombatAction)) {
						attacker.getActionSender().sendMessage("You must use magic.");
						return ;
					}
				}
				if(npc.getSize() > 1)
					npc.inflictDamage(new Hit(Misc.random((int)(attacker.getSkills().getLevel(Skills.STRENGTH) / 7))), npc);
			}
			break;
		case 4151:
			attacker.playAnimation(weapon.getEquipmentDefinition().getAnimation().getAttack(0));
			victim.playGraphics(Graphic.create(341, 0, 100));
			break;
		case 1434:
			attacker.playAnimation(Animation.create(1060));
			attacker.playGraphics(Graphic.create(251, (100 << 16)));
			break;
		case 7158:
			attacker.playAnimation(Animation.create(3157));
			attacker.playGraphics(Graphic.create(559));
			for(Player closePlayers : World.getWorld().getPlayers()) {
				Player attk = (Player) attacker;
				if(closePlayers.getLocation().isWithinDistance(attacker.getLocation(), 1)) {
					if(!closePlayers.getName().equalsIgnoreCase(attk.getName())) {
						if(closePlayers != victim && closePlayers != attacker) {
							if(closePlayers.getLocation().isInMulti() && attacker.getLocation().isInMulti()) {
								closePlayers.playAnimation(closePlayers.getDefendAnimation());
								closePlayers.inflictDamage(new Hit(Misc.random(17)), null);
							}
						}
					}
				}
			}
			break;
		case 1215:
		case 1231:
		case 5680:
		case 5698:
			attacker.playAnimation(Animation.create(1062));
			attacker.playGraphics(Graphic.create(252, 0, 100));
			break;
		case 4153:
			attacker.playAnimation(Animation.create(1667));
			attacker.playGraphics(Graphic.create(340, 0, 100));
			break;
		case 11694:
			attacker.playAnimation(Animation.create(7074));
			attacker.playGraphics(Graphic.create(1222, 0, 0));
			break;
		case 11696:
			int[] skills = new int[] {
				Skills.DEFENCE, Skills.STRENGTH, Skills.PRAYER, Skills.ATTACK, Skills.MAGIC, Skills.RANGE			
			};
			int newDmg = damage / 10;
			for(int i = 0; i < skills.length; i++) {
				if(newDmg > 0) {
					if(victim.getSkills().getLevel(skills[i]) > 0) {
						int before = victim.getSkills().getLevel(skills[i]);
						victim.getSkills().decreaseLevelToZero(skills[i], newDmg);
						int after = before - victim.getSkills().getLevel(skills[i]);
						newDmg -= after;
					}
				} else {
					break;
				}
			}
			attacker.playAnimation(Animation.create(7073));
			attacker.playGraphics(Graphic.create(1223, 0, 0));
			break;
		case 11698:
			attacker.playAnimation(Animation.create(7071));
			attacker.playGraphics(Graphic.create(1220, 0, 0));
			int hitpointsHeal = damage / 2;
			int prayerHeal = damage / 4;
			attacker.getSkills().increaseLevelToMaximum(Skills.HITPOINTS, hitpointsHeal);
			attacker.getSkills().increaseLevelToMaximum(Skills.PRAYER, prayerHeal);
			break;
		case 11700:
			attacker.playAnimation(Animation.create(7070));
			attacker.playGraphics(Graphic.create(1221, 0, 0));
			victim.playGraphics(Graphic.create(369, 0, 0));
			int freezeTimer = 33;
			if(victim.getCombatState().canMove() && victim.getCombatState().canBeFrozen()) {
				/*
				 * If the enemy has protect from magic, freeze time is halved.
				 */
				if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC)) {
					freezeTimer = freezeTimer / 2;
				}
				victim.getCombatState().setCanMove(false);
				victim.getCombatState().setCanBeFrozen(false);
				victim.getWalkingQueue().reset();
				if(victim.getActionSender() != null) {
					victim.getActionSender().sendMessage("You have been frozen!");
				}
				World.getWorld().submit(new Tickable(freezeTimer) {
					public void execute() {
						victim.getCombatState().setCanMove(true);
						this.stop();
					}
				});
				World.getWorld().submit(new Tickable(freezeTimer + 5) {
					public void execute() {
						victim.getCombatState().setCanBeFrozen(true);
						this.stop();
					}
				});
			}
			break;
		case 11235:
			attacker.playAnimation(Animation.create(426));
			int clientSpeed;
			int showDelay;
			int slope;
			if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				clientSpeed = 55;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 3)) {
				clientSpeed = 55;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				clientSpeed = 65;
			} else {
				clientSpeed = 75;
			}
			showDelay = 45;
			slope = 15;
			clientSpeed += 30;			
			attacker.playProjectile(Projectile.create(attacker.getLocation(), victim.getCentreLocation(), attacker.getEquipment().get(Equipment.SLOT_ARROWS).getId() == 11212 ? 1099 : 1101, showDelay, 50, clientSpeed - 10, 41, 31, victim.getProjectileLockonIndex(), 3, 36));
			attacker.playProjectile(Projectile.create(attacker.getLocation(), victim.getCentreLocation(), attacker.getEquipment().get(Equipment.SLOT_ARROWS).getId() == 11212 ? 1099 : 1101, showDelay, 50, clientSpeed + 10, 46, 36, victim.getProjectileLockonIndex(), slope + 6, 36));
			victim.playGraphics(Graphic.create(attacker.getEquipment().get(Equipment.SLOT_ARROWS).getId() == 11212 ? 1100 : 1103, clientSpeed, 100));
			break;
		case 861:
			int distance = attacker.getLocation().distanceToEntity(attacker, victim);
			attacker.playAnimation(Animation.create(1074));
			attacker.playGraphics(Graphic.create(256, 0, 100));
			World.getWorld().submit(new Tickable(1) {
				public void execute() {
					attacker.playGraphics(Graphic.create(256, 0, 100));
					this.stop();
				}
			});
			attacker.playProjectile(Projectile.create(attacker.getLocation(), victim.getCentreLocation(), 249, 30, 50, 40 + (distance * 5), 43, 35, victim.getProjectileLockonIndex(), 10, 36));
			attacker.playProjectile(Projectile.create(attacker.getLocation(), victim.getCentreLocation(), 249, 60, 50, 65 + (distance * 5), 43, 35, victim.getProjectileLockonIndex(), 10, 36));
			break;
		case 805:
			Mob[] closeMobs = new Mob[3];
			int[] distances = new int[] { 10, 10, 10 };
			if(BoundaryManager.isWithinBoundaryNoZ(attacker.getLocation(), "MultiCombat")) {
				for(Mob mob : World.getWorld().getRegionManager().getLocalMobs(victim)) {
					boolean canContinue = true;
					for(int i = 0; i < closeMobs.length; i++) {
						if(canContinue) {
							int newDist = attacker.getLocation().distanceToEntity(attacker, mob);
							if(newDist <= distances[i]) {
								if(mob != attacker && mob != victim
											&& mob.getLocation().isWithinDistance(attacker, mob, 5)
											&& attacker.getActiveCombatAction().canHit(attacker, mob, false, false)
											&& BoundaryManager.isWithinBoundaryNoZ(mob.getLocation(), "MultiCombat")) {
									closeMobs[i] = mob;
									distances[i] = newDist;
									canContinue = false;
								}
							}
						} else {
							break;
						}
					}
				}
			}

			int count = 3;
			final int maxHit = CombatFormulae.calculateRangeMaxHit(attacker, true);
			final int newDamage = damage(maxHit, attacker, victim, attacker.getCombatState().getAttackType(), Skills.RANGE, Prayers.PROTECT_FROM_MISSILES, true, false);

			if(BoundaryManager.isWithinBoundaryNoZ(attacker.getLocation(), "MultiCombat")) {
				Mob lastMob = victim;
				int lastDelay = 60;
				int lastSpeed = 90;
				for(final Mob mob : closeMobs) {
					if(mob == null) {
						continue;
					}
					attacker.getCombatState().decreaseSpecial(10);
					lastMob.playProjectile(Projectile.create(lastMob.getLocation(), mob.getCentreLocation(), 258, lastDelay, 50, lastSpeed, 43, 35, mob.getProjectileLockonIndex(), 13, 48));
					World.getWorld().submit(new Tickable(count) {
						@Override
						public void execute() {
							int hit = random.nextInt(newDamage < 1 ? 1 : newDamage + 1); // +1 as its exclusive
							mob.inflictDamage(new Hit(hit), attacker);
							smite(attacker, mob, hit);
							recoil(attacker, mob, hit);
							victim.getActiveCombatAction().defend(attacker, mob, true);
							this.stop();
						}			
					});
					lastDelay += 40;
					lastSpeed += 35;
					lastMob = mob;
					count++;
				}
			}

			attacker.playAnimation(Animation.create(1068));
			attacker.playGraphics(Graphic.create(257, 0, 100));
			attacker.playProjectile(Projectile.create(attacker.getLocation(), victim.getCentreLocation(), 258, 45, 50, 55, 43, 35, victim.getProjectileLockonIndex(), 13, 48));
			attacker.getCombatState().setSpecial(false);
			break;
		case 9236:
			victim.playGraphics(Graphic.create(749));
			break;
		case 9238:
			victim.playGraphics(Graphic.create(750));
			break;
		case 9239:
			victim.playGraphics(Graphic.create(757));
			break;
		case 9240:
			victim.playGraphics(Graphic.create(745, 0, 100));
			if(victim.isPlayer()) {
				int prayerReduction = random.nextInt(9);
				int before = victim.getSkills().getLevel(Skills.PRAYER);
				victim.getSkills().decreaseLevelToZero(Skills.PRAYER, prayerReduction);
				int addition = before - victim.getSkills().getLevel(Skills.PRAYER);
				attacker.getSkills().increaseLevelToMaximum(Skills.PRAYER, addition);
			}
			break;
		case 9241:
			victim.playGraphics(Graphic.create(752));
			victim.getCombatState().setPoisonDamage(6, attacker);
			if(victim.getActionSender() != null) {
				victim.getActionSender().sendMessage("You have been poisoned!");
			}
			break;
		case 9242:
			victim.playGraphics(Graphic.create(754));
			break;
		case 9243:
			victim.playGraphics(Graphic.create(758));
			break;
		case 9244:
			victim.playGraphics(Graphic.create(756));
			break;
		case 9245:
			victim.playGraphics(Graphic.create(753));
			break;
		}
		//System.out.println(weapon.getDefinition().getName() + " special consumption: " + weapon.getEquipmentDefinition().getSpecialConsumption());
		attacker.getCombatState().decreaseSpecial(weapon.getEquipmentDefinition().getSpecialConsumption());
	}

	@Override
	public void special(Mob attacker, final Item item) {
		if(attacker.getInteractingEntity() == null || attacker.getInteractionMode() != InteractionMode.ATTACK) {
			return;
		}
		switch(item.getId()) {
		case 4153:
			attacker.getCombatState().inverseSpecial();
			int damage = CombatFormulae.calculateMeleeMaxHit(attacker, true);
			Hit hit = new Hit(random.nextInt(attacker.getActiveCombatAction().damage(damage, attacker, attacker.getInteractingEntity(), AttackType.CRUSH, Skills.ATTACK, Prayers.PROTECT_FROM_MELEE, true, false) + 1));
			if(hit.getDamage() > attacker.getInteractingEntity().getSkills().getLevel(Skills.HITPOINTS)) {
				hit = new Hit(hit.getDamage());
			}
			attacker.getInteractingEntity().inflictDamage(hit, attacker);
			special(attacker, attacker.getInteractingEntity(), hit.getDamage());
			break;
		}
	}
	
	@Override
	public int damage(int maxHit, Mob attacker, Mob victim, AttackType attackType, int skill, int prayer, boolean special, boolean ignorePrayers) {
		boolean veracEffect = false;
		if(skill == Skills.ATTACK) {
			if(CombatFormulae.fullVerac(attacker)) {
				if(random.nextInt(8) == 3) {
					veracEffect = true;
				}
			}
		}
		
		double attackBonus = attacker.getCombatState().getBonus(attackType.getId()) == 0 ? 1 : attacker.getCombatState().getBonus(attackType.getId());
		if(attackBonus < 1) {
			attackBonus = 1;
		}
		double attackCalc = attackBonus * attacker.getSkills().getLevel(skill); // +1 as its exclusive
		
		/**
		 * Prayer calculations.
		 */
		if(skill == Skills.ATTACK) {
			//melee attack prayer modifiers
			if(attacker.getCombatState().getPrayer(Prayers.CLARITY_OF_THOUGHT)) {
				attackCalc *= 1.05;
			} else if(attacker.getCombatState().getPrayer(Prayers.IMPROVED_REFLEXES)) {
				attackCalc *= 1.10;				
			} else if(attacker.getCombatState().getPrayer(Prayers.INCREDIBLE_REFLEXES)) {
				attackCalc *= 1.15;				
			} else if(attacker.getCombatState().getPrayer(Prayers.CHIVALRY)) {
				attackCalc *= 1.15;				
			} else if(attacker.getCombatState().getPrayer(Prayers.PIETY)) {
				attackCalc *= 1.20;				
			}
		} else if(skill == Skills.RANGE) {
			//range attack prayer modifiers
			if(attacker.getCombatState().getPrayer(Prayers.SHARP_EYE)) {
				attackCalc *= 1.05;
			} else if(attacker.getCombatState().getPrayer(Prayers.HAWK_EYE)) {
				attackCalc *= 1.10;				
			} else if(attacker.getCombatState().getPrayer(Prayers.EAGLE_EYE)) {
				attackCalc *= 1.15;				
			}
		} else if(skill == Skills.MAGIC) {
			//magic attack prayer modifiers
			if(attacker.getCombatState().getPrayer(Prayers.MYSTIC_WILL)) {
				attackCalc *= 1.05;
			} else if(attacker.getCombatState().getPrayer(Prayers.MYSTIC_LORE)) {
				attackCalc *= 1.10;				
			} else if(attacker.getCombatState().getPrayer(Prayers.MYSTIC_MIGHT)) {
				attackCalc *= 1.15;				
			}
		}
		
		/**
		 * As with the melee/range max hit calcs, combat style bonuses are added AFTER the modifiers have taken place.
		 */
		if(attacker.getCombatState().getCombatStyle() == CombatStyle.ACCURATE) {
			attackCalc += 3;
		} else if(attacker.getCombatState().getCombatStyle() == CombatStyle.CONTROLLED_1 
				|| attacker.getCombatState().getCombatStyle() == CombatStyle.CONTROLLED_2 
				|| attacker.getCombatState().getCombatStyle() == CombatStyle.CONTROLLED_3) {
			attackCalc += 1;
		}
		
		double defenceBonus = victim.getCombatState().getBonus(attackType.getId() + 5) == 0 ? 1 : victim.getCombatState().getBonus(attackType.getId() + 5);
		double defenceCalc = defenceBonus * victim.getSkills().getLevel(Skills.DEFENCE); // +1 as its exclusive
		
		/**
		 * Prayer calculations.
		 */
		if(victim.getCombatState().getPrayer(Prayers.THICK_SKIN)) {
			defenceCalc *= 1.05;
		} else if(victim.getCombatState().getPrayer(Prayers.ROCK_SKIN)) {
			defenceCalc *= 1.10;
		} else if(victim.getCombatState().getPrayer(Prayers.STEEL_SKIN)) {
			defenceCalc *= 1.15;
		} else if(victim.getCombatState().getPrayer(Prayers.CHIVALRY)) {
			defenceCalc *= 1.20;
		} else if(victim.getCombatState().getPrayer(Prayers.PIETY)) {
			defenceCalc *= 1.25;
		}
		
		/**
		 * As with the melee/range max hit calcs, combat style bonuses are added AFTER the modifiers have taken place.
		 */
		if(victim.getCombatState().getCombatStyle() == CombatStyle.DEFENSIVE) {
			defenceCalc += 3;
		} else if(victim.getCombatState().getCombatStyle() == CombatStyle.CONTROLLED_1 
				|| victim.getCombatState().getCombatStyle() == CombatStyle.CONTROLLED_2 
				|| victim.getCombatState().getCombatStyle() == CombatStyle.CONTROLLED_3) {
			defenceCalc += 1;
		}
		
		if(veracEffect) {
			defenceCalc = 0;
		}
			
		/**
		 * The chance to succeed out of 1.0.
		 */
		double hitSucceed = CombatFormulae.DEFENCE_MODIFIER * (attackCalc / defenceCalc);
		if(hitSucceed > 1.0) {
			hitSucceed = 1;
		}
		if(hitSucceed < random.nextDouble()) {
			return 0;
		} else {
			/**
			 * Protection prayers.
			 * Note: If an NPC is hitting on a protection prayer, it is 100% blocked, where as if a player is hitting on a protection prayer, their damage is simply reduced by 40%.
			 * Also, if the attacker has the Verac effect active, it will ignore the opponent's protection prayers.
			 */
			int hit = maxHit;
			double protectionPrayer = ((victim.getCombatState().getPrayer(prayer) && !veracEffect) ? attacker.getProtectionPrayerModifier() : 1);
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MELEE) && !veracEffect) {
				protectionPrayer = 0.6;
			}
			if(ignorePrayers) {
				protectionPrayer = 1;
			}
			hit = (int) (maxHit * protectionPrayer); // +1 as its exclusive
			return hit;
		}
	}
	
	@Override
	public void addExperience(Mob attacker, int damage) {
		attacker.getSkills().addExperience(Skills.HITPOINTS, (damage * 1.3) * Constants.EXP_MODIFIER);
	}
	
	@Override
	public void recoil(Mob attacker, Mob victim, int damage) {
		if(victim.getEquipment().get(Equipment.SLOT_RING) != null && victim.getEquipment().get(Equipment.SLOT_RING).getId() == 2550) {
			if(damage > 0) {
				int recoil = (int) Math.ceil(damage / 10);
				if(recoil > victim.getCombatState().getRingOfRecoil()) {
					recoil = victim.getCombatState().getRingOfRecoil();
				}
				if(recoil > attacker.getSkills().getLevel(Skills.HITPOINTS)) {
					recoil = attacker.getSkills().getLevel(Skills.HITPOINTS);
				}
				if(recoil < 1) {
					return;
				}
				victim.getCombatState().setRingOfRecoil(victim.getCombatState().getRingOfRecoil() - recoil);
				attacker.inflictDamage(new Hit(recoil, HitPriority.LOW_PRIORITY), victim);
				if(victim.getCombatState().getRingOfRecoil() < 1) {
					victim.getEquipment().remove(new Item(2550));
					victim.getCombatState().setRingOfRecoil(40);
					victim.getActionSender().sendMessage("Your Ring of Recoil has shattered.");
				}
			}
		}
	}
	
	@Override
	public void smite(Mob attacker, Mob victim, int damage) {
		if(attacker.getCombatState().getPrayer(Prayers.SMITE)) {
			int prayerDrain = (int) (damage * 0.25);
			victim.getSkills().decreasePrayerPoints(prayerDrain);
		}
	}
	
	@Override
	public void vengeance(final Mob attacker, final Mob victim, final int damage, int delay) {
		if(!victim.getCombatState().hasVengeance()) {
			return;
		}
		World.getWorld().submit(new Tickable(delay) {
			@Override
			public void execute() {
				if(damage < 2) {
					return;
				}
				int hit = random.nextInt((int) (damage * 0.75)) + 1;
				if(hit < 1) {
					return;
				}
				if(!victim.getCombatState().isDead()) {
					victim.forceChat("Taste vengeance!");
					victim.getCombatState().setVengeance(false);
					attacker.inflictDamage(new Hit(hit > attacker.getSkills().getLevel(Skills.HITPOINTS) ? attacker.getSkills().getLevel(Skills.HITPOINTS) : hit), victim);
				}
				this.stop();
			}			
		});
	}
}