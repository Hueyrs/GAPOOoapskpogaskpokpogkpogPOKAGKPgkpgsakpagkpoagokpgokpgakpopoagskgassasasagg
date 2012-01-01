package org.rs2server.rs2.event.impl;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.NPC;

public class NPCProcessEvent extends Event {

	public NPCProcessEvent() {
		super(600);
	}

	@Override
	public void execute() {
		/*for(NPC npcs : World.getWorld().getNPCs()) {
			if(npcs != null) {
				if(npcs.followingPlayer != null) {
					doActions(npcs);
				}
			}
		}*/
	}
	
	@SuppressWarnings("unused")
	private void doActions(NPC npc) {
		/*if(npc == null || npc.followingPlayer == null) {
			return;
		}
		if(npc.getSkills().getLevel(Skills.HITPOINTS) < 1) {
			npc.followingPlayer = null;
			return;
		}
		if(npc.followingPlayer != null) {
			if(npc != null) {
				if(Misc.getDistance(npc.getLocation().getX(), npc.getLocation().getY(), npc.followingPlayer.getLocation().getX(), npc.followingPlayer.getLocation().getY()) < 2) {
					//npc.getWalkingQueue().reset();
				} else {
					npc.followPlayer(npc.followingPlayer);
				}
			}
		}*/
		/*if(Misc.getDistance(npc.getLocation().getX(), npc.getLocation().getY(), npc.followingPlayer.getLocation().getX(), npc.followingPlayer.getLocation().getY()) < 2) {
			npc.getWalkingQueue().reset();
		} else {
			npc.followPlayer(npc.followingPlayer);
			//npc.walkTo(npc.followingPlayer);
		}*/
	}
}
