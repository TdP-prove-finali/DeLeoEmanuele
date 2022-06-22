package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.util.Objects;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Arco extends DefaultWeightedEdge {

	private static final long serialVersionUID = 1L;
	double distanza;
	String tipo;
	int id;

	public Arco() {
		// TODO Auto-generated constructor stub
	}

	public Arco(double distanza, String tipo, int id) {
		super();
		this.distanza = distanza;
		this.tipo = tipo;
		this.id = id;
	}

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
	public String toString() {
		return "Arco [ distanza: " + distanza + ", tipo=" + tipo + "]";
	}

	public Object getSorgente() {
		return this.getSource();
	}

	public Object getDestinazione() {
		return this.getTarget();
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

	@Override
	public int hashCode() {
		return Objects.hash(distanza, id, tipo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Arco other = (Arco) obj;
		return Double.doubleToLongBits(distanza) == Double.doubleToLongBits(other.distanza) && id == other.id
				&& Objects.equals(tipo, other.tipo);
	}

}
