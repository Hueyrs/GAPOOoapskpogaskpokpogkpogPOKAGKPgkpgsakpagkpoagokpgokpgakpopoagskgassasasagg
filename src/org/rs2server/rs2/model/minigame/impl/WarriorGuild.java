package org.rs2server.rs2.model.minigame.impl;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Player;

public class WarriorGuild {
	
	private Player player;
	
	public WarriorGuild(Player player) {
		this.setPlayer(player);
	}
	
	public void summonArmour(Item item) {
		
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

}
