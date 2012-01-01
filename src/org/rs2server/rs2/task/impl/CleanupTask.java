package org.rs2server.rs2.task.impl;

import org.rs2server.rs2.GameEngine;
import org.rs2server.rs2.task.Task;

/**
 * Performs garbage collection and finalization.
 * @author Graham Edgecombe
 *
 */
public class CleanupTask implements Task {

	@Override
	public void execute(GameEngine context) {
		context.submitWork(new Runnable() {
			public void run() {
				System.gc();
				System.runFinalization();
			}
		});
	}

}