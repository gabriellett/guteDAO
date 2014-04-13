package br.com.lett.guteDAO;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public class ConnectionManager {
	
	static Logger logger = LogManager.getLogger(ConnectionManager.class.getName());
	
	private static HashMap<String,BoneCP> connectionPools = null;

	public static void configureConnPool() throws DAOException {
		
		connectionPools = new HashMap<String,BoneCP>();
		
		logger.debug("[guteDAO] Configuring connection pool");
		
		Properties dbProps = null;
		
		try{
			dbProps = new Properties();
			InputStream in = ConnectionManager.class.getResourceAsStream("/guteDAO.properties");
			dbProps.load(in);
			in.close();
		}catch(IOException ioe){
			logger.error("[guteDAO] Error openning properties file:"+ioe.getMessage());
		}
		
		Integer numberOfDataSources = Integer.valueOf(dbProps.getProperty("dataSourcesQuant"));
		logger.debug("[guteDAO] Number of DS:"+numberOfDataSources);
		
		for(int i=1;i<=numberOfDataSources;i++){
			
			String databaseName = dbProps.getProperty("databaseName"+i);
			if(numberOfDataSources==1) databaseName = "";
			String username = dbProps.getProperty("databaseUsername"+i);
			String password = dbProps.getProperty("databasePassword"+i);
			String dbUrl =  dbProps.getProperty("databaseUrl"+i);
			String driverName = dbProps.getProperty("driverName"+i);
			
			logger.debug("[guteDAO] Properties loaded for database "+i+":"+databaseName+";");
			try {
				Class.forName(driverName).newInstance();
				BoneCPConfig config = new BoneCPConfig();
				config.setJdbcUrl(dbUrl);
				config.setUsername(username);
				config.setPassword(password);
				
				ConnectionManager.putConnectionPool(databaseName, new BoneCP(config)); 	
			}catch(Exception e){
				logger.error("[guteDAO] Error configurating boneCP for connection "+i+":"+e.getMessage());
				throw new DAOException(e);
			}
		}
		
		logger.debug("[guteDAO] Done setting up connection pool");

	}

	public static void shutdownConnPool() throws DAOException {

		logger.debug("[guteDAO] Shutting down connection pool");
		try {
			
			for(String key : connectionPools.keySet()){

				logger.debug("[guteDAO] Shutting down pool for databaseConn '"+key+"'");
				BoneCP connectionPool = ConnectionManager.getConnectionPool(key);
				if (connectionPool != null) {
					connectionPool.shutdown();
				}
			}

		} catch (Exception e) {
			logger.error("[guteDAO] Error while trying to shutdown the connection pool:"+e.getMessage());
			throw new DAOException(e);
		}
		logger.debug("[guteDAO] Done shutting down connection pool");
	}

	public static Connection getConnection() throws DAOException{

		return getConnection("");

	}

	public static Connection getConnection(String databaseName) throws DAOException{

		Connection conn = null;
		try {
			conn = getConnectionPool(databaseName).getConnection();
		} catch (SQLException e) {
			logger.error("[guteDAO] Error when obtaining a connection from the pool "+e.getMessage());
			throw new DAOException(e);
		}
		
		return conn;

	}
	
	private static BoneCP getConnectionPool(String name){
		return connectionPools.get(name);
	}

	private static void putConnectionPool(String name,BoneCP connectionPool) {
		ConnectionManager.connectionPools.put(name, connectionPool);
	}

}
