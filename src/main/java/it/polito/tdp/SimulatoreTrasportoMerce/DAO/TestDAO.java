package it.polito.tdp.SimulatoreTrasportoMerce.DAO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Citta;
import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Mezzo;
import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Ordine;

public class TestDAO {

	public static void main(String[] args) {

		DAO dao = new DAO();

		List<Mezzo> mezzi = new ArrayList<Mezzo>();
		
		Mezzo mezzo = new Mezzo(1,"Aereo",2.3,3.5,4.5,1);
		mezzi.add(mezzo);
	
		
	}

}
