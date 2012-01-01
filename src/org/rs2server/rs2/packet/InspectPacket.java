package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.net.Packet;

public class InspectPacket implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		player.getActionSender().sendMessage("Your dragonfire shield currently has: " + player.dfsCharges + " charge(s)");
	}
}
