package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.net.Packet;

/**
 * Switch item packet handler.
 * @author Graham Edgecombe
 *
 */
public class SwitchItemPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		int fromSlot = packet.getLEShortA();
		packet.getByteA();
		int interfaceId = packet.getLEInt();
		int toSlot = packet.getLEShort();
		if(player.getAttribute("cutScene") != null) {
			return;
		}

		switch(interfaceId) {
		case Bank.PLAYER_INVENTORY_INTERFACE:
		case Inventory.INTERFACE:
			if(fromSlot >= 0 && fromSlot < Inventory.SIZE && toSlot >= 0 && toSlot < Inventory.SIZE && toSlot != fromSlot) {
				player.getInventory().swap(fromSlot, toSlot);
			}
			break;
		case 5832716: //TODO: Find out why the bank interface is using this ID
			if(fromSlot >= 0 && fromSlot < Bank.SIZE && toSlot >= 0 && toSlot < Bank.SIZE && toSlot != fromSlot) {
				if(player.getSettings().isSwapping()) {
					player.getBank().swap(fromSlot, toSlot);
				} else {
					player.getBank().insert(fromSlot, toSlot);
				}
			}
			break;
		}
	}

}
