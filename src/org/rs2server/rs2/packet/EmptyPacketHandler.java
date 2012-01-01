package org.rs2server.rs2.packet;

import org.rs2server.rs2.content.DragonfireShield;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.net.Packet;

public class EmptyPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		DragonfireShield.empty(player);
	}
}