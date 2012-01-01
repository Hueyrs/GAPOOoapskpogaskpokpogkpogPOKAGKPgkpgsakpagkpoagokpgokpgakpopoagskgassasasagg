package org.rs2server.rs2.model.combat.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.ScriptManager;
import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Entity;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.ItemDefinition;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatFormulae;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.CombatState.CombatStyle;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.equipment.PoisonType;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.tickable.impl.SkillsUpdateTick;
import org.rs2server.rs2.util.NameUtils;


/**
 * Normal magic combat action.
 * @author Graham Edgecombe
 *
 */
public class MagicCombatAction extends AbstractCombatAction {
	
	/**
	 * The singleton instance.
	 */
	private static final MagicCombatAction INSTANCE = new MagicCombatAction();
	
	/**
	 * Gets the singleton instance.
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();
	
	/**
	 * Default private constructor.
	 */
	private MagicCombatAction() {
		
	}
	
	@Override
	public boolean canHit(Mob attacker, Mob victim, boolean messages, boolean cannon) {
		if(!super.canHit(attacker, victim, messages, cannon)) {
			return false;
		}
		if(cannon) {
			return true;
		}
		if(attacker.getAutocastSpell() != null) {
			attacker.getCombatState().setQueuedSpell(attacker.getAutocastSpell());
		}
		return canCastSpell(attacker, victim, attacker.getCombatState().getQueuedSpell());
	}
	
	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);
		
		if(attacker.getAutocastSpell() == null && attacker.getActionSender() != null) {
			attacker.getActionSender().sendResetFollowing();
		}

		attacker.getCombatState().setCurrentSpell(attacker.getCombatState().getQueuedSpell());
		attacker.getCombatState().setQueuedSpell(null);
		
		Spell spell = attacker.getCombatState().getCurrentSpell();
		int clientSpeed;
		int gfxDelay;
		if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			clientSpeed = 70;
			gfxDelay = 80;
		} else if(attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
			clientSpeed = 90;
			gfxDelay = 100;
		} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
			clientSpeed = 110;
			gfxDelay = 120;
		} else {
			clientSpeed = 130;
			gfxDelay = 140;
		}
		int delay = (gfxDelay / 20) - 1;
		
		executeSpell(spell, attacker, attacker, victim, spell, attacker.getAutocastSpell() != null, clientSpeed, INSTANCE, gfxDelay, delay);
	}

	@Override
	public void defend(Mob attacker, Mob victim, boolean blockAnimation) {
		super.defend(attacker, victim, blockAnimation);
	}

	@Override
	public boolean canSpecial(Mob attacker, Mob victim) {
		return super.canSpecial(attacker, victim);
	}

	@Override
	public void special(Mob attacker, Mob victim, int damage) {
		super.special(attacker, victim, damage);
	}
	
	@Override
	public void special(Mob attacker, final Item item) {
		super.special(attacker, item);
	}
	
	@Override
	public int distance(Mob attacker) {
		return 10;
	}
	
	@Override
	public int damage(int maxHit, Mob attacker, Mob victim, AttackType attackType, int skill, int prayer, boolean special, boolean ignorePrayers) {
		double attackBonus = attacker.getCombatState().getBonus(attackType.getId()) == 0 ? 1 : attacker.getCombatState().getBonus(attackType.getId());
		if(attackBonus < 1) {
			attackBonus = 1;
		}
		double attackCalc =  attackBonus * attacker.getSkills().getLevel(skill); // +1 as its exclusive
		double defenceBonus = victim.getCombatState().getBonus(attackType.getId() + 5) == 0 ? 1 : victim.getCombatState().getBonus(attackType.getId() + 5);	
		if(defenceBonus < 1) {
			defenceBonus = 1;
		}	
		double defenceCalc = defenceBonus * victim.getSkills().getLevel(Skills.DEFENCE); // +1 as its exclusive
		
		/**
		 * Prayer calculations.
		 */
		if(attacker.getCombatState().getPrayer(Prayers.MYSTIC_WILL)) {
			attackCalc *= 1.05;
		} else if(attacker.getCombatState().getPrayer(Prayers.MYSTIC_LORE)) {
			attackCalc *= 1.10;				
		} else if(attacker.getCombatState().getPrayer(Prayers.MYSTIC_MIGHT)) {
			attackCalc *= 1.15;				
		}
		
		
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
		
		/**
		 * The chance to succeed out of 1.0.
		 */
		double hitSucceed = CombatFormulae.DEFENCE_MODIFIER * (attackCalc / defenceCalc);
		if(hitSucceed > 1.0) {
			hitSucceed = 1;
		}
		if(hitSucceed < random.nextDouble()) {
			return -1; //NOTE: For magic, we return -1. This is because if a spell has a max hit of 0, its damage will always return 0, saying that it has failed, when it hasn't!
		} else {
			/**
			 * Protection prayers.
			 * Note: If an NPC is hitting on a protection prayer, it is 100% blocked, where as if a player is hitting on  a protection prayer, their damage is simply reduced by 40%.
			 */
			int hit = maxHit; // +1 as its exclusive
			if(victim.getCombatState().getPrayer(prayer) && !ignorePrayers) {
				if(attacker.getProtectionPrayerModifier() == 0) {
					hit = -1;
				} else {
					hit = (int) (hit * attacker.getProtectionPrayerModifier());
				}
			}
			if(hit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				hit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			return hit;
		}
	}
	
	@Override
	public void addExperience(Mob attacker, int damage) {
		super.addExperience(attacker, damage);
		if(attacker.getCombatState().getCombatStyle() == CombatStyle.DEFENSIVE_AUTOCAST) {
			attacker.getSkills().addExperience(Skills.MAGIC, (1.33 * damage) * Constants.EXP_MODIFIER);			
			attacker.getSkills().addExperience(Skills.DEFENCE, (1 * damage) * Constants.EXP_MODIFIER);			
		} else {
			attacker.getSkills().addExperience(Skills.MAGIC, (2 * damage) * Constants.EXP_MODIFIER);
		}
	}
	
	@Override
	public void recoil(Mob attacker, Mob victim, int damage) {
		super.recoil(attacker, victim, damage);
	}
	
	@Override
	public void smite(Mob attacker, Mob victim, int damage) {
		super.smite(attacker, victim, damage);
	}
	
	public static void executeSpell(Spell spell, final Mob mob, Object... args) {
		if(spell.getSpellName().contains("Teleport")) {
			if(!mob.canTeleport()) {
				return;
			}
		}
		if(!canCastSpell(mob, null, spell)) {
			return;
		}
		if(!checkRunes(mob, spell)) {
			return;
		}
		if(spell == Spell.HOME_TELEPORT_MODERN || spell == Spell.HOME_TELEPORT_LUNAR || spell == Spell.HOME_TELEPORT_ANCIENT) {
			if(BoundaryManager.isWithinBoundaryNoZ(mob.getLocation(), World.getWorld().getFightPits().getBoundary()) || BoundaryManager.isWithinBoundaryNoZ(mob.getLocation(), "Fight Pits Waiting Room")) {
				if(mob.getActionSender() != null) {
					mob.getActionSender().sendMessage("You can't teleport in here.");
				}
				return;
			}
			mob.getWalkingQueue().reset();
			mob.getActionQueue().addAction(new Action(mob, 2) {
				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ALWAYS;
				}
				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.ALWAYS;
				}
				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}
				@Override
				public void execute() {
					mob.playAnimation(Animation.create(4847));
					mob.playGraphics(Graphic.create(800));
					this.stop();
				}
			});
			mob.getActionQueue().addAction(new Action(mob, 7) {
				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ALWAYS;
				}
				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.ALWAYS;
				}
				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}
				@Override
				public void execute() {
					mob.playAnimation(Animation.create(4850));
					mob.playGraphics(Graphic.create(801));
					this.stop();
				}
			});
			mob.getActionQueue().addAction(new Action(mob, 11) {
				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ALWAYS;
				}
				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.ALWAYS;
				}
				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}
				@Override
				public void execute() {
					mob.playAnimation(Animation.create(4853));
					mob.playGraphics(Graphic.create(802));
					this.stop();
				}
			});
			mob.getActionQueue().addAction(new Action(mob, 13) {
				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ALWAYS;
				}
				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.ALWAYS;
				}
				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}
				@Override
				public void execute() {
					mob.playAnimation(Animation.create(4855));
					mob.playGraphics(Graphic.create(803));
					this.stop();
				}
			});
			mob.getActionQueue().addAction(new Action(mob, 17) {
				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ALWAYS;
				}
				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.ALWAYS;
				}
				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}
				@Override
				public void execute() {
					mob.playAnimation(Animation.create(4857));
					mob.playGraphics(Graphic.create(804));
					this.stop();
				}
			});
			mob.getActionQueue().addAction(new Action(mob, 19) {
				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ALWAYS;
				}
				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.ALWAYS;
				}
				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}
				@Override
				public void execute() {
					mob.setTeleportTarget(Entity.DEFAULT_LOCATION);
					this.stop();
				}
			});
		} else if(spell == Spell.SPELLBOOK_SWAP && !mob.getCombatState().spellbookSwap()) {
			if(mob.isPlayer()) {
				DialogueManager.openDialogue((Player) mob, 12);
			}
		} else if (ScriptManager.getScriptManager().invokeWithFailTest(spell.getSpellName(), args)) {		
			for(int i = 0; i < spell.getRunes().length; i++) {
				if(mob.getInventory() != null && spell.getRune(i) != null) {
					if(deleteRune(mob, spell.getRune(i))) {
						if(mob.getInventory() != null) {
							mob.getInventory().remove(spell.getRune(i));
						}
					}
				}			
			}
			if(mob.getCombatState().spellbookSwap() && spell != Spell.SPELLBOOK_SWAP) {
				mob.getActionSender().sendSidebarInterface(105, MagicCombatAction.SpellBook.LUNAR_MAGICS.getInterfaceId());
				mob.getCombatState().setSpellBook(MagicCombatAction.SpellBook.LUNAR_MAGICS.getSpellBookId());
				mob.getCombatState().setSpellbookSwap(false);
			}
		} else if(mob.getActionSender() != null) {
			mob.getActionSender().sendMessage("Spell script missing: " + spell.getSpellName() + ".");
		}
	}
	
	public static boolean canCastSpell(final Mob attacker, Mob victim, Spell spell) {
		if(attacker.getSkills().getLevel(Skills.MAGIC) < spell.getLevelRequired()) {
			if(attacker.getActionSender() != null) {
				attacker.getActionSender().sendMessage("Your Magic level is not high enough for this spell.");
			}
			return false;
		}
		if(!attacker.isNPC()) {
			switch(spell.getSpellBook()) {
			case MODERN_MAGICS:
				if(attacker.getCombatState().getSpellBook() == SpellBook.ANCIENT_MAGICKS.getSpellBookId()) {
					attacker.getActionSender().sendMessage("You can only cast modern spells whilst on the modern spellbook.");					
					return false;
				}
				if(spell == Spell.CHARGE) {
					if(attacker.getCombatState().isCharged()) {
						if(attacker.getActionSender() != null) {
							attacker.getActionSender().sendMessage("Your current charge is too powerful.");
						}
						return false;
					}
				}
				break;
			case ANCIENT_MAGICKS:
				if(attacker.getCombatState().getSpellBook() == SpellBook.MODERN_MAGICS.getSpellBookId()) {
					attacker.getActionSender().sendMessage("You can only cast ancient spells whilst on the ancient spellbook.");
					return false;
				}
				break;
			case LUNAR_MAGICS:
				if(attacker.getSkills().getLevelForExperience(Skills.DEFENCE) < 45) {
					if(attacker.getActionSender() != null) {
						attacker.getActionSender().sendMessage("You need a Defence level of 45 to use Lunar spells.");
					}
					return false;
				}
				if(spell == Spell.SPELLBOOK_SWAP) {
					if(attacker.getCombatState().spellbookSwap()) {
						if(attacker.isPlayer()) {
							if(((Player) attacker).getInterfaceState().getOpenDialogueId() != 13
									&& ((Player) attacker).getInterfaceState().getOpenDialogueId() != 14) {
								attacker.getActionSender().sendMessage("You are already swapping your spellbook.");
								return false;
							} else {
								if(attacker.getActionSender() != null) {
									attacker.getActionSender().sendMessage("You have 2 minutes before your spellbook changes back to the Lunar Spellbook!");
								}
								World.getWorld().submit(new Tickable(200) {
									@Override
									public void execute() {
										if(attacker.getCombatState().spellbookSwap()) {
											attacker.getActionSender().sendSidebarInterface(105, MagicCombatAction.SpellBook.LUNAR_MAGICS.getInterfaceId());
											attacker.getCombatState().setSpellBook(MagicCombatAction.SpellBook.LUNAR_MAGICS.getSpellBookId());
											attacker.getCombatState().setSpellbookSwap(false);
										}
									}							
								});
							}
						}
					}
					if(!attacker.getCombatState().canSpellbookSwap()) {
						if(attacker.getActionSender() != null) {
							attacker.getActionSender().sendMessage("You can only cast spellbook swap every 2 minutes.");
						}
						return false;
					}
				}
				if(spell == Spell.VENGEANCE || spell == Spell.VENGEANCE_OTHER) {
					if(!attacker.getCombatState().canVengeance()) {
						if(attacker.getActionSender() != null) {
							attacker.getActionSender().sendMessage("You can only cast vengeance spells every 30 seconds.");
						}
						return false;
					}			
				}
				break;
			}
		}
		if(spell.getSpellName().contains("Tele") && !spell.getSpellName().contains("Other")) {
			if(attacker.getCombatState().isTeleblocked()) {
				if(attacker.getActionSender() != null) {
					attacker.getActionSender().sendMessage("A magical force stops you from teleporting.");
				}
				return false;
			}
		}
		if(!checkRunes(attacker, spell)) {
			return false;
		}
		/**
		 * Extra items required for casting.
		 */
		if(spell.getRequiredItem() != null) {
			if(attacker.isPlayer() && attacker.getEquipment().getCount(spell.getRequiredItem().getId()) < spell.getRequiredItem().getCount()) {
				attacker.getActionSender().sendMessage("You must be wielding a " + ItemDefinition.forId(spell.getRequiredItem().getId()).getName() + " to cast this spell.");
				return false;								
			}
		}
		switch(spell) {
		case CONFUSE:
		case STUN:
			//return true;
			if(victim.isAttackDrained()) {
				attacker.getActionSender().sendMessage("Your foe's attack has already been weakened.");
				return false;
			}
			break;
		case WEAKEN:
		case ENFEEBLE:
			if(victim.isStrengthDrained()) {
				attacker.getActionSender().sendMessage("Your foe's strength has already been weakened.");
				return false;
			}
			break;
		case CURSE:
		case VULNERABILITY:
			if(victim.isDefenceDrained()) {
				attacker.getActionSender().sendMessage("Your foe's defence has already been weakened.");
				return false;
			}
			break;
		case CRUMBLE_UNDEAD:
			if(!victim.getDefinedName().toLowerCase().contains("skeleton") && !victim.getDefinedName().toLowerCase().contains("zombie") && !victim.getDefinedName().toLowerCase().contains("shade") && !victim.getDefinedName().toLowerCase().contains("ghost")) {
				if(attacker.getActionSender() != null) {
					attacker.getActionSender().sendMessage("This spell only affects skeletons, zombies, ghosts and shades.");
				}
				return false;				
			}
			break;
		case TELE_BLOCK:
			if(!victim.isPlayer()) {
				if(attacker.getActionSender() != null) {
					attacker.getActionSender().sendMessage("Nothing interesting happens.");
				}
				return false;	
			} else if(victim.getCombatState().isTeleblocked()) {
				if(attacker.getActionSender() != null) {
					attacker.getActionSender().sendMessage("That player is already teleblocked.");
				}
				return false;	
			}
			break;
		}
		return true;
	}
	
	public void hitEnemy(final Mob attacker, Mob victim, final Spell spell, final Graphic graphic, final PoisonType poisonType, boolean multi, int maxDamage, final int delay, final int freezeTimer) {
		if(!BoundaryManager.isWithinBoundaryNoZ(attacker.getLocation(), "MultiCombat")) {
			multi = false;
		}
		final ArrayList<Mob> enemies = new ArrayList<Mob>();
		ArrayList<Location> locationsUsed = new ArrayList<Location>();
		enemies.add(victim);
		locationsUsed.add(victim.getLocation());
		if(multi) {
			for(Mob mob : victim.getRegion().getMobs()) {
				if(mob != attacker && mob != victim && mob.getLocation().isWithinDistance(attacker, victim, 1)
						&& !locationsUsed.contains(mob.getLocation()) && super.canHit(attacker, mob, false, false)
						&& BoundaryManager.isWithinBoundaryNoZ(mob.getLocation(), "MultiCombat")) {
					enemies.add(mob);
					locationsUsed.add(mob.getLocation());
				}
			}
		}
		final ArrayList<Integer> hits = new ArrayList<Integer>();
		for(int i = 0; i < enemies.size(); i++) {
			final Mob enemy = enemies.get(i);
			int hit = damage(maxDamage, attacker, enemy, AttackType.MAGIC, Skills.MAGIC, Prayers.PROTECT_FROM_MAGIC, false, false); // +1 as its exclusive
			if(graphic != null) {
				enemy.playGraphics(hit < 0 ? Graphic.create(85, graphic.getDelay(), 100) : graphic);
			}
			if(hit > -1) {
				hit = (hit > 0 ? random.nextInt(hit) : 0) + (maxDamage > 0 ? 1 : 0); //1 because we started at -1
				if(hit > 0) {		
					
					if(hit > enemy.getSkills().getLevel(Skills.HITPOINTS)) {
						hit = enemy.getSkills().getLevel(Skills.HITPOINTS);
					}
					
					//freezing happens immediately
					if(freezeTimer > 0) {
						if(enemy.getCombatState().canMove() && enemy.getCombatState().canBeFrozen()) {
							/*
							 * If the enemy has protect from magic, freeze time is halved.
							 */
							int finalTimer = freezeTimer;
							if(enemy.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC)) {
								finalTimer = freezeTimer / 2;
							}
							enemy.getCombatState().setCanMove(false);
							enemy.getCombatState().setCanBeFrozen(false);
							enemy.getWalkingQueue().reset();
							if(enemy.getActionSender() != null) {
								enemy.getActionSender().sendMessage("You have been frozen!");
							}
							World.getWorld().submit(new Tickable(finalTimer + delay) {
								public void execute() {
									enemy.getCombatState().setCanMove(true);
									this.stop();
								}
							});
							World.getWorld().submit(new Tickable(finalTimer + delay + 5) {
								public void execute() {
									enemy.getCombatState().setCanBeFrozen(true);
									this.stop();
								}
							});
						}
					}
					
					if(CombatFormulae.fullAhrim(attacker)) {
						int ahrim = random.nextInt(8);
						if(ahrim == 3) {
							victim.getSkills().decreaseLevelToMinimum(Skills.STRENGTH, 5);
							victim.playGraphics(Graphic.create(400, 0, 100));
						}
					}
				}
			}
			addExperience(attacker, hit < 0 ? 0 : hit);
			hits.add(i, hit);
		}
		World.getWorld().submit(new Tickable(delay) {
			public void execute() {
				for(int i = 0; i < enemies.size(); i++) {
					final Mob enemy = enemies.get(i);
					int hit = hits.get(i);

					if(hit > enemy.getSkills().getLevel(Skills.HITPOINTS)) {//check again for hp, as it could of changed between the time of delay
						hit = enemy.getSkills().getLevel(Skills.HITPOINTS);
					}
					
					if(hit > 0) {
						//only inflict damage if the hit is > 0
						enemy.inflictDamage(new Hit(hit), attacker);
						smite(attacker, enemy, hit);
						recoil(attacker, enemy, hit);
						vengeance(attacker, enemy, hit, 1);
					}
					
					enemy.getActiveCombatAction().defend(attacker, enemy, false);
					
					if(hit != -1) { //the effect will not occur if we have splashed
						if(poisonType != PoisonType.NONE) {
							if(enemy.getCombatState().getPoisonDamage() < 1 && enemy.getCombatState().canBePoisoned() && random.nextInt(11) == 3) {
								enemy.getCombatState().setPoisonDamage(poisonType.getRangeDamage(), attacker);
								if(enemy.getActionSender() != null) {
									enemy.getActionSender().sendMessage("You have been poisoned!");
								}
							}
						}
											
						int drainedLevel = -1;
						double drainModifier = 0;
						switch(spell) {
						case SHADOW_RUSH:
						case SHADOW_BURST:
							enemy.getSkills().decreaseLevelOnce(Skills.ATTACK, (int) (enemy.getSkills().getLevelForExperience(Skills.ATTACK) * 0.10));
							break;
						case SHADOW_BLITZ:
						case SHADOW_BARRAGE:
							enemy.getSkills().decreaseLevelOnce(Skills.ATTACK, (int) (enemy.getSkills().getLevelForExperience(Skills.ATTACK) * 0.15));
							break;
						case BLOOD_RUSH:
						case BLOOD_BURST:
						case BLOOD_BLITZ:
						case BLOOD_BARRAGE:
							int heal = (int) (hit * 0.25);
							attacker.getSkills().increaseLevelToMaximum(Skills.HITPOINTS, heal);
							if(attacker.getActionSender() != null) {
								attacker.getActionSender().sendMessage("You drain some of your opponent's health.");
							}
							break;
						case TELE_BLOCK:
							enemy.getCombatState().setTeleblocked(true);
							World.getWorld().submit(new Tickable(500) {
								public void execute() {
									enemy.getCombatState().setTeleblocked(false);
									this.stop();
								}
							});							
							break;
						case SARADOMIN_STRIKE:
							enemy.getSkills().decreasePrayerPoints(1);
							break;
						case CLAWS_OF_GUTHIX:
							enemy.getSkills().decreaseLevelOnce(Skills.DEFENCE, (int) (enemy.getSkills().getLevelForExperience(Skills.DEFENCE) * 0.5));
							break;
						case FLAMES_OF_ZAMORAK:
							enemy.getSkills().decreaseLevelOnce(Skills.MAGIC, 5);
							break;
						case CONFUSE:
							drainedLevel = Skills.ATTACK;
							drainModifier = 0.05;
							enemy.setAttackDrained(true);
							break;
						case WEAKEN:
							drainedLevel = Skills.STRENGTH;
							drainModifier = 0.05;
							enemy.setStrengthDrained(true);
							break;
						case CURSE:
							drainedLevel = Skills.DEFENCE;
							drainModifier = 0.05;
							enemy.setDefenceDrained(true);
							break;
						case STUN:
							drainedLevel = Skills.ATTACK;
							drainModifier = 0.10;
							enemy.setAttackDrained(true);
							break;
						case ENFEEBLE:
							drainedLevel = Skills.STRENGTH;
							drainModifier = 0.10;
							enemy.setStrengthDrained(true);
							break;
						case VULNERABILITY:
							drainedLevel = Skills.DEFENCE;
							drainModifier = 0.10;
							enemy.setDefenceDrained(true);
							break;
						}
						if(drainedLevel != -1 && drainModifier != 0) {
							int levelBefore = enemy.getSkills().getLevel(drainedLevel);
							enemy.getSkills().decreaseLevelOnce(drainedLevel, (int) (drainModifier * enemy.getSkills().getLevelForExperience(drainedLevel)));
							int levelDifference = levelBefore - enemy.getSkills().getLevel(drainedLevel);
							if(levelDifference < 1) {
								levelDifference = 1;
							}
							World.getWorld().submit(new Tickable(levelDifference * SkillsUpdateTick.CYCLE_TIME) {						
								public void execute() {
									switch(spell) {
									case CONFUSE:
									case STUN:
										enemy.setAttackDrained(false);
										break;
									case WEAKEN:
									case ENFEEBLE:
										enemy.setStrengthDrained(false);
										break;
									case CURSE:
									case VULNERABILITY:
										enemy.setDefenceDrained(false);
										break;
									}
								}						
							});
						}
					}
				}
				attacker.getCombatState().setCurrentSpell(null);
				this.stop();
			}
		});
	}

	public static boolean checkRunes(Mob mob, Spell spell) {
		for(int i = 0; i < spell.getRunes().length; i++) {
			if(spell.getRune(i) != null) {
				if(deleteRune(mob, spell.getRune(i))) {
					if(mob.getInventory() != null && mob.getInventory().getCount(spell.getRune(i).getId()) < spell.getRune(i).getCount()) {
						String runeName = NameUtils.formatName(ItemDefinition.forId(spell.getRune(i).getId()).getName().toLowerCase());
						if(mob.getActionSender() != null) {
							mob.getActionSender().sendMessage("You do not have enough " + runeName + "s to cast this spell.");
						}
						return false;								
					}
				}
			}			
		}
		return true;
	}
	
	public static void setAutocast(Mob mob, Spell spell, int config, boolean defensive) {
		if(defensive) {
			config += 93;
		}
		if(mob.getEquipment() != null && spell != null) {
			mob.getEquipment().fireItemChanged(Equipment.SLOT_WEAPON);
		}
		if(spell == null) {//we don't want to set our atk style back to accurate if we aren't already autocasting
			if(mob.getActionSender() != null) {
				mob.getActionSender().sendConfig(439, 3);
				mob.getActionSender().sendInterfaceConfig(90, 83, false); //Shows the spellbook icon
				mob.getActionSender().sendInterfaceConfig(90, 176, false); //Shows the spellbook icon
				if(mob.getInterfaceState().getLastUsedAutocast() != -1) {
					mob.getActionSender().sendInterfaceConfig(90, mob.getInterfaceState().getLastUsedAutocast(), true);					
				}
			}
			if(mob.getCombatState().getCombatStyle() == CombatStyle.AUTOCAST //only if we are still on the autocast mode, do we reset our attack types.
							|| mob.getCombatState().getCombatStyle() == CombatStyle.DEFENSIVE_AUTOCAST) {
				mob.getCombatState().setAttackType(AttackType.CRUSH);
				mob.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				if(mob.getActionSender() != null) {
					mob.getActionSender().sendConfig(43, 0);
				}
			}
		} else if(spell != null) {
			mob.getCombatState().setCombatStyle(defensive ? CombatStyle.DEFENSIVE_AUTOCAST : CombatStyle.AUTOCAST);
			mob.getInterfaceState().setLastUsedAutocast(config);
			if(mob.getActionSender() != null) {
				mob.getActionSender().sendConfig(43, 3);
				mob.getActionSender().sendConfig(439, defensive ? -5 : 3);
				mob.getActionSender().sendInterfaceConfig(90, defensive ? 176 : 83, true); //Hides the spellbook icon
				mob.getActionSender().sendInterfaceConfig(90, config, false); //Shows the spell icon
			}
		}
		mob.setAutocastSpell(spell);
	}
	
	public static final int FIRE_RUNE = 554, 
		WATER_RUNE = 555, AIR_RUNE = 556, 
		EARTH_RUNE = 557, MIND_RUNE = 558, 
		BODY_RUNE = 559, DEATH_RUNE = 560, 
		NATURE_RUNE = 561, CHAOS_RUNE = 562, 
		LAW_RUNE = 563, COSMIC_RUNE = 564,
		BLOOD_RUNE = 565, SOUL_RUNE = 566,
		ASTRAL_RUNE = 9075;

	/**
	 * Represents spells.
	 * @author Michael Bull
	 *
	 */
	public static enum Spell {

		/**
		 * Wind Strike.
		 */
		WIND_STRIKE(1, 1, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(AIR_RUNE, 1), new Item(MIND_RUNE, 1) }, "windStrike", null),

		/**
		 * Water Strike.
		 */
		WATER_STRIKE(4, 5, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(WATER_RUNE, 1), new Item(AIR_RUNE, 1), new Item(MIND_RUNE, 1) }, "waterStrike", null),
		
		/**
		 * Earth Strike.
		 */
		EARTH_STRIKE(6, 9, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(EARTH_RUNE, 2), new Item(AIR_RUNE, 1), new Item(MIND_RUNE, 1) }, "earthStrike", null),
		
		/**
		 * Fire Strike.
		 */
		FIRE_STRIKE(8, 13, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(FIRE_RUNE, 3), new Item(AIR_RUNE, 2), new Item(MIND_RUNE, 1) }, "fireStrike", null),
		
		/**
		 * Wind Bolt.
		 */
		WIND_BOLT(10, 17, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(AIR_RUNE, 2), new Item(CHAOS_RUNE, 1) }, "windBolt", null),
		
		/**
		 * Water Bolt.
		 */
		WATER_BOLT(14, 23, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(AIR_RUNE, 2), new Item(WATER_RUNE, 2), new Item(CHAOS_RUNE, 1) }, "waterBolt", null),
		
		/**
		 * Earth Bolt.
		 */
		EARTH_BOLT(17, 29, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(AIR_RUNE, 2), new Item(EARTH_RUNE, 3), new Item(CHAOS_RUNE, 1) }, "earthBolt", null),
		
		/**
		 * Fire Bolt.
		 */
		FIRE_BOLT(20, 35, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(AIR_RUNE, 3), new Item(WATER_RUNE, 5), new Item(CHAOS_RUNE, 1) }, "fireBolt", null),
		
		/**
		 * Wind Blast.
		 */
		WIND_BLAST(24, 41, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(AIR_RUNE, 3), new Item(DEATH_RUNE, 1) }, "windBlast", null),
		
		/**
		 * Water Blast.
		 */
		WATER_BLAST(27, 47, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(AIR_RUNE, 3), new Item(WATER_RUNE, 3), new Item(DEATH_RUNE, 1) }, "waterBlast", null),
		
		/**
		 * Earth Blast.
		 */
		EARTH_BLAST(33, 53, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(AIR_RUNE, 3), new Item(EARTH_RUNE, 4), new Item(DEATH_RUNE, 1) }, "earthBlast", null),
		
		/**
		 * Fire Blast.
		 */
		FIRE_BLAST(38, 59, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(AIR_RUNE, 4), new Item(FIRE_RUNE, 5), new Item(DEATH_RUNE, 1) }, "fireBlast", null),

		/**
		 * Wind Wave.
		 */
		WIND_WAVE(45, 62, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(AIR_RUNE, 5), new Item(BLOOD_RUNE, 1) }, "windWave", null),
		
		/**
		 * Water Wave.
		 */
		WATER_WAVE(48, 65, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(AIR_RUNE, 5), new Item(WATER_RUNE, 7), new Item(BLOOD_RUNE, 1) }, "waterWave", null),
		
		/**
		 * Earth Wave.
		 */
		EARTH_WAVE(52, 70, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(AIR_RUNE, 5), new Item(EARTH_RUNE, 7), new Item(BLOOD_RUNE, 1) }, "earthWave", null),
		
		/**
		 * Fire Wave.
		 */
		FIRE_WAVE(55, 75, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(AIR_RUNE, 5), new Item(FIRE_RUNE, 7), new Item(BLOOD_RUNE, 1) }, "fireWave", null),
		
		/**
		 * Saradomin Strike.
		 */
		SARADOMIN_STRIKE(41, 60, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(FIRE_RUNE, 2), new Item(BLOOD_RUNE, 2), new Item(AIR_RUNE, 4) }, "saradominStrike", new Item(2415)),
		
		/**
		 * Claws of Guthix.
		 */
		CLAWS_OF_GUTHIX(42, 60, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(FIRE_RUNE, 1), new Item(BLOOD_RUNE, 2), new Item(AIR_RUNE, 4) }, "clawsOfGuthix", new Item(2416)),
		
		/**
		 * Flames of Zamorak.
		 */
		FLAMES_OF_ZAMORAK(43, 60, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(FIRE_RUNE, 4), new Item(BLOOD_RUNE, 2), new Item(AIR_RUNE, 1) }, "flamesOfZamorak", new Item(2417)),

		/**
		 * Iban Blast.
		 */
		IBAN_BLAST(29, 50, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(FIRE_RUNE, 5), new Item(DEATH_RUNE, 1) }, "ibanBlast", new Item(1409)),
		
		/**
		 * Magic Dart.
		 */
		MAGIC_DART(31, 50, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(MIND_RUNE, 4), new Item(DEATH_RUNE, 1) }, "magicDart", new Item(4170)),
		
		/**
		 * Crumble Undead.
		 */
		CRUMBLE_UNDEAD(22, 39, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(EARTH_RUNE, 2), new Item(AIR_RUNE, 2), new Item(CHAOS_RUNE, 1) }, "crumbleUndead", null),
		
		/**
		 * Smoke Rush.
		 */
		SMOKE_RUSH(8, 50, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 2), new Item(CHAOS_RUNE, 2), new Item(FIRE_RUNE, 1) }, "smokeRush", null),
		
		/**
		 * Shadow Rush.
		 */
		SHADOW_RUSH(12, 52, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 2), new Item(CHAOS_RUNE, 2), new Item(SOUL_RUNE, 1) }, "shadowRush", null),
		
		/**
		 * Blood Rush.
		 */
		BLOOD_RUSH(4, 56, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 2), new Item(CHAOS_RUNE, 2), new Item(BLOOD_RUNE, 1) }, "bloodRush", null),
		
		/**
		 * Ice Rush.
		 */
		ICE_RUSH(0, 58, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 2), new Item(CHAOS_RUNE, 2), new Item(WATER_RUNE, 2) }, "iceRush", null),
		
		/**
		 * Smoke Burst.
		 */
		SMOKE_BURST(10, 62, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 2), new Item(CHAOS_RUNE, 4), new Item(AIR_RUNE, 2) }, "smokeBurst", null),
		
		/**
		 * Shadow Burst.
		 */
		SHADOW_BURST(14, 64, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 2), new Item(CHAOS_RUNE, 4), new Item(AIR_RUNE, 2), new Item(AIR_RUNE, 2) }, "shadowBurst", null),
		
		/**
		 * Blood Burst.
		 */
		BLOOD_BURST(6, 68, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 2), new Item(CHAOS_RUNE, 4), new Item(BLOOD_RUNE, 2) }, "bloodBurst", null),
		
		/**
		 * Ice Burst.
		 */
		ICE_BURST(2, 70, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 2), new Item(CHAOS_RUNE, 4), new Item(WATER_RUNE, 2) }, "iceBurst", null),
		
		/**
		 * Smoke Blitz.
		 */
		SMOKE_BLITZ(9, 74, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 2), new Item(FIRE_RUNE, 2), new Item(BLOOD_RUNE, 2), new Item(AIR_RUNE, 2) }, "smokeBlitz", null),
		
		/**
		 * Shadow Blitz.
		 */
		SHADOW_BLITZ(13, 76, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 2), new Item(BLOOD_RUNE, 2), new Item(AIR_RUNE, 2), new Item(SOUL_RUNE, 2) }, "shadowBlitz", null),
		
		/**
		 * Blood Blitz.
		 */
		BLOOD_BLITZ(5, 80, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 2), new Item(BLOOD_RUNE, 4) }, "bloodBlitz", null),
		
		/**
		 * Ice Blitz.
		 */
		ICE_BLITZ(1, 82, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 2), new Item(BLOOD_RUNE, 2), new Item(WATER_RUNE, 3) }, "iceBlitz", null),
		
		/**
		 * Smoke Barrage.
		 */
		SMOKE_BARRAGE(11, 86, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 4), new Item(BLOOD_RUNE, 2), new Item(AIR_RUNE, 4), new Item(FIRE_RUNE, 4) }, "smokeBarrage", null),
		
		/**
		 * Shadow Barrage.
		 */
		SHADOW_BARRAGE(15, 88, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 4), new Item(BLOOD_RUNE, 2), new Item(AIR_RUNE, 4), new Item(SOUL_RUNE, 3) }, "shadowBarrage", null),
		
		/**
		 * Blood Barrage.
		 */
		BLOOD_BARRAGE(7, 92, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 4), new Item(BLOOD_RUNE, 4), new Item(SOUL_RUNE, 1) }, "bloodBarrage", null),
		
		/**
		 * Ice Barrage.
		 */
		ICE_BARRAGE(3, 94, SpellBook.ANCIENT_MAGICKS, SpellType.COMBAT, new Item[] { new Item(DEATH_RUNE, 4), new Item(BLOOD_RUNE, 2), new Item(WATER_RUNE, 6) }, "iceBarrage", null),
		
		/**
		 * Tele-block.
		 */
		TELE_BLOCK(60, 85, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(LAW_RUNE, 1), new Item(CHAOS_RUNE, 1), new Item(DEATH_RUNE, 1) }, "teleBlock", null),
		
		/**
		 * Bind.
		 */
		BIND(12, 20, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(EARTH_RUNE, 3), new Item(WATER_RUNE, 3), new Item(NATURE_RUNE, 2) }, "bind", null),
		
		/**
		 * Snare.
		 */
		SNARE(30, 50, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(EARTH_RUNE, 4), new Item(WATER_RUNE, 4), new Item(NATURE_RUNE, 3) }, "snare", null),
		
		/**
		 * Entangle.
		 */
		ENTANGLE(56, 79, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(EARTH_RUNE, 5), new Item(WATER_RUNE, 5), new Item(NATURE_RUNE, 4) }, "entangle", null),
		
		/**
		 * Confuse.
		 */
		CONFUSE(2, 3, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(WATER_RUNE, 3), new Item(EARTH_RUNE, 2), new Item(BODY_RUNE, 1) }, "confuse", null),
		
		/**
		 * Weaken.
		 */
		WEAKEN(7, 11, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(WATER_RUNE, 3), new Item(EARTH_RUNE, 2), new Item(BODY_RUNE, 1) }, "weaken", null),
		
		/**
		 * Curse.
		 */
		CURSE(11, 19, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(WATER_RUNE, 2), new Item(EARTH_RUNE, 3), new Item(BODY_RUNE, 1) }, "curse", null),
		
		/**
		 * Vulnerability.
		 */
		VULNERABILITY(50, 76, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(WATER_RUNE, 6), new Item(EARTH_RUNE, 6), new Item(SOUL_RUNE, 1) }, "vulnerability", null),
		
		/**
		 * Enfeeble.
		 */
		ENFEEBLE(53, 73, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(WATER_RUNE, 8), new Item(EARTH_RUNE, 8), new Item(SOUL_RUNE, 1) }, "enfeeble", null),
		
		/**
		 * Stun.
		 */
		STUN(57, 80, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] { new Item(WATER_RUNE, 12), new Item(EARTH_RUNE, 12), new Item(SOUL_RUNE, 1) }, "stun", null),
		
		/**
		 * Varrock teleport.
		 */
		VARROCK_TELEPORT(15, 25, SpellBook.MODERN_MAGICS, SpellType.NON_COMBAT, new Item[] { new Item(LAW_RUNE, 1), new Item(FIRE_RUNE, 1), new Item(AIR_RUNE, 3) }, "varrockTeleport", null),
		
		/**
		 * Lumbridge teleport.
		 */
		LUMBRIDGE_TELEPORT(18, 31, SpellBook.MODERN_MAGICS, SpellType.NON_COMBAT, new Item[] { new Item(LAW_RUNE, 1), new Item(EARTH_RUNE, 1), new Item(AIR_RUNE, 3) }, "lumbridgeTeleport", null),
		
		/**
		 * Falador teleport.
		 */
		FALADOR_TELEPORT(21, 37, SpellBook.MODERN_MAGICS, SpellType.NON_COMBAT, new Item[] { new Item(LAW_RUNE, 1), new Item(WATER_RUNE, 1), new Item(AIR_RUNE, 3) }, "faladorTeleport", null),
		
		/**
		 * Camelot teleport.
		 */
		CAMELOT_TELEPORT(26, 45, SpellBook.MODERN_MAGICS, SpellType.NON_COMBAT, new Item[] { new Item(LAW_RUNE, 1), new Item(AIR_RUNE, 5) }, "camelotTeleport", null),
		
		/**
		 * Ardougne teleport.
		 */
		ARDOUGNE_TELEPORT(32, 51, SpellBook.MODERN_MAGICS, SpellType.NON_COMBAT, new Item[] { new Item(LAW_RUNE, 2), new Item(WATER_RUNE, 2) }, "ardougneTeleport", null),
		
		/**
		 * Watchtower teleport.
		 */
		WATCHTOWER_TELEPORT(37, 58, SpellBook.MODERN_MAGICS, SpellType.NON_COMBAT, new Item[] { new Item(LAW_RUNE, 2), new Item(EARTH_RUNE, 2) }, "watchtowerTeleport", null),
		
		/**
		 * Trollheim teleport.
		 */
		TROLLHEIM_TELEPORT(44, 61, SpellBook.MODERN_MAGICS, SpellType.NON_COMBAT, new Item[] { new Item(LAW_RUNE, 2), new Item(FIRE_RUNE, 2) }, "trollheimTeleport", null),
		
		/**
		 * Paddewwa teleport.
		 */
		PADDEWWA_TELEPORT(16, 54, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT, new Item[] { new Item(LAW_RUNE, 2), new Item(FIRE_RUNE, 2) }, "paddewwaTeleport", null),
		
		/**
		 * Senntisten teleport.
		 */
		SENNTISTEN_TELEPORT(17, 60, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT, new Item[] { new Item(LAW_RUNE, 2), new Item(SOUL_RUNE, 1) }, "senntistenTeleport", null),
		
		/**
		 * Kharyrll teleport.
		 */
		KHARYRLL_TELEPORT(18, 66, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT, new Item[] { new Item(LAW_RUNE, 2), new Item(BLOOD_RUNE, 1) }, "kharyrllTeleport", null),
		
		/**
		 * Lassar teleport.
		 */
		LASSAR_TELEPORT(19, 72, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT, new Item[] { new Item(LAW_RUNE, 2), new Item(WATER_RUNE, 4) }, "lassarTeleport", null),
		
		/**
		 * Dareeyak teleport.
		 */
		DAREEYAK_TELEPORT(20, 78, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT, new Item[] { new Item(LAW_RUNE, 2), new Item(FIRE_RUNE, 3), new Item(AIR_RUNE, 2) }, "dareeyakTeleport", null),
		
		/**
		 * Carrallangar teleport.
		 */
		CARRALLANGAR_TELEPORT(21, 84, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT, new Item[] { new Item(LAW_RUNE, 2), new Item(SOUL_RUNE, 2) }, "carrallangarTeleport", null),
		
		/**
		 * Annakarl teleport.
		 */
		ANNAKARL_TELEPORT(22, 90, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT, new Item[] { new Item(LAW_RUNE, 2), new Item(BLOOD_RUNE, 2) }, "annakarlTeleport", null),
		
		/**
		 * Ghorrock teleport.
		 */
		GHORROCK_TELEPORT(23, 96, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT, new Item[] { new Item(LAW_RUNE, 2), new Item(WATER_RUNE, 8) }, "ghorrockTeleport", null),
		
		/**
		 * Vengeance
		 */
		VENGEANCE(14, 94, SpellBook.LUNAR_MAGICS, SpellType.NON_COMBAT, new Item[] { new Item(ASTRAL_RUNE, 4), new Item(DEATH_RUNE, 2), new Item(EARTH_RUNE, 10) }, "vengeance", null),
		
		/**
		 * Vengeance Other
		 */
		VENGEANCE_OTHER(19, 93, SpellBook.LUNAR_MAGICS, SpellType.NON_COMBAT, new Item[] { new Item(ASTRAL_RUNE, 4), new Item(DEATH_RUNE, 2), new Item(EARTH_RUNE, 10) }, "vengeanceOther", null),
		
		/**
		 * Spellbook Swap
		 */
		SPELLBOOK_SWAP(12, 96, SpellBook.LUNAR_MAGICS, SpellType.NON_COMBAT, new Item[] { new Item(ASTRAL_RUNE, 3), new Item(COSMIC_RUNE, 2), new Item(LAW_RUNE, 1) }, "spellbookSwap", null),

		/**
		 * Home teleport modern
		 */
		HOME_TELEPORT_MODERN(0, 1, SpellBook.MODERN_MAGICS, SpellType.NON_COMBAT, new Item[0], "homeTeleport", null),

		/**
		 * Home teleport lunar
		 */
		HOME_TELEPORT_LUNAR(16, 1, SpellBook.LUNAR_MAGICS, SpellType.NON_COMBAT, new Item[0], "homeTeleport", null),

		/**
		 * Home teleport ancient
		 */
		HOME_TELEPORT_ANCIENT(24, 1, SpellBook.ANCIENT_MAGICKS, SpellType.NON_COMBAT, new Item[0], "homeTeleport", null),

		/**
		 * Charge
		 */
		CHARGE(58, 80, SpellBook.MODERN_MAGICS, SpellType.NON_COMBAT, new Item[] { new Item(FIRE_RUNE, 3), new Item(BLOOD_RUNE, 3), new Item(AIR_RUNE, 3) } , "charge", null),

		/**
		 * Bandos spiritual mage
		 */
		BANDOS_SPIRITUAL_MAGE(900, 1, SpellBook.MODERN_MAGICS, SpellType.COMBAT, new Item[] {  } , "bandosSpiritualMage", null)

		;
		
		/**
		 * A map of spell IDs.
		 */
		private static List<Spell> spells = new ArrayList<Spell>();
		
		/**
		 * Gets a spell by its ID.
		 * @param spell The Spell id.
		 * @return The spell, or <code>null</code> if the id is not a spell.
		 */
		public static Spell forId(int spellId, SpellBook spellBook) {
			for(Spell spell : spells) {
				if(spell.getSpellBook().getSpellBookId() == spellBook.getSpellBookId() && spell.getSpellId() == spellId) {
					return spell;
				}
			}
			return null;
		}

		/**
		 * Populates the prayer map.
		 */
		static {
			for(Spell spell : Spell.values()) {
				spells.add(spell);
			}
		}

		/**
		 * The id of this spell.
		 */
		private int id;

		/**
		 * The level required to use this spell.
		 */
		private int levelRequired;
		
		/**
		 * The spellbook this spell is on.
		 */
		private SpellBook spellBook;
		
		/**
		 * The type of spell this is.
		 */
		private SpellType spellType;

		/**
		 * The runes required for this spell.
		 */
		private Item[] runes;
		
		/**
		 * The spell's name for script parsing..
		 */
		private String spellName;
		
		/**
		 * The item required to cast this spell.
		 */
		private Item requiredItem;

		/**
		 * Creates the spell.
		 * @param id The spell id.
		 * @return 
		 */
		private Spell(int id, int levelRequired, SpellBook spellBook, SpellType spellType, Item[] runes, String spellName, Item requiredItem) {
			this.id = id;
			this.levelRequired = levelRequired;
			this.spellBook = spellBook;
			this.spellType = spellType;
			this.runes = runes;
			this.spellName = spellName;
			this.requiredItem = requiredItem;
		}

		/**
		 * Gets the spell id.
		 * @return The spell id.
		 */
		public int getSpellId() {
			return id;
		}
				
		/**
		 * Gets the level required to use this spell.
		 * @return The level required to use this spell.
		 */
		public int getLevelRequired() {
			return levelRequired;
		}
		
		/**
		 * Gets the spell book this spell is on.
		 * @return The spell book this spell is on.
		 */
		public SpellBook getSpellBook() {
			return spellBook;
		}

		/**
		 * @return the spellType
		 */
		public SpellType getSpellType() {
			return spellType;
		}
				
		/**
		 * Gets the runes required for this spell.
		 * @return The runes required for this spell.
		 */
		public Item[] getRunes() {
			return runes;
		}
		
		/**
		 * Gets the rune required for this spell by its index.
		 * @return The rune required for this spell by its index.
		 */
		public Item getRune(int index) {
			return runes[index];
		}

		/**
		 * Gets the spell's name for script parsing.
		 * @return the spellName
		 */
		public String getSpellName() {
			return spellName;
		}

		/**
		 * Gets the required item to cast this spell.
		 * @return The required item to cast this spell.
		 */
		public Item getRequiredItem() {
			return requiredItem;
		}
	}

	/**
	 * Represents spell books.
	 * @author Michael Bull
	 *
	 */
	public static enum SpellBook {
		MODERN_MAGICS(0, 192),
		
		ANCIENT_MAGICKS(1, 193),
		
		LUNAR_MAGICS(2, 430)
		;
		
		/**
		 * A map of spell book IDs.
		 */
		private static Map<Integer, SpellBook> spellBooks = new HashMap<Integer, SpellBook>();
		
		/**
		 * Gets a spell book by its ID.
		 * @param spellBook The Spell book id.
		 * @return The spell book, or <code>null</code> if the id is not a spell book.
		 */
		public static SpellBook forId(int spellBook) {
			return spellBooks.get(spellBook);
		}

		/**
		 * Populates the spell book map.
		 */
		static {
			for(SpellBook spellBook : SpellBook.values()) {
				spellBooks.put(spellBook.id, spellBook);
			}
		}

		/**
		 * The id of this spell book.
		 */
		private int id;

		/**
		 * The interface id of this spell book.
		 */
		private int interfaceId;

		/**
		 * Creates the spell book.
		 * @param id The spellBook id.
		 * @return 
		 */
		private SpellBook(int id, int interfaceId) {
			this.id = id;
			this.interfaceId = interfaceId;
		}

		/**
		 * Gets the spellBook id.
		 * @return The spellBook id.
		 */
		public int getSpellBookId() {
			return id;
		}

		/**
		 * @return The interfaceId.
		 */
		public int getInterfaceId() {
			return interfaceId;
		}
	}

	/**
	 * An enum containing different spell types.
	 * @author Michael
	 *
	 */
	public enum SpellType {
		COMBAT,
		
		NON_COMBAT;
	}
	
	public static boolean deleteRune(Mob mob, Item rune) {
		int id = rune.getId();
		if (!(id == 556 && mob.getEquipment().get(3) != null && mob
				.getEquipment().get(3).getId() == 1381)
				&& !(id == 555 && mob.getEquipment().get(3) != null && mob
						.getEquipment().get(3).getId() == 1383)
				&& !(id == 557 && mob.getEquipment().get(3) != null && mob
						.getEquipment().get(3).getId() == 1385)
				&& !(id == 554 && mob.getEquipment().get(3) != null && (mob
						.getEquipment().get(3).getId() == 1387 || mob
						.getEquipment().get(3).getId() == 3053))
				&& !(id == 555 && mob.getEquipment().get(3) != null && mob
						.getEquipment().get(3).getId() == 6563)
				&& !(id == 557 && mob.getEquipment().get(3) != null && (mob
						.getEquipment().get(3).getId() == 6563 || mob
						.getEquipment().get(3).getId() == 3053))) {
			return true;
		}
		return false;
	}
}
