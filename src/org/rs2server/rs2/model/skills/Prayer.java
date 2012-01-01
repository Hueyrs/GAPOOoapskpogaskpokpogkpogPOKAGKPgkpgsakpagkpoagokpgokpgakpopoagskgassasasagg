package org.rs2server.rs2.model.skills;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;

/**
 * For prayer related activities.
 * @author Canownueasy
 */
@SuppressWarnings("unused")
public class Prayer {
	
	public void prayAltar(Location loc) {
		if(player.getSkills().getPrayerPoints() >= player.getSkills().getLevelForExperience(Skills.PRAYER)) {
			player.getActionSender().sendMessage("You already have full prayer points.");
			return;
		}
		player.getSkills().setLevel(Skills.PRAYER, player.getSkills().getLevelForExperience(Skills.PRAYER));
		player.getSkills().setPrayerPoints(player.getSkills().getLevelForExperience(Skills.PRAYER));
		if(player.getActionSender() != null) {
			player.getActionSender().sendSkills();
		}
		player.playAnimation(Animation.create(645));
		player.getSkills().setLevel(Skills.PRAYER, player.getSkills().getLevelForExperience(Skills.PRAYER));
		player.getActionSender().sendMessage("You pray at the altar...");
	}
	
	public void buryBone(Item bone, int slot) {
		if(!player.canOption) {
			return;
		}
		player.canOption = false;
		player.playAnimation(Animation.create(7270));//827 = old
		player.getInventory().remove(bone, slot);
		player.getActionSender().sendMessage("You bury the bones...");
		player.getSkills().addExperience(Skills.PRAYER, getBoneXP(bone));
		World.getWorld().submit(new Event(600) {
			public void execute() {
				player.canOption = true;
				stop();
			}
		});
	}
	
	public void altarBone(Item bone) {
		if(!player.canOption) {
			return;
		}
		player.canOption = false;
		player.playAnimation(Animation.create(883, 10));
		player.getInventory().remove(bone);
		player.getActionSender().sendMessage("You use the bones on the altar.");
		player.getSkills().addExperience(Skills.PRAYER, getAltarXP(bone));
		World.getWorld().submit(new Event(1000) {
			public void execute() {
				player.canOption = true;
				stop();
			}
		});
	}
	
	private enum Bone {
		
		NORMAL_BONES(526, 4),
		BURNT_BONES(528, 4),
		BAT_BONES(530, 5),
		BIG_BONES(532, 15),
		BABYDRAGON_BONES(534, 30),
		DRAGON_BONES(536, 72),
		WOLF_BONES(2859, 4),
		JOGRE_BONES(3125, 15),
		DAGANNOTH_BONES(6729, 125),
		WYVERN_BONES(6812, 50),
		SHAIKAHAN_BONES(3123, 25),
		OURG_BONES(4834, 140);
		
		private int id;
		private int xp;
		
		public int getId() {
			return id;
		}
		public int getXp() {
			return xp * Constants.EXP_MODIFIER;
		}
		
		private Bone(int id, int xp) {
			this.id = id;
			this.xp = xp;
		}
	}
	
	private int getBoneXP(Item bone) {
		int xp = 1;
		switch(bone.getId()) {
		case 526:
		case 528:
		case 2530:
		case 2859:
			xp = 4;
		case 530:
			xp = 5;
		case 532:
		case 3125:
			xp = 15;
		case 3123:
			xp = 25;
		case 534:
			xp = 30;
		case 6812:
			xp = 50;
		case 536:
			xp = 72;
		case 6729:
			xp = 125;
		case 4834:
			xp = 140;
		}
		return xp * Constants.EXP_MODIFIER;
	}
	
	private int getAltarXP(Item bone) {
		return getBoneXP(bone) * 2;
	}

	private Player player;
	
	/**
	 * Constructs a new prayer skill.
	 * @param player For this player.
	 */
	public Prayer(Player player) {
		this.player = player;
	}
}
