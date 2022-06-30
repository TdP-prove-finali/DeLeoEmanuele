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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public double getPesoMax() {
		return pesoMax;
	}

	public void setPesoMax(double pesoMax) {
		this.pesoMax = pesoMax;
	}

	public double getSpazioMax() {
		return spazioMax;
	}


	public void setSpazioMax(double spazioMax) {
		this.spazioMax = spazioMax;
	}


	public double getVelocitaMedia() {
		return velocitaMedia;
	}

	
	public void setVelocitaMedia(double velocitaMedia) {
		this.velocitaMedia = velocitaMedia;
	}


	public double getCostoCarburante() {
		return costoCarburante;
	}


	public void setCostoCarburante(double costoCarburante) {
		this.costoCarburante = costoCarburante;
	}


	public Citta getCitta() {
		return citta;
	}


	public void setCitta(Citta citta) {
		this.citta = citta;
	}

	public void assegnaOrdine(Ordine o) {
		this.spazioOccupato += o.getVolume();
		this.pesoOccupato += o.getPeso();
		this.dataMezzo = o.getDataOra();
		ordiniMezzo.add(o);

	}

	public PriorityQueue<Ordine> getOrdiniMezzo() {
		return ordiniMezzo;
	}

	public void setOrdiniMezzo(List<Ordine> ordiniMezzo) {
		for (Ordine o : ordiniMezzo) {
			this.ordiniMezzo.add(o);
		}
	}

	public LocalDateTime getDataMezzo() {
		return dataMezzo;
	}

	public void setDataMezzo(LocalDateTime dataMezzo) {
		this.dataMezzo = dataMezzo;
	}

	public Citta getDestinazione() {
		return destinazione;
	}

	public void setDestinazione(Citta destinazione) {
		this.destinazione = destinazione;
	}

	public void setOrdiniMezzo(PriorityQueue<Ordine> ordiniMezzo) {
		this.ordiniMezzo = ordiniMezzo;
	}

	public Stato getStato() {
		return stato;
	}

	public void setStato(Stato stato) {
		this.stato = stato;
	}

	public double getPesoOccupato() {
		return pesoOccupato;
	}

	public void setPesoOccupato(double pesoOccupato) {
		this.pesoOccupato = pesoOccupato;
	}

	public double getSpazioOccupato() {
		return spazioOccupato;
	}

	public void setSpazioOccupato(double spazioOccupato) {
		this.spazioOccupato = spazioOccupato;
	}

	public boolean isFlagPieno() {
		return flagPieno;
	}

	public void setFlagPieno(boolean flagPieno) {
		this.flagPieno = flagPieno;
	}

	public int getNumeroVIaggi() {
		return numeroVIaggi;
	}

	public void aggiungiVIaggio() {
		this.numeroVIaggi++;
	}

}
