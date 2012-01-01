package org.rs2server.rs2.content;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.tickable.Tickable;

/**
 * For book preaching like zamorak book.
 * @author Canownueasy
 *
 */
public class BookPreaching {
	//1337 lol
	public static void preach(final Player player, final String[] words) {
		World.getWorld().submit(new Tickable(4) {
			public void execute() {
				if(player.preachWord >= words.length) {
					player.preachWord = 0;
					player.playAnimation(Animation.create(-1));
					stop();
					return;
				}
				player.playAnimation(Animation.create(1336));
				player.forceChat(words[player.preachWord]);
				player.preachWord++;
			}
		});
	}

}
