package br.com.lett.guteDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class DAOResult {

	private ResultSet resultSet = null;

	/**
	 * @return the resultSet
	 */
	private ResultSet getResultSet() {
		return resultSet;
	}

	/**
	 * @param resultSet the resultSet to set
	 */
	private void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}
	
	DAOResult(ResultSet rs){
		this.setResultSet(rs);
	}
	
	public void closeResultSet() throws DAOException{
		try {
			this.getResultSet().close();
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}
	
	public boolean isValid() throws DAOException{
		try {
			return (this.getResultSet()!=null && !this.getResultSet().isClosed());
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}
	
	public boolean next() throws DAOException{
		try {
			return this.getResultSet().next();
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}
	
	public String getString(String column) throws DAOException{
		return (String) this.getValue(column, ObjectType.STRING);
	}
	
	public Double getDouble(String column) throws DAOException{
		return (Double) this.getValue(column, ObjectType.DOUBLE);
	}
	
	public Integer getInt(String column) throws DAOException{
		return (Integer) this.getValue(column, ObjectType.INTEGER);
	}
	
	public Date getDate(String column) throws DAOException{
		return (Date) this.getValue(column, ObjectType.DATE);
	}
	
	public Object getValue(String columnName,ObjectType type) throws DAOException{
		
		Object obj = null;
		
		try{
			switch(type){
			case DOUBLE:
				obj = this.getResultSet().getDouble(columnName);
				break;
			case INTEGER:
				obj = this.getResultSet().getInt(columnName);
				break;
			case STRING:
				obj = this.getResultSet().getString(columnName);
				break;
			case DATE:
				try{
					obj = this.getResultSet().getTimestamp(columnName);
					if (obj == null) throw new Exception("dumb");
					obj = new Date(((Timestamp) obj).getTime());
					
				}catch(Exception e){
					obj = this.getResultSet().getDate(columnName);
				}
				break;
			default:
				break;
			}
			
			if(this.getResultSet().wasNull()){
				obj = null;
			}

			return obj;
			
		}catch(SQLException e){
			throw new DAOException(e);
		}

	}
}
