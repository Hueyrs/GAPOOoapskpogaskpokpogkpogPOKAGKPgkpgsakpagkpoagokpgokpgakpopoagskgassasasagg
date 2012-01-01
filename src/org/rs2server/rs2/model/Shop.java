package org.rs2server.rs2.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.container.Container.Type;
import org.rs2server.rs2.model.container.impl.InterfaceContainerListener;
import org.rs2server.util.XMLController;


/**
 * Shopping utility class.
 * @author Michael Bull
 *
 */
public class Shop {
	
	/**
	 * The logger instance.
	 */
	private static final Logger logger = Logger.getLogger(Shop.class.getName());

	/**
	 * The list of registered shops.
	 */
	private static List<Shop> shops;
	
	/**
	 * Returns a shop by its ID.
	 * @param id The shop ID.
	 * @return The shop.
	 */
	public static Shop forId(int id) {
		return shops.get(id);
	}
	
	/**
	 * The id of the main stock.
	 */
	private int mainStockId;

	/**
	 * The shops name, as displayed
	 * on the interface.
	 */
	private String name = "Shop";
	
	/**
	 * The shop's type.
	 */
	private ShopType shopType;
	
	/**
	 * The shop's currency.
	 */
	private int currency = 995;
	
	/**
	 * The starting stock of this shop.
	 */
	private Item[] mainItems;

	/**
	 * The starting stock of this shop.
	 */
	private Item[] playerItems;

	/**
	 * The default stock of the shop.
	 */
	private Container defaultStock;

	/**
	 * The stock of the shop.
	 */
	private Container mainStock;

	/**
	 * The stock of the shop.
	 */
	private Container playerStock;
	
	/**
	 * @return the mainStockId
	 */
	public int getMainStockId() {
		return mainStockId;
	}
	
	/**
	 * @return the mainItems
	 */
	public Item[] getMainItems() {
		return mainItems;
	}

	/**
	 * @return the playerItems
	 */
	public Item[] getPlayerItems() {
		return playerItems;
	}
	
	/**
	 * @param defaultStock the defaultStock to set
	 */
	public void setDefaultStock(Container defaultStock) {
		this.defaultStock = defaultStock;
	}
	
	/**
	 * @param mainStock the mainStock to set
	 */
	public void setMainStock(Container mainStock) {
		this.mainStock = mainStock;
	}
	
	/**
	 * @param playerStock the playerStock to set
	 */
	public void setPlayerStock(Container playerStock) {
		this.playerStock = playerStock;
	}

	/**
	 * Gets the shop's main stock.
	 * @return The shop's main stock.
	 */
	public Container getMainStock() {
		return mainStock;
	}
	
	/**
	 * Gets the shop's player stock.
	 * @return The shop's player stock.
	 */
	public Container getPlayerStock() {
		return playerStock;
	}
	
	/**
	 * Gets the shop's default stock.
	 * @return The shop's default stock.
	 */
	public Container getDefaultStock() {
		return defaultStock;
	}
	
	/**
	 * Gets the shop's name.
	 * @return The shop's name.
	 */
	public String getShopName() {
		return name;
	}
	
	/**
	 * Gets the shop's type.
	 * @return The shop's type.
	 */
	public ShopType getShopType() {
		return shopType;
	}
	
	/**
	 * Gets shop's currency.
	 * @return The shop's currency.
	 */
	public int getCurrency() {
		return currency;
	}
	
	/**
	 * Creates a new shop instance.
	 * @param name The shop's name.
	 * @param shopType The shop's type.
	 * @param currency The shop's currency.
	 */
	public Shop(String name, int mainStockId, ShopType shopType, int currency, Item[] mainItems, Item[] playerItems) {
		this.name = name;
		this.mainStockId = mainStockId;
		this.shopType = shopType;
		this.currency = currency;
		this.mainItems = mainItems;
		this.playerItems = playerItems;
	}

	public static enum ShopType {
		
		/**
		 * A general store, which takes all items, buys items for 0.4 of their price
		 * and sells them for 0.8 of their price. Default stock will go to 0, and 
		 * non-default will be removed if the stock is < 1.
		 */
		GENERAL_STORE,
		
		/**
		 * A specialist store that does buy items that are in its stock (none others though!).
		 * It will buy items for 0.6 of their price, and sell them for 1.0
		 */
		SPECIALIST_STORE_BUY,
		
		/**
		 * A specialist store that will not buy any stock. It will sell items for 1.0 of their
		 * price.
		 */
		SPECIALIST_STORE_NO_BUY;
	}
	
	public static void init() throws IOException {
		if(shops != null) {
			throw new IllegalStateException("Shops already loaded.");
		}
		logger.info("Loading Shops definitions...");
		File file = new File("data/shops.xml");
		shops = new ArrayList<Shop>();
		if(file.exists()) {
			shops = XMLController.readXML(file);
			logger.info("Loaded " + shops.size() + " shops.");		
		} else {
			logger.info("Shops not found.");
		}
//		Shop test = new Shop("474 Project is cool!", 860, ShopType.GENERAL_STORE, 995, new Item[] { new Item(4151, 5) }, new Item[] { new Item(4151, 5) });		
//		shops.add(test);
//		XMLController.writeXML(shops, file);
		for(Shop shop : shops) {
			Container defaultStock = new Container(Type.ALWAYS_STACK, SIZE);
			Container playerStock = new Container(Type.ALWAYS_STACK, SIZE);
			Container mainStock = new Container(Type.ALWAYS_STACK, SIZE);
			if(shop.getMainItems() != null) {
				for(Item item : shop.getMainItems()) {
					if(item != null) {
						mainStock.add(item);	
					}
				}
			}
			if(shop.getPlayerItems() != null) {
				for(Item item : shop.getPlayerItems()) {
					if(item != null) {
						playerStock.add(item);		
						defaultStock.add(item);		
					}
				}
			}
			shop.setDefaultStock(defaultStock);
			shop.setPlayerStock(playerStock);
			shop.setMainStock(mainStock);
		}
	}
	
	/**
	 * The shop size.
	 */
	public static final int SIZE = 40;
	
	/**
	 * The player inventory interface.
	 */
	public static final int PLAYER_INVENTORY_INTERFACE = 621;

	/**
	 * The shop inventory interface.
	 */
	public static final int SHOP_INVENTORY_INTERFACE = 620;
	
	/**
	 * The shop's main stock.
	 */
	public static final int SHOP_MAIN_STOCK = 23;
	
	/**
	 * The shop's player stock.
	 */
	public static final int SHOP_PLAYER_STOCK = 24;
		
	/**
	 * Opens the shop for the specified player.
	 * @param player The player to open the shop for.
	 */
	public static void open(Player player, int shopId, int stockType) {
		player.getActionSender().removeChatboxInterface();
		player.getActionSender().sendConfig(118, 17);
		player.getActionSender().sendInterface(SHOP_INVENTORY_INTERFACE, false);
		player.getActionSender().sendInterfaceInventory(PLAYER_INVENTORY_INTERFACE);
		player.getInterfaceState().setOpenShop(shopId);
		Shop shop = shops.get(player.getInterfaceState().getOpenShop());
		switch(stockType) {
		case 1: //Main stock
			if(shop.getMainStock().size() < 1) {
				stockType = 2;
			}
			break;
		case 2: //Player stock
			if(shop.getPlayerStock().size() < 1) {
				stockType = 1;
			}
			break;
		}
		player.getInterfaceState().setOpenStockType(stockType);

		if(shop.getMainStockId() != -1) {
			player.getActionSender().sendRunScript(25, new Object[] { shop.getMainStockId(), 93 }, Constants.MAIN_STOCK_TYPE_STRING);
		} else {
			player.getActionSender().sendRunScript(25, new Object[] { 860, 93 }, Constants.MAIN_STOCK_TYPE_STRING); //We'll just send 860 to stop the errors
		}
		player.getActionSender().sendRunScript(150, Constants.BUY_PARAMETERS, Constants.MAIN_STOCK_OPTIONS_STRING);

		player.getActionSender().sendRunScript(150, Constants.SELL_PARAMETERS, Constants.MAIN_STOCK_OPTIONS_STRING);
		
		player.getActionSender().sendAccessMask(Constants.SCRIPT_OPTIONS_EXAMINE, 0, PLAYER_INVENTORY_INTERFACE, 0, 28);
		
		player.getActionSender().sendString(SHOP_INVENTORY_INTERFACE, 22, shop.getShopName());
		
		player.getInterfaceState().removeListeners();
		switch (stockType) {
		case 1: // Main stock
			player.getActionSender().sendInterfaceConfig(620, 23, false) //Main stock contents
									.sendInterfaceConfig(620, 24, true) //Player stock contents
									.sendInterfaceConfig(620, 25, true) //Unknown
									.sendInterfaceConfig(620, 26, false) //The player stock tab highlighted
									.sendInterfaceConfig(620, 27, true) //The player stock tab highlighted
									.sendInterfaceConfig(620, 29, false) //The main stock tab unhighlighted
									.sendInterfaceConfig(620, 34, shop.getShopType() != ShopType.GENERAL_STORE)
									.sendAccessMask(Constants.SCRIPT_OPTIONS_EXAMINE, SHOP_MAIN_STOCK, SHOP_INVENTORY_INTERFACE, 0, 40);
			player.getInterfaceState().addListener(shop.getMainStock(), new InterfaceContainerListener(player, -1, 2, 91));
			break;
		case 2: // Player stock
			player.getActionSender().sendInterfaceConfig(620, 23, true) //Main stock contents
									.sendInterfaceConfig(620, 24, false) //Player stock contents
									.sendInterfaceConfig(620, 25, false) //Unknown
									.sendInterfaceConfig(620, 26, true) //The player stock tab highlighted
									.sendInterfaceConfig(620, 27, false) //The player stock tab highlighted
									.sendInterfaceConfig(620, 29, true) //The main stock tab unhighlighted
									.sendInterfaceConfig(620, 34, shop.getShopType() != ShopType.GENERAL_STORE)
									.sendAccessMask(Constants.SCRIPT_OPTIONS_EXAMINE, SHOP_PLAYER_STOCK, SHOP_INVENTORY_INTERFACE, 0, 40);
			player.getInterfaceState().addListener(shop.getPlayerStock(), new InterfaceContainerListener(player, -1, 2, 91));
			break;
		}
	}
	
	/**
	 * Sells an item.
	 * @param player The player.
	 * @param slot The slot in the player's inventory.
	 * @param id The item id.
	 * @param amount The amount of the item to sell.
	 */
	public static void sellItem(Player player, int slot, int id, int amount) {
		player.getActionSender().removeChatboxInterface();
		Shop shop = shops.get(player.getInterfaceState().getOpenShop());
		Item item = player.getInventory().get(slot);
		if(item == null) {
			return; // invalid packet, or client out of sync
		}
		if(item.getId() != id) {
			return; // invalid packet, or client out of sync
		}
		for(int i = 0; i < ItemDefinition.untradableItems.length; i++) {
			if(item.getId() == ItemDefinition.untradableItems[i]) {
				player.getActionSender().sendMessage("You cannot sell this item.");
				return;
			}
		}
		int transferAmount = player.getInventory().getCount(id);
		if(amount >= transferAmount) {
			amount = transferAmount;
		} else if(transferAmount == 0) {
			return; // invalid packet, or client out of sync
		}
		boolean canSell = false;
		if(shop.getShopType() == ShopType.SPECIALIST_STORE_BUY)  {
			if(shop.getMainStock().contains(item.getId()) || shop.getDefaultStock().contains(item.getId())) {
				canSell = true;
			}
		}
		if(shop.getShopType() == ShopType.GENERAL_STORE)  {
			canSell = true;
		}
		if(item.getId() == shop.getCurrency()) {
			canSell = false;
		}
		if(shop.getPlayerStock().size() < 1) {
			canSell = false;
		}
		if(canSell) {
			Shop.open(player, player.getInterfaceState().getOpenShop(), 2); //Forces open the player stock
			int shopSlot = shop.getPlayerStock().contains(item.getId()) ? shop.getPlayerStock().getSlotById(item.getId()) : shop.getPlayerStock().freeSlot();	
			if(shopSlot == -1) {
				player.getActionSender().sendMessage("This shop is currently full.");
			} else {
				if(shop.getPlayerStock().get(shopSlot) != null) {
					if(shop.getPlayerStock().get(shopSlot).getCount() + amount < 1 || shop.getPlayerStock().get(shopSlot).getCount() + amount > Constants.MAX_ITEMS) {
						player.getActionSender().sendMessage("This shop is currently full.");
						return;
					}
				}
				long totalAmount = amount;
				long totalValue = shop.getSellValue(player, item);
				long totalPrice = (totalAmount * totalValue);
				if(totalPrice > Integer.MAX_VALUE) {
					amount = (Integer.MAX_VALUE / shop.getSellValue(player, item)) - 1;
				}
				Item reward = new Item(shop.getCurrency(), amount * shop.getSellValue(player, item));
				/*
				 * We make a temporary inventory container, why? This is because if we have 1 AGS, and 27 whips, we would have no free slot for the coins.
				 * Now on most servers, if you tried to sell the AGS, it would say "not enough inventory space", which is true, but wrong. On RS, it removes
				 * the AGS first, THEN adds the coins. However, we also want to keep our checks for inventory space in there, so we do it on a temporary
				 * inventory first.
				 */
				Container temporaryInventory = new Container(Type.STANDARD, Inventory.SIZE);
				for(Item invItem : player.getInventory().toArray()) {
					temporaryInventory.add(invItem);
				}
				temporaryInventory.remove(new Item(item.getId(), amount));
				if(!temporaryInventory.add(reward)) {
					return; //We wouldn't have enough inventory space, even after removing the sold item.
				}				
				player.getInventory().remove(new Item(item.getId(), amount));
				shop.getPlayerStock().add(new Item(item.getId(), amount));
				player.getInventory().add(reward);
			}
		} else {
			player.getActionSender().sendMessage("This shop will not buy that item.");
		}
	}

	/**
	 * Buys an item.
	 * @param player The player.
	 * @param slot The slot in the player's inventory.
	 * @param id The item id.
	 * @param amount The amount of the item to buy.
	 */
	public static void buyItem(Player player, int slot, int id, int amount) {
		player.getActionSender().removeChatboxInterface();
		Shop shop = shops.get(player.getInterfaceState().getOpenShop());
		Item item = player.getInterfaceState().getOpenStockType() == 1 ? shop.getMainStock().get(slot) : shop.getPlayerStock().get(slot);
		if(item == null || item.getId() != id) {
			return; // invalid packet, or client out of sync
		}
		int transferAmount = player.getInventory().freeSlots();
		if(amount >= transferAmount && !ItemDefinition.forId(item.getId()).isStackable()) {
			amount = transferAmount;
		} else if(transferAmount == 0) {
			return; // invalid packet, or client out of sync
		}
		if(transferAmount > 200) {
			transferAmount = 200;
			player.getActionSender().sendMessage("You cannot buy more than 200 items at a time.");
		}
		switch(player.getInterfaceState().getOpenStockType()) {
		case 1: //Main stock
			if(shop.getMainStock().get(slot).getCount() > 0) {
				if(amount >= shop.getMainStock().get(slot).getCount()) {
					amount = shop.getMainStock().get(slot).getCount();
				}
				if(!shop.hasCurrency(player, item, amount)) {
					player.getActionSender().sendMessage("You don't have enough " + ItemDefinition.forId(shop.getCurrency()).getName().toLowerCase() + ".");
				} else {
					Item reward = new Item(item.getId(), amount);
					Container temporaryInventory = new Container(Type.STANDARD, Inventory.SIZE);
					for(Item invItem : player.getInventory().toArray()) {
						temporaryInventory.add(invItem);
					}
					temporaryInventory.remove(new Item(shop.getCurrency(), amount * shop.getCostValue(player, new Item(item.getId(), amount))));
					if(!temporaryInventory.add(reward)) {
						return; //We wouldn't have enough inventory space, even after removing the currency cost.
					}					
					player.getInventory().remove(new Item(shop.getCurrency(), amount * shop.getCostValue(player, new Item(item.getId(), amount))));
					player.getInventory().add(reward);
					//We don't remove from an infinity stock.
				}
			}
			break;
		case 2: //Player stock
			if(shop.getPlayerStock().get(slot).getCount() > 0) {
				if(amount >= shop.getPlayerStock().get(slot).getCount()) {
					amount = shop.getPlayerStock().get(slot).getCount();
				}
				if(!shop.hasCurrency(player, item, amount)) {
					player.getActionSender().sendMessage("You don't have enough " + ItemDefinition.forId(shop.getCurrency()).getName().toLowerCase() + ".");
				} else {
					long totalAmount = amount;
					long totalValue = shop.getCostValue(player, new Item(item.getId(), amount));
					long totalPrice = (totalAmount * totalValue);
					if(totalPrice > Integer.MAX_VALUE || totalPrice > player.getInventory().getCount(shop.getCurrency())) {
						amount = (player.getInventory().getCount(shop.getCurrency()) / shop.getSellValue(player, new Item(item.getId(), amount))) - 1;
					}
					Item reward = new Item(item.getId(), amount);
					Container temporaryInventory = new Container(Type.STANDARD, Inventory.SIZE);
					for(Item invItem : player.getInventory().toArray()) {
						temporaryInventory.add(invItem);
					}
					temporaryInventory.remove(new Item(shop.getCurrency(), amount * shop.getCostValue(player, new Item(item.getId(), amount))));
					if(!temporaryInventory.add(reward)) {
						return; //We wouldn't have enough inventory space, even after removing the currency cost.						
					}
					player.getInventory().remove(new Item(shop.getCurrency(), amount * shop.getCostValue(player, new Item(item.getId(), amount))));
					player.getInventory().add(reward);
					if(shop.getDefaultStock().contains(item.getId())) { //If the player stock started with this item, it goes to 0, rather than removing completely.
						shop.getPlayerStock().removeOrZero(new Item(item.getId(), amount));
					} else {
						shop.getPlayerStock().remove(new Item(item.getId(), amount));
					}
				}
			}
			break;
		}
	}
	
	public boolean hasCurrency(Player player, Item item, int amt) {
		Shop shop = shops.get(player.getInterfaceState().getOpenShop());
		int finalAmt = getCostValue(player, item);
		if(finalAmt == -1) {
			player.getActionSender().sendMessage("Currency Error.");
			return false;
		}
		finalAmt *= amt;
		return player.getInventory().getCount(shop.getCurrency()) >= finalAmt;
	}
	
	public int getCostValue(Player player, Item item) {
		switch(player.getInterfaceState().getOpenStockType()) {
		case 1: //Main stock
			switch(getShopType()) {
			case GENERAL_STORE:
				return (int) (ItemDefinition.forId(item.getId()).getValue() * 0.8 < 1 ? 1 : ItemDefinition.forId(item.getId()).getValue() * 0.8);
			case SPECIALIST_STORE_BUY:
			case SPECIALIST_STORE_NO_BUY:
				return (int) ItemDefinition.forId(item.getId()).getValue();
			}
			break;
		case 2: //Player stock
			switch(getShopType()) {
			case GENERAL_STORE:
				return (int) (ItemDefinition.forId(item.getId()).getValue() * 0.6 < 1 ? 1 : ItemDefinition.forId(item.getId()).getValue() * 0.6);
			case SPECIALIST_STORE_BUY:
			case SPECIALIST_STORE_NO_BUY:
				return (int) (ItemDefinition.forId(item.getId()).getValue() * 0.8 < 1 ? 1 : ItemDefinition.forId(item.getId()).getValue() * 0.8);
			}
			break;
		}
		return 1;
	}

	public int getSellValue(Player player, Item item) {
		switch(getShopType()) {
		case GENERAL_STORE:
			return (int) (ItemDefinition.forId(item.getId()).getValue() * 0.4 < 1 ? 1 : ItemDefinition.forId(item.getId()).getValue() * 0.4);
		case SPECIALIST_STORE_BUY:
		case SPECIALIST_STORE_NO_BUY:
			return (int) (ItemDefinition.forId(item.getId()).getValue() * 0.6 < 1 ? 1 : ItemDefinition.forId(item.getId()).getValue() * 0.6);
		}
		return 1;
	}
	

	public static void costItem(Player player, int slot, int id) {
		Shop shop = shops.get(player.getInterfaceState().getOpenShop());
		Item item = player.getInterfaceState().getOpenStockType() == 1 ? shop.getMainStock().get(slot) : shop.getPlayerStock().get(slot);
		if(item == null || item.getId() != id) {
			return; // invalid packet, or client out of sync
		}
		player.getActionSender().sendMessage(ItemDefinition.forId(item.getId()).getName() + ": currently costs " + shop.getCostValue(player, item) + " " + ItemDefinition.forId(shop.getCurrency()).getName().toLowerCase() + ".");
	}

	public static void valueItem(Player player, int slot, int id) {
		player.getActionSender().removeChatboxInterface();
		Shop shop = shops.get(player.getInterfaceState().getOpenShop());
		Item item = player.getInventory().get(slot);
		if (item == null) {
			return; // invalid packet, or client out of sync
		}
		if (item.getId() != id) {
			return; // invalid packet, or client out of sync
		}
		boolean message = false;
		if (shop.getShopType() == ShopType.GENERAL_STORE) {
			message = true;
		}
		if (shop.getShopType() == ShopType.SPECIALIST_STORE_BUY) {
			if (shop.getMainStock().contains(item.getId()) || shop.getDefaultStock().contains(item.getId())) {
				message = true;
			}
		}
		int finalValue = shop.getSellValue(player, item);
		String shopAdd = "";
		if (finalValue >= 1000 && finalValue < 1000000) {
			shopAdd = "(" + (finalValue / 1000) + "K).";
		} else if (finalValue >= 1000000) {
			shopAdd = "(" + (finalValue / 1000000) + " million).";
		}
		player.getActionSender().sendMessage(message ? ItemDefinition.forId(item.getId()).getName() + ": shop will buy for " + finalValue + " " + ItemDefinition.forId(shop.getCurrency()).getName().toLowerCase() + " " + shopAdd: "This shop will not buy that item.");
	}
	
}