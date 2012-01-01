package org.rs2server.rs2.database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionDriver implements Driver {
	
	private static final String URL_PREFIX = "jdbc:jdc:";
    private static final int MAJOR_VERSION = 1;
    private static final int MINOR_VERSION = 0;
    private final ConnectionPool pool;
    
    public ConnectionDriver(String driver, ConnectionConfig config) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    	DriverManager.registerDriver(this);
    	Class.forName(driver).newInstance();
    	pool = new ConnectionPool(config);
    }

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		return url.startsWith(URL_PREFIX);
	}

	@Override
	public Connection connect(String url, Properties props) throws SQLException {
		if(!url.startsWith(URL_PREFIX)) {
            return null;
       }
       return pool.getConnection();
	}

	@Override
	public int getMajorVersion() {
		return MAJOR_VERSION;
	}

	@Override
	public int getMinorVersion() {
		return MINOR_VERSION;
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String str, Properties props)
			throws SQLException {
		return new DriverPropertyInfo[0];
	}

	@Override
	public boolean jdbcCompliant() {
		return false;
	}

}
