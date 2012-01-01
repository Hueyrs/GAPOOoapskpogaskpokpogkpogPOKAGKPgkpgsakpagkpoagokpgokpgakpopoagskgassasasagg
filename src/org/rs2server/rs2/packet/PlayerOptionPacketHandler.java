package org.rs2server.rs2.packet;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.Mob.InteractionMode;
import org.rs2server.rs2.model.RequestManager.RequestType;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.Spell;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.SpellBook;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.SpellType;
import org.rs2server.rs2.net.Packet;

public class PlayerOptionPacketHandler implements PacketHandler {

	private static final int OPTION_ATTACK = 96, OPTION_2 = 37, OPTION_3 = 185, OPTION_4 = 247, OPTION_SPELL = 35;
	
	@Override
	public void handle(Player player, Packet packet) {
		if(player.getAttribute("cutScene") != null) {
			return;
		}
		if(player.getInterfaceAttribute("fightPitOrbs") != null) {
			return;
		}
		switch(packet.getOpcode()) {
		case OPTION_ATTACK:
			optionAttack(player, packet);
			break;
		case OPTION_2:
			option2(player,  packet);
			break;
		case OPTION_3:
			option3(player, packet);
			break;
		case OPTION_4:
			option4(player, packet);
			break;
		case OPTION_SPELL:
			optionSpell(player, packet);
			break;
		}
	}

	/**
	 * Handles the first option on a player option menu.
	 * @param player
	 * @param packet
	 */
	private void optionAttack(final Player player, Packet packet) {
		int id = packet.getLEShortA();
		if(id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		if(player.getCombatState().isDead()) {
			return;
		}
		Player victim = (Player) World.getWorld().getPlayers().get(id);
		if(player.getMinigame() != null) {
			if(!player.getMinigame().attackMobHook(player, victim)) {
				return;
			}
		}
		if(victim != null && victim != player) {
			player.getCombatState().setQueuedSpell(null);
			player.getCombatState().startAttacking(victim, false);
		}
		player.getActionSender().sendDebugPacket(packet.getOpcode(), "PlayerAttack", new Object[] { "Index: " + id });
	}
	
	/**
	 * Handles the second option on a player option menu.
	 * @param player
	 * @param packet
	 */
	private void option2(Player player, Packet packet) {
		int id = packet.getShort() & 0xFFFF;
		if(id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		if(player.getCombatState().isDead()) {
			return;
		}
		player.getActionSender().sendDebugPacket(packet.getOpcode(), "Opt2", new Object[] { "Index: " + id });
	}
	
	/**
	 * Handles the third option on a player option menu.
	 * @param player
	 * @param packet
	 */
	private void option3(Player player, Packet packet) {
		int id = packet.getLEShortA() & 0xFFFF;
		if(id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		if(player.getCombatState().isDead()) {
			return;
		}
		player.getActionSender().sendDebugPacket(packet.getOpcode(), "Follow", new Object[] { "Index: " + id });

		Player target = (Player) World.getWorld().getPlayers().get(id);
		if(target != null && target != player) {
			player.getActionSender().sendFollowing(target, 1);
		}
	}
	
	/**
	 * Handles the fourth option on a player option menu.
	 * @param player
	 * @param packet
	 */
	private void option4(Player player, Packet packet) {
		int id = packet.getShortA() & 0xFFFF;
		if(id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		if(player.getCombatState().isDead()) {
			return;
		}
		Player target = (Player) World.getWorld().getPlayers().get(id);
		if(target != null && target != player) {
			player.getRequestManager().request(RequestType.TRADE, target);
		}
		player.getActionSender().sendDebugPacket(packet.getOpcode(), "Trade", new Object[] { "Index: " + id });
	}
	/**
	 * Handles player spell option.
	 * @param player The player.
	 * @param packet The packet.
	 */
	private void optionSpell(Player player, Packet packet) {
		int interfaceValue = packet.getInt();
//		int interfaceId = interfaceValue >> 16;
		int childButton = interfaceValue & 0xFFFF;
		int id = packet.getLEShortA();
		if(id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		if(player.getCombatState().isDead()) {
			return;
		}
		Player victim = (Player) World.getWorld().getPlayers().get(id);
		if(player.getMinigame() != null) {
			if(!player.getMinigame().attackMobHook(player, victim)) {
				return;
			}
		}
		Spell spell = Spell.forId(childButton, SpellBook.forId(player.getCombatState().getSpellBook()));
		if(victim != null && victim != player && spell != null) {
			if(spell.getSpellType() == SpellType.COMBAT) {
				MagicCombatAction.setAutocast(player, null, -1, false);
				player.getCombatState().setQueuedSpell(spell);
				player.getCombatState().startAttacking(victim, false);
			} else if(spell.getSpellType() == SpellType.NON_COMBAT) {
				if(!BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "MultiCombat")) {
					player.getActionSender().sendMessage("You must be in a multi-combat area to cast that spell.");
					return;					
				}
				if(!victim.getSettings().isAcceptingAid()) {
					player.getActionSender().sendMessage("That person doesn't have accept aid on.");
					return;
				}
				if(player.getLocation().isWithinDistance(victim.getLocation(), 10)) {
					player.getWalkingQueue().reset();
				}
				player.setInteractingEntity(InteractionMode.TALK, victim);
				MagicCombatAction.executeSpell(spell, player, player, victim);
			}
		}
		player.getActionSender().sendDebugPacket(packet.getOpcode(), "Spell", new Object[] { "ChildButton: " + childButton, "Index: " + id });
	}
		
}

