package br.com.lett.guteDAO;

public class DAOException extends Exception {
	
	/** Serial */
	private static final long serialVersionUID = -2053073837369052255L;

	public DAOException(Exception e){
		super(e);
	}
	
	public DAOException(String excep){
		super(excep);
	}
}
