package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.PrivateChat.ClanRank;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.util.TextUtils;

public class PrivateMessagingPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		long nameAsLong = packet.getLong();
		if(player.getAttribute("cutScene") != null) {
			return;
		}
		switch(packet.getOpcode()) {
		case 252:
			int size = packet.getLength() - 8;
			byte[] data = new byte[size];
			packet.get(data);
			String unpacked = TextUtils.textUnpack(data, size);
			byte[] packed = new byte[size];
			TextUtils.textPack(packed, unpacked);
			player.getPrivateChat().sendMessage(nameAsLong, packed);
			break;
		case 131:
			player.getPrivateChat().addFriend(nameAsLong, ClanRank.FRIEND);
			break;
		case 122:
			player.getPrivateChat().removeFriend(nameAsLong);
			break;
		case 152:
			player.getPrivateChat().addIgnore(nameAsLong);
			break;
		case 1:
			player.getPrivateChat().removeIgnore(nameAsLong);
			break;
		}
	}
		
}