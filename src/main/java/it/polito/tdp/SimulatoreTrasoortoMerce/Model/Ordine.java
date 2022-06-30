package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Ordine implements Comparable<Ordine> {

	int id;
	Citta sorgente;
	Citta destinazione;
	double peso;
	double volume;
	LocalDateTime dataOra;
	boolean timeout;
	LinkedList<Arco> percorso;
	Mezzo mezzo;
	LocalDateTime dataArrivo;
	String dataOraString;
	String dataArrivoString;

	// TODO Aggiungere uno stato IN_ELABORAZIONE, IN_ATTESA

	public Ordine(int id, Citta sorgente, Citta destinazione, double peso, double volume, LocalDateTime dataOra) {

		this.id = id;
		this.sorgente = sorgente;
		this.destinazione = destinazione;
		this.peso = peso;
		this.volume = volume;
		this.dataOra = dataOra;
		this.timeout = false;
		this.mezzo = null;
		this.percorso = new LinkedList<Arco>();
		this.dataArrivo = null;

	}

	public Ordine(int id, Citta sorgente, Citta destinazione, double peso, double volume, String dataPartenza,
			String dataArrivo) {

		this.id = id;
		this.sorgente = sorgente;
		this.destinazione = destinazione;
		this.peso = peso;
		this.volume = volume;
		this.timeout = false;
		this.mezzo = null;
		this.percorso = new LinkedList<Arco>();
		this.dataArrivo = null;
		this.dataOraString = dataPartenza;
		this.dataArrivoString = dataArrivo;

	}

	public int getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the sorgente
	 */
	public Citta getSorgente() {
		return sorgente;
	}

	/**
	 * @param sorgente the sorgente to set
	 */
	public void setSorgente(Citta sorgente) {
		this.sorgente = sorgente;
	}

	/**
	 * @return the destinazione
	 */
	public Citta getDestinazione() {
		return destinazione;
	}

	public double getPeso() {
		return peso;
	}

	public double getVolume() {
		return volume;
	}

	public LocalDateTime getDataOra() {
		return dataOra;
	}

	public void setDataOra(LocalDateTime dataOra) {
		this.dataOra = dataOra;
	}

	public List<Arco> getPercorso() {
		return percorso;
	}

	public void setPercorso(List<Arco> percorso) {
		this.percorso = new LinkedList<Arco>(percorso);
	}

	public Arco getProssimaTratta() {
		return ((LinkedList<Arco>) percorso).peek();
	}

	public String getDataOraString() {
		return dataOraString;
	}

	public void setDataOraString(String dataOraString) {
		this.dataOraString = dataOraString;
	}

	public String getDataArrivoString() {
		return dataArrivoString;
	}

	public void setDataArrivoString(String dataArrivoString) {
		this.dataArrivoString = dataArrivoString;
	}

	public void rimuoviTratta() {

		((LinkedList<Arco>) percorso).poll();

	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ordine other = (Ordine) obj;
		return id == other.id;
	}

	@Override
	public int compareTo(Ordine other) {

		if (this.isTimeout() == true && other.isTimeout() == false) {
			return 1;
		}

		if (this.isTimeout() == false && other.isTimeout() == true) {
			return -1;
		}
		return this.dataOra.compareTo(other.dataOra);
	}

	/**
	 * @return the timeout
	 */
	public boolean isTimeout() {
		return timeout;
	}

	public void setTimeout(boolean timeout) {
		this.timeout = timeout;
	}

	public LocalDateTime getDataArrivo() {
		return dataArrivo;
	}

	public void setDataArrivo(LocalDateTime dataArrivo) {
		this.dataArrivo = dataArrivo;
	}

	public Mezzo getMezzo() {
		return mezzo;
	}

	public void setMezzo(Mezzo mezzo) {
		this.mezzo = mezzo;
	}

	public boolean isProcessable() {

		if (mezzo.getStato().equals(Mezzo.Stato.DISPONIBILE)
				&& mezzo.getPesoOccupato() + this.peso <= mezzo.getPesoMax()
				&& mezzo.getSpazioOccupato() + this.volume <= mezzo.getSpazioMax()) {
			return true;
		}

		if (mezzo.getStato().equals(Mezzo.Stato.DISPONIBILE)) {
			mezzo.setFlagPieno(true);
		}
		return false;
	}

	@Override
	public String toString() {
		return "-ID: " + id + "\n-Sorgente: " + sorgente + "\n-Destinazione: " + destinazione + "\n-Peso= " + peso
				+ "\n-Volume=" + volume + "\n-Data di arrivo= "
				+ dataOra.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")).toString();
	}

}
