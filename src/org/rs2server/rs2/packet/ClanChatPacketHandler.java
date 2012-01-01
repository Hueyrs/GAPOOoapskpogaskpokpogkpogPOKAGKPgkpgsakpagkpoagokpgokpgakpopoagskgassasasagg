package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.PrivateChat;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.PrivateChat.ClanRank;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.NameUtils;

public class ClanChatPacketHandler implements PacketHandler {
	
	@Override
	public void handle(final Player player, Packet packet) {
		long nameAsLong;
		switch(packet.getOpcode()) {
		case 163:
			if(player.getInterfaceState().getClan().length() < 1) {
				player.getActionSender().sendMessage("Attempting to join channel...:clan:");
				nameAsLong = packet.getLong();
				String name = NameUtils.formatName(NameUtils.longToName(nameAsLong));
				if(!World.getWorld().privateIsRegistered(name)) {
					if(!World.getWorld().deserializePrivate(name)) {
						player.getActionSender().sendMessage("The channel you tried to join does not exist.:clan:");
						return;
					}				
				}
				if(World.getWorld().getPrivateChat().get(name).kickedQueueHas(player.getName())) {
					player.getActionSender().sendMessage("You are temporarily banned from this clan channel.");
					return;
				}
				World.getWorld().getPrivateChat().get(name).addClanMember(player);
			} else {
				player.getActionSender().sendMessage("You left the channel.:clan:");
				World.getWorld().getPrivateChat().get(player.getInterfaceState().getClan()).removeClanMember(player);
			}
			break;
		case 250:
			int rank = packet.getByteC();
			nameAsLong = packet.getBigLong();
			player.getPrivateChat().getFriends().put(nameAsLong, ClanRank.forId(rank));
			for(Player p : player.getPrivateChat().getMembers()) {
				if(p.getNameAsLong() == nameAsLong) {
					player.getPrivateChat().updateClanMembers();
				}
			}
			break;		
		case 22:
			nameAsLong = packet.getLong();
			if(player.getInterfaceState().getClan().length() < 1) {
				return;
			}
			final PrivateChat privateChat = World.getWorld().getPrivateChat().get(player.getInterfaceState().getClan());
			if(!player.getName().equals(privateChat.getOwner())) {
				if(!privateChat.getFriends().containsKey(player.getNameAsLong())
								|| (privateChat.getFriends().get(player.getNameAsLong()).getId() < privateChat.getKickRank().getId())) {
					player.getActionSender().sendMessage("You do not have a high enough rank to kick in this clan channel.:clan:");
					return;		
				}
				if(privateChat.getFriends().containsKey(nameAsLong)) {
					if(privateChat.getFriends().get(nameAsLong).getId() > privateChat.getFriends().get(player.getNameAsLong()).getId()) {
						player.getActionSender().sendMessage("You do not have a high enough to kick that person.:clan:");
						return;						
					}
				}
			}
			for(final Player p : privateChat.getMembers()) {
				if(p.getNameAsLong() == nameAsLong) {
					privateChat.removeClanMember(p);
					p.getActionSender().sendMessage("You have been kicked from the channel.:clan:");
					privateChat.addKickedQueue(p.getName());
					World.getWorld().submit(new Tickable(100) {
						public void execute() {
							privateChat.removeKickedQueue(p.getName());
							stop();
						}
					});
					return;
				}
			}
			break;
		}
	}

}
