importPackage(org.rs2server.rs2.model)
importPackage(org.rs2server.rs2.model.skills)
importPackage(java.util)

function lumbridgeGate(player, obstacle, object) {
	var y = 1;
	var dir = 0;
	if(player.getLocation().getY() >= 3191) {
		y = -1;
		dir = 2;
	}
	var forceMovementVars =  [ 0, 0, 0, y, 10, 40, dir, 2 ];
	Agility.forceMovement(player, Animation.create(7268), forceMovementVars, 1, true);
}

function shantayPass(player, obstacle, object) {
	var y = 3;
	var dir = 0;
	if(player.getLocation().getY() >= 3117) {
		y = -1;
		dir = 3;
	}
	var forceMovementVars =  [ 0, 0, 0, y, 10, 40, dir, 2 ];
	Agility.forceMovement(player, player.getWalkAnimation(), forceMovementVars, 1, true);
}

function jumpFence(player, obstacle, object) {
	var y = 1;
	var dir = 0;
	if(player.getLocation().getY() >= 3335) {
		y = -1;
		dir = 2;
	}
	var forceMovementVars =  [ 0, 0, 0, y, 10, 40, dir, 2 ];
	Agility.forceMovement(player, Animation.create(2750), forceMovementVars, 1, true);
}

function wildernessDitch(player, obstacle, object) {
	var y = 3;
	var dir = 0;
	if(player.getLocation().getY() > 3520) {
		y = -3;
		dir = 2;
	}
	var forceMovementVars =  [ 0, 0, 0, y, 33, 60, dir, 2 ];
	Agility.forceMovement(player, Animation.create(6132), forceMovementVars, 1, true);
}

function faladorUnderwallTunnel(player, obstacle, object) {
	var y = 4;
	var dir = 0;
	if(player.getLocation().getY() >= 3313) {
		y = -4;
		dir = 4;
	}
	var forceMovementVars =  [ 0, 0, 0, y, 20, 60, dir, 2 ];
	Agility.forceMovement(player, Animation.create(839), forceMovementVars, 1, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function faladorCrumblingWall(player, obstacle, object) {
	var x = 2;
	var dir = 1;
	if(player.getLocation().getX() >= 2936) {
		x = -2;
		dir = 3;
	}
	var forceMovementVars =  [ 0, 0, x, 0, 20, 60, dir, 2 ];
	Agility.forceMovement(player, Animation.create(839), forceMovementVars, 1, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function gnomeLogBalance(player, obstacle, object) {
	if(player.getLocation().getX() != 2474) {
		player.removeAttribute("busy");
		return;
	}
	var gnomeAgilityCourseLvl = player.getAttribute("gnomeAgilityCourse");
	if(gnomeAgilityCourseLvl == null) {
		player.setAttribute("gnomeAgilityCourse", 1);
	}
	Agility.setRunningToggled(player, false, 7);
	Agility.forceWalkingQueue(player, Animation.create(762), 2474, 3429, 0, 7, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function gnomeObstacleNet(player, obstacle, object) {
	player.face(Location.create(player.getLocation().getX(), 0, 0));
	var gnomeAgilityCourseLvl = player.getAttribute("gnomeAgilityCourse");
	if(gnomeAgilityCourseLvl == 1) {
		player.setAttribute("gnomeAgilityCourse", 2);
	}
	Agility.forceTeleport(player, Animation.create(828), Location.create(player.getLocation().getX(), 3424, 1), 0, 2);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function gnomeTreeBranch(player, obstacle, object) {
	var gnomeAgilityCourseLvl = player.getAttribute("gnomeAgilityCourse");
	if(gnomeAgilityCourseLvl == 2) {
		player.setAttribute("gnomeAgilityCourse", 3);
	}
	Agility.forceTeleport(player, Animation.create(828), Location.create(2473, 3420, 2), 0, 2);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function gnomeBalanceRope(player, obstacle, object) {
	if(!player.getLocation().equals(Location.create(2477, 3420, 2))) {
		player.removeAttribute("busy");
		return;
	}
	var gnomeAgilityCourseLvl = player.getAttribute("gnomeAgilityCourse");
	if(gnomeAgilityCourseLvl == 3) {
		player.setAttribute("gnomeAgilityCourse", 4);
	}
	Agility.setRunningToggled(player, false, 7);
	Agility.forceWalkingQueue(player, Animation.create(762), 2483, 3420, 0, 7, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function gnomeTreeBranch2(player, obstacle, object) {
	var gnomeAgilityCourseLvl = player.getAttribute("gnomeAgilityCourse");
	if(gnomeAgilityCourseLvl == 4) {
		player.setAttribute("gnomeAgilityCourse", 5);
	}
	Agility.forceTeleport(player, Animation.create(828), Location.create(2485, 3419, 0), 0, 2);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function gnomeObstacleNet2(player, obstacle, object) {
	if(player.getLocation().getY() != 3425) {
		player.removeAttribute("busy");
		player.getActionSender().sendMessage("You can't go over the net from here.");
		return;
	}
	player.face(Location.create(player.getLocation().getX(), 9999, 0));
	var gnomeAgilityCourseLvl = player.getAttribute("gnomeAgilityCourse");
	if(gnomeAgilityCourseLvl == 5) {
		player.setAttribute("gnomeAgilityCourse", 6);
	}
	Agility.forceTeleport(player, Animation.create(828), Location.create(player.getLocation().getX(), 3427, 0), 0, 2);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function gnomeObstaclePipe(player, obstacle, object) {
	if(!player.getLocation().equals(Location.create(2484, 3430, 0)) && !player.getLocation().equals(Location.create(2487, 3430, 0))) {
		player.removeAttribute("busy");
		return;
	}
	if(player.getAttribute("gnomeAgilityCourse") != null) {
		var courseLevel = player.getAttribute("gnomeAgilityCourse");
		if(courseLevel == 6) {
			player.getActionSender().sendMessage("You completed the course!");
			player.getSkills().addExperience(Skills.AGILITY, 40);
			player.removeAttribute("gnomeAgilityCourse");
		}
	}
	var forceMovementVars =  [ 0, 2, 0, 5, 45, 100, 0, 3 ];
	var forceMovementVars2 =  [ 0, 0, 0, 2, 0, 15, 0, 1 ];
	Agility.forceMovement(player, Animation.create(746), forceMovementVars, 1, false);
	Agility.forceMovement(player, Animation.create(748), forceMovementVars2, 5, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function barbarianObstaclePipe(player, obstacle, object) {
	if(!player.getLocation().equals(Location.create(2552, 3561, 0)) && !player.getLocation().equals(Location.create(2552, 3558, 0))) {
		player.removeAttribute("busy");
		return;
	}
	var y = -3;
	var dir = 2;
	if(player.getLocation().getY() <= 3558) {
		y = 3;
		dir = 0;
	}
	var forceMovementVars =  [ 0, 0, 0, y, 0, 60, dir, 2 ];
	Agility.forceMovement(player, Animation.create(749), forceMovementVars, 1, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function barbarianRopeSwing(player, obstacle, object) {
	if(!player.getLocation().equals(Location.create(2551, 3554, 0))) {
		player.removeAttribute("busy");
		return;
	}
	
	var random = new Random();
	var randomVar = random.nextInt(player.getSkills().getLevel(Skills.AGILITY));
	var success = true;
	
	if(randomVar < 20) {
		success = false;
	}
	
	Agility.animateObject(object, Animation.create(54), 0);
	Agility.animateObject(object, Animation.create(55), 2);
	if(success) {
		var barbAgilityCourseLvl = player.getAttribute("barbarianAgilityCourse");
		if(barbAgilityCourseLvl == null) {
			player.setAttribute("barbarianAgilityCourse", 1);
		}
		var forceMovementVars =  [ 0, 0, 0, -5, 30, 50, 2, 2 ];
		Agility.forceMovement(player, Animation.create(751), forceMovementVars, 1, true);
		player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
	} else {
		var forceMovementVars =  [ 0, 0, 0, -3, 30, 50, 2, 2 ];
		Agility.forceMovement(player, Animation.create(751), forceMovementVars, 1, true);
		Agility.forceTeleport(player, Animation.create(766), Location.create(2551, 9951, 0), 3, 6);
		Agility.forceWalkingQueue(player, null, 2549, 9951, 7, 2, true);
		Agility.setRunningToggled(player, false, 9);
		Agility.damage(player, 5, 7);
	}
}

function barbarianLogBalance(player, obstacle, object) {
	if(player.getLocation().getY() != 3546) {
		player.removeAttribute("busy");
		return;
	}
	
	var random = new Random();
	var randomVar = random.nextInt(player.getSkills().getLevel(Skills.AGILITY));
	var success = true;
	
	if(randomVar < 20) {
		success = false;
	}
	
	if(success) {
		var barbAgilityCourseLvl = player.getAttribute("barbarianAgilityCourse");
		if(barbAgilityCourseLvl == 1) {
			player.setAttribute("barbarianAgilityCourse", 2);
		}
		Agility.setRunningToggled(player, false, 12);
		Agility.forceWalkingQueue(player, Animation.create(762), 2541, 3546, 0, 11, true);
		player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
	} else {
		player.face(Location.create(0, player.getLocation().getY(),0));
			
		Agility.forceTeleport(player, Animation.create(773), Location.create(2545, 3547, 0), 10, 12);
		Agility.setRunningToggled(player, false, 16);
		Agility.forceWalkingQueue(player, Animation.create(772), 2545, 3549, 12, 4, false);
		Agility.forceWalkingQueue(player, Animation.create(772), 2546, 3550, 13, 3, false);
		
		Agility.forceWalkingQueue(player, Animation.create(762), 2545, 3546, 0, 7, false);		
		
		var forceMovementVars =  [ 0, 0, 0, 1, 25, 30, 3, 2 ];
		Agility.forceMovement(player, Animation.create(771), forceMovementVars, 8, false);
		
		Agility.forceTeleport(player, null, Location.create(2546, 3550, 0), 16, 16);		
	}
}

function barbarianObstacleNet(player, obstacle, object) {
	if(player.getLocation().getX() != 2539) {
		player.removeAttribute("busy");
		return;
	}
	if(player.getLocation().getY() >= 3547) {
		player.removeAttribute("busy");
		return;
	}
	player.face(Location.create(0, player.getLocation().getY(), 0));
	var barbAgilityCourseLvl = player.getAttribute("barbarianAgilityCourse");
	if(barbAgilityCourseLvl == 2) {
		player.setAttribute("barbarianAgilityCourse", 3);
	}
	Agility.forceTeleport(player, Animation.create(828), Location.create(player.getLocation().getX() - 2, 3546, 1), 0, 2);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function barbarianLedge(player, obstacle, object) {
	if(player.getLocation().getY() != 3547) {
		player.removeAttribute("busy");
		return;
	}
	
	var random = new Random();
	var randomVar = random.nextInt(player.getSkills().getLevel(Skills.AGILITY));
	var success = true;
	
	if(randomVar < 20) {
		success = false;
	}
		
	player.face(Location.create(0, player.getLocation().getY(), 0));
	
	if(success) {
		var barbAgilityCourseLvl = player.getAttribute("barbarianAgilityCourse");
		if(barbAgilityCourseLvl == 2) {
			player.setAttribute("barbarianAgilityCourse", 3);
		}
		player.playAnimation(Animation.create(753));
		Agility.setRunningToggled(player, false, 8);
		Agility.forceWalkingQueue(player, null, 2532, 3546, 4, 2, false);
		Agility.forceWalkingQueue(player, Animation.create(756), 2532, 3547, 0, 4, false);	
		Agility.forceTeleport(player, Animation.create(828), Location.create(2532, 3546, 0), 7, 8);
		player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
	} else {
		Agility.setRunningToggled(player, false, 8);
		Agility.forceTeleport(player, null, Location.create(2534, 3546, 1), 3, 3);
		Agility.forceWalkingQueue(player, null, 2536, 3547, 6, 3, true);
		Agility.forceTeleport(player, Animation.create(766), Location.create(2534, 3546, 0), 3, 5);
		Agility.forceWalkingQueue(player, Animation.create(756), 2534, 3547, 0, 2, false);
		Agility.damage(player, 5, 6);		
	}
}

function barbarianCrumblingWall1(player, obstacle, object) {
	if(!player.getLocation().equals(Location.create(2535, 3553, 0))) {
		player.removeAttribute("busy");
		return;
	}
	var forceMovementVars =  [ 0, 0, 2, 0, 0, 60, 1, 2 ];
	Agility.forceMovement(player, Animation.create(839), forceMovementVars, 1, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
	var barbAgilityCourseLvl = player.getAttribute("barbarianAgilityCourse");
	if(barbAgilityCourseLvl == 3) {
		player.setAttribute("barbarianAgilityCourse", 4);
	}
}

function barbarianCrumblingWall2(player, obstacle, object) {
	if(!player.getLocation().equals(Location.create(2538, 3553, 0))) {
		player.removeAttribute("busy");
		return;
	}
	var forceMovementVars =  [ 0, 0, 2, 0, 0, 60, 1, 2 ];
	Agility.forceMovement(player, Animation.create(839), forceMovementVars, 1, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
	var barbAgilityCourseLvl = player.getAttribute("barbarianAgilityCourse");
	if(barbAgilityCourseLvl == 4) {
		player.setAttribute("barbarianAgilityCourse", 5);
	}
}

function barbarianCrumblingWall3(player, obstacle, object) {
	if(!player.getLocation().equals(Location.create(2541, 3553, 0))) {
		player.removeAttribute("busy");
		return;
	}
	var forceMovementVars =  [ 0, 0, 2, 0, 0, 60, 1, 2 ];
	Agility.forceMovement(player, Animation.create(839), forceMovementVars, 1, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
	var barbAgilityCourseLvl = player.getAttribute("barbarianAgilityCourse");
	if(barbAgilityCourseLvl == 5) {
		player.getActionSender().sendMessage("You completed the course!");
		player.getSkills().addExperience(Skills.AGILITY, 46.2);
		player.removeAttribute("barbarianAgilityCourse");
	}
}