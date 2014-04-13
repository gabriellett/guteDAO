package br.com.lett.guteDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DataAccessObject {
	
	Logger logger = LogManager.getLogger(DataAccessObject.class.getName());
	
    private Connection conn; 
    private boolean transactionMode = false;
    private boolean isCommited = false;
    private DAOResult daoResult;
    
    Map<String,Object> parameters;
    
    public DataAccessObject(Connection conn){
    	this.setConn(conn);
    	this.parameters = new LinkedHashMap<String,Object>();
    }
    
    private void setConn(Connection conn){
    	this.conn = conn;
    }
    
    private Connection getConn() throws DAOException{
    	if(!isConnActive()){
    		throw new DAOException("[guteDAO] ERRO: Conexao inativa.");
    	}
    	return this.conn;
    }
    
    /**
	 * @return the DAO Result
	 */
	public DAOResult getDAOResult() {
		return daoResult;
	}

	/**
	 * @param daoResult the rs to set
	 */
	public void setDAOResult(DAOResult daoResult) {
		this.daoResult = daoResult;
	}
    
    private boolean isConnActive() throws DAOException{
	    try{
	    	if(this.conn !=null && !this.conn.isClosed()){
	    		return true;
	    	}else{
	    		return false;
	    	}
	    }catch (SQLException e) {
			logger.error("[guteDAO] Erro ao tentar verificar a situacao da conexao:"+e.getMessage());
			throw new DAOException(e);
		}
    }
    
    private PreparedStatement createStatement(String query) throws DAOException{
    	try {
			return this.getConn().prepareStatement(query);
		} catch (SQLException e) {
			logger.error("[guteDAO] Erro ao tentar criar um statement:"+e.getMessage());
			throw new DAOException(e);
		}
    }
    
    public DAOResult executeQuery(String query) throws DAOException{
    	logger.debug("[guteDAO] Executing Query:"+query);
    	
    	PreparedStatement stmt = this.createStatement(query);
    	
    	ResultSet rs;
		try {
			setParamStatement(stmt);
	    	
			rs = stmt.executeQuery();
		} catch (SQLException e) {
 			logger.error("[guteDAO] Erro ao tentar executar uma query:"+e.getMessage());
			throw new DAOException(e);
		}
		
		DAOResult daoRes = new DAOResult(rs);
    	this.setDAOResult(daoRes);
    	return daoRes;
    }
    
    public int executeUpdate(String query) throws DAOException{
    	logger.debug("[guteDAO] Executing Update:"+query);

    	PreparedStatement stmt = this.createStatement(query);

    	int rowsAffected;
		try {
			setParamStatement(stmt);

			rowsAffected = stmt.executeUpdate();
			
			if(this.isTransactionMode()) isCommited = false;
		}  catch (SQLException e) {
 			logger.error("[guteDAO] Erro ao tentar executar um update:"+e.getMessage());
			throw new DAOException(e);
		}
    	
    	return rowsAffected;
    }

    public void setTransactionMode() throws DAOException{
		this.setTransactionMode(false);
    }
    
    public void setTransactionMode(boolean transMode) throws DAOException{
    	try {
			this.getConn().setAutoCommit(transMode);
		} catch (SQLException e) {
			logger.error("[guteDAO] Erro ao tentar setar a flag de AutoCommit:"+e.getMessage());
			throw new DAOException(e);
		}
    	this.transactionMode = transMode;
    }
    
    public boolean isTransactionMode(){
    	return this.transactionMode;
    }
    
    public void commit() throws DAOException{
    	logger.debug("[guteDAO] Commiting changes.");
    	
    	if(!this.isTransactionMode()) logger.warn("[guteDAO] ATENCAO: Tentativa em commit manual quando o auto-commit se encontra ativado!");
    	try{
    		this.getConn().commit();
    		this.isCommited=true;
    	}catch(SQLException e){
    		logger.error("[guteDAO] Erro ao tentar efetuar o commit:"+e.getMessage());
    		throw new DAOException(e);
    	}
    }
    
    public void rollback(){
    	logger.debug("[guteDAO] Rolling back changes.");
    	
    	if(!this.isTransactionMode()) logger.warn("[guteDAO] ATENCAO: Tentativa em commit manual quando o auto-commit se encontra ativado!");
    	
    	try{
    		this.getConn().rollback();
    	}catch(Exception e){
    		logger.error("[guteDAO] Erro ao tentar efetuar o rollback:"+e.getMessage());
    	} 
    }
    
    public void closeDao(){
    	logger.debug("[guteDAO] Closing DAO.");
    	try {
	    	if (this.isTransactionMode() && !isCommited){
	    		this.commit();
	    	}
	    	this.setTransactionMode(false);
	    	
			this.getConn().close();
			
			if(this.getDAOResult()!=null){
				this.getDAOResult().closeResultSet();
			}
			
		} catch (SQLException e) {
    		logger.error("[guteDAO] Erro ao tentar fechar o DAO:"+e.getMessage());
		} catch (DAOException e) {
    		logger.error("[guteDAO] Erro ao tentar fechar o DAO:"+e.getMessage());
		}
    	
    }
    
    public void setParam(String paramName, Object value){
    	this.parameters.put(paramName, value);
    }

	public void removeParam(String paramName){
    	this.parameters.remove(paramName);
    }
	
	public Object getParam(String paramName){
    	return this.parameters.get(paramName);
    }

	private void setParamStatement(PreparedStatement stmt)
			throws SQLException {
		
		int i=1;
		for (Map.Entry<String, Object> e : parameters.entrySet()) {
			
			if(i==1) logger.debug("[guteDAO] Parametters:");
			
			String key = e.getKey();
			Object value = e.getValue();

			if(value instanceof String){
				stmt.setString(i, (String) value);
			}else if(value instanceof Integer){
				stmt.setInt(i, (Integer) value);
			}else if(value instanceof Date){
				stmt.setDate(i, new java.sql.Date(((Date)value).getTime()));
			}else if(value instanceof java.sql.Date){
				stmt.setDate(i, (java.sql.Date) value);
			}else if(value instanceof Double){
				stmt.setDouble(i, (Double) value);
			}else if(value instanceof BigDecimal){
				stmt.setBigDecimal(i, (BigDecimal) value);
			}

			logger.debug("[guteDAO]    <"+i+">["+key+"]:\""+String.valueOf(value)+"\" ");
			
			i++;
		}
	}
}
