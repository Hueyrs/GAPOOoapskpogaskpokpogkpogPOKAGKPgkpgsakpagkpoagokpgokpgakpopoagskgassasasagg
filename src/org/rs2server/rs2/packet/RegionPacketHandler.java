package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.net.Packet;

/**
 * Player enters a new region.
 * @author Scu11
 *
 */
public class RegionPacketHandler implements PacketHandler {
	
	@Override
	public void handle(Player player, Packet packet) {		
		player.getActionSender().sendGroundItemsInArea();
		player.getActionSender().sendGameObjectsInArea();
	}

}
