package org.rs2server.rs2.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import org.rs2server.rs2.database.impl.ConnectionTicker;
import org.rs2server.rs2.model.World;


public class ConnectionPool {

	private Vector<Connection> connections;
	private final long TIMEOUT = 30000;
	private final int POOLSIZE = 10;
	private ConnectionConfig connectionConfig;

	protected ConnectionPool(ConnectionConfig connectionConfig) {
		this.connectionConfig = connectionConfig;
		connections = new Vector<Connection>(POOLSIZE);
		World.getWorld().submit(new ConnectionTicker(this));
	}

	public synchronized void reapConnections() {
		long stale = System.currentTimeMillis() - TIMEOUT;
		Enumeration<Connection> connlist = connections.elements();		
		while ((connlist != null) && (connlist.hasMoreElements())) {
			DataConnection conn = (DataConnection) connlist.nextElement();
			if ((stale > conn.getLastUse())) {
				removeConnection(conn);
			}
		}
	}

	public synchronized Connection getConnection() throws SQLException {
		DataConnection connection;
		for (int i = 0; i < connections.size(); i++) {
			connection = (DataConnection) connections.elementAt(i);
			if (connection.lease()) {
				return connection;
			}
		}
		Connection conn = DriverManager.getConnection(connectionConfig
				.getHostAddress(), connectionConfig.getUsername(),
				connectionConfig.getPassword());
		connection = new DataConnection(conn, this);
		connection.lease();
		connections.addElement(connection);
		return connection;
	}

	public synchronized void closeConnections() {
		Enumeration<Connection> connlist = connections.elements();
		while ((connlist != null) && (connlist.hasMoreElements())) {
			DataConnection conn = (DataConnection) connlist.nextElement();
			removeConnection(conn);
		}
	}

	public synchronized void returnConnection(DataConnection conn) {
		conn.expireLease();
	}

	private synchronized void removeConnection(DataConnection conn) {
		connections.removeElement(conn);
	}

	public ConnectionConfig getConnectionConfig() {
		return connectionConfig;
	}

}
