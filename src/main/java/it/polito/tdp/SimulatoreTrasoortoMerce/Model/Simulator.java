package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.PriorityQueue;

public class Simulator {

	// Eventi

	private PriorityQueue<Event> queue;

	// Parametri

	private int nOrdiniGiornalieri;
	private LocalDateTime dataInizio;
	private LocalDateTime dataFine;
	private LocalTime oraInizio;           //ORE DI TRASPORTO GIORNALIERE
	private LocalTime oraFine;             // = LocalTime.of(8, 00); INPUT
    private Duration timeout;              // = Duration.ofMinutes(x)
	
	// Stato del mondo

	private int nOrdiniInAttesa;
	
	// Misure in uscita
	
	private int nOrdiniCompletati; //private int nOrdiniInRitardo

	// Impostazione parametri iniziali

	public void init(int nOrdiniGiornalieri, LocalDateTime dataInizio, LocalDateTime dataFine, LocalTime oraInizio, LocalTime oraFine, Duration timeout) {
		
		this.queue = new PriorityQueue<Event>();   // Inizializzo coda eventi
		
		this.nOrdiniInAttesa = 0;                  // Inizializzo modello del mondo
		this.nOrdiniCompletati = 0;
		
		
	}
	
	// ESECUZIONE

	public void run() {
		
		// Stato iniziale
		

		// Eventi iniziali

		// Ciclo di simulazione

		while (!this.queue.isEmpty()) {
			Event nuovoEvento = this.queue.poll();
			processEvent(nuovoEvento);
		}
	}

	private void processEvent(Event e) {

		switch (e.getTipo()) {

		case NUOVO_ORDINE:
			break;

		case TIMEOUT:
			break;

		case CONSEGNA_ORDINE:
			break;

		case ORDINE_CONSEGNATO:
			break;

		}

	}
	
	/**
	 * @param nOrdiniGiornalieri the nOrdiniGiornalieri to set
	 */
	public void setnOrdiniGiornalieri(int nOrdiniGiornalieri) {
		this.nOrdiniGiornalieri = nOrdiniGiornalieri;
	}

	/**
	 * @param dataInizio the dataInizio to set
	 */
	public void setDataInizio(LocalDateTime dataInizio) {
		this.dataInizio = dataInizio;
	}

	/**
	 * @param dataFine the dataFine to set
	 */
	public void setDataFine(LocalDateTime dataFine) {
		this.dataFine = dataFine;
	}

	/**
	 * @param oraInizio the oraInizio to set
	 */
	public void setOraInizio(LocalTime oraInizio) {
		this.oraInizio = oraInizio;
	}

	/**
	 * @param oraFine the oraFine to set
	 */
	public void setOraFine(LocalTime oraFine) {
		this.oraFine = oraFine;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(Duration timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the nOrdiniInAttesa
	 */
	public int getnOrdiniInAttesa() {
		return nOrdiniInAttesa;
	}

	/**
	 * @return the nOrdiniCompletati
	 */
	public int getnOrdiniCompletati() {
		return nOrdiniCompletati;
	}

	
}
