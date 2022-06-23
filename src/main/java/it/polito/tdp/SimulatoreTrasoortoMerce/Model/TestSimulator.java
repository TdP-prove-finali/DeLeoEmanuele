package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.time.LocalDate;

public class TestSimulator {

	public static void main(String[] args) {

		Model m = new Model();
		Simulator sim = new Simulator();
		m.clearTableOrdini();
		m.clearTableOrdiniConsegnati();

		// impostazione parametri

		m.generaMezzo("Aereo", 50, 200, 400, 7);
		m.generaMezzo("Autobus", 50, 300, 100, 2.5);
		sim.setnOrdiniGiornalieri(1000);
		sim.setnGiorni(4);
		sim.setDataInizio(LocalDate.of(2022, 5, 13));
		sim.setOraInizio(8, 00);
		sim.setOraFine(16, 00);
		sim.setTimeout(1);
		// esecuzione

		// output
		System.out.println(m.creaGrafo(50));

		sim.init(m.getDijkstra(), m.grafo, m.getMezziConSpecifiche(), m.getMappaCitta(), m.getMetropoli(), 1);

		sim.run();

		System.out.println("Ordini Completati: " + sim.getnOrdiniCompletati());

		System.out.println(
				"tir: " + sim.getNtir() + "  aerei: " + sim.getNaerei() + " costo:" + sim.getCostoTotale() + "$");
	}

}
