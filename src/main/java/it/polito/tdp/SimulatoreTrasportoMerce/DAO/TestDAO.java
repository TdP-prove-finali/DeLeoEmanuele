package it.polito.tdp.SimulatoreTrasportoMerce.DAO;

import java.time.LocalDateTime;

import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Citta;
import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Ordine;

public class TestDAO {

	public static void main(String[] args) {

	DAO dao = new DAO();
	
	Citta ci = new Citta("io");
	Citta cv = new Citta("tu");
	
	dao.addOrdine(new Ordine(1,ci,cv,5.5,6.7, LocalDateTime.now()));
	}

}
