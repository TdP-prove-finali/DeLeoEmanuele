package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.time.LocalDateTime;

public class OrdineConsegnato implements Comparable<OrdineConsegnato> {

	int id;
	Citta cittaConsegna;
	LocalDateTime data;
	int id_mezzo;
	String tipo;

	public OrdineConsegnato(int id, Citta cittaConsegna, LocalDateTime data, int id_mezzo, String tipo) {
		super();
		this.id = id;
		this.cittaConsegna = cittaConsegna;
		this.data = data;
		this.id_mezzo = id_mezzo;
		this.tipo = tipo;
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
	 * @return the cittaConsegna
	 */
	public Citta getCittaConsegna() {
		return cittaConsegna;
	}

	/**
	 * @param cittaConsegna the cittaConsegna to set
	 */
	public void setCittaConsegna(Citta cittaConsegna) {
		this.cittaConsegna = cittaConsegna;
	}

	/**
	 * @return the data
	 */
	public LocalDateTime getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(LocalDateTime data) {
		this.data = data;
	}

	/**
	 * @return the id_mezzo
	 */
	public int getId_mezzo() {
		return id_mezzo;
	}

	/**
	 * @param id_mezzo the id_mezzo to set
	 */
	public void setId_mezzo(int id_mezzo) {
		this.id_mezzo = id_mezzo;
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
		return String.format("%-10s %10s %10s %10s %10s", "ID_ORDINE=" + id, " città=" + cittaConsegna, " data=" + data,
				" id_mezzo=" + id_mezzo, " tipo=" + tipo + "\n");
	}

	@Override
	public int compareTo(OrdineConsegnato other) {
		// TODO Auto-generated method stub
		return this.data.compareTo(other.data);
	}

}
