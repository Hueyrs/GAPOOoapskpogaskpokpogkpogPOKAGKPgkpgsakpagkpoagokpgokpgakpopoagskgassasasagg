package org.rs2server.rs2.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.logging.Logger;

import org.apache.mina.core.buffer.IoBuffer;
import org.rs2server.rs2.util.IoBufferUtils;
import org.rs2server.util.Buffers;


/**
 * The item definition manager.
 * @author Vastico
 * @author Graham Edgecombe
 *
 */
public class ItemDefinition {
	
	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(ItemDefinition.class.getName());
	
	/**
	 * The definition array.
	 */
	private static ItemDefinition[] definitions;
	
	/**
	 * Gets a definition for the specified id.
	 * @param id The id.
	 * @return The definition.
	 */
	public static ItemDefinition forId(int id) {
		return definitions[id];
	}
	
	/**
	 * Gets the definition array.
	 * @return The definition array
	 */
	public static ItemDefinition[] getDefinitions() {
		return definitions;
	}
	
	
	/**
	 * Loads the item definitions.
	 * @throws IOException if an I/O error occurs.
	 * @throws IllegalStateException if the definitions have been loaded already.
	 */
	public static void init() throws IOException {
		if(definitions != null) {
			throw new IllegalStateException("Item definitions already loaded.");
		}
		logger.info("Loading item definitions...");
		RandomAccessFile raf = new RandomAccessFile("data/itemDefinitions.bin", "r");
		try {
			ByteBuffer buffer = raf.getChannel().map(MapMode.READ_ONLY, 0, raf.length());
			int count = buffer.getShort() + 1;//+1 because arrays start at 0
			definitions = new ItemDefinition[14000];
			for(int i = 0; i < count; i++) {				
				int id = buffer.getShort();
				if(id == -1) {
					continue;
				}
				String name = Buffers.readString(buffer).replace("(+)", "(p+)").replace("(s)", "(p++)");
				String examine = Buffers.readString(buffer);
				boolean noted = buffer.get() == 1 ? true : false;
				int parentId = buffer.getShort() & 0xFFFF;
				if(parentId == 65535) {
					parentId = -1;
				}
				boolean noteable = buffer.get() == 1 ? true : false;
				int notedId = buffer.getShort() & 0xFFFF;
				if(notedId == 65535) {
					notedId = -1;
				}
				boolean stackable = buffer.get() == 1 ? true : false;
				boolean members = buffer.get() == 1 ? true : false;
				int shopValue = buffer.getInt();
				definitions[id] = new ItemDefinition(id, name, examine, noted, noteable, stackable, parentId, notedId, members, shopValue);
			}
			logger.info("Loaded " + definitions.length + " item definitions.");
		} finally {
			raf.close();
//			definitions[11698] = new ItemDefinition(11698, "Saradomin godsword", "A gracious, heavy sword.", false, true, false, 11698, 11699, true, 1250000);
//			definitions[11699] = new ItemDefinition(11699, "Saradomin godsword", "Swap this note at any bank for a Saradomin godsword.", true, true, true, 11698, 11699, true, 1250000);
//			definitions[11701] = new ItemDefinition(11701, "Zamorak godsword", "Swap this note at any bank for a Zamorak godsword.", true, true, true, 11700, 11701, true, 1250000);
//			
//			
//			definitions[11724] = new ItemDefinition(11724, "Bandos chestplate", "A sturdy chestplate.", false, true, false, 11724, 11725, true, 265000);
//			definitions[11725] = new ItemDefinition(11725, "Bandos chestplate", "Swap this note at any bank for a Bandos chestplate.", true, true, true, 11724, 11725, true, 265000);
//			definitions[11726] = new ItemDefinition(11726, "Bandos tassets", "A sturdy pair of tassets.", false, true, false, 11726, 11727, true, 271666);
//			definitions[11727] = new ItemDefinition(11727, "Bandos tassets", "Swap this note at any bank for a Bandos tassets.", true, true, true, 11726, 11727, true, 271666);
//			definitions[11728] = new ItemDefinition(11728, "Bandos boots", "Some sturdy boots.", false, true, false, 11728, 11729, true, 25000);
//			definitions[11729] = new ItemDefinition(11729, "Bandos boots", "Swap this note at any bank for a Bandos boots.", true, true, true, 11728, 11729, true, 25000);
//
//			definitions[11212] = new ItemDefinition(11212, "Dragon arrow", "An arrow made using a dragon's talon.", false, false, true, 11212, 11212, true, 800);
//			definitions[11235] = new ItemDefinition(11235, "Dark bow", "A bow from a darker dimension.", false, true, false, 11235, 11236, true, 120000);
//			definitions[11236] = new ItemDefinition(11236, "Dark bow", "Swap this note at any bank for a Dark bow.", true, true, true, 11235, 11236, true, 120000);
//			definitions[9075] = new ItemDefinition(9075, "Astral rune", "Used for Lunar Spells.", false, false, true, 9075, 9075, true, 800);
//			definitions[10498] = new ItemDefinition(10498, "Ava's attractor", "A bagged chicken ready to serve you, magnet in claw.", false, false, false, 10498, 10498, true, 495);
//			definitions[11733] = new ItemDefinition(11733, "Dragon boots", "Swap this note at any bank for a Dragon boots.", true, true, true, 11732, 11733, true, 20000);
//			definitions[10499] = new ItemDefinition(10499, "Ava's accumulator", "A superior bagged chicken ready to serve you, magnet in claw.", false, false, false, 10499, 10499, true, 495);
//			
//			
//			definitions[9174] = new ItemDefinition(9174, "Bronze c'bow", "A bronze crossbow.", false, true, false, 9174, 9175, true, 72);
//			definitions[9175] = new ItemDefinition(9175, "Bronze c'bow", "Swap this note at any bank for a Bronze c'bow.", true, true, true, 9174, 9175, true, 72);
//			definitions[9176] = new ItemDefinition(9176, "Blurite c'bow", "A blurite crossbow.", false, false, false, 9176, 9176, true, 91);
//			definitions[9177] = new ItemDefinition(9177, "Iron c'bow", "An iron crossbow.", false, true, false, 9177, 9178, true, 157);
//			definitions[9178] = new ItemDefinition(9178, "Iron c'bow", "Swap this note at any bank for an Iron c'bow.", true, true, true, 9177, 9178, true, 157);
//			definitions[9179] = new ItemDefinition(9179, "Steel c'bow", "A steel crossbow.", false, true, false, 9179, 9180, true, 360);
//			definitions[9180] = new ItemDefinition(9180, "Steel c'bow", "Swap this note at any bank for a Steel c'bow.", true, true, true, 9179, 9180, true, 360);
//			definitions[9181] = new ItemDefinition(9181, "Mith c'bow", "A mithril crossbow.", false, true, false, 9181, 9182, true, 782);
//			definitions[9182] = new ItemDefinition(9182, "Mith c'bow", "Swap this note at any bank for a Mith c'bow.", true, true, true, 9181, 9182, true, 782);
//			definitions[9183] = new ItemDefinition(9183, "Adamant c'bow", "An adamant crossbow.", false, true, false, 9183, 9184, true, 1472);
//			definitions[9184] = new ItemDefinition(9184, "Adamant c'bow", "Swap this note at any bank for an Adamant c'bow.", true, true, true, 9183, 9184, true, 1472);
//			definitions[9185] = new ItemDefinition(9185, "Rune c'bow", "A rune crossbow.", false, true, false, 9185, 9186, true, 16200);
//			definitions[9186] = new ItemDefinition(9186, "Rune c'bow", "Swap this note at any bank for a Rune c'bow.", true, true, true, 9185, 9186, true, 16200);
//			
//			
//
//			int id = 9144;
//			definitions[id] = new ItemDefinition(id, "Runite bolts", "Runite crossbow bolts.", false, false, true, id, id, true, 300);
//			id = 9341;
//			definitions[id] = new ItemDefinition(id, "Dragon bolts", "Dragonstone tipped Runite crossbow bolts.", false, false, true, id, id, true, 2000);
//			id = 9244;
//			definitions[id] = new ItemDefinition(id, "Dragon bolts (e)", "Enchanted Dragonstone tipped Runite crossbow bolts.", false, false, true, id, id, true, 1061);
//			id = 9342;
//			definitions[id] = new ItemDefinition(id, "Onyx bolts", "Onyx tipped Runite crossbow bolts.", false, false, true, id, id, true, 13632);
//			id = 9245;
//			definitions[id] = new ItemDefinition(id, "Onyx bolts (e)", "Enchanted Onyx tipped Runite crossbow bolts.", false, false, true, id, id, true, 15000);
//			id = 9140;
//			definitions[id] = new ItemDefinition(id, "Iron bolts", "Iron crossbow bolts.", false, false, true, id, id, true, 2);
//			id = 880;
//			definitions[id] = new ItemDefinition(id, "Pearl bolts", "Pearl tipped Iron crossbow bolts.", false, false, true, id, id, true, 15);
//			id = 9238;
//			definitions[id] = new ItemDefinition(id, "Pearl bolts (e)", "Enchanted Pearl tipped Iron crossbow bolts.", false, false, true, id, id, true, 14);
//			id = 9141;
//			definitions[id] = new ItemDefinition(id, "Steel bolts", "Steel crossbow bolts.", false, false, true, id, id, true, 24);
//			id = 9336;
//			definitions[id] = new ItemDefinition(id, "Topaz bolts", "Red Topaz tipped Steel crossbow bolts.", false, false, true, id, id, true, 20);
//			id = 9239;
//			definitions[id] = new ItemDefinition(id, "Topaz bolts (e)", "Enchanted Red Topaz tipped Steel crossbow bolts.", false, false, true, id, id, true, 20);
//			id = 9142;
//			definitions[id] = new ItemDefinition(id, "Mithril bolts", "Mithril crossbow bolts.", false, false, true, id, id, true, 20);
//			id = 9337;
//			definitions[id] = new ItemDefinition(id, "Sapphire bolts", "Sapphire tipped Mithril crossbow bolts.", false, false, true, id, id, true, 20);
//			id = 9240;
//			definitions[id] = new ItemDefinition(id, "Sapphire bolts (e)", "Enchanted Sapphire tipped Mithril crossbow bolts.", false, false, true, id, id, true, 40);
//			id = 9338;
//			definitions[id] = new ItemDefinition(id, "Emerald bolts", "Emerald tipped Mithril crossbow bolts.", false, false, true, id, id, true, 417);
//			id = 9241;
//			definitions[id] = new ItemDefinition(id, "Emerald bolts (e)", "Enchanted Emerald tipped Mithril crossbow bolts.", false, false, true, id, id, true, 167);
//			id = 9143;
//			definitions[id] = new ItemDefinition(id, "Adamant bolts", "Adamantite crossbow bolts.", false, false, true, id, id, true, 57);
//			id = 9339;
//			definitions[id] = new ItemDefinition(id, "Ruby bolts", "Ruby tipped Adamantite crossbow bolts.", false, false, true, id, id, true, 57);
//			id = 9242;
//			definitions[id] = new ItemDefinition(id, "Ruby bolts (e)", "Enchanted Ruby tipped Adamantite crossbow bolts.", false, false, true, id, id, true, 137);
//			id = 9340;
//			definitions[id] = new ItemDefinition(id, "Diamond bolts", "Diamond tipped Adamantite crossbow bolts.", false, false, true, id, id, true, 1083);
//			id = 9243;
//			definitions[id] = new ItemDefinition(id, "Diamond bolts (e)", "Enchanted Diamond tipped Adamantite crossbow bolts.", false, false, true, id, id, true, 210);
//			id = 877;
//			definitions[id] = new ItemDefinition(id, "Bronze bolts", "Bronze crossbow bolts.", false, false, true, id, id, true, 2);
//			id = 879;
//			definitions[id] = new ItemDefinition(id, "Opal bolts", "Opal tipped Bronze crossbow bolts.", false, false, true, id, id, true, 18);
//			id = 9236;
//			definitions[id] = new ItemDefinition(id, "Opal bolts (e)", "Enchanted Opal tipped Bronze crossbow bolts.", false, false, true, id, id, true, 18);
//			
//			
//			
//			id = 9703;
//			definitions[id] = new ItemDefinition(id, "Training sword", "Basic training sword.", false, false, false, id, id, false, 0);
//			id = 9704;
//			definitions[id] = new ItemDefinition(id, "Training shield", "Made of flimsy painted wood.", false, false, false, id, id, false, 0);
//			id = 9705;
//			definitions[id] = new ItemDefinition(id, "Training bow", "Light and flexible, good for a beginner.", false, false, false, id, id, false, 0);
//			id = 9706;
//			definitions[id] = new ItemDefinition(id, "Training arrows", "Standard training arrows.", false, false, true, id, id, false, 0);
//			int id = 9440;
//			definitions[id] = new ItemDefinition(id, "Wooden stock", "A wooden crossbow stock.", false, true, false, id, id+1, true, 20);
//			id++;
//			definitions[id] = new ItemDefinition(id, "Wooden stock", "Swap this note at any bank for a Wooden stock.", true, true, true, id-1, id, true, 20);
//			id++;
//			definitions[id] = new ItemDefinition(id, "Oak stock", "An oak crossbow stock.", false, true, false, id, id+1, true, 27);
//			id++;
//			definitions[id] = new ItemDefinition(id, "Oak stock", "Swap this note at any bank for an Oak stock.", true, true, true, id-1, id, true, 27);
//			id++;
//			definitions[id] = new ItemDefinition(id, "Willow stock", "A willow crossbow stock.", false, true, false, id, id+1, true, 52);
//			id++;
//			definitions[id] = new ItemDefinition(id, "Willow stock", "Swap this note at any bank for a Willow stock.", true, true, true, id-1, id, true, 52);
//			id++;
//			definitions[id] = new ItemDefinition(id, "Teak stock", "A teak crossbow stock.", false, true, false, id, id+1, true, 76);
//			id++;
//			definitions[id] = new ItemDefinition(id, "Teak stock", "Swap this note at any bank for a Teak stock.", true, true, true, id-1, id, true, 76);
//			id++;
//			definitions[id] = new ItemDefinition(id, "Maple stock", "A maple crossbow stock.", false, true, false, id, id+1, true, 180);
//			id++;
//			definitions[id] = new ItemDefinition(id, "Maple stock", "Swap this note at any bank for a Maple stock.", true, true, true, id-1, id, true, 180);
//			id++;
//			definitions[id] = new ItemDefinition(id, "Mahogany stock", "A mahogany crossbow stock.", false, true, false, id, id+1, true, 131);
//			id++;
//			definitions[id] = new ItemDefinition(id, "Mahogany stock", "Swap this note at any bank for a Mahogany stock.", true, true, true, id-1, id, true, 131);
//			id++;
//			definitions[id] = new ItemDefinition(id, "Yew stock", "A yew crossbow stock.", false, true, false, id, id+1, true, 167);
//			id++;
//			definitions[id] = new ItemDefinition(id, "Yew stock", "Swap this note at any bank for a Yew stock.", true, true, true, id-1, id, true, 167);
//			dump();
			definitions[8851] = new ItemDefinition(8851, "Warrior guild token", "Warrior Guild Token.", false, false, true, 8511, 8511, true, 18);
			definitions[11286] = new ItemDefinition(11286, "Draconic visage", "It looks like this could be attached to a shield somehow.", false, false, true, 11286, 11286, true, 18);
			definitions[11283] = new ItemDefinition(11283, "Dragonfire shield", "A heavy shield with a snarling, draconic visage.", false, false, false, 11283, 11283, true, 495);
			definitions[11718] = new ItemDefinition(11718, "Armadyl helmet", "A helmet of great craftmanship.", false, false, false, 11718, 11718, true, 495);
			definitions[11720] = new ItemDefinition(11720, "Armadyl chestplate", "A chestplate of great craftmanship.", false, false, false, 11720, 11720, true, 495);
			definitions[11722] = new ItemDefinition(11722, "Armadyl chainskirt", "A chainskirt of great craftmanship.", false, false, false, 11722, 11722, true, 495);
			definitions[11730] = new ItemDefinition(11730, "Saradomin sword", "The incredible blade of an Icyene.", false, false, false, 11730, 11730, true, 495);
			definitions[11716] = new ItemDefinition(11716, "Zamorakian spear", "An evil spear.", false, false, false, 11716, 11716, true, 495);
			definitions[4561] = new ItemDefinition(4561, "Purple sweets", "Not likely to laste until next Halloween.", false, false, true, 4561, 4561, true, 800);
			dump();
			//definitions[8851] = new ItemDefinition(8851, "Warrior guild token", "Warrior Guild Token.", true, true, true, true, id-1, id, true, 167);
		}
	}
	
	
	/**
	 * Dumps the item definitions.
	 * @throws IOException if an I/O error occurs.
	 * @throws IllegalStateException if the definitions have been loaded already.
	 */
	public static void dump() {
		try {
			logger.info("Dumping item definitions...");
			OutputStream os = new FileOutputStream("data/itemDefinitions-new.bin");
			IoBuffer buf = IoBuffer.allocate(1024);
			buf.setAutoExpand(true);

			ItemDefinition item = null;
			for(ItemDefinition itemDef : definitions) {
				if(itemDef != null) {
					if(item == null || itemDef.getId() > item.getId()) {
						item = itemDef;
					}
				}
			}

			buf.putShort((short) item.getId());
			for(int i = 0; i < definitions.length; i++) {
				item = definitions[i];
				if(item != null) {
					buf.putShort((short) item.getId());
					IoBufferUtils.putRS2String(buf, item.getName());
					IoBufferUtils.putRS2String(buf, item.getDescription());
					buf.put((byte) (item.isNoted() ? 1 : 0));
					buf.putShort((short) item.getNormalId());
					buf.put((byte) (item.isNoteable() ? 1 : 0));
					buf.putShort((short) item.getNotedId());
					buf.put((byte) (item.isStackable() ? 1 : 0));
					buf.put((byte) (item.isMembersOnly() ? 1 : 0));
					buf.putInt(item.getValue());
				} else {
					buf.putShort((short) -1);
				}
			}		
			buf.flip();
			byte[] data = new byte[buf.limit()];
			buf.get(data);
			os.write(data);
			os.flush();
			os.close();
			logger.info("Item definitions dumped successfully.");
		} catch(IOException e) {
			e.printStackTrace();
		}
		return;
	}
	
	/**
	 * Untradable items 
	 */
	
	public static int[] untradableItems = new int[] {
		6570,
		2677
	};
	
	/**
	 * Id.
	 */
	private final int id;
	
	/**
	 * Name.
	 */
	private final String name;
	
	/**
	 * Description.
	 */
	private final String examine;
	
	/**
	 * Noted flag.
	 */
	private final boolean noted;
	
	/**
	 * Noteable flag.
	 */
	private final boolean noteable;
	
	/**
	 * Stackable flag.
	 */
	private final boolean stackable;
	
	/**
	 * Non-noted id.
	 */
	private final int parentId;
	
	/**
	 * Noted id.
	 */
	private final int notedId;
	
	/**
	 * Members flag.
	 */
	private final boolean members;
	
	/**
	 * Shop value.
	 */
	private final int shopValue;
	
	/**
	 * Creates the item definition.
	 * @param id The id.
	 * @param name The name.
	 * @param examine The description.
	 * @param noted The noted flag.
	 * @param noteable The noteable flag.
	 * @param stackable The stackable flag.
	 * @param parentId The non-noted id.
	 * @param notedId The noted id.
	 * @param members The members flag.
	 * @param shopValue The shop price.
	 * @param highAlcValue The high alc value.
	 * @param lowAlcValue The low alc value.
	 */
	private ItemDefinition(int id, String name, String examine, boolean noted, boolean noteable, boolean stackable, int parentId, int notedId, boolean members, int shopValue) {
		this.id = id;
		this.name = name;
		this.examine = examine;
		this.noted = noted;
		this.noteable = noteable;
		this.stackable = stackable;
		this.parentId = parentId;
		this.notedId = notedId;
		this.members = members;
		this.shopValue = shopValue;
	}
	
	/**
	 * Gets the id.
	 * @return The id.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Gets the name.
	 * @return The name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the description.
	 * @return The description.
	 */
	public String getDescription() {
		return examine;
	}
	
	/**
	 * Gets the noted flag.
	 * @return The noted flag.
	 */
	public boolean isNoted() {
		return noted;
	}
	
	/**
	 * Gets the noteable flag.
	 * @return The noteable flag.
	 */
	public boolean isNoteable() {
		return noteable;
	}
	
	/**
	 * Gets the stackable flag.
	 * @return The stackable flag.
	 */
	public boolean isStackable() {
		return stackable || noted;
	}
	
	/**
	 * Gets the normal id.
	 * @return The normal id.
	 */
	public int getNormalId() {
		return parentId;
	}
	
	/**
	 * Gets the noted id.
	 * @return The noted id.
	 */
	public int getNotedId() {
		return notedId;
	}
	
	/**
	 * Gets the members only flag.
	 * @return The members only flag.
	 */
	public boolean isMembersOnly() {
		return members;
	}
	
	/**
	 * Gets the value.
	 * @return The value.
	 */
	public int getValue() {
		return shopValue;
	}
	
	public static int forName(String text) {
		for (ItemDefinition d : definitions)
			if (d.name.equalsIgnoreCase(text))
				return d.id;
		return -1;
	}
}