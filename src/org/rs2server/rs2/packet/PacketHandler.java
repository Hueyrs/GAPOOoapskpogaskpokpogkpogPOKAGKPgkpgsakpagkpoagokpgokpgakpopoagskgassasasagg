package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.net.Packet;

/**
 * An interface which describes a class that handles packets.
 * @author Graham Edgecombe
 *
 */
public interface PacketHandler {
	
	/**
	 * Handles a single packet.
	 * @param player The player.
	 * @param packet The packet.
	 */
	public void handle(Player player, Packet packet);

}
