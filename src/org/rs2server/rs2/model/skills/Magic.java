package org.rs2server.rs2.model.skills;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.tickable.Tickable;

public class Magic {

	private static final Animation LOW_ALCH_ANIM = Animation.create(712);
	private static final Graphic LOW_ALCH_GFX = Graphic.create(112, 0, 75);

	private static final Animation HIGH_ALCH_ANIM = Animation.create(713);
	private static final Graphic HIGH_ALCH_GFX = Graphic.create(113, 0, 75);

	public static void lowAlch(final Player player, int slot) {
		if(player.getAttribute("busy") != null && (Boolean)player.getAttribute("busy"))
			return;
		player.getActionSender().sendRunScript(115, new Object[]{6}, "i");
		final Item item = new Item(player.getInventory().get(slot).getId());
		final Item[] runes = new Item[] {new Item(561), new Item(554, 3)};
		for(Item rune: runes)
			if(!player.getInventory().contains(rune) && !hasStaff(player, runes[1].getId())) {
				String name = rune.getDefinition().getName().split(" ")[0];
				player.getActionSender().sendMessage("You do not have enough " + name + " runes to cast this spell.");
				return;
			}
		if(player.getSkills().getLevel(Skills.MAGIC) < 21) {
			player.getActionSender().sendMessage("You need a magic level of 21 to cast this spell");
			return;
		}
		if(item == null || item.getId() == 995) {
			player.getActionSender().sendMessage("You can't alch that item.");
			return;
		}
		if(!player.getInventory().hasRoomFor(new Item(995))) {
			player.getActionSender().sendMessage("You do not have enough room in your inventory.");
			return;
		}
		player.setAttribute("busy", true);
		player.playAnimation(LOW_ALCH_ANIM);
		player.playGraphics(LOW_ALCH_GFX);
		World.getWorld().submit(new Tickable(2) {
			@Override
			public void execute() {
				player.setAttribute("busy", false);
				for(Item rune : runes)
					if(!hasStaff(player, rune.getId()))
						player.getInventory().remove(rune);
				player.getInventory().remove(item);
				player.getInventory().add(new Item(995, item.getDefinition().getValue() / 2)); // correct value?
				player.getSkills().addExperience(Skills.MAGIC, 31);
				this.stop();
			}
		});
	}

	public static void highAlch(final Player player, int slot) {
		if(player.getAttribute("busy") != null && (Boolean) player.getAttribute("busy"))
			return;
		player.getActionSender().sendRunScript(115, new Object[]{6}, "i");
		final Item item = new Item(player.getInventory().get(slot).getId());
		final Item[] runes = new Item[] {new Item(561), new Item(554, 5)};
		for(Item rune: runes)
			if(!player.getInventory().contains(rune) && !hasStaff(player, runes[1].getId())) {
				String name = rune.getDefinition().getName().split(" ")[0];
				player.getActionSender().sendMessage("You do not have enough " + name + " runes to cast this spell.");
				return;
			}
		if(player.getSkills().getLevel(Skills.MAGIC) < 55) {
			player.getActionSender().sendMessage("You need a magic level of 55 to cast this spell");
			return;
		}
		if(item == null || item.getId() == 995) {
			player.getActionSender().sendMessage("You can't alch that item.");
			return;
		}
		if(!player.getInventory().hasRoomFor(new Item(995))) {
			player.getActionSender().sendMessage("You do not have enough room in your inventory.");
			return;
		}
		player.setAttribute("busy", true);
		player.playAnimation(HIGH_ALCH_ANIM);
		player.playGraphics(HIGH_ALCH_GFX);
		World.getWorld().submit(new Tickable(3) {
			@Override
			public void execute() {
				player.setAttribute("busy", false);
				for(Item rune : runes)
					if(!hasStaff(player, rune.getId()))
						player.getInventory().remove(rune);
				player.getInventory().remove(item);
				player.getInventory().add(new Item(995, item.getDefinition().getValue()));
				player.getSkills().addExperience(Skills.MAGIC, 65);
				this.stop();
			}
		});
	}

	private static final int[][] STAVES = new int[][] {
		{1393, 1401, 1387, 3053, 3054, 11736, 11738}, //fire
		{1383, 1395, 1403, 11736, 11738, 6562, 6563}, //water
		{1381, 1397, 1405}, //air
		{1385, 1399, 1407, 6562, 6563} //earth
	};

	public static boolean hasStaff(Player player, int rune) {
		int index = rune - 554;
		if(index < 0 || index > 3)
			return false;
		for(int staff : STAVES[index])
			if(player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == staff)
				return true;
		return false;
	}

}
