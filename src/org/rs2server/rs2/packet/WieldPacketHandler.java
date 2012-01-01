package org.rs2server.rs2.packet;

import java.util.ArrayList;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.tickable.*;
import org.rs2server.rs2.action.*;
import org.rs2server.rs2.action.impl.*;

/**
 * Handles the 'wield' option on items.
 * @author Graham Edgecombe
 *
 */
public class WieldPacketHandler implements PacketHandler {

	@Override
	public void handle(final Player player, Packet packet) {
		final int slot = packet.getLEShortA() & 0xFF;
		final int id = packet.getShort() & 0xFFFF;
		int interfaceId = packet.getLEInt();
		if(player.getAttribute("cutScene") != null) {
			return;
		}
		if(player.getInterfaceAttribute("fightPitOrbs") != null) {
			return;
		}

		player.getActionSender().sendDebugPacket(packet.getOpcode(), "WieldItem", new Object[] { "ID: " + id, "Interface: " + interfaceId });
		switch(interfaceId) {
		case Inventory.INTERFACE:
			if(slot >= 0 && slot < Inventory.SIZE) {
				if(player.getCombatState().isDead()) {
					return;
				}
				if(player.newSwitch) {
					if(player.queuedSwitch.isEmpty()) {
						final ArrayList<Item> blockedSwitch = new ArrayList<Item>();
						World.getWorld().submit(new Tickable(1) {
							public void execute() {
								if(player.queuedSwitch.size() < 2) {
									for(Item itemz : player.queuedSwitch) {
										if(itemz != null && !blockedSwitch.contains(itemz)) {
											final Action action = new WieldItemAction(player, itemz.getId(), itemz.getSlot(), 0);
											action.execute();
											blockedSwitch.add(itemz);
										}
									}
									blockedSwitch.clear();
									player.queuedSwitch.clear();
									this.stop();
								}
							}
						});
						World.getWorld().submit(new Tickable(2) {
							public void execute() {
								for(Item itemz : player.queuedSwitch) {
									if(itemz != null && !blockedSwitch.contains(itemz) && player.queuedSwitch.size() > 1) {
										final Action action = new WieldItemAction(player, itemz.getId(), itemz.getSlot(), 0);
										action.execute();
										blockedSwitch.add(itemz);
									}
								}
								blockedSwitch.clear();
								player.queuedSwitch.clear();
								this.stop();
							}
						});
					}
					Item item = Item.slot(id, slot);
					player.queuedSwitch.add(item);
					return;
				}
				Item item = player.getInventory().get(slot);
				if(item != null && item.getId() == id) {
					final Action action = new WieldItemAction(player, id, slot, 0);
					if(player.getCombatState().getWeaponSwitchTimer() < 1) {
						action.execute();
					} else {
						World.getWorld().submit(new Tickable(player.getCombatState().getWeaponSwitchTimer()) {
							@Override
							public void execute() {
								action.execute();
								this.stop();
							}							
						});
					}
				}
			}
			break;
		}
	}

}
