package org.rs2server.rs2.content.quests.impl;

import org.rs2server.rs2.content.quests.Quest;
import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;

/**
 * Tutorial island quest..
 * @author Canownueasy
 *
 */
public class TutorialIsland implements Quest {
	
	private Player player;
	
	public TutorialIsland(Player player) {
		this.player = player;
	}
	
	public void start() {
		player.setTeleportTarget(Location.create(3093, 3103));
		NPC runescapeGuide = null;
		for(NPC npcs : World.getWorld().getNPCs()) {
			if(npcs != null) {
				if(npcs.getDefinition().getId() == 945) {
					runescapeGuide = npcs;
				}
			}
		}
		player.getActionSender().sendHintArrow(runescapeGuide, 0, 1);
		DialogueManager.openDialogue(player, 560);
	}
	
	public void finish() {
		
	}

}
