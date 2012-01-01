package org.rs2server.rs2.model.combat.npcs;

import java.util.Random;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Hit.HitType;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;


/**
 * Commander Zilyana
 * @author Canownueasy
 *
 */
public class CommanderZilyana extends AbstractCombatAction {
	
	/**
	 * The singleton instance.
	 */
	private static final CommanderZilyana INSTANCE = new CommanderZilyana();
	
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
	public CommanderZilyana() {
		
	}
	
	private enum CombatStyle {
		MELEE,
		
		MAGIC
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
			switch(random.nextInt(3)) {
			case 0:
			case 1:
				style = CombatStyle.MELEE;	
				break;
			case 2:
				style = CombatStyle.MAGIC;
				break;
			}
		}
		
		switch(style) {
		case MELEE:
			Animation anim = attacker.getAttackAnimation();
			attacker.playAnimation(anim);
			
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
		default:
		case MAGIC:
			attacker.playAnimation(Animation.create(6967));
			victim.playGraphics(Graphic.create(1207, 60));

			maxHit = 32;
			final int maxx = maxHit;
			World.getWorld().submit(new Tickable(2) {
				public void execute() {
					for(final Player near : World.getWorld().getPlayers()) {
						if(near != null && near != attacker && near != victim && near.getSkills().getLevel(Skills.HITPOINTS) > 0) {
							if(Misc.getDistance(near.getLocation(), attacker.getLocation()) <= 10) {
								near.playGraphics(Graphic.create(1207));
								World.getWorld().submit(new Tickable(1) {
									public void execute() {
										int maxHit = maxx;
										if(near.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC)) {
											maxHit = 12;
										}
										int lowestHit = (int)(maxHit / 3);
										int randomHit = Misc.random(lowestHit, maxHit);
										near.inflictDamage(new Hit(randomHit > 0 ? HitType.NORMAL_HIT : HitType.ZERO_DAMAGE_HIT, randomHit), near);
										int preDouble = (int)(randomHit / 2);
										int secondHit = Misc.random(preDouble);
										near.inflictDamage(new Hit(secondHit > 0 ? HitType.NORMAL_HIT : HitType.ZERO_DAMAGE_HIT, secondHit), near);
										attacker.getSkills().increaseLevel(Skills.HITPOINTS, Misc.random(3));
										stop();
									}
								});
							}
						}
					}
					stop();
				}
			});
			
			hitDelay = 3;
			blockAnimation = false;
			maxHit = 32;
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC)) {
				maxHit = 12;
			}
			int lowestHit = (int)(maxHit / 3);
			int randomHitz = Misc.random(lowestHit, maxHit);
			if(randomHitz > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHitz;
			break;
		}		
		
		attacker.getCombatState().setAttackDelay(npc.getCombatDefinition().getCombatCooldownDelay());
		attacker.getCombatState().setSpellDelay(npc.getCombatDefinition().getCombatCooldownDelay());
		
		World.getWorld().submit(new Tickable(hitDelay) {
			@Override
			public void execute() {
				victim.inflictDamage(new Hit(hit > 0 ? HitType.NORMAL_HIT : HitType.ZERO_DAMAGE_HIT, hit), attacker);
				int preDouble = (int)(hit / 2);
				int secondHit = Misc.random(preDouble);
				victim.inflictDamage(new Hit(secondHit > 0 ? HitType.NORMAL_HIT : HitType.ZERO_DAMAGE_HIT, secondHit), victim);
				attacker.getSkills().increaseLevel(Skills.HITPOINTS, Misc.random(3));
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