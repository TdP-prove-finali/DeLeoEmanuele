package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.time.LocalDateTime;

public class TestModel {

	public static void main(String[] args) {

		Model m = new Model();
		// LocalDateTime data = LocalDateTime.now();
		Mezzo mezzo2 = new Mezzo(1, "Tir", 2.3, 3.5, 4.5, 1.6, m.getMappaCitta().get("Gorizia"));
		// Mezzo mezzo1 = new
		// Mezzo(1,"Autobus",2.3,3.5,4.5,1.6,m.getMappaCitta().get("Gorizia"));
		// m.aggiungiMezzo(mezzo1);
		m.generaMezzo("Aereo", 2.3, 3.5, 5.5, 1.2);
		System.out.println(m.creaGrafo());
		System.out.println(m.dao.getOrdini());

		// m.caricaOrdini();
		// System.out.println(m.getMappaOrdini().values().toString());
		// Ordine o = new Ordine (1,m.getMappaCitta().get("Bolzano -
		// Bozen"),m.getMappaCitta().get("Trapani"), 0.9, 0.6, LocalDateTime.now());

		// Mezzo migliore = m.getMezzoMigliore(o, m.getListaMezzi(), 0.7, 0.2, 0.1);

		// System.out.println(migliore);
	}

}
