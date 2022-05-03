package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.util.Objects;

import org.jgrapht.graph.DefaultWeightedEdge;

public class Arco extends DefaultWeightedEdge {
	
	private static final long serialVersionUID = 1L;
	
	Citta sorgente;
	Citta destinazione;
	double distanza;
	String tipo;
	
	
	public Arco() {
		// TODO Auto-generated constructor stub
	}
	
	public Arco(Citta sorgente, Citta destinazione,double distanza, String tipo) {
		super();
		this.distanza= distanza;
		this.tipo=tipo;
		this.sorgente=sorgente;
		this.destinazione =destinazione;
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
	
	@Override
	public int hashCode() {
		return Objects.hash(destinazione, distanza, sorgente, tipo);
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Arco other = (Arco) obj;
		return Objects.equals(destinazione, other.destinazione)
				&& Double.doubleToLongBits(distanza) == Double.doubleToLongBits(other.distanza)
				&& Objects.equals(sorgente, other.sorgente) && Objects.equals(tipo, other.tipo);
	}

	@Override
	public String toString() {
		return "Arco [sorgente=" + sorgente + ", destinazione=" + destinazione + ", distanza=" + distanza + ", tipo="
				+ tipo + "]";
	}

	
	
}
