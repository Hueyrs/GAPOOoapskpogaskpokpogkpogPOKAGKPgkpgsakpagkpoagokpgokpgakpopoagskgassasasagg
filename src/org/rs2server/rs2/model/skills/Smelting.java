package org.rs2server.rs2.model.skills;

import java.util.Random;

import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;

public class Smelting extends Action {

	private Player player;
	private Bar bar;
	private int productionAmount;

	private int fourTickDelay = 1;
	private int animationTick;
	private static final Animation ANIMATION = Animation.create(899);
	private Random random = new Random();

	public Smelting(Player player, Bar bar, int productionAmount) {
		super(player, 600);
		this.player = player;
		this.bar = bar;
		this.productionAmount = productionAmount;
	}

	public void begin() {
		player.getActionSender().removeChatboxInterface();
		String message = "";
		if(bar.getPrimaryOre() != null && !player.getInventory().contains(bar.getPrimaryOre()))
			message = "You do not have enough " + bar.getPrimaryOre().getDefinition().getName().toLowerCase() 
			+ " to smelt this bar.";
		else if(bar.getSecondaryOre() != null && !player.getInventory().contains(bar.getSecondaryOre()))
			message = "You do not have enough " + bar.getSecondaryOre().getDefinition().getName().toLowerCase()
			+ " to smelt this bar.";
		if(message != "") {
			player.getActionSender().sendMessage(message);
			return;
		}
		if(player.getSkills().getLevel(Skills.SMITHING) < bar.getRequiredLevel()) {
			player.getActionSender().sendMessage("Your smithing level is too low to make this bar.");
			return;
		}
		player.playAnimation(ANIMATION);
		player.getActionQueue().addAction(this);
	}

	@Override
	public void execute() {
		if(!player.getInventory().contains(bar.getPrimaryOre()) || (bar.getSecondaryOre() != null 
				&& !player.getInventory().contains(bar.getSecondaryOre())) || productionAmount < 1) {
			this.stop();
			Animation.create(-1);
			return;
		}
		fourTickDelay++;
		animationTick++;
		if(animationTick % 5 == 0) {
			player.playAnimation(ANIMATION);
			fourTickDelay = 1;
			if(bar == Bar.GOLD)
				player.getActionSender().sendMessage("You place a lump of gold in the furnace.");
		}
		if(fourTickDelay % 5 != 0) {
			return;

		}
		if(bar == Bar.IRON && random.nextBoolean() && !player.getEquipment().contains(2568)) {
			player.getInventory().remove(bar.primary);
			player.getActionSender().sendMessage("The ore is too impure and you fail to refine it.");
		} else {
			player.getInventory().remove(bar.getPrimaryOre());
			player.getInventory().remove(bar.getSecondaryOre());
			String message = bar.toString().toLowerCase();
			message = bar == Bar.GOLD ?  "You retrieve a lump of gold from the furnace" 
					: "You smelt the " + message + " in the furnace.";
			player.getInventory().add(bar.getProduct());
			player.getActionSender().sendMessage(message);
			player.getSkills().addExperience(Skills.SMITHING, bar.getExperience());
		}

		productionAmount--;	
	}
	

	@Override
	public AnimationPolicy getAnimationPolicy() {
		return AnimationPolicy.RESET_ALL;
	}

	@Override
	public CancelPolicy getCancelPolicy() {
		return CancelPolicy.ALWAYS;
	}

	@Override
	public StackPolicy getStackPolicy() {
		return StackPolicy.ALWAYS;
	}

	public static void handleInterfaceButtons(Player player, int button) {
		int amount = 0;
		Bar bar = null;
		switch(button) {
		case 16: case 15: case 14:
			amount = amounts[16 - button];
			bar = Bar.BRONZE;
			break;
		case 20: case 19: case 18:
			amount = amounts[20 - button];
			bar = Bar.BLURITE;
			break;
		case 24: case 23: case 22:
			amount = amounts[24-button];
			bar = Bar.IRON;
			break;
		case 28: case 27: case 26:
			amount = amounts[28-button];
			bar = Bar.SILVER;
			break;
		case 32: case 31: case 30:
			amount = amounts[32-button];
			bar = Bar.STEEL;
			break;
		case 36: case 35: case 34:
			amount = amounts[36-button];
			bar = Bar.GOLD;
			break;
		case 40: case 39: case 38:
			amount = amounts[40-button];
			bar = Bar.MITHRIL;
			break;
		case 44: case 43: case 42:
			amount = amounts[44-button];
			bar = Bar.ADAMANTITE;
			break;
		case 48: case 47: case 46:
			amount = amounts[48-button];
			bar = Bar.RUNITE;
			break;
		case 13:
			bar = Bar.BRONZE;
			break;
		case 17:
			bar = Bar.BLURITE;
			break;
		case 21:
			bar = Bar.IRON;
			break;
		case 25:
			bar = Bar.SILVER;
			break;
		case 29:
			bar = Bar.STEEL;
			break;
		case 33:
			bar = Bar.GOLD;
			break;
		case 37:
			bar = Bar.MITHRIL;
			break;
		case 41:
			bar = Bar.ADAMANTITE;
			break;
		case 45:
			bar = Bar.RUNITE;
			break;
		}
		if(bar != null && amount != 0) {
			new Smelting(player, bar, amount).begin();
		} else {
			player.getAttributes().put("smelting_bar", bar);
			//player.getInterfaceState().openEnterAmountInterface(INTERFACE);
			//player.getInterfaceState().openEnterAmountInterface(interfaceId, slot, id);
		}
	}

	private static final int[] amounts = new int[] {1, 5, 10};

	public static void openInterface(final Player player) {
		for(int i = 0; i < 9; i++) {
			int itemId = i == 1 ? 9467 : 2349 + (i*2);
			String color = player.getSkills().getLevel(Skills.SMITHING) < REQUIRED_LEVELS[i] ? "<col=#FF0000>" : "";
			String move = "<br><br><br><br>";
			player.getActionSender().sendString(INTERFACE, 16 + (i * 4), move + color + ITEM_NAMES[i]).
			sendInterfaceModel(INTERFACE, i+4, 150, itemId);
		}
		World.getWorld().submit(new Event(600) {

			@Override
			public void execute() {
				player.getActionSender().sendChatboxInterface(INTERFACE);
				this.stop();
			}

		});
	}

	public static final int INTERFACE = 311;

	public static final int[] REQUIRED_LEVELS = new int[] {
		1, 99, 15, 20, 30, 40, 50, 70, 85
	};

	public static final String[] ITEM_NAMES = new String[] {
		"Bronze", "Blurite", "Iron", "Silver", "Steel", "Gold", "Mithril", "Adamant", "Runite"
	};

	public static enum Bar {
		BRONZE(new Item(436), new Item(438), 1, 6.25, new Item(2349)),
		BLURITE(new Item(668), null, 8, 8, new Item(9467)),
		IRON(new Item(440), null, 15, 12.5, new Item(2351)),
		SILVER(new Item(442), null, 20, 13.7, new Item(2355)),
		STEEL(new Item(440), new Item(453, 2), 30, 17.5, new Item(2353)),
		GOLD(new Item(444), null, 40, 22.5, new Item(2357)),
		MITHRIL(new Item(447), new Item(453, 4), 50, 30, new Item(2359)),
		ADAMANTITE(new Item(449), new Item(453, 6), 70, 37.5, new Item(2361)),
		RUNITE(new Item(451), new Item(453, 8), 85, 50, new Item(2363));

		private Item primary;
		private Item secondary;
		private int requiredLevel;
		private double experience;
		private Item product;

		private Bar() {}

		private Bar(Item primary, Item seconday, int requiredLevel, double experience, Item product) {
			this.primary = primary;
			this.secondary = seconday;
			this.requiredLevel = requiredLevel;
			this.experience = experience;
			this.product = product;
		}

		public Item getPrimaryOre() {
			return primary;
		}

		public Item getSecondaryOre() {
			return secondary;
		}

		public int getRequiredLevel() {
			return requiredLevel;
		}

		public double getExperience() {
			return experience;
		}

		public Item getProduct() {
			return product;
		}

	}

}