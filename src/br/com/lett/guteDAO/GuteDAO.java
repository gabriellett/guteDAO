package br.com.lett.guteDAO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuteDAO {

	static Logger logger = LogManager.getLogger(GuteDAO.class.getName());
	
	public void startup() throws DAOException {
		logger.info("[guteDAO] Starting guteDAO");
		ConnectionManager.configureConnPool();
	}
	
	public void destroy() throws DAOException{
		logger.info("[guteDAO] Shutting down guteDAO");
		ConnectionManager.shutdownConnPool();
	}
}
