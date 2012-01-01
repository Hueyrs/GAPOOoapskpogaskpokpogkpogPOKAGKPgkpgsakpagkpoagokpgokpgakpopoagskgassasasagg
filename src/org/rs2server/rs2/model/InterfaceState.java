package org.rs2server.rs2.model;

import java.util.ArrayList;
import java.util.List;

import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.container.ContainerListener;
import org.rs2server.rs2.model.container.Trade;
import org.rs2server.rs2.model.skills.Fletching;
import org.rs2server.rs2.model.skills.Herblore;
import org.rs2server.rs2.model.skills.Fletching.Log;
import org.rs2server.rs2.model.skills.Herblore.HerbloreType;
import org.rs2server.rs2.model.skills.Herblore.PrimaryIngredient;
import org.rs2server.rs2.model.skills.Herblore.SecondaryIngredient;


/**
 * Contains information about the state of interfaces open in the client.
 * @author Graham Edgecombe
 *
 */
public class InterfaceState {
	
	/**
	 * The current open interface.
	 */
	private int currentInterface = -1;
	
	/**
	 * The current open interface walkable flag.
	 */
	private boolean walkableInterface = false;

	/**
	 * The active enter amount interface.
	 */
	private int enterAmountInterfaceId = -1;
	
	/**
	 * The active enter amount id.
	 */
	private int enterAmountId;
	
	/**
	 * The active enter amount slot.
	 */
	private int enterAmountSlot;
	
	/**
	 * The open shop.
	 */
	private int openShop = -1;
	
	/**
	 * The main or player stock.
	 */
	private int openStockType;
	
	/**
	 * The open dialogue id.
	 */
	private int openDialogueId;
	
	/**
	 * The open autocast type.
	 * -1 = none
	 * 0 = normal
	 * 1 = defensive
	 */
	private int openAutocastType;
	
	/**
	 * The last used autocast config.
	 */
	private int lastUsedAutocast;
	
	/**
	 * The public chat config.
	 * 0 = On
	 * 1 = Friends
	 * 2 = Off
	 * 3 = Hide
	 */
	private int publicChat = 0;

	/**
	 * The private chat config.
	 * 0 = On
	 * 1 = Friends
	 * 2 = Off
	 * 3 = Hide
	 */
	private int privateChat = 0;

	/**
	 * The trade offer config.
	 * 0 = On
	 * 1 = Friends
	 * 2 = Off
	 * 3 = Hide
	 */
	private int trade = 0;
	
	/**
	 * The clan this player is in.
	 */
	private String clan = "";

	/**
	 * The next dialogue id.
	 */
	private int[] nextDialogueId = new int[] { -1, -1, -1, -1, -1 };

	/**
	 * The player.
	 */
	private Player player;
		
	/**
	 * A list of container listeners used on interfaces that have containers.
	 */
	private List<ContainerListener> containerListeners = new ArrayList<ContainerListener>();
	
	/**
	 * Creates the interface state.
	 */
	public InterfaceState(Player player) {
		this.player = player;
	}
	
	/**
	 * Checks if the specified interface is open.
	 * @param id The interface id.
	 * @return <code>true</code> if the interface is open, <code>false</code> if not.
	 */
	public boolean isInterfaceOpen(int id) {
		return currentInterface == id;
	}
	
	/**
	 * Gets the current open interface.
	 * @return The current open interface.
	 */
	public int getCurrentInterface() {
		return currentInterface;
	}
	
	/**
	 * Called when an interface is opened.
	 * @param id The interface.
	 */
	public void interfaceOpened(int id, boolean walkable) {
		if(currentInterface != -1 && currentInterface != id) {
			interfaceClosed();
		}
		walkableInterface = walkable;
		currentInterface = id;
	}
	
	/**
	 * Called when an interface is closed.
	 */
	public void interfaceClosed() {
		currentInterface = -1;
		enterAmountInterfaceId = -1;
		openShop = -1;
		openStockType = -1;
		walkableInterface = false;
		if(openDialogueId == 12) {
			player.getCombatState().setSpellbookSwap(false);
		}
		openDialogueId = -1;
		nextDialogueId = new int[] { -1, -1, -1, -1, -1 };
		removeListeners();
		player.getActionQueue().clearRemovableActions();
		player.removeAllInterfaceAttributes();
	}
	
	/**
	 * Removes all listeners.
	 */
	public void removeListeners() {
		for(ContainerListener l : containerListeners) {
			player.getInventory().removeListener(l);
			player.getEquipment().removeListener(l);
			player.getBank().removeListener(l);
		}
	}

	/**
	 * Adds a listener to an interface that is closed when the inventory is closed.
	 * @param container The container.
	 * @param containerListener The listener.
	 */
	public void addListener(Container container, ContainerListener containerListener) {
		container.addListener(containerListener);
		containerListeners.add(containerListener);
	}

	/**
	 * Called to open the enter amount interface.
	 * @param interfaceId The interface id.
	 * @param slot The slot.
	 * @param id The id.
	 */
	public void openEnterAmountInterface(int interfaceId, int slot, int id) {
		enterAmountInterfaceId = interfaceId;
		enterAmountSlot = slot;
		enterAmountId = id;
		player.getActionSender().sendEnterAmountInterface();
	}

	/**
	 * Called to open the enter amount interface.
	 * @param interfaceId The interface id.
	 * @param slot The slot.
	 * @param id The id.
	 */
	public void openEnterTextInterface(int interfaceId, String question) {
		enterAmountInterfaceId = interfaceId;
		player.getActionSender().sendEnterTextInterface(question);
	}
	
	/**
	 * Checks if the enter amount interface is open.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isEnterAmountInterfaceOpen() {
		return enterAmountInterfaceId != -1;
	}

	/**
	 * Called when the enter amount interface is closed.
	 * @param amount The amount that was entered.
	 */
	public void closeEnterAmountInterface(int amount) {
		try {
			switch(enterAmountInterfaceId) {
			case Bank.PLAYER_INVENTORY_INTERFACE:
				Bank.deposit(player, enterAmountSlot, enterAmountId, amount);
				break;
			case Bank.BANK_INVENTORY_INTERFACE:
				Bank.withdraw(player, enterAmountSlot, enterAmountId, amount);
				break;
			case Trade.TRADE_INVENTORY_INTERFACE:
				Trade.removeItem(player, enterAmountSlot, enterAmountId, amount);
				break;
			case Trade.PLAYER_INVENTORY_INTERFACE:
				Trade.offerItem(player, enterAmountSlot, enterAmountId, amount);
				break;
			case Shop.SHOP_INVENTORY_INTERFACE:
				Shop.buyItem(player, enterAmountSlot, enterAmountId, amount);
				break;
			case Shop.PLAYER_INVENTORY_INTERFACE:
				Shop.sellItem(player, enterAmountSlot, enterAmountId, amount);
				break;
			case 303:
			case 304:
			case 305:
			case 306:
				if(player.getInterfaceAttribute("fletching_log") != null && player.getInterfaceAttribute("fletching_index") != null) {
					player.getActionQueue().addAction(new Fletching(player, amount, (Integer) player.getInterfaceAttribute("fletching_index"), (Log) player.getInterfaceAttribute("fletching_log")));
				}
				break;
			case 309:
				if(player.getInterfaceAttribute("herblore_index") != null && player.getInterfaceAttribute("herblore_type") != null) {
					switch((HerbloreType) player.getInterfaceAttribute("herblore_type")) {
					case PRIMARY_INGREDIENT:
						player.getActionQueue().addAction(new Herblore(player, amount, PrimaryIngredient.forId((Integer) player.getInterfaceAttribute("herblore_index")), null, HerbloreType.PRIMARY_INGREDIENT));
						break;
					case SECONDARY_INGREDIENT:
						player.getActionQueue().addAction(new Herblore(player, amount, null, SecondaryIngredient.forId((Integer) player.getInterfaceAttribute("herblore_index")), HerbloreType.SECONDARY_INGREDIENT));
						break;
					}
				}
				break;
			}
		} finally {
			enterAmountInterfaceId = -1;
			player.getActionSender().removeChatboxInterface();
		}
	}

	/**
	 * Called when the enter amount interface is closed.
	 * @param amount The amount that was entered.
	 */
	public void closeEnterTextInterface(String text) {
		try {
			switch(enterAmountInterfaceId) {
			case 590:
				if(text.length() < 1) {
					return;
				}
				player.getPrivateChat().setChannelName(text);
				if(player.getPrivateChat().getMembers().size() > 0) {
					player.getPrivateChat().updateClanMembers();
				}
				player.getActionSender().sendString(590, 32, text);
				break;
			}
		} finally {
			enterAmountInterfaceId = -1;
		}
	}

	/**
	 * @return the openShop
	 */
	public int getOpenShop() {
		return openShop;
	}

	/**
	 * @param openShop the openShop to set
	 */
	public void setOpenShop(int openShop) {
		this.openShop = openShop;
	}

	/**
	 * @return the openStockType
	 */
	public int getOpenStockType() {
		return openStockType;
	}

	/**
	 * @param openStockType the openStockType to set
	 */
	public void setOpenStockType(int openStockType) {
		this.openStockType = openStockType;
	}

	/**
	 * @return the openDialogueId
	 */
	public int getOpenDialogueId() {
		return openDialogueId;
	}

	/**
	 * @param openDialogueId the openDialogueId to set
	 */
	public void setOpenDialogueId(int openDialogueId) {
		this.openDialogueId = openDialogueId;
	}

	/**
	 * @return the nextDialogueId
	 */
	public int getNextDialogueId(int index) {
		return nextDialogueId[index];
	}

	/**
	 * @param nextDialogueId the nextDialogueId to set
	 */
	public void setNextDialogueId(int index, int nextDialogueId) {
		this.nextDialogueId[index] = nextDialogueId;
	}
	
	/**
	 * @return the openAutocastType
	 */
	public int getOpenAutocastType() {
		return openAutocastType;
	}

	/**
	 * @param openAutocastType the openAutocastType to set
	 */
	public void setOpenAutocastType(int openAutocastType) {
		this.openAutocastType = openAutocastType;
	}

	/**
	 * @return the lastUsedAutocast
	 */
	public int getLastUsedAutocast() {
		return lastUsedAutocast;
	}

	/**
	 * @param lastUsedAutocast the lastUsedAutocast to set
	 */
	public void setLastUsedAutocast(int lastUsedAutocast) {
		this.lastUsedAutocast = lastUsedAutocast;
	}
	
	/**
	 * @return the publicChat
	 */
	public int getPublicChat() {
		return publicChat;
	}

	/**
	 * @param publicChat the publicChat to set
	 */
	public void setPublicChat(int publicChat) {
		this.publicChat = publicChat;
	}
	
	/**
	 * @return the privateChat
	 */
	public int getPrivateChat() {
		return privateChat;
	}

	/**
	 * @param privateChat the privateChat to set
	 */
	public void setPrivateChat(int privateChat) {
		this.privateChat = privateChat;
	}

	/**
	 * @return the trade
	 */
	public int getTrade() {
		return trade;
	}

	/**
	 * @param trade the trade to set
	 */
	public void setTrade(int trade) {
		this.trade = trade;
	}

	/**
	 * @return the clan
	 */
	public String getClan() {
		return clan;
	}

	/**
	 * @param clan the clan to set
	 */
	public void setClan(String clan) {
		this.clan = clan;
	}
	
	/**
	 * @return The walkableInterface.
	 */
	public boolean isWalkableInterface() {
		return walkableInterface;
	}

	/**
	 * @param walkableInterface The walkableInterface to set.
	 */
	public void setWalkableInterface(boolean walkableInterface) {
		this.walkableInterface = walkableInterface;
	}

}
