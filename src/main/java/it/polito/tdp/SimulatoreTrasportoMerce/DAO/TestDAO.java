package it.polito.tdp.SimulatoreTrasportoMerce.DAO;

import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Citta;

public class TestDAO {

	public static void main(String[] args) {
		
		
		DAO dao = new DAO();


		for (Citta c : dao.getAllCitta()) {
			System.out.println(c.toString());
			
		}
		
		

	}

}
