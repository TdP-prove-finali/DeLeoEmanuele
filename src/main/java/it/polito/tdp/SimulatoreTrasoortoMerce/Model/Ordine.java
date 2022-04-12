package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Ordine {
	
	int id;
	String sorgente;
	String destinazione;
	double peso;
	double volume;
	LocalDateTime data;
	
	public Ordine(int id, String sorgente, String destinazione, double peso, double volume, LocalDateTime data) {
		
		this.id = id;
		this.sorgente = sorgente;
		this.destinazione = destinazione;
		this.peso = peso;
		this.volume = volume;
		this.data=data;
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
	public String getSorgente() {
		return sorgente;
	}

	/**
	 * @param sorgente the sorgente to set
	 */
	public void setSorgente(String sorgente) {
		this.sorgente = sorgente;
	}

	/**
	 * @return the destinazione
	 */
	public String getDestinazione() {
		return destinazione;
	}

	/**
	 * @param destinazione the destinazione to set
	 */
	public void setDestinazione(String destinazione) {
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
	
	public LocalDateTime getData() {
		return this.data;
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
	public String toString() {
		return "Ordine [id=" + id + ", sorgente=" + sorgente + ", destinazione=" + destinazione + ", peso=" + peso
				+ ", volume=" + volume + "]";
	}
	
	
	

}
