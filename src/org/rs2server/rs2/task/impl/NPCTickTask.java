package org.rs2server.rs2.task.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.rs2server.rs2.GameEngine;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.CombatNPCDefinition.GodWarsMinion;
import org.rs2server.rs2.model.Hit.HitPriority;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.task.Task;


/**
 * A task which performs pre-update tasks for an NPC.
 * @author Graham Edgecombe
 *
 */
public class NPCTickTask implements Task {
	
	/**
	 * The npc who we are performing pre-update tasks for.
	 */
	private NPC npc;
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();
	
	/**
	 * Creates the tick task.
	 * @param npc The npc.
	 */
	public NPCTickTask(NPC npc) {
		this.npc = npc;
	}

	@Override
	public void execute(GameEngine context) {
		/*
		 * If the map region changed set the last known region.
		 */
		if(npc.isMapRegionChanging()) {
			npc.setLastKnownRegion(npc.getLocation());
		}
		
		if(npc.getCombatDefinition() != null && npc.getCombatDefinition().isAggressive() && npc.getCombatState().getLastHitTimer() <= System.currentTimeMillis()) {
			List<Mob> enemiesInArea = new ArrayList<Mob>();
			if(npc.getRegion().getPlayers().size() > 0) {
				for(Player player : npc.getRegion().getPlayers()) {
					if(player.getLocation().getZ() != npc.getLocation().getZ()) {
						continue;
					}
					boolean canContinue = true;
					if(npc.getCombatDefinition().getGodWarsTeam() != null) {
						int[] itemsToCheck = new int[0];
						switch(npc.getCombatDefinition().getGodWarsTeam()) {
						case ZAMORAK:
							itemsToCheck = Equipment.ZAMORAK_ITEMS;
							break;
						case SARADOMIN:
							itemsToCheck = Equipment.SARADOMIN_ITEMS;
							break;
						case BANDOS:
							itemsToCheck = Equipment.BANDOS_ITEMS;
							break;
						case ARMADYL:
							itemsToCheck = Equipment.ARMADYL_ITEMS;
							break;
						}
						for(Item item : player.getEquipment().toArray()) {
							if(item != null) {
								for(int i = 0; i < itemsToCheck.length; i++) {
									if(item.getId() == itemsToCheck[i]) {
										canContinue = false;
										break;
									}
								}
							}
						}
					}
					if(!canContinue) {
						continue;
					}
					int npcCombatLvl = 100 * 2;
					if(npc.getDefinition().getId() == 2892) {
						npc.setAggressiveDistance(12);	
					}
					if(player != null
									&& player.getSkills().getCombatLevel() <= npcCombatLvl
									&& !player.getCombatState().isDead()
									&& npc.getActiveCombatAction().canHit(npc, player, true, false)
									&& player.getLocation().isWithinDistance(npc, player, npc.getAggressiveDistance())) {
						enemiesInArea.add(player);
					}
				}
			}
			if(enemiesInArea.size() > 0) {
				int randomPlayer = random.nextInt(enemiesInArea.size());
				Mob p = enemiesInArea.get(randomPlayer);
				npc.getCombatState().startAttacking(p, false);
			} else if(npc.getCombatDefinition().getGodWarsTeam() != null) {
				if(npc.getRegion().getNpcs().size() > 0) {
					for(NPC enemy : npc.getRegion().getNpcs()) {
						if(enemy.getLocation().getZ() != npc.getLocation().getZ()) {
							continue;
						}
						if(enemy.getCombatDefinition() == null) {
							continue;
						}
						boolean canContinue = true;
						if(npc.getCombatDefinition().getGodWarsTeam() != null) {
							switch(npc.getCombatDefinition().getGodWarsTeam()) {
							case ZAMORAK:
								if(enemy.getCombatDefinition().getGodWarsTeam() == GodWarsMinion.ZAMORAK) {
									canContinue = false;
								}
								break;
							case SARADOMIN:
								if(enemy.getCombatDefinition().getGodWarsTeam() == GodWarsMinion.SARADOMIN) {
									canContinue = false;
								}
								break;
							case BANDOS:
								if(enemy.getCombatDefinition().getGodWarsTeam() == GodWarsMinion.BANDOS) {
									canContinue = false;
								}
								break;
							case ARMADYL:
								if(enemy.getCombatDefinition().getGodWarsTeam() == GodWarsMinion.ARMADYL) {
									canContinue = false;
								}
								break;
							}
						}
						if(!canContinue) {
							continue;
						}
						if(enemy != null
										&& !enemy.getCombatState().isDead()
										&& npc.getActiveCombatAction().canHit(npc, enemy, true, false)
										&& enemy.getLocation().isWithinDistance(npc, enemy, 5)) {
							enemiesInArea.add(enemy);
						}
					}
				}
				if(enemiesInArea.size() > 0) {
					int randomPlayer = random.nextInt(enemiesInArea.size());
					Mob p = enemiesInArea.get(randomPlayer);
					npc.getCombatState().startAttacking(p, false);
				}
			}
		}
		
		if(npc.canMove()) {
			if(!npc.isInteracting() && !npc.getCombatState().isDead() && npc.getCombatState().canMove() && random.nextInt(3) == 1) {
				int x = npc.getLocation().getX() + random.nextInt(2) - random.nextInt(2);
				int y = npc.getLocation().getY() + random.nextInt(2) - random.nextInt(2);
				boolean canWalk = true;
				if(npc.getWidth() > 1 || npc.getHeight() > 1) {
					/**
					 * Checks of any of the NPCs tiles are outside of the designated area.
					 */
					for(int offX = 0; offX < npc.getWidth(); offX++) {
						for(int offY = 0; offY < npc.getHeight(); offY++) {
							int offsetX = x + offX;
							int offsetY = y+ offX;
							if(offsetX < npc.getMinLocation().getX() && offsetX > npc.getMaxLocation().getX()
											&& offsetY < npc.getMinLocation().getY() && offsetY > npc.getMaxLocation().getY()) {
								canWalk = false;
							}
						}
					}
				}
				if(canWalk && x >= npc.getMinLocation().getX() && x <= npc.getMaxLocation().getX()
								&& y >= npc.getMinLocation().getY() && y <= npc.getMaxLocation().getY()) {
					npc.getWalkingQueue().addStep(x, y);
					npc.getWalkingQueue().finish();
				}
			}
		}

		if (npc.getCombatState().getAttackDelay() > 0) {
			npc.getCombatState().decreaseAttackDelay(1);
		}
		if (npc.getCombatState().getSpellDelay() > 0) {
			npc.getCombatState().decreaseSpellDelay(1);
		}

		/*
		 * Gets the next two hits from the queue.
		 */
		List<Hit> hits = npc.getHitQueue();
		Hit first = null;
		if (hits.size() > 0) {
			for (int i = 0; i < hits.size(); i++) {
				Hit hit = hits.get(i);
				if (hit.getDelay() < 1) {
					first = hit;
					hits.remove(hit);
					break;
				}
			}
		}
		if (first != null) {
			npc.setPrimaryHit(first);
			npc.getUpdateFlags().flag(UpdateFlag.HIT);
		}
		Hit second = null;
		if (hits.size() > 0) {
			for (int i = 0; i < hits.size(); i++) {
				Hit hit = hits.get(i);
				if (hit.getDelay() < 1) {
					second = hit;
					hits.remove(hit);
					break;
				}
			}
		}
		if (second != null) {
			npc.setSecondaryHit(second);
			npc.getUpdateFlags().flag(UpdateFlag.HIT_2);
		}
		if (hits.size() > 0) {// tells us we still have more hits
			Iterator<Hit> hitIt = hits.iterator();
			while(hitIt.hasNext()) {
				Hit hit = hitIt.next();
				if(hit.getDelay() > 0) {
					hit.setDelay(hit.getDelay() - 1);
				}
				if(hit.getHitPriority() == HitPriority.LOW_PRIORITY) {
					hitIt.remove();
				}
			}
		}
		
		/*
		 * Process the next movement in the NPC's walking queue.
		 */
		npc.getWalkingQueue().processNextMovement();
	}

}
