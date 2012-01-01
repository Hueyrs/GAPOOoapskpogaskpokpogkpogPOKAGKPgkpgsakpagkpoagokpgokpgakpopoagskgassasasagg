package org.rs2server.rs2.model.combat.npcs;

import java.util.Random;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Projectile;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.util.Misc;


/**
 * Kree Arra
 * @author Canownueasy
 *
 */
public class Kreearra extends AbstractCombatAction {
	
	/**
	 * The singleton instance.
	 */
	private static final Kreearra INSTANCE = new Kreearra();
	
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
	public Kreearra() {
		
	}
	
	private enum CombatStyle {
		MELEE,
		
		MAGIC,
		
		RANGE
	}
	
	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);
		
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		NPC npc = (NPC) attacker;
		
		CombatStyle style = CombatStyle.MELEE;
		
		int maxHit;
		int damage;
		int randomHit;
		int hitDelay;
		boolean blockAnimation;
		final int hit;

		if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			switch(random.nextInt(5)) {
			case 0:
				style = CombatStyle.MELEE;	
				break;
			case 1:
			case 2:
				style = CombatStyle.MAGIC;
				break;
			default:
				style = CombatStyle.RANGE;
				break;
			}
		}
		
		switch(style) {
		case MELEE:
			maxHit = 25;
			attacker.playAnimation(attacker.getAttackAnimation());
			
			hitDelay = 1;
			blockAnimation = true;
			maxHit = npc.getCombatDefinition().getMaxHit();
			damage = damage(maxHit, attacker, victim, attacker.getCombatState().getAttackType(), Skills.ATTACK , Prayers.PROTECT_FROM_MELEE, false, false);
			randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		case MAGIC:
			for(final Player near : World.getWorld().getPlayers()) {
				if(near != null && Misc.getDistance(near.getLocation(), attacker.getLocation()) < 10 && near != victim && near != attacker && near.getSkills().getLevel(Skills.HITPOINTS) > 0) {
					int rClientSpeed;
					int rGfxDelay;
					if(attacker.getLocation().isWithinDistance(attacker, near, 1)) {
						rClientSpeed = 70;
						rGfxDelay = 80;
					} else if(attacker.getLocation().isWithinDistance(attacker, near, 5)) {
						rClientSpeed = 90;
						rGfxDelay = 100;
					} else if(attacker.getLocation().isWithinDistance(attacker, near, 8)) {
						rClientSpeed = 110;
						rGfxDelay = 120;
					} else {
						rClientSpeed = 130;
						rGfxDelay = 140;
					}
					hitDelay = (rGfxDelay / 20) - 1;
					
					attacker.playAnimation(Animation.create(6976));
					attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), near.getCentreLocation(), 1198, 45, 50, rClientSpeed, 43, 35, near.getProjectileLockonIndex(), 10, 48));
					near.playGraphics(Graphic.create(1196, rGfxDelay, 100));

					blockAnimation = false;
					maxHit = (npc.getSkills().getCombatLevel() / 20) + 2;
					final int maxHitMagic = maxHit;
					World.getWorld().submit(new Tickable(hitDelay) {
						public void execute() {
							near.inflictDamage(new Hit(Misc.random(maxHitMagic)), attacker);
							this.stop();
						}
					});
				}
			}
			maxHit = 21;
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
			hitDelay = (gfxDelay / 20) - 1;
			
			attacker.playAnimation(Animation.create(6976));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 1198, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			victim.playGraphics(Graphic.create(1196, gfxDelay, 100));

			blockAnimation = false;
			/*maxHit = (npc.getSkills().getCombatLevel() / 15) + 2;
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC)) {
				maxHit = (npc.getSkills().getCombatLevel() / 20) + 2;
			}*/
			damage = damage(maxHit, attacker, victim, AttackType.MAGIC, Skills.MAGIC , Prayers.PROTECT_FROM_MAGIC, false, true); //these ignore prayers
			randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		default:
		case RANGE:
			maxHit = 71;
			for(final Player near : World.getWorld().getPlayers()) {
				if(near != null && Misc.getDistance(near.getLocation(), attacker.getLocation()) < 10 && near != victim && near != attacker) {
					int rClientSpeed;
					int rGfxDelay;
					if(attacker.getLocation().isWithinDistance(attacker, near, 1)) {
						rClientSpeed = 70;
						rGfxDelay = 80;
					} else if(attacker.getLocation().isWithinDistance(attacker, near, 5)) {
						rClientSpeed = 90;
						rGfxDelay = 100;
					} else if(attacker.getLocation().isWithinDistance(attacker, near, 8)) {
						rClientSpeed = 110;
						rGfxDelay = 120;
					} else {
						rClientSpeed = 130;
						rGfxDelay = 140;
					}
					hitDelay = (rGfxDelay / 20) - 1;
					
					attacker.playAnimation(Animation.create(6976));
					attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), near.getCentreLocation(), 1197, 45, 50, rClientSpeed, 43, 35, near.getProjectileLockonIndex(), 10, 48));
					//near.playGraphics(Graphic.create(1196, rGfxDelay, 100));

					blockAnimation = false;
					if(near.getCombatState().getPrayer(Prayers.PROTECT_FROM_MISSILES)) {
						maxHit = 24;
					} else {
						maxHit = 71;
					}
					final int maxHitRange = maxHit;
					World.getWorld().submit(new Tickable(hitDelay) {
						public void execute() {
							near.inflictDamage(new Hit(Misc.random(maxHitRange)), attacker);
							this.stop();
						}
					});
				}
			}
			int rClientSpeed;
			int rGfxDelay;
			if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				rClientSpeed = 70;
				rGfxDelay = 80;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				rClientSpeed = 90;
				rGfxDelay = 100;
			} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				rClientSpeed = 110;
				rGfxDelay = 120;
			} else {
				rClientSpeed = 130;
				rGfxDelay = 140;
			}
			hitDelay = (rGfxDelay / 20) - 1;
			
			attacker.playAnimation(Animation.create(6976));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 1197, 45, 50, rClientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			//victim.playGraphics(Graphic.create(1196, rGfxDelay, 100));

			blockAnimation = false;
			/*maxHit = (npc.getSkills().getCombatLevel() / 10) + 2;
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MISSILES)) {
				maxHit = (npc.getSkills().getCombatLevel() / 12) + 2;
			}*/
			//damage = damage(maxHit, attacker, victim, AttackType.RANGE, Skills.RANGE , Prayers.PROTECT_FROM_MISSILES, false, true); //these ignore prayers
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MISSILES)) {
				maxHit = 24;
			} else {
				maxHit = 71;
			}
			//randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
			randomHit = Misc.random(maxHit);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		}		
		
		attacker.getCombatState().setAttackDelay(4);
		attacker.getCombatState().setSpellDelay(4);
		
		World.getWorld().submit(new Tickable(hitDelay) {
			@Override
			public void execute() {
				victim.inflictDamage(new Hit(hit), attacker);
				smite(attacker, victim, hit);
				recoil(attacker, victim, hit);
				this.stop();
			}			
		});
		vengeance(attacker, victim, hit, 1);
		
		victim.getActiveCombatAction().defend(attacker, victim, blockAnimation);
	}
	
	@Override
	public int distance(Mob attacker) {
		return 5;
	}
}