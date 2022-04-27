package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.time.LocalDateTime;

public class TestModel {

	public static void main(String[] args) {
		

		
		Model m = new Model();
		LocalDateTime data = LocalDateTime.now();
		Mezzo mezzo = new Mezzo(1,"Aereo",2.3,3.5,4.5,1,m.getMappaCitta().get("Agrigento"));
		Mezzo mezzo2 = new Mezzo(2,"Treno",2.3,3.5,4.5,1,m.getMappaCitta().get("TOrino"));
		m.aggiungiMezzo(mezzo);
		m.aggiungiMezzo(mezzo2);
		System.out.println(m.creaGrafo());
		
		Ordine o = new Ordine (1,m.getMappaCitta().get("Vercelli"),m.getMappaCitta().get("Trapani"), 0.9, 0.4, data);
		
		Mezzo migliore = m.getMezzoMigliore(o, m.getListaMezzi(), 0.7, 0.2, 0.1);
		
		System.out.println(migliore);
	}

}
