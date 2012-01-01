package org.rs2server.rs2.model.skills;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.Hit.HitType;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

/**
 * For thieving...
 * @author Canownueasy
 *
 */
public class Thieving {
	
	/**
	 * The pickpocket animation.
	 */
	public final Animation PICKPOCKET = Animation.create(881);
	
	/**
	 * The stun bird graphic.
	 */
	public final Graphic STUN_BIRDS = Graphic.create(80);

	/**
	 * The player whom will be thieving.
	 */
	private final Player player;
	
	/**
	 * Constructs a new thieving skill.
	 * @param player For this player.
	 */
	public Thieving(Player player) {
		this.player = player;
	}
	
	public void pickpocket(final NPC npc) {
		if(Misc.random(3) == 3) {
			npc.face(player.getLocation());
			npc.forceChat("What do you think you're doing?!?");
			npc.playAnimation(npc.getAttackAnimation());
			player.playGraphics(STUN_BIRDS);
			World.getWorld().submit(new Tickable(1) {
				public void execute() {
					player.inflictDamage(new Hit(HitType.NORMAL_HIT, Misc.random(1) + 1), null);
					stop();
				}
			});
			return;
		}
		player.face(npc.getLocation());
		player.playAnimation(PICKPOCKET);
		World.getWorld().submit(new Tickable(1) {
			public void execute() {
				player.getInventory().add(pickpocketMoney(npc));
				player.getActionSender().sendMessage("You stole coins from the " + npc.getDefinition().getName() + ".");
				stop();
			}
		});
	}
	
	/**
	 * The money a person will receive if they thieve a certain NPC.
	 * @param NPC The NPC they are pickpocketing.
	 * @param npc
	 * @return
	 */
	public Item pickpocketMoney(NPC npc) {
		switch(npc.getDefinition().getId()) {
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			return new Item(995, Misc.random(2) + 1);
		case 7:
			return new Item(995, Misc.random(3) + 1);
		case 5919:
		case 5920:
			return new Item(995, Misc.random(5) + 1);
		}
		return new Item(995);
	}
}
