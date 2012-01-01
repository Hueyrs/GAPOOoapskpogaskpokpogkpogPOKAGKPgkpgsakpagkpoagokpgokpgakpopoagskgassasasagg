package org.rs2server.rs2.model.minigame.impl;

import java.util.ArrayList;
import java.util.List;

import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.NPCDefinition;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.WalkingQueue;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.Mob.InteractionMode;
import org.rs2server.rs2.model.boundary.Boundary;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.tickable.Tickable;

public class FremennikTrials extends AbstractMinigame {
	
	/**
	 * The list of participants in this instance.
	 */
	private List<Player> participants;
	
	/**
	 * The wave of npcs this player is fighting.
	 */
	private Wave wave;
	
	/**
	 * The current npcs, if there is one.
	 */
	private List<NPC> currentNPCs;
	
	/**
	 * The specific height lvl.
	 */
	private int heightLvl;
	
	/**
	 * Creates the fremennik trial instance for the specified player.
	 * @param player The player.
	 */
	public FremennikTrials(Player player) {
		int[] equipment = new int[] {
			Equipment.SLOT_HELM, Equipment.SLOT_CAPE, Equipment.SLOT_AMULET, Equipment.SLOT_CHEST,
			Equipment.SLOT_BOTTOMS, Equipment.SLOT_BOOTS, Equipment.SLOT_GLOVES, Equipment.SLOT_RING
		};
		for(int i = 0; i < equipment.length; i++) {
			if(player.getEquipment().get(equipment[i]) != null) {
				player.getActionSender().sendMessage("You can only take a weapon, shield or ammunition into the Fremennik Trials minigame.");
				return;
			}
		}
		player.getCombatState().resetPrayers();
		init(); //Begins initialization
		this.participants = new ArrayList<Player>();
		this.participants.add(player);
		this.heightLvl = (player.getIndex() * 4) + 2;
		start();
	}
	
	private enum Wave {
		
		ONE(0, Location.create(2660, 10091, 2), new NPC[] { new NPC(NPCDefinition.forId(3697), Location.create(2659, 10091, 2), null, null, WalkingQueue.EAST) }),
		
		TWO(1, Location.create(2646, 10083, 2), new NPC[] { new NPC(NPCDefinition.forId(3701), Location.create(2646, 10082, 2), null, null, WalkingQueue.NORTH) }),
		
		THREE(2, Location.create(2647, 10076, 2), new NPC[] { new NPC(NPCDefinition.forId(3697), Location.create(2647, 10075, 2), null, null, WalkingQueue.NORTH),
															  new NPC(NPCDefinition.forId(3701), Location.create(2648, 10076, 2), null, null, WalkingQueue.WEST)}),
		
		FOUR(3, Location.create(2655, 10072, 2), new NPC[] { new NPC(NPCDefinition.forId(3703), Location.create(2655, 10071, 2), null, null, WalkingQueue.NORTH) }),
		
		FIVE(4, Location.create(2664, 10081, 2), new NPC[] { new NPC(NPCDefinition.forId(3703), Location.create(2664, 10082, 2), null, null, WalkingQueue.SOUTH),
															 new NPC(NPCDefinition.forId(3701), Location.create(2663, 10081, 2), null, null, WalkingQueue.EAST),
															 new NPC(NPCDefinition.forId(3697), Location.create(2665, 10081, 2), null, null, WalkingQueue.WEST) });
		
		/**
		 * The list of waves of npcs.
		 */
		public static List<Wave> waves = new ArrayList<Wave>();
		
		public static Wave forId(int wave) {
			if(wave >= waves.size()) {
				return null;
			}
			return waves.get(wave);
		}
		
		/**
		 * Populates the wave list.
		 */
		static {
			for(Wave wave : Wave.values()) {
				waves.add(wave);
			}
		}
		
		/**
		 * The id of this wave.
		 */
		private int id;
		
		/**
		 * The location you are teleported to on this wave.
		 */
		private Location location;
		
		/**
		 * The npc spawned in this wave.
		 */
		private NPC[] npcs;
		
		private Wave(int id, Location location, NPC[] npcs) {
			this.id = id;
			this.location = location;
			this.npcs = npcs;
		}

		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}

		/**
		 * @return the location
		 */
		public Location getLocation() {
			return location;
		}

		/**
		 * @return the npc
		 */
		public NPC[] getNPCs() {
			return npcs;
		}
	}
	
	public void spawnWave(Wave wave) {
		NPC[] waveNPCs = wave.getNPCs();
		currentNPCs = new ArrayList<NPC>();
		for(NPC npc : waveNPCs) {
			NPC newNPC = new NPC(npc.getDefinition(), Location.create(npc.getSpawnLocation().getX(), npc.getSpawnLocation().getY(), heightLvl), null, null, npc.getDirection());
			currentNPCs.add(newNPC);
			World.getWorld().register(newNPC);
			newNPC.getCombatState().startAttacking(participants.get(0), false);
		}
	}

	@Override
	public void end() {
		super.end();
		for(NPC npc : currentNPCs) {
			World.getWorld().unregister(npc);
		}
	}

	@Override
	public void quit(Player player) {
		super.quit(player);
	}

	@Override
	public Boundary getBoundary() {
		return Boundary.create(getName(), Location.create(2638, 10061, 0), Location.create(2686, 10109, 0));
	}

	@Override
	public ItemSafety getItemSafety() {
		return ItemSafety.SAFE;
	}

	@Override
	public String getName() {
		return "Fremennik Trials";
	}

	@Override
	public List<Player> getParticipants() {
		return participants;
	}

	@Override
	public void start() {
		super.start();
		wave = Wave.ONE;
		for(Player player : participants) {
			player.resetVariousInformation();
			player.setAttribute("temporaryHeight", 2);
			player.setTeleportTarget(Location.create(wave.getLocation().getX(), wave.getLocation().getY(), heightLvl));
		}
		spawnWave(wave);
	}

	@Override
	public Tickable getGameCycle() {
		return null;
	}

	@Override
	public Location getStartLocation() {
		return Location.create(2666, 3694, 0);
	}

	@Override
	public boolean deathHook(Player player) {
		quit(player);
		end();
		return true;
	}

	@Override
	public void movementHook(Player player) {
		super.movementHook(player);
	}

	@Override
	public void killHook(Player player, Mob victim) {
		super.killHook(player, victim);
		if(victim.isNPC()) {
			NPC npc = (NPC) victim;
			currentNPCs.remove(npc);
			if(currentNPCs.size() < 1) {
				wave = Wave.forId(this.wave.getId() + 1);
				if(wave == null) {
					end();
					for(Player participant : participants) {
						participant.setFremennikTrials(true);
						NPC skulgrimmen = (NPC) World.getWorld().getNPCs().get(194);
						participant.setInteractingEntity(InteractionMode.TALK, skulgrimmen);
						skulgrimmen.setInteractingEntity(InteractionMode.TALK, player);
						DialogueManager.openDialogue(player, 180);
					}
					return;//finished
				}
				for(Player participant : participants) {
					participant.setTeleportTarget(Location.create(wave.getLocation().getX(), wave.getLocation().getY(), heightLvl));
				}
				spawnWave(wave);
			}
		}
	}

}
