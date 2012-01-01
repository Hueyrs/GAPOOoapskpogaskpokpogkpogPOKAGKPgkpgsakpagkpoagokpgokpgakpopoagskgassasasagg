package org.rs2server.rs2.model.container;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.ItemDefinition;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.RequestManager.RequestState;
import org.rs2server.rs2.model.container.Container.Type;
import org.rs2server.rs2.model.container.impl.InterfaceContainerListener;

public class Trade {
	
	/**
	 * The trade size.
	 */
	public static final int SIZE = 28;
	
	/**
	 * The player inventory interface.
	 */
	public static final int PLAYER_INVENTORY_INTERFACE = 336;

	/**
	 * The trade inventory interface.
	 */
	public static final int TRADE_INVENTORY_INTERFACE = 335;

	/**
	 * The trade inventory interface.
	 */
	public static final int SECOND_TRADE_SCREEN = 334;
	
	/**
	 * Opens the trade for the specified player.
	 * @param player The player to open the trade for.
	 */
	public static void open(Player player, Player partner) {
		player.getActionSender().sendRunScript(150, Constants.TRADE_PARAMETERS_2, Constants.TRADE_TYPE_STRING)
								.sendRunScript(150, Constants.TRADE_PARAMETERS_1, "iiiiii")
								.sendRunScript(150, Constants.OFFER_PARAMETERS, Constants.TRADE_TYPE_STRING)
								.sendInterface(TRADE_INVENTORY_INTERFACE, false)
								.sendInterfaceInventory(PLAYER_INVENTORY_INTERFACE)
								.sendString(TRADE_INVENTORY_INTERFACE, 56, "")
								.sendAccessMask(1278, 48, TRADE_INVENTORY_INTERFACE, 0, 28)
								.sendAccessMask(1026, 50, TRADE_INVENTORY_INTERFACE, 0, 28)
								.sendAccessMask(1278, 0, PLAYER_INVENTORY_INTERFACE, 0, 28)
								.sendString(335, 16, "Trading with: " + partner.getName())
								.sendString(335, 20, partner.getName() + " has " + partner.getInventory().freeSlots() + " free inventory slots.");
		player.getInterfaceState().addListener(player.getTrade(), new InterfaceContainerListener(player, -1, 1, 81));
		player.getInterfaceState().addListener(player.getInventory(), new InterfaceContainerListener(player, -1, 1, 82));
		player.getInterfaceState().addListener(partner.getTrade(), new InterfaceContainerListener(player, -2, TRADE_INVENTORY_INTERFACE << 16 | 50, 80));

		partner.getActionSender().sendRunScript(150, Constants.TRADE_PARAMETERS_2, Constants.TRADE_TYPE_STRING)
								.sendRunScript(150, Constants.TRADE_PARAMETERS_1, "iiiiii")
								.sendRunScript(150, Constants.OFFER_PARAMETERS, Constants.TRADE_TYPE_STRING)
								.sendInterface(TRADE_INVENTORY_INTERFACE, false)
								.sendInterfaceInventory(PLAYER_INVENTORY_INTERFACE)
								.sendString(TRADE_INVENTORY_INTERFACE, 56, "")
								.sendAccessMask(1278, 48, TRADE_INVENTORY_INTERFACE, 0, 28)
								.sendAccessMask(1026, 50, TRADE_INVENTORY_INTERFACE, 0, 28)
								.sendAccessMask(1278, 0, PLAYER_INVENTORY_INTERFACE, 0, 28)
								.sendString(335, 16, "Trading with: " + player.getName())
								.sendString(335, 20, player.getName() + " has " + player.getInventory().freeSlots() + " free inventory slots.");
		partner.getInterfaceState().addListener(partner.getTrade(), new InterfaceContainerListener(partner, -1, 1, 81));
		partner.getInterfaceState().addListener(partner.getInventory(), new InterfaceContainerListener(partner, -1, 1, 82));
		partner.getInterfaceState().addListener(player.getTrade(), new InterfaceContainerListener(partner, -2, TRADE_INVENTORY_INTERFACE << 16 | 50, 80));
	}

	/**
	 * Offers an item.
	 * @param player The player.
	 * @param slot The slot in the player's inventory.
	 * @param id The item id.
	 * @param amount The amount of the item to offer.
	 */
	public static void offerItem(Player player, int slot, int id, int amount) {
		if(player.getInterfaceState().getCurrentInterface() != TRADE_INVENTORY_INTERFACE) {
			return;
		}
		player.getActionSender().removeChatboxInterface();
		Item item = player.getInventory().get(slot);
		if(item == null) {
			return; // invalid packet, or client out of sync
		}
		if(item.getId() != id) {
			return; // invalid packet, or client out of sync
		}
		for(int i = 0; i < ItemDefinition.untradableItems.length; i++) {
			if(item.getId() == ItemDefinition.untradableItems[i]) {
				player.getActionSender().sendMessage("You cannot trade this item.");
				return;
			}
		}
		Player partner = player.getRequestManager().getAcquaintance();
		if(partner == null) {
			return;
		}
		player.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
		partner.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
		player.getRequestManager().setState(RequestState.PARTICIPATING);
		partner.getRequestManager().setState(RequestState.PARTICIPATING);
		boolean inventoryFiringEvents = player.getInventory().isFiringEvents();
		player.getInventory().setFiringEvents(false);
		try {
			int transferAmount = player.getInventory().getCount(id);
			if(transferAmount >= amount) {
				transferAmount = amount;
			} else if(transferAmount == 0) {
				return; // invalid packet, or client out of sync
			}
			
			if(player.getTrade().add(new Item(item.getId(), transferAmount), -1)) {
				player.getInventory().remove(new Item(item.getId(), transferAmount));
			}
			player.getInventory().fireItemsChanged();
		} finally {
			player.getInventory().setFiringEvents(inventoryFiringEvents);
			updateFirstScreen(player);
			player.getActionSender().removeChatboxInterface();
		}
	}

	/**
	 * Removes an offered an item.
	 * @param player The player.
	 * @param slot The slot in the player's inventory.
	 * @param id The item id.
	 * @param amount The amount of the item to offer.
	 */
	public static void removeItem(Player player, int slot, int id, int amount) {
		if(player.getInterfaceState().getCurrentInterface() != TRADE_INVENTORY_INTERFACE) {
			return;
		}
		player.getActionSender().removeChatboxInterface();
		Player partner = player.getRequestManager().getAcquaintance();
		if(partner == null) {
			return;
		}
		player.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
		partner.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
		player.getRequestManager().setState(RequestState.PARTICIPATING);
		partner.getRequestManager().setState(RequestState.PARTICIPATING);
		boolean inventoryFiringEvents = player.getInventory().isFiringEvents();
		player.getInventory().setFiringEvents(false);
		try {			
			Item item = player.getTrade().get(slot);
			if(item == null || item.getId() != id) {
				return; // invalid packet, or client out of sync
			}
			int transferAmount = player.getTrade().getCount(id);
			if(transferAmount >= amount) {
				transferAmount = amount;
			} else if(transferAmount == 0) {
				return; // invalid packet, or client out of sync
			}
			
			if(player.getInventory().add(new Item(item.getId(), transferAmount), -1)) {
				player.getTrade().remove(new Item(item.getId(), transferAmount));
			}
			player.getInventory().fireItemsChanged();
		} finally {
			player.getInventory().setFiringEvents(inventoryFiringEvents);
			updateFirstScreen(player);
			player.getActionSender().removeChatboxInterface();
		}
	}
	
	public static void secondScreen(Player player) {
		Player partner = player.getRequestManager().getAcquaintance();
		if(partner == null) {
			return;
		}
		player.getActionSender().removeChatboxInterface();
		partner.getActionSender().removeChatboxInterface();
		int myFreeSpaces = player.getTrade().freeSlots();
		int otherFreeSpaces = partner.getTrade().freeSlots();
		boolean myOneLine = myFreeSpaces >= 14;
		boolean otherOneLine = otherFreeSpaces >= 14;
		if(!myOneLine) {
			Container firstHalf = new Container(Type.STANDARD, 14);
			Container secondHalf = new Container(Type.STANDARD, 14);
			for(Item item : player.getTrade().toArray()) {
				if(!firstHalf.add(item)) {
					secondHalf.add(item);
				}
			}
			String firstHalfString = listContainerContents(firstHalf).replace("<col=FFFFFF>Absolutely nothing!", "");
			String secondHalfString = listContainerContents(secondHalf).replace("<col=FFFFFF>Absolutely nothing!", "");
			player.getActionSender().sendInterfaceConfig(334, 38, false) // Thin text left - Your offer
									.sendInterfaceConfig(334, 39, false) // Thin text right - Your offer
									.sendInterfaceConfig(334, 37, true) //Bold text centre - Your offer
									.sendString(334, 38, firstHalfString)
									.sendString(334, 39, secondHalfString); 
			
			partner.getActionSender().sendInterfaceConfig(334, 41, false) // Thin text left - Opponents offer
									.sendInterfaceConfig(334, 42, false) // Thin text right - Opponents offer
									.sendInterfaceConfig(334, 40, true) // Bold text centre - Opponents offer
									.sendString(334, 41, firstHalfString)
									.sendString(334, 42, secondHalfString);
		} else {
			player.getActionSender().sendInterfaceConfig(334, 38, true) // Thin text left - Your offer
									.sendInterfaceConfig(334, 39, true) // Thin text right - Your offer
									.sendInterfaceConfig(334, 37, false) //Bold text centre - Your offer
									.sendString(334, 37, listContainerContents(player.getTrade()));
			
			partner.getActionSender().sendInterfaceConfig(334, 41, true) // Thin text left - Opponents offer
									.sendInterfaceConfig(334, 42, true) // Thin text right - Opponents offer
									.sendInterfaceConfig(334, 40, false) // Bold text centre - Opponents offer
									.sendString(334, 40, listContainerContents(player.getTrade()));			
		}
		if(!otherOneLine) {
			Container firstHalf = new Container(Type.STANDARD, 14);
			Container secondHalf = new Container(Type.STANDARD, 14);
			for(Item item : partner.getTrade().toArray()) {
				if(!firstHalf.add(item)) {
					secondHalf.add(item);
				}
			}
			String firstHalfString = listContainerContents(firstHalf).replace("<col=FFFFFF>Absolutely nothing!", "");
			String secondHalfString = listContainerContents(secondHalf).replace("<col=FFFFFF>Absolutely nothing!", "");
			player.getActionSender().sendInterfaceConfig(334, 41, false) // Thin text left - Opponents offer
									.sendInterfaceConfig(334, 42, false) // Thin text right - Opponents offer
									.sendInterfaceConfig(334, 40, true) // Bold text centre - Opponents offer
									.sendString(334, 41, firstHalfString)
									.sendString(334, 42, secondHalfString); 

			partner.getActionSender().sendInterfaceConfig(334, 38, false) // Thin text left - Your offer
									.sendInterfaceConfig(334, 39, false) // Thin text right - Your offer
									.sendInterfaceConfig(334, 37, true) //Bold text centre - Your offer
									.sendString(334, 38, firstHalfString)
									.sendString(334, 39, secondHalfString);
			
		} else {
			player.getActionSender().sendInterfaceConfig(334, 41, true) // Thin text left - Opponents offer
									.sendInterfaceConfig(334, 42, true) // Thin text right - Opponents offer
									.sendInterfaceConfig(334, 40, false) // Bold text centre - Opponents offer
									.sendString(334, 40, listContainerContents(partner.getTrade()));

			partner.getActionSender().sendInterfaceConfig(334, 38, true) // Thin text left - Your offer
									.sendInterfaceConfig(334, 39, true) // Thin text right - Your offer
									.sendInterfaceConfig(334, 37, false) //Bold text centre - Your offer
									.sendString(334, 37, listContainerContents(partner.getTrade()));					
		}
		player.getActionSender().sendInterface(SECOND_TRADE_SCREEN, false)
							.sendString(334, 44, "<col=00FFFF>Trading with:<br><col=00FFFF>" + partner.getName());
		partner.getActionSender().sendInterface(SECOND_TRADE_SCREEN, false)
							.sendString(334, 44, "<col=00FFFF>Trading with:<br><col=00FFFF>" + player.getName());
//		player.getActionSender().sendString(SECOND_TRADE_SCREEN, 37, "sup1")
//								.sendString(SECOND_TRADE_SCREEN, 41, "hiiii")
//								.sendInterfaceConfig(334, 37, false) //Bold text centre - Your offer
//								.sendInterfaceConfig(334, 38, false) // Thin text left - Your offer
//								.sendInterfaceConfig(334, 39, false) // Thin text right - Your offer
//								.sendInterfaceConfig(334, 40, false) // Bold text centre - Opponents offer
//								.sendInterfaceConfig(334, 41, false) // Thin text left - Opponents offer
//								.sendInterfaceConfig(334, 42, false) // Thin text right - Opponents offer
//								.sendInterfaceConfig(334, 45, false) // Trade modified - Your offer
//								.sendInterfaceConfig(334, 46, false) // Trade modified - Opponents offer
////								.sendInterfaceConfig(334, 41, false)
////								.sendInterfaceConfig(334, 45, true)
////								.sendInterfaceConfig(334, 46, true)
//								.sendInterface(SECOND_TRADE_SCREEN);
				
//		String tradeString = "Absolutely nothing!";
//		String tradeStringAmt = "";
//		int amt = 0;
//		for (int i = 0; i < SIZE; i++) {
//			if (player.getTrade().get(i) != null) {
//				if (player.getTrade().get(i).getCount() >= 1000 && player.getTrade().get(i).getCount() < 1000000) {
//					tradeStringAmt = "@cya@" + (player.getTrade().get(i).getCount() / 1000) + "K @whi@(" + NumberFormat.getInstance().format(player.getTrade().get(i).getCount()) + ")";
//				}  else if (player.getTrade().get(i).getCount() >= 1000000) {
//					tradeStringAmt = "@gre@" + (player.getTrade().get(i).getCount() / 1000000) + " million @whi@(" + NumberFormat.getInstance().format(player.getTrade().get(i).getCount()) + ")";
//				} else {
//					tradeStringAmt = "" + NumberFormat.getInstance().format(player.getTrade().get(i).getCount());
//				}
//
//				if(amt == 0) {
//					tradeString = ItemDefinition.forId(player.getTrade().get(i).getId()).getName();
//				} else {
//					tradeString = tradeString + "\\n" + ItemDefinition.forId(player.getTrade().get(i).getId()).getName();
//				}
//				if (ItemDefinition.forId(player.getTrade().get(i).getId()).isStackable()) {
//					tradeString = tradeString + " x " + tradeStringAmt;
//				}
//				amt++;
//			}
//		}
//
//		player.getActionSender().sendString(3557, tradeString);
//		tradeString = "Absolutely nothing!";
//		tradeStringAmt = "";
//		amt = 0;
//
//		for (int i = 0; i < SIZE; i++) {
//			if (partner.getTrade().get(i) != null) {
//				if (partner.getTrade().get(i).getCount() >= 1000 && partner.getTrade().get(i).getCount() < 1000000) {
//					tradeStringAmt = "@cya@" + (partner.getTrade().get(i).getCount() / 1000) + "K @whi@(" + NumberFormat.getInstance().format(partner.getTrade().get(i).getCount()) + ")";
//				}  else if (partner.getTrade().get(i).getCount() >= 1000000) {
//					tradeStringAmt = "@gre@" + (partner.getTrade().get(i).getCount() / 1000000) + " million @whi@(" + NumberFormat.getInstance().format(partner.getTrade().get(i).getCount()) + ")";
//				} else {
//					tradeStringAmt = "" + NumberFormat.getInstance().format(partner.getTrade().get(i).getCount());
//				}
//
//				if (amt == 0) {
//					tradeString = ItemDefinition.forId(partner.getTrade().get(i).getId()).getName();
//				} else {
//					tradeString = tradeString + "\\n" +  ItemDefinition.forId(partner.getTrade().get(i).getId()).getName();
//				}
//				if (ItemDefinition.forId(partner.getTrade().get(i).getId()).isStackable()) {
//					tradeString = tradeString + " x " + tradeStringAmt;
//				}
//				
//				amt++;
//			}
//		}
//		player.getActionSender().sendString(3558, tradeString);
//		player.getActionSender().sendInventoryInterface(3443, 3213);	
	}
	
	public static void acceptTrade(Player player, int screenStage) {
		Player partner = player.getRequestManager().getAcquaintance();
		if(partner == null) {
			return;
		}
		switch(screenStage) {
		case 1:
			if(player.getInventory().freeSlots() < partner.getTrade().size()) {
				player.getActionSender().sendMessage("You do not have enough free inventory slots.");
				player.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
				partner.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
				player.getRequestManager().setState(RequestState.PARTICIPATING);
				partner.getRequestManager().setState(RequestState.PARTICIPATING);
				return;
			}
			for(Item item : partner.getTrade().toArray()) {
				if(item != null && item.getDefinition().isStackable() && player.getInventory().getCount(item.getId()) > 0) {
					long partnerCount = player.getInventory().getCount(item.getId());
					long myCount = item.getCount();
					long totalCount = (partnerCount + myCount);
					if(totalCount > Integer.MAX_VALUE) {
						player.getActionSender().sendMessage("You cannot accept this amount of " + item.getDefinition().getName() + (item.getDefinition().getName().endsWith("s") ? "" : "s") + ".");
						player.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
						partner.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
						player.getRequestManager().setState(RequestState.PARTICIPATING);
						partner.getRequestManager().setState(RequestState.PARTICIPATING);
						return;						
					}
				}
			}
			if(partner.getInventory().freeSlots() < player.getTrade().size()) {
				player.getActionSender().sendMessage("The other player does not have enough free inventory slots.");
				player.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
				partner.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
				player.getRequestManager().setState(RequestState.PARTICIPATING);
				partner.getRequestManager().setState(RequestState.PARTICIPATING);
				return;
			}
			for(Item item : player.getTrade().toArray()) {
				if(item != null && item.getDefinition().isStackable() && partner.getInventory().getCount(item.getId()) > 0) {
					long partnerCount = partner.getInventory().getCount(item.getId());
					long myCount = item.getCount();
					long totalCount = (partnerCount + myCount);
					if(totalCount > Integer.MAX_VALUE) {
						player.getActionSender().sendMessage("The other player cannot accept this amount of " + item.getDefinition().getName() + (item.getDefinition().getName().endsWith("s") ? "" : "s") + ".");
						player.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
						partner.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "");
						player.getRequestManager().setState(RequestState.PARTICIPATING);
						partner.getRequestManager().setState(RequestState.PARTICIPATING);
						return;						
					}
				}
			}
			if(partner.getRequestManager().getState() == RequestState.CONFIRM_1) {
				secondScreen(player);
				return;
			}
			player.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "Waiting for other player...");
			partner.getActionSender().sendString(TRADE_INVENTORY_INTERFACE, 56, "Other player has accepted");
			player.getRequestManager().setState(RequestState.CONFIRM_1);
			break;
		case 2:
			if(partner.getRequestManager().getState() == RequestState.CONFIRM_2) {
				player.getRequestManager().finishRequest();
			} else {
				player.getRequestManager().setState(RequestState.CONFIRM_2);
				player.getActionSender().sendString(SECOND_TRADE_SCREEN, 33, "Waiting for other player...");
				partner.getActionSender().sendString(SECOND_TRADE_SCREEN, 33, "Other player has accepted");
			}
			break;
		}
		player.getActionSender().removeChatboxInterface();
	}
	
	/**
	 * Updates the first trade screen for a player and the acquaintance.
	 * @param player The player.
	 */
	public static void updateFirstScreen(Player player) {
		if(player.getInterfaceState().getCurrentInterface() != TRADE_INVENTORY_INTERFACE) {
			return;
		}
		Player partner = player.getRequestManager().getAcquaintance();
		if(partner == null) {
			return;
		}
		player.getActionSender().sendString(335, 20, partner.getName() + " has " + partner.getInventory().freeSlots() + " free inventory slots.");
		partner.getActionSender().sendString(335, 20, player.getName() + " has " + player.getInventory().freeSlots() + " free inventory slots.");
	}
	
	/**
	 * Creates a string with a list of each item in a container and it's amount.
	 * @param container The container.
	 * @return A string with a list of each item in a container and it's amount.
	 */
	private static String listContainerContents(Container container) {
		if (container.freeSlots() == container.capacity()) {
			return "<col=FFFFFF>Absolutely nothing!";
		} else {
			StringBuilder bldr = new StringBuilder();
			for (int i = 0; i < container.capacity(); i++) {
				Item item = container.get(i);
				if (item != null) {
					bldr.append("<col=FF9040>" + item.getDefinition().getName());
					if (item.getCount() > 1) {
						bldr.append(" <col=FFFFFF> x <col=FFFFFF>" + item.getCount());
					}
					bldr.append("<br>");
				}
			}
			return bldr.toString();
		}
	}
}
