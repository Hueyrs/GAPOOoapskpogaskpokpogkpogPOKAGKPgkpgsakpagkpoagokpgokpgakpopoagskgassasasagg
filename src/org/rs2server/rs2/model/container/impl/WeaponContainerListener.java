package org.rs2server.rs2.model.container.impl;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.CombatState.CombatStyle;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.container.ContainerListener;
import org.rs2server.rs2.model.container.Equipment;

/**
 * A listener which updates the weapon tab.
 * 
 * @author Graham Edgecombe
 * 
 */
public class WeaponContainerListener implements ContainerListener {

	/**
	 * The player.
	 */
	private Player player;

	/**
	 * Creates the listener.
	 * 
	 * @param player
	 *            The player.
	 */
	public WeaponContainerListener(Player player) {
		this.player = player;
	}

	@Override
	public void itemChanged(Container container, int slot) {
		if (slot == Equipment.SLOT_WEAPON ) {
			sendWeapon();
		}
	}

	@Override
	public void itemsChanged(Container container, int[] slots) {
		for (int slot : slots) {
			if (slot == Equipment.SLOT_WEAPON) {
				sendWeapon();
				return;
			}
		}
	}

	@Override
	public void itemsChanged(Container container) {
		sendWeapon();
	}

	/**
	 * Sends weapon information.
	 */
	private void sendWeapon() {
		Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
		if (weapon != null && weapon.getEquipmentDefinition() != null) {
			player.setStandAnimation(weapon.getEquipmentDefinition()
					.getAnimation().getStand());
			player.setRunAnimation(weapon.getEquipmentDefinition()
					.getAnimation().getRun());
			player.setWalkAnimation(weapon.getEquipmentDefinition()
					.getAnimation().getWalk());
			player.setStandTurnAnimation(weapon.getEquipmentDefinition()
					.getAnimation().getStandTurn());
			player.setTurn180Animation(weapon.getEquipmentDefinition()
					.getAnimation().getTurn180());
			player.setTurn90ClockwiseAnimation(weapon.getEquipmentDefinition()
					.getAnimation().getTurn90ClockWise());
			player.setTurn90CounterClockwiseAnimation(weapon
					.getEquipmentDefinition().getAnimation()
					.getTurn90CounterClockWise());
		} else {
			player.setDefaultAnimations();
		}
		int id = -1;
		String name = null;
		if (weapon == null) {
			name = "Unarmed";
		} else {
			name = weapon.getDefinition().getName();
			id = weapon.getId();
		}
		String genericName = filterWeaponName(name).trim();
		sendWeapon(id, name, name.toLowerCase(), genericName.toLowerCase());
	}

	/**
	 * Sends weapon information.
	 * 
	 * @param id
	 *            The id.
	 * @param name
	 *            The name.
	 * @param genericName
	 *            The filtered name.
	 */
	private void sendWeapon(int id, String originalName, String name, String genericName) {
		if (name.equals("unarmed")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case DEFENSIVE:
			case CONTROLLED_3:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 2);
				break;
			}
			player.getActionSender().sendSidebarInterface(99, 92);
			player.getActionSender().sendString(92, 0, originalName);
		} else if (name.endsWith("whip") || name.contains("mouse")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_2);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 2);
				break;
			}
			player.getActionSender().sendSidebarInterface(99, 93);
			player.getActionSender().sendString(93, 0, originalName);
			player.getActionSender().sendInterfaceConfig(93, 10, true);
		} else if (name.endsWith("scythe")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_2);
				player.getActionSender().sendConfig(43, 2);
				break;
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			player.getActionSender().sendSidebarInterface(99, 86);
			player.getActionSender().sendString(86, 0, originalName);
		} else if ((name.contains("bow") || name.equals("seercull")) && !name.contains("karil") && !name.contains("c'bow")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case DEFENSIVE:
			case CONTROLLED_3:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 2);
				break;
			}
			player.getActionSender().sendSidebarInterface(99, 77);
			player.getActionSender().sendString(77, 0, originalName);
			player.getActionSender().sendInterfaceConfig(77, 10, true);
			player.getActionSender().updateSpecialConfig();
		} else if (name.contains("karil") || name.contains("c'bow") || name.contains("cross")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case DEFENSIVE:
			case CONTROLLED_3:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 2);
				break;
			}
			player.getActionSender().sendSidebarInterface(99, 79);
			player.getActionSender().sendString(79, 0, originalName);
			player.getActionSender().sendInterfaceConfig(79, 10, true);
		} else if (genericName.contains("scimitar") || name.equals("excalibur") || name.endsWith("light") || (genericName.contains("sword") && !name.contains("2h") && !name.contains("god") && !name.contains("saradomin"))) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_1);
				player.getActionSender().sendConfig(43, 2);
				break;
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			player.getActionSender().sendSidebarInterface(99, 81);
			player.getActionSender().sendString(81, 0, originalName);
			player.getActionSender().sendInterfaceConfig(81, 12, true);
		} else if (name.contains("staff") || name.contains("wand")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case DEFENSIVE:
			case CONTROLLED_3:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 2);
				break;
			}
			player.getActionSender().sendSidebarInterface(99, 90);
			player.getActionSender().sendString(90, 0, originalName);
		} else if (genericName.startsWith("dart") || genericName.endsWith("knife") || genericName.endsWith("thrownaxe") || genericName.endsWith("javelin") || name.equals("toktz-xil-ul")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case DEFENSIVE:
			case CONTROLLED_3:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 2);
				break;
			}
			player.getActionSender().sendSidebarInterface(99, 91);
			player.getActionSender().sendString(91, 0, originalName);
		} else if (genericName.contains("mace") || name.endsWith("flail") || name.endsWith("anchor")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_2);
				player.getActionSender().sendConfig(43, 2);
				break;
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			player.getActionSender().sendSidebarInterface(99, 88);
			player.getActionSender().sendString(88, 0, originalName);
			player.getActionSender().sendInterfaceConfig(88, 12, true);
		} else if (genericName.startsWith("dagger")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_2);
				player.getActionSender().sendConfig(43, 2);
				break;
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			player.getActionSender().sendSidebarInterface(99, 89);
			player.getActionSender().sendString(89, 0, originalName);
			player.getActionSender().sendInterfaceConfig(89, 12, true);
		} else if (genericName.startsWith("pickaxe")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_2);
				player.getActionSender().sendConfig(43, 2);
				break;
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			player.getActionSender().sendSidebarInterface(99, 83);
			player.getActionSender().sendString(83, 0, originalName);
		} else if (genericName.startsWith("maul") || genericName.endsWith("warhammer") || name.endsWith("hammers") || name.equals("tzhaar-ket-om")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case DEFENSIVE:
			case CONTROLLED_3:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 2);
				break;
			}
			player.getActionSender().sendSidebarInterface(99, 76);
			player.getActionSender().sendString(76, 0, originalName);
			player.getActionSender().sendInterfaceConfig(76, 10, true);
		} else if (name.contains("2h") || name.contains("godsword") || name.equals("saradomin sword")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_2);
				player.getActionSender().sendConfig(43, 2);
				break;
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			player.getActionSender().sendSidebarInterface(99, 82);
			player.getActionSender().sendString(82, 0, originalName);
			player.getActionSender().sendInterfaceConfig(82, 12, true);
		} else if (genericName.contains("axe") || genericName.contains("battleaxe")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_2);
				player.getActionSender().sendConfig(43, 2);
				break;
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			player.getActionSender().sendSidebarInterface(99, 75);
			player.getActionSender().sendString(75, 0, originalName);
			player.getActionSender().sendInterfaceConfig(75, 12, true);
		} else if (genericName.contains("claws")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_2);
				player.getActionSender().sendConfig(43, 2);
				break;
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			player.getActionSender().sendSidebarInterface(99, 78);
			player.getActionSender().sendString(78, 0, originalName);
			player.getActionSender().sendInterfaceConfig(78, 12, true);
		} else if (genericName.startsWith("halberd")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_1);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_2);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case DEFENSIVE:
			case CONTROLLED_3:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_3);
				player.getActionSender().sendConfig(43, 2);
				break;
			}
			player.getActionSender().sendSidebarInterface(99, 84);
			player.getActionSender().sendString(84, 0, originalName);
			player.getActionSender().sendInterfaceConfig(84, 10, true);
		} else if (genericName.contains("spear")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_1);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_2);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_3);
				player.getActionSender().sendConfig(43, 2);
				break;
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			player.getActionSender().sendSidebarInterface(99, 87);
			player.getActionSender().sendString(87, 0, originalName);
		} else if (genericName.equals("bow-sword")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
			case AGGRESSIVE_1:
			case CONTROLLED_2:
			case AGGRESSIVE_2:
			case CONTROLLED_3:
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			}
			player.getActionSender().sendSidebarInterface(99, 80);
			player.getActionSender().sendString(80, 0, originalName);
		}
		player.getCombatState().setQueuedSpell(null);
		player.getCombatState().setSpecial(false);
		setSpecials(id);
		if(player.getAutocastSpell() != null) {
			MagicCombatAction.setAutocast(player, null, -1, false);
			return;
		}
	}
	
	/**
	 * Sends the special bar on an interface.
	 * @param weaponId The item weapon id.
	 */
	private void setSpecials(int weaponId) {
		if (weaponId == 4151) {
			player.getActionSender().sendInterfaceConfig(93, 10, false);
		} else if (weaponId == 1215 || weaponId == 1231 || weaponId == 5680
				|| weaponId == 5698 || weaponId == 8872 || weaponId == 8874
				|| weaponId == 8876 || weaponId == 8878) {
			player.getActionSender().sendInterfaceConfig(89, 12, false);
		} else if (weaponId == 35 || weaponId == 1305 || weaponId == 4587
				|| weaponId == 6746 || weaponId == 11037) {
			player.getActionSender().sendInterfaceConfig(81, 12, false);
		} else if (weaponId == 7158 || weaponId == 11694 || weaponId == 11696
				|| weaponId == 11698 || weaponId == 11700 || weaponId == 11730) {
			player.getActionSender().sendInterfaceConfig(82, 12, false);
		} else if (weaponId == 859 || weaponId == 861 || weaponId == 6724
				|| weaponId == 10284 || weaponId == 11235) {
			player.getActionSender().sendInterfaceConfig(77, 10, false);
		} else if (weaponId == 8880) {
			player.getActionSender().sendInterfaceConfig(79, 10, false);
		} else if (weaponId == 3101) {
			player.getActionSender().sendInterfaceConfig(78, 12, false);
		} else if (weaponId == 1434 || weaponId == 11061 || weaponId == 10887) {
			player.getActionSender().sendInterfaceConfig(88, 12, false);
		} else if (weaponId == 1377 || weaponId == 6739) {
			player.getActionSender().sendInterfaceConfig(75, 12, false);
		} else if (weaponId == 4153) {
			player.getActionSender().sendInterfaceConfig(76, 10, false);
		} else if (weaponId == 3204) {
			player.getActionSender().sendInterfaceConfig(84, 10, false);
		} else if (weaponId == 805) {
			player.getActionSender().sendInterfaceConfig(91, 10, false);
		}
		player.getActionSender().updateSpecialConfig();
	}

	/**
	 * Filters a weapon name.
	 * 
	 * @param name
	 *            The original name.
	 * @return The filtered name.
	 */
	private String filterWeaponName(String name) {
		final String[] filtered = new String[] { "Iron", "Steel", "Scythe",
				"Black", "Mithril", "Adamant", "Rune", "Granite", "Dragon",
				"Crystal", "Bronze", "Drag" };
		for (String filter : filtered) {
			name = name.replaceAll(filter, "");
		}
		return name;
	}
}
