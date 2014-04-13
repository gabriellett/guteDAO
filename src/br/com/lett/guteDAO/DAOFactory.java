package br.com.lett.guteDAO;

import java.sql.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DAOFactory {

	static Logger logger = LogManager.getLogger(DAOFactory.class.getName());
	
	public static DataAccessObject createDAO(String connName) throws DAOException{
		
		logger.debug("[guteDAO] Creating a new DAO");
		
		Connection connection;
		connection = ConnectionManager.getConnection(connName);
		
		DataAccessObject dao = new DataAccessObject(connection);
		
		logger.debug("[guteDAO] DAO created");
		
		return dao;
	}
	
	public static DataAccessObject createDAO() throws DAOException{
		return DAOFactory.createDAO("");
	}
}
