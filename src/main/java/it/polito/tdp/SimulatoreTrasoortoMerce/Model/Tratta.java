package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.util.Objects;

public class Tratta {
	
	Citta sorgente;
	Citta destinazione;
	double distanza;
	String mezzoTrasporto;
	int emissioni;
	
	public Tratta(Citta sorgente, Citta destinazione, double distanza, String mezzoTrasporto, int emissioni) {
		this.sorgente = sorgente;
		this.destinazione = destinazione;
		this.distanza = distanza;
		this.mezzoTrasporto = mezzoTrasporto;
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
	 * @return the distanza
	 */
	public double getDistanza() {
		return distanza;
	}

	/**
	 * @param distanza the distanza to set
	 */
	public void setDistanza(double distanza) {
		this.distanza = distanza;
	}

	/**
	 * @return the mezzoTrasporto
	 */
	public String getMezzoTrasporto() {
		return mezzoTrasporto;
	}

	/**
	 * @param mezzoTrasporto the mezzoTrasporto to set
	 */
	public void setMezzoTrasporto(String mezzoTrasporto) {
		this.mezzoTrasporto = mezzoTrasporto;
	}

	/**
	 * @return the emissioni
	 */
	public int getEmissioni() {
		return emissioni;
	}

	/**
	 * @param emissioni the emissioni to set
	 */
	public void setEmissioni(int emissioni) {
		this.emissioni = emissioni;
	}


	@Override
	public String toString() {
		return "Tratta [sorgente=" + sorgente + ", destinazione=" + destinazione + ", distanza=" + distanza
				+ ", mezzoTrasporto=" + mezzoTrasporto + ", emissioni=" + emissioni + "]";
	}

	
	
	
	
	
}

