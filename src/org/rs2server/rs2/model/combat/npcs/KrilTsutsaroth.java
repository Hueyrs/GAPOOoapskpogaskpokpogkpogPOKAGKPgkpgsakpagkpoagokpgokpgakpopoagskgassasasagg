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
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.util.Misc;


/**
 * Kree Arra
 * @author Canownueasy
 *
 */
public class KrilTsutsaroth extends AbstractCombatAction {
	
	/**
	 * The singleton instance.
	 */
	private static final KrilTsutsaroth INSTANCE = new KrilTsutsaroth();
	
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
	public KrilTsutsaroth() {
		
	}
	
	private enum CombatStyle {
		MELEE,
		
		MAGIC
	}
	
	@SuppressWarnings("unused")
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
			switch(random.nextInt(2)) {
			case 0:
				style = CombatStyle.MELEE;	
				break;
			case 1:
				style = CombatStyle.MAGIC;
				break;
			}
		}
		
		switch(style) {
		case MELEE:
			maxHit = 46;
			attacker.playAnimation(attacker.getAttackAnimation());
			
			hitDelay = 2;
			blockAnimation = true;
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MELEE)) {
				if(Misc.random(8) == 8) {
					maxHit = 49;
					victim.getActionSender().sendMessage("The demon rampages through your prayer!");
				} else {
					maxHit = 22;
				}
			} else {
				maxHit = npc.getCombatDefinition().getMaxHit();
			}
			randomHit = Misc.random(maxHit);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		default:
		case MAGIC:
			maxHit = 30;
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
					attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), near.getCentreLocation(), 1213, 45, 50, rClientSpeed, 43, 35, near.getProjectileLockonIndex(), 10, 48));
					near.playGraphics(Graphic.create(346, rGfxDelay, 100));

					blockAnimation = false;
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
			
			attacker.playAnimation(Animation.create(6945));
			attacker.playGraphics(Graphic.create(1212));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 1213, 45, 50, rClientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			victim.playGraphics(Graphic.create(346, rGfxDelay, 100));

			blockAnimation = false;
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC)) {
				maxHit = 19;
			} else {
				maxHit = 30;
			}
			randomHit = Misc.random(maxHit);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		}		
		
		attacker.getCombatState().setAttackDelay(6);
		attacker.getCombatState().setSpellDelay(6);
		
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