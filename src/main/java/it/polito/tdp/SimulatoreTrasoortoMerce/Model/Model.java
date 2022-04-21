package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.SimulatoreTrasportoMerce.DAO.DAO;

public class Model {
	
	private Graph<Citta, DefaultWeightedEdge> grafo;
	DAO dao;
	List<Mezzo> mezzi;
	List<Citta> citta;
	
	
	public Model() {
	dao = new DAO();
	mezzi = new LinkedList<Mezzo>();
	this.citta = dao.getAllCitta();
	}

	public String creaGrafo() {
	List<Tratta> tratte = dao.getTratte(mezzi, citta);	
	this.grafo = new SimpleWeightedGraph<Citta, DefaultWeightedEdge>(DefaultWeightedEdge.class) ;
	Graphs.addAllVertices(this.grafo, citta);	
		
	for (Tratta t : tratte) {
		if (grafo.containsVertex(t.getSorgente()) && grafo.containsVertex(t.getDestinazione())) {
			
		Graphs.addEdge(grafo, t.getSorgente(), t.getDestinazione(), t.getDistanza());
		}
	}	
		return String.format("Grafo creato con %d vertici e %d archi\n",
				this.grafo.vertexSet().size(),
				this.grafo.edgeSet().size()) ;
		
	}
	
	
	
	public void aggiungiMezzo(Mezzo m) {
		mezzi.add(m);
	} 
 	
	
	public Citta getCitta(String nome) {
		Citta ris = null;
		for (Citta c : this.citta) {
			if (c.getNome().compareTo(nome)==0) {
				ris = c;
				break;
			}
		}
		return ris;
	}
	
	
	
	
	
	
	
	
	
	

}
