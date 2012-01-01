package org.rs2server.rs2.database;

import java.sql.Connection;

public class ConnectionConfig {
	
	private Connection connection;
	
	private final String hostAddress;
	private final String username;
	private final String password;
	
	public ConnectionConfig(String hostAddress, String username, String password) {
		this.hostAddress = hostAddress;
		this.username = username;
		this.password = password;
	}
	
	public ConnectionConfig setConnection(Connection connection) {
		this.connection = connection;
		return this;
	}

	public Connection getConnection() {
		return connection;
	}

	public String getHostAddress() {
		return hostAddress;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	

}
