package org.rs2server.rs2.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.GameEngine;
import org.rs2server.rs2.GenericWorldLoader;
import org.rs2server.rs2.WorldLoader;
import org.rs2server.rs2.WorldLoader.LoginResult;
import org.rs2server.rs2.content.land.Landscapes;
import org.rs2server.rs2.content.sounds.MusicParser;
import org.rs2server.rs2.database.impl.LoginConnection;
import org.rs2server.rs2.event.*;
import org.rs2server.rs2.event.impl.*;
import org.rs2server.rs2.tickable.impl.*;
import org.rs2server.rs2.login.LoginServerConnector;
import org.rs2server.rs2.login.LoginServerWorldLoader;
import org.rs2server.rs2.model.CombatNPCDefinition.Skill;
import org.rs2server.rs2.model.PrivateChat.ClanRank;
import org.rs2server.rs2.model.PrivateChat.EntryRank;
import org.rs2server.rs2.model.PrivateChat.KickRank;
import org.rs2server.rs2.model.PrivateChat.TalkRank;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.equipment.EquipmentDefinition;
import org.rs2server.rs2.model.minigame.impl.Barrelchest;
import org.rs2server.rs2.model.minigame.impl.FightPits;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.model.region.RegionCoordinates;
import org.rs2server.rs2.model.region.RegionManager;
import org.rs2server.rs2.model.region.Tile;
import org.rs2server.rs2.net.PacketBuilder;
import org.rs2server.rs2.net.PacketManager;
import org.rs2server.rs2.packet.PacketHandler;
import org.rs2server.rs2.task.Task;
import org.rs2server.rs2.task.impl.SessionLoginTask;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.tickable.TickableManager;
import org.rs2server.rs2.tickable.impl.CleanupTick;
import org.rs2server.rs2.tickable.impl.WorldSave;
import org.rs2server.rs2.util.ConfigurationParser;
import org.rs2server.rs2.util.EntityList;
import org.rs2server.rs2.util.IoBufferUtils;
import org.rs2server.rs2.util.NameUtils;
import org.rs2server.util.BlockingExecutorService;


/**
 * Holds data global to the game world.
 * @author Graham Edgecombe
 *
 */
public class World {
	
	private Barrelchest barrelchest = new Barrelchest();
	public Barrelchest getBarrelchest() {
		return barrelchest;
	}
	
	public ArrayList<String> warriorGuildPlayers = new ArrayList<String>();
	
	/**
	 * Logging class.
	 */
	private static final Logger logger = Logger.getLogger(World.class.getName());
	
	/**
	 * World instance.
	 */
	private static final World world = new World();
	
	/**
	 * Gets the world instance.
	 * @return The world instance.
	 */
	public static World getWorld() {
		return world;
	}
	
	/**
	 * An executor service which handles background loading tasks.
	 */
	private BlockingExecutorService backgroundLoader = new BlockingExecutorService(Executors.newSingleThreadExecutor());

	/**
	 * The game engine.
	 */
	private GameEngine engine;
	
	/**
	 * The event manager.
	 */
	private EventManager eventManager;
	
	/**
	 * The tickable manager.
	 */
	private TickableManager tickManager;
	
	/**
	 * The current loader implementation.
	 */
	private WorldLoader loader;
	
	/**
	 * A list of connected players.
	 */
	private EntityList<Player> players = new EntityList<Player>(Constants.MAX_PLAYERS);
	
	/**
	 * A map of connected player names
	 */
	private Map<Long, Player> playerNames = new HashMap<Long, Player>();

	/**
	 * A list of active NPCs.
	 */
	private EntityList<NPC> npcs = new EntityList<NPC>(Constants.MAX_NPCS);
	
	/**
	 * The game object manager.
	 */
	private ObjectManager objectManager;
	
	/**
	 * The login server connector.
	 */
	private LoginServerConnector connector;
	
	/**
	 * The region manager.
	 */
	private RegionManager regionManager = new RegionManager();
	
	/**
	 * A map of all private chats.
	 */
	private Map<String, PrivateChat> privateChat = new HashMap<String, PrivateChat>();
	
	/**
	 * The global fight pits instance.
	 */
	private FightPits fightPits;

	/**
	 * Creates the world and begins background loading tasks.
	 */
	public World() {
		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				objectManager = new ObjectManager();
				objectManager.load();
				Door.init();
				return null;
			}
		});
		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				BoundaryManager.init();
				return null;
			}
		});
		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				ItemDefinition.init();
				return null;
			}
		});
		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				NPCDefinition.init();
				CombatNPCDefinition.init(); //Load the combat spawns, or the npc defs wont use them	
				NPCSpawn.init(); //define them before spawning or it won't spawn them.
				return null;
			}
		});
		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				EquipmentDefinition.init();
				return null;
			}
		});
		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				Shop.init();
				return null;
			}
		});
		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				ItemSpawn.init();
				return null;
			}
		});
		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				fightPits = new FightPits();
				return null;
			}
		});
		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				MusicParser.loadRegions();
				return null;
			}
		});
	}
	
	/**
	 * Gets the login server connector.
	 * @return The login server connector.
	 */
	public LoginServerConnector getLoginServerConnector() {
		return connector;
	}
	
	/**
	 * Gets the background loader.
	 * @return The background loader.
	 */
	public BlockingExecutorService getBackgroundLoader() {
		return backgroundLoader;
	}
	
	/**
	 * Gets the region manager.
	 * @return The region manager.
	 */
	public RegionManager getRegionManager() {
		return regionManager;
	}

	/**
	 * @return the privateChat
	 */
	public Map<String, PrivateChat> getPrivateChat() {
		return privateChat;
	}
	
	public FightPits getFightPits() {
		return fightPits;
	}

	/**
	 * Gets the tickable manager.
	 * @return The tickable manager.
	 */
	public TickableManager getTickableManager() {
		return tickManager;
	}
	
	/**
	 * Initialises the world: loading configuration and registering global
	 * events.
	 * @param engine The engine processing this world's tasks.
	 * @throws IOException if an I/O error occurs loading configuration.
	 * @throws ClassNotFoundException if a class loaded through reflection was not found.
	 * @throws IllegalAccessException if a class could not be accessed.
	 * @throws InstantiationException if a class could not be created.
	 * @throws IllegalStateException if the world is already initialised.
	 */
	public void init(GameEngine engine) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		if(this.engine != null) {
			throw new IllegalStateException("The world has already been initialised.");
		} else {
			this.engine = engine;
			this.eventManager = new EventManager(engine);
			this.tickManager = new TickableManager();
			LoginConnection.getSingleton();
			this.registerGlobalEvents();
			this.loadConfiguration();
			this.registerLandscapes();
		}
	}
	
	private ArrayList<Location> landscapes = new ArrayList<Location>();
	public ArrayList<Location> getLandscapes() {
		return landscapes;
	}
	
	private void registerLandscapes() {
		for(Location loc : Landscapes.landscapes) {
			landscapes.add(loc);
		}
	}
	
	/**
	 * Loads server configuration.
	 * @throws IOException if an I/O error occurs.
	 * @throws ClassNotFoundException if a class loaded through reflection was not found.
	 * @throws IllegalAccessException if a class could not be accessed.
	 * @throws InstantiationException if a class could not be created.
	 */
	private void loadConfiguration() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		FileInputStream fis = new FileInputStream("data/configuration.cfg");
		try {
			ConfigurationParser p = new ConfigurationParser(fis);
			Map<String, String> mappings = p.getMappings();
			if(mappings.containsKey("worldLoader")) {
				String worldLoaderClass = mappings.get("worldLoader");
				Class<?> loader = Class.forName(worldLoaderClass);
				this.loader = (WorldLoader) loader.newInstance();
				logger.fine("WorldLoader set to : " + worldLoaderClass);
			} else {
				this.loader = new GenericWorldLoader();
				logger.fine("WorldLoader set to default");
			}
			Map<String, Map<String, String>> complexMappings = p.getComplexMappings();
			if(complexMappings.containsKey("packetHandlers")) {
				Map<Class<?>, Object> loadedHandlers = new HashMap<Class<?>, Object>();
				for(Map.Entry<String, String> handler : complexMappings.get("packetHandlers").entrySet()) {
					int id = Integer.parseInt(handler.getKey());
					Class<?> handlerClass = Class.forName(handler.getValue());
					Object handlerInstance;
					if(loadedHandlers.containsKey(handlerClass)) {
						handlerInstance = loadedHandlers.get(loadedHandlers.get(handlerClass));
					} else {
						handlerInstance = handlerClass.newInstance();
					}
					PacketManager.getPacketManager().bind(id, (PacketHandler) handlerInstance);
					logger.fine("Bound " + handler.getValue() + " to opcode : " + id);
				}
			}
			if(loader instanceof LoginServerWorldLoader) {
				connector = new LoginServerConnector(mappings.get("loginServer"));
				connector.connect(mappings.get("nodePassword"), Integer.parseInt(mappings.get("nodeId")));
			}
		} finally {
			fis.close();
		}
	}
	
	/**
	 * Registers global events such as updating.
	 */
	private void registerGlobalEvents() {
		submit(new UpdateEvent());
		submit(new WorldSave());
		submit(new CleanupTick());
		submit(new NPCProcessEvent());
		submit(new WarriorsGuildEvent());
		submit(new SecondTick());
		submit(new HealthRestoreTick());
		submit(new BarrelchestEvent());
	}
	
	/**
	 * Submits a new event.
	 * @param event The event to submit.
	 */
	public void submit(Event event) {
		this.eventManager.submit(event);
	}
	
	/**
	 * Submits a new tickable.
	 * @param tickable The tickable to submit.
	 */
	public void submit(final Tickable tickable) {
		submit(new Task() {
			@Override
			public void execute(GameEngine context) {
				// DO NOT REMOVE THIS CODE, IT PREVENTS CONCURRENT MODIFICATION
				// PROBLEMS!
				World.this.tickManager.submit(tickable);
			}
		});
	}
	
	/**
	 * Submits a new task.
	 * @param task The task to submit.
	 */
	public void submit(Task task) {
		this.engine.pushTask(task);
	}
	
	/**
	 * Gets the object map.
	 * @return The object map.
	 */
	public ObjectManager getObjectMap() {
		return objectManager;
	}
	
	/**
	 * Gets the world loader.
	 * @return The world loader.
	 */
	public WorldLoader getWorldLoader() {
		return loader;
	}
	
	/**
	 * Gets the game engine.
	 * @return The game engine.
	 */
	public GameEngine getEngine() {
		return engine;
	}
	
	/**
	 * Loads a player's game in the work service.
	 * @param pd The player's details.
	 */
	public void load(final PlayerDetails pd) {
		engine.submitWork(new Runnable() {
			public void run() {
				LoginResult lr = loader.checkLogin(pd);
				int code = lr.getReturnCode();
				if(!NameUtils.isValidName(pd.getName())) {
					code = 11;
				}
				if(code != 2) {
					PacketBuilder bldr = new PacketBuilder();
					bldr.put((byte) code);
					pd.getSession().write(bldr.toPacket()).addListener(new IoFutureListener<IoFuture>() {
						@Override
						public void operationComplete(IoFuture future) {
							future.getSession().close(false);
						}
					});
				} else {
					lr.getPlayer().getSession().setAttribute("player", lr.getPlayer());
					loader.loadPlayer(lr.getPlayer());
					engine.pushTask(new SessionLoginTask(lr.getPlayer()));
				}
			}
		});
	}
	
	/**
	 * Registers a new npc.
	 * @param npc The npc to register.
	 */
	public void register(NPC npc) {
		npcs.add(npc);
		npc.setLocation(npc.getSpawnLocation());
		CombatNPCDefinition combatDefinition = CombatNPCDefinition.forId(npc.getDefinition().getId());
		if(combatDefinition != null) {
			npc.setCombatDefinition(combatDefinition);
			npc.setCombatCooldownDelay(combatDefinition.getCombatCooldownDelay());
			for(Skill skill : combatDefinition.getSkills().keySet()) {
				npc.getSkills().setSkill(skill.getId(), combatDefinition.getSkills().get(skill), npc.getSkills().getExperienceForLevel(combatDefinition.getSkills().get(skill)));
			}
			npc.getCombatState().setCombatStyle(combatDefinition.getCombatStyle());
			npc.getCombatState().setAttackType(combatDefinition.getAttackType());
			npc.getCombatState().setBonuses(combatDefinition.getBonuses());
		}
	}
	
	public void registerNpc(NPC npc) {
		npcs.add(npc);
		npc.setLocation(npc.getSpawnLocation());
		CombatNPCDefinition combatDefinition = CombatNPCDefinition.forId(npc.getDefinition().getId());
		if(combatDefinition != null) {
			npc.setCombatDefinition(combatDefinition);
			npc.setCombatCooldownDelay(combatDefinition.getCombatCooldownDelay());
			for(Skill skill : combatDefinition.getSkills().keySet()) {
				npc.getSkills().setSkill(skill.getId(), combatDefinition.getSkills().get(skill), npc.getSkills().getExperienceForLevel(combatDefinition.getSkills().get(skill)));
			}
			npc.getCombatState().setCombatStyle(combatDefinition.getCombatStyle());
			npc.getCombatState().setAttackType(combatDefinition.getAttackType());
			npc.getCombatState().setBonuses(combatDefinition.getBonuses());
		}
	}
	
	/**
	 * Unregisters an old npc.
	 * @param npc The npc to unregister.
	 */
	public void unregister(NPC npc) {
		npcs.remove(npc);
		npc.destroy();
	}

	/**
	 * Registers a new player.
	 * @param player The player to register.
	 */
	public void register(final Player player) {
		// do final checks e.g. is player online? is world full?
		int returnCode = 2;
		if(isPlayerOnline(player.getName())) {
			returnCode = 5;
		} else {
			if(!players.add(player)) {
				returnCode = 7;
				logger.info("Could not register player : " + player + " [world full]");
			} else {
				playerNames.put(player.getNameAsLong(), player);
			}
		}
		final int fReturnCode = returnCode;
		PacketBuilder bldr = new PacketBuilder();
		bldr.put((byte) returnCode);
		bldr.put((byte) player.getRights().toInteger());
		bldr.put((byte) 0);
		bldr.putShort(player.getIndex());
		bldr.put((byte) 1);
		player.getSession().write(bldr.toPacket()).addListener(new IoFutureListener<IoFuture>() {
			@Override
			public void operationComplete(IoFuture future) {
				if(fReturnCode != 2) {
					player.getSession().close(false);
				} else {
					player.getActionSender().sendLogin();
				}
			}
		});
		if(returnCode == 2) {
			logger.info("Registered player : " + player + " [online=" + players.size() + "]");
		}
	}
	
	/**
	 * Creates a ground item.
	 * @param item The ground item.
	 * @param player The controller.
	 */
	public void createGroundItem(final GroundItem item, final Player player) {
		Tile tile = item.getRegion().getTile(item.getLocation());
		if(item.getItem().getDefinition().isStackable()) {
			if(tile.getGroundItems().size() > 0) {
				for(GroundItem g : tile.getGroundItems()) {
					if(item.isOwnedBy(player.getName()) && g.getItem().getId() == item.getItem().getId()) {
						long existingItemCount = (long) g.getItem().getCount();
						long newItemCount = (long) item.getItem().getCount();
						long total = existingItemCount + newItemCount;
						long remainder = 0;
						if(total > Integer.MAX_VALUE) {
							total = existingItemCount + (Integer.MAX_VALUE - existingItemCount);
							remainder = (existingItemCount + newItemCount) - Integer.MAX_VALUE;
						}
						g.setItem(new Item(g.getItem().getId(), (int) total));
						if(remainder > 0) {
							if(player != null) {
								player.getInventory().add(new Item(g.getItem().getId(), (int) remainder));
							}
						}
						if(g.isGlobal()) {
							for(Region r : getRegionManager().getSurroundingRegions(item.getLocation())) {
								for(Player p : r.getPlayers()) {
									p.getActionSender().removeGroundItem(g);
									p.getActionSender().sendGroundItem(g);
								}
							}
						} else {
							player.getActionSender().removeGroundItem(g);
							player.getActionSender().sendGroundItem(g);
						}
						return;
					}
				}			
			}
		}
		register(item, player);
	}
	
	/**
	 * Registers a new ground item.
	 * @param item The item to register.
	 * @param player The controller.
	 */
	public void register(final GroundItem item, final Player player) {
		item.getRegion().getGroundItems().add(item);
		item.getRegion().getTile(item.getLocation()).getGroundItems().add(item);
		if(player != null) {
			player.getActionSender().sendGroundItem(item);			
			submit(new Tickable(100) {
				public void execute() {
					if(item.isRegistered()) {
						for(Region r : getRegionManager().getSurroundingRegions(item.getLocation())) {
							for(Player p : r.getPlayers()) {
								if(!p.getName().equalsIgnoreCase(player.getName())) {
									p.getActionSender().sendGroundItem(item);
								}							
							}
						}
						item.setGlobal(true);
					}
					this.stop();
				}
			});
		} else if(player == null || item.getControllerName().length() < 1) {
			for(Region r : getRegionManager().getSurroundingRegions(item.getLocation())) {
				for(Player p : r.getPlayers()) {
					if(player == null || !p.getName().equalsIgnoreCase(player.getName())) {
						p.getActionSender().sendGroundItem(item);
					}							
				}
			}
			item.setGlobal(true);			
		}
		submit(new Tickable(200) {
			public void execute() {
				if(item.isRegistered()) {
					unregister(item);
				}
				this.stop();
			}
		});
	}
	
	/**
	 * Unregisters a new ground item.
	 * @param item The item to unregister.
	 */
	public void unregister(GroundItem item) {
		item.setRegistered(false);
		item.getRegion().getGroundItems().remove(item);
		item.getRegion().getTile(item.getLocation()).getGroundItems().remove(item);
		if(item.isGlobal()) {
			for(Region r : getRegionManager().getSurroundingRegions(item.getLocation())) {
				for(Player p : r.getPlayers()) {
					p.getActionSender().removeGroundItem(item);
				}
			}
		} else {
			for(Region r : getRegionManager().getSurroundingRegions(item.getLocation())) {
				for(Player p : r.getPlayers()) {
					if(item.isOwnedBy(p.getName())) {
						p.getActionSender().removeGroundItem(item);
					}					
				}
			}
		}
	}
	
	/**
	 * Registers a game object.
	 * @param obj The game object to register.
	 */
	public void register(GameObject obj) {
		obj.setLocation(obj.getSpawnLocation() != null ? obj.getSpawnLocation() : obj.getLocation());
		if(obj.getRegion().getCoordinates().equals(new RegionCoordinates(77, 107)) || obj.getRegion().getCoordinates().equals(new RegionCoordinates(79, 110)) || obj.getRegion().getCoordinates().equals(new RegionCoordinates(79, 111))) {
			obj.setLocation(Location.create(obj.getLocation().getX(), obj.getLocation().getY(), 0));
		}
		if(obj.getLocation().equals(Location.create(2535, 3547, 0))) {
			obj.setLocation(Location.create(2535, 3547, 1));
		}
		
		/*
		 * Tzhaar walls are not sent for some reason...
		 */
		if((obj.getId() >= 9360 && obj.getId() <= 9365)
						&& obj.getLocation().getX() >= 2391 && obj.getLocation().getY() >= 5168
						&& obj.getLocation().getX() <= 2406 && obj.getLocation().getY() <= 5175) {
			obj.setLoadedInLandscape(false);
		}
		if(!obj.isLoadedInLandscape()) {
			Region[] regions = regionManager.getSurroundingRegions(obj.getLocation());
			for(Region r : regions) {
				for(Player p : r.getPlayers()) {
					p.getActionSender().sendObject(obj);
				}
			}
		}
//		obj.getRegion().getGameObjects().add(obj);
	}
	
	public void replaceObject(final GameObject original, final GameObject replacement, int cycles) {
		unregister(original, false);
		register(replacement);
		submit(new Tickable(cycles) {
			@Override
			public void execute() {
				unregister(replacement, false);
				register(original);
			}			
		});
	}
	
	/**
	 * Unregisters a game object.
	 * @param obj The game object to unregister.
	 * @param remove The flag to remove it on players screens.
	 */
	public void unregister(GameObject obj, boolean remove) {
		if(remove) {
			Region[] regions = regionManager.getSurroundingRegions(obj.getLocation());
			for(Region r : regions) {
				for(Player p : r.getPlayers()) {
					p.getActionSender().removeObject(obj);
				}
			}
		}
		obj.removeFromRegion(obj.getRegion());
	}
	
	/**
	 * Gets the player list.
	 * @return The player list.
	 */
	public EntityList<Player> getPlayers() {
		return players;
	}
	
	/**
	 * @return the playerNames
	 */
	public Map<Long, Player> getPlayerNames() {
		return playerNames;
	}
	
	/**
	 * Gets the npc list.
	 * @return The npc list.
	 */
	public EntityList<NPC> getNPCs() {
		return npcs;
	}
	
	/**
	 * Checks if a player is online.
	 * @param name The player's name.
	 * @return <code>true</code> if they are online, <code>false</code> if not.
	 */
	public boolean isPlayerOnline(String name) {
		return playerNames.get(NameUtils.nameToLong(name)) != null;
	}
	
	/**
	 * Saves a player's work.
	 * @param player The player to save.
	 */
	public void save(final Player player) {
		engine.submitWork(new Runnable() {
			public void run() {
				loader.savePlayer(player);
			}
		});
	}

	/**
	 * Unregisters a player, and saves their game.
	 * @param player The player to unregister.
	 */
	public void unregister(final Player player) {
		if(player.isActive()) {
			if(getBarrelchest().players.contains(player)) {
				getBarrelchest().leaveGame(player);
			}
			if(player.getAttribute("cannon") != null) {
				Cannon cannon = (Cannon) player.getAttribute("cannon");
				cannon.destroy();
				player.removeAttribute("cannon");
			}
			if(player.getAttribute("cutScene") != null) {
				DialogueManager.openDialogue(player, 123);
			}
			if(player.getInterfaceState().getClan().length() > 0) {
				World.getWorld().getPrivateChat().get(player.getInterfaceState().getClan()).removeClanMember(player);
			}
			if(player.getCombatState().spellbookSwap()) {
				player.getCombatState().setSpellBook(MagicCombatAction.SpellBook.LUNAR_MAGICS.getSpellBookId());
				player.getCombatState().setSpellbookSwap(false);
			}
			player.getActionSender().sendInterfacesRemovedClientSide();
			player.getActionQueue().clearAllActions();
			playerNames.put(player.getNameAsLong(), null);
			player.getPrivateChat().updateFriendList(false);
			if(player.getMinigame() != null) {
				player.getMinigame().quit(player);
			}
			if(player.getInterfaceAttribute("fightPitOrbs") != null) {
				World.getWorld().getFightPits().quit(player);
			}
			player.write(new PacketBuilder(166).toPacket());
		}
		player.destroy();
		player.getSession().close(false);
		players.remove(player);
		logger.info("Unregistered player : " + player + " [online=" + players.size() + "]");
		engine.submitWork(new Runnable() {
			public void run() {
				loader.savePlayer(player);
				if(World.getWorld().getLoginServerConnector() != null) {
					World.getWorld().getLoginServerConnector().disconnected(player.getName());
				}
			}
		});
	}

	/**
	 * Handles an exception in any of the pools.
	 * @param t The exception.
	 */
	public void handleError(Throwable t) {
		logger.severe("An error occurred in an executor service! The server will be halted immediately.");
		t.printStackTrace();
		System.exit(1);
	}
	
	@SuppressWarnings("unchecked")
	public boolean deserializePrivate(String owner) {
		owner = NameUtils.formatName(owner);
		File f = new File("data/savedGames/privateChats/" + owner + ".dat.gz");
		if(f.exists()) {
			try {
				InputStream is = new GZIPInputStream(new FileInputStream(f));
				IoBuffer buf = IoBuffer.allocate(1024);
				buf.setAutoExpand(true);
				while(true) {
					byte[] temp = new byte[1024];
					int read = is.read(temp, 0, temp.length);
					if(read == -1) {
						break;
					} else {
						buf.put(temp, 0, read);
					}
				}
				buf.flip();

				
				PrivateChat privateChat = new PrivateChat(owner, IoBufferUtils.getRS2String(buf));
				privateChat.setEntryRank(EntryRank.forId(buf.get()));
				privateChat.setTalkRank(TalkRank.forId(buf.get()));
				privateChat.setKickRank(KickRank.forId(buf.get()));
				World.getWorld().getPrivateChat().put(owner, privateChat);

				if (buf.hasRemaining()) {
					try {
						HashMap<Long, ClanRank> friends = (HashMap<Long, ClanRank>) buf.getObject();
						for(long l : friends.keySet()) {
							World.getWorld().getPrivateChat().get(owner).addFriend(l, friends.get(l));
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				if (buf.hasRemaining()) {
					try {
						List<Long> ignores = (List<Long>) buf.getObject();
						for(long l : ignores) {
							World.getWorld().getPrivateChat().get(owner).addIgnore(l);
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}				
				
				
				return true;
			} catch(IOException ex) {
				return false;
			}
		}
		return false;
	}
	
	public boolean serializePrivate(String owner) {
		try {
			OutputStream os = new GZIPOutputStream(new FileOutputStream("data/savedGames/privateChats/" + owner + ".dat.gz"));
			IoBuffer buf = IoBuffer.allocate(1024);
			buf.setAutoExpand(true);
			
			PrivateChat privateChat = World.getWorld().getPrivateChat().get(owner);
			IoBufferUtils.putRS2String(buf, privateChat.getChannelName());
			buf.put((byte) privateChat.getEntryRank().getId());
			buf.put((byte) privateChat.getTalkRank().getId());
			buf.put((byte) privateChat.getKickRank().getId());
			buf.putObject(privateChat.getFriends());
			buf.putObject(privateChat.getIgnores());
			
			buf.flip();
			byte[] data = new byte[buf.limit()];
			buf.get(data);
			os.write(data);
			os.flush();
			os.close();
			return true;
		} catch(IOException ex) {
			return false;
		}
	}
	
	public boolean privateExists(String owner) {
		owner = NameUtils.formatName(owner);
		File f = new File("data/savedGames/privateChats/" + owner + ".dat.gz");
		return f.exists();
	}
	
	public boolean privateIsRegistered(String owner) {
		return World.getWorld().getPrivateChat().containsKey(owner);
	}
	
	public boolean clanIsRegistered(String owner) {
		if(privateExists(owner)
						&& !privateIsRegistered(owner)) {
			if(!deserializePrivate(owner)) {
				return false;
			}
		}
		return World.getWorld().getPrivateChat().get(owner).getChannelName().length() > 0;
	}
}
