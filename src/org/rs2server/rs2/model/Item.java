package org.rs2server.rs2.model;

import org.rs2server.rs2.model.equipment.EquipmentDefinition;

/**
 * Represents a single item.
 * @author Graham Edgecombe
 *
 */
public class Item {
	
	/**
	 * The id.
	 */
	private int id;
	
	/**
	 * The number of items.
	 */
	private int count;
	
	private int slot;
	
	public int getSlot() {
		return slot;
	}
	
	/**
	 * Creates a single item.
	 * @param id The id.
	 */
	public Item(int id) {
		this(id, 1);
	}
	
	public Item(int id, int count, int slot) {
		this.id = id;
		this.count = count;
		this.slot = slot;
	}
	
	public static Item create(int id) {
		return new Item(id);
	}
	
	public static Item slot(int id, int slot) {
		return new Item(id, 1, slot);
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	/**
	 * Creates a stacked item.
	 * @param id The id.
	 * @param count The number of items.
	 * @throws IllegalArgumentException if count is negative.
	 */
	public Item(int id, int count) {
		if(count < 0) {
			throw new IllegalArgumentException("Count cannot be negative.");
		}
		this.id = id;
		this.count = count;
	}
	
	/**
	 * Gets the definition of this item.
	 * @return The definition.
	 */
	public ItemDefinition getDefinition() {
		return ItemDefinition.forId(id);
	}
	
	/**
	 * Gets the definition of this item.
	 * @return The definition.
	 */
	public EquipmentDefinition getEquipmentDefinition() {
		return EquipmentDefinition.forId(id);
	}
	
	/**
	 * Gets the item id.
	 * @return The item id.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Gets the count.
	 * @return The count.
	 */
	public int getCount() {
		return count;
	}
	
	@Override
	public String toString() {
		return Item.class.getName() + " [id=" + id + ", count=" + count + "]";
	}

}
