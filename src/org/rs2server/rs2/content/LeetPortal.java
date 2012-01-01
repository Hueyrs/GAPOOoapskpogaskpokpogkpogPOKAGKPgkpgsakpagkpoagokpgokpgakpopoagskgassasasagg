package org.rs2server.rs2.content;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.tickable.Tickable;

public class LeetPortal {
	
	public static void doPortal(final Player player, final GameObject portal, final Location loc, String dir) {
		int x = 0, y = 0;
		if(dir.equalsIgnoreCase("North")) {
			y--;
		} else
		if(dir.equalsIgnoreCase("South")) {
			y++;
		} else
		if(dir.equalsIgnoreCase("East")) {
			x++;
		} else
		if(dir.equalsIgnoreCase("West")) {
			x--;
		}
		player.getWalkingQueue().addStep((portal.getLocation().getX() + x), (portal.getLocation().getY() + y));
		player.getWalkingQueue().finish();
		final int xf = x;
		final int yf = y;
		World.getWorld().submit(new Tickable(1) {
			public void execute() {
				if(player.getLocation().getY() == (portal.getLocation().getY() + yf) && player.getLocation().getX() == (portal.getLocation().getX() + xf)) {
					stop();
					World.getWorld().submit(new Tickable(2) {
						public void execute() {
							player.getWalkingQueue().addStep(portal.getLocation().getX(), portal.getLocation().getY());
							player.getWalkingQueue().finish();
							World.getWorld().submit(new Tickable(2) {
								public void execute() {
									player.playGraphics(Graphic.create(804, 0, 0));
									World.getWorld().submit(new Tickable(2) {
										public void execute() {
											player.setTeleportTarget(loc);
											player.playAnimation(Animation.create(-1));
											player.playGraphics(Graphic.create(-1));
											stop();
										}
									});
									stop();
								}
							});
							stop();
						}
					});
				}
			}
		});
		
	}
	
	public static void doPortal(final Player player, final GameObject portal, final Location loc) {
		World.getWorld().submit(new Tickable(2) {
			public void execute() {
				player.getWalkingQueue().addStep(portal.getLocation().getX(), portal.getLocation().getY());
				player.getWalkingQueue().finish();
				World.getWorld().submit(new Tickable(2) {
					public void execute() {
						player.playGraphics(Graphic.create(804, 0, 0));
						World.getWorld().submit(new Tickable(2) {
							public void execute() {
								player.setTeleportTarget(loc);
								player.playAnimation(Animation.create(-1));
								stop();
							}
						});
						stop();
					}
				});
				stop();
			}
		});
	}

}
