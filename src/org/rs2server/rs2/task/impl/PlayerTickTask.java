package org.rs2server.rs2.task.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import org.rs2server.rs2.GameEngine;
import org.rs2server.rs2.model.ChatMessage;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Hit.HitPriority;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.task.Task;


/**
 * A task which is executed before an <code>UpdateTask</code>. It is similar to
 * the call to <code>process()</code> but you should use <code>Event</code>s
 * instead of putting timers in this class.
 * @author Graham Edgecombe
 *
 */
public class PlayerTickTask implements Task {
	
	/**
	 * The player.
	 */
	private Player player;
	
	/**
	 * Creates a tick task for a player.
	 * @param player The player to create the tick task for.
	 */
	public PlayerTickTask(Player player) {
		this.player = player;
	}

	@Override
	public void execute(GameEngine context) {
		Queue<ChatMessage> messages = player.getChatMessageQueue();
		if(messages.size() > 0) {
			player.getUpdateFlags().flag(UpdateFlag.CHAT);
			ChatMessage message = player.getChatMessageQueue().poll();
			player.setCurrentChatMessage(message);
		} else {
			player.setCurrentChatMessage(null);
		}
		
		if (player.getCombatState().getAttackDelay() > 0) {
			player.getCombatState().decreaseAttackDelay(1);
		}
		if (player.getCombatState().getSpellDelay() > 0) {
			player.getCombatState().decreaseSpellDelay(1);
		}
		if (player.getCombatState().getWeaponSwitchTimer() > 0) {
			player.getCombatState().decreaseWeaponSwitchTimer(1);
		}
		if (player.getCombatState().getSkullTicks() > 0) {
			player.getCombatState().decreaseSkullTicks(1);			
		}

		/*
		 * Gets the next two hits from the queue.
		 */
		List<Hit> hits = player.getHitQueue();
		Hit first = null;
		if(hits.size() > 0) {
			for(int i = 0; i < hits.size(); i++) {
				Hit hit = hits.get(i);
				if(hit.getDelay() < 1) {
					first = hit;
					hits.remove(hit);
					break;
				}
			}		
		}
		if (first != null) {
			player.setPrimaryHit(first);
			player.getUpdateFlags().flag(UpdateFlag.HIT);
		}
		Hit second = null;
		if(hits.size() > 0) {
			for(int i = 0; i < hits.size(); i++) {
				Hit hit = hits.get(i);
				if(hit.getDelay() < 1) {
					second = hit;
					hits.remove(hit);
					break;
				}
			}
		}
		if (second != null) {
			player.setSecondaryHit(second);
			player.getUpdateFlags().flag(UpdateFlag.HIT_2);
		}
		if(hits.size() > 0) {//tells us we still have more hits
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
		
		player.getWalkingQueue().processNextMovement();
	}

}
