package org.rs2server.rs2.content.sounds;

public class SoundList {

	public enum SOUND_LIST {
		/*
		 * Miscellaneous sounds.
		 */
		FOOD_EAT(317),
		ITEM_DROP(376),
		ITEM_COOK(357),
		ITEM_PICKUP(358),
		FIRE_LIGHT(375),
		ARROW_SHOOT(370),
		TELEPORT(202),
		BONE_BURY(380),
		POTION_DRINK(334),
		TREE_CUT_BEGIN(471),
		TREE_CUTTING(472),
		TREE_EMPTY(473),
		LOG_FLETCH(375),
		ANVIL(468, 5),
		MINING(1331),	
		EXPLODING_ROCK_1(429),
		EXPLODING_ROCK_2(432, 20),
		DITCH(2462),
		
		/*
		 * Prayers
		 */
		RECHARGE_PRAYER(442),
		PROT_RANGE(444),
		PROT_MELEE(433),
		PROT_MAGE(438),
		NO_PRAY(437),	
		
		/*
		 * Combat effects
		 */
		MAGE_SPLASH(193),
		TELEBLOCK_CAST(1185),
		TELEBLOCK_HIT(1183),

		/*
		 * Magic
		 */
		ALCHEMY_LOW(224),
		ALCHEMY_HIGH(223),
		BLITZ_ICE(1110);
		
		private int sound, delay = 0;

		SOUND_LIST(int sound) {
			this.sound = sound;
		}
		
		SOUND_LIST(int sound, int delay) {
			this.sound = sound;
			this.delay = delay;
		}

		public int getSound() {
			return sound;
		}
		
		public int getDelay() {
			return delay;
		}
	}

}