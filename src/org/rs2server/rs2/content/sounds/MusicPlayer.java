package org.rs2server.rs2.content.sounds;

import org.rs2server.rs2.model.Player;

public class MusicPlayer {
	
	public static void playFor(Player p) {
		MusicParser regionData = MusicParser.regionData[p.getLocation().getX() / 32][p
				.getLocation().getY() / 32];
		if (regionData != null)
			if (regionData.getMusicId() != -1) {
				//p.getActionSender().sendMusic(regionData.getMusicId());
				//p.getActionSender().sendMessage("MUSIC ID "+ regionData.getMusicId());
			}
	}
	
}