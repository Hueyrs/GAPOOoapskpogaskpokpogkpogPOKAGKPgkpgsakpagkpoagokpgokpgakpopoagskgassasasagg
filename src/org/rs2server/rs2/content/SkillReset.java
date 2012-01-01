package org.rs2server.rs2.content;

import org.rs2server.rs2.model.Player;

/**
 * For skill resetting.
 * @author Canownueasy
 *
 */
public class SkillReset {
	
	private Player player;
	
	public SkillReset(Player player) {
		this.player = player;
	}
	
	public void resetSkill(int skill, String skillName) {
		if(!(player.getInventory().getCount(995) >= resetCost(skill))) {
			player.getInterfaceState().setNextDialogueId(0, 97);
			return;
		}
		player.getSkills().detractLevel(skill, player.getSkills().getLevel(skill));
	}
	
	public int resetCost(int skill) {
		return (player.getSkills().getLevelForExperience(skill) - 1) * 300000;
	}

}
