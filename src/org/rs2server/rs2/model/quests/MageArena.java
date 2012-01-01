package org.rs2server.rs2.model.quests;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.NPCDefinition;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.CombatNPCDefinition.Skill;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.tickable.Tickable;

/**
 * 
 * @author Jimmy
 *
 */
public class MageArena {

	private Player player;
	public boolean complete = false;
	private int battleIndex = 1;
	private NPC kolodion = null;
	private String message = "";

	private static final int ARENA_X = 3106, ARENA_Y = 3934; //Center
	private static final int MIN_X = 3039, MIN_Y = 3992, MAX_X = 3117, MAX_Y = 3946;

	public MageArena(Player player) {
		this.player = player;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public void jumpIntoSparklingPool(final int objectId) { // 2878 2879
		if(!complete)
			return;
		player.setTeleportTarget(objectId == 2878 ? Location.create(2509, 4689, 0) : Location.create(2548, 4718));
		/*int[] movement = new int[] {0, 0, 0, 2, 33, 60, 0, 2};;
		/*if(objectId == 2879) {
			//movement = 
		}
		player.getActionSender().sendMessage(":hurr:");
		player.setAttribute("busy", true);
		Agility.forceMovement(player, Animation.create(1604), movement, 1, true);
		World.getWorld().submit(new Tickable(4) {

			@Override
			public void execute() {
				player.playGraphics(Graphic.create(68));
				player.playAnimation(Animation.create(804));
				this.stop();
			}
			
		});
		World.getWorld().submit(new Tickable(6) {

			@Override
			public void execute() {
				player.setAttribute("busy", false);
				player.setTeleportTarget(objectId == 2878 ? Location.create(2509, 4689, 0) : Location.create(2548, 4718));
				player.playAnimation(Animation.RESET);
				this.stop();
			}
		});*/
	}

	public void begin() {
		try {
			if(isComplete())
				return;
			World.getWorld().submit(new Tickable(1) {

				@Override
				public void execute() {
					NPC kolodion = null;
					for(NPC npc : player.getRegion().getNpcs()) {
						if(npc.getDefinition().getId() == 905) {
							kolodion = npc;
							break;
						}
					}
					if(kolodion == null) {
						return;
					}
					kolodion.playAnimation(Animation.create(811)); //fixed the animation for u :p
					this.stop();
				}
			});
			World.getWorld().submit(new Tickable(2) {

				@Override
				public void execute() {
					player.playAnimation(Animation.create(714));
					player.playGraphics(Graphic.create(308, 50, 100));
					this.stop();
				}
			});
			World.getWorld().submit(new Tickable(4) {

				@Override
				public void execute() {
					player.playAnimation(Animation.RESET);
					player.setTeleportTarget(Location.create(ARENA_X, ARENA_Y));
					destroy();
					this.stop();
				}

			});
			World.getWorld().submit(new Tickable(5) {

				@Override
				public void execute() {
					Location spawn = Location.create(ARENA_X, ARENA_Y+1, 0);
					Location min = Location.create(MIN_X, MIN_Y, 0);
					Location max = Location.create(MAX_X, MAX_Y, 0);
					kolodion = new NPC(NPCDefinition.forId(907), spawn, min, max, 0);
					kolodion.setSummoner(player);
					kolodion.playGraphics(Graphic.create(86, 0, 100));
					World.getWorld().register(kolodion);
					kolodion.forceChat("You must prove yourself... now!");
					player.getActionSender().sendHintArrow(kolodion, 1, 0);
					kolodion.getCombatState().startAttacking(player, player.getSettings().isAutoRetaliating());
					this.stop();
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void transform() {
		try {
			switch(battleIndex) {
			case 1:
				kolodion.transform(908);
				kolodion.playAnimation(Animation.create(134));//real animation
				kolodion.playGraphics(Graphic.create(189, 0, 0));
				battleIndex = 2;
				message = "This is only the beginning, you can't beat me!";
				break;
			case 2:
				kolodion.transform(909);
				kolodion.playAnimation(Animation.create(5324)); // Idk the id ?
				kolodion.playGraphics(Graphic.create(190, 0, 0)); //idk graphic id either?
				battleIndex = 3;
				message = "Foolish mortal, I am unstoppable!";
				break;
			case 3:
				kolodion.transform(910);
				kolodion.playAnimation(Animation.create(715));//real animation
				kolodion.playGraphics(Graphic.create(189, 0, 0));
				battleIndex = 4;
				message = "Now you feel it... The dark energy.";
				break;
			case 4:
				kolodion.transform(911);
				kolodion.playAnimation(Animation.create(-1));//real animation
				kolodion.playGraphics(Graphic.create(190, 0, 0));
				battleIndex = 5;
				message = "Aaaaaaaaaarrgghhhh! The power!";
				break;
			case 5:
				battleIndex = 6;
				World.getWorld().submit(new Tickable(3) {

					@Override
					public void execute() {
						player.setTeleportTarget(Location.create(2540, 4716, 0));
						this.stop();
					}	
				});
				World.getWorld().submit(new Tickable(4) {

					@Override
					public void execute() {
						World.getWorld().unregister(kolodion);
						kolodion = null;
						this.stop();
					}
				});
				complete = true;
				return;
			default: return;
			}
			kolodion.setCombatCooldownDelay(1);
			for(Skill skill : kolodion.getCombatDefinition().getSkills().keySet()) {
				kolodion.getSkills().setSkill(skill.getId(), kolodion.getCombatDefinition().getSkills().get(skill),
						kolodion.getSkills().getExperienceForLevel(
								kolodion.getCombatDefinition().getSkills().get(skill)));
			}
			kolodion.getCombatState().setCombatStyle(kolodion.getCombatDefinition().getCombatStyle());
			kolodion.getCombatState().setAttackType(kolodion.getCombatDefinition().getAttackType());
			kolodion.getCombatState().setBonuses(kolodion.getCombatDefinition().getBonuses());
			kolodion.getSkills().normalizeLevel(Skills.HITPOINTS);

			World.getWorld().submit(new Tickable(1) {

				@Override
				public void execute() {
					kolodion.forceChat(message);
					kolodion.getCombatState().setDead(false);
					kolodion.getCombatState().startAttacking(player, player.getSettings().isAutoRetaliating());
					this.stop();
				}	
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		try {
			World.getWorld().submit(new Tickable(1) {

				@Override
				public void execute() {
					if(battleIndex == 6) {
						this.stop();
						return;
					}
					if(!BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "Mage Arena") ||
							player.getCombatState().isDead()) {
						battleIndex = 0;
						World.getWorld().submit(new Tickable(1) {

							@Override
							public void execute() {
								World.getWorld().unregister(kolodion);
								kolodion = null;
								this.stop();	
							}

						});
						this.stop();
						return;
					}
					if(!World.getWorld().isPlayerOnline(player.getName())) {
						battleIndex = 0;
						player.setTeleportTarget(Location.create(2540, 4716, 0));//Mage bank coords
						World.getWorld().submit(new Tickable(1) {

							@Override
							public void execute() {
								World.getWorld().unregister(kolodion);
								kolodion = null;
								this.stop();	
							}

						});
						this.stop();
					}	
				}

			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
