package org.rs2server.rs2.packet;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.util.LocationChecker;

/**
 * For shoo away of stuff like cats.
 * @author Canownueasy
 *
 */

public class ShooAwayPacket implements PacketHandler {

	@Override
	public void handle(final Player player, Packet packet) {
		int id = packet.getLEShort() & 0xFFFF;
		System.out.println(id);
		if(id < 0 || id >= Constants.MAX_NPCS) {
			return;
		}
		if(player.getCombatState().isDead()) {
			return;
		}
		player.getActionQueue().clearRemovableActions();
		final NPC npc = (NPC) World.getWorld().getNPCs().get(id);
		player.face(npc.getLocation());
		if(npc != null) {
			switch(npc.getDefinition().getId()) {
			case 5917:
			case 5918:
				player.forceChat("Thbbbbt!");
				player.playAnimation(Animation.RASPBERRY);
				npc.playAnimation(Animation.create(4774));
				World.getWorld().submit(new Event(1000) {
					public void execute() {
						/**
						 * We wait 1 second, since the dog needs to realize you are scaring him.
						 */
						//We need to refresh the dog's walking queue.
						LocationChecker.refreshDog(npc);
						//TODO: Get location checker of "dogLocation" method to be clipped.
						//Could use an object dump maybe?
						npc.getWalkingQueue().addStep(LocationChecker.npcLocation(npc, player, 2).getX(), 
								LocationChecker.npcLocation(npc, player, 2).getY());
						stop();
					}
				});
				break;
			}
		}
	}

}
