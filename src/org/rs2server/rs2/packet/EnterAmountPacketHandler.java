package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.net.Packet;

/**
 * A packet sent when the player enters a custom amount for banking etc.
 * @author Graham Edgecombe
 *
 */
public class EnterAmountPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		int amount = packet.getInt();
		if(player.getAttribute("cutScene") != null) {
			return;
		}
		if(player.getInterfaceState().isEnterAmountInterfaceOpen()) {
			player.getInterfaceState().closeEnterAmountInterface(amount);
		}
	}

}
