package org.rs2server.rs2.model;

import java.util.Random;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.content.Canoes;
import org.rs2server.rs2.content.LeetPortal;
import org.rs2server.rs2.content.Sedridor;
import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.Spell;
import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.minigame.impl.WarriorsGuild;
import org.rs2server.rs2.net.ActionSender.DialogueType;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

/**
 * Handles dialogues.
 * @author Canownueasy
 */
public class DialogueManager {
	
	private static Random r = new Random();

	public static void openDialogue(final Player player, int dialogueId) {
		if (dialogueId == -1) {
			return;
		}
		for (int i = 0; i < 5; i++) {
			player.getInterfaceState().setNextDialogueId(i, -1);
		}
		player.getInterfaceState().setOpenDialogueId(dialogueId);
		NPC npc = (NPC) player.getInteractingEntity();
		Item skillcape = null;
		Item hood = null;
		switch (dialogueId) {
		case 0:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Good day. How may I help you?");
			player.getInterfaceState().setNextDialogueId(0, 1);
			break;
		case 1:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"I'd like to access my bank account, please.",
					"I'd like to set/change my PIN please.",
					"What is this place?");
			player.getInterfaceState().setNextDialogueId(0, 2);
			player.getInterfaceState().setNextDialogueId(1, 3);
			player.getInterfaceState().setNextDialogueId(2, 5);
			break;
		case 2:
			player.getActionSender().removeChatboxInterface();
			Bank.open(player);
			break;
		case 3:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"I'd like to set/change my PIN please.");
			player.getInterfaceState().setNextDialogueId(0, 4);
			break;
		case 4:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"We currently do not offer bank PINs, sorry.");
			break;
		case 5:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"What is this place?");
			player.getInterfaceState().setNextDialogueId(0, 6);
			break;
		case 6:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(),
					DialogueType.NPC,
					npc.getDefinition().getId(),
					FacialAnimation.DEFAULT,
					"This is a branch of the Bank of " + Constants.SERVER_NAME
							+ ". We have", "branches in many towns.");
			player.getInterfaceState().setNextDialogueId(0, 7);
			break;
		case 7:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"And what do you do?",
					"Didn't you used to be called the Bank of Varrock?");
			player.getInterfaceState().setNextDialogueId(0, 8);
			player.getInterfaceState().setNextDialogueId(1, 10);
			break;
		case 8:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"And what do you do?");
			player.getInterfaceState().setNextDialogueId(0, 9);
			break;
		case 9:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"We will look after your items and money for you.",
					"Leave your valuables with us if you want to keep them",
					"safe.");
			break;
		case 10:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Didn't you used to be called the Bank of Varrock?");
			player.getInterfaceState().setNextDialogueId(0, 11);
			break;
		case 11:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Yes we did, but people kept on coming into our",
					"branches outside of Varrock and telling us that our",
					"signs were wrong. They acted as if we didn't know",
					"what town we were in or something.");
			break;
		case 12:
			player.getActionSender().sendDialogue("Choose spellbook:",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Standard", "Ancient", "Cancel");
			player.getInterfaceState().setNextDialogueId(0, 13);
			player.getInterfaceState().setNextDialogueId(1, 14);
			player.getInterfaceState().setNextDialogueId(2, 15);
			break;
		case 13:
			player.getCombatState().setSpellbookSwap(true);
			MagicCombatAction.executeSpell(Spell.SPELLBOOK_SWAP, player,
					player, 0);
			player.getActionSender().removeAllInterfaces();
			break;
		case 14:
			player.getCombatState().setSpellbookSwap(true);
			MagicCombatAction.executeSpell(Spell.SPELLBOOK_SWAP, player,
					player, 1);
			player.getActionSender().removeAllInterfaces();
			break;
		case 15:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Hello there.");
			player.getInterfaceState().setNextDialogueId(0, 16);
			break;
		case 16:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Hello traveller.");
			player.getInterfaceState().setNextDialogueId(0, 17);
			break;
		case 17:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Where am I?", "Who are you?", "Goodbye.");
			player.getInterfaceState().setNextDialogueId(0, 18);
			player.getInterfaceState().setNextDialogueId(1, 20);
			player.getInterfaceState().setNextDialogueId(2, 28);
			break;
		case 18:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Where am I?");
			player.getInterfaceState().setNextDialogueId(0, 19);
			break;
		case 19:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(),
					DialogueType.NPC,
					npc.getDefinition().getId(),
					FacialAnimation.DEFAULT,
					"You are in " + Constants.SERVER_NAME
							+ ". Through quests and",
					"adventures you may learn the ancient origins of",
					"this mysterious land, but until then,",
					"all I can do is offer you help.");
			break;
		case 20:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Who are you?");
			player.getInterfaceState().setNextDialogueId(0, 21);
			break;
		case 21:
			player
					.getActionSender()
					.sendDialogue(
							npc.getDefinition().getName(),
							DialogueType.NPC,
							npc.getDefinition().getId(),
							FacialAnimation.DEFAULT,
							"I am Mawnis Burowgar, leader of Neitiznot. I am here",
							"to guide travellers like you through their own adventure.",
							"Would you like help with anything?");
			player.getInterfaceState().setNextDialogueId(0, 22);
			break;
		case 22:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Where can I get basic supplies?",
					"Where can I make money?", "No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 23);
			player.getInterfaceState().setNextDialogueId(1, 25);
			player.getInterfaceState().setNextDialogueId(2, 27);
			break;
		case 23:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Where can I get basic supplies?");
			player.getInterfaceState().setNextDialogueId(0, 24);
			break;
		case 24:
			player
					.getActionSender()
					.sendDialogue(
							npc.getDefinition().getName(),
							DialogueType.NPC,
							npc.getDefinition().getId(),
							FacialAnimation.DEFAULT,
							"There are many people on this island who will help you.",
							"Talk to various characters around the island who will give",
							"you starting items. Also check the shops near by for",
							"any good deals, and possibly some free items.");
			break;
		case 25:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Where can I make money?");
			player.getInterfaceState().setNextDialogueId(0, 26);
			break;
		case 26:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"The Wise Old Man often helps new comers to make",
					"some small amounts of money. Other than that, you",
					"should train your skills and sell anything you make",
					"to shops or other players for profit.");
			break;
		case 27:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"No thank you.");
			break;
		case 28:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Goodbye.");
			break;
		case 29:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT, "Hello.");
			player.getInterfaceState().setNextDialogueId(0, 30);
			break;
		case 30:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Hello there young warrior. I am Harlan, master",
					"of melee. What can I do for you?");
			player.getInterfaceState().setNextDialogueId(0, 31);
			break;
		case 31:
			if (player.getSkills().getLevelForExperience(Skills.DEFENCE) > 98) {
				player.getActionSender().sendDialogue("Select an Option",
						DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
						"Do you have any supplies for me?",
						"Talk about defence skillcape.", "Nothing, thanks.");
				player.getInterfaceState().setNextDialogueId(0, 32);
				player.getInterfaceState().setNextDialogueId(1, 52);
				player.getInterfaceState().setNextDialogueId(2, 34);
			} else {
				player.getActionSender().sendDialogue("Select an Option",
						DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
						"Do you have any supplies for me?", "Nothing, thanks.");
				player.getInterfaceState().setNextDialogueId(0, 32);
				player.getInterfaceState().setNextDialogueId(1, 34);
			}
			break;
		case 32:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Do you have any supplies for me?");
			player.getInterfaceState().setNextDialogueId(0, 33);
			break;
		case 33:
			if (player.getSkills().getLevel(Skills.RANGE) < 20) {
				player
						.getActionSender()
						.sendDialogue(
								npc.getDefinition().getName(),
								DialogueType.NPC,
								npc.getDefinition().getId(),
								FacialAnimation.DEFAULT,
								"Ofcourse! I am always eager to help young little",
								"warriors like yourself. Take this sword and this shield",
								"and train your melee. But make sure you use",
								"better armour once you level up.");
				player.getInventory().add(new Item(9703));
				player.getInventory().add(new Item(9704));
			} else {
				player.getActionSender().sendDialogue(
						npc.getDefinition().getName(), DialogueType.NPC,
						npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"I'm sorry, I can't help you at all. You should be",
						"using better equipment at your level.");
			}
			break;
		case 34:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Nothing, thanks.");
			break;
		case 35:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT, "Hi.");
			player.getInterfaceState().setNextDialogueId(0, 36);
			break;
		case 36:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Heya! I'm Nemarti. What can I do for you?");
			player.getInterfaceState().setNextDialogueId(0, 37);
			break;
		case 37:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Do you have any supplies for me?", "Nothing, thanks.");
			player.getInterfaceState().setNextDialogueId(0, 38);
			player.getInterfaceState().setNextDialogueId(1, 34);
			break;
		case 38:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Do you have any supplies for me?");
			player.getInterfaceState().setNextDialogueId(0, 39);
			break;
		case 39:
			if (player.getSkills().getCombatLevel() < 20) {
				player.getActionSender().sendDialogue(
						npc.getDefinition().getName(), DialogueType.NPC,
						npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Sure thing, I'm always helping young arrow slingers!",
						"Here you go.");
				player.getInventory().add(new Item(9705));
				player.getInventory().add(new Item(9706, 50));
			} else {
				player.getActionSender().sendDialogue(
						npc.getDefinition().getName(), DialogueType.NPC,
						npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"I'm sorry, I can't help you at all. You should be",
						"using better equipment at your level.");
			}
			break;
		case 40:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Hello there.");
			player.getInterfaceState().setNextDialogueId(0, 41);
			break;
		case 41:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Hello young sage. What can I do for you?");
			player.getInterfaceState().setNextDialogueId(0, 42);
			break;
		case 42:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Do you have any supplies for me?", "Nothing, thanks.");
			player.getInterfaceState().setNextDialogueId(0, 43);
			player.getInterfaceState().setNextDialogueId(1, 34);
			break;
		case 43:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Do you have any supplies for me?");
			player.getInterfaceState().setNextDialogueId(0, 44);
			break;
		case 44:
			if (player.hasReceivedStarterRunes()) {
				player.getActionSender().sendDialogue(
						npc.getDefinition().getName(), DialogueType.NPC,
						npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"I've already given you your set of starter runes.");
			} else {
				player.getActionSender().sendDialogue(
						npc.getDefinition().getName(), DialogueType.NPC,
						npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Sure thing. Here's enough rune stones for 50 casts",
						"of Wind Strike. Use them wisely as I won't be",
						"replacing them for you.");
				player.setReceivedStarterRunes(true);
				player.getInterfaceState().setNextDialogueId(0, 45);
				player.getInventory().add(
						new Item(MagicCombatAction.AIR_RUNE, 50));
				player.getInventory().add(
						new Item(MagicCombatAction.MIND_RUNE, 50));
			}
			break;
		case 45:
			player.getActionSender()
					.sendDialogue(player.getName(), DialogueType.PLAYER, -1,
							FacialAnimation.DEFAULT, "Thanks!");
			break;
		case 46:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT, "Hey.");
			player.getInterfaceState().setNextDialogueId(0, 47);
			break;
		case 47:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"'Ello there! Would ya' like to buy some of me own",
					"knitwear? It's all the rage with the young folk. For",
					"no more than 750 coins, now I can't say fairer",
					"than that.");
			player.getInterfaceState().setNextDialogueId(0, 48);
			break;
		case 48:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT, "Sure.",
					"No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 49);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 49:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT, "Sure.");
			player.getInterfaceState().setNextDialogueId(0, 50);
			break;
		case 50:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 0, 1);
			break;
		case 51:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"No thank you.");
			break;
		case 52:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"I see you have 99 defence, well done! Would you",
					"like to buy the defence cape? It'll be 99,0000", "coins.");
			player.getInterfaceState().setNextDialogueId(0, 53);
			break;
		case 53:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"I have 99,000 coins, here you go.",
					"99,000 coins, that's outrageous.");
			player.getInterfaceState().setNextDialogueId(0, 54);
			player.getInterfaceState().setNextDialogueId(1, 56);
			break;
		case 54:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"I have 99,000 coins, here you go.");
			player.getInterfaceState().setNextDialogueId(0, 55);
			break;
		case 55:
			if (player.getInventory().getCount(995) >= 99000) {
				skillcape = player.checkForSkillcape(new Item(9753));
				hood = new Item(9755);
				if (player.getInventory().hasRoomFor(skillcape)
						&& player.getInventory().hasRoomFor(skillcape)) {
					player.getActionSender().sendDialogue(
							npc.getDefinition().getName(), DialogueType.NPC,
							npc.getDefinition().getId(),
							FacialAnimation.DEFAULT, "Here you go.");
					player.getInventory().remove(new Item(995, 99000));
					player.getInventory().add(hood);
					player.getInventory().add(skillcape);
				} else {
					player.getActionSender().sendDialogue(
							npc.getDefinition().getName(), DialogueType.NPC,
							npc.getDefinition().getId(),
							FacialAnimation.DEFAULT,
							"Perhaps you should clear some space from",
							"your inventory first.");
				}
			} else {
				player.getActionSender().sendDialogue(
						npc.getDefinition().getName(), DialogueType.NPC,
						npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"You don't have 99,000 coins.");
			}
			break;
		case 56:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"99,000 coins, that's outrageous.");
			break;
		case 57:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Hello, how may I help you?");
			player.getInterfaceState().setNextDialogueId(0, 58);
			break;
		case 58:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"What are you selling?", "Goodbye.");
			player.getInterfaceState().setNextDialogueId(0, 59);
			player.getInterfaceState().setNextDialogueId(1, 28);
			break;
		case 59:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"What are you selling?");
			player.getInterfaceState().setNextDialogueId(0, 60);
			break;
		case 60:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 0, 1);
			break;
		case 61:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Hello there, would you like to sail anywhere? It's free",
					"of charge for Jatizso, but any other exotic lands you",
					"want sailing to might cost a fair bit.");
			player.getInterfaceState().setNextDialogueId(0, 62);
			break;
		case 62:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Jatizso.", "Nowhere.");
			player.getInterfaceState().setNextDialogueId(0, 63);
			player.getInterfaceState().setNextDialogueId(1, 66);
			break;
		case 63:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Jatizso please.");
			player.getInterfaceState().setNextDialogueId(0, 64);
			break;
		case 64:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Land-ho!");
			player.getInterfaceState().setNextDialogueId(0, 65);
			break;
		case 65:
			player.getActionSender().removeChatboxInterface();
			player.setTeleportTarget(Location.create(2421, 3781, 0));
			break;
		case 66:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Nowhere, thanks for your time.");
			break;
		case 67:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Hi there. I'm offering a boating service with my",
					"husband. Would you like to go anywhere? It will cost",
					"you unless it's Neitiznot.");
			player.getInterfaceState().setNextDialogueId(0, 68);
			break;
		case 68:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Neitiznot.", "Nowhere.");
			player.getInterfaceState().setNextDialogueId(0, 69);
			player.getInterfaceState().setNextDialogueId(1, 66);
			break;
		case 69:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Neitiznot please.");
			player.getInterfaceState().setNextDialogueId(0, 70);
			break;
		case 70:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Land-ho!");
			player.getInterfaceState().setNextDialogueId(0, 71);
			break;
		case 71:
			player.getActionSender().removeChatboxInterface();
			player.setTeleportTarget(Location.create(2311, 3780, 0));
			break;
		case 72:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Hello. Would you like to buy some of my fine cuisine?");
			player.getInterfaceState().setNextDialogueId(0, 73);
			break;
		case 73:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"What are you selling?", "No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 74);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 74:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"What are you selling?");
			player.getInterfaceState().setNextDialogueId(0, 75);
			break;
		case 75:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 2, 1);
			break;
		case 76:
			player
					.getActionSender()
					.sendDialogue(
							player.getName(),
							DialogueType.ATTACK_LEVEL_UP,
							-1,
							FacialAnimation.DEFAULT,
							"  Congratulations, you have just advanced an Attack level!",
							"You have now reached level "
									+ player.getSkills().getLevelForExperience(
											Skills.ATTACK) + ".");
			break;
		case 77:
			player
					.getActionSender()
					.sendDialogue(
							player.getName(),
							DialogueType.DEFENCE_LEVEL_UP,
							-1,
							FacialAnimation.DEFAULT,
							" Congratulations, you have just advanced a Defence level!",
							"You have now reached level "
									+ player.getSkills().getLevelForExperience(
											Skills.DEFENCE) + ".");
			if (player.getSkills().getLevelForExperience(Skills.DEFENCE) > 98) {
				player.getInterfaceState().setNextDialogueId(0, 107);
			}
			break;
		case 78:
			player
					.getActionSender()
					.sendDialogue(
							player.getName(),
							DialogueType.STRENGTH_LEVEL_UP,
							-1,
							FacialAnimation.DEFAULT,
							"Congratulations, you have just advanced a Strength level!",
							"You have now reached level "
									+ player.getSkills().getLevelForExperience(
											Skills.STRENGTH) + ".");
			break;
		case 79:
			player
					.getActionSender()
					.sendDialogue(
							player.getName(),
							DialogueType.HITPOINT_LEVEL_UP,
							-1,
							FacialAnimation.DEFAULT,
							"Congratulations, you have just advanced a Hitpoints level!",
							"You have now reached level "
									+ player.getSkills().getLevelForExperience(
											Skills.HITPOINTS) + ".");
			break;
		case 80:
			player.getActionSender().sendDialogue(
					player.getName(),
					DialogueType.RANGING_LEVEL_UP,
					-1,
					FacialAnimation.DEFAULT,
					"Congratulations, you have just advanced a Ranged level!",
					"You have now reached level "
							+ player.getSkills().getLevelForExperience(
									Skills.RANGE) + ".");
			if (player.getSkills().getLevelForExperience(Skills.RANGE) > 98) {
				player.getInterfaceState().setNextDialogueId(0, 108);
			}
			break;
		case 81:
			player.getActionSender().sendDialogue(
					player.getName(),
					DialogueType.PRAYER_LEVEL_UP,
					-1,
					FacialAnimation.DEFAULT,
					"Congratulations, you have just advanced a Prayer level!",
					"You have now reached level "
							+ player.getSkills().getLevelForExperience(
									Skills.PRAYER) + ".");
			break;
		case 82:
			player.getActionSender().sendDialogue(
					player.getName(),
					DialogueType.MAGIC_LEVEL_UP,
					-1,
					FacialAnimation.DEFAULT,
					"Congratulations, you have just advanced a Magic level!",
					"You have now reached level "
							+ player.getSkills().getLevelForExperience(
									Skills.MAGIC) + ".");
			break;
		case 83:
			player.getActionSender().sendDialogue(
					player.getName(),
					DialogueType.COOKING_LEVEL_UP,
					-1,
					FacialAnimation.DEFAULT,
					"Congratulations, you have just advanced a Cooking level!",
					"You have now reached level "
							+ player.getSkills().getLevelForExperience(
									Skills.COOKING) + ".");
			break;
		case 84:
			player
					.getActionSender()
					.sendDialogue(
							player.getName(),
							DialogueType.WOODCUTTING_LEVEL_UP,
							-1,
							FacialAnimation.DEFAULT,
							"Congratulations, you have just advanced a Woodcutting level!",
							"You have now reached level "
									+ player.getSkills().getLevelForExperience(
											Skills.WOODCUTTING) + ".");
			break;
		case 85:
			player
					.getActionSender()
					.sendDialogue(
							player.getName(),
							DialogueType.FLETCHING_LEVEL_UP,
							-1,
							FacialAnimation.DEFAULT,
							"  Congratulations, you have just advanced a Fletching level!",
							"You have now reached level "
									+ player.getSkills().getLevelForExperience(
											Skills.FLETCHING) + ".");
			break;
		case 86:
			player.getActionSender().sendDialogue(
					player.getName(),
					DialogueType.FISHING_LEVEL_UP,
					-1,
					FacialAnimation.DEFAULT,
					"Congratulations, you have just advanced a Fishing level!",
					"You have now reached level "
							+ player.getSkills().getLevelForExperience(
									Skills.FISHING) + ".");
			break;
		case 87:
			player
					.getActionSender()
					.sendDialogue(
							player.getName(),
							DialogueType.FIREMAKING_LEVEL_UP,
							-1,
							FacialAnimation.DEFAULT,
							"Congratulations, you have just advanced a Firemaking level!",
							"You have now reached level "
									+ player.getSkills().getLevelForExperience(
											Skills.FIREMAKING) + ".");
			break;
		case 88:
			player
					.getActionSender()
					.sendDialogue(
							player.getName(),
							DialogueType.CRAFTING_LEVEL_UP,
							-1,
							FacialAnimation.DEFAULT,
							"Congratulations, you have just advanced a Crafting level!",
							"You have now reached level "
									+ player.getSkills().getLevelForExperience(
											Skills.CRAFTING) + ".");
			break;
		case 89:
			player
					.getActionSender()
					.sendDialogue(
							player.getName(),
							DialogueType.SMITHING_LEVEL_UP,
							-1,
							FacialAnimation.DEFAULT,
							"  Congratulations, you have just advanced a Smithing level!",
							"You have now reached level "
									+ player.getSkills().getLevelForExperience(
											Skills.SMITHING) + ".");
			break;
		case 90:
			player
					.getActionSender()
					.sendDialogue(
							player.getName(),
							DialogueType.MINING_LEVEL_UP,
							-1,
							FacialAnimation.DEFAULT,
							"  Congratulations, you have just advanced a Mining level!",
							"You have now reached level "
									+ player.getSkills().getLevelForExperience(
											Skills.MINING) + ".");
			break;
		case 91:
			player
					.getActionSender()
					.sendDialogue(
							player.getName(),
							DialogueType.HERBLORE_LEVEL_UP,
							-1,
							FacialAnimation.DEFAULT,
							"Congratulations, you have just advanced a Herblore level!",
							"You have now reached level "
									+ player.getSkills().getLevelForExperience(
											Skills.HERBLORE) + ".");
			break;
		case 92:
			player
					.getActionSender()
					.sendDialogue(
							player.getName(),
							DialogueType.AGILITY_LEVEL_UP,
							-1,
							FacialAnimation.DEFAULT,
							"Congratulations, you have just advanced an Agility level!",
							"You have now reached level "
									+ player.getSkills().getLevelForExperience(
											Skills.AGILITY) + ".");
			break;
		case 93:
			player
					.getActionSender()
					.sendDialogue(
							player.getName(),
							DialogueType.THIEVING_LEVEL_UP,
							-1,
							FacialAnimation.DEFAULT,
							"Congratulations, you have just advanced a Thieving level!",
							"You have now reached level "
									+ player.getSkills().getLevelForExperience(
											Skills.THIEVING) + ".");
			break;
		case 94:
			player.getActionSender().sendDialogue(
					player.getName(),
					DialogueType.SLAYER_LEVEL_UP,
					-1,
					FacialAnimation.DEFAULT,
					"Congratulations, you have just advanced a Slayer level!",
					"You have now reached level "
							+ player.getSkills().getLevelForExperience(
									Skills.SLAYER) + ".");
			break;
		case 95:
			player.getActionSender().sendDialogue(
					player.getName(),
					DialogueType.FARMING_LEVEL_UP,
					-1,
					FacialAnimation.DEFAULT,
					"Congratulations, you have just advanced a Farming level!",
					"You have now reached level "
							+ player.getSkills().getLevelForExperience(
									Skills.FARMING) + ".");
			break;
		case 96:
			player
					.getActionSender()
					.sendDialogue(
							player.getName(),
							DialogueType.RUNECRAFTING_LEVEL_UP,
							-1,
							FacialAnimation.DEFAULT,
							"Congratulations, you have just advanced a Runecrafting level!",
							"You have now reached level "
									+ player.getSkills().getLevelForExperience(
											Skills.RUNECRAFTING) + ".");
			break;
		case 99:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Hello. How may I help you?");
			player.getInterfaceState().setNextDialogueId(0, 100);
			break;
		case 100:
			if (player.getSkills().getLevelForExperience(Skills.RANGE) > 98) {
				player.getActionSender().sendDialogue("Select an Option",
						DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
						"What are you selling?", "Talk about range skillcape.",
						"Goodbye.");
				player.getInterfaceState().setNextDialogueId(0, 101);
				player.getInterfaceState().setNextDialogueId(1, 103);
				player.getInterfaceState().setNextDialogueId(2, 29);
			} else {
				player.getActionSender().sendDialogue("Select an Option",
						DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
						"What are you selling?", "Goodbye.");
				player.getInterfaceState().setNextDialogueId(0, 101);
				player.getInterfaceState().setNextDialogueId(1, 29);
			}
			break;
		case 101:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"What are you selling?");
			player.getInterfaceState().setNextDialogueId(0, 102);
			break;
		case 102:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 3, 1);
			break;
		case 103:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Ah, I see you have maxed the ranging skill! Bravo!",
					"I have something special for elite rangers such as",
					"yourself, would you like to buy it? It'll be",
					"99,000 coins.");
			player.getInterfaceState().setNextDialogueId(0, 104);
			break;
		case 104:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Sure, here's 99,000 coins.", "No way.");
			player.getInterfaceState().setNextDialogueId(0, 105);
			player.getInterfaceState().setNextDialogueId(1, 106);
			break;
		case 105:
			if (player.getInventory().getCount(995) >= 99000) {
				skillcape = player.checkForSkillcape(new Item(9756));
				hood = new Item(9758);
				if (player.getInventory().hasRoomFor(skillcape)
						&& player.getInventory().hasRoomFor(skillcape)) {
					player.getActionSender().sendDialogue(
							npc.getDefinition().getName(), DialogueType.NPC,
							npc.getDefinition().getId(),
							FacialAnimation.DEFAULT, "Here you go.");
					player.getInventory().remove(new Item(995, 99000));
					player.getInventory().add(hood);
					player.getInventory().add(skillcape);
				} else {
					player.getActionSender().sendDialogue(
							npc.getDefinition().getName(), DialogueType.NPC,
							npc.getDefinition().getId(),
							FacialAnimation.DEFAULT,
							"Perhaps you should clear some space from",
							"your inventory first.");
				}
			} else {
				player.getActionSender().sendDialogue(
						npc.getDefinition().getName(), DialogueType.NPC,
						npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"You don't have 99,000 coins.");
			}
			break;
		case 106:
			player.getActionSender()
					.sendDialogue(player.getName(), DialogueType.PLAYER, -1,
							FacialAnimation.DEFAULT, "No way.");
			break;
		case 107:
			player
					.getActionSender()
					.sendDialogue(
							player.getName(),
							DialogueType.MESSAGE_MODEL_LEFT,
							9753,
							FacialAnimation.DEFAULT,
							"Congratulations! You are now a master of Defence. Why not visit the Melee combat tutor on Neitiznot? He has something special that is only available to the true masters of the Defence skill!");
			break;
		case 108:
			player
					.getActionSender()
					.sendDialogue(
							player.getName(),
							DialogueType.MESSAGE_MODEL_LEFT,
							9756,
							FacialAnimation.DEFAULT,
							"Congratulations! You are now a master of Ranging. Why not visit Lowe in the northern building on Neitiznot? He has something special that is only available to the true masters of the Ranging skill!");
			break;
		case 109:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Hello there, would you like to sample my fine ores?");
			player.getInterfaceState().setNextDialogueId(0, 110);
			break;
		case 110:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT, "Sure.",
					"No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 111);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 111:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT, "Sure.");
			player.getInterfaceState().setNextDialogueId(0, 112);
			break;
		case 112:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 4, 1);
			break;
		case 113:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Interested in me bars are ya'?");
			player.getInterfaceState().setNextDialogueId(0, 114);
			break;
		case 114:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Totally.", "No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 115);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 115:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Totally.");
			player.getInterfaceState().setNextDialogueId(0, 116);
			break;
		case 116:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 5, 1);
			break;
		case 117:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Would you like to see some of my hand", "crafted items?");
			player.getInterfaceState().setNextDialogueId(0, 118);
			break;
		case 118:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT, "Yes.",
					"No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 119);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 119:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT, "Yes.");
			player.getInterfaceState().setNextDialogueId(0, 120);
			break;
		case 120:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 6, 1);
			break;
		case 121:
			player.getActionSender().sendDialogue(
					"Squire",
					DialogueType.NPC,
					606,
					FacialAnimation.DEFAULT,
					"Congratulations, " + player.getName()
							+ "! You have just completed",
					"the White Knight training course. Now it is time for you",
					"to venture out into our wide world, and defend it as an",
					"honourable White Knight.");
			player.getInterfaceState().setNextDialogueId(0, 530);
			break;
		case 122:
			player.getInventory().add(new Item(995, 35000));
			int[] items = { 1277, 1103, 1075, 841 };
			for (int ids : items) {
				player.getInventory().add(new Item(ids));
			}
			player.getInventory().add(new Item(882, 250));
			player.setCompletedTutorial(true);
			player.getActionSender().sendDialogue("Squire", DialogueType.NPC,
					606, FacialAnimation.DEFAULT,
					"Here is your complimentary starter pack.");
			player.getInterfaceState().setNextDialogueId(0, 123);
			break;
		case 123:
			if (player.getAttribute("squire") != null) {
				World.getWorld()
						.unregister((NPC) player.getAttribute("squire"));
				Location teleport = Location.create(2965, 3365, 0);
				player.setLocation(teleport);
				player.setTeleportTarget(teleport);
				player.removeAllAttributes();
				player.getActionSender().removeAllInterfaces()
						.sendDefaultChatbox().sendSidebarInterfaces();
			}
			break;
		case 124:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Hello there! Up for a quick trim or a totally",
					"new look?");
			player.getInterfaceState().setNextDialogueId(0, 125);
			break;
		case 125:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Haircut.", "Shave.", "No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 126);
			player.getInterfaceState().setNextDialogueId(1, 129);
			player.getInterfaceState().setNextDialogueId(2, 51);
			break;
		case 126:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"I want a haircut please.");
			player.getInterfaceState().setNextDialogueId(0, 127);
			break;
		case 127:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Certainly.");
			player.getInterfaceState().setNextDialogueId(0, 128);
			break;
		case 128:
			player.getActionSender().removeChatboxInterface()
					.sendConfig(261, 1).sendConfig(262, 1).sendInterface(
							204 - player.getAppearance().getGender(), true);
			player.setInterfaceAttribute("newHair", 0 + (player.getAppearance()
					.getGender() * 45));
			player.setInterfaceAttribute("newHairColour", 0);
			break;
		case 129:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"I want a shave please.");
			player.getInterfaceState().setNextDialogueId(0,
					131 - player.getAppearance().getGender());
			break;
		case 130:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"I'm sorry but women don't grow facial hair.");
			break;
		case 131:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Certainly.");
			player.getInterfaceState().setNextDialogueId(0, 132);
			break;
		case 132:
			player.getActionSender().removeChatboxInterface()
					.sendConfig(261, 1).sendConfig(262, 1).sendInterface(199,
							true);
			player.setInterfaceAttribute("newBeard", 11);
			player.setInterfaceAttribute("newHairColour", 0);
			break;
		case 133:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"*gulp* ... Hiya. Fancy changing your body for a",
					"small fee of 3,000 coins? It will reset all your",
					"clothes to their default, so bear that in mind!");
			player.getInterfaceState().setNextDialogueId(0, 134);
			break;
		case 134:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT, "Sure.",
					"No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 135);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 135:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT, "Sure.");
			player.getInterfaceState().setNextDialogueId(0, 136);
			break;
		case 136:
			player.getActionSender().removeChatboxInterface()
					.sendConfig(261, 1).sendConfig(262, 1).sendInterface(205,
							true);
			player.setInterfaceAttribute("newGender", 0);
			player.setInterfaceAttribute("newSkinColour", 0);
			break;
		case 137:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Hi there. Fancy a drink?");
			player.getInterfaceState().setNextDialogueId(0, 138);
			break;
		case 138:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"What have you got?", "No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 139);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 139:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 3, 1);
			break;
		case 140:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"My shields are amazing. Would you like to see",
					"the best ones I have?");
			player.getInterfaceState().setNextDialogueId(0, 141);
			break;
		case 141:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT, "Sure.",
					"No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 142);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 142:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 4, 1);
			break;
		case 143:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"I'm the best smith in this town. Cassie is just jealous.",
					"I'll prove it, my maces are the finest fashion work",
					"around! Would you like to see them?");
			player.getInterfaceState().setNextDialogueId(0, 144);
			break;
		case 144:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT, "Sure.",
					"No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 145);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 145:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 5, 1);
			break;
		case 146:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Hi there. Are you interested in buying some good",
					"quality chainmail?");
			player.getInterfaceState().setNextDialogueId(0, 147);
			break;
		case 147:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT, "Yes.",
					"No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 148);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 148:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 6, 1);
			break;
		case 149:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"'ay there tall stuff. Do you be needing some",
					"of the best pickaxes?");
			player.getInterfaceState().setNextDialogueId(0, 150);
			break;
		case 150:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT, "Yes.",
					"No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 151);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 151:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 7, 1);
			break;
		case 152:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Fancy takin' a look at me ore store?");
			player.getInterfaceState().setNextDialogueId(0, 153);
			break;
		case 153:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT, "Sure.",
					"No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 154);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 154:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 8, 1);
			break;
		case 155:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Heya. Do you need any runes or mage supplies?");
			player.getInterfaceState().setNextDialogueId(0, 156);
			break;
		case 156:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"What have you got?", "No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 157);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 157:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 9, 1);
			break;
		case 158:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"'Ay lad, you be talkin' to an expert on axes. Don't",
					"suppose you be needin anythin' of the sort?");
			player.getInterfaceState().setNextDialogueId(0, 159);
			break;
		case 159:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes please.", "No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 160);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 160:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 10, 1);
			break;
		case 161:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Ello ello ello! Need any fishin' gear matey?");
			player.getInterfaceState().setNextDialogueId(0, 162);
			break;
		case 162:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes please.", "No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 163);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 163:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 11, 1);
			break;
		case 164:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Hello there. Can I interest you in the best",
					"baked food in the land?");
			player.getInterfaceState().setNextDialogueId(0, 165);
			break;
		case 165:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT, "Sure.",
					"No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 166);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 166:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 12, 1);
			break;
		case 167:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Hi. Would you like me to sail you anywhere?");
			player.getInterfaceState().setNextDialogueId(0, 168);
			break;
		case 168:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Rellekka please.", "Karamja please.", "No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 169);
			player.getInterfaceState().setNextDialogueId(1, 194);
			player.getInterfaceState().setNextDialogueId(2, 51);
			break;
		case 169:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Rellekka please.");
			player.getInterfaceState().setNextDialogueId(0, 170);
			break;
		case 170:
			player.getActionSender().removeChatboxInterface();
			player.setTeleportTarget(Location.create(2629, 3693, 0));
			break;
		case 171:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Hi. Would you like me to sail you anywhere?");
			player.getInterfaceState().setNextDialogueId(0, 172);
			break;
		case 172:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Port Sarim please.", "No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 196);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 173:
			player.getActionSender().removeChatboxInterface();
			player.setTeleportTarget(Location.create(3029, 3217, 0));
			break;
		case 174:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"I only offer my goods to the best warriors,",
					"and you are by far one of the best I've seen.",
					"Do you need any battle gear brother?");
			player.getInterfaceState().setNextDialogueId(0, 175);
			break;
		case 175:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes please.", "No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 176);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 176:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 13, 1);
			break;
		case 177:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Step right up I got the best fish in all of Rellekka!");
			player.getInterfaceState().setNextDialogueId(0, 178);
			break;
		case 178:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"What have you got?", "No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 179);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 179:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 14, 1);
			break;
		case 180:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Wow, you beat my trial! Talk to me ",
					"anytime you need some Fremennik gear.");
			break;
		case 181:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"I only offer my goods to the best warriors.");
			player.getInterfaceState().setNextDialogueId(0, 182);
			break;
		case 182:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"How do I prove I'm the best?", "Bye then.");
			player.getInterfaceState().setNextDialogueId(0, 184);
			player.getInterfaceState().setNextDialogueId(1, 183);
			break;
		case 183:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Bye then.");
			break;
		case 184:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"How do I prove I'm the best?");
			player.getInterfaceState().setNextDialogueId(0, 185);
			break;
		case 185:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"You must take on my Fremennik Trial. When",
					"you are ready for combat, enter down the ladder at",
					"the back of my house. From there on your combat",
					"skills will be severely tested.");
			player.getInterfaceState().setNextDialogueId(0, 186);
			break;
		case 186:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"What happens if I die?");
			player.getInterfaceState().setNextDialogueId(0, 187);
			break;
		case 187:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"I shall return you your items.");
			break;
		case 188:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Would you like to buy some vintage", "Fremennik clothes?");
			player.getInterfaceState().setNextDialogueId(0, 189);
			break;
		case 189:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"What have you got?", "No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 190);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 190:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 15, 1);
			break;
		case 191:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"In need of the finest metal work in the land, mate?");
			player.getInterfaceState().setNextDialogueId(0, 189);
			break;
		case 192:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"What have you got?", "No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 193);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 193:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 16, 1);
			break;
		case 194:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Karamja please.");
			player.getInterfaceState().setNextDialogueId(0, 195);
			break;
		case 195:
			player.getActionSender().removeChatboxInterface();
			player.setTeleportTarget(Location.create(2956, 3146, 0));
			break;
		case 196:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Port Sarim please.");
			player.getInterfaceState().setNextDialogueId(0, 173);
			break;
		case 197:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Banana's are a great source of potassium!");
			player.getInterfaceState().setNextDialogueId(0, 198);
			break;
		case 198:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Tell me more!", "Good bye.");
			player.getInterfaceState().setNextDialogueId(0, 200);
			player.getInterfaceState().setNextDialogueId(1, 199);
			break;
		case 199:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Good bye.");
			break;
		case 200:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 17, 1);
			break;
		case 201:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"*hic* my beers are great!!!");
			player.getInterfaceState().setNextDialogueId(0, 202);
			break;
		case 202:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"What have you got?", "No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 203);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 203:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 18, 1);
			break;
		case 204:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Hello JalYt-Ket-" + player.getName() + ". You want",
					"any equipment? I sell for good price.");
			player.getInterfaceState().setNextDialogueId(0, 205);
			break;
		case 205:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"What have you got?", "No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 206);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 206:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 19, 1);
			break;
		case 207:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"What you want JalYt-Ket-" + player.getName() + "?");
			player.getInterfaceState().setNextDialogueId(0, 208);
			break;
		case 208:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"What have you got?", "No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 209);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 209:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 20, 1);
			break;
		case 210:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"You want to access your bank account,",
					"JalYt-Ket-" + player.getName() + "?");
			player.getInterfaceState().setNextDialogueId(0, 211);
			break;
		case 211:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes please.", "No thank you.");
			player.getInterfaceState().setNextDialogueId(0, 2);
			player.getInterfaceState().setNextDialogueId(1, 51);
			break;
		case 212:
			player.getActionSender().sendDialogue("TzHaar-Mej-Kah",
					DialogueType.NPC, 2618, FacialAnimation.DEFAULT,
					"Wait for my signal before fighting.");
			break;
		case 213:
			player.getActionSender().sendDialogue("TzHaar-Mej-Kah",
					DialogueType.NPC, 2618, FacialAnimation.DEFAULT, "FIGHT!");
			break;
		/**
		 * 
		 * EVERYTHING AFTER HERE WAS MADE BY CANOWNUEASY
		 * 
		 * 100% CREDITS TO CANOWNUEASY
		 * 
		 * CONTACT AT: tgpn1996@hotmail.com
		 * 
		 * 
		 */
		case 214:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Who are you?", "I'd like to be teleported.");
			player.getInterfaceState().setNextDialogueId(0, 215);
			player.getInterfaceState().setNextDialogueId(1, 226);
			break;
		case 215:
			player.getActionSender().sendDialogue("Sedridor", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"I'm Sedridor, visiting Falador.",
					"Previously I was in the Wizard's tower,",
					"however the King of Falador commanded me",
					"to provide teleportation services to Falador.");
			player.getInterfaceState().setNextDialogueId(0, 216);
			break;
		case 216:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Wow! The King of Falador!",
					"Could I use your teleportation services?");
			player.getInterfaceState().setNextDialogueId(0, 217);
			player.getInterfaceState().setNextDialogueId(1, 227);
			break;
		case 217:
			player.getActionSender().sendDialogue("Sedridor", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Yes, I am certainly honored for my position.");
			player.getInterfaceState().setNextDialogueId(0, 218);
			break;
		case 218:
			player.getActionSender().sendDialogue("Sedridor", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Anything else I could do for you?");
			player.getInterfaceState().setNextDialogueId(0, 228);
			break;
		case 219:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Could I use your teleportation services?",
					"No thank you, see you around.");
			player.getInterfaceState().setNextDialogueId(0, 223);
			player.getInterfaceState().setNextDialogueId(1, 97);
			break;
		case 220:
			player.getActionSender().sendDialogue("Where to?",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Port Sarim", "Tzhaar", "Lumbridge", "See more...",
					"Nevermind");
			player.getInterfaceState().setNextDialogueId(0, 224);
			player.getInterfaceState().setNextDialogueId(1, 225);
			player.getInterfaceState().setNextDialogueId(2, 389);
			player.getInterfaceState().setNextDialogueId(3, 393);
			player.getInterfaceState().setNextDialogueId(4, 97);
			break;
	
		
		
		
		
			/**
			 * Mage Arena
			 */
			case 222:
				if(player.getMageArena().isComplete() == true) {
					player.getActionSender().sendDialogue(npc.getDefinedName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT, "You have already complete the Challenge.");
					player.getInterfaceState().setNextDialogueId(0, 97);
				} else if(player.getSkills().getLevel(Skills.MAGIC) < 60) {
					player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.SAD, "I dont think he want's to talk to me.");
					player.getInterfaceState().setNextDialogueId(0, 97);
				} else {
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT, "Hello there. What is this place?");
				player.getInterfaceState().setNextDialogueId(0, 223);
				}
				break;
			case 223:
				player.getActionSender().sendDialogue(npc.getDefinedName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT, "I am the great Kolodion, master of battle magic, and",
						"this is my battle arena. Top wizards travel from all over",
						"Runescape to fight here.");
				player.getInterfaceState().setNextDialogueId(0, 224);
				break;
			case 224:
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT, "Can I fight here?");
				player.getInterfaceState().setNextDialogueId(0, 225);
				break;
			case 225:
				player.getActionSender().sendDialogue(npc.getDefinedName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT, "My arena is open to any high level wizard, but this is",
						"no game. Many wizards fall in this arena, never to rise",
						"again. The strongest mages have been destroyed.");
				player.getInterfaceState().setNextDialogueId(0, 226);
				break;
			case 226:
				player.getActionSender().sendDialogue(npc.getDefinedName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT, "If you're sure you want in?");
				player.getInterfaceState().setNextDialogueId(0, 227);
				break;	
			case 227:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT, 
						"Yes indeedy.",
						"No I don't.");
				player.getInterfaceState().setNextDialogueId(0, 228);
				player.getInterfaceState().setNextDialogueId(1, 229);
				break;
			case 229:
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT, "No I don't.");
				player.getInterfaceState().setNextDialogueId(0, 97);
				break;
			case 228:
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT, "Yes indeedy.");
				player.getInterfaceState().setNextDialogueId(0, 230);
				break;
			case 230:
				player.getActionSender().sendDialogue(npc.getDefinedName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT, "Good, good. You have a healthy sense of competition.");
				player.getInterfaceState().setNextDialogueId(0, 231);
				break;		
			case 231:
				player.getActionSender().sendDialogue(npc.getDefinedName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT, "Remember, traveller - in my arena, hand-to-hand",
						"combat is useless. Your strength will diminish as you",
						"enter the arena, but the spells you can learn are",
						"amongst the most powerful in all of Runescape.");
				player.getInterfaceState().setNextDialogueId(0, 232);
				break;	
			case 232:
				player.getActionSender().sendDialogue(npc.getDefinedName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT, "Before I can accept you in, we must duel.");
				player.getInterfaceState().setNextDialogueId(0, 233);
				break;	
			case 233:
				player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT, 
						"Okay, let's fight.",
						"No thanks.");
				player.getInterfaceState().setNextDialogueId(0, 235);
				player.getInterfaceState().setNextDialogueId(1, 234);
				break;
			case 234:
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT, "No thanks.");
				player.getInterfaceState().setNextDialogueId(0, 97);
				break;	
			case 235:
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT, "Okay, let's fight.");
				player.getInterfaceState().setNextDialogueId(0, 236);
				break;
			case 236:
				player.getActionSender().sendDialogue(npc.getDefinedName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT, "I must check that you are up to scratch.");
				player.getInterfaceState().setNextDialogueId(0, 237);
				break;
			case 237:
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT, "You don't need to worry, about that.");
				player.getInterfaceState().setNextDialogueId(0, 238);
				break;
			case 238:
				player.getActionSender().sendDialogue(npc.getDefinedName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT, "Not just any magician can enter - only the most",
						"powerful and most feared. Before you can use the",
						"power of the arena, you must prove yourself against",
						"me.");
				player.getInterfaceState().setNextDialogueId(0, 239);
				break;
			case 239:
				player.getActionSender().removeChatboxInterface();
				player.getMageArena().begin();
				break;
			case 240:
				if(player.getSkills().getLevel(Skills.MAGIC) < 60) {
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.SAD, "I dont think he want's to talk to me.");
				player.getInterfaceState().setNextDialogueId(0, 97);
				}
				break;
			case 1000:
				player.setTeleportTarget(Location.create(3551, 9690, 0));
				player.getActionSender().removeChatboxInterface();
				break;
		case 241:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.MORE_SAD,
					"That's terrible! Talk to me anytime you feel lonely.");
			player.getInterfaceState().setNextDialogueId(0, 242);
			break;
		case 242:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.ALMOST_CRYING,
					"Thanks I suppose.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 243:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.LAUGH_1,
					"I'm feeling joyful and delighted!");
			player.getInterfaceState().setNextDialogueId(0, 238);
			break;
		case 244:
			player.getActionSender().sendDialogue(
					"Estate angent",
					DialogueType.NPC,
					npc.getDefinition().getId(),
					FacialAnimation.HAPPY,
					"Hello. And welcome to the " + Constants.SERVER_NAME
							+ " housing agency!", "What can I do for you?");
			player.getInterfaceState().setNextDialogueId(0, 245);
			break;
		case 245:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"I'd like to purchase a house.",
					"Inform my mind on realty.",
					"How much money is my house worth?",
					"Tell me about that skillcape you're wearing.",
					"I don't need anything, sorry for bugging you.");
			player.getInterfaceState().setNextDialogueId(0, 246);
			player.getInterfaceState().setNextDialogueId(1, 253);
			player.getInterfaceState().setNextDialogueId(2, 256);
			player.getInterfaceState().setNextDialogueId(3, 257);
			player.getInterfaceState().setNextDialogueId(4, 260);
			break;
		case 246:
			player.getActionSender().sendDialogue("Estate angent",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.HAPPY,
					"Oh that's wonderful! Houses are great, however",
					"require a lot of work and are very limited. We can",
					"provide only one house per person. Different homes cost",
					"more for maintenance and prime cost, so choose wisely!");
			player.getInterfaceState().setNextDialogueId(0, 247);
			break;
		case 247:
			player.getActionSender().sendDialogue("Select a House",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"House 1: For 300K prime, and 12K maintenance.",
					"House 2: For 400K prime, and 2K maintenance.",
					"House 3: For 10M prime, and 100K maintenance.");
			player.getInterfaceState().setNextDialogueId(0, 249);
			player.getInterfaceState().setNextDialogueId(1, 250);
			player.getInterfaceState().setNextDialogueId(2, 251);
			break;
		case 248:
			player.getActionSender().sendDialogue("Estate angent",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.SAD, "Oh, due to recent news,",
					"Houses are currently off the market.",
					"I am very sorry, however check daily to be",
					"Sure you purchase the house of your dreams!");
			player.getInterfaceState().setNextDialogueId(0, 252);
			break;
		case 249:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"I'd like to purchase house 1.");
			player.getInterfaceState().setNextDialogueId(0, 248);
			break;
		case 250:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"I'd like to purchase house 2.");
			player.getInterfaceState().setNextDialogueId(0, 248);
			break;
		case 251:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"I'd like to purchase house 3.");
			player.getInterfaceState().setNextDialogueId(0, 248);
			break;
		case 252:
			player.getActionSender().sendDialogue("Estate angent",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.DEFAULT, "Anything else you need?");
			player.getInterfaceState().setNextDialogueId(0, 245);
			break;
		case 253:
			player.getActionSender().sendDialogue("Estate angent",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.HAPPY,
					"Realty is like lottery, the more cash you",
					"put in, the more likely to get money back.",
					"Customers (you) buy homes for a set price with",
					"maintenance costs to be paid every 24 hours.");
			player.getInterfaceState().setNextDialogueId(0, 252);
			break;
		case 254:
			player.getActionSender().sendDialogue("Estate angent",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.DEFAULT,
					"One moment please, I am searching",
					"my records for your home's estimated price.");
			player.getInterfaceState().setNextDialogueId(0, 255);
			break;
		case 255:
			player.getActionSender().sendDialogue("Estate angent",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.DEFAULT,
					"It seems that you currently do not own a home.");
			player.getInterfaceState().setNextDialogueId(0, 252);
			break;
		case 256:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"How much would my house be estimated to sell at?");
			player.getInterfaceState().setNextDialogueId(0, 254);
			break;
		case 257:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Tell me about that skillcape you're wearing.");
			player.getInterfaceState().setNextDialogueId(0, 258);
			break;
		case 258:
			player
					.getActionSender()
					.sendDialogue(
							"Estate angent",
							DialogueType.NPC,
							npc.getDefinition().getId(),
							FacialAnimation.DEFAULT,
							"As you may know, skillcapes are only available to",
							"masters in a skill. I have spent my entire life building",
							"houses and now I spend my time selling them! As a",
							"sign of my abilities I wear this Skillcape of Construction.");
			player.getInterfaceState().setNextDialogueId(0, 259);
			break;
		case 259:
			player.getActionSender().sendDialogue("Estate angent",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.DEFAULT,
					"If you ever reach a construction level of 99,",
					"come and see me and I will sell you a copy",
					"of my Skillcape of Construction for a solid",
					"price of 99,000 gold coins.");
			player.getInterfaceState().setNextDialogueId(0, 252);
			break;
		case 260:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"I don't need anything, sorry for bugging you.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 261:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT, "Hello.");
			player.getInterfaceState().setNextDialogueId(0, 262);
			break;
		case 262:
			player.getActionSender().sendDialogue("Sir Tiffy Cashien",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.HAPPY, "What ho, sirrah.",
					"Spiffing day for a walk in the park, what?");
			player.getInterfaceState().setNextDialogueId(0, 263);
			break;
		case 263:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"Spiffing?");
			player.getInterfaceState().setNextDialogueId(0, 264);
			break;
		case 264:
			player.getActionSender().sendDialogue("Sir Tiffy Cashien",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.HAPPY, "Absolutely top-hole!",
					"Well, can't stay and chat all day, dontchaknow!",
					"Ta-ta for now!");
			player.getInterfaceState().setNextDialogueId(0, 265);
			break;
		case 265:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"Urm...goodbye.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 266:
			player.getActionSender().sendDialogue("Dwarf", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Welcome to the Mining Guild.",
					"Could I help you with anything?");
			player.getInterfaceState().setNextDialogueId(0, 267);
			break;
		case 267:
			player.getActionSender().sendDialogue(
					"What would you like to say?", DialogueType.OPTION, -1,
					FacialAnimation.DEFAULT, "What have you got in the guild?",
					"What do you dwarves do with the ore you mine?",
					"Can you tell me about your skillcape?",
					"No thanks, I'm fine.");
			player.getInterfaceState().setNextDialogueId(0, 268);
			player.getInterfaceState().setNextDialogueId(1, 274);
			player.getInterfaceState().setNextDialogueId(2, 277);
			player.getInterfaceState().setNextDialogueId(3, 273);
			break;
		case 268:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"What have you got in the guild?");
			player.getInterfaceState().setNextDialogueId(0, 269);
			break;
		case 269:
			player
					.getActionSender()
					.sendDialogue(
							"Dwarf",
							DialogueType.NPC,
							npc.getDefinition().getId(),
							FacialAnimation.HAPPY,
							"Ooh, it's WONDERFUL! There are lots of coal rocks,",
							"and even a few mithril rocks in the guild,",
							"all exclusively for people with at least level 60 mining.",
							"There's no better mining site anywhere near here.");
			if (player.getSkills().getLevelForExperience(Skills.MINING) < 60) {
				player.getInterfaceState().setNextDialogueId(0, 270);
			} else {
				player.getInterfaceState().setNextDialogueId(0, 279);
			}
			break;
		case 270:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.SAD,
					"So you won't let me go in there?");
			player.getInterfaceState().setNextDialogueId(0, 271);
			break;
		case 271:
			player.getActionSender().sendDialogue("Dwarf", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.SAD,
					"Sorry, but rules are rules. Do some more training",
					"first. Can I help you with anything else?");
			player.getInterfaceState().setNextDialogueId(0, 272);
			break;
		case 272:
			player.getActionSender().sendDialogue(
					"What would you like to say?", DialogueType.OPTION, -1,
					FacialAnimation.DEFAULT, "Yes please.",
					"No thanks, I'm fine.");
			player.getInterfaceState().setNextDialogueId(0, 267);
			player.getInterfaceState().setNextDialogueId(1, 273);
			break;
		case 273:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DISINTERESTED,
					"No thanks, I'm fine.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 274:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"What do you dwarves do with the ore you mine?");
			player.getInterfaceState().setNextDialogueId(0, 275);
			break;
		case 275:
			player.getActionSender().sendDialogue("Dwarf", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.LAUGH_3,
					"What do you think? We smelt it into bars, smith the",
					"metal to make armour and weapons, the we exchange",
					"them for goods and services.");
			player.getInterfaceState().setNextDialogueId(0, 276);
			break;
		case 276:
			player.getActionSender().sendDialogue("Dwarf", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DISTRESSED,
					"Anything else I could help you with?");
			player.getInterfaceState().setNextDialogueId(0, 272);
			break;
		case 277:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"Can you tell me about your skillcape?");
			player.getInterfaceState().setNextDialogueId(0, 278);
			break;
		case 278:
			player.getActionSender().sendDialogue("Dwarf", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Sure, this is a Skillcape of Mining. It shows my stature",
					"as a master miner! It has all sorts of uses including a",
					"skill boost to my Mining skill. When you get to level 99",
					"come and talk to me and I'll sell you one.");
			player.getInterfaceState().setNextDialogueId(0, 276);
			break;
		case 279:
			player.getActionSender().sendDialogue("Dwarf", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Of course! As I previously said, all people",
					"with level 60 or more Mining can enter the guild.");
			player.getInterfaceState().setNextDialogueId(0, 276);
			break;
		case 280:
			player.getActionSender().sendDialogue("Town crier",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.HAPPY,
					"Hear ye! Hear ye! Player Moderators massive help to",
					"Innovat-");
			player.getInterfaceState().setNextDialogueId(0, 281);
			break;
		case 281:
			player.getActionSender().sendDialogue("Town crier",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.DEFAULT,
					"Oh, hello citizen. Are you here to find out about Player",
					"Moderators? Or perhaps you would like to know about",
					"the laws of the land?");
			player.getInterfaceState().setNextDialogueId(0, 282);
			break;
		case 282:
			player.getActionSender()
					.sendDialogue(
							"Select an Option",
							DialogueType.OPTION,
							-1,
							FacialAnimation.DEFAULT,
							"Tell me about Player Moderators.",
							"Tell me about the Rules of "
									+ Constants.SERVER_NAME + ".",
							"Could you give me a quick tip?",
							"Nevermind, sorry to bother.");
			player.getInterfaceState().setNextDialogueId(0, 283);
			player.getInterfaceState().setNextDialogueId(1, 294);
			player.getInterfaceState().setNextDialogueId(2, 285);
			player.getInterfaceState().setNextDialogueId(3, 293);
			break;
		case 283:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"Tell me about Player Moderators.");
			player.getInterfaceState().setNextDialogueId(0, 284);
			break;
		case 284:
			player.getActionSender().sendDialogue("Town crier",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.HAPPY,
					"Player Moderators are normal players of the game,",
					"just like you. However, since they have shown",
					"themselves to be trustworthy, they have been");
			player.getInterfaceState().setNextDialogueId(0, 295);
			break;
		case 285:
			player.getActionSender().sendDialogue("Town crier",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.DEFAULT,
					"Take time to check the second trade window carefully.",
					"Don't be scammed!");
			player.getInterfaceState().setNextDialogueId(0, 282);
			break;
		case 286:
			player.getActionSender().sendDialogue("Town crier",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.DISTRESSED,
					"Is there anything else you'd like to know?");
			player.getInterfaceState().setNextDialogueId(0, 287);
			break;
		case 287:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes please.", "No thanks, I'm fine.");
			player.getInterfaceState().setNextDialogueId(0, 288);
			player.getInterfaceState().setNextDialogueId(1, 273);
			break;
		case 288:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Yes please.");
			player.getInterfaceState().setNextDialogueId(0, 282);
			break;
		case 289:
			player.getActionSender().sendDialogue("Town crier",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.DEFAULT,
					"Remember, if there is no silver crown next to",
					"their name, they are not a Player Moderator!",
					"Do you need anymore help?");
			player.getInterfaceState().setNextDialogueId(0, 290);
			break;
		case 290:
			player.getActionSender().sendDialogue("Select what to say",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"I've seen players with gold crowns!",
					"No thanks, I'm fine.");
			player.getInterfaceState().setNextDialogueId(0, 291);
			player.getInterfaceState().setNextDialogueId(1, 273);
			break;
		case 291:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"I've seen players with gold crowns!");
			player.getInterfaceState().setNextDialogueId(0, 292);
			break;
		case 292:
			player.getActionSender().sendDialogue("Town crier",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.DEFAULT,
					"Oh yes. They are official " + Constants.SERVER_NAME,
					"staff members. They serve in similar roles to",
					"Player Moderators, however much more power.");
			player.getInterfaceState().setNextDialogueId(0, 296);
			break;
		case 293:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DISINTERESTED,
					"Nevermind, sorry to bother.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 294:
			player.getActionSender()
					.sendDialogue(
							player.getName(),
							DialogueType.PLAYER,
							-1,
							FacialAnimation.HAPPY,
							"Tell me about the Rules of "
									+ Constants.SERVER_NAME + ".");
			player.getInterfaceState().setNextDialogueId(0, 297);
			break;
		case 295:
			player
					.getActionSender()
					.sendDialogue(
							"Town crier",
							DialogueType.NPC,
							npc.getDefinition().getId(),
							FacialAnimation.HAPPY,
							"invited by " + Constants.SERVER_NAME
									+ " staff to monitor",
							"the game and take appropriate action to rule breakers.");
			player.getInterfaceState().setNextDialogueId(0, 289);
			break;
		case 296:
			player.getActionSender().sendDialogue("Town crier",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.CALM_1,
					"Disrespect to these players can get you",
					"in serious trouble, so think about what you say!");
			player.getInterfaceState().setNextDialogueId(0, 286);
			break;
		case 297:
			player.getActionSender().sendDialogue("Town crier",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.CALM_1,
					"Always respect others - people have feelings!",
					"Don't use cheats, hacks, or macro! It is quite",
					"easy to detect by other players and is a serious",
					"offense. You can find more rules at " + Constants.WEBSITE);
			player.getInterfaceState().setNextDialogueId(0, 286);
			break;
		case 298:
			player.getActionSender().sendDialogue("Gypsy Aris",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.CALM_1, "Greetings young one.");
			player.getInterfaceState().setNextDialogueId(0, 299);
			break;
		case 299:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Hello, I'm interested in slaying a dragon.",
					"Could you possibly give me a task?");
			player.getInterfaceState().setNextDialogueId(0, 300);
			break;
		case 300:
			player.getActionSender().sendDialogue("Gypsy Aris",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.HAPPY,
					"Sorry but Dragon Slayer is currently under",
					"development. Check back soon to see if it's",
					"been completed and I'll surely give you a task!");
			player.getInterfaceState().setNextDialogueId(0, 301);
			break;
		case 301:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.SAD,
					"Awhh I wanted to slay a dragon so bad!",
					"Well I'll talk to you later.");
			player.getInterfaceState().setNextDialogueId(0, 302);
			break;
		case 302:
			player.getActionSender().sendDialogue("Gypsy Aris",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.CALM_1, "Ta-ta.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 303:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.HAPPY, "Hello.");
			player.getInterfaceState().setNextDialogueId(0, 304);
			break;
		case 304:
			player.getActionSender().sendDialogue("Master Smithing Tutor",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.CALM_1,
					"Hello! Welcome to my workshop. My name is Sani. Feel",
					"free to use my anvils, I can't use them all at", "once!");
			player.getInterfaceState().setNextDialogueId(0, 305);
			break;
		case 305:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Thanks! But I've talked to you because I",
					"want to learn about the art of smithing!");
			player.getInterfaceState().setNextDialogueId(0, 306);
			break;
		case 306:
			player.getActionSender().sendDialogue("Master Smithing Tutor",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.LAUGH_1,
					"Haha! You think smithing is an art? Smithing",
					"is life - simple life.");
			player.getInterfaceState().setNextDialogueId(0, 307);
			break;
		case 307:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.SAD,
					"Well it seems pretty tricky to me.");
			player.getInterfaceState().setNextDialogueId(0, 308);
			break;
		case 308:
			player.getActionSender().sendDialogue("Master Smithing Tutor",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.HAPPY,
					"No worries! I offer much advice on smithing.",
					"What would you like to know?");
			player.getInterfaceState().setNextDialogueId(0, 309);
			break;
		case 309:
			player.getActionSender().sendDialogue("Select what to say",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Tell me about Smithing.",
					"I'd like to know about Smithing skillcapes.",
					"Nevermind, sorry to bother.");
			player.getInterfaceState().setNextDialogueId(0, 310);
			player.getInterfaceState().setNextDialogueId(1, 317);
			player.getInterfaceState().setNextDialogueId(2, 293);
			break;
		case 310:
			player.getActionSender().sendDialogue("Master Smithing Tutor",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.HAPPY,
					"Smithing befriends mining. Hardworking people",
					"dig ore to sell to us smithers. No worrys though,",
					"you can mine your own ore as well!");
			player.getInterfaceState().setNextDialogueId(0, 311);
			break;
		case 311:
			player.getActionSender().sendDialogue("Master Smithing Tutor",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.HAPPY,
					"To smith, simply gather the required ores and",
					"smelt them into a bar. With these bars you can",
					"grab a hammer, come to my anvils (or any anvils)",
					"and use the bars onto the anvil. You should");
			player.getInterfaceState().setNextDialogueId(0, 312);
			break;
		case 312:
			player.getActionSender().sendDialogue("Master Smithing Tutor",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.HAPPY,
					"See a display of weapon or armoury you can create!",
					"With these freshly made items, you can use them for",
					"yourself of even sell them to others for a profit!");
			player.getInterfaceState().setNextDialogueId(0, 314);
			break;
		case 313:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Tell me about Smithing.");
			player.getInterfaceState().setNextDialogueId(0, 310);
			break;
		case 314:
			player.getActionSender().sendDialogue("Master Smithing Tutor",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.DISTRESSED,
					"Could I help you with anything else?");
			player.getInterfaceState().setNextDialogueId(0, 315);
			break;
		case 315:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes please.", "No thanks, I'm fine.");
			player.getInterfaceState().setNextDialogueId(0, 316);
			player.getInterfaceState().setNextDialogueId(1, 273);
			break;
		case 316:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Yes please.");
			player.getInterfaceState().setNextDialogueId(0, 309);
			break;
		case 317:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"I'd like to know about Smithing skillcapes.");
			player.getInterfaceState().setNextDialogueId(0, 318);
			break;
		case 318:
			if (player.getSkills().getLevelForExperience(Skills.SMITHING) < 99) {
				player
						.getActionSender()
						.sendDialogue(
								"Master Smithing Tutor",
								DialogueType.NPC,
								npc.getDefinition().getId(),
								FacialAnimation.CALM_2,
								"Smithing skillcapes are worn by masters of the skill.",
								"They represent true expertness in Smithing. If you",
								"ever reach a smithing level of 99, talk to me and I'd",
								"gladly sell you one for a price of 99,000 gold coins.");
				player.getInterfaceState().setNextDialogueId(0, 314);
			} else {
				player
						.getActionSender()
						.sendDialogue(
								"Master Smithing Tutor",
								DialogueType.NPC,
								npc.getDefinition().getId(),
								FacialAnimation.CALM_2,
								"Smithing skillcapes are worn by masters of the skill.",
								"They represent true expertness in Smithing. It appears",
								"that you have the ability to wear one! Would you like",
								"to buy a Skillcape of Smithing for 99,000 gold coins?");
				player.getInterfaceState().setNextDialogueId(0, 319);
			}
			break;
		case 319:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes, I want to be recognized as a true Smither!",
					"No, I don't think I'd need one.");
			player.getInterfaceState().setNextDialogueId(0, 321);
			player.getInterfaceState().setNextDialogueId(1, 320);
			break;
		case 320:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DISINTERESTED,
					"No, I don't think I'd need one.");
			player.getInterfaceState().setNextDialogueId(0, 314);
			break;
		case 321:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Yes, I want to be recognized as a true Smither!");
			player.getInterfaceState().setNextDialogueId(0, 322);
			break;
		case 322:
			Item smithingSkillcape = new Item(9795);
			if (player.getInventory().getCount(995) >= 99000
					&& player.getInventory().hasRoomFor(smithingSkillcape)) {
				player.getInventory().remove(new Item(995, 99000));
				player.getInventory().add(smithingSkillcape);
				player.getActionSender().sendDialogue("Master Smithing Tutor",
						DialogueType.NPC, npc.getDefinition().getId(),
						FacialAnimation.HAPPY, "Enjoy!");
				player.getInterfaceState().setNextDialogueId(0, 314);
			} else if (player.getInventory().getCount(995) < 99000) {
				player
						.getActionSender()
						.sendDialogue(
								"Master Smithing Tutor",
								DialogueType.NPC,
								npc.getDefinition().getId(),
								FacialAnimation.CALM_1,
								"Sorry but you do not have enough money on you",
								"to pay for a Skillcape of Smithing. If you'd like",
								"one please bring 99,000 gold coins in your inventory.");
				player.getInterfaceState().setNextDialogueId(0, 314);
			} else if (player.getInventory().getCount(995) >= 99000
					& !player.getInventory().hasRoomFor(smithingSkillcape)) {
				player
						.getActionSender()
						.sendDialogue(
								"Master Smithing Tutor",
								DialogueType.NPC,
								npc.getDefinition().getId(),
								FacialAnimation.CALM_1,
								"Sorry but you do not have enough inventory space",
								"to put your Skillcape of Smithing. You need at least",
								"one free inventory slot to buy a Skillcape of Smithing.");
				player.getInterfaceState().setNextDialogueId(0, 314);
			}
			break;
		case 323:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Hello, are you okay?");
			player.getInterfaceState().setNextDialogueId(0, 324);
			break;
		case 324:
			player.getActionSender().sendDialogue("Gertrude", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.ANGER_1,
					"Do I look okay? Those kids drive me crazy.");
			player.getInterfaceState().setNextDialogueId(0, 325);
			break;
		case 325:
			player.getActionSender().sendDialogue("Gertrude", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.ALMOST_CRYING,
					"I'm sorry, it's just that I've lost her.");
			player.getInterfaceState().setNextDialogueId(0, 326);
			break;
		case 326:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"Lost whom?");
			player.getInterfaceState().setNextDialogueId(0, 327);
			break;
		case 327:
			player.getActionSender().sendDialogue("Gertrude", DialogueType.NPC,
					npc.getDefinition().getId(),
					FacialAnimation.BOWS_HEAD_WHILE_SAD,
					"Fluffs, poor Fluffs. She never hurt anyone.");
			player.getInterfaceState().setNextDialogueId(0, 328);
			break;
		case 328:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"Who's Fluffs?");
			player.getInterfaceState().setNextDialogueId(0, 329);
			break;
		case 329:
			player.getActionSender().sendDialogue("Gertrude", DialogueType.NPC,
					npc.getDefinition().getId(),
					FacialAnimation.BOWS_HEAD_WHILE_SAD,
					"My beloved feline friend, Fluffs. She's been purring by",
					"my side for almost a decade. Please, could you go and",
					"search for her while I take care of the children?");
			player.getInterfaceState().setNextDialogueId(0, 330);
			break;
		case 330:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Well, I suppose I could, though I'll need more details.",
					"What's in it for me?",
					"Sorry, I'm too busy to play pet rescue.");
			player.getInterfaceState().setNextDialogueId(0, 340);
			player.getInterfaceState().setNextDialogueId(1, 333);
			player.getInterfaceState().setNextDialogueId(2, 331);
			break;
		case 331:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DISINTERESTED,
					"Sorry, I'm too busy to play pet rescue.");
			player.getInterfaceState().setNextDialogueId(0, 332);
			break;
		case 332:
			player.getActionSender().sendDialogue("Gertrude", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.NEARLY_CRYING,
					"Well, okay then, I'll have to find someone else; some",
					"less heartless. It will be on your conscience if a poor",
					"kitty is lost in the wilds though.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 333:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"What's in it for me?");
			player.getInterfaceState().setNextDialogueId(0, 334);
			break;
		case 334:
			player.getActionSender().sendDialogue("Gertrude", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.NEARLY_CRYING,
					"I'm sorry, I'm too poor to pay you anything, the best I",
					"could offer is a warm meal.");
			player.getInterfaceState().setNextDialogueId(0, 335);
			break;
		case 335:
			player.getActionSender().sendDialogue("Gertrude", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.CALM_1,
					"So, can you help?");
			player.getInterfaceState().setNextDialogueId(0, 336);
			break;
		case 336:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"Just a meal? It's not the best offer I've had but I",
					"suppose I can help.");
			player.getInterfaceState().setNextDialogueId(0, 337);
			break;
		case 337:
			player.getActionSender().sendDialogue("Gertrude", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.NEARLY_CRYING,
					"I suppose I could give you some nice, yummy chocolate",
					"cake; maybe even a kitten too, if you seem like a nice",
					"sort.");
			player.getInterfaceState().setNextDialogueId(0, 338);
			break;
		case 338:
			player.getActionSender().sendDialogue("Gertrude", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DISTRESSED,
					"Is that something you could be persuaded with?");
			player.getInterfaceState().setNextDialogueId(0, 339);
			break;
		case 339:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Well, I suppose so.",
					"Sorry, I'm too busy to play pet rescue.");
			player.getInterfaceState().setNextDialogueId(0, 340);
			player.getInterfaceState().setNextDialogueId(1, 331);
			break;
		case 340:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.CALM_1,
					"Well, I suppose I could, though I'd need more details.");
			player.getInterfaceState().setNextDialogueId(0, 341);
			break;
		case 341:
			player.getActionSender().sendDialogue("Gertrude", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Really? Thank you so much! I really have no idea",
					"where she could be!");
			player.getInterfaceState().setNextDialogueId(0, 342);
			break;
		case 342:
			player.getActionSender().sendDialogue("Gertrude", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.CALM_2,
					"I think my sons, Philop and Wilough, saw the cat last.",
					"They'll be out in the marketplace.");
			player.getInterfaceState().setNextDialogueId(0, 343);
			break;
		case 343:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"The marketplace? Which one would that be? It would",
					"help to know what they get up to, as well.");
			player.getInterfaceState().setNextDialogueId(0, 344);
			break;
		case 344:
			player.getActionSender().sendDialogue("Gertrude", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Really? Well, I generally let them do what they want,",
					"so I've got no idea exactly what they would be doing.",
					"They are good lads, though. I'm sure they are just",
					"watching the passer-by in Varrock Marketplace.");
			player.getInterfaceState().setNextDialogueId(0, 345);
			break;
		case 345:
			player.getActionSender().sendDialogue("Gertrude", DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Oh, to be young and carefree again!");
			player.getInterfaceState().setNextDialogueId(0, 346);
			break;
		case 346:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Alright then, I'll see what I can do. Two young lads in",
					"Varrock Marketplace; I can only hope that there's no",
					"school trip passing through when I arrive.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 347:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Hello, what's your name?");
			player.getInterfaceState().setNextDialogueId(0, 348);
			break;
		case 348:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(),
					FacialAnimation.DELIGHTED_EVIL, "Gwwrr!");
			player.getInterfaceState().setNextDialogueId(0, 349);
			break;
		case 349:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.CALM_2,
					"Err, hello there. What's that you have there?");
			player.getInterfaceState().setNextDialogueId(0, 350);
			break;
		case 350:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(),
					FacialAnimation.DELIGHTED_EVIL,
					"Gwwwrrr! Dwa-gon! Gwwwwrrrr!");
			player.getInterfaceState().setNextDialogueId(0, 351);
			break;
		case 351:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.CALM_2,
					"That's a nice dragon.");
			player.getInterfaceState().setNextDialogueId(0, 352);
			break;
		case 352:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(),
					FacialAnimation.DELIGHTED_EVIL,
					"Gwwrr! Dwa-gon eat Fluffs! Chomp! Chomp!");
			player.getInterfaceState().setNextDialogueId(0, 353);
			break;
		case 353:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.SAD,
					"That wasn't such a helpful clue.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 354:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Hello. What are you doing here?");
			player.getInterfaceState().setNextDialogueId(0, 355);
			break;
		case 355:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"I'm looking for whoever is in charge of this place.",
					"I have come to kill everyone in the castle!",
					"I don't know. I'm lost. Where am I?");
			player.getInterfaceState().setNextDialogueId(0, 356);
			player.getInterfaceState().setNextDialogueId(1, 358);
			player.getInterfaceState().setNextDialogueId(2, 360);
			break;
		case 356:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"I'm looking for whoever is in charge of this place.");
			player.getInterfaceState().setNextDialogueId(0, 357);
			break;
		case 357:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Who, the Duke? He's in his study, on the first floor.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 358:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.ANGER_4,
					"I have come to kill everyone in the castle!");
			player.getInterfaceState().setNextDialogueId(0, 359);
			break;
		case 359:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.NEARLY_CRYING,
					"Oh dear!");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 360:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"I don't know. I'm lost. Where am I?");
			player.getInterfaceState().setNextDialogueId(0, 361);
			break;
		case 361:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.CALM_2,
					"You are in Lumbridge castle.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 362:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.CALM_1,
					"Dooooom!");
			player.getInterfaceState().setNextDialogueId(0, 363);
			break;
		case 363:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.SAD, "Where?");
			player.getInterfaceState().setNextDialogueId(0, 364);
			break;
		case 364:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.CALM_1,
					"All around us! I can feel it in the air, hear it in the",
					"wind, smell it...also in the air!");
			player.getInterfaceState().setNextDialogueId(0, 365);
			break;
		case 365:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.ALMOST_CRYING,
					"Is there anything we can do about this doom?");
			player.getInterfaceState().setNextDialogueId(0, 366);
			break;
		case 366:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.CALM_1,
					"There is nothing you need to do my friend! I am the",
					"Doomsayer, although my real title could be something",
					"like the Danger Tutor.");
			player.getInterfaceState().setNextDialogueId(0, 367);
			break;
		case 367:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"Danger tutor?");
			player.getInterfaceState().setNextDialogueId(0, 368);
			break;
		case 368:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Yes! I roam the world sensing danger.");
			player.getInterfaceState().setNextDialogueId(0, 369);
			break;
		case 369:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"If I find a dangerous area, then I put up warning",
					"signs that will tell you what is so dangerous about that",
					"area.");
			player.getInterfaceState().setNextDialogueId(0, 370);
			break;
		case 370:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Thanks! Now I know when I could be facing trouble.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 371:
			player.getActionSender().sendDialogue("Lumbridge Guide",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.HAPPY,
					"Greetings, adventurer. How may I help you?");
			player.getInterfaceState().setNextDialogueId(0, 372);
			break;
		case 372:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Who are you?", "Tell me about the town of Lumbridge.",
					"I'm fine for now, thanks.");
			player.getInterfaceState().setNextDialogueId(0, 374);
			player.getInterfaceState().setNextDialogueId(1, 377);
			player.getInterfaceState().setNextDialogueId(2, 373);
			break;
		case 373:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"I'm fine for now, thanks.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 374:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"Who are you?");
			player.getInterfaceState().setNextDialogueId(0, 375);
			break;
		case 375:
			player.getActionSender().sendDialogue("Lumbridge Guide",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.CALM_1,
					"I am Phileas, the Lumbridge Guide. In times past, people",
					"come from all around to ask me for advice. My renown",
					"seems to have diminished somewhat in recent years,",
					"though. Can I help you with anything?");
			player.getInterfaceState().setNextDialogueId(0, 376);
			break;
		case 376:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Tell me about the town of Lumbridge.",
					"I'm fine for now, thanks.");
			player.getInterfaceState().setNextDialogueId(0, 377);
			player.getInterfaceState().setNextDialogueId(1, 373);
			break;
		case 377:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"Tell me about the town of Lumbridge.");
			player.getInterfaceState().setNextDialogueId(0, 378);
			break;
		case 378:
			player.getActionSender().sendDialogue("Lumbridge Guide",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.DEFAULT,
					"Lumbridge is one of the older towns in the human-",
					"controlled kingdoms. It was founded over two hundred",
					"years ago towards the end of the Fourth Age. It's",
					"called Lumbridge because of this bridge built over the");
			player.getInterfaceState().setNextDialogueId(0, 379);
			break;
		case 379:
			player.getActionSender().sendDialogue("Lumbridge Guide",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.DEFAULT,
					"River Lum. The town is governed by Duke Horacio,",
					"who is a good friend of our monarch, King Roald of",
					"Misthalin.");
			player.getInterfaceState().setNextDialogueId(0, 380);
			break;
		case 380:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.HAPPY, "Goodbye.");
			player.getInterfaceState().setNextDialogueId(0, 381);
			break;
		case 381:
			player.getActionSender().sendDialogue("Lumbridge Guide",
					DialogueType.NPC, npc.getDefinition().getId(),
					FacialAnimation.HAPPY, "Good adventuring, traveller.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 382:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Hello ghost, how are you?");
			player.getInterfaceState().setNextDialogueId(0, 383);
			break;
		case 383:
			if (player.getEquipment().contains(552)) {
				player.getActionSender().sendDialogue(
						npc.getDefinition().getName(), DialogueType.NPC,
						npc.getDefinition().getId(),
						FacialAnimation.DISTRESSED,
						"I'm doing swell. What do you need?");
			} else {
				player.getActionSender().sendDialogue(
						npc.getDefinition().getName(), DialogueType.NPC,
						npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Woooo woo wooooo!");
			}
			player.getInterfaceState().setNextDialogueId(0, 384);
			break;
		case 384:
			if (player.getEquipment().contains(552)) {
				player.getActionSender().sendDialogue(player.getName(),
						DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
						"Why are you here?");
			} else {
				player.getActionSender().sendDialogue(
						npc.getDefinition().getName(), DialogueType.NPC,
						npc.getDefinition().getId(), FacialAnimation.HAPPY,
						"Weee ooh! Wooeh wee!");
			}
			player.getInterfaceState().setNextDialogueId(0, 385);
			break;
		case 385:
			if (player.getEquipment().contains(552)) {
				player.getActionSender().sendDialogue(
						npc.getDefinition().getName(), DialogueType.NPC,
						npc.getDefinition().getId(), FacialAnimation.SAD,
						"Well you see, my coffin did not hold by",
						"spirit inside. I was released from my",
						"body and can't go back inside.");
				player.getInterfaceState().setNextDialogueId(0, 386);
			} else {
				player.getActionSender().sendDialogue(player.getName(),
						DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
						"I'll talk to you later...");
				player.getInterfaceState().setNextDialogueId(0, 97);
			}
			break;
		case 386:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.ALMOST_CRYING,
					"That's terrible! I hope it doesn't happen to me.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 387:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Sorry, but rules are rules!",
					"I can't let you go down there unless",
					"you have a Mining level of at least 60.");
			player.getInterfaceState().setNextDialogueId(0, 388);
			break;
		case 388:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Okay, well I'd better start mining!");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 389:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Lumbridge please.");
			player.getInterfaceState().setNextDialogueId(0, 390);
			break;
		case 390:
			Sedridor.teleport(player, npc, Location.create(3222, 3218, 0),
					"Lumbridge");
			break;
		case 391:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Varrock please.");
			player.getInterfaceState().setNextDialogueId(0, 392);
			break;
		case 392:
			Sedridor.teleport(player, npc, Location.create(3211, 3423, 0),
					"Varrock");
			break;
		case 393:
			player.getActionSender().sendDialogue("Where to?",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Varrock",
					"The Hut",
					"Go back...");
			player.getInterfaceState().setNextDialogueId(0, 391);
			player.getInterfaceState().setNextDialogueId(1, 821);
			player.getInterfaceState().setNextDialogueId(2, 220);
			break;
		case 394:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Hello, dearie! Were you wanting to collect a random",
					"event costume, or is there something else I can do for",
					"you today?");
			player.getInterfaceState().setNextDialogueId(0, 395);
			break;
		case 395:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"I've come for a random event costume.",
					"Aren't you selling anything?", 
					"I just came for a chat.");
			player.getInterfaceState().setNextDialogueId(0, 396);
			player.getInterfaceState().setNextDialogueId(1, 400);
			player.getInterfaceState().setNextDialogueId(2, 402);
			break;
		case 396:
			player.getActionSender().sendDialogue(player.getName(),
					DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"I've come for a random event costume.");
			player.getInterfaceState().setNextDialogueId(0, 397);
			break;
		case 397:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.SAD,
					"Sorry hun, but you've got no",
					"random event costume points to spend.");
			player.getInterfaceState().setNextDialogueId(0, 398);
			break;
		case 398:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Anything else I can help you with?");
			player.getInterfaceState().setNextDialogueId(0, 399);
			break;
		case 399:
			player.getActionSender().sendDialogue("Select an Option",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes please.", "No thanks, I'm fine.");
			player.getInterfaceState().setNextDialogueId(0, 395);
			player.getInterfaceState().setNextDialogueId(1, 273);
			break;
		case 400:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"Aren't you selling anything?");
			player.getInterfaceState().setNextDialogueId(0, 401);
			break;
		case 401:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.LAUGH_1,
					"Oh, you want to talk to Thessalia.",
					"She's in charge of the shop and makeover service.");
			player.getInterfaceState().setNextDialogueId(0, 398);
			break;
		case 402:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"I've just came for a chat.");
			player.getInterfaceState().setNextDialogueId(0, 403);
			break;
		case 403:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.SAD,
					"I'm sorry, but I'll never get my knitting",
					"done if I stop for a chit-chat with every young lad",
					"who wanders through the shop!");
			player.getInterfaceState().setNextDialogueId(0, 398);
			break;
		case 404:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.SAD,
					"Oh dear, oh dear!",
					"I miss my love Juliet as so!");
			player.getInterfaceState().setNextDialogueId(0, 405);
			break;
		case 405:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.SAD,
					"If you ever get the chance to talk to",
					"her please come back here to me!",
					"You will be greatly rewarded with nearly",
					"all of my family's fortune!");
			player.getInterfaceState().setNextDialogueId(0, 406);
			break;
		case 406:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.SAD,
				"That's certainly terrible! I'll tell",
				"you if I ever see or talk to her.");
			player.getInterfaceState().setNextDialogueId(0, 407);
			break;
		case 407:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Thank you, so greatly!",
					"I've longed to be with her ever since.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 408:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.SAD,
					"Got any spare change, mate?");
			player.getInterfaceState().setNextDialogueId(0, 409);
			break;
		case 409:
			if(player.getInventory().getCount(995) > 0) {
			player.getActionSender().sendDialogue("What would you like to say?",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes, I can spare a little money.",
					"Sorry, you'll have to earn it yourself.");
			player.getInterfaceState().setNextDialogueId(0, 410);
			player.getInterfaceState().setNextDialogueId(1, 413);
			} else {
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISINTERESTED,
					"Sorry, I havn't gotten any.");
				player.getInterfaceState().setNextDialogueId(0, 412);
			}
			break;
		case 410:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISINTERESTED,
					"Yes, I can spare a little money.");
				player.getInterfaceState().setNextDialogueId(0, 411);
			break;
		case 411:
				player.getInventory().remove(new Item(995, 1));
				player.getActionSender().sendDialogue(
						npc.getDefinition().getName(), DialogueType.NPC,
						npc.getDefinition().getId(), FacialAnimation.HAPPY,
						"Thanks mate!");
				player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 412:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.ANGER_2,
					"That's what they all say!");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 413:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISINTERESTED,
				"Sorry, you'll have to earn it yourself, just like I did.");
			player.getInterfaceState().setNextDialogueId(0, 414);
			break;
		case 414:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.ANGER_1,
					"Please yourself.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 415:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Hello, what can I do for you?");
			player.getInterfaceState().setNextDialogueId(0, 416);
			break;
		case 416:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"I'd like to buy some fur.",
					"Nothing, was just stopping to chat.");
			player.getInterfaceState().setNextDialogueId(0, 418);
			player.getInterfaceState().setNextDialogueId(1, 417);
			break;
		case 417:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISINTERESTED,
				"Nothing, was just stopping to chat.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 418:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"I'd like to buy some fur.");
			player.getInterfaceState().setNextDialogueId(0, 419);
			break;
		case 419:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Yeah, sure. They're just 20 gold coins each.");
			player.getInterfaceState().setNextDialogueId(0, 420);
			break;
		case 420:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Yeah, okay, here you go.",
					"20 gold coins? That's an outrage!");
			player.getInterfaceState().setNextDialogueId(0, 421);
			player.getInterfaceState().setNextDialogueId(1, 423);
			break;
		case 421:
			if(player.getInventory().getCount(995) >= 20) {
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"Yeah, okay, here you go.");
			player.getInterfaceState().setNextDialogueId(0, 422);
			} else {
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.SAD,
					"Oh! How embarassing, I don't have enough cash.");
				player.getInterfaceState().setNextDialogueId(0, 97);
			}
			break;
		case 422:
			player.getInventory().remove(new Item(995, 20));
			player.getInventory().add(new Item(948));
			player.getActionSender().sendDialogue(player.getName(), DialogueType.MESSAGE_MODEL_LEFT, 948, FacialAnimation.DEFAULT,
				"Baraek sells you a fur.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 423:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.ANGER_1,
				"20 gold coins? That's an outrage!");
			player.getInterfaceState().setNextDialogueId(0, 424);
			break;
		case 424:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Well, my best price is 18 coins.");
			player.getInterfaceState().setNextDialogueId(0, 425);
			break;
		case 425:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Yeah, okay, here you go.",
					"No thanks. I'll just leave it.");
			player.getInterfaceState().setNextDialogueId(0, 421);
			player.getInterfaceState().setNextDialogueId(1, 426);
			break;
		case 426:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.CALM_1,
				"No thanks. I'll just leave it.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 427:
			if(player.getInventory().getCount(995) >= 18) {
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Yeah, okay, here you go.");
				player.getInterfaceState().setNextDialogueId(0, 428);
				} else {
					player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.SAD,
						"Oh! How embarassing, I don't have enough cash.");
					player.getInterfaceState().setNextDialogueId(0, 97);
				}
			break;
		case 428:
			player.getInventory().remove(new Item(995, 18));
			player.getInventory().add(new Item(948));
			player.getActionSender().sendDialogue(player.getName(), DialogueType.MESSAGE_MODEL_LEFT, 948, FacialAnimation.DEFAULT,
				"Baraek sells you a fur.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 429:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"Hello!");
			player.getInterfaceState().setNextDialogueId(0, 430);
			break;
		case 430:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Do you need anything?");
			player.getInterfaceState().setNextDialogueId(0, 431);
			break;
		case 431:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Yes, I'd like to look at the paper.",
					"No thanks. I'll just leave it.");
			player.getInterfaceState().setNextDialogueId(0, 432);
			player.getInterfaceState().setNextDialogueId(1, 426);
			break;
		case 432:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.CALM_1,
				"Yes, I'd like to look at the paper.");
			player.getInterfaceState().setNextDialogueId(0, 434);
			break;
		case 433:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.MESSAGE_MODEL_LEFT, 11171, FacialAnimation.DEFAULT,
				"Varrock news: InnovationX PRO will be going BETA soon!");
			player.getInterfaceState().setNextDialogueId(0, 435);
			break;
		case 434:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.MESSAGE, -1, FacialAnimation.DEFAULT,
				"You read the paper.");
			player.getInterfaceState().setNextDialogueId(0, 433);
			break;
		case 435:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"Thanks!");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 436:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Would you like to buy or sell some staves?");
			player.getInterfaceState().setNextDialogueId(0, 437);
			break;
		case 437:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Yes, please.",
					"No, thank you.",
					"Do you have any battlestaves?");
			player.getInterfaceState().setNextDialogueId(0, 441);
			player.getInterfaceState().setNextDialogueId(1, 438);
			player.getInterfaceState().setNextDialogueId(2, 442);
			break;
		case 438:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"No, thank you.");
			player.getInterfaceState().setNextDialogueId(0, 439);
			break;
		case 439:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Well 'stick' your head in again if you ever change your",
					"mind.");
			player.getInterfaceState().setNextDialogueId(0, 440);
			break;
		case 440:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"Huh, terrible pun. You just can't get the 'staff' these",
				"days!");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 441:
			Shop.open(player, 21, 1);
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 442:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Do you have any battlestaves?");
			player.getInterfaceState().setNextDialogueId(0, 443);
			break;
		case 443:
			if(player.getInventory().getCount(995) >= 7000) {
				player.getActionSender().sendDialogue(
						npc.getDefinition().getName(), DialogueType.NPC,
						npc.getDefinition().getId(), FacialAnimation.HAPPY,
						"Yes, battlestaves cost 7,000 gold pieces each.",
						"Would you like to buy one?");
				player.getInterfaceState().setNextDialogueId(0, 444);
			} else {
				player.getActionSender().sendDialogue(
						npc.getDefinition().getName(), DialogueType.NPC,
						npc.getDefinition().getId(), FacialAnimation.SAD,
						"Yes, but I'm afriad my battlestaves cost 7,000 gold",
						"pieces each. Come back with more money.");
				player.getInterfaceState().setNextDialogueId(0, 97);
			}
			break;
		case 444:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Yes please.",
					"No thanks, I'll just leave it.");
			player.getInterfaceState().setNextDialogueId(0, 445);
			player.getInterfaceState().setNextDialogueId(1, 426);
			break;
		case 445:
			player.getInventory().remove(new Item(995, 7000));
			player.getInventory().add(new Item(1391));
			player.getActionSender().sendDialogue(player.getName(), DialogueType.MESSAGE_MODEL_LEFT, 1391, FacialAnimation.DEFAULT,
				"Zaff sells you a battlestaff.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 446:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Hello, bold adventurer. Can I interest you in some",
					"swords?");
			player.getInterfaceState().setNextDialogueId(0, 447);
			break;
		case 447:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Yes, please.",
					"No, I'm okay for swords right now.");
			player.getInterfaceState().setNextDialogueId(0, 450);
			player.getInterfaceState().setNextDialogueId(1, 448);
			break;
		case 448:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"No, I'm okay for swords right now.");
			player.getInterfaceState().setNextDialogueId(0, 449);
			break;
		case 449:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.CALM_1,
					"Come back if you need any.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 450:
			//TODO: Would open up the sword shop.
			break;
		case 451:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.CALM_1,
				"Hello there.");
			player.getInterfaceState().setNextDialogueId(0, 452);
			break;
		case 452:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.CALM_1,
					"Oh! Hello there.");
			player.getInterfaceState().setNextDialogueId(0, 453);
			break;
		case 453:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Who are you?",
					"Can you teach me about Canoeing?",
					"Could you sail me somewhere?");
			player.getInterfaceState().setNextDialogueId(0, 454);
			player.getInterfaceState().setNextDialogueId(1, 474);
			player.getInterfaceState().setNextDialogueId(2, 475);
			break;
		case 454:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"Who are you?");
			player.getInterfaceState().setNextDialogueId(0, 455);
			break;
		case 455:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"My name is Ex Sea Captain Barfy Bill.");
			player.getInterfaceState().setNextDialogueId(0, 456);
			break;
		case 456:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Ex sea captain?");
			player.getInterfaceState().setNextDialogueId(0, 457);
			break;
		case 457:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.SAD,
					"Yeah, I bought a lovely ship and was planning to make",
					"a fortune running her as a merchant vessel.");
			player.getInterfaceState().setNextDialogueId(0, 458);
			break;
		case 458:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Why are you not sailing?");
			player.getInterfaceState().setNextDialogueId(0, 459);
			break;
		case 459:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.SAD,
					"Chronic sea sickness. My first, and only voyage was",
					"spent dry heaving over the rails.");
			player.getInterfaceState().setNextDialogueId(0, 460);
			break;
		case 460:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"If I had known about the sea sickness I could have",
					"saved myself a lot of money.");
			player.getInterfaceState().setNextDialogueId(0, 461);
			break;
		case 461:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"What are you up to know then?");
			player.getInterfaceState().setNextDialogueId(0, 462);
			break;
		case 462:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Well my ship had a little fire related problem.",
					"Fortunately it was well insured.");
			player.getInterfaceState().setNextDialogueId(0, 463);
			break;
		case 463:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.CALM_2,
					"Anyway, I don't have to work anymore so I've taken to",
					"canoeing on the river.");
			player.getInterfaceState().setNextDialogueId(0, 464);
			break;
		case 464:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"I don't get river sick!");
			player.getInterfaceState().setNextDialogueId(0, 465);
			break;
		case 465:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Would you like me to take you for a sail?");
			player.getInterfaceState().setNextDialogueId(0, 466);
			break;
		case 466:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Yes",
					"No");
			player.getInterfaceState().setNextDialogueId(0, 468);
			player.getInterfaceState().setNextDialogueId(1, 467);
			break;
		case 467:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISINTERESTED,
				"No thanks, I'd better get going.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 468:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"Yes, It'd be a joy.");
			player.getInterfaceState().setNextDialogueId(0, 469);
			break;
		case 469:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Okay guv. Where would you like to go?");
			player.getInterfaceState().setNextDialogueId(0, 470);
			break;
		case 470:
			player.getActionSender().sendDialogue(
					"Where to sail?", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Edgeville",
					"Barbarian Village",
					"Champion's Guild");
			player.getInterfaceState().setNextDialogueId(0, 471);
			player.getInterfaceState().setNextDialogueId(1, 472);
			player.getInterfaceState().setNextDialogueId(2, 473);
			break;
		case 471:
			Canoes.sail(player, npc, Location.create(3132, 3510, 0), "Edgeville");
			break;
		case 472:
			Canoes.sail(player, npc, Location.create(3112, 3411, 0), "Barbarian Village");
			break;
		case 473:
			Canoes.sail(player, npc, Location.create(3202, 3343, 0), "Champion's Guild");
			break;
		case 474:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Can you teach me about canoeing?");
			player.getInterfaceState().setNextDialogueId(0, 476);
			break;
		case 475:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"Could you sail me somewhere?");
			player.getInterfaceState().setNextDialogueId(0, 469);
			break;
		case 476:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Sailing is an art. On my voyages I",
					"always know which direction to travel",
					"by using my compass. It takes years of",
					"practice in order to fully master canoeing.");
			player.getInterfaceState().setNextDialogueId(0, 477);
			break;
		case 477:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.LAUGH_1,
					"However, maybe one day you could be",
					"a true master just like myself!");
			player.getInterfaceState().setNextDialogueId(0, 478);
			break;
		case 478:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.LAUGH_4,
				"Oh yes, maybe!");
			player.getInterfaceState().setNextDialogueId(0, 479);
			break;
		case 479:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Could I help you with anything else guv?");
			player.getInterfaceState().setNextDialogueId(0, 480);
			break;
		case 480:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Yes",
					"No");
			player.getInterfaceState().setNextDialogueId(0, 481);
			player.getInterfaceState().setNextDialogueId(1, 467);
			break;
		case 481:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"Okay, tell me what you can offer.");
			player.getInterfaceState().setNextDialogueId(0, 453);
			break;
		case 482:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"Hello there.");
			player.getInterfaceState().setNextDialogueId(0, 483);
			break;
		case 483:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Greetings. Do you need anything?");
			player.getInterfaceState().setNextDialogueId(0, 484);
			break;
		case 484:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Who are you?",
					"I'd like to purchase a Shantay pass.",
					"Nothing, I was just passing by.");
			player.getInterfaceState().setNextDialogueId(0, 485);
			player.getInterfaceState().setNextDialogueId(1, 496);
			player.getInterfaceState().setNextDialogueId(2, 494);
			break;
		case 485:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Who are you?");
			player.getInterfaceState().setNextDialogueId(0, 486);
			break;
		case 486:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"I'm Shantay. That pass over there is owned",
					"and operated by me. I've come-");
			player.getInterfaceState().setNextDialogueId(0, 487);
			break;
		case 487:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Wait wait wait. That's the only entrance",
				"into the Al-Karid desert and you own it?");
			player.getInterfaceState().setNextDialogueId(0, 488);
			break;
		case 488:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Well, I need to make a living as well. Many",
					"years ago my family purchased the entire Al-Karid.",
					"After time progressed, my family degraded. The only",
					"people on " + Constants.SERVER_NAME + " that are part");
			player.getInterfaceState().setNextDialogueId(0, 489);
			break;
		case 489:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Of my main family root is my brother, living inside",
					"the palace over there. Go and talk to him if you want",
					"information on buying pieces of Al-Karid. He makes",
					"all the descisions around here, and is so greedy!");
			player.getInterfaceState().setNextDialogueId(0, 490);
			break;
		case 490:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"I'd be happy to help you",
					"with anything else that may interest you.");
			player.getInterfaceState().setNextDialogueId(0, 491);
			break;
		case 491:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Yes",
					"No");
			player.getInterfaceState().setNextDialogueId(0, 493);
			player.getInterfaceState().setNextDialogueId(1, 492);
			break;
		case 492:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISINTERESTED,
					"No thanks, I ought to be going.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 493:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"Yes please.");
			player.getInterfaceState().setNextDialogueId(0, 484);
			break;
		case 494:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISINTERESTED,
				"Nothing, I was just passing by.");
			player.getInterfaceState().setNextDialogueId(0, 495);
			break;
		case 495:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.ANGER_4,
					"If you go through my passage without",
					"a pass I will call guards upon you!");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 496:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"I'd like to buy a shantay pass.");
			player.getInterfaceState().setNextDialogueId(0, 497);
			break;
		case 497:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"That'll be 100 coins, sir.");
			player.getInterfaceState().setNextDialogueId(0, 498);
			break;
		case 498:
			if(player.getInventory().getCount(995) >= 100) {
				player.getActionSender().sendDialogue(
						"Select an Option", DialogueType.OPTION,
						npc.getDefinition().getId(), FacialAnimation.DEFAULT,
						"Okay, here you go.",
						"A bloody 100 coins, that's an outrage!");
				player.getInterfaceState().setNextDialogueId(0, 501);
				player.getInterfaceState().setNextDialogueId(1, 500);
			} else {
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.SAD,
					"I don't have enough coins.");
				player.getInterfaceState().setNextDialogueId(0, 499);
			}
			break;
		case 499:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Well, come back if you ever have enough money.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 500:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.ANGER_3,
					"A bloody 100, that's an outrage!");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 501:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Okay, here you go.");
			player.getInterfaceState().setNextDialogueId(0, 502);
			break;
		case 502:
			player.getInventory().remove(new Item(995, 100));
			player.getInventory().add(new Item(1854, 1));
			player.getActionSender().sendDialogue("Shantay", DialogueType.MESSAGE_MODEL_LEFT, 1854, FacialAnimation.DEFAULT,
					"Shantay sells you a pass.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 503:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"If you'd like to go through the path, please",
					"buy a pass from Shantay.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 504:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.ANGER_2,
					"Your NOT allowed to go through without",
					"one of Shantay's infamous passes.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 505:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Who are you?",
					"What is that cape you're wearing?");
			player.getInterfaceState().setNextDialogueId(0, 506);
			player.getInterfaceState().setNextDialogueId(1, 510);
			break;
		case 506:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"Who are you?");
			player.getInterfaceState().setNextDialogueId(0, 507);
			break;
		case 507:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"My name is Harlan, a master of defence!");
			player.getInterfaceState().setNextDialogueId(0, 508);
			break;
		case 508:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"What do you do here?");
			player.getInterfaceState().setNextDialogueId(0, 509);
			break;
		case 509:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.LAUGH_3,
					"I assist new adventurers in learning the ways of melee",
					"combat. It is a dangerous but worthwhile study. There",
					"is nothing like the feeling of wading into battle against",
					"many foes.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 510:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"What is that cape you're wearing?");
			player.getInterfaceState().setNextDialogueId(0, 511);
			break;
		case 511:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Ah, this is a Skillcape of Defence. I have mastered the",
					"art of defence and wear it proudly to show others.");
			player.getInterfaceState().setNextDialogueId(0, 512);
			break;
		case 512:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISINTERESTED,
				"Hmm...interesting.");
			player.getInterfaceState().setNextDialogueId(0, 513);
			break;
		case 513:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Is there any way I could possibly have",
				"or buy one of those Skillcapes of Defence?");
			player.getInterfaceState().setNextDialogueId(0, 514);
			break;
		case 514:
			if(player.getSkills().getLevelForExperience(Skills.DEFENCE) >= 99 && player.getInventory().getCount(995) >= 99000) {
				player.getActionSender().sendDialogue(
						npc.getDefinition().getName(), DialogueType.NPC,
						npc.getDefinition().getId(), FacialAnimation.HAPPY,
						"In fact, yes! Since you seem to have mastered the",
						"skill of defence, I will sell you a copy of my cape",
						"for a standard Skillcape price of 99,000 gold coins.");
				player.getInterfaceState().setNextDialogueId(0, 515);
			} else if(player.getInventory().getCount(995) >= 99000){
				player.getActionSender().sendDialogue(
						npc.getDefinition().getName(), DialogueType.NPC,
						npc.getDefinition().getId(), FacialAnimation.LAUGH_1,
						"Haha, you with this cape? You only have a defence",
						"level of " + player.getSkills().getLevelForExperience(Skills.DEFENCE) + "! You need to level");
				player.getInterfaceState().setNextDialogueId(0, 521);
			} else {
				DialogueManager.openDialogue(player, 519);
			}
			break;
		case 515:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Yes, I'd love one!",
					"No thanks, I'll pass.");
			player.getInterfaceState().setNextDialogueId(0, 517);
			player.getInterfaceState().setNextDialogueId(1, 516);
			break;
		case 516:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISINTERESTED,
					"No thanks, I'll pass.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 517:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Yes, I'd love one!");
			player.getInterfaceState().setNextDialogueId(0, 518);
			break;
		case 518:
			player.getInventory().remove(new Item(995, 99000));
			player.getInventory().add(new Item(9753));
			player.getActionSender().sendDialogue(null, DialogueType.MESSAGE_MODEL_LEFT, 9753, FacialAnimation.DEFAULT,
					"You pay 99,000 and receive a Skillcape of Defence.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 519:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.LAUGH_1,
					"Oh, haha. Silly me forgot the money for the cape.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 520:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.LAUGH_1,
					"Haha, you with this cape? You only have a defence",
					"level of " + player.getSkills().getLevelForExperience(Skills.DEFENCE) + "! You need",
					"to level up " + (99 - player.getSkills().getLevelForExperience(Skills.DEFENCE)) + " more times in",
					"order to buy one.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 521:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.LAUGH_1,
					"up " + (99 - player.getSkills().getLevelForExperience(Skills.DEFENCE)) + " more times in order",
					"to purchase this Skillcape of Defence.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 522:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"up " + (99 - player.getSkills().getLevelForExperience(Skills.DEFENCE)) + " more times in order",
					"to purchase this Skillcape of Defence.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 523:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Hello there, I am Mahigorak.",
					"I've been blessed by the Gods to lower human defence",
					"permantly. What could I do for you?");
			player.getInterfaceState().setNextDialogueId(0, 524);
			break;
		case 524:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"I'd like my defence lowered!",
					"Nothing, was just passing by.");
			player.getInterfaceState().setNextDialogueId(0, 526);
			player.getInterfaceState().setNextDialogueId(1, 525);
			break;
		case 525:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISINTERESTED,
				"Nothing, was just passing by.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 526:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"I'd like my defence lowered!");
			player.getInterfaceState().setNextDialogueId(0, 528);
			break;
		case 527:
			int[] equipment = new int[] {
					Equipment.SLOT_BOOTS, Equipment.SLOT_BOTTOMS, Equipment.SLOT_CHEST, Equipment.SLOT_CAPE, Equipment.SLOT_GLOVES,
					Equipment.SLOT_HELM, Equipment.SLOT_SHIELD
				};
				for(int i = 0; i < equipment.length; i++) {
					if(player.getEquipment().get(equipment[i]) != null) {
						player.getActionSender().sendDialogue(
								npc.getDefinition().getName(), DialogueType.NPC,
								npc.getDefinition().getId(), FacialAnimation.ANNOYED,
								"You can't lower your Defence level while wearing",
								"equipment! My powers don't work on armour.");
						player.getInterfaceState().setNextDialogueId(0, 97);
						return;
					} else {
						player.getActionSender().sendDialogue(
								npc.getDefinition().getName(), DialogueType.NPC,
								npc.getDefinition().getId(), FacialAnimation.DELIGHTED_EVIL,
									"Mwuahaha! Zamorak conquers defence!");
						player.getSkills().setLevel(1, 1);
						player.getSkills().setExperience(1, player.getSkills().getExperienceForLevel(1));
						player.getInterfaceState().setNextDialogueId(0, 97);
					}
				}
			break;
		case 528:
			player.getActionSender().sendDialogue(
					"Reset your defence?", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Yes",
					"No");
			player.getInterfaceState().setNextDialogueId(0, 527);
			player.getInterfaceState().setNextDialogueId(1, 97);
			break;
		case 529:
			npc.playAnimation(Animation.BECKON);
			npc.face(player.getLocation());
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.ANGER_3,
						"Argh you! Stay away from my flowers!");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 530:
			player.getActionSender().sendDialogue(
					"Squire",
					DialogueType.NPC,
					606,
					FacialAnimation.DEFAULT,
					"But before you go, I'd like you to know that " + Constants.SERVER_NAME,
					"is at it's BETA stage. You have the option to use",
					"commands. A list can be found by clicking on the",
					"'Commands' button where 'Report Abuse' used to be.");
			player.getInterfaceState().setNextDialogueId(0, 122);
			break;
		case 531:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
						"Hello, I'm Gillie. What can I do for you?");
			player.getInterfaceState().setNextDialogueId(0, 532);
			break;
		case 532:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Who are you?",
					"Can you tell me how to milk a cow?",
					"Can I buy milk off you?",
					"I'm fine, thanks.");
			player.getInterfaceState().setNextDialogueId(0, 537);
			player.getInterfaceState().setNextDialogueId(1, 542);
			player.getInterfaceState().setNextDialogueId(2, 534);
			player.getInterfaceState().setNextDialogueId(3, 533);
			break;
		case 533:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"I'm fine, thanks.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 534:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Can I buy milk off you?");
			player.getInterfaceState().setNextDialogueId(0, 535);
			break;
		case 535:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.SAD,
						"I'm afraid not. My husband has already taken all of",
						"our stock to the market.");
			player.getInterfaceState().setNextDialogueId(0, 536);
			break;
		case 536:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
						"You could get some by milking the dairy cows yourself.",
						"If you would still rather buy it, you can probably get",
						"some at Falador bank by the garden. A lot of",
						"adventurers sell their goods there.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 537:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Who are you?");
			player.getInterfaceState().setNextDialogueId(0, 538);
			break;
		case 538:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.SAD,
						"My name's Gillie Groats. My father is a farmer and I",
						"milk the cows for him.");
			player.getInterfaceState().setNextDialogueId(0, 539);
			break;
		case 539:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Do you have any buckets of milk spare?");
			player.getInterfaceState().setNextDialogueId(0, 540);
			break;
		case 540:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.SAD,
						"I'm afraid not. We need all of our milk to sell to",
						"the market, but you can milk the cow yourself if you",
						"need milk.");
			player.getInterfaceState().setNextDialogueId(0, 541);
			break;
		case 541:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"Thanks.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 542:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"So, how do you get milk from a cow then?");
			player.getInterfaceState().setNextDialogueId(0, 543);
			break;
		case 543:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
						"It's very easy. First, you need an empty bucket to hold",
						"the milk.");
			player.getInterfaceState().setNextDialogueId(0, 544);
			break;
		case 544:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
						"You can buy empty buckets from the general store in",
						"Lumbridge, south-west of here, or from most general",
						"stores in " + Constants.SERVER_NAME + ". You can also buy them by",
						"purchasing them off other players.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 545:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Who are you?",
					"Any advice for an advanced woodcutter?",
					"Tell me about different trees and axes.",
					"What is that cape you're wearing?",
					"Goodbye.");
			player.getInterfaceState().setNextDialogueId(0, 555);
			player.getInterfaceState().setNextDialogueId(1, 557);
			player.getInterfaceState().setNextDialogueId(2, 547);
			player.getInterfaceState().setNextDialogueId(3, 553);
			player.getInterfaceState().setNextDialogueId(4, 546);
			break;
		case 546:
			player.getActionSender().sendDialogue(
					player.getName(), DialogueType.PLAYER,
					-1, FacialAnimation.DEFAULT,
						"Goodbye.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 547:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"Tell me about different trees and axes.");
			player.getInterfaceState().setNextDialogueId(0, 548);
			break;
		case 548:
			player.getActionSender().sendDialogue(
					"Trees", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Oak and Willow",
					"Maple and Yew",
					"Axes",
					"Go back to teaching");
			player.getInterfaceState().setNextDialogueId(0, 549);
			player.getInterfaceState().setNextDialogueId(1, 552);
			player.getInterfaceState().setNextDialogueId(2, 559);
			player.getInterfaceState().setNextDialogueId(3, 545);
			break;
		case 549:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
				"Almost every tree can be chopped down. Normal logs",
				"will be produced by chopping Trees' and Oak logs will",
				"come from chopping 'Oak Trees'. You can find Oak",
				"trees in amongst normal trees scattered about the");
			player.getInterfaceState().setNextDialogueId(0, 550);
			break;
		case 550:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"lands.");
			player.getInterfaceState().setNextDialogueId(0, 551);
			break;
		case 551:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
				"Willow trees will yield willow logs. You'll find willows like",
				"to grow near water, you can find some south of",
				"Draynor.");
			player.getInterfaceState().setNextDialogueId(0, 548);
			break;
		case 552:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Maple and yew trees are rare to find. They are",
					"indicated in the minimap by a tree symbol. You can",
					"find some maple trees in Seer's Village and some yews",
					"just North in the Lumbridge forest.");
			player.getInterfaceState().setNextDialogueId(0, 548);
			break;
		case 553:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"What is that cape you're wearing?");
			player.getInterfaceState().setNextDialogueId(0, 554);
			break;
		case 554:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
						"This is a Skillcape of Woodcutting. Only a person who",
						"has achieved the highest possible level in a skill can wear",
						"one.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 555:
			player.getActionSender().sendDialogue(
					player.getName(), DialogueType.PLAYER,
					-1, FacialAnimation.DISTRESSED,
						"Who are you?");
			player.getInterfaceState().setNextDialogueId(0, 556);
			break;
		case 556:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
						"My name is Wilfred and I'm the best woodsman in",
						"Asgarnia! I've spent my life studying the best methods",
						"for woodcutting. That's why I have this cape, the",
						"Skillcape of Woodcutting.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 557:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Any advice for an advanced woodcutter?");
			player.getInterfaceState().setNextDialogueId(0, 558);
			break;
		case 558:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.CALM_1,
						"Advanced woodcutters should purchase a good",
						"axe. Rune can cut wood extremely fast, and for a",
						"price of around 9,000 coins, they're surely worth it!",
						"You can smith a Rune axe or buy one off others.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 559:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.CALM_1,
						"You can buy selective axes from Bob, just",
						"South of here. Bronze, iron, steel, black, mithril,",
						"adamant, rune, and even the legendary Dragon - you",
						"can get them all, just search around!");
			player.getInterfaceState().setNextDialogueId(0, 548);
			break;
		//Start of Tutorial Island
		case 560:
			player.getActionSender().sendDialogue(
					"Getting started", DialogueType.NPC,
					-1, null,
					"To start the tutorial use your left mouse button to click on the",
					Constants.SERVER_NAME + " Guide in this room. He is indicated by a flashing",
					"yellow arrow above his head. If you can't see him, use your",
					"keyboard's arrow keys to rotate the view.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 561:
			player.tutorialStep = 1;
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
						"Greetings! I see you are a new arrival to this land. My",
						"job is to welcome to new comers. So welcome!");
			player.getInterfaceState().setNextDialogueId(0, 562);
			break;
		case 562:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.CALM_1,
						"You have already learned the first thing needed to",
						"succeed in this world; talking to other people!");
			player.getInterfaceState().setNextDialogueId(0, 563);
			break;
		case 563:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.CALM_1,
						"You will find many inhabitants of this world have useful",
						"things to say to you. By clicking on them with your",
						"mouse you can talk to them.");
			player.getInterfaceState().setNextDialogueId(0, 564);
			break;
		case 564:
			player.tutorialStep = 2;
			player.getActionSender().sendHintArrow(Location.create(3098, 3107, 0), 0, 1);
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
						"To continue the tutorial go through that door over",
						"there and speak to your first instructor!");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 565:
			player.tutorialStep = 4;
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC,
					npc.getDefinition().getId(), FacialAnimation.HAPPY,
						"Hello there, newcomer. My name is Brynna. My job is",
						"to teach you a few survival tips and tricks. First off",
						"we're going to start with the most basic surivival skill of",
						"all: making a fire.");
			player.getInterfaceState().setNextDialogueId(0, 566);
			break;
		case 566:
			player.getInventory().add(new Item(7156)); //Tinderbox
			player.getInventory().add(new Item(1351)); //Bronze axe
			player.getActionSender().sendDialogue(
					"", DialogueType.MESSAGE_MODEL_LEFT,
					590, FacialAnimation.DEFAULT,
						"Brynna gives you a tinderbox and a bronze axe.");
			player.getInterfaceState().setNextDialogueId(0, 567);
			break;
		case 567:
			//player.getActionSender().sendSidebarInterface(11, 261);
			//player.getActionSender().sendFlashingTab(11);
			player.getActionSender().sendDialogue(
					"Viewing the items that you were given", DialogueType.NPC, -1, FacialAnimation.DEFAULT,
					"Click on the flashing backpack icon to the right hand side of",
					"the main window to view your inventory. Your inventory is a list",
					"of everything you have in your backpack.");
			player.getInterfaceState().setNextDialogueId(0, 568);
			break;
		case 568:
			player.tutorialStep = 5;
			player.getActionSender().sendHintArrow(Location.create(3099, 3095), 0, 1);
			player.getActionSender().sendDialogue(
					"Cut down a tree", DialogueType.NPC, -1, FacialAnimation.DEFAULT,
					"You can click on the backpack item at any time to view the",
					"items that you currently have in your inventory. You will see",
					"that you now have an axe in your inventory. Use this to get",
					"some logs by clicking on one of the trees in the area.");
			player.getInterfaceState().setNextDialogueId(0, 569);
			break;
		case 569:
			player.tutorialStep = 6;
			NPC survivalExpert = null;
			for(NPC npcs : World.getWorld().getNPCs()) {
				if(npcs != null) {
					if(npcs.getDefinition().getId() == 943) {
						survivalExpert = npcs;
					}
				}
			}
			player.getActionSender().sendHintArrow(survivalExpert, 0, 1);
			player.getActionSender().sendDialogue(
					"Please wait.", DialogueType.NPC, -1, FacialAnimation.DEFAULT,
					"Your character is now attempting to cut down the tree. Sit back",
					"for a moment while he does all the hard work.");
			player.getInterfaceState().setNextDialogueId(0, 570);
			break;
		case 570:
			player.getActionSender().sendDialogue(
					"Making a fire", DialogueType.NPC, -1, FacialAnimation.DEFAULT,
					"Well done! You managed to cut some logs from the tree! Next,",
					"use the tinderbox in your inventory to light the logs.",
					"First click on the tinderbox to 'use' it.",
					"Then click on the logs in your inventory to light them.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 571:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Greetings " + player.getName () + ". Welcome you are in the test",
					"of combat.");
			player.getInterfaceState().setNextDialogueId(0, 604);
			break;
		case 572:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Why hello there! I'm Kafreena. Like the look of my",
					"pets? I think they're eyeing you up.");
			player.getInterfaceState().setNextDialogueId(0, 573);
			break;
		case 573:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.LAUGH_4,
				"That was a really bad pun.");
			player.getInterfaceState().setNextDialogueId(0, 574);
			break;
		case 574:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.SAD,
					"Sorry... I don't get to see the rest of the guild much,",
					"stuck up here. The Cyclopes don't talk much you see.");
			player.getInterfaceState().setNextDialogueId(0, 575);
			break;
		case 575:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Shouldn't that be cyclopses?");
			player.getInterfaceState().setNextDialogueId(0, 576);
			break;
		case 576:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.LAUGH_1,
					"Nope! Cyclopes is the plural of cyclops. One cyclops,",
					"many cyclopes.");
			player.getInterfaceState().setNextDialogueId(0, 577);
			break;
		case 577:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"Oh, right, thanks.");
			player.getInterfaceState().setNextDialogueId(0, 578);
			break;
		case 578:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Where are they from?",
					"How did they get here?",
					"Why are they here?",
					"Bye!");
			player.getInterfaceState().setNextDialogueId(0, 581);
			player.getInterfaceState().setNextDialogueId(1, 583);
			player.getInterfaceState().setNextDialogueId(2, 585);
			player.getInterfaceState().setNextDialogueId(3, 579);
			break;
		case 579:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"Bye!");
			player.getInterfaceState().setNextDialogueId(0, 580);
			break;
		case 580:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"See you back here soon I hope!");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 581:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Where are they from?");
			player.getInterfaceState().setNextDialogueId(0, 582);
			break;
		case 582:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"They're from the far east lands.");
			player.getInterfaceState().setNextDialogueId(0, 578);
			break;
		case 583:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"How did they get here?");
			player.getInterfaceState().setNextDialogueId(0, 584);
			break;
		case 584:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Ahh.. our guildmaster, Harrallak, went on an expedition",
					"there. He brought them back with him.");
			player.getInterfaceState().setNextDialogueId(0, 578);
			break;
		case 585:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Why are they here?");
			player.getInterfaceState().setNextDialogueId(0, 586);
			break;
		case 586:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"For the warriors to train on of course! They also drop",
					"a rather nice blade.");
			player.getInterfaceState().setNextDialogueId(0, 587);
			break;
		case 587:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Oh? What would that be?");
			player.getInterfaceState().setNextDialogueId(0, 588);
			break;
		case 588:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.CALM_2,
					"Defenders.");
			player.getInterfaceState().setNextDialogueId(0, 589);
			break;
		case 589:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Err what are they?");
			player.getInterfaceState().setNextDialogueId(0, 590);
			break;
		case 590:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"It's a blade you can defend with using your shield",
					"hand, like I have.");
			player.getInterfaceState().setNextDialogueId(0, 591);
			break;
		case 591:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.ON_ONE_HAND,
				"Wow!");
			player.getInterfaceState().setNextDialogueId(0, 592);
			break;
		case 592:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"For every 10 tokens you collect around the guild, you",
					"can spend one minute in with my pets. As you get",
					"defenders you can show them to me to earn even",
					"better ones... but remember if you lose them you'll have");
			player.getInterfaceState().setNextDialogueId(0, 593);
			break;
		case 593:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"to start at bronze again. I'd advise keeping a spare in",
					"your bank.");
			player.getInterfaceState().setNextDialogueId(0, 594);
			break;
		case 594:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"Okay!");
			player.getInterfaceState().setNextDialogueId(0, 595);
			break;
		case 595:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Oh, by the way, you'll need to earn 100 tokens before",
					"I'll let you in!");
			player.getInterfaceState().setNextDialogueId(0, 596);
			break;
		case 596:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"Right, I'd better go play some games then!");
			player.getInterfaceState().setNextDialogueId(0, 578);
			break;
		case 597:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Greetings warrior, how may I help you?");
			player.getInterfaceState().setNextDialogueId(0, 598);
			break;
		case 598:
			player.getActionSender().sendDialogue(
					"What would you like to say?", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"I'd like to access my bank account, please.",
					"I'd like to check my PIN settings.",
					"How long have you worked here?",
					"In no way, sorry to bother.");
			player.getInterfaceState().setNextDialogueId(0, 2);
			player.getInterfaceState().setNextDialogueId(1, 4);
			player.getInterfaceState().setNextDialogueId(2, 600);
			player.getInterfaceState().setNextDialogueId(3, 599);
			break;
		case 599:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"In no way, sorry to bother.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 600:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"How long have you worked here?");
			player.getInterfaceState().setNextDialogueId(0, 601);
			break;
		case 601:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Oh ever since the Guild opened. I like it here.");
			player.getInterfaceState().setNextDialogueId(0, 602);
			break;
		case 602:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Why's that?");
			player.getInterfaceState().setNextDialogueId(0, 603);
			break;
		case 603:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Well.. with all these warriors around, there's not",
					"much chance of my bank being robbed, is there!");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 604:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"What do I do here?",
					"Where do my machines come from?",
					"May I claim my tokens please?",
					"Bye!");
			player.getInterfaceState().setNextDialogueId(0, 607);
			player.getInterfaceState().setNextDialogueId(1, 618);
			player.getInterfaceState().setNextDialogueId(2, 624);
			player.getInterfaceState().setNextDialogueId(3, 605);
			break;
		case 605:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"Bye!");
			player.getInterfaceState().setNextDialogueId(0, 606);
			break;
		case 606:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.CALM_1,
					"Health be with you travelling.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 607:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"What do I do here?");
			player.getInterfaceState().setNextDialogueId(0, 608);
			break;
		case 608:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"A spare suit of plate armour need you will. Full helm",
					"plate leggings and platebody yes? Placing it in the",
					"centre of the magical machines you will be doing. KA-",
					"POOF! The armour, it attacks most furiously as if");
			player.getInterfaceState().setNextDialogueId(0, 609);
			break;
		case 609:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"alive! Kill it you must, yes.");
			player.getInterfaceState().setNextDialogueId(0, 610);
			break;
		case 610:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"So I use a full set of plate armour on the centre plate",
				"of the machines and it will animate it? Then I have to",
				"kill my own armour... how bizarre!");
			player.getInterfaceState().setNextDialogueId(0, 611);
			break;
		case 611:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Yes. It is as you are saying. For this earn tokens you",
					"will. Also gain experience in combat you will. Trained",
					"long and hard here have I.");
			player.getInterfaceState().setNextDialogueId(0, 612);
			break;
		case 612:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"You're not from around here are you...?");
			player.getInterfaceState().setNextDialogueId(0, 613);
			break;
		case 613:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"It is as you say.");
			player.getInterfaceState().setNextDialogueId(0, 614);
			break;
		case 614:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"So I will loose my armour?");
			player.getInterfaceState().setNextDialogueId(0, 615);
			break;
		case 615:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.CALM_1,
					"Lose armour you will if damaged too much it becomes.",
					"Rare this is, but still possible. If kill you the armour",
					"does, also lose armour you will.");
			player.getInterfaceState().setNextDialogueId(0, 616);
			break;
		case 616:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"So, occasionally I might lose a bit because it's being",
				"bashed about and I'll obviously lose it if I die... that it?");
			player.getInterfaceState().setNextDialogueId(0, 617);
			break;
		case 617:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.CALM_1,
					"It is as you say.");
			player.getInterfaceState().setNextDialogueId(0, 604);
			break;
		case 618:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"Where do the machines come from?");
			player.getInterfaceState().setNextDialogueId(0, 619);
			break;
		case 619:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.CALM_1,
					"Make them I did, with magics.");
			player.getInterfaceState().setNextDialogueId(0, 620);
			break;
		case 620:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Magic, in the Warrior's Guild?");
			player.getInterfaceState().setNextDialogueId(0, 621);
			break;
		case 621:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.CALM_2,
					"A skilled warrior also am I. Harrallak mistakes does not",
					"make. Potential in my invention he sees and",
					"opportunity grasps.");
			player.getInterfaceState().setNextDialogueId(0, 622);
			break;
		case 622:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"I see, so you made the magical machines and Harrallak",
				"saw how they could be used in the guild to train",
				"warrior's combat... interesting. Harrallak certainly is an",
				"intelligent guy.");
			player.getInterfaceState().setNextDialogueId(0, 623);
			break;
		case 623:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"It is as you say.");
			player.getInterfaceState().setNextDialogueId(0, 604);
			break;
		case 624:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"May I claim my tokens please?");
			player.getInterfaceState().setNextDialogueId(0, 625);
			break;
		case 625:
			player.getActionSender().sendDialogue(
					npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.SAD,
					"No tokens earned have you. In training activites",
					"participate you must.");
			player.getInterfaceState().setNextDialogueId(0, 626);
			break;
		case 626:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.CALM_1,
				"Ok, I'll see what I can find to do around here to",
				"earn some tokens.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 627:
			player.getActionSender().sendDialogue("", DialogueType.MESSAGE_MODEL_LEFT, 8851, FacialAnimation.DEFAULT,
					"You don't have enough Warrior Guild Tokens to enter the cyclopes enclosure yet, collect at least 100 then come back.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 628:
			String name = WarriorsGuild.create(player).getDefender().getDefinition().getName();
			String phrase = "a";
			String[] vowels = { "a", "e", "i", "o", "u" };
			for(String letter : vowels) {
				if(name.startsWith(letter)) {
					phrase = "an";
				}
			}
			player.getActionSender().sendDialogue(
					"", DialogueType.MESSAGE_MODEL_LEFT, WarriorsGuild.create(player).getDefender().getDefinition().getId(), FacialAnimation.DEFAULT,
					"You need to get " + phrase + " " + name);
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 629:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Ghommal welcome you to Warrior Guild!");
			player.getInterfaceState().setNextDialogueId(0, 630);
			break;
		case 630:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"Umm...thank you, I think.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 631:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
				"Welcome to my humble guild, " + player.getName() + ".");
			player.getInterfaceState().setNextDialogueId(0, 632);
			break;
		case 632:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Quite a place you've got here.",
					"You any good with a sword?",
					"Bye!");
			player.getInterfaceState().setNextDialogueId(0, 635);
			player.getInterfaceState().setNextDialogueId(1, 637);
			player.getInterfaceState().setNextDialogueId(2, 633);
			break;
		case 633:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"Bye!");
			player.getInterfaceState().setNextDialogueId(0, 634);
			break;
		case 634:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Farewell, brave warrior, I do hope you enjoy my guild.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 635:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"Quite a place you've got here.");
			player.getInterfaceState().setNextDialogueId(0, 636);
			break;
		case 636:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
				"Indeed we do.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 637:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"You any good with a sword?");
			player.getInterfaceState().setNextDialogueId(0, 638);
			break;
		case 638:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.ON_ONE_HAND,
				"Am I any good with a sword? Have you any clue who",
				"I am?");
			player.getInterfaceState().setNextDialogueId(0, 639);
			break;
		case 639:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"Not really, no.");
			player.getInterfaceState().setNextDialogueId(0, 640);
			break;
		case 640:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Why I could best any person in a rapier duel!");
			player.getInterfaceState().setNextDialogueId(0, 641);
			break;
		case 641:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.EVIL,
				"Try me, then!");
			player.getInterfaceState().setNextDialogueId(0, 642);
			break;
		case 642:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
				"My dear man, I couldn't possibly duel you, I might",
				"hurt you and then would happen to my",
				"reputation! Besides, I have this wonderful guild to run.",
				"Why don't you take a look at the various activites we");
			player.getInterfaceState().setNextDialogueId(0, 643);
			break;
		case 643:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"have. You might even collect enough tokens to be",
					"allowed in to kill teh strange beasts from the far east!");
				player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 644:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.SLEEPY,
					"Uh, what was that dark force? I've never sensed",
					"anything like it...");
			player.getInterfaceState().setNextDialogueId(0, 645);
			break;
		case 645:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Anyway, sorry about that.");
			player.getInterfaceState().setNextDialogueId(0, 646);
			break;
		case 646:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION,
					npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Many greetings.",
					"I seek knowledge and power.");
			player.getInterfaceState().setNextDialogueId(0, 647);
			player.getInterfaceState().setNextDialogueId(1, 650);
			break;
		case 647:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"Many greetings.");
			player.getInterfaceState().setNextDialogueId(0, 648);
			break;
		case 648:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
				"Greetings!");
			player.getInterfaceState().setNextDialogueId(0, 649);
			break;
		case 649:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
				"Remember, whenever you set out to do something,",
				"something else must be done first.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 650:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"I seek knowledge and power.");
			player.getInterfaceState().setNextDialogueId(0, 651);
			break;
		case 651:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
				"Knowledge comes from experience, power comes from",
				"battleaxes.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 652:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.SLEEPY,
					"My inner eye is clouded from the wave of darkness;",
					"give me a moment.");
			player.getInterfaceState().setNextDialogueId(0, 653);
			break;
		case 653:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.ANNOYED,
					"What do you want?");
			player.getInterfaceState().setNextDialogueId(0, 654);
			break;
		case 654:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.NEARLY_CRYING,
				"Augh! Your sins! They BLIND me!");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 655:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
				"Ahhh, hello there, " + player.getName() + ".");
			player.getInterfaceState().setNextDialogueId(0, 656);
			break;
		case 656:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Can you tell me a bit about skillcapes?",
					"What can I do here?",
					"That's a big axe!",
					"May I claim my tokens, please?",
					"Bye!");
			player.getInterfaceState().setNextDialogueId(0, 682);
			player.getInterfaceState().setNextDialogueId(1, 674);
			player.getInterfaceState().setNextDialogueId(2, 662);
			player.getInterfaceState().setNextDialogueId(3, 659);
			player.getInterfaceState().setNextDialogueId(4, 657);
			break;
		case 657:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"Bye!");
			player.getInterfaceState().setNextDialogueId(0, 658);
			break;
		case 658:
			npc.face(player.getLocation());
			npc.playAnimation(Animation.create(858));
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Be well, warrior " + player.getName() + ".");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 659:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"May I claim my tokens, please?");
			player.getInterfaceState().setNextDialogueId(0, 660);
			break;
		case 660:
			if(player.warriorTokens > 0) {
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Of course! Here you go, you've earned " + player.warriorTokens + " tokens!");
				player.getInterfaceState().setNextDialogueId(0, 661);
			} else {
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.SAD,
						"I'm afraid you have not earned any tokens yet. Try",
						"some of the activites around the guild to earn some.");
				player.getInterfaceState().setNextDialogueId(0, 97);
			}
			break;
		case 661:
			WarriorsGuild.create(player).rewardPlayer();
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"Thanks!");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 662:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.BOWS_HEAD_WHILE_SAD,
				"That's a big axe!");
			player.getInterfaceState().setNextDialogueId(0, 663);
			break;
		case 663:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.LAUGH_2,
					"Yes, indeed, it is. Have to be mighty strong to wield it",
					"too.");
			player.getInterfaceState().setNextDialogueId(0, 664);
			break;
		case 664:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"But you don't look that strong!");
			player.getInterfaceState().setNextDialogueId(0, 665);
			break;
		case 665:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Maybe, maybe not, but I still had to beat a barbarian to",
					"get it. Mind you, usually they don't part with them.",
					"This was an unusual circumstance.");
			player.getInterfaceState().setNextDialogueId(0, 666);
			break;
		case 666:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Oh?");
			player.getInterfaceState().setNextDialogueId(0, 667);
			break;
		case 667:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.LAUGH_2,
					"I bet him he couldn't catch a squirrel while still holding",
					"his axe, but that I could...and that if I won I'd get his",
					"axe.");
			player.getInterfaceState().setNextDialogueId(0, 668);
			break;
		case 668:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"What happened?");
			player.getInterfaceState().setNextDialogueId(0, 669);
			break;
		case 669:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.LAUGH_2,
					"He went running up after thd squirrel...nearly caught it",
					"too, but it shot up a tree and he tried to climb it.",
					"Only he got a bit tangled up with his axe 'cause he",
					"couldn't hang on to it at the same time and... he fell out");
			player.getInterfaceState().setNextDialogueId(0, 670);
			break;
		case 670:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.LAUGH_2,
					"of the tree. Then it was my turn...");
			player.getInterfaceState().setNextDialogueId(0, 671);
			break;
		case 671:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"I simply went and chopped the tree down with his big",
					"old axe, still holding it. The squirrel was so petrified it",
					"simply jumped straight out of the tree onto me. I won",
					"the axe and let the poor creature go!");
			player.getInterfaceState().setNextDialogueId(0, 672);
			break;
		case 672:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"I sense a moral in there somewhere.");
			player.getInterfaceState().setNextDialogueId(0, 673);
			break;
		case 673:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Aye, indeed. Brawn isn't all you need to wield a big",
					"axe, brains are required too!");
			player.getInterfaceState().setNextDialogueId(0, 656);
			break;
		case 674:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"What can I do here?");
			player.getInterfaceState().setNextDialogueId(0, 675);
			break;
		case 675:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.CALM_1,
					"Ahh, the shot put is a great test of strength and can be",
					"quite rewarding. Mind you do it properly, though, you",
					"might want to dust your hands with some powdery",
					"substance first. I'll give better grip.");
			player.getInterfaceState().setNextDialogueId(0, 676);
			break;
		case 676:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"I'll remember that. What should I use?");
			player.getInterfaceState().setNextDialogueId(0, 677);
			break;
		case 677:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"I personally prefer ground ashes, it makes a nice fine",
					"powder and gives some really good grip.");
			player.getInterfaceState().setNextDialogueId(0, 678);
			break;
		case 678:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Okay, what else?");
			player.getInterfaceState().setNextDialogueId(0, 679);
			break;
		case 679:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.CALM_1,
					"Basically you'll need to go in there, make sure you've",
					"got nothing cluttering up your hands, pick up a shot",
					"and throw it. Depending upon your technique, you can",
					"get quite long throws.");
			player.getInterfaceState().setNextDialogueId(0, 680);
			break;
		case 680:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"What was your best?");
			player.getInterfaceState().setNextDialogueId(0, 681);
			break;
		case 681:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Ahh, that would be telling.");
			player.getInterfaceState().setNextDialogueId(0, 656);
			break;
		case 682:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Can you tell me a bit about skillcapes?");
			player.getInterfaceState().setNextDialogueId(0, 683);
			break;
		case 683:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.CALM_1,
				"Sure. You can buy a skillcape when you reach level 99",
				"in a skill. Apart from boosting your statistics in that skill,",
				"they can also be used to perform some pretty amazing",
				"emotes. Is there anything else you would like to know?");
			player.getInterfaceState().setNextDialogueId(0, 656);
			break;
		case 684:
			WarriorsGuild.create(player).throwShot(Animation.create(4151));
			break;
		case 685:
			WarriorsGuild.create(player).throwShot(Animation.create(4152));
			break;
		case 686:
			WarriorsGuild.create(player).throwShot(Animation.create(4153));
			break;
		case 687:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
				"Hello, my name is Bork.",
				"What can I do for ya guv?");
			player.getInterfaceState().setNextDialogueId(0, 688);
			break;
		case 688:
			player.getActionSender().sendDialogue(
					"Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Could I have some supplies?",
					"I don't need anything.");
			player.getInterfaceState().setNextDialogueId(0, 689);
			player.getInterfaceState().setNextDialogueId(1, 692);
			break;
		case 689:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Could I have some supplies?");
			player.getInterfaceState().setNextDialogueId(0, 690);
			break;
		case 690:
			if(player.borkWait > 0) {
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.SAD,
						"Sorry guv, I can only give you supplies",
						"every hour.");
				player.getInterfaceState().setNextDialogueId(0, 97);
			} else {
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
						"Here ya go mate.");
				player.getInterfaceState().setNextDialogueId(0, 691);
			}
			break;
		case 691:
			player.borkWait = 3600;
			int[] borkSupplies = { 590, 1351, 1265, 946, 2347 };
			for(int ids : borkSupplies) {
				player.getInventory().add(new Item(ids));
			}
			player.getActionSender().sendDialogue(player.getName(), DialogueType.MESSAGE_MODEL_LEFT, 590, FacialAnimation.DEFAULT,
				"Bork hands you some supplies.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 692:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISINTERESTED,
				"I don't need anything.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 693:
			player.getActionSender().sendDialogue(
					"Where to teleport?", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Edgeville",
					"Green Dragons",
					"Fire Giants",
					"Warrior's Guild",
					"More...");
			player.getInterfaceState().setNextDialogueId(0, 694);
			player.getInterfaceState().setNextDialogueId(1, 695);
			player.getInterfaceState().setNextDialogueId(2, 696);
			player.getInterfaceState().setNextDialogueId(3, 697);
			player.getInterfaceState().setNextDialogueId(4, 698);
			break;
		case 694:
			player.getActionSender().removeChatboxInterface();
			LeetPortal.doPortal(player, player.portal, Location.create(3088 + r.nextInt(2), 3500 + r.nextInt(2)));
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 695:
			player.getActionSender().removeChatboxInterface();
			LeetPortal.doPortal(player, player.portal, Location.create(2978 + r.nextInt(3), 3615 + r.nextInt(3)));
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 696:
			player.getActionSender().removeChatboxInterface();
			LeetPortal.doPortal(player, player.portal, Location.create(2578 + r.nextInt(1), 9897 + r.nextInt(1)));
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 697:
			player.getActionSender().removeChatboxInterface();
			LeetPortal.doPortal(player, player.portal, Location.create(2870 + r.nextInt(2), 3546 + r.nextInt(3)));
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 698:
			player.getActionSender().sendDialogue(
					"Where to teleport?", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"God Wars",
					"Dagganoths",
					"King Black Dragon",
					"More...",
					"Back...");
			player.getInterfaceState().setNextDialogueId(0, 699);
			player.getInterfaceState().setNextDialogueId(1, 708);
			player.getInterfaceState().setNextDialogueId(2, 705);
			player.getInterfaceState().setNextDialogueId(3, 706);
			player.getInterfaceState().setNextDialogueId(4, 693);
			break;
		case 699:
			player.getActionSender().sendDialogue(
					"Which room?", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Saradomin",
					"Zamorak",
					"Armadyl",
					"Bandos");
			player.getInterfaceState().setNextDialogueId(0, 702);
			player.getInterfaceState().setNextDialogueId(1, 704);
			player.getInterfaceState().setNextDialogueId(2, 703);
			player.getInterfaceState().setNextDialogueId(3, 701);
			break;
		case 700:
			player.getActionSender().removeChatboxInterface();
			LeetPortal.doPortal(player, player.portal, Location.create(2442, 10146));
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 701:
			player.getActionSender().removeChatboxInterface();
			LeetPortal.doPortal(player, player.portal, Location.create(2870 + r.nextInt(3), 5359 + r.nextInt(3), 2));
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 702:
			player.getActionSender().removeChatboxInterface();
			LeetPortal.doPortal(player, player.portal, Location.create(2896 + r.nextInt(3), 5268 + r.nextInt(3)));
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 703:
			player.getActionSender().removeChatboxInterface();
			LeetPortal.doPortal(player, player.portal, Location.create(2830 + r.nextInt(3), 5302 + r.nextInt(3), 2));
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 704:
			player.getActionSender().removeChatboxInterface();
			LeetPortal.doPortal(player, player.portal, Location.create(2930 + r.nextInt(3), 5325 + r.nextInt(3), 2));
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 705:
			player.getActionSender().removeChatboxInterface();
			LeetPortal.doPortal(player, player.portal, Location.create(2273 + r.nextInt(2), 4684 + r.nextInt(2)));
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 706:
			player.getActionSender().sendDialogue(
					"Where to teleport?", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Chaos Elemental",
					"Giant Sea Snake",
					"Back...");
			player.getInterfaceState().setNextDialogueId(0, 707);
			player.getInterfaceState().setNextDialogueId(1, 801);
			player.getInterfaceState().setNextDialogueId(2, 698);
			break;
		case 707:
			player.getActionSender().removeChatboxInterface();
			LeetPortal.doPortal(player, player.portal, Location.create(3253 + r.nextInt(3), 3922 + r.nextInt(3)));
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 708:
			player.getActionSender().sendDialogue(
					"Where to teleport?", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Dagganoth cave",
					"Dagannoth kings",
					"Back...");
			player.getInterfaceState().setNextDialogueId(0, 700);
			player.getInterfaceState().setNextDialogueId(1, 709);
			player.getInterfaceState().setNextDialogueId(2, 698);
			break;
		case 709:
			player.getActionSender().removeChatboxInterface();
			LeetPortal.doPortal(player, player.portal, Location.create(2899, 4449));
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 710:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"Why hello there.");
			player.getInterfaceState().setNextDialogueId(0, 711);
			break;
		case 711:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.ANGER_1,
					"Him! Him!");
			player.getInterfaceState().setNextDialogueId(0, 712);
			break;
		case 712:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Who?");
			player.getInterfaceState().setNextDialogueId(0, 713);
			break;
		case 713:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.ANGER_1,
				"Lucien.");
			player.getInterfaceState().setNextDialogueId(0, 714);
			break;
		case 714:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"What did he do...? And who is he?");
			player.getInterfaceState().setNextDialogueId(0, 715);
			break;
		case 715:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.ANGER_3,
				"Ever the 5th stone age, he commanded for Zamorak's for-",
				"ces. Advisor he was to Zamorak. Rewardfully, Zamorak",
				"trained him, and at most, the High Preist of the world",
				"Saradomin aided with our forces. Times progressed.");
			player.getInterfaceState().setNextDialogueId(0, 716);
			break;
		case 716:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.ANGER_3,
					"Zamorak became angered with Saradomin's preachings.",
					"Saradomin taught peace, and only to use self-defense -",
					"a fool at will. We banished him from our kingdoms, and",
					"with so, cleared thoughts of his teachings from our");
			player.getInterfaceState().setNextDialogueId(0, 717);
			break;
		case 717:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.ANGER_3,
					"followers. Lucien was a fast learner. Quickly powerful",
					"became he. Anticipated some did to Lucien as a God.",
					"Teaching of him represented many ideals from Zamorak",
					"however his thoughts cleared not! My readings give me");
			player.getInterfaceState().setNextDialogueId(0, 718);
			break;
		case 718:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.ANGER_3,
					"information from the. Lucien believed in self-control. Little",
					"did he want to kill others. Angered became Zamorak. Upon",
					"the execution date of Lucien, he fled. Nowhere to be found",
					"- until now. His diminished self regained it's strength");
			player.getInterfaceState().setNextDialogueId(0, 719);
			break;
		case 719:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.ANGER_3,
					"through the times. In the burning years, Zamorak's powers",
					"were spread. Lucien was knowledgable of his powers, and",
					"quickly learned the powers of Zaros, whom diminished",
					"from our kingdom. The readings end.");
			player.getInterfaceState().setNextDialogueId(0, 720);
			break;
		case 720:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.ANGER_2,
					"Lucien is regaining his strength. We must stop him",
					"and his followers. Rumours speak about a mystical",
					"realm of chaos and destruction. Only the bravest and",
					"most powerful of all warriors can complete this task.");
			player.getInterfaceState().setNextDialogueId(0, 721);
			break;
		case 721:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.ANGER_3,
					"Accept my task will you?");
			player.getInterfaceState().setNextDialogueId(0, 722);
			break;
		case 722:
			player.getActionSender().sendDialogue("Accept the Mage's Quest?", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes",
					"No");
			player.getInterfaceState().setNextDialogueId(0, 723);
			player.getInterfaceState().setNextDialogueId(1, 97);
			break;
		case 723:
			player.getActionSender().removeChatboxInterface();
			player.magesRevenge = 1;
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
				"Thanks be to you.");
			player.getInterfaceState().setNextDialogueId(0, 724);
			break;
		case 724:
			player.getActionSender().sendDialogue("What to Say?", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Where can I find Lucien?",
					"You were once in the Wilderness near Edgeville?",
					"I'll be off, wish me luck!");
			player.getInterfaceState().setNextDialogueId(0, 727);
			player.getInterfaceState().setNextDialogueId(1, 729);
			player.getInterfaceState().setNextDialogueId(2, 725);
			break;
		case 725:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"I'll be off, wish me luck!");
			player.getInterfaceState().setNextDialogueId(0, 726);
			break;
		case 726:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
				"To whom may guide you on your journey be Zamorak.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 727:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Where can I find Lucien?");
			player.getInterfaceState().setNextDialogueId(0, 728);
			break;
		case 728:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
				"Drawrfs be near. North a travel.");
			player.getInterfaceState().setNextDialogueId(0, 731);
			break;
		case 729:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"You were once in the Wilderness near Edgeville?");
			player.getInterfaceState().setNextDialogueId(0, 730);
			break;
		case 730:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.LAUGH_1,
				"Ahh confused you are. I have many bretheren.");
			player.getInterfaceState().setNextDialogueId(0, 724);
			break;
		case 731:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
				"Saradomin's powers are too high in this city.",
				"When you find me, I will teleport you to the",
				"chaotic realm. Your journey while there can only",
				"be aided by your own might.");
			player.getInterfaceState().setNextDialogueId(0, 724);
			break;
		case 732:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"Ahh, hello again.",
				"Is this the place you wanted to meet?");
			player.getInterfaceState().setNextDialogueId(0, 733);
			break;
		case 733:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.EVIL,
					"Very strong my powers are in this area. Defeat",
					"to Lucien is must. Bodyguards Lucien will likely",
					"behold. Teleportation to the chaos realm will you?");
			player.getInterfaceState().setNextDialogueId(0, 734);
			break;
		case 734:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes",
					"No");
			player.getInterfaceState().setNextDialogueId(0, 735);
			player.getInterfaceState().setNextDialogueId(1, 97);
			break;
		case 735:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.EVIL,
					"Extreme power from Lucien. Power to his",
					"bodyguards are near his I expect. Ready you",
					"are to be teleported?");
			player.getInterfaceState().setNextDialogueId(0, 736);
			break;
		case 736:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes",
					"No");
			player.getInterfaceState().setNextDialogueId(0, 737);
			player.getInterfaceState().setNextDialogueId(1, 97);
			break;
		case 737:
			player.getActionSender().removeChatboxInterface();
			player.magesRevenge = 2;
			Sedridor.teleport(player, npc, Location.create(2443, 4956), "Chaos Realm");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 738:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Aye' mate! Help at last!");
			player.getInterfaceState().setNextDialogueId(0, 739);
			break;
		case 739:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.ALMOST_CRYING,
					"Who's kept you in this place?");
			player.getInterfaceState().setNextDialogueId(0, 740);
			break;
		case 740:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.SAD,
				"Them! I've been stuck here sealed by a spell.");
			player.getInterfaceState().setNextDialogueId(0, 741);
			break;
		case 741:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Who's 'them'?");
			player.getInterfaceState().setNextDialogueId(0, 742);
			break;
		case 742:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
				"The uh...");
			player.getInterfaceState().setNextDialogueId(0, 743);
			break;
		case 743:
			player.magesRevenge = 3;
			player.playSound(Sound.create(400, (byte) 1, 0));
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.SAD,
				"We've got company!");
			player.getInterfaceState().setNextDialogueId(0, 97);
			npc.getWalkingQueue().addStep(2445, 4957);
			npc.getWalkingQueue().finish();
			World.getWorld().submit(new Tickable(5) {
				public void execute() {
					Location loc = Location.create(2440, 4956);
					NPC khazard1 = new NPC(NPCDefinition.quickDef(253), loc, loc, loc, 0);
					World.getWorld().register(khazard1);
					this.stop();
				}
			});
			break;
		case 744:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.ALMOST_CRYING,
				"Help me! I'm defenseless!");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 745:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
				"Thank you brave warrior! I'm finally free!");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 746:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.EVIL,
				"DEATH TO THE TRESPASSER!");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 747:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
				"Again you appear. Slay to Lucien have you?");
			player.getInterfaceState().setNextDialogueId(0, 748);
			break;
		case 748:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"Lucien got away. But I did manage to slay",
				"his most powerful allie, Solus Dellagar.");
			player.getInterfaceState().setNextDialogueId(0, 749);
			break;
		case 749:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.EVIL,
				"Death to Lucien I wanted.");
			player.getInterfaceState().setNextDialogueId(0, 750);
			break;
		case 750:
			player.magesRevenge = 8;
			int list[] = { 2653, 2655, 2657, 2659 };
			for(int id : list) {
				player.getInventory().add(new Item(id));
			}
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
				"Valiance shows in you. Reward you may take.",
				"Zamorak commences you to a soldier. Armour",
				"one soldier should have. Take this.");
			player.getInterfaceState().setNextDialogueId(0, 751);
			break;
		case 751:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Thanks!");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 752:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Thanks I give you for slaying Solus.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 753:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
				"Greetings!");
			player.getInterfaceState().setNextDialogueId(0, 754);
			break;
		case 754:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"What is this place?",
					"Can I have a quest?");
			player.getInterfaceState().setNextDialogueId(0, 97); //TODO
			player.getInterfaceState().setNextDialogueId(1, 755);
			break;
		case 755:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Can I have a quest?");
			player.getInterfaceState().setNextDialogueId(0, 756);
			break;
		case 756:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
				"Aha!");
			player.getInterfaceState().setNextDialogueId(0, 757);
			break;
		case 757:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
				"Yes! A mighty and perilous quest fit only for the most",
				"powerful champions! And, at the end of it, you will earn",
				"the right to wear the legendary rune platebody!");
			player.getInterfaceState().setNextDialogueId(0, 758);
			break;
		case 758:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"So, what is this quest?");
			player.getInterfaceState().setNextDialogueId(0, 759);
			break;
		case 759:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"You'll have to speak to Oziach, the maker of rune",
					"armour. He sets the quests that champions must",
					"complete in order to wear it.");
			player.getInterfaceState().setNextDialogueId(0, 760);
			break;
		case 760:
			player.dragonSlayer = 1;
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Oziach lives in a hut, by the cliffs to the West of",
					"Edgeville. He can be a little...odd...sometimes, though.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 761:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Aye. 'tis a fair day my friend.");
			player.getInterfaceState().setNextDialogueId(0, 762);
			break;
		case 762:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Can you sell me a rune platebody?",
					"I'm not your friend.",
					"Yes, it's a very nice day.");
			player.getInterfaceState().setNextDialogueId(0, 763);
			break;
		case 763:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Can you sell me a rune platebody?");
			player.getInterfaceState().setNextDialogueId(0, 764);
			break;
		case 764:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DISTRESSED,
				"So, how does one know I 'ave some?");
			player.getInterfaceState().setNextDialogueId(0, 765);
			break;
		case 765:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"The Guildmaster of the Champions' Guild told me.");
			player.getInterfaceState().setNextDialogueId(0, 766);
			break;
		case 766:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DISINTERESTED,
				"Yes, I suppose he would, wouldn't he? He's always",
				"sending you fancy-pants 'heroes' up to bother me.",
				"Telling me I'll give you a quest or sommat like that.");
			player.getInterfaceState().setNextDialogueId(0, 767);
			break;
		case 767:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.ANGER_1,
					"Well, I'm not going to let just anyone wear my rune",
					"platemail. It's only for heroes. So, leave me alone.");
			player.getInterfaceState().setNextDialogueId(0, 768);
			break;
		case 768:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.SAD,
				"I thought you were going to give me a quest.");
			player.getInterfaceState().setNextDialogueId(0, 769);
			break;
		case 769:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.SAD,
					"*Sigh*");
			player.getInterfaceState().setNextDialogueId(0, 770);
			break;
		case 770:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.ANGER_1,
					"All right, I'll give ye a quest. I'll let you wear my rune",
					"platemail if ye...");
			player.getInterfaceState().setNextDialogueId(0, 771);
			break;
		case 771:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.LAUGH_3,
					"Slay the dragon of Crandor!");
			player.getInterfaceState().setNextDialogueId(0, 772);
			break;
		case 772:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"A dragon? That sounds like fun.");
			player.getInterfaceState().setNextDialogueId(0, 773);
			break;
		case 773:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Hah, yes, you are a typical reckless adventurer, aren't",
					"you? Now go kill the dragon and get out of my face.");
			player.getInterfaceState().setNextDialogueId(0, 774);
			break;
		case 774:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"But how can I defeat the dragon?");
			player.getInterfaceState().setNextDialogueId(0, 775);
			break;
		case 775:
			player.dragonSlayer = 2;
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Go talk to the Guildmaster in the Champions' Guild. He'll",
					"help ye out if yer so keen on doing a quest. I'm not",
					"going to be handholding any adventurers.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 776:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"I talked to Oziach and he gave me a quest.");
			player.getInterfaceState().setNextDialogueId(0, 777);
			break;
		case 777:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Oh? What did he tell you to do?");
			player.getInterfaceState().setNextDialogueId(0, 778);
			break;
		case 778:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"Defeat the dragon of Crandor.");
			player.getInterfaceState().setNextDialogueId(0, 779);
			break;
		case 779:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DISTRESSED,
				"The dragon of Crandor?");
			player.getInterfaceState().setNextDialogueId(0, 780);
			break;
		case 780:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Um, yes...");
			player.getInterfaceState().setNextDialogueId(0, 781);
			break;
		case 781:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.SAD,
				"Goodness, he hasn't given you an easy job, has he?");
			player.getInterfaceState().setNextDialogueId(0, 782);
			break;
		case 782:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"What's so special about this dragon?");
			player.getInterfaceState().setNextDialogueId(0, 783);
			break;
		case 783:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
				"Thirty years ago, Crandor was a thriving community",
				"with great tradition of mages and adventurers. Many",
				"Crandorians even earned the right to be part of the",
				"Champions' Guild!");
			player.getInterfaceState().setNextDialogueId(0, 784);
			break;
		case 784:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.SAD,
				"One of their adventurers went too far, however. He",
				"descended into the volcano in the centre of Crandor",
				"and woke the dragon Elvarg.");
			player.getInterfaceState().setNextDialogueId(0, 785);
			break;
		case 785:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"He must have faught valiently against the dragon",
					"because they say that, to this day, she has a scar",
					"down her side.");
			player.getInterfaceState().setNextDialogueId(0, 786);
			break;
		case 786:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"But the dragon still won the fight. She emergered and laid",
					"waste to the whole of Crandor with her fire breath!");
			player.getInterfaceState().setNextDialogueId(0, 787); //TODO
			break;
		case 787:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Some refugees managed to escape in fishing boats.",
					"They landed on the coast, north of Rimmington, and",
					"set up camp but the dragon followed them and burned",
					"the camp to the ground.");
			player.getInterfaceState().setNextDialogueId(0, 788);
			break;
		case 788:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Out of all the people in Crandor there were only three",
					"survivors: a trio of wizards who used magic to escape.",
					"Their names were Thalzar, Lozar, Melzar.");
			player.getInterfaceState().setNextDialogueId(0, 789);
			break;
		case 789:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"If you're serious about taking on Elvarg, first you'll",
					"need to get to Crandor. The island is surrounded by",
					"dangerous reefs, so you'll need a ship capable of getting",
					"through them and a map to show you the way.");
			player.getInterfaceState().setNextDialogueId(0, 790);
			break;
		case 790:
			player.dragonSlayer = 3;
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"When you reach Crandor, you'll also need some kind of",
					"protection against the dragon's breath.");
			player.getInterfaceState().setNextDialogueId(0, 791);
			break;
		case 791:
			//TODO
			break;
		case 792:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Ello dearie. Would ya like to try some sweets?");
			player.getInterfaceState().setNextDialogueId(0, 793);
			break;
		case 793:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes, it sounds so sweet.",
					"No thanks. My dentist said candy's bad for your teeth.");
			player.getInterfaceState().setNextDialogueId(0, 796);
			player.getInterfaceState().setNextDialogueId(1, 794);
			break;
		case 794:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISINTERESTED,
				"No thanks. My dentist said candy's bad for your teeth.");
			player.getInterfaceState().setNextDialogueId(0, 795);
			break;
		case 795:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.ANGER_1,
				"A fool he is!");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 796:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"Yes, it sounds so sweet.");
			player.getInterfaceState().setNextDialogueId(0, 797);
			break;
		case 797:
			if(player.getInventory().getCount(995) >= 5000) {
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"Yes indeed. You can take some for 5,000 coins.");
				player.getInterfaceState().setNextDialogueId(0, 798);	
			} else {
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.SAD,
					"Sorry hun but they cost 5,000 coins each.");
				player.getInterfaceState().setNextDialogueId(0, 97);	
			}
			break;
		case 798:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Pay fee",
					"Nevermind");
			player.getInterfaceState().setNextDialogueId(0, 799);
			player.getInterfaceState().setNextDialogueId(1, 97);
			break;
		case 799:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"All right here you go.");
			player.getInterfaceState().setNextDialogueId(0, 800);
			break;
		case 800:
			player.getInventory().remove(new Item(995, 5000));
			player.getInventory().add(new Item(4561));
			player.getActionSender().sendDialogue(player.getName(), DialogueType.MESSAGE_MODEL_LEFT, 4561, FacialAnimation.DEFAULT,
				"The witch hands you some sweets.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 801:
			player.getActionSender().removeChatboxInterface();
			LeetPortal.doPortal(player, player.portal, Location.create(2464 + r.nextInt(1), 4771 + r.nextInt(1)));
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 802:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"Hello.");
			player.getInterfaceState().setNextDialogueId(0, 803);
			break;
		case 803:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Ahh...a hero!");
			player.getInterfaceState().setNextDialogueId(0, 804);	
			break;
		case 804:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Oh *uhum* I'm a hero!",
					"I'm not a hero.");
			player.getInterfaceState().setNextDialogueId(0, 807);
			player.getInterfaceState().setNextDialogueId(1, 805);
			break;
		case 805:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"I'm not a hero.");
			player.getInterfaceState().setNextDialogueId(0, 806);
			break;
		case 806:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.SAD,
				"Omnipotent you have being.",
				"I perceived a hero in you.");
			player.getInterfaceState().setNextDialogueId(0, 97);
			break;
		case 807:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"Oh *uhum* I'm a hero!");
			player.getInterfaceState().setNextDialogueId(0, 808);
			break;
		case 808:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
				"Just as I perceived! Hero...");
			player.getInterfaceState().setNextDialogueId(0, 809);	
			break;
		case 809:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
				"It's " + player.getName() + ".");
			player.getInterfaceState().setNextDialogueId(0, 810);
			break;
		case 810:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
				"Oh yes, " + player.getName() + ". Please warrior,",
				"help me on a journey!");
			player.getInterfaceState().setNextDialogueId(0, 811);
			break;
		case 811:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"And that journey is...?");
			player.getInterfaceState().setNextDialogueId(0, 812);
			break;
		case 812:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.ANGER_1,
				"The downfall of Barrelchest!");
			player.getInterfaceState().setNextDialogueId(0, 813);	
			break;
		case 813:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
				"Why's it such an importance to destroy this 'Barrelchest'?",
				"And who is he anyway?");
			player.getInterfaceState().setNextDialogueId(0, 814);
			break;
		case 814:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.ANGER_1,
				"A former breteren - lost in his obsessions.",
				"Once a dwarf, and now with his new size he's",
				"betrayed my family, and attacked our land.");
			player.getInterfaceState().setNextDialogueId(0, 815);
			break;
		case 815:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DISTRESSED,
					"How do I find him?");
			player.getInterfaceState().setNextDialogueId(0, 816);
			break;
		case 816:
			player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
					"You must join with the other forces. Waves",
					"of monsters will attempt to perish our forces,",
					"on the final wave, Barrelchest will be summoned!");
			player.getInterfaceState().setNextDialogueId(0, 817);
			break;
		case 817:
			player.barrelChest = true;
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Let me join the forces!",
					"Could you explain the directions again?",
					"Cancel");
			player.getInterfaceState().setNextDialogueId(0, 818);
			player.getInterfaceState().setNextDialogueId(1, 812);
			player.getInterfaceState().setNextDialogueId(2, 806);
			break;
		case 818:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
				"Let me join the forces!");
			player.getInterfaceState().setNextDialogueId(0, 819);
			break;
		case 819:
			if(player.barrelWait <= 0) {
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.HAPPY,
					"Now THAT'S a hero!",
					"Tata my fellow " + player.getName() + "!",
					(World.getWorld().getBarrelchest().players.size() < 1 ? 
							"No forces are waiting for you" : 
								World.getWorld().getBarrelchest().players.size() + " forces are waiting for you")
								+ ", good luck!");
				player.getInterfaceState().setNextDialogueId(0, 820);
			} else {
				player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.SAD,
						"Sorry soldier, but you need to heal because",
						"of your defeat. You can rejoin in " + player.barrelWait + " more seconds.");
				player.getInterfaceState().setNextDialogueId(0, 97);
			}
			break;
		case 820:
			Sedridor.teleport(player, npc, Misc.random(1) > 0 ? Location.create(2759, 10064) : Location.create(2807, 10105), "Barrelchest Cave");
			World.getWorld().submit(new Tickable(3) {
				@Override
				public void execute() {
					World.getWorld().getBarrelchest().enterGame(player, false);
					this.stop();
				}
			});
			break;
		case 821:
			Sedridor.teleport(player, npc, Location.create(2373, 3615), "The Hut");
			break;
		case 97:
		case 98:
		default:
			player.getActionSender().removeChatboxInterface();
			break;
		}
	}

	public static void advanceDialogue(Player player, int index) {
		int dialogueId = player.getInterfaceState().getNextDialogueId(index);
		if (dialogueId == -1) {
			player.getActionSender().removeChatboxInterface();
			return;
		}
		openDialogue(player, dialogueId);
	}

}