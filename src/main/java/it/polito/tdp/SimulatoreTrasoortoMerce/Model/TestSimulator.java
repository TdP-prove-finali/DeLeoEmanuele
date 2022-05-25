package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestSimulator {

	public static void main(String[] args) {

		Model m = new Model();
		Simulator sim = new Simulator();

		// impostazione parametri

		m.generaMezzo("Autobus", 2.3, 3.5, 5.5, 1.2);
		sim.setnOrdiniGiornalieri(50);
		sim.setnGiorni(2);
		sim.setDataInizio(LocalDate.of(2022, 5, 13));
		sim.setOraInizio(10, 00);
		sim.setOraFine(18, 00);
		sim.setTimeout(1);
		// esecuzione

		// output
		System.out.println(m.creaGrafo());
		sim.init(m.getDijkstra(), m.grafo, m.getMezziConSpecifiche(), m.getMappaCitta().values(),
				m.getMetropoli());

		sim.run();
System.out.println(sim.getnTir());
	}

}
