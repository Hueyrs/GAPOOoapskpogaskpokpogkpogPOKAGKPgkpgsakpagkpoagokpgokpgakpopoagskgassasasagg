package org.rs2server.rs2.model.combat.npcs;

import java.util.Random;

import org.rs2server.rs2.content.DragonfireShield;
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
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;


/**
 * Green Dragon combat instance
 * @author Canownueasy
 *
 */
public class KingBlackDragon extends AbstractCombatAction {
	
	/**
	 * The singleton instance.
	 */
	private static final KingBlackDragon INSTANCE = new KingBlackDragon();
	
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
	
	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);
		
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		NPC npc = (NPC) attacker;
		
		int maxHit;
		int damage;
		int randomHit;
		int hitDelay;
		boolean blockAnimation;
		final int hit;
		
		switch(Misc.random(2)) {
		default:
		case 0:
			Animation anim = attacker.getAttackAnimation();
			if(random.nextInt(2) == 1) {
				anim = Animation.create(91);
			}
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
		case 1:
			if(Misc.getDistance(attacker.getLocation(), victim.getLocation()) > 3) {
				return;
			}
			attacker.playAnimation(Animation.create(82));
			attacker.playGraphics(Graphic.create(1, 0, 100));
			if(victim instanceof Player) {
				if(((Player) victim).dfsCharges < 50) {
					DragonfireShield.charge((Player) victim);
				}
			}
			hitDelay = 2;
			blockAnimation = false;
			maxHit = 65;
			if(victim.getEquipment().hasItem(new Item(1540)) || victim.getEquipment().hasItem(new Item(11283))) {
				maxHit = 23;
				victim.getActionSender().sendMessage("Your shield absorbs most of the dragon's firey breath.");
			} else {
				if(Misc.random(3) == 3) {
					maxHit = 35;
					victim.getActionSender().sendMessage("You manage to resist some of the dragon's fire.");
				} else {
					maxHit = 65;
				}
			}
			randomHit = Misc.random(maxHit);
			if(randomHit >= 30) {
				victim.getActionSender().sendMessage("The dragon's fire burns you heavily!");
			}
			hit = randomHit;
			break;
		case 2:
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
			
			attacker.playAnimation(Animation.create(81));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), Misc.random(393, 396), 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));

			blockAnimation = false;
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC)) {
				maxHit = 28;
			} else {
				maxHit = 65;
			}
			randomHit = Misc.random(maxHit);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		}		
		
		attacker.getCombatState().setAttackDelay(5);
		attacker.getCombatState().setSpellDelay(5);
		
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