package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.time.LocalDateTime;

public class Event implements Comparable<Event> {

	public enum EventType {
		NUOVO_ORDINE, ORDINE_IN_CORSO
	}

	private Ordine ordine;
	private LocalDateTime istante;
	private EventType tipo;

	public Event(Ordine ordine, EventType tipo, LocalDateTime istante) {
		super();
		this.ordine = ordine;
		this.tipo = tipo;
		this.istante = istante;
	}

	/**
	 * @return the istante
	 */
	public LocalDateTime getIstante() {
		return istante;
	}

	/**
	 * @param istante the istante to set
	 */
	public void setIstante(LocalDateTime istante) {
		this.istante = istante;
	}

	/**
	 * @return the tipo
	 */
	public EventType getTipo() {
		return tipo;
	}

	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(EventType tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the ordine
	 */
	public Ordine getOrdine() {
		return ordine;
	}

	/**
	 * @param ordine the ordine to set
	 */
	public void setOrdine(Ordine ordine) {
		this.ordine = ordine;
	}

	@Override
	public int compareTo(Event other) {
		// TODO Auto-generated method stub
		return this.istante.compareTo(other.istante);
	}

	@Override
	public String toString() {
		return "Event [ordine=" + ordine.getId() + ", istante=" + istante + ", tipo=" + tipo + "]";
	}

}
