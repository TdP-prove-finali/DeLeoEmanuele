package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.PriorityQueue;

public class Mezzo {

	public enum Stato {
		DISPONIBILE, OCCUPATO
	};

	int id;
	String tipo;
	Citta citta;
	double pesoOccupato;
	double spazioOccupato;
	double pesoMax;
	double spazioMax;
	double velocitaMedia;
	double costoCarburante;
	PriorityQueue<Ordine> ordiniMezzo;
	LocalDateTime dataMezzo;
	Citta destinazione;
	private Stato stato;
	boolean flagPieno;
	int numeroVIaggi;

	public Mezzo(int id, String tipo, double pesoMax, double spazioMax, double velocitaMedia, double costoCarburante,
			Citta citta, Stato stato) {
		this.id = id;
		this.tipo = tipo;
		this.pesoMax = pesoMax;
		this.spazioMax = spazioMax;
		this.velocitaMedia = velocitaMedia;
		this.costoCarburante = costoCarburante;
		this.citta = citta;
		this.stato = stato;
		this.pesoOccupato = 0.0;
		this.spazioOccupato = 0.0;
		ordiniMezzo = new PriorityQueue<Ordine>();
		destinazione = null;
		flagPieno = false;
		numeroVIaggi = 0;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the pesoMax
	 */
	public double getPesoMax() {
		return pesoMax;
	}

	/**
	 * @param pesoMax the pesoMax to set
	 */
	public void setPesoMax(double pesoMax) {
		this.pesoMax = pesoMax;
	}

	/**
	 * @return the spazioMax
	 */
	public double getSpazioMax() {
		return spazioMax;
	}

	/**
	 * @param spazioMax the spazioMax to set
	 */
	public void setSpazioMax(double spazioMax) {
		this.spazioMax = spazioMax;
	}

	/**
	 * @return the velocitaMedia
	 */
	public double getVelocitaMedia() {
		return velocitaMedia;
	}

	/**
	 * @param velocitaMedia the velocitaMedia to set
	 */
	public void setVelocitaMedia(double velocitaMedia) {
		this.velocitaMedia = velocitaMedia;
	}

	/**
	 * @return the costoCarburante
	 */
	public double getCostoCarburante() {
		return costoCarburante;
	}

	/**
	 * @param costoCarburante the costoCarburante to set
	 */
	public void setCostoCarburante(double costoCarburante) {
		this.costoCarburante = costoCarburante;
	}

	/**
	 * @return the citta
	 */
	public Citta getCitta() {
		return citta;
	}

	/**
	 * @param citta the citta to set
	 */
	public void setCitta(Citta citta) {
		this.citta = citta;
	}

	/**
	 * @param pesoOccupato the pesoOccupato to set
	 */
	public void assegnaOrdine(Ordine o) {
		this.spazioOccupato += o.getVolume();
		this.pesoOccupato += o.getPeso();
		this.dataMezzo = o.getDataOra();
		ordiniMezzo.add(o);

	}

	/**
	 * @return the ordiniMezzo
	 */
	public PriorityQueue<Ordine> getOrdiniMezzo() {
		return ordiniMezzo;
	}

	/**
	 * @param ordiniMezzo the ordiniMezzo to set
	 */
	public void setOrdiniMezzo(List<Ordine> ordiniMezzo) {
		for (Ordine o : ordiniMezzo) {
			this.ordiniMezzo.add(o);
		}
	}

	public boolean areTuttiGliOrdiniInTimeout() {

		for (Ordine o : this.ordiniMezzo) {

			if (o.isTimeout() == false) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @return the dataMezzo
	 */
	public LocalDateTime getDataMezzo() {
		return dataMezzo;
	}

	/**
	 * @param dataMezzo the dataMezzo to set
	 */
	public void setDataMezzo(LocalDateTime dataMezzo) {
		this.dataMezzo = dataMezzo;
	}

	/**
	 * @return the destinazione
	 */
	public Citta getDestinazione() {
		return destinazione;
	}

	/**
	 * @param destinazione the destinazione to set
	 */
	public void setDestinazione(Citta destinazione) {
		this.destinazione = destinazione;
	}

	/**
	 * @param ordiniMezzo the ordiniMezzo to set
	 */
	public void setOrdiniMezzo(PriorityQueue<Ordine> ordiniMezzo) {
		this.ordiniMezzo = ordiniMezzo;
	}

	/**
	 * @return the stato
	 */
	public Stato getStato() {
		return stato;
	}

	/**
	 * @param stato the stato to set
	 */
	public void setStato(Stato stato) {
		this.stato = stato;
	}

	/**
	 * @return the pesoOccupato
	 */
	public double getPesoOccupato() {
		return pesoOccupato;
	}

	/**
	 * @param pesoOccupato the pesoOccupato to set
	 */
	public void setPesoOccupato(double pesoOccupato) {
		this.pesoOccupato = pesoOccupato;
	}

	/**
	 * @return the spazioOccupato
	 */
	public double getSpazioOccupato() {
		return spazioOccupato;
	}

	/**
	 * @param spazioOccupato the spazioOccupato to set
	 */
	public void setSpazioOccupato(double spazioOccupato) {
		this.spazioOccupato = spazioOccupato;
	}

	/**
	 * @return the flagPieno
	 */
	public boolean isFlagPieno() {
		return flagPieno;
	}

	/**
	 * @param flagPieno the flagPieno to set
	 */
	public void setFlagPieno(boolean flagPieno) {
		this.flagPieno = flagPieno;
	}

	/**
	 * @return the numeroVIaggi
	 */
	public int getNumeroVIaggi() {
		return numeroVIaggi;
	}

	/**
	 * @param numeroVIaggi the numeroVIaggi to set
	 */
	public void aggiungiVIaggio() {
		this.numeroVIaggi++;
	}

}
