package org.rs2server.rs2.model.minigame.impl;

import java.util.ArrayList;
import java.util.Random;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

/**
 * For the Castle Wars minigame.
 * @author Canownueasy
 *
 */
public class CastleWars {
	
	public static CastleWars access() {
		return new CastleWars();
	}
	
	public Location 
			MAIN_LOBBY = Location.create(2441, 3090),
			ZAMORAK_ROOM = Location.create(2422, 9523),
			SARADOMIN_ROOM = Location.create(2382, 9489),
			ZAMORAK_SPAWN = Location.create(2372, 3130, 1),
			SARADOMIN_SPAWN = Location.create(2427, 3077, 1);		
	
	private Random r = new Random();
	
	public int gameTime = 0;
	public final int FULL_TIME = 300; //5 mins
	
	public ArrayList<Player> 
				players,
				waitingPlayers,
				gamePlayers,
				zamorakPlayers,
				saradominPlayers,
				zamorakWaiters,
				saradominWaiters
			= new ArrayList<Player>();
	
	public void tick() {
		gameTime--;
		if(gameTime == 1) {
			removeGamePlayers();
			addWaitPlayers();
			gameTime = FULL_TIME;
		}
	}
	
	public void removeGamePlayers() {
		for(Player player : gamePlayers) {
			if(player != null) {
				MAIN_LOBBY = Location.create(2441 + r.nextInt(3), 3090 + r.nextInt(3));
				player.setTeleportTarget(MAIN_LOBBY);
				zamorakPlayers.remove(player);
				saradominPlayers.remove(player);
				players.remove(player);
				player.getActionSender().sendMessage("The game has finished!");
				gamePlayers.remove(player);
			}
		}
	}
	
	public int[] items = { 4513, 4514, 4515, 4516 };
	
	public void addWaitPlayers() {
		for(Player player : waitingPlayers) {
			if(player != null) {
				gamePlayers.add(player);
				waitingPlayers.remove(player);
			}
		}
		for(Player player : zamorakWaiters) {
			if(player != null) {
				ZAMORAK_SPAWN = Location.create(2372 + r.nextInt(3), 3130 + r.nextInt(3), 1);
				player.setTeleportTarget(ZAMORAK_SPAWN);
				zamorakWaiters.remove(player);
			}
		}
		for(Player player : saradominWaiters) {
			if(player != null) {
				SARADOMIN_SPAWN = Location.create(2427 + r.nextInt(3), 3077 + r.nextInt(3), 1);
				player.setTeleportTarget(SARADOMIN_SPAWN);
				saradominWaiters.remove(player);
			}
		}
	}
	
	public void addWaitPlayer(final Player player) {
		players.add(player);
		waitingPlayers.add(player);
		if(zamorakWaiters.size() > saradominWaiters.size()) {
			joinZamorak(player);
			return;
		}
		if(saradominWaiters.size() > zamorakWaiters.size()) {
			joinSaradomin(player);
			return;
		}
		int rint = Misc.random(1);
		switch(rint) {
		case 0:
			joinZamorak(player);
			break;
		case 1:
			joinSaradomin(player);
			break;
		}
	}
	
	public void joinZamorak(final Player player) {
		zamorakWaiters.add(player);
		ZAMORAK_ROOM = Location.create(2422 + r.nextInt(4), 9523 + r.nextInt(4));
		player.setTeleportTarget(ZAMORAK_ROOM);
	}
	public void joinSaradomin(final Player player) {
		saradominWaiters.add(player);
		SARADOMIN_ROOM = Location.create(2382 + r.nextInt(4), 9489 + r.nextInt(4));
		player.setTeleportTarget(SARADOMIN_ROOM);
	}
	
	public void playerDied(final Player player) {
		World.getWorld().submit(new Tickable(1) {
			public void execute() {
				player.playAnimation(Animation.create(-1));
				player.getCombatState().setCanMove(true);
				stop();
			}
		});
		if(zamorakPlayers.contains(player)) {
			ZAMORAK_SPAWN = Location.create(2372 + r.nextInt(3), 3130 + r.nextInt(3), 1);
			player.setTeleportTarget(ZAMORAK_SPAWN);
			return;
		}
		if(saradominPlayers.contains(player)) {
			SARADOMIN_SPAWN = Location.create(2427 + r.nextInt(3), 3077 + r.nextInt(3), 1);
			player.setTeleportTarget(SARADOMIN_SPAWN);
			return;
		}
		System.out.println("The hell?");
	}

}
