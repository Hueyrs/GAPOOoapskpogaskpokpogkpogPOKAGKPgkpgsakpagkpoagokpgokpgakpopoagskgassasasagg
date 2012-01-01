package org.rs2server.rs2.model;

/*
 * IMPORTANT MESSAGE - READ BEFORE ADDING NEW METHODS/FIELDS TO THIS CLASS
 * 
 * Before you create a field (variable) or method in this class, which is specific to a particular
 * skill, quest, minigame, etc, THINK! There is almost always a better way (e.g. attribute system,
 * helper methods in other classes, etc.)
 * 
 * We don't want this to turn into another client.java! If you need advice on alternative methods,
 * feel free to discuss it with me.
 * 
 * Graham
 */


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import org.rs2server.rs2.model.quests.MageArena;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.rs2server.data.Persistable;
import org.rs2server.rs2.content.SkillReset;
import org.rs2server.rs2.model.Skills.SkillCape;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatFormulae;
import org.rs2server.rs2.model.combat.CombatState.CombatStyle;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.Spell;
import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.container.Trade;
import org.rs2server.rs2.model.container.Container.Type;
import org.rs2server.rs2.model.minigame.Minigame;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.model.skills.SlayerTasks;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.net.ISAACCipher;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.util.IoBufferUtils;
import org.rs2server.rs2.util.Misc;
import org.rs2server.rs2.util.NameUtils;
import org.rs2server.rs2.util.TextUtils;


/**
 * Represents a player-controller character.
 * 
 * @author Graham Edgecombe
 * 
 */
public class Player extends Mob implements Persistable {
	
	/**
	 * Represents the rights of a player.
	 * 
	 * @author Graham Edgecombe
	 * 
	 */
	public enum Rights {

		/**
		 * A standard account.
		 */
		PLAYER(0),

		/**
		 * A player-moderator account.
		 */
		MODERATOR(1),

		/**
		 * An administrator account.
		 */
		ADMINISTRATOR(2);

		/**
		 * The integer representing this rights level.
		 */
		private int value;

		/**
		 * Creates a rights level.
		 * 
		 * @param value
		 *            The integer representing this rights level.
		 */
		private Rights(int value) {
			this.value = value;
		}

		/**
		 * Gets an integer representing this rights level.
		 * 
		 * @return An integer representing this rights level.
		 */
		public int toInteger() {
			return value;
		}

		/**
		 * Gets rights by a specific integer.
		 * 
		 * @param value
		 *            The integer returned by {@link #toInteger()}.
		 * @return The rights level.
		 */
		public static Rights getRights(int value) {
			if (value == 1) {
				return MODERATOR;
			} else if (value == 2) {
				return ADMINISTRATOR;
			} else {
				return PLAYER;
			}
		}
	}

	/*
	 * Attributes specific to our session.
	 */

	/**
	 * The ISAAC cipher for incoming data.
	 */
	private final ISAACCipher inCipher;

	/**
	 * The ISAAC cipher for outgoing data.
	 */
	private final ISAACCipher outCipher;

	/**
	 * The action sender.
	 */
	private final ActionSender actionSender = new ActionSender(this);

	/**
	 * A queue of pending chat messages.
	 */
	private final Queue<ChatMessage> chatMessages = new LinkedList<ChatMessage>();

	/**
	 * The current chat message.
	 */
	private ChatMessage currentChatMessage;
/**
 * Mage Arena
 */
	private MageArena mageArena = new MageArena(this);
	
	/**
	 * Active flag: if the player is not active certain changes (e.g. items)
	 * should not send packets as that indicates the player is still loading.
	 */
	private boolean active = false;

	/**
	 * The interface state.
	 */
	private final InterfaceState interfaceState = new InterfaceState(this);

	/**
	 * A queue of packets that are pending.
	 */
	private final Queue<Packet> pendingPackets = new LinkedList<Packet>();

	/**
	 * The request manager which manages trading and duelling requests.
	 */
	private final RequestManager requestManager = new RequestManager(this);

	/**
	 * The <code>IoSession</code>.
	 */
	private final IoSession session;

	/**
	 * The player's skill levels.
	 */
	private final Skills skills = new Skills(this);

	/**
	 * The stand animation.
	 */
	private Animation standAnimation = Animation.create(808);

	/**
	 * The run animation.
	 */
	private Animation runAnimation = Animation.create(824);

	/**
	 * The walk animation.
	 */
	private Animation walkAnimation = Animation.create(819);

	/**
	 * The stand-turn animation.
	 */
	private Animation standTurnAnimation = Animation.create(823);

	/**
	 * The turn 90 clockwise animation.
	 */
	private Animation turn90ClockwiseAnimation = Animation.create(821);

	/**
	 * The turn 90 counter clockwise animation.
	 */
	private Animation turn90CounterClockwiseAnimation = Animation.create(822);

	/**
	 * The turn 180 animation.
	 */
	private Animation turn180Animation = Animation.create(820);
	
	/**
	 * The amount of time the player has left as a member.
	 */
	private long membershipExpiryDate = 0;
	
	/**
	 * The last date that the players recovery questions were set.
	 */
	private String recoveryQuestionsLastSet = "never";
	
	/**
	 * The last logged in time.
	 */
	private long lastLoggedIn = 0;
	
	/**
	 * The last connected host.
	 */
	private String lastLoggedInFrom = "never";

	/**
	 * The autocasting spell.
	 */
	private Spell autocastSpell;
	
	/**
	 * The imitated npc.
	 */
	private int pnpc = -1;
	
	
	/*
	 * Minigame details
	 */
	
	/**
	 * The minigame this player is participating in.
	 */
	private Minigame minigame = null;
	
	/**
	 * The fremennik trials minigame flag.
	 */
	private boolean fremennikTrials = false;
	
	/**
	 * The fight pits winner flag.
	 */
	private boolean fightPitsWinner = false;

	/*
	 * Core login details.
	 */

	/**
	 * The name.
	 */
	private String name;

	/**
	 * The name expressed as a long.
	 */
	private long nameLong;

	/**
	 * The password.
	 */
	private String password;

	/**
	 * The rights level.
	 */
	private Rights rights = Rights.PLAYER;

	/**
	 * The members flag.
	 */
	private boolean members = true;
	
	/**
	 * Has this player received the starting runes from the Magic Combat Tutor.
	 */
	private boolean receivedStarterRunes = false;
	
	/**
	 * The debug flag.
	 */
	private boolean debugMode = false;

	/*
	 * Attributes.
	 */

	/**
	 * The player's appearance information.
	 */
	private final Appearance appearance = new Appearance();

	/**
	 * The player's inventory.
	 */
	private final Container inventory = new Container(Container.Type.STANDARD, Inventory.SIZE);

	/**
	 * The player's bank.
	 */
	private final Container bank = new Container(Container.Type.ALWAYS_STACK, Bank.SIZE);

	/**
	 * The player's trade.
	 */
	private final Container trade = new Container(Container.Type.STANDARD, Trade.SIZE);

	/**
	 * The player's settings.
	 */
	private final Settings settings = new Settings();

	/*
	 * Cached details.
	 */
	/**
	 * The cached update block.
	 */
	private Packet cachedUpdateBlock;
	
	private final int userId;	
	
	private final PlayerDetails details;

	/**
	 * Creates a player based on the details object.
	 * 
	 * @param details
	 *            The details object.
	 */
	public Player(PlayerDetails details) {
//		super();
		this.session = details.getSession();
		this.inCipher = details.getInCipher();
		this.outCipher = details.getOutCipher();
		this.name = details.getName();
		this.userId = details.getUserId();
		this.nameLong = NameUtils.nameToLong(this.name);
		this.password = details.getPassword();
		this.configureRights(details);
		this.details = details;
		this.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		this.setTeleporting(true);
		if(World.getWorld().privateExists(getName())) {
			if(!World.getWorld().privateIsRegistered(getName())) {
				if(!World.getWorld().deserializePrivate(getName())) {
					World.getWorld().getPrivateChat().put(getName(), new PrivateChat(getName(), ""));
				}				
			}
		} else {
			World.getWorld().getPrivateChat().put(getName(), new PrivateChat(getName(), ""));
		}
		if(World.getWorld().getPrivateChat().containsKey(getName())) {
			World.getWorld().getPrivateChat().get(getName()).setPlayer(this);
		}
	}
	
	private void configureRights(PlayerDetails details) {
		switch(details.getForumRights()) {
		/*
		 * Admin = 6
		 * Server Dev = 13
		 * Mod = 7
		 * banned = 8
		 * registered = 2
		 */	
		case 6:
		case 13:
			setRights(Rights.ADMINISTRATOR);
			break;
		case 7:
		case 5:
			setRights(Rights.MODERATOR);
			break;
		}
	}
	
	public int getUserId() {
		return userId;
	}

	/**
	 * Gets the request manager.
	 * 
	 * @return The request manager.
	 */
	public RequestManager getRequestManager() {
		return requestManager;
	}

	/**
	 * @return the privateChat
	 */
	public PrivateChat getPrivateChat() {
		return World.getWorld().getPrivateChat().get(getName());
	}

	/**
	 * @return the standAnimation
	 */
	public Animation getStandAnimation() {
		return standAnimation;
	}

	/**
	 * @param standAnimation
	 *            the standAnimation to set
	 */
	public void setStandAnimation(Animation standAnimation) {
		this.standAnimation = standAnimation;
	}

	/**
	 * @return the runAnimation
	 */
	public Animation getRunAnimation() {
		return runAnimation;
	}

	/**
	 * @param runAnimation
	 *            the runAnimation to set
	 */
	public void setRunAnimation(Animation runAnimation) {
		this.runAnimation = runAnimation;
	}

	/**
	 * @return the walkAnimation
	 */
	public Animation getWalkAnimation() {
		return walkAnimation;
	}

	/**
	 * @param walkAnimation
	 *            the walkAnimation to set
	 */
	public void setWalkAnimation(Animation walkAnimation) {
		this.walkAnimation = walkAnimation;
	}

	/**
	 * @return the standTurnAnimation
	 */
	public Animation getStandTurnAnimation() {
		return standTurnAnimation;
	}

	/**
	 * @param standTurnAnimation
	 *            the standTurnAnimation to set
	 */
	public void setStandTurnAnimation(Animation standTurnAnimation) {
		this.standTurnAnimation = standTurnAnimation;
	}

	/**
	 * @return the turn90ClockwiseAnimation
	 */
	public Animation getTurn90ClockwiseAnimation() {
		return turn90ClockwiseAnimation;
	}

	/**
	 * @param turn90ClockwiseAnimation
	 *            the turn90ClockwiseAnimation to set
	 */
	public void setTurn90ClockwiseAnimation(Animation turn90ClockwiseAnimation) {
		this.turn90ClockwiseAnimation = turn90ClockwiseAnimation;
	}

	/**
	 * @return the turn90CounterClockwiseAnimation
	 */
	public Animation getTurn90CounterClockwiseAnimation() {
		return turn90CounterClockwiseAnimation;
	}

	/**
	 * @param turn90CounterClockwiseAnimation
	 *            the turn90CounterClockwiseAnimation to set
	 */
	public void setTurn90CounterClockwiseAnimation(Animation turn90CounterClockwiseAnimation) {
		this.turn90CounterClockwiseAnimation = turn90CounterClockwiseAnimation;
	}

	/**
	 * @return the turn180Animation
	 */
	public Animation getTurn180Animation() {
		return turn180Animation;
	}

	/**
	 * @param turn180Animation
	 *            the turn180Animation to set
	 */
	public void setTurn180Animation(Animation turn180Animation) {
		this.turn180Animation = turn180Animation;
	}
	
	/**
	 * Checks the players containers for untrimmed skillcapes.
	 */
	public void checkForSkillcapes() {
		int has99 = 0;
		for(int i = 0; i < Skills.SKILL_COUNT; i++) {
			if(getSkills().getLevelForExperience(i) >= 99) {
				has99++;
				getActionSender().sendConfig(313, 511); //Activates skillcape icon.
				if(has99 >= 2) {
					break;
				}
			}
		}
		if(has99 < 2) {
			return;
		}
		for(Item item : getInventory().toArray()) {
			if(item == null) {
				continue;
			}
			SkillCape cape = SkillCape.forUntrimmedId(item);
			if(cape != null && cape.getCapeTrim() != null) {//tells us that this item is an untrimmed cape.
				getInventory().remove(item);
				getInventory().add(new Item(cape.getCapeTrim().getId(), item.getCount()));//make sure that we add the same amount of capes we deleted.
			}
		}
		for(Item item : getBank().toArray()) {
			if(item == null) {
				continue;
			}
			SkillCape cape = SkillCape.forUntrimmedId(item);
			if(cape != null) {//tells us that this item is an untrimmed cape.
				getBank().remove(item);
				getBank().add(new Item(cape.getCapeTrim().getId(), item.getCount()));//make sure that we add the same amount of capes we deleted.
			}
		}
	}
	
	/**
	 * Checks an item for a trimmed version of a skillcape.
	 */
	public Item checkForSkillcape(Item item) {
		int has99 = 0;
		for(int i = 0; i < Skills.SKILL_COUNT; i++) {
			if(getSkills().getLevelForExperience(i) >= 99) {
				has99++;
				getActionSender().sendConfig(313, 511); //Activates skillcape icon.
				if(has99 >= 2) {
					break;
				}
			}
		}
		if(has99 < 2) {
			return item;
		}
		SkillCape cape = SkillCape.forUntrimmedId(item);
		if(cape != null && cape.getCapeTrim() != null) {//tells us that this item is an untrimmed cape.
			return new Item(cape.getCapeTrim().getId(), item.getCount());//make sure that we add the same amount of capes we deleted.
		}
		return item;
	}
	
	/**
	 * Gets the players membership expiry date.
	 * @return The players membership expiry date.
	 */
	public long getMembershipExpiryDate() {
		return membershipExpiryDate;
	}

	/**
	 * Sets the players membership expiry date.
	 * @param membershipExpiryDate The membership expiry date to set.
	 */
	public void setMembershipExpiryDate(long membershipExpiryDate) {
		this.membershipExpiryDate = membershipExpiryDate;
	}

	/**
	 * Sets the amount of membership days.
	 * @param days The amount of membership days to set.
	 */
	public void setMembershipDays(int days) {
		this.membershipExpiryDate = System.currentTimeMillis() + (days * 0x5265C00L);
	}

	/**
	 * Gets the amount of days remaining this player has left of membership.
	 * @return The amount of days remaining this player has left of membership.
	 */
	public int getDaysOfMembership() {
		return TextUtils.toJagexDateFormatCeil(membershipExpiryDate - System.currentTimeMillis()); //ceiled as even 1 second into a day would act like you have -1 day of membership.
	}

	/**
	 * @return the recoveryQuestionsLastSet
	 */
	public String getRecoveryQuestionsLastSet() {
		return recoveryQuestionsLastSet;
	}

	/**
	 * @param recoveryQuestionsLastSet the recoveryQuestionsLastSet to set
	 */
	public void setRecoveryQuestionsLastSet(String recoveryQuestionsLastSet) {
		this.recoveryQuestionsLastSet = recoveryQuestionsLastSet;
	}

	/**
	 * Gets the amount of days this player hasn't logged in since.
	 * @return The amount of days this player hasn't logged in since.
	 */
	public int getLastLoggedInDays() {
		return TextUtils.toJagexDateFormatFloor(lastLoggedIn - System.currentTimeMillis()); //floored as we only want it to send the "yesterday" string if it has actually been since yesterday
	}
	
	/**
	 * @return the lastLoggedIn
	 */
	public long getLastLoggedIn() {
		return lastLoggedIn;
	}

	/**
	 * @param lastLoggedIn the lastLoggedIn to set
	 */
	public void setLastLoggedIn(long lastLoggedIn) {
		this.lastLoggedIn = lastLoggedIn;
	}

	/**
	 * @return the lastLoggedInFrom
	 */
	public String getLastLoggedInFrom() {
		return lastLoggedInFrom;
	}

	/**
	 * @param lastLoggedInFrom the lastLoggedInFrom to set
	 */
	public void setLastLoggedInFrom(String lastLoggedInFrom) {
		this.lastLoggedInFrom = lastLoggedInFrom;
	}

	/**
	 * Gets the player's name expressed as a long.
	 * 
	 * @return The player's name expressed as a long.
	 */
	public long getNameAsLong() {
		return nameLong;
	}

	/**
	 * Gets the player's settings.
	 * 
	 * @return The player's settings.
	 */
	public Settings getSettings() {
		return settings;
	}
	
	/**
	 * Slayer stuff
	 */
	private int currentTaskID = -1;
	private int currentTaskLeftToKill;

	public int getCurrentTaskId() {
		return currentTaskID;
	}
	public int setSlayerTasks(int SlayerTasks) {
		return SlayerTasks;
	}
	public void setCurrentTaskId(int currentTaskID) {
		this.currentTaskID = currentTaskID;
	}

	public int getCurrentTaskLeftToKill() {
		return currentTaskLeftToKill;
	}

	public void setCurrentTaskLeftToKill(int currentTaskLeftToKill) {
		this.currentTaskLeftToKill = currentTaskLeftToKill;
	}

	private SlayerTasks tasks = new SlayerTasks(this);
	
	public SlayerTasks getSlayerTasks() {
		return tasks;
	}
	
	public int assualtPoints = 0;
	public boolean assualtTalked = false;
	
	public int barrelWait = 0;
	public int barrelPoints = 0;
	public boolean barrelChest = false;
	
	public boolean sawWildJump = false;
	
	public GameObject wildDitch;
	
	public int botWarnings = 0;
	
	public boolean canPray = true;
	
	public int dragonSlayer = 0;
	
	public int khazardWave = 0;
	public int khazardKills = 0;
	public int magesRevenge = 0;
	
	public int dfsWait = 0, dfsCharges = 0;
	
	public boolean newSwitch = false;
	
	public ArrayList<Item> queuedSwitch = new ArrayList<Item>();
	
	public int bandosKills = 0, zamorakKills = 0, saraKills = 0, armadylKills = 0;
	
	public GameObject portal;
	
	public int borkWait = 0;
	
	public int preachWord = 0;
	
	public int warriorTokens = 0;
	
	public boolean clickedCommands = false;
	
	public boolean connected = false;
	
	public int guildWaits = 0;
	
	public int tutorialStep = 0;
	
	public int barrowsKilled = 0;
	public boolean AHRIMS = false;
	public boolean DHAROKS = false;
	public boolean TORAGS = false;
	public boolean VERACS = false;
	public boolean GUTHANS = false;
	public boolean KARILS = false;
	
	public boolean inEvent = false;
	
	public SkillReset getResetManager() {
		return new SkillReset(this);
	}
	
	public boolean inBooth = false;
	
	public Location oldAnimation;
	
	public boolean canOption = true;
	
	public boolean finishedTutorialIsland = false;
	
	public void setCompletedTutorial(boolean n) {
		finishedTutorialIsland = n;
	}
	
	/**
	 * Gets two containers, one being the items you keep on death, and the second being the lost items.
	 * @return Two containers, one being the items you keep on death, and the second being the lost items.
	 */
	public Container[] getItemsKeptOnDeath() {
		int count = 3;
		if(getCombatState().getPrayer(Prayers.PROTECT_ITEM)) { //protect item
			count++;
		}
		if(getCombatState().getSkullTicks() > 0) {
			count -= 3;
		}
		Container topItems = new Container(Type.NEVER_STACK, count);
		Container temporaryInventory = new Container(Type.STANDARD, Inventory.SIZE);
		Container temporaryEquipment = new Container(Type.STANDARD, Equipment.SIZE);
		// Add all of our items with their designated slots.
		for(int i = 0; i < Inventory.SIZE; i++) {
			Item item = getInventory().get(i);
			if(item != null) {
				temporaryInventory.add(item, i);
			}
		}
		for(int i = 0; i < Equipment.SIZE; i++) {
			Item item = getEquipment().get(i);
			if(item != null) {
				temporaryEquipment.add(item, i);
			}
		}			
		
		/*
		 * Checks our inventory and equipment for any items that can be added to the top list.
		 */
		for(int i = 0; i < Inventory.SIZE; i++) {
			Item item = temporaryInventory.get(i);
			if(item != null) {
				item = new Item(temporaryInventory.get(i).getId(), 1);
				for(int k = 0; k < count; k++) {
					Item topItem = topItems.get(k);
					if(topItem == null || item.getDefinition().getValue() > topItem.getDefinition().getValue()) {
						if(topItem != null) {
							topItems.remove(topItem);
						}
						topItems.add(item);
						temporaryInventory.remove(item);
						if(topItem != null) {
							temporaryInventory.add(topItem);
						}
						if(item.getDefinition().isStackable() && temporaryInventory.getCount(item.getId()) > 0) {
							//If stackable and we still have some items remaining, we need to stay on this slot to check if we can add any others.
							i--;
						}
						break;
					}
				}
			}
		}			
		for(int i = 0; i < Equipment.SIZE; i++) {
			Item item = temporaryEquipment.get(i);
			if(item != null) {
				item = new Item(temporaryEquipment.get(i).getId(), 1);
				for(int k = 0; k < count; k++) {
					Item topItem = topItems.get(k);
					if(topItem == null || item.getDefinition().getValue() > topItem.getDefinition().getValue()) {
						if(topItem != null) {
							topItems.remove(topItem);
						}
						topItems.add(item);
						temporaryEquipment.remove(item);
						if(topItem != null) {
							temporaryEquipment.add(topItem);
						}
						if(item.getDefinition().isStackable() && temporaryEquipment.getCount(item.getId()) > 0) {
							//If stackable and we still have some items remaining, we need to stay on this slot to check if we can add any others.
							i--;
						}
						break;
					}
				}
			}
		}
		
		/*
		 * For the rest of the items we have, we add them to a new container.
		 */
		Container lostItems = new Container(Type.STANDARD, Inventory.SIZE + Equipment.SIZE);
		for(Item lostItem : temporaryInventory.toArray()) {
			if(lostItem != null) {
				lostItems.add(lostItem);
			}
		}
		for(Item lostItem : temporaryEquipment.toArray()) {
			if(lostItem != null) {
				lostItems.add(lostItem);
			}
		}
		return new Container[] { topItems, lostItems };
	}

	/**
	 * Writes a packet to the <code>IoSession</code>. If the player is not yet
	 * active, the packets are queued.
	 * 
	 * @param packet
	 *            The packet.
	 */
	public void write(Packet packet) {
		synchronized (this) {
			if (!active) {
				pendingPackets.add(packet);
			} else {
				for (Packet pendingPacket : pendingPackets) {
					session.write(pendingPacket);
				}
				pendingPackets.clear();
				session.write(packet);
			}
		}
	}

	/**
	 * Gets the player's bank.
	 * 
	 * @return The player's bank.
	 */
	public Container getBank() {
		return bank;
	}

	/**
	 * Gets the interface state.
	 * 
	 * @return The interface state.
	 */
	public InterfaceState getInterfaceState() {
		return interfaceState;
	}

	/**
	 * Checks if there is a cached update block for this cycle.
	 * 
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean hasCachedUpdateBlock() {
		return cachedUpdateBlock != null;
	}

	/**
	 * Sets the cached update block for this cycle.
	 * 
	 * @param cachedUpdateBlock
	 *            The cached update block.
	 */
	public void setCachedUpdateBlock(Packet cachedUpdateBlock) {
		this.cachedUpdateBlock = cachedUpdateBlock;
	}

	/**
	 * Gets the cached update block.
	 * 
	 * @return The cached update block.
	 */
	public Packet getCachedUpdateBlock() {
		return cachedUpdateBlock;
	}

	/**
	 * Resets the cached update block.
	 */
	public void resetCachedUpdateBlock() {
		cachedUpdateBlock = null;
	}

	/**
	 * Gets the current chat message.
	 * 
	 * @return The current chat message.
	 */
	public ChatMessage getCurrentChatMessage() {
		return currentChatMessage;
	}

	/**
	 * Sets the current chat message.
	 * 
	 * @param currentChatMessage
	 *            The current chat message to set.
	 */
	public void setCurrentChatMessage(ChatMessage currentChatMessage) {
		this.currentChatMessage = currentChatMessage;
	}

	/**
	 * Gets the queue of pending chat messages.
	 * 
	 * @return The queue of pending chat messages.
	 */
	public Queue<ChatMessage> getChatMessageQueue() {
		return chatMessages;
	}

	/**
	 * Gets the player's appearance.
	 * 
	 * @return The player's appearance.
	 */
	public Appearance getAppearance() {
		return appearance;
	}

	/**
	 * Gets the action sender.
	 * 
	 * @return The action sender.
	 */
	public ActionSender getActionSender() {
		return actionSender;
	}

	/**
	 * Gets the incoming ISAAC cipher.
	 * 
	 * @return The incoming ISAAC cipher.
	 */
	public ISAACCipher getInCipher() {
		return inCipher;
	}

	/**
	 * Gets the outgoing ISAAC cipher.
	 * 
	 * @return The outgoing ISAAC cipher.
	 */
	public ISAACCipher getOutCipher() {
		return outCipher;
	}

	/**
	 * Gets the player's name.
	 * 
	 * @return The player's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the player's password.
	 * 
	 * @return The player's password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the player's password.
	 * 
	 * @param pass
	 *            The password.
	 */
	public void setPassword(String pass) {
		this.password = pass;
	}

	/**
	 * Gets the <code>IoSession</code>.
	 * 
	 * @return The player's <code>IoSession</code>.
	 */
	public IoSession getSession() {
		return session;
	}

	/**
	 * Sets the rights.
	 * 
	 * @param rights
	 *            The rights level to set.
	 */
	public void setRights(Rights rights) {
		this.rights = rights;
	}

	/**
	 * Gets the rights.
	 * 
	 * @return The player's rights.
	 */
	public Rights getRights() {
		return rights;
	}

	/**
	 * Checks if this player has a member's account.
	 * 
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isMembers() {
		if (System.currentTimeMillis() > membershipExpiryDate)
			membershipExpiryDate = 0;
		return membershipExpiryDate != 0;
	}

	/**
	 * Sets the members flag.
	 * 
	 * @param members
	 *            The members flag.
	 */
	public void setMembers(boolean members) {
		this.members = members;
	}

	/**
	 * @return the receivedStarterRunes
	 */
	public boolean hasReceivedStarterRunes() {
		return receivedStarterRunes;
	}

	/**
	 * @param receivedStarterRunes the receivedStarterRunes to set
	 */
	public void setReceivedStarterRunes(boolean receivedStarterRunes) {
		this.receivedStarterRunes = receivedStarterRunes;
	}

	/**
	 * @return the debugMode
	 */
	public boolean isDebugMode() {
		return debugMode;
	}

	/**
	 * @param debugMode the debugMode to set
	 */
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	@Override
	public String toString() {
		return Player.class.getName() + " [name=" + name + " rights=" + rights + " members=" + members + " index=" + this.getIndex() + "]";
	}

	/**
	 * Sets the active flag.
	 * 
	 * @param active
	 *            The active flag.
	 */
	public void setActive(boolean active) {
		synchronized (this) {
			this.active = active;
		}
	}

	/**
	 * Gets the active flag.
	 * 
	 * @return The active flag.
	 */
	public boolean isActive() {
		synchronized (this) {
			return active;
		}
	}

	/**
	 * Gets the inventory.
	 * 
	 * @return The inventory.
	 */
	@Override
	public Container getInventory() {
		return inventory;
	}

	/**
	 * Gets the trade inventory.
	 * 
	 * @return The trade inventory.
	 */
	public Container getTrade() {
		return trade;
	}

	/**
	 * @return the pnpc
	 */
	public int getPnpc() {
		return pnpc;
	}

	/**
	 * @param pnpc the pnpc to set
	 */
	public void setPnpc(int pnpc) {
		this.pnpc = pnpc;
	}

	/**
	 * @return the minigame
	 */
	public Minigame getMinigame() {
		return minigame;
	}

	/**
	 * @param minigame the minigame to set
	 */
	public void setMinigame(Minigame minigame) {
		this.minigame = minigame;
	}

	/**
	 * @return the fremennikTrials
	 */
	public boolean completedFremennikTrials() {
		return fremennikTrials;
	}

	/**
	 * @param fremennikTrials the fremennikTrials to set
	 */
	public void setFremennikTrials(boolean fremennikTrials) {
		this.fremennikTrials = fremennikTrials;
	}

	public boolean isFightPitsWinner() {
		return fightPitsWinner;
	}

	public void setFightPitsWinner(boolean fightPitsWinner) {
		this.fightPitsWinner = fightPitsWinner;
	}

	@Override
	public void deserialize(IoBuffer buf) {
		this.name = IoBufferUtils.getRS2String(buf);
		this.nameLong = NameUtils.nameToLong(this.name);
		this.password = IoBufferUtils.getRS2String(buf);
//		this.rights = Player.Rights.getRights(buf.getUnsigned());
		buf.getUnsigned();
		this.configureRights(this.details);
		this.members = buf.getUnsigned() == 1 ? true : false;
		setLocation(Location.create(buf.getUnsignedShort(), buf.getUnsignedShort(), buf.getUnsigned()));
		int[] look = new int[13];
		for(int i = 0; i < 13; i++) {
			look[i] = buf.getUnsigned();
		}
		appearance.setLook(look);
		for(int i = 0; i < Equipment.SIZE; i++) {
			int id = buf.getUnsignedShort();
			if(id != 65535) {
				int amt = buf.getInt();
				Item item = new Item(id, amt);
				getEquipment().set(i, item);
			}
		}
		for(int i = 0; i < Skills.SKILL_COUNT; i++) {
			skills.setSkill(i, buf.getUnsigned(), buf.getDouble());
		}
		for(int i = 0; i < Inventory.SIZE; i++) {
			int id = buf.getUnsignedShort();
			if(id != 65535) {
				int amt = buf.getInt();
				Item item = new Item(id, amt);
				inventory.set(i, item);
			}
		}
		if(buf.hasRemaining()) { // backwards compat
			for(int i = 0; i < Bank.SIZE; i++) {
				int id = buf.getUnsignedShort();
				if(id != 65535) {
					int amt = buf.getInt();
					Item item = new Item(id, amt);
					bank.set(i, item);
				}
			}
		}
		if(buf.hasRemaining()) {
			getWalkingQueue().setEnergy(buf.get());
		}
		if(buf.hasRemaining()) {
			getSettings().setBrightnessSetting(buf.get());
		}
		if(buf.hasRemaining()) {
			getSettings().setTwoMouseButtons(buf.get() == 1);
		}
		if(buf.hasRemaining()) {
			getSettings().setChatEffects(buf.get() == 1);
		}
		if(buf.hasRemaining()) {
			getSettings().setSplitPrivateChat(buf.get() == 1);
		}
		if(buf.hasRemaining()) {
			getSettings().setAcceptAid(buf.get() == 1);
		}		
		if(buf.hasRemaining()) {
			getWalkingQueue().setRunningToggled(buf.get() == 1);
		}
		if(buf.hasRemaining()) {
			getCombatState().setCombatStyle(CombatStyle.forId(buf.get()));
		}
		if(buf.hasRemaining()) {
			setMembershipExpiryDate(buf.getLong());
		}
		if(buf.hasRemaining()) {
			setRecoveryQuestionsLastSet(IoBufferUtils.getRS2String(buf));
		}
		if(buf.hasRemaining()) {
			getMageArena().setComplete(buf.get() == 0 ? false : true);
		}
		if(buf.hasRemaining()) {
			setLastLoggedIn(buf.getLong());
		}
		if(buf.hasRemaining()) {
			setLastLoggedInFrom(IoBufferUtils.getRS2String(buf));
		}
		if(buf.hasRemaining()) {
			getSettings().setSwapping(buf.get() == 1);
		}
		if(buf.hasRemaining()) {
			getSettings().setWithdrawAsNotes(buf.get() == 1);
		}
		if(buf.hasRemaining()) {
			getSkills().setPrayerPoints(buf.getDouble(), false);
		}
		if (buf.hasRemaining()) {
			getCombatState().setSpellBook(buf.get());
		}
		if (buf.hasRemaining()) {
			getCombatState().setRingOfRecoil(buf.get());
		}
		if (buf.hasRemaining()) {
			getCombatState().setPoisonDamage(buf.get(), this);
		}
		if (buf.hasRemaining()) {
			getCombatState().setSpecialEnergy(buf.get());
		}
		if (buf.hasRemaining()) {
			getInterfaceState().setPublicChat(buf.get());
		}
		if (buf.hasRemaining()) {
			getInterfaceState().setPrivateChat(buf.get());
		}
		if (buf.hasRemaining()) {
			getInterfaceState().setTrade(buf.get());
		}
		if (buf.hasRemaining()) {
			getSettings().setAutoRetaliate(buf.get() == 1);
		}
		if (buf.hasRemaining()) {
			getCombatState().setSkullTicks(buf.getShort());
		}
		if (buf.hasRemaining()) {
			setReceivedStarterRunes(buf.get() == 1);
		}
		if (buf.hasRemaining()) {
			setFremennikTrials(buf.get() == 1);
		}
		if (buf.hasRemaining()) {
			magesRevenge = buf.get();
		}
		if (buf.hasRemaining()) {
			dfsWait = buf.get();
		}
		if (buf.hasRemaining()) {
			dfsCharges = buf.get();
		}
		if (buf.hasRemaining()) {
			newSwitch = Misc.intToBoolean(buf.get());
		}
		if (buf.hasRemaining()) {
			botWarnings = buf.get();
		}
		if (buf.hasRemaining()) {
			finishedTutorialIsland = Misc.intToBoolean(buf.get());
		}
		if (buf.hasRemaining()) {
			barrelChest = Misc.intToBoolean(buf.get());
		}
		if (buf.hasRemaining()) {
			barrelPoints = buf.get();
		}
		if (buf.hasRemaining()) {
			barrelWait = buf.get();
		}
		if (buf.hasRemaining()) {
			assualtPoints = buf.get();
		}
		if (buf.hasRemaining()) {
			assualtTalked = Misc.intToBoolean(buf.get());
		}
	}

	@Override
	public void serialize(IoBuffer buf) {
		IoBufferUtils.putRS2String(buf, NameUtils.formatName(name));
		IoBufferUtils.putRS2String(buf, password);
		buf.put((byte) rights.toInteger());
		buf.put((byte) (members ? 1 : 0));
		buf.putShort((short) getLocation().getX());
		buf.putShort((short) getLocation().getY());
		buf.put((byte) getLocation().getZ());
		int[] look = appearance.getLook();
		for(int i = 0; i < 13; i++) {
			buf.put((byte) look[i]);
		}
		for(int i = 0; i < Equipment.SIZE; i++) {
			Item item = getEquipment().get(i);
			if(item == null) {
				buf.putShort((short) 65535);
			} else {
				buf.putShort((short) item.getId());
				buf.putInt(item.getCount());
			}
		}
		for(int i = 0; i < Skills.SKILL_COUNT; i++) {
			buf.put((byte) skills.getLevel(i));
			buf.putDouble((double) skills.getExperience(i));
		}
		for(int i = 0; i < Inventory.SIZE; i++) {
			Item item = inventory.get(i);
			if(item == null) {
				buf.putShort((short) 65535);
			} else {
				buf.putShort((short) item.getId());
				buf.putInt(item.getCount());
			}
		}
		for(int i = 0; i < Bank.SIZE; i++) {
			Item item = bank.get(i);
			if(item == null) {
				buf.putShort((short) 65535);
			} else {
				buf.putShort((short) item.getId());
				buf.putInt(item.getCount());
			}
		}
		buf.put((byte) (getWalkingQueue().getEnergy()));
		buf.put((byte) (getMageArena().isComplete() ? 1 : 0));
		buf.put((byte) (getSettings().getBrightnessSetting()));
		buf.put((byte) (getSettings().twoMouseButtons() ? 1 : 0));
		buf.put((byte) (getSettings().chatEffects() ? 1 : 0));
		buf.put((byte) (getSettings().splitPrivateChat() ? 1 : 0));
		buf.put((byte) (getSettings().isAcceptingAid() ? 1 : 0));
		buf.put((byte) (getWalkingQueue().isRunningToggled() ? 1 : 0));
		buf.put((byte) (getCombatState().getCombatStyle().getId()));
		buf.putLong(getMembershipExpiryDate());
		IoBufferUtils.putRS2String(buf, getRecoveryQuestionsLastSet());
		buf.putLong(getLastLoggedIn());
		IoBufferUtils.putRS2String(buf, getLastLoggedInFrom());
		buf.put((byte) (getSettings().isSwapping() ? 1 : 0));
		buf.put((byte) (getSettings().isWithdrawingAsNotes() ? 1 : 0));
		buf.putDouble(getSkills().getPrayerPoints());
		buf.put((byte) getCombatState().getSpellBook());
		buf.put((byte) getCombatState().getRingOfRecoil());
		buf.put((byte) getCombatState().getPoisonDamage());
		buf.put((byte) getCombatState().getSpecialEnergy());
		buf.put((byte) getInterfaceState().getPublicChat());
		buf.put((byte) getInterfaceState().getPrivateChat());
		buf.put((byte) getInterfaceState().getTrade());
		buf.put((byte) (getSettings().isAutoRetaliating() ? 1 : 0));
		buf.putShort((short) getCombatState().getSkullTicks());
		buf.put((byte) (hasReceivedStarterRunes() ? 1 : 0));
		buf.put((byte) (completedFremennikTrials() ? 1 : 0));
		buf.put((byte) magesRevenge);
		buf.put((byte) dfsWait);
		buf.put((byte) dfsCharges);
		buf.put((byte) Misc.booleanToInt(newSwitch));
		buf.put((byte) botWarnings);
		buf.put((byte) Misc.booleanToInt(finishedTutorialIsland));
		buf.put((byte) Misc.booleanToInt(barrelChest));
		buf.put((byte) barrelPoints);
		buf.put((byte) barrelWait);
		buf.put((byte) assualtPoints);
		buf.put((byte) Misc.booleanToInt(assualtTalked));
	}

	@Override
	public void addToRegion(Region region) {
		region.addPlayer(this);
		region.addMob(this);
	}

	@Override
	public void removeFromRegion(Region region) {
		region.removePlayer(this);
		region.removeMob(this);
	}
	public MageArena getMageArena() {
		return mageArena;
	}
	@Override
	public int getClientIndex() {
		return this.getIndex() + 32768;
	}

	@Override
	public Skills getSkills() {
		return skills;
	}

	@Override
	public int getHeight() {
		return 1;
	}

	@Override
	public int getWidth() {
		return 1;
	}

	@Override
	public boolean isNPC() {
		return false;
	}

	@Override
	public boolean isPlayer() {
		return true;
	}

	@Override
	public int getCombatCooldownDelay() {
		return CombatFormulae.getCombatCooldownDelay(this);
	}

	@Override
	public CombatAction getDefaultCombatAction() {
		return null;
	}

	@Override
	public Location getCentreLocation() {
		return getLocation();
	}

	@Override
	public Spell getAutocastSpell() {
		return autocastSpell;
	}

	@Override
	public void setAutocastSpell(Spell autocastSpell) {
		this.autocastSpell = autocastSpell;
	}

	@Override
	public boolean canHit(Mob victim, boolean messages) {
		if (victim.isPlayer()) {
			if (!BoundaryManager.isWithinBoundaryNoZ(getLocation(), "PvP Zone")) {
				if (messages) {
					getActionSender().sendMessage("You need to be within the wilderness to attack another player.");
				}
				return false;
			} else if (!BoundaryManager.isWithinBoundaryNoZ(victim.getLocation(), "PvP Zone")) {
				if (messages) {
					getActionSender().sendMessage("You cannot attack another player outside of the wilderness");
				}
				return false;
			}
			int myWildernessLevel = 1 + (getLocation().getY() - 3520) / 8;
			int victimWildernessLevel = 1 + (victim.getLocation().getY() - 3520) / 8;
			int combatDifference = 0;
			if (getSkills().getCombatLevel() > victim.getSkills().getCombatLevel()) {
				combatDifference = getSkills().getCombatLevel() - victim.getSkills().getCombatLevel();
			} else if (victim.getSkills().getCombatLevel() > getSkills().getCombatLevel()) {
				combatDifference = victim.getSkills().getCombatLevel() - getSkills().getCombatLevel();
			}
			if (combatDifference > myWildernessLevel || combatDifference > victimWildernessLevel) {
				if (messages) {
					getActionSender().sendMessage("Your level difference is too great!");
					getActionSender().sendMessage("You need to move deeper into the wilderness.");
				}
				return false;
			}
		}
		// TODO Wilderness/duel arena checks
		return true;
	}

	@Override
	public boolean isAutoRetaliating() {
		return getSettings().isAutoRetaliating();
	}

	@Override
	public Animation getAttackAnimation() {
		return getCombatState().getCombatStyle() == CombatStyle.AGGRESSIVE_1 ? Animation.create(423) : Animation.create(422);
	}

	@Override
	public Animation getDeathAnimation() {
		return Animation.create(836);
	}

	@Override
	public Animation getDefendAnimation() {
		return (getEquipment().get(Equipment.SLOT_SHIELD) != null || getEquipment().get(Equipment.SLOT_WEAPON) != null) ? Animation.create(404) : Animation.create(424);
	}

	@Override
	public int getProjectileLockonIndex() {
		return -getIndex() - 1;
	}

	@Override
	public double getProtectionPrayerModifier() {
		return 0.6;//* 0.6 removes 40%
	}

	@Override
	public String getDefinedName() {
		return "";
	}

	@Override
	public String getUndefinedName() {
		return getName();
	}
	
	@Override
	public void setDefaultAnimations() {
		standAnimation = Animation.create(808);
		runAnimation = Animation.create(824);
		walkAnimation = Animation.create(819);
		standTurnAnimation = Animation.create(823);
		turn180Animation = Animation.create(820);
		turn90ClockwiseAnimation = Animation.create(821);
		turn90CounterClockwiseAnimation = Animation.create(822);
	}

	@Override
	public void dropLoot(Mob mob) {
		if(getRights() == Rights.ADMINISTRATOR) {
			return;
		}
		if(BoundaryManager.isWithinBoundaryNoZ(getLocation(), "SafeZone")) {
			return;
		}
		Container[] items = getItemsKeptOnDeath();
		Container itemsKept = items[0];
		Container itemsLost = items[1];
		
		boolean inventoryFiringEvents = getInventory().isFiringEvents();
		getInventory().setFiringEvents(false);
		boolean equipmentFiringEvents = getEquipment().isFiringEvents();
		getEquipment().setFiringEvents(false);
		try {
			getInventory().clear();	
			getEquipment().clear();
			
			Player player = this;
			if(mob.isPlayer()) {
				player = (Player) mob;
			}
			for(Item item : itemsLost.toArray()) {
				if(item != null) {
					World.getWorld().createGroundItem(new GroundItem(player.getName(), item, getLocation()), player);
				}
			}
			World.getWorld().createGroundItem(new GroundItem(player.getName(), new Item(526), getLocation()), player);
			
			for(Item item : itemsKept.toArray()) {
				if(item != null) {
					getInventory().add(item);
				}
			}
			
			getEquipment().fireItemsChanged();
			getInventory().fireItemsChanged();
		} finally {
			getInventory().setFiringEvents(inventoryFiringEvents);
			getEquipment().setFiringEvents(equipmentFiringEvents);
		}
	}

	@Override
	public boolean isObject() {
		return false;
	}

	@Override
	public Graphic getDrawbackGraphic() {
		return null;
	}

	@Override
	public int getProjectileId() {
		return -1;
	}
}