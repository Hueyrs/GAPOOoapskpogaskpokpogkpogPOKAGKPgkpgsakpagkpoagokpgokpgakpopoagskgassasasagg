package org.rs2server.rs2.content.sounds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MusicParser {
	
	public static MusicParser[][] regionData = new MusicParser[1024][1024];
	private boolean isMulti;
	public static final File file = new File("./data/regionmusic.dat");
	private int musicId;

	public static void loadRegions() throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		int rX = 0, rY = 0;
		while ((line = reader.readLine()) != null) {
			if (line.contains("</"))
				line = line.substring(0, line.indexOf("</"));
			line = line.replaceAll("<", "");
			line = line.replaceAll(">", ":");
			line = line.replaceAll(",", ":");
			line = line.replaceAll("	", "");
			line = line.replaceAll(" ", "");
			String[] data = line.split(":");
			if (data[0].equalsIgnoreCase("position")) {
				rX = Integer.parseInt(data[1]);
				rY = Integer.parseInt(data[2]);
			} else if (data[0].equalsIgnoreCase("music")) {
				if (regionData[rX][rY] == null) {
					regionData[rX][rY] = new MusicParser(-1, false);
				}
				//System.out.println(rX * 32 + " " + rY * 32);
				int musicId = Integer.parseInt(data[1]);
				regionData[rX][rY].setMusicId(musicId);
			} else if (data[0].equalsIgnoreCase("Multiway combat")) {
				if (regionData[rX][rY] == null) {
					regionData[rX][rY] = new MusicParser(-1, false);
				}
				regionData[rX][rY].isMulti = Boolean.parseBoolean(data[1]);
			}
		}
	}

	public MusicParser(int musicId, boolean isMulti) {
		this.musicId = musicId;
		this.isMulti = isMulti;
	}

	public void setMulti(boolean isMulti) {
		this.isMulti = isMulti;
	}

	public void setMusicId(int musicId) {
		this.musicId = musicId;
	}

	public boolean isMulti() {
		return isMulti;
	}

	public int getMusicId() {
		return musicId;
	}

}