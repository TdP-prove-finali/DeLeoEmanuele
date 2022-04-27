package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Mezzo {
	
	int id;
	String tipo;
	Citta citta;
	double pesoMax;
	double spazioMax;
	double velocitaMedia;
	double costoCarburante;
	double pesoOccupato;
	double spazioOccupato;
	List<Ordine> ordiniMezzo;

	
	
	public Mezzo(int id, String tipo, double pesoMax, double spazioMax, double velocitaMedia, double costoCarburante, Citta citta) {
		this.id = id;
		this.tipo = tipo;
		this.pesoMax = pesoMax;
		this.spazioMax = spazioMax;
		this.velocitaMedia = velocitaMedia;
		this.costoCarburante = costoCarburante;
		this.citta = citta;
		this.pesoOccupato=0.0;
		this.spazioOccupato=0.0;
		ordiniMezzo = new LinkedList<Ordine>();
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
	 * @return the pesoOccupato
	 */
	public double getPesoOccupato() {
		return pesoOccupato;
	}


	/**
	 * @param pesoOccupato the pesoOccupato to set
	 */
	public void assegnaOrdine(Ordine o) {
		ordiniMezzo.add(o);
	    this.spazioOccupato = this.spazioOccupato+o.getVolume();
	    this.pesoOccupato = this.pesoOccupato+o.getPeso();
		
	}

	public void scaricaMerce(Ordine o) {
		ordiniMezzo.remove(o);
	}

	/**
	 * @return the spazioOccupato
	 */
	public double getSpazioOccupato() {
		return spazioOccupato;
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
		Mezzo other = (Mezzo) obj;
		return id == other.id;
	}


	@Override
	public String toString() {
		return "Mezzo [id=" + id + ", tipo=" + tipo + ", pesoMax=" + pesoMax + ", spazioMax=" + spazioMax
				+ ", velocitaMedia=" + velocitaMedia + ", costoCarburante=" + costoCarburante + "]";
	}
	
	

}
