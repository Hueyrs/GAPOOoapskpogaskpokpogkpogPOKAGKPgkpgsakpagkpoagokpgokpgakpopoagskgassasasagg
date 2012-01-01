package org.rs2server.rs2.model;

/**
 * 
 * @author Joe.melsha@live.com (Killer 99)
 *
 */
public class NPCDrop {
	
	/**
	 * The chance out of 1.0.
	 */
	private double frequency;

	/**
	 * The item dropped.
	 */
	private Item item;

	public NPCDrop(double frequency, Item item) {
		this.frequency = frequency;
		this.item = item;
	}

	/**
	 * @return the frequency
	 */
	public double getFrequency() {
		return frequency;
	}
	
	/**
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}

}
