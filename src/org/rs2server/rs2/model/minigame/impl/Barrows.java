package org.rs2server.rs2.model.minigame.impl;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.NPCDefinition;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.util.Misc;

/**
 * For the Barrows minigame.
 * @author Canownueasy
 *
 */
public class Barrows {
	
	public Barrows(Player player) {
		this.player = player;
	}
	
	private Player player;
	
	private Location BARROWS_TOP = Location.create(3565, 3306);

	private Location AHRIMS = Location.create(3557, 9703, 3);
	private Location DHAROKS = Location.create(3556, 9718, 3);
	private Location VERACS = Location.create(3578, 9706, 3);
	private Location TORAGS = Location.create(3568, 9683, 3);
	private Location KARILS = Location.create(3546, 9684, 3);
	private Location GUTHANS = Location.create(3534, 9704, 3);
	
	private int AHRIM_S = 6821;
	private int DHAROK_S = 6771;
	private int VERAC_S = 6823;
	private int TORAG_S = 6772;
	private int KARIL_S = 6822;
	private int GUTHAN_S = 6773;
	
	private boolean dharokTop() {
		int x = player.getLocation().getX();
		int y = player.getLocation().getY();
		return x >= 3573 && x <= 3578 && y >= 3294 && y <= 3300;
	}
	private boolean veracTop() {
		int x = player.getLocation().getX();
		int y = player.getLocation().getY();
		return x >= 3554 && y >= 3294 && x <= 3560 && y <= 3300;
	}
	private boolean toragTop() {
		int x = player.getLocation().getX();
		int y = player.getLocation().getY();
		return x >= 3550 && y >= 3279 && x <= 3557 && y <= 3286;
	}
	private boolean karilTop() {
		int x = player.getLocation().getX();
		int y = player.getLocation().getY();
		return x >= 3562 && y >= 3272 && x <= 3568 && y <= 3278;
	}
	private boolean guthanTop() {
		int x = player.getLocation().getX();
		int y = player.getLocation().getY();
		return x >= 3574 && y >= 3278 && x <= 3580 && y <= 3285;
	}
	private boolean ahrimTop() {
		int x = player.getLocation().getX();
		int y = player.getLocation().getY();
		return x >= 3561 && y >= 3284 && x <= 3568 && y <= 3292;
	}
	
	public boolean handleSpade() {
		if(player.getLocation().getZ() != 0) {
			return false;
		}
		if(dharokTop()) {
			player.setTeleportTarget(DHAROKS);
			player.getActionSender().sendMessage("And fall into Dharok's tunnel!");
			return true;
		} else
		if(veracTop()) {
			player.setTeleportTarget(VERACS);
			player.getActionSender().sendMessage("And fall into Verac's tunnel!");
			return true;
		} else
		if(karilTop()) {
			player.setTeleportTarget(KARILS);
			player.getActionSender().sendMessage("And fall into Karil's tunnel!");
			return true;
		} else
		if(ahrimTop()) {
			player.setTeleportTarget(AHRIMS);
			player.getActionSender().sendMessage("And fall into Ahrim's tunnel!");
			return true;
		} else
		if(guthanTop()) {
			player.setTeleportTarget(GUTHANS);
			player.getActionSender().sendMessage("And fall into Guthan's tunnel!");
			return true;
		} else
		if(toragTop()) {
			player.setTeleportTarget(TORAGS);
			player.getActionSender().sendMessage("And fall into Torag's tunnel!");
			return true;
		}
		return false;
	}
	
	public void dropItems(int id) {
		int[] ahrims = { 4708, 4710, 4712, 4714 };
		int[] dharoks = { 4716, 4718, 4720, 4722 };
		int[] guthans = { 4724, 4726, 4728, 4730 };
		int[] karils = { 4732, 4734, 4736, 4738 };
		int[] torags = { 4745, 4747, 4749, 4751 };
		int[] veracs = { 4753, 4755, 4757, 4759 };
		Item item = null;
		switch(id) {
		case 2025:
			item = new Item(Misc.random(ahrims[0], ahrims[3]));
			break;
		case 2026:
			item = new Item(Misc.random(dharoks[0], dharoks[3]));
			break;
		case 2027:
			item = new Item(Misc.random(guthans[0], guthans[3]));
			break;
		case 2028:
			item = new Item(Misc.random(karils[0], karils[3]));
			break;
		case 2029:
			item = new Item(Misc.random(torags[0], torags[3]));
			break;
		case 2030:
			item = new Item(Misc.random(veracs[0], veracs[3]));
			break;
		}
		if(item == null) {
			return;
		}
		player.getInventory().add(item);
	}
	
	public boolean addKill() {
		player.barrowsKilled++;
		if(player.barrowsKilled >= 6) {
			completedGame();
			return true;
		}
		return false;
	}
	
	public void completedGame() {
		player.setTeleportTarget(BARROWS_TOP);
		player.getActionSender().sendMessage("You've completed the game!");
		reset();
	}
	
	public void handleObjectOption(final int id) {
		if(id == AHRIM_S) {
			spawn(2025);
			return;
		}
		if(id == DHAROK_S) {
			spawn(2026);
			return;
		}
		if(id == GUTHAN_S) {
			spawn(2027);
			return;
		}
		if(id == KARIL_S) {
			spawn(2028);
			return;
		}
		if(id == TORAG_S) {
			spawn(2029);
			return;
		}
		if(id == VERAC_S) {
			spawn(2030);
			return;
		}
	}
	
	private void reset() {
		player.AHRIMS = false;
		player.DHAROKS = false;
		player.GUTHANS = false;
		player.TORAGS = false;
		player.KARILS = false;
		player.VERACS = false;
		player.barrowsKilled = 0;
	}
	
	private void spawn(int id) {
		NPCDefinition def = null;
		switch(id) {
		case 2025:
			def = new NPCDefinition(id, "Ahrim", "It's Ahrim.", 1, 2, null);
			break;
		case 2026:
			def = new NPCDefinition(id, "Dharok", "It's Dharok.", 1, 2, null);
			break;
		case 2027:
			def = new NPCDefinition(id, "Guthan", "It's Guthan.", 1, 2, null);
			break;
		case 2028:
			def = new NPCDefinition(id, "Karil", "It's Karil.", 1, 2, null);
			break;
		case 2029:
			def = new NPCDefinition(id, "Torag", "It's Torag.", 1, 2, null);
			break;
		case 2030:
			def = new NPCDefinition(id, "Verac", "It's Verac.", 1, 2, null);
			break;
		}
		if(def == null) {
			return;
		}
		NPC npc = new NPC(def, getLocation(id), getLocation(id), getLocation(id), 0);
		World.getWorld().register(npc);
		player.getActionSender().sendHintArrow(npc, 0, 1);
	}
	
	private Location getLocation(int id) {
		Location location = null;
		switch(id) {
		case 2025:
			location = AHRIMS;
			break;
		case 2026:
			location = DHAROKS;
			break;
		case 2027:
			location = GUTHANS;
			break;
		case 2028:
			location = KARILS;
			break;
		case 2029:
			location = TORAGS;
			break;
		case 2030:
			location = VERACS;
			break;
		}
		return location;
	}
	
}
