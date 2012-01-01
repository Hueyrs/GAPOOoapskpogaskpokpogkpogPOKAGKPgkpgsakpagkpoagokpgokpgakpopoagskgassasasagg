package org.rs2server.rs2.model.skills;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.tickable.Tickable;

public class Runecrafting {

	private static final int[] RUNES = new int[] { 556, 558, 555, 557, 554, 559, 564, 563, 561,
		562, 560 };

	private static final int[][] MULTIPLIERS = new int[][] {
		new int[] { 11, 22, 33, 44, 55, 66, 77, 88, 99 },
		new int[] { 14, 28, 42, 56, 70, 84, 98 }, new int[] { 19, 38, 57, 76, 95 },
		new int[] { 26, 52, 78 }, new int[] { 35, 70 }, new int[] { 46, 92 }, new int[] { 59 },
		new int[] { 200 }, new int[] { 91 }, new int[] { 74 }, new int[] { 200 }, };

	private static final int[] REQUIRED_LEVELS = new int[] { 1, 2, 5, 9, 14, 20, 27, 54, 44, 35, 65 };

	public static boolean isRunecraftingAltar(Player player, int object) {
		if (object < 2478 || object > 2488) {
			return false;
		}
		int index = Math.abs(2478 - object);
		craftRune(player, index);
		return true;
	}

	public static void craftRune(final Player player, final int index) {
		boolean needsPure = REQUIRED_LEVELS[index] > 20;
		if (needsPure && !player.getInventory().contains(7936)) {
			player.getActionSender().sendMessage("You need pure essence to craft this rune.");
			return;
		}
		if (!needsPure
				&& (!player.getInventory().contains(7936) && !player.getInventory().contains(1436))) {
			player.getActionSender().sendMessage("You need essence to craft runes.");
			return;
		}
		final int essence = needsPure ? 7936 : player.getInventory().contains(7936) ? 7936 : 1436;
		if (player.getSkills().getLevel(Skills.RUNECRAFTING) < REQUIRED_LEVELS[index]) {
			player.getActionSender().sendMessage(
					"You need a runecrafting level of " + REQUIRED_LEVELS[index]
							+ " to craft this rune.");
			return;
		}
		player.playAnimation(Animation.create(791));
		player.playGraphics(Graphic.create(186, 0, 100));
		final int amount = player.getInventory().getCount(essence);
		World.getWorld().submit(new Tickable(1) {

			@Override
			public void execute() {
				player.getInventory().remove(new Item(essence, amount));
				player.getInventory().add(
						new Item(RUNES[index], amount * getMultiplier(player, index)));
				player.getActionSender().sendMessage("derp");
				double experience = (5 + (.5 * index)) * amount;
				player.getSkills().addExperience(Skills.RUNECRAFTING, experience);
				stop();
			}
		});
	}

	private static int getMultiplier(Player player, int index) {
		int multiplier = 1;
		for (int i = 0; i < MULTIPLIERS[index].length; i++) {
			if (player.getSkills().getLevel(Skills.RUNECRAFTING) >= MULTIPLIERS[index][i]) {
				multiplier++;
			} else {
				break;
			}
		}
		return multiplier;
	}

	public static void fillPouch(Player player, int id) {
		Pouch pouch = Pouch.forId(id);
		int essCount = player.getInventory().getCount(7936);
		if (essCount < 1) {
			return;
		}
		int essInPouch = (Integer) (player.getAttribute(pouch.getKey()) == null ? 0 : player
				.getAttribute(pouch.getKey()));
		int space = pouch.getCapacity() - essInPouch;
		if (space < 1) {
			player.getActionSender().sendMessage("This pouch is full.");
		}
		int amountToFill = essCount < space ? essCount : space;
		player.getInventory().remove(new Item(7936, amountToFill));
		essInPouch += amountToFill;
		player.setAttribute(pouch.getKey(), essInPouch);
	}

	public static void emptyPouch(Player player, int id) {
		Pouch pouch = Pouch.forId(id);
		int essInPouch = (Integer) (player.getAttribute(pouch.getKey()) == null ? 0 : player
				.getAttribute(pouch.getKey()));
		if (essInPouch < 1) {
			player.getActionSender().sendMessage("This pouch is empty.");
			return;
		}
		int invoSpace = player.getInventory().freeSlots();
		if (invoSpace < 1) {
			player.getActionSender().sendMessage("Your inventory is too full");
			return;
		}
		int amountToEmpty = invoSpace >= essInPouch ? essInPouch : invoSpace;
		player.getInventory().add(new Item(7936, amountToEmpty));
		essInPouch -= amountToEmpty;
		player.setAttribute(pouch.getKey(), essInPouch);
	}

	public static boolean checkPouch(Player player, int id) {
		Pouch pouch = Pouch.forId(id);
		int amount = (Integer) (player.getAttribute(pouch.getKey()) == null ? 0 : player
				.getAttribute(pouch.getKey()));
		String message = "This pouch contains " + amount + " pure essence.";
		if (amount < 1) {
			message = "This pouch is empty.";
		}
		player.getActionSender().sendMessage(message);
		return false;
	}

	public static enum Pouch {
		// random key? could've used anything
		SMALL(5509, 3, "15975325l845669"), MEDIUM(5510, 6, "3571592l5845669"), BIG(5512, 9,
				"75395145l685269"), LARGE(5514, 12, "95175l385265469");

		private final int id;
		private final int capacity;
		private final String key;

		private Pouch(int id, int capacity, String key) {
			this.id = id;
			this.capacity = capacity;
			this.key = key;
		}

		public static Pouch forId(int id) {
			switch (id) {
			case 5509:
				return SMALL;
			case 5510:
				return MEDIUM;
			case 5512:
				return BIG;
			case 5514:
				return LARGE;
			}
			return null;
		}

		public int getId() {
			return id;
		}

		public int getCapacity() {
			return capacity;
		}

		public String getKey() {
			return key;
		}

	}

}