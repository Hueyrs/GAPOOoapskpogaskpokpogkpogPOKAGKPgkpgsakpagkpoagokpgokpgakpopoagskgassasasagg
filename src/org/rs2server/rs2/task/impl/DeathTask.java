package org.rs2server.rs2.task.impl;

import org.rs2server.rs2.GameEngine;
import org.rs2server.rs2.task.Task;

/**
 * A task which stops the game engine.
 * @author Graham Edgecombe
 *
 */
public class DeathTask implements Task {

	@Override
	public void execute(GameEngine context) {
		if(context.isRunning()) {
			context.stop();
		}
	}

}
