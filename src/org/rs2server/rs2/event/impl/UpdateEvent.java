package org.rs2server.rs2.event.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.task.ConsecutiveTask;
import org.rs2server.rs2.task.ParallelTask;
import org.rs2server.rs2.task.Task;
import org.rs2server.rs2.task.impl.NPCResetTask;
import org.rs2server.rs2.task.impl.NPCTickTask;
import org.rs2server.rs2.task.impl.NPCUpdateTask;
import org.rs2server.rs2.task.impl.PlayerResetTask;
import org.rs2server.rs2.task.impl.PlayerTickTask;
import org.rs2server.rs2.task.impl.PlayerUpdateTask;
import org.rs2server.rs2.tickable.Tickable;


/**
 * An event which starts player update tasks.
 * @author Graham Edgecombe
 *
 */
public class UpdateEvent extends Event {

	/**
	 * The cycle time, in milliseconds.
	 */
	public static final int CYCLE_TIME = 600;
	
	/**
	 * Creates the update event to cycle every 600 milliseconds.
	 */
	public UpdateEvent() {
		super(CYCLE_TIME);
	}

	@Override
	public void execute() {		
		Iterator<Tickable> tickIt$ = World.getWorld().getTickableManager().getTickables().iterator();
		while(tickIt$.hasNext()) {
			Tickable t = tickIt$.next();
			t.cycle();
			if(!t.isRunning()) {
				tickIt$.remove();
			}
		}
		
		List<Task> tickTasks = new ArrayList<Task>();
		List<Task> updateTasks = new ArrayList<Task>();
		List<Task> resetTasks = new ArrayList<Task>();
		
		for(NPC npc : World.getWorld().getNPCs()) {
			tickTasks.add(new NPCTickTask(npc));
			resetTasks.add(new NPCResetTask(npc));
		}

		Iterator<Player> it$ = World.getWorld().getPlayers().iterator();
		while (it$.hasNext()) {
			Player player = it$.next();
			if (player.isDestroyed()) { // could be online but session could not
										// (xlogged), so we simply check if
										// their account is destroyed instead
				it$.remove();
			} else {
				tickTasks.add(new PlayerTickTask(player));
				updateTasks.add(new ConsecutiveTask(new PlayerUpdateTask(player), new NPCUpdateTask(player)));
				resetTasks.add(new PlayerResetTask(player));
			}
		}
		
		// ticks can no longer be parallel due to region code
		Task tickTask = new ConsecutiveTask(tickTasks.toArray(new Task[0]));
		Task updateTask = new ParallelTask(updateTasks.toArray(new Task[0]));
		Task resetTask = new ParallelTask(resetTasks.toArray(new Task[0]));
		
		World.getWorld().submit(new ConsecutiveTask(tickTask, updateTask, resetTask));
	}

}
