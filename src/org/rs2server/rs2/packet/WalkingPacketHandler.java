package org.rs2server.rs2.packet;

import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.net.Packet;

/**
 * A packet which handles walking requests.
 * @author Graham Edgecombe
 *
 */
public class WalkingPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		if(player.getSkills().getLevel(Skills.HITPOINTS) < 1) {
			return;
		}
		int size = packet.getLength();
		if (packet.getOpcode() == 11) {
			size -= 14;
		}
		if(player.getAttribute("cutScene") != null) {
			return;
		}
		if(player.getInterfaceAttribute("fightPitOrbs") != null) {
			return;
		}
		
		if(packet.getOpcode() != 59) { //force walking
			player.getCombatState().setQueuedSpell(null);
			player.resetInteractingEntity();
			player.getActionQueue().clearAllActions();
			player.getActionSender().removeAllInterfaces();
		}


		player.getWalkingQueue().reset();
		
		if(!player.getCombatState().canMove()) {
			if(packet.getOpcode() != 59) { //force walking
				player.getActionSender().sendMessage("A magical force stops you from moving.");
			}
			return;
		}
		if(!player.canEmote()) {
			return; //stops walking during skillcape animations.
		}

		final int steps = (size - 5) / 2;
		final int[][] path = new int[steps][2];

		for (int i = 0; i < steps; i++) {
			final int byte1 = packet.getByte();
			final int byteS = packet.getByteS();
			path[i][0] = byte1;
			path[i][1] = byteS;
		}
		final int firstX = packet.getShortA();
		final int firstY = packet.getLEShort();
		final boolean runSteps = packet.get() == 1;

		player.getWalkingQueue().setRunningQueue(runSteps);
		player.getWalkingQueue().addStep(firstX, firstY);
		for (int i = 0; i < steps; i++) {
			path[i][0] += firstX;
			path[i][1] += firstY;
			player.getWalkingQueue().addStep(path[i][0], path[i][1]);
		}
		player.getWalkingQueue().finish();
		//Boothes.outBooth(player);
	}

}
