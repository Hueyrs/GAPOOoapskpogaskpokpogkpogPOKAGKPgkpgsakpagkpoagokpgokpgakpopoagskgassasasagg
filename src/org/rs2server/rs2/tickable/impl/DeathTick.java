package org.rs2server.rs2.tickable.impl;

import java.util.Random;

import org.rs2server.rs2.content.Following;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GroundItem;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.NPCDefinition;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.minigame.impl.Barrows;
import org.rs2server.rs2.model.minigame.impl.WarriorsGuild;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;


/**
 * The death tickable handles player and npc deaths. Drops loot, does animation,
 * teleportation, etc.
 * 
 * @author Graham
 * @author Scu11
 * @author Canownueasy
 */
public class DeathTick extends Tickable {

	/**
	 * The mob who has just died.
	 */
	private Mob mob;

	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	/**
	 * Creates the death event for the specified entity.
	 * 
	 * @param entity
	 *            The mob whose death has just happened.
	 */
	public DeathTick(Mob mob, int ticks) {
		super(ticks);
		this.mob = mob;
	}

	@SuppressWarnings("static-access")
	@Override
	public void execute() {
		this.stop();
		if (mob.getCombatState().isDead()) { //they might of been saved in this period
			/*
			 * If set to true, the minigame handles items such as teleporting.
			 */
			boolean minigameDeathHook = false;
			
			/*
			 * The killer of this mob.
			 */
			Mob killer = (mob.getCombatState().getDamageMap().highestDamage() != null && !mob.isDestroyed()) ? mob.getCombatState().getDamageMap().highestDamage() : mob;
			if(mob.isNPC()) {
				NPC npc = (NPC) mob;
				if(npc.getDefinition().getName().equalsIgnoreCase("kolodion")) {
					if(npc.getSummoner() != null) {
						npc.getSummoner().getMageArena().transform();
						this.stop();
						return;
					}
				}
			}
			if(mob instanceof Player) {
				@SuppressWarnings("unused")
				Player player = (Player) mob;
				/*if(CastleWars.access().players.contains(player)) {
					CastleWars.access().playerDied(player);
					return;
				}*/
			}
			/*
			 * Drops the loot and performs minigame kill hooks.
			 */
			if(killer.isPlayer()) {
				Player player = (Player) killer;
				int barrowsBrothers[] = { 2025, 2026, 2027, 2028, 2029, 2030 };
				int cyclopes[] = { 4292, 6078, 6079, 6080, 6081 };
				if(mob instanceof NPC) {
					NPC npc = (NPC) mob;
					int npcId = npc.getDefinition().getId();
					if(World.getWorld().getBarrelchest().npcs.contains(npc)) {
						World.getWorld().getBarrelchest().npcs.remove(npc);
						if(World.getWorld().getBarrelchest().npcs.size() <= 1) {
							for(final Player pz : World.getWorld().getBarrelchest().players) {
								if(pz != null && pz.getSkills().getLevel(Skills.HITPOINTS) > 0) {
									for(NPC nz : World.getWorld().getBarrelchest().npcs) {
										if(nz != null && nz.getSkills().getLevel(Skills.HITPOINTS) > 0) {
											pz.getActionSender().sendHintArrow(nz, 0, 1);
											break;
										}
									}
								}
							}
						}
					}
					if(npcId == 2780) {
						player.magesRevenge = 7;
						player.getActionSender().sendMessage("The soul of Solus Dellagar was diminished.");
						player.setTeleportTarget(player.DEFAULT_LOCATION);
					}
					if(npcId == 253 || npcId == 477 || npcId == 258) {
						player.khazardWave++;
						Location loc = Location.create(2440, 4956);
						Location l = Location.create(0, 0);
						switch(player.khazardWave) {
						case 0:
							NPC khazard1 = new NPC(NPCDefinition.quickDef(253), loc, l, l, 0);
							World.getWorld().register(khazard1);
							Following.create(player, khazard1).follow();
							break;
						case 1:
							NPC khazard2 = new NPC(NPCDefinition.quickDef(253), loc, l, l, 0);
							World.getWorld().register(khazard2);
							Following.create(player, khazard2).follow();
							break;
						case 2:
							NPC khazard3 = new NPC(NPCDefinition.quickDef(253), loc, l, l, 0);
							World.getWorld().register(khazard3);
							Following.create(player, khazard3).follow();
							break;
						case 3:
							NPC warlord = new NPC(NPCDefinition.quickDef(477), loc, loc, l, 0);
							World.getWorld().register(warlord);
							Following.create(player, warlord).follow();
							break;
						case 4:
							NPC generalKhazard = new NPC(NPCDefinition.quickDef(258), loc, l, l, 0);
							World.getWorld().register(generalKhazard);
							Following.create(player, generalKhazard).follow();
							break;
						case 5:
							player.magesRevenge++;
							player.getActionSender().sendMessage("All the Khazard forces were defeated!");
							break;
						}
						if(npcId == 258) {
							GroundItem key = new GroundItem(player.getName(), new Item(6792), npc.getLocation());
							World.getWorld().register(key, player);
						}
					}
					for(int id : cyclopes) {
						if(npc.getDefinition().getId() == id) {
							if(Misc.random(50) >= 50) {
								GroundItem defender = new GroundItem(player.getName(), WarriorsGuild.create(player).getDefender(), npc.getLocation());
								World.getWorld().register(defender, player);	
							}
						}
					}
					for(int id : barrowsBrothers) {
						if(npc.getDefinition().getId() == id) {
							Barrows barrows = new Barrows(player);
							switch(npc.getDefinition().getId()) {
							case 2025:
								player.AHRIMS = true;
								break;
							case 2026:
								player.DHAROKS = true;
								break;
							case 2027:
								player.GUTHANS = true;
								break;
							case 2028:
								player.KARILS = true;
								break;
							case 2029:
								player.TORAGS = true;
								break;
							case 2030:
								player.VERACS = true;
								break;
							}
							if(barrows.addKill()) {
								barrows.dropItems(npc.getDefinition().getId());
							}
						}
					}
				}
			} else {
				if(!(mob instanceof Player)) {
					mob.dropLoot(mob);	
				}			
			}
			if(mob instanceof Player) {
				Player player = (Player) mob;
				if(player.getRights() != player.getRights().ADMINISTRATOR || !(World.getWorld().getBarrelchest().players.contains(player))) {
					mob.dropLoot(player);
				}
				if(player.getMinigame() != null) {
					player.getMinigame().killHook(player, mob);
				}
			}
			/*
			 * The location to teleport to.
			 */
			Location teleportTo = Mob.DEFAULT_LOCATION;
			
			/*
			 * Resets the opponents tag timer. Player only as NPC's reset their killer as soon as they die.
			 */
			if (!mob.isNPC() && mob.getCombatState().getLastHitBy() != null && mob.getCombatState().getLastHitBy().getCombatState().getLastHitTimer() > (System.currentTimeMillis() + 4000) && mob.getCombatState().getLastHitBy().getCombatState().getLastHitBy() == mob) {
				mob.getCombatState().getLastHitBy().getCombatState().setLastHitBy(null);
				mob.getCombatState().getLastHitBy().getCombatState().setLastHitTimer(0);
			}
			/*
			 * Resets our tag timer.
			 */
			mob.getCombatState().setLastHitBy(null);
			mob.getCombatState().setLastHitTimer(0);			

			/*
			 * Resets various attributes.
			 */
			mob.getCombatState().setDead(false);
			mob.resetVariousInformation();
			if(mob instanceof Player) {
				Player player = (Player) mob;
				if(World.getWorld().getBarrelchest().players.contains(player)) {
					World.getWorld().getBarrelchest().death(player);
				}
				player.getCombatState().calculateBonuses();
				player.getActionSender().sendBonuses();
			}
			
			/*
			 * Performs checks for players/npcs.
			 */
			if (mob.isPlayer()) {
				final Player player = (Player) mob;
				player.getActionSender().updateSpecialConfig();
				player.getActionSender().sendMessage("Oh dear, you are dead!");
				player.getActionSender().sendBonuses();
				player.getActionSender().updateRunningConfig();
				if(player.getMinigame() != null) {
					minigameDeathHook = player.getMinigame().deathHook(player);
				}
			} else if(mob.isNPC()) {
				final NPC npc = (NPC) mob;
				if(npc.getCombatDefinition().getRespawnTicks() > 0) {
					teleportTo = Location.create(1, 1, 0);
					World.getWorld().submit(new Tickable(npc.getCombatDefinition().getRespawnTicks()) {
						public void execute() {
							npc.setTeleportTarget(npc.getSpawnLocation());
							npc.setLocation(npc.getSpawnLocation());
							npc.setDirection(npc.getSpawnDirection());
							this.stop();
						}
					});
				} else {
					teleportTo = null;
					World.getWorld().unregister(npc);
				}
			}
			
			/*
			 * Teleports the mob if the minigame hasn't handled it.
			 */
			if(!minigameDeathHook && teleportTo != null) {
				mob.setTeleportTarget(teleportTo);
				mob.getCombatState().resetBonuses();
				mob.setDefaultAnimations();
			}
			
			/*
			 * If in a PvP situation, send the defeat message.
			 */
			if(killer.isPlayer() && mob.isPlayer() && killer != mob) {
				switch (random.nextInt(10)) {
				default:
				case 0:
					killer.getActionSender().sendMessage("You have defeated " + mob.getUndefinedName() + ".");
					break;
				case 1:
					killer.getActionSender().sendMessage("Can anyone defeat you? Certainly not " + mob.getUndefinedName() + ".");
					break;
				case 2:
					killer.getActionSender().sendMessage(mob.getUndefinedName() + " falls before your might.");
					break;
				case 3:
					killer.getActionSender().sendMessage("A humiliating defeat for " + mob.getUndefinedName() + ".");
					break;
				case 4:
					killer.getActionSender().sendMessage("You were clearly a better fighter than " + mob.getUndefinedName() + ".");
					break;
				case 5:
					killer.getActionSender().sendMessage(mob.getUndefinedName() + " has won a free ticket to Lumbridge.");
					break;
				case 6:
					killer.getActionSender().sendMessage("It's all over for " + mob.getUndefinedName() + ".");
					break;
				case 7:
					killer.getActionSender().sendMessage("With a crushing blow you finish " + mob.getUndefinedName() + ".");
					break;
				case 8:
					killer.getActionSender().sendMessage(mob.getUndefinedName() + " regrets the day they met you in combat.");
					break;
				case 9:
					killer.getActionSender().sendMessage(mob.getUndefinedName() + " didn't stand a chance against you.");
					break;
				}
			}
			
			/*
			 * Resets the mobs animation and frozen flag.
			 */
			World.getWorld().submit(new Tickable(1) {
				public void execute() {
					mob.playAnimation(Animation.create(-1));
					mob.getCombatState().setCanMove(true);
					this.stop();
				}
			});
		}
	}

}