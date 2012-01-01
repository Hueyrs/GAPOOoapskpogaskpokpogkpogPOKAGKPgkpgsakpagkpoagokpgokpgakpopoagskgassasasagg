package org.rs2server.rs2.database.impl;

import org.rs2server.rs2.database.ConnectionPool;
import org.rs2server.rs2.tickable.Tickable;

public class ConnectionTicker extends Tickable {
	
	private ConnectionPool pool;
	private final static int DELAY = 15;
	
	public ConnectionTicker(ConnectionPool pool) {
		super(DELAY);
		this.pool = pool;
	}
	
	@Override
	public void execute() {
		pool.reapConnections();
	}	
	

}
