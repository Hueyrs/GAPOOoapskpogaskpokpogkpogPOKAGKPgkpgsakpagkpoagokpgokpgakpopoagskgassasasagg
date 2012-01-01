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
 * <p>Represents a type of NPC.</p>
 * @author Graham Edgecombe
 *
 */
public class NPCDefinition {
	
	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(NPCDefinition.class.getName());
	
	/**
	 * The definitions array.
	 */
	private static NPCDefinition[] definitions;

	/**
	 * @return the definitions
	 */
	public static NPCDefinition[] getDefinitions() {
		return definitions;
	}

	/**
	 * @param definitions the definitions to set
	 */
	public static void setDefinitions(NPCDefinition[] definitions) {
		NPCDefinition.definitions = definitions;
	}

	/**
	 * Adds a definition. TODO better way?
	 * @param def The definition.
	 */
	static void addDefinition(NPCDefinition def) {
		definitions[def.getId()] = def;
	}
	
	/**
	 * Gets an npc definition by its id.
	 * @param id The id.
	 * @return The definition.
	 */
	public static NPCDefinition forId(int id) {
		return definitions[id];
	}
	

	/**
	 * Loads the item definitions.
	 * @throws IOException if an I/O error occurs.
	 * @throws IllegalStateException if the definitions have been loaded already.
	 */
	public static void init() throws IOException {
		if(definitions != null) {
			throw new IllegalStateException("NPC definitions already loaded.");
		}
		logger.info("Loading NPC definitions...");
		RandomAccessFile raf = new RandomAccessFile("./data/npcDefinitions.bin", "r");
		try {
			ByteBuffer buffer = raf.getChannel().map(MapMode.READ_ONLY, 0, raf.length());
			int count = buffer.getShort();
			definitions = new NPCDefinition[count];
			while(buffer.hasRemaining()) {
				int id = buffer.getShort();
				if(id == -1) {
					continue;
				}
				String name = Buffers.readString(buffer);
				String examine = Buffers.readString(buffer);
				byte size = buffer.get();
				int combatLevel = buffer.getShort();
				String[] interactionMenu = new String[5];
				for(int k = 0; k < 5; k++) {
					String opt = Buffers.readString(buffer);
					if(!opt.equals("hidden")) {
						interactionMenu[k] = opt;
					}
				}
				definitions[id] = new NPCDefinition(id, name, examine, combatLevel, size, interactionMenu);
			}
			logger.info("Loaded " + definitions.length + " NPC definitions.");
		} finally {
			raf.close();
		}
//		raf = new RandomAccessFile("npcExamines.bin", "r");
//		try {
//			ByteBuffer buffer = raf.getChannel().map(MapMode.READ_ONLY, 0, raf.length());
//			int count = buffer.getShort();
//			while(buffer.hasRemaining()) {
//				int id = buffer.getShort();
//				if(id == -1) {
//					continue;
//				}
//				String examine = Buffers.readString(buffer);
//				if(definitions[id] != null) {
//					definitions[id] = new NPCDefinition(definitions[id].getId(), definitions[id].getName(), examine, definitions[id].getCombatLevel(), definitions[id].getSize(), definitions[id].getInteractionMenu());
//				}
//			}
//			logger.info("Loaded " + definitions.length + " NPC definitions.");
//		} finally {
//			raf.close();
//		}
//		definitions[5509].setDescription("Sells genuine Neitiznot knitwear.");
//		definitions[5518].setDescription("Guards the Burgher's room.");
//		definitions[5519].setDescription("Guards the Burgher's room.");
//		dump();
	}
	
	/**
	 * Dumps the NPC definitions.
	 * @throws IOException if an I/O error occurs.
	 * @throws IllegalStateException if the definitions have been loaded already.
	 */
	public static void dump() {
		try {
			logger.info("Dumping NPC definitions...");
			OutputStream os = new FileOutputStream("./data/npcDefinitions-new.bin");
			IoBuffer buf = IoBuffer.allocate(1024);
			buf.setAutoExpand(true);

			buf.putShort((short) definitions.length);
			for(int i = 0; i < definitions.length; i++) {
				NPCDefinition npc = definitions[i];
				if(npc != null) {
					buf.putShort((short) npc.getId());
					IoBufferUtils.putRS2String(buf, npc.getName());
					String desc = "";
					if(npc.getDescription() != null && !npc.getDescription().equalsIgnoreCase("null")) {
						desc = npc.getDescription();
					} else {
						String prefix = "a ";
						char c = npc.getName().charAt(0);
						if(c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
							prefix = "an ";
						}
						desc = "It's " + prefix + npc.getName() + ".";
					}
					IoBufferUtils.putRS2String(buf, desc);
					buf.put((byte) npc.getSize());
					buf.putShort((short) npc.getCombatLevel());
					for(int k = 0; k < 5; k++) {
						if(npc.getInteractionMenu()[k] != null) {
							IoBufferUtils.putRS2String(buf, npc.getInteractionMenu()[k]);
						} else {
							IoBufferUtils.putRS2String(buf, "hidden");
						}
					}
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
			logger.info("NPC definitions dumped successfully.");
		} catch(IOException e) {
			e.printStackTrace();
		}
		return;
	}
	
	/**
	 * The npc's id.
	 */
	private int id;
	
	/**
	 * The npc's name.
	 */
	private String name;
	
	/**
	 * The npc's description.
	 */
	private String description;

	/**
	 * The npc's size.
	 */
	private int size;
	
	/**
	 * The npc's combat level.
	 */
	private int combatLevel;
	
	/**
	 * The npc's right click options.
	 */
	private String[] interactionMenu;

	/**
	 * Creates the definition.
	 * @param id The id.
	 */
	public NPCDefinition(int id, String name, String description, int combatLevel, int size, String[] interactionMenu) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.combatLevel = combatLevel;
		this.size = size;
		this.interactionMenu = interactionMenu;
	}
	
	public static NPCDefinition quickDef(int id) {
		String[] options = null;
		return new NPCDefinition(id, "", "", 1, 1, options);
	}
		
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
		
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
		
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the combatLevel
	 */
	public int getCombatLevel() {
		return combatLevel;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	
	public static int forName(String text) {
		for (NPCDefinition d : definitions)
			if (d.name.equalsIgnoreCase(text))
				return d.id;
		return -1;
	}
	/**
	 * @return The interactionMenu.
	 */
	public String[] getInteractionMenu() {
		return interactionMenu;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
