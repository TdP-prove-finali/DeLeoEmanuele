package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Ordine implements Comparable<Ordine> {

	int id;
	Citta sorgente;
	Citta destinazione;
	double peso;
	double volume;
	LocalDateTime dataOra;
	boolean timeout;
	Citta prossimaCitta;

	public Ordine(int id, Citta sorgente, Citta destinazione, double peso, double volume, LocalDateTime dataOra) {

		this.id = id;
		this.sorgente = sorgente;
		this.destinazione = destinazione;
		this.peso = peso;
		this.volume = volume;
		this.dataOra=dataOra;
		this.timeout = false;
		prossimaCitta=null;
		
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

	/**
	 * @param destinazione the destinazione to set
	 */
	public void setDestinazione(Citta destinazione) {
		this.destinazione = destinazione;
	}

	/**
	 * @return the peso
	 */
	public double getPeso() {
		return peso;
	}

	/**
	 * @param peso the peso to set
	 */
	public void setPeso(double peso) {
		this.peso = peso;
	}

	/**
	 * @return the volume
	 */
	public double getVolume() {
		return volume;
	}

	/**
	 * @param volume the volume to set
	 */
	public void setVolume(double volume) {
		this.volume = volume;
	}
	
	
	
	/**
	 * @return the dataOra
	 */
	public LocalDateTime getDataOra() {
		return dataOra;
	}

	/**
	 * @param dataOra the dataOra to set
	 */
	public void setDataOra(LocalDateTime dataOra) {
		this.dataOra = dataOra;
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
		
		if (this.isTimeout()==true && other.isTimeout()==false) {
			return 1;
		}
		
		if (this.isTimeout()==false && other.isTimeout()==true) {
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

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(boolean timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the prossimaCitta
	 */
	public Citta getProssimaCitta() {
		return prossimaCitta;
	}

	/**
	 * @param prossimaCitta the prossimaCitta to set
	 */
	public void setProssimaCitta(Citta prossimaCitta) {
		this.prossimaCitta = prossimaCitta;
	}

	
	

}
