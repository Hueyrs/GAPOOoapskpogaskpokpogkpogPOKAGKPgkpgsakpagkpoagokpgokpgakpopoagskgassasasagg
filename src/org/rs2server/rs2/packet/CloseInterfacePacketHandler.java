package org.rs2server.rs2.packet;

import java.util.logging.Logger;

import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.skills.Fletching;
import org.rs2server.rs2.model.skills.Herblore;
import org.rs2server.rs2.model.skills.Fletching.Log;
import org.rs2server.rs2.model.skills.Herblore.HerbloreType;
import org.rs2server.rs2.model.skills.Herblore.PrimaryIngredient;
import org.rs2server.rs2.model.skills.Herblore.SecondaryIngredient;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.util.Misc;


/**
 * A packet handler that is called when an interface is closed.
 * @author Graham Edgecombe
 *
 */
public class CloseInterfacePacketHandler implements PacketHandler {

	/**
	 * The logger instance.
	 */
	private static final Logger logger = Logger.getLogger(CloseInterfacePacketHandler.class.getName());

	@Override
	public void handle(Player player, Packet packet) {
		if(packet.getOpcode() == 64) {
			int interfaceId = packet.getInt2();
			int childId = packet.getLEShort();
			switch(interfaceId) {
			case 184: //What is this random event
				switch(childId) {
				case 8:
					int randMoney = Misc.random(100, 30000);
					player.getInventory().add(new Item(995, randMoney));
					player.getActionSender().sendMessage("Congratulations! You are rewarded " + randMoney + " coins!");
					break;
				case 9:
				case 10:
					player.botWarnings++;
					player.getActionSender().sendMessage("Wrong answer! You have " + player.botWarnings + " bot warnings.");
					break;
				}
				player.getActionSender().removeChatboxInterface();
				break;
			case 191: //Odd one out random event
				switch(childId) {
				case 7:
					int randMoney = Misc.random(100, 30000);
					player.getInventory().add(new Item(995, randMoney));
					player.getActionSender().sendMessage("Congratulations! You are rewarded " + randMoney + " coins!");
					break;
				case 6:
				case 5:
					player.botWarnings++;
					player.getActionSender().sendMessage("Wrong answer! You have " + player.botWarnings + " bot warnings.");
					break;
				}
				player.getActionSender().removeChatboxInterface();
				break;
			case 7:
			case 64:
			case 65:
			case 66:
			case 67:
			case 241:
			case 242:
			case 243:
			case 244:
			case 157:
			case 158:
			case 159:
			case 160:
			case 161:
			case 162:
			case 163:
			case 164:
			case 165:
			case 166:
			case 167:
			case 168:
			case 169:
			case 170:
			case 171:
			case 172:
			case 173:
			case 174:
			case 175:
			case 176:
			case 177:
			case 519:
				DialogueManager.advanceDialogue(player, 0);
				break;
			case 210:
			case 211:
			case 212:
			case 213:
			case 214:
			case 228:
			case 229:
			case 230:
			case 231:
			case 232:
			case 233:
			case 234:
			case 235:
			case 236:
			case 237:
			case 238:
				DialogueManager.advanceDialogue(player, childId - 1);
				break;
			case 53:
				if(player.getInterfaceAttribute("herblore_index") != null && player.getInterfaceAttribute("herblore_type") != null) {
					int productionCount = -1;
					switch(childId) {
					case 6:
						productionCount = 1;
						break;
					case 5:
						productionCount = 5;
						break;
					case 4:
						player.getInterfaceState().openEnterAmountInterface(309, -1, -1);
						break;
					case 3:
						productionCount = 28;
						break;
					}
					if(productionCount != -1) {
						switch((HerbloreType) player.getInterfaceAttribute("herblore_type")) {
						case PRIMARY_INGREDIENT:
							player.getActionQueue().addAction(new Herblore(player, productionCount, PrimaryIngredient.forId((Integer) player.getInterfaceAttribute("herblore_index")), null, HerbloreType.PRIMARY_INGREDIENT));
							break;
						case SECONDARY_INGREDIENT:
							player.getActionQueue().addAction(new Herblore(player, productionCount, null, SecondaryIngredient.forId((Integer) player.getInterfaceAttribute("herblore_index")), HerbloreType.SECONDARY_INGREDIENT));
							break;
						}
						player.getActionSender().removeChatboxInterface();
					}
				}
				break;
			case 47:
				if(player.getInterfaceAttribute("fletching_log") != null) {
					int logIndex = -1;
					int productionCount = -1;
					switch(childId) {
					case 7:
					case 6:
					case 5:
					case 4:
						logIndex = 0;
						break;
					case 11:
					case 10:
					case 9:
					case 8:
						logIndex = 1;
						break;
					}
					switch(childId) {
					case 7:
					case 11:
						productionCount = 1;
						break;
					case 6:
					case 10:
						productionCount = 5;
						break;
					case 5:
					case 9:
						productionCount = 10;
						break;
					case 4:
					case 8:
						player.getInterfaceState().openEnterAmountInterface(303, logIndex, -1);
						player.setInterfaceAttribute("fletching_index", logIndex);
						break;
					}
					if(productionCount != -1) {
						player.getActionQueue().addAction(new Fletching(player, productionCount, logIndex, (Log) player.getInterfaceAttribute("fletching_log")));
						player.getActionSender().removeChatboxInterface();
					}
				}
				break;
			case 48:
				if(player.getInterfaceAttribute("fletching_log") != null) {
					int logIndex = -1;
					int productionCount = -1;
					switch(childId) {
					case 8:
					case 7:
					case 6:
					case 5:
						logIndex = 0;
						break;
					case 12:
					case 11:
					case 10:
					case 9:
						logIndex = 1;
						break;
					case 16:
					case 15:
					case 14:
					case 13:
						logIndex = 2;
						break;
					}
					switch(childId) {
					case 8:
					case 12:
					case 16:
						productionCount = 1;
						break;
					case 7:
					case 11:
					case 15:
						productionCount = 5;
						break;
					case 6:
					case 10:
					case 14:
						productionCount = 10;
						break;
					case 5:
					case 9:
					case 13:
						player.getInterfaceState().openEnterAmountInterface(304, logIndex, -1);
						player.setInterfaceAttribute("fletching_index", logIndex);
						break;
					}
					if(productionCount != -1) {
						player.getActionQueue().addAction(new Fletching(player, productionCount, logIndex, (Log) player.getInterfaceAttribute("fletching_log")));
						player.getActionSender().removeChatboxInterface();
					}
				}
				break;
			case 49:
				if(player.getInterfaceAttribute("fletching_log") != null) {
					int logIndex = -1;
					int productionCount = -1;
					switch(childId) {
					case 9:
					case 8:
					case 7:
					case 6:
						logIndex = 0;
						break;
					case 13:
					case 12:
					case 11:
					case 10:
						logIndex = 1;
						break;
					case 17:
					case 16:
					case 15:
					case 14:
						logIndex = 2;
						break;
					case 21:
					case 20:
					case 19:
					case 18:
						logIndex = 3;
						break;
					}
					switch(childId) {
					case 9:
					case 13:
					case 17:
					case 21:
						productionCount = 1;
						break;
					case 8:
					case 12:
					case 16:
					case 20:
						productionCount = 5;
						break;
					case 7:
					case 11:
					case 15:
					case 19:
						productionCount = 10;
						break;
					case 6:
					case 10:
					case 14:
					case 18:
						player.getInterfaceState().openEnterAmountInterface(305, logIndex, -1);
						player.setInterfaceAttribute("fletching_index", logIndex);
						break;
					}
					if(productionCount != -1) {
						player.getActionQueue().addAction(new Fletching(player, productionCount, logIndex, (Log) player.getInterfaceAttribute("fletching_log")));
						player.getActionSender().removeChatboxInterface();
					}
				}
				break;
			default:
				logger.info("Unhandled close interface : " + interfaceId + " - " + childId);
				break;
			}
		} else {
			player.getActionSender().removeChatboxInterface().sendInterfacesRemovedClientSide();
		}
	}

}
