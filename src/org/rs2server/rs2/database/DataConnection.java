package org.rs2server.rs2.database;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

public class DataConnection implements Connection {
	
	private ConnectionPool pool;
	private Connection connection;
	private boolean used;
	private long timestamp;
	
	public DataConnection(Connection connection, ConnectionPool pool) {
		this.connection = connection;
		this.pool = pool;
		this.used = false;
		this.timestamp = 0L;
	}
	
	public synchronized boolean lease() {
		if(used) {
			return false;
		} else {
			used = true;
			timestamp = System.currentTimeMillis();
			return true;
		}
	}	
	
	public boolean validate() {
		try {
			connection.getMetaData().getDriverName();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public boolean isUsed() {
		return used;
	}
	
	public long getLastUse() {
		return timestamp;
	}
	
	@Override
	public void close() throws SQLException {
		pool.returnConnection(this);
	}
	
	protected void expireLease() {
		used = false;
	}
	
	protected Connection getConnection() {
		return connection;
	}

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        return connection.prepareCall(sql);
    }

    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    public String nativeSQL(String sql) throws SQLException {
        return connection.nativeSQL(sql);
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
    	connection.setAutoCommit(autoCommit);
    }

    public boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }

    public void commit() throws SQLException {
    	connection.commit();
    }

    public void rollback() throws SQLException {
    	connection.rollback();
    }

    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return connection.getMetaData();
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
    	connection.setReadOnly(readOnly);
    }
  
    public boolean isReadOnly() throws SQLException {
        return connection.isReadOnly();
    }

    public void setCatalog(String catalog) throws SQLException {
    	connection.setCatalog(catalog);
    }

    public String getCatalog() throws SQLException {
        return connection.getCatalog();
    }

    public void setTransactionIsolation(int level) throws SQLException {
    	connection.setTransactionIsolation(level);
    }

    public int getTransactionIsolation() throws SQLException {
        return connection.getTransactionIsolation();
    }

    public SQLWarning getWarnings() throws SQLException {
        return connection.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        connection.clearWarnings();
    }

	@Override
	public Array createArrayOf(String arg0, Object[] arg1) throws SQLException {
		return connection.createArrayOf(arg0, arg1);
	}

	@Override
	public Blob createBlob() throws SQLException {
		return connection.createBlob();
	}

	@Override
	public Clob createClob() throws SQLException {
		return connection.createClob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return connection.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return connection.createSQLXML();
	}

	@Override
	public Statement createStatement(int arg0, int arg1) throws SQLException {
		return connection.createStatement(arg0, arg1);
	}

	@Override
	public Statement createStatement(int arg0, int arg1, int arg2)
			throws SQLException {
		return connection.createStatement(arg0, arg1, arg2);
	}

	@Override
	public Struct createStruct(String arg0, Object[] arg1) throws SQLException {
		return connection.createStruct(arg0, arg1);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return connection.getClientInfo();
	}

	@Override
	public String getClientInfo(String arg0) throws SQLException {
		return connection.getClientInfo(arg0);
	}

	@Override
	public int getHoldability() throws SQLException {
		return connection.getHoldability();
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return connection.getTypeMap();
	}

	@Override
	public boolean isValid(int arg0) throws SQLException {
		return connection.isValid(arg0);
	}

	@Override
	public CallableStatement prepareCall(String arg0, int arg1, int arg2)
			throws SQLException {
		return connection.prepareCall(arg0, arg1, arg2);	
	}

	@Override
	public CallableStatement prepareCall(String arg0, int arg1, int arg2,
			int arg3) throws SQLException {
		return connection.prepareCall(arg0, arg1, arg2, arg3);	
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1)
			throws SQLException {
		return connection.prepareStatement(arg0, arg1);	
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, int[] arg1)
			throws SQLException {
		return connection.prepareStatement(arg0, arg1);	
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, String[] arg1)
			throws SQLException {
		return connection.prepareStatement(arg0, arg1);	
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2)
			throws SQLException {
		return connection.prepareStatement(arg0, arg1, arg2);	
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2,
			int arg3) throws SQLException {
		return connection.prepareStatement(arg0, arg1, arg2, arg3);
	}

	@Override
	public void releaseSavepoint(Savepoint arg0) throws SQLException {
		connection.releaseSavepoint(arg0);		
	}

	@Override
	public void rollback(Savepoint arg0) throws SQLException {
		connection.rollback(arg0);		
	}

	@Override
	public void setClientInfo(Properties arg0) throws SQLClientInfoException {
		connection.setClientInfo(arg0);	
	}

	@Override
	public void setClientInfo(String arg0, String arg1)
			throws SQLClientInfoException {
		connection.setClientInfo(arg0, arg1);
	}

	@Override
	public void setHoldability(int arg0) throws SQLException {
		connection.setHoldability(arg0);		
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return connection.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String arg0) throws SQLException {
		return connection.setSavepoint(arg0);
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException {
		connection.setTypeMap(arg0);	
	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return connection.isWrapperFor(arg0);
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return connection.unwrap(arg0);
	}


}
