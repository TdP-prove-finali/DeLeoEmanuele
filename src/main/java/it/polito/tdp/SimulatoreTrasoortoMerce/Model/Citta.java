package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

public class Citta {

String nome;

public Citta(String nome) {
	this.nome = nome;
}


/**
 * @return the nome
 */
public String getNome() {
	return nome;
}



/**
 * @param nome the nome to set
 */
public void setNome(String nome) {
	this.nome = nome;
}



@Override
public String toString() {
	return "" + nome + "";
}



}
