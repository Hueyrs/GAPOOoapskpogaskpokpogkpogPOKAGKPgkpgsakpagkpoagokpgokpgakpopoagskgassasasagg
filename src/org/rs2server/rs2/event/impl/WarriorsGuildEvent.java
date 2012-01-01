package org.rs2server.rs2.event.impl;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.minigame.impl.WarriorsGuild;

/**
 * The event for the warriors guild.
 * @author Canownueasy
 *
 */
public class WarriorsGuildEvent extends Event {

	public WarriorsGuildEvent() {
		super(1000);
	}

	@Override
	public void execute() {
		for(Player players : World.getWorld().getPlayers()) {
			if(players != null) {
				for(String name : World.getWorld().warriorGuildPlayers) {
					if(players.getName().trim().toLowerCase().equalsIgnoreCase(name)) {
						actions(players);
					}
				}
			}
		}
	}
	
	private void actions(Player player) {
		if(player == null) {
			return;
		}
		if(player.getInventory().getCount(8851) < 10) {
			WarriorsGuild.create(player).leaveGame(true);
			return;
		}
		player.guildWaits++;
		if(player.guildWaits >= 60) {
			player.getInventory().remove(new Item(8851, 10));
			player.getActionSender().sendMessage("You were inside the cyclopes cage for another minute, 10 tokens removed.");
			player.guildWaits = 0;
		}
	}
}
