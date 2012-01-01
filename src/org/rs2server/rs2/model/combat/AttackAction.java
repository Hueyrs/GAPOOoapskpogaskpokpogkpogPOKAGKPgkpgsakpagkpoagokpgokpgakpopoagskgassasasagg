package org.rs2server.rs2.model.combat;

import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Mob.InteractionMode;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;

/**
 * The periodic attack event.
 * 
 * @author Graham Edgecombe
 * 
 */
public class AttackAction extends Action {

	/**
	 * Creates the attack event.
	 * 
	 * @param mob
	 *            The entity that is attacking.
	 * @param retaliating
	 *            The retaliation flag.
	 */
	public AttackAction(Mob mob, boolean retaliating) {
		super(mob, retaliating ? 4 : 0);
	}

	@Override
	public CancelPolicy getCancelPolicy() {
		return CancelPolicy.ONLY_ON_WALK;
	}

	@Override
	public StackPolicy getStackPolicy() {
		return StackPolicy.NEVER;
	}

	@Override
	public AnimationPolicy getAnimationPolicy() {
		return AnimationPolicy.RESET_NONE;
	}

	@Override
	public void execute() {
		final Mob mob = getMob();
		if (mob.isDestroyed()) {
			this.stop(); // they disconnected/got removed
			return;
		}

		InteractionMode mode = mob.getInteractionMode();
		Mob target = mob.getInteractingEntity();

		if (target == null || target.isDestroyed() || target.getCombatState().isDead() || mode != InteractionMode.ATTACK) {
			mob.resetInteractingEntity();
			target = null;
			this.stop(); // the target disconnected / got removed
			return;
		}

		if(mob.isNPC()) {
			if (!mob.getLocation().isWithinDistance(mob.getWidth(), mob.getHeight(), target.getLocation(), target.getWidth(), target.getHeight(), mob.getActiveCombatAction().distance(mob) + 5)) {
				mob.resetInteractingEntity();
				this.stop();
				return;
			}
		}

		int requiredDistance = mob.getActiveCombatAction().distance(mob);
		int movementDistance = 0;
		if(mob.getSprites().getPrimarySprite() != -1) {
			movementDistance = 3;
		} else if(mob.getSprites().getSecondarySprite() != -1) {
			movementDistance = 1;
		}
		int distance = mob.getLocation().distanceToEntity(mob, target);
		if (distance > requiredDistance + movementDistance) {
			return;
		}
		
		if(distance <= requiredDistance) { //only reset walking queue when they are exactly in distance, and not still moving
			mob.getWalkingQueue().reset();
		}

		final CombatAction action = mob.getActiveCombatAction();
		if (!action.canHit(mob, target, true, false)) {
			mob.getCombatState().setQueuedSpell(null);
			mob.resetInteractingEntity();
			this.stop();
			return;
		}

		/**
		 * Checks for attack types as spells run on a seperate timer.
		 */
		if (CombatFormulae.getActiveCombatAction(mob) == MagicCombatAction.getAction()) {
			if (mob.getCombatState().getSpellDelay() > 0 || mob.getCombatState().getAttackDelay() > 2) {
				return;
			}
		} else {
			if (mob.getCombatState().getAttackDelay() > 0) {
				return;
			}	
		}

		if (target != null && mode == InteractionMode.ATTACK) {
			attack(target);
			if(CombatFormulae.getActiveCombatAction(mob) != MagicCombatAction.getAction()
							&& mob.getCombatState().getAttackDelay() == 0) {
				mob.getCombatState().setAttackDelay(mob.getCombatCooldownDelay());
			}
		} else {
			this.stop(); // will be restarted later if another attack starts
		}
	}

	/**
	 * Attacks the target, if possible.
	 * @para target The target..
	 */
	private void attack(Mob target) {
		final Mob mob = getMob();
		final CombatAction action = mob.getActiveCombatAction();
		action.hit(mob, target);
		if (action == MagicCombatAction.getAction() && mob.getCombatState().getQueuedSpell() == null && mob.getAutocastSpell() == null) {
			this.stop();
		}
	}

}
