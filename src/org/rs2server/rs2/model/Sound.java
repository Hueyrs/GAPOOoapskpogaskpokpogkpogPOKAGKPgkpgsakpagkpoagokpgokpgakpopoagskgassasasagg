package org.rs2server.rs2.model;

/**
 * Represents an in-game sound.
 * @author Michael
 *
 */
public class Sound {

	/**
	 * The id of this sound.
	 */
	private int id;
	
	/**
	 * The volume this sound is played at.
	 */
	private byte volume = 1;
	
	/**
	 * The delay before this sound is played.
	 */
	private int delay = 0;
	
	public Sound(int id, byte volume, int delay) {
		this.id = id;
		this.volume = volume;
		this.delay = delay;
	}
	
	public static Sound create(int id, byte volume, int delay) {
		return new Sound(id, volume, delay);
	}

	public int getId() {
		return id;
	}

	public byte getVolume() {
		return volume;
	}

	public int getDelay() {
		return delay;
	}
}
