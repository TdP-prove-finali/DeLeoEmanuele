package it.polito.tdp.SimulatoreTrasportoMerce.DAO;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Citta;
import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Ordine;

public class TestDAO {

	public static void main(String[] args) {

		DAO dao = new DAO();

LocalDateTime date = LocalDateTime.now();
Ordine oo = new Ordine(5,"sorgente","destino",34.5,7.6,date);
dao.addOrdine(oo);
	}

}
