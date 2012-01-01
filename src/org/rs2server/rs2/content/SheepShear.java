package org.rs2server.rs2.content;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.NPCDefinition;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.LocationChecker;
import org.rs2server.rs2.util.Misc;

/**
 * For shearing sheeps.
 * @author Canownueasy
 *
 */
public class SheepShear {
	
	public Item SHEAR = new Item(1735);
	
	public Item WOOL = new Item(1737);

	private final Player player;
	
	public SheepShear(Player player) {
		this.player = player;
	}
	
	public void shearSheep(final NPC sheep) {
		if(!player.getInventory().hasItem(SHEAR)) {
			player.getActionSender().sendMessage("You don't have any shears!");
			return;
		}
		player.playAnimation(Animation.create(893));
			//Wait until the sheep realizes you've tried to shear it.
			World.getWorld().submit(new Tickable(2) {
				public void execute() {
					if(Misc.random(3) == 3) {
						sheep.forceChat("Baa!");
						sheep.getWalkingQueue().addStep(LocationChecker.npcLocation(sheep, player, 1).getX(), LocationChecker.npcLocation(sheep, player, 1).getY());
						stop();
						return;
					}
					player.getInventory().add(WOOL);
					NPCDefinition oldDef = sheep.getDefinition();
					NPCDefinition newDef = new NPCDefinition(42, sheep.getDefinition().getName(), "Freshly sheared.", 1, 2, null);
					final NPC woolSheep = new NPC(oldDef, sheep.getLocation(), sheep.getMinLocation(), sheep.getMaxLocation(), sheep.getDirection());
					final NPC shearedSheep = new NPC(newDef, sheep.getLocation(), sheep.getMinLocation(), sheep.getMaxLocation(), sheep.getDirection());
					sheep.transformTo(shearedSheep);
					player.getActionSender().sendMessage("You shear the sheep.");
					//we need to wait 12 seconds for the sheep to regain wool
					World.getWorld().submit(new Event(12000) {
						public void execute() {
							shearedSheep.transformTo(woolSheep);
							stop();
						}
					});
					stop();
				}
			});
	}
}
