package org.rs2server.rs2.model.minigame.impl;

import org.rs2server.rs2.model.Item;

/**
 * The armor types for warriors guild..
 * @author Canownueasy
 *
 */
public enum ArmorType {
	
	RUNE(Item.create(1163), Item.create(1127), Item.create(1079)),
	ADAMANT(Item.create(1161), Item.create(1123), Item.create(1073)),
	MITHRIL(Item.create(1159), Item.create(1121), Item.create(1071)),
	BLACK(Item.create(1165), Item.create(1125), Item.create(1077)),
	STEEL(Item.create(1157), Item.create(1119), Item.create(1069)),
	IRON(Item.create(1153), Item.create(1115), Item.create(1067)),
	BRONZE(Item.create(1155), Item.create(1117), Item.create(1075));
	
	public Item helm, body, legs;
	
	ArmorType(Item helm, Item body, Item legs) {
		this.helm = helm;
		this.body = body;
		this.legs = legs;
	}

}
