package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

public class TestModel {

	public static void main(String[] args) {
		

		
		Model m = new Model();
		Mezzo mezzo = new Mezzo(1,"Aereo",2.3,3.5,4.5,1);
	
		m.aggiungiMezzo(mezzo);
		System.out.println(m.creaGrafo());
	}

}
