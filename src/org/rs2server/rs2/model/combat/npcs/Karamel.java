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
import org.rs2server.util.Misc;

/**
 * 
 * @author Jesse
 *
 */
public class Karamel extends AbstractCombatAction {
	
	/**
	 * The singleton instance.
	 */
	private static final Karamel INSTANCE = new Karamel();
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();
	/**
	 * Gets the singleton instance.
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}
	
	/**
	 * Default private constructor.
	 */
	public Karamel () {
		
	}
	
	private enum CombatStyle {
		MAGIC,
		
		MELEE
	}
	
	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);
		
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		NPC npc = (NPC) attacker;
		
		CombatStyle style = CombatStyle.MAGIC;
		
		int maxHit;
		int randomHit;
		int hitDelay;
		boolean blockAnimation;
		final int hit;
		
		if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			switch(random.nextInt(2)) {
			default:
			case 0:
				style = CombatStyle.MELEE;	
				break;
			case 1:
				style = CombatStyle.MAGIC;
				break;
			}
		}
		switch(style) {
		default:
		case MELEE:
			attacker.playAnimation(npc.getCombatDefinition().getAttack());
			hitDelay = 2;
			blockAnimation = true;
			maxHit = npc.getCombatDefinition().getMaxHit();
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MELEE)) {
				maxHit = (int) (maxHit * 0.0);
			}
			randomHit = Misc.random(maxHit);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		case MAGIC:
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
			attacker.playAnimation(Animation.create(1979));
			victim.playGraphics(Graphic.create(369, 60));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 368, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			int freezeTimer = 33;
			blockAnimation = false;
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC)) {
				freezeTimer = freezeTimer / 2;
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
				maxHit = 0;
				
			} else {
				maxHit = 30;
				freezeTimer = freezeTimer / 1;
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
		victim.getActiveCombatAction().defend(attacker, victim, blockAnimation);
	}
	
	@Override
	public int distance(Mob attacker) {
		return 5;
	}
}