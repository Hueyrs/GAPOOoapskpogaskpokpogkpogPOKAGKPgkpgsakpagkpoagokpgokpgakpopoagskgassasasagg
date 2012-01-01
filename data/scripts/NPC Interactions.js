importPackage(org.rs2server.rs2.model)

function talkTo4542(player, npc) {
	if(player.barrelChest) {
		DialogueManager.openDialogue(player, 817);
	} else {
		DialogueManager.openDialogue(player, 802);
	}
}

function talkTo5200(player, npc) {
	DialogueManager.openDialogue(player, 792);
}

function talkTo863(player, npc) {
	if(player.magesRevenge == 2) {
		DialogueManager.openDialogue(player, 738);
	}
	if(player.magesRevenge == 3) {
		DialogueManager.openDialogue(player, 744);
	}
	if(player.magesRevenge == 4) {
		DialogueManager.openDialogue(player, 745);
	}
}

function talkTo6370(player, npc) {
	if(player.getLocation().getX() >= 3013 && player.getLocation().getX() <= 3020 && player.getLocation().getY() >= 3479 && player.getLocation().getY() <= 3492) {
		if(player.magesRevenge == 1) {
			DialogueManager.openDialogue(player, 732);
		}
		if(player.magesRevenge >= 8) {
			DialogeManager.openDialogue(player, 710);
		}
	} else {
		if(player.magesRevenge >= 1 && player.magesRevenge <= 6) {
	    	DialogueManager.openDialogue(player, 724);
		} else if(player.magesRevenge == 7) {
			DialogueManager.openDialogue(player, 747);
		} else if(player.magesRevenge >= 8) {
			DialogueManager.openDialogue(player, 752);
		} else {
	    	DialogueManager.openDialogue(player, 710);
		}
	}
}

function talkTo5477(player, npc) {
	DialogueManager.openDialogue(player, 687);
}

function talkTo4297(player, npc) {
	DialogueManager.openDialogue(player, 655);
}

function talkTo5571(player, npc) {
	DialogueManager.openDialogue(player, 652);
}
function talkTo388(player, npc) {
	DialogueManager.openDialogue(player, 644);
}

function talkTo4286(player, npc) {
	DialogueManager.openDialogue(player, 631);
}

function talkTo4285(player, npc) {
	DialogueManager.openDialogue(player, 629);
}

function talkTo4296(player, npc) {
	DialogueManager.openDialogue(player, 597);
}

function talkTo4289(player, npc) {
	DialogueManager.openDialogue(player, 572);
}

function talkTo4290(player, npc) {
	DialogueManager.openDialogue(player, 571);
}

function talkTo945(player, npc) {
	DialogueManager.openDialogue(player, 561);
}
function talkTo943(player, npc) {
	DialogueManager.openDialogue(player, 565);
}

function talkTo4906(player, npc) {
	DialogueManager.openDialogue(player, 545);
}

function talkTo3807(player, npc) {
	DialogueManager.openDialogue(player, 531);
}

function talkTo1217(player, npc) {
	DialogueManager.openDialogue(player, 529);
}

function talkTo5195(player, npc) {
	DialogueManager.openDialogue(player, 523);
}

function talkTo705(player, npc) {
	DialogueManager.openDialogue(player, 505);
}

function talkTo838(player, npc) {
	DialogueManager.openDialogue(player, 503);
}
function talkTo837(player, npc) {
	DialogueManager.openDialogue(player, 503);
}

function talkTo836(player, npc) {
	DialogueManager.openDialogue(player, 482);
}

function talkTo3331(player, npc) {
	DialogueManager.openDialogue(player, 451);
}

function talkTo546(player, npc) {
	DialogueManager.openDialogue(player, 436);
}

function talkTo5925(player, npc) {
	DialogueManager.openDialogue(player, 429);
}

function talkTo547(player, npc) {
	DialogueManager.openDialogue(player, 415);
}

function talkTo11(player, npc) {
	DialogueManager.openDialogue(player, 408);
}

function talkTo5914(player, npc) {
	DialogueManager.openDialogue(player, 394);
}

function talkTo639(player, npc) {
	DialogueManager.openDialogue(player, 404);
}

function talkTo457(player, npc) {
	DialogueManager.openDialogue(player, 382);
}

function talkTo2244(player, npc) {
	DialogueManager.openDialogue(player, 371);
}

function talkTo3777(player, npc) {
	DialogueManager.openDialogue(player, 362);
}

function talkTo0(player, npc) {
	DialogueManager.openDialogue(player, 354);
}

function talkTo782(player, npc) {
	DialogueManager.openDialogue(player, 347);
}
function talkTo784(player, npc) {
	DialogueManager.openDialogue(player, 347);
}

function talkTo780(player, npc) {
	DialogueManager.openDialogue(player, 323);
}

function talkTo4905(player, npc) {
	DialogueManager.openDialogue(player, 303);
}

function talkTo882(player, npc) {
 	DialogueManager.openDialogue(player, 298);
}

function talkTo6135(player, npc) {
	DialogueManager.openDialogue(player, 280);
}
function talkTo6136(player, npc) {
	DialogueManager.openDialogue(player, 280);
}
function talkTo6137(player, npc) {
	DialogueManager.openDialogue(player, 280);
}
function talkTo6138(player, npc) {
	DialogueManager.openDialogue(player, 280);
}
function talkTo6139(player, npc) {
	DialogueManager.openDialogue(player, 280);
}

function talkTo3295(player, npc) {
	DialogueManager.openDialogue(player, 266);
}

function talkTo2290(player, npc) {
	DialogueManager.openDialogue(player, 261);
}

function talkTo4247(player, npc) {
	DialogueManager.openDialogue(player, 244);
}

function talkTo1(player, npc) {
	DialogueManager.openDialogue(player, 235);
}
function talkTo2(player, npc) {
	DialogueManager.openDialogue(player, 235);
}
function talkTo3(player, npc) {
	DialogueManager.openDialogue(player, 235);
}
function talkTo4(player, npc) {
	DialogueManager.openDialogue(player, 235);
}
function talkTo5(player, npc) {
	DialogueManager.openDialogue(player, 235);
}
function talkTo6(player, npc) {
	DialogueManager.openDialogue(player, 235);
}

function talkTo606(player, npc) {
	DialogueManager.openDialogue(player, 229);
}

function talkTo300(player, npc) {
	DialogueManager.openDialogue(player, 214);
}

function talkTo598(player, npc) {
	DialogueManager.openDialogue(player, 124);
}

function tradeWith598(player, npc) {
	DialogueManager.openDialogue(player, 126);
}

function talkTo599(player, npc) {
	DialogueManager.openDialogue(player, 133);
}

function talkTo520(player, npc) {
	DialogueManager.openDialogue(player, 57);
}

function tradeWith520(player, npc) {
	Shop.open(player, 0, 1);
}

function talkTo736(player, npc) {
	DialogueManager.openDialogue(player, 137);
}

function talkTo577(player, npc) {
	DialogueManager.openDialogue(player, 140);
}

function tradeWith577(player, npc) {
	Shop.open(player, 4, 1);
}

function talkTo580(player, npc) {
	DialogueManager.openDialogue(player, 143);
}

function tradeWith580(player, npc) {
	Shop.open(player, 5, 1);
}

function talkTo581(player, npc) {
	DialogueManager.openDialogue(player, 146);
}

function tradeWith581(player, npc) {
	Shop.open(player, 6, 1);
}

function talkTo594(player, npc) {
	DialogueManager.openDialogue(player, 149);
}

function tradeWith594(player, npc) {
	Shop.open(player, 7, 1);
}

function talkTo579(player, npc) {
	DialogueManager.openDialogue(player, 152);
}

function tradeWith579(player, npc) {
	Shop.open(player, 8, 1);
}

function talkTo583(player, npc) {
	DialogueManager.openDialogue(player, 155);
}

function tradeWith583(player, npc) {
	Shop.open(player, 9, 1);
}

function talkTo559(player, npc) {
	DialogueManager.openDialogue(player, 158);
}

function tradeWith559(player, npc) {
	Shop.open(player, 10, 1);
}

function talkTo558(player, npc) {
	DialogueManager.openDialogue(player, 161);
}

function tradeWith558(player, npc) {
	Shop.open(player, 11, 1);
}

function talkTo557(player, npc) {
	DialogueManager.openDialogue(player, 164);
}

function tradeWith557(player, npc) {
	Shop.open(player, 12, 1);
}

function talkTo376(player, npc) {
	DialogueManager.openDialogue(player, 167);
}

function tradeWith376(player, npc) {
	DialogueManager.openDialogue(player, 168);
}

function talkTo377(player, npc) {
	DialogueManager.openDialogue(player, 171);
}

function tradeWith377(player, npc) {
	DialogueManager.openDialogue(player, 172);
}

function talkTo380(player, npc) {
	DialogueManager.openDialogue(player, 171);
}

function tradeWith380(player, npc) {
	DialogueManager.openDialogue(player, 172);
}

function talkTo1303(player, npc) {
	if(player.completedFremennikTrials()) {
		DialogueManager.openDialogue(player, 174);
	} else {
		DialogueManager.openDialogue(player, 181);
	}	
}

function tradeWith1303(player, npc) {
	if(player.completedFremennikTrials()) {
		Shop.open(player, 13, 1);
	} else {
		player.getActionSender().sendMessage("You must complete the Fremennik Trials minigame before accessing this shop.");
	}
}

function talkTo1369(player, npc) {
	DialogueManager.openDialogue(player, 177);
}

function tradeWith1369(player, npc) {
	Shop.open(player, 14, 1);
}

function talkTo1301(player, npc) {
	DialogueManager.openDialogue(player, 188);
}

function tradeWith1301(player, npc) {
	Shop.open(player, 15, 1);
}

function talkTo549(player, npc) {
	DialogueManager.openDialogue(player, 191);
}

function tradeWith549(player, npc) {
	Shop.open(player, 16, 1);
}

function talkTo379(player, npc) {
	DialogueManager.openDialogue(player, 197);
}

function talkTo568(player, npc) {
	DialogueManager.openDialogue(player, 201);
}

function tradeWith568(player, npc) {
	Shop.open(player, 18, 1);
}

function talkTo2620(player, npc) {
	DialogueManager.openDialogue(player, 204);
}

function tradeWith2620(player, npc) {
	Shop.open(player, 19, 1);
}

function talkTo2623(player, npc) {
	DialogueManager.openDialogue(player, 207);
}

function tradeWith2623(player, npc) {
	Shop.open(player, 20, 1);
}

function talkTo2619(player, npc) {
	DialogueManager.openDialogue(player, 210);
}