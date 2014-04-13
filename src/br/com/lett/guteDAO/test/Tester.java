package br.com.lett.guteDAO.test;

import java.sql.ResultSet;

import br.com.lett.guteDAO.DAOFactory;
import br.com.lett.guteDAO.DataAccessObject;
import br.com.lett.guteDAO.GuteDAO;


public class Tester {
	
	public static void main(String args[]){
		try {
			
			GuteDAO guteDao = new GuteDAO();
			guteDao.startup();
			
			DataAccessObject dao = DAOFactory.createDAO();
			
			ResultSet rs = null; //dao.executeQuery("SELECT * FROM TAB_AULA3");
			
			while (rs.next()) {
				System.out.println("Nome:"+rs.getString("nome"));
		    }
			 
			dao.closeDao();
			guteDao.destroy();
		} catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
