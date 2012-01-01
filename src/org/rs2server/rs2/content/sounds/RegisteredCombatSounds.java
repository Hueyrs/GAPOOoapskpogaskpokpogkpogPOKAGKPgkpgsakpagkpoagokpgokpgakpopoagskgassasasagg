package org.rs2server.rs2.content.sounds;

import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.util.Misc;


/**
 * @author Jinrake
 * Holds registered sounds
 * Www.Jinrake.Info
 * Carnage Kingdom
 */

public class RegisteredCombatSounds {
	
	public static int getNpcAttackSounds(NPC n)
	{		
		switch(n.getDefinition().getId()) {
			case 122://hobgoblin
			case 1771:
			case 1772:
			case 1773:
			case 1774:
			case 1775:
			case 1776:// goblins	
				return 123;
			case 50: //kbd
			case 51:
			case 53:
			case 52:
			case 1590://metal dragon
			case 1591:
			case 1592:
			case 941://green dragon
			return 115;
			case 90:
			case 91:
			case 92:
			case 93: //skeleton
			return 108;
			case 708: //imp
			return 64;
			case 103:
			case 104:
			case 655:
			case 491: //ghost
			return 61;
			case 1017:
			case 2693:
			case 41: // chicken
			return 26;
			case 78:
			case 412: //bat
			case 2734:
			case 2627://tzhaar 
			return 1;
			case 117:
			case 116:
			case 111:
			case 1585:
			case 110:
			case 112: //giant
			return 56;
			case 47://rat
			return 16;
			case 1195: //bear
			return 20;
			case 1766:
			case 1767:
			case 81: // cow
			return 4;
	
			case 82:
			case 83:
			case 84: //lesser
			return 47;
		}
		String npc = n.getDefinition().getName().toLowerCase();		
		if (npc.contains("bat")) {
			return 1;
		}		
		if (npc.contains("cow")) {
			return 4;
		}		
		if (npc.contains("imp"))
		{
			return 11;
		}		
		if (npc.contains("rat"))
		{
			return 17;
		}		
		if (npc.contains("duck"))
		{
			return 26;
		}
		if (npc.contains("wolf"))
		{
			return 28;
		}		
		if (npc.contains("dragon"))
		{
			return 47;
		}		
		if (npc.contains("ghost"))
		{
			return 57;
		}		
		if (npc.contains("goblin"))
		{
			return 88;
		}		
		if (npc.contains("skeleton") || npc.contains("demon") || npc.contains("ogre") || npc.contains("giant") || npc.contains("tz-") || npc.contains("jad"))
		{
			return 48;
		}		
		if (npc.contains("zombie"))
		{
			return 1155;
		}		
		if (npc.contains("bear")) {
			return 12;
		}
		if (npc.contains("man") || npc.contains("woman") || npc.contains("monk"))
		{
			return 417;
		}		
		return Misc.random(6) > 3 ? 398 : 394;
		
	}
	
	public static int getNpcBlockSound(NPC n)
	{		
		String npc = n.getDefinition().getName().toLowerCase();			
		if (npc.contains("bat")) {
			return 7;
		}		
		if (npc.contains("bear")) {
			return 14;
		}
		if (npc.contains("cow")) {
			return 5;
		}		
		if (npc.contains("imp"))
		{
			return 11;
		}		
		if (npc.contains("rat"))
		{
			return 16;
		}		
		if (npc.contains("duck"))
		{
			return 24;
		}
		if (npc.contains("wolf"))
		{
			return 34;
		}		
		if (npc.contains("dragon"))
		{
			return 45;
		}		
		if (npc.contains("ghost"))
		{
			return 53;
		}		
		if (npc.contains("goblin"))
		{
			return 87;
		}		
		if (npc.contains("skeleton") || npc.contains("demon") || npc.contains("ogre") || npc.contains("giant") || npc.contains("tz-") || npc.contains("jad"))
		{
			return 1154;
		}		
		if (npc.contains("zombie"))
		{
			return 1151;
		}		
		if (npc.contains("man") && !npc.contains("woman"))
		{
			return 816;
		}		
		if (npc.contains("monk"))
		{
			return 816;
		}	

		if (!npc.contains("man") && npc.contains("woman"))
		{
			return 818;
		}		
		return 791;
		
	}
	
	public static int getNpcDeathSounds(NPC n)
	{		
		switch(n.getDefinition().getId()) {
			case 122://hobgoblin
			case 1771:
			case 1772:
			case 1773:
			case 1774:
			case 1775:
			case 1776:// goblins	
				return 125;
			case 82:
			case 83:
			case 84: //lesser
			return 45;
			case 708: //imp
			return 63;
			case 78:
			case 412: //bat
			case 2734:
			case 2627://tzhaar 
			return 0;
			case 117:
			case 116:
			case 111:
			case 1585:
			case 110:
			case 112: //giant
			return 55;
			case 1766:
			case 1767:
			case 81: // cow
			return 5;
			case 90:
			case 91:
			case 92:
			case 93: //skeleton
			return 109;
			case 66:
			case 67:
			case 168:
			case 169:
			case 162:
			case 68://gnomes
				return 7;
			case 73:
			case 74:
			case 75:
			case 76: //zombie
			case 77:
			return 147;
			case 103:
			case 104:
			case 655:
			case 491: //ghost
			return 60;
			case 1195: //bear
			return 19;
			case 47://rat
			return 15;
			case 1017:
			case 2693:
			case 41: // chicken
			return 25;
			case 49://hellhound
			return 121;
		}
		
		String npc = n.getDefinition().getName().toLowerCase();	
		if (npc.contains("bat")) {
			return 7;
		}		
		if (npc.contains("cow")) {
			return 3;
		}		
		if (npc.contains("imp"))
		{
			return 9;
		}		
		if (npc.contains("rat"))
		{
			return 15;
		}		
		if (npc.contains("duck"))
		{
			return 25;
		}
		if (npc.contains("wolf"))
		{
			return 35;
		}		
		if (npc.contains("dragon"))
		{
			return 44;
		}		
		if (npc.contains("ghost"))
		{
			return 60;
		}	
		if (npc.contains("bear")) {
			return 13;
		}
		if (npc.contains("goblin"))
		{
			return 125;
		}		
		if (npc.contains("skeleton") || npc.contains("demon") || npc.contains("ogre") || npc.contains("giant") || npc.contains("tz-") || npc.contains("jad"))
		{
			return 70;
		}		
		if (npc.contains("zombie"))
		{
			return 1140;
		}		
		return 70;
		
	}
	
	public static int getPlayerBlockSounds(Player c) {
	
		int blockSound = 511;
		
		if (c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 2499 || 
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 2501 || 
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 2503 || 
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 4746 || 
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 4757 || 
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 10330) {//Dragonhide sound
			blockSound = 24;
		}
		else if (c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 10551 ||//Torso
		    	c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 10438) {//3rd age
			blockSound = 32;//Weird sound
		}
		else if (c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 10338 ||//3rd age
		    	c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 7399 ||//Enchanted
		    	c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 6107 ||//Ghostly
		    	c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 4091 ||//Mystic
		    	c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 4101 ||//Mystic
		    	c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 4111 ||//Mystic
		    	c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 1035 ||//Zamorak
		    	c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 12971) {//Combat
			blockSound = 14;//Robe sound
		}
		else if (c.getEquipment().get(Equipment.EquipmentType.SHIELD.getSlot()).getId() == 4224) {//Crystal Shield
			blockSound = 30;//Crystal sound
		}
		else if (c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 1101 ||//Chains
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 1103|| 
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 1105|| 
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 1107|| 
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 1109|| 
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 1111|| 
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 1113|| 
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 1115|| //Plates
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 1117|| 
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 1119|| 
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 1121|| 
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 1123|| 
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 1125|| 
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 1127|| 
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 4720|| //Barrows armour
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 4728|| 
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 4749||
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 4712||
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 11720||//Godwars armour
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 11724||
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 3140||//Dragon
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 2615||//Fancy
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 2653||
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 2661||
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 2669||
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 2623||
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 3841||
		    c.getEquipment().get(Equipment.EquipmentType.BODY.getSlot()).getId() == 1127) {//Metal armour sound
			blockSound = 15;
		} else {
			blockSound = 511;
		}
		return blockSound;
	}
	
	public static int getWeaponSound(Player c)	{
	
		String wep = c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getDefinition().getName().toLowerCase();
		
		if (c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 4718) {//Dharok's Greataxe
			return 1320;
		}
		if (c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 4734) {//Karil's Crossbow
			return 1081;
		}
		if (c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 4747) {//Torag's Hammers
			return 1330;
		}
		if (c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 4710) {//Ahrim's Staff
			return 2555;
		}
		if (c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 4755) {//Verac's Flail
			return 1323;
		}
		if (c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 4726) {//Guthan's Warspear
			return 2562;
		}
		
		if (c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 772
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1379
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1381
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1383
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1385
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1387
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1389
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1391
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1393
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1395
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1397
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1399
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1401
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1403
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1405
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1407
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1409
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 9100) { //Staff wack
			return 394;
		}
		if (c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 839
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 841
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 843
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 845
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 847
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 849
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 851
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 853
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 855
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 857
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 859
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 861
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 4734
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 2023 //RuneC'Bow
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 4212
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 4214
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 4934
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 9104
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 9107) { //Bows/Crossbows
			return 370;
		}
		if (c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1363
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1365
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1367
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1369
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1371
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1373
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1375
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1377
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1349
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1351
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1353
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1355
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1357
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1359
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1361
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 9109) { //BattleAxes/Axes
			return 399;
		}
		if (c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 4718 || c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 7808)
		{ //Dharok GreatAxe
			return 400;
		}
		if (c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 6609
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1307
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1309
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1311
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1313
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1315
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1317
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1319) { //2h
			return 425;
		}		
		if (c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1321 
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1323 
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1325 
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1327 
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1329 
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1331 
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 1333
				|| c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 4587) { //Scimitars
			return 396;
		}		
		if (wep.contains("halberd"))
		{
			return 420;
		}		
		if (wep.contains("long"))
		{
			return 396;
		}		
		if (wep.contains("knife"))
		{
			return 368;
		}		
		if (wep.contains("javelin"))
		{
			return 364;
		}

		if (c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 9110) {
			return 401;
		}
		if (c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 4755) {
			return 1059;
		}
		if (c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 4153) {
			return 1079;
		}
		if (c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 9103) {
			return 385;
		}
		if (c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == -1) { // fists
			return 417;
		}
		/*if (c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 2745 || c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 2746 || c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 2747 || c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 2748) { // Godswords
				return 390;
			}*/
		if (c.getEquipment().get(Equipment.EquipmentType.WEAPON.getSlot()).getId() == 4151) {
			return 1080;
		} else {
			return 398; //Daggers(this is enything that isn't added)
		}
	}
	
	
	public static int specialSounds(int id)
	{		
		if (id == 4151) //whip
		{
		return 1081;
		}		
		if (id == 5698) //dds
		{
		return 793;
		}		
		if (id == 1434)//Mace
		{
		return 387;
		}		
		if (id == 3204) //halberd
		{
		return 420;
		}		
		if (id == 4153) //gmaul
		{
		return 1082;
		}		
		if (id == 7158) //d2h
		{
		return 426;
		}
		if (id == 4587) //dscim
		{
		return 1305;
		}		
		if (id == 1215) //Dragon dag
		{
		return 793;
		}		
		if (id == 1305) //D Long
		{
		return 390;
		}		
		if (id == 861) //MSB
		{
		return 386;
		}	
		if (id == 11235) //DBow
		{
		return 386;
		}
		if (id == 6739) //D Axe
		{
		}		
		if (id == 1377) //DBAxe
		{
		return 389;
		}		
		return -1;		
	}
}