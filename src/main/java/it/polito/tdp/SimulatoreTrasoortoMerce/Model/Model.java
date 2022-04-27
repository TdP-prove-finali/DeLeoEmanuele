package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.SimulatoreTrasportoMerce.DAO.DAO;

public class Model {
	
	private Graph<Citta, DefaultWeightedEdge> grafo;
	DAO dao;
	List<Mezzo> mezzi;
	Map<String,Citta> mapCitta;
	Map<Integer, Ordine> mappaOrdini;
	DijkstraShortestPath<Citta, DefaultWeightedEdge> dijkstra;
	
	public Model() {
	dao = new DAO();
	mezzi = new LinkedList<Mezzo>();
	this.mapCitta = new TreeMap<String, Citta>();
	dao.getCitta(mapCitta);
	this.mappaOrdini = new LinkedHashMap<Integer, Ordine>();
	}

	public String creaGrafo() {
	List<Tratta> tratte = dao.getTratte(mezzi, mapCitta);	
	this.grafo = new SimpleWeightedGraph<Citta, DefaultWeightedEdge>(DefaultWeightedEdge.class) ;
	Graphs.addAllVertices(this.grafo, mapCitta.values());	
		
	for (Tratta t : tratte) {
		if (grafo.containsVertex(t.getSorgente()) && grafo.containsVertex(t.getDestinazione())) {
			
		Graphs.addEdge(grafo, t.getSorgente(), t.getDestinazione(), t.getDistanza());
		}
	}	
	
	dijkstra = new DijkstraShortestPath<Citta, DefaultWeightedEdge>(grafo);
	System.out.println(this.grafo.vertexSet());
		return String.format("Grafo creato con %d vertici e %d archi\n",
				this.grafo.vertexSet().size(),
				this.grafo.edgeSet().size()) ;
		
	}
	
	
	
	public void aggiungiMezzo(Mezzo m) {
		mezzi.add(m);
	} 
 	
	
	public Mezzo getMezzoMigliore(Ordine o, List<Mezzo> mezzi, double percentualeDistanza, double percentualeVelocita, double percentualeConsumo) {
		
		Mezzo mezzoMigliore = null;
		Double pesoMigliore = 99999.9;
		
		for (Mezzo m : mezzi) {
			Double peso = getPesoComplessivo(dijkstra.getPathWeight(m.getCitta(), o.getSorgente()), m.getVelocitaMedia(), m.getCostoCarburante()*dijkstra.getPathWeight(m.getCitta(), o.getSorgente()), percentualeDistanza, percentualeVelocita, percentualeConsumo);
			if (peso<=pesoMigliore && m.getSpazioOccupato()<m.getSpazioMax() && m.getPesoOccupato()<m.getPesoMax()) {
				mezzoMigliore = m;
				pesoMigliore = peso;
			}
		}
		
		mappaOrdini.put(o.getId(), o);
		mezzoMigliore.assegnaOrdine(o);
		return mezzoMigliore;
	}
	
	public double getPesoComplessivo(double distanza, double velocita, double consumo, double percentualeDistanza, double percentualeVelocita, double percentualeConsumo) {
		double pesoComplessivo = percentualeDistanza*distanza+percentualeVelocita*velocita+percentualeConsumo*consumo;       //FORMULA MATEMATICA DA STRUTTURARE PER DEFINIRE IL PESO TOTALE DEL CAMMINO PER UN MEZZO CHE TENGA CONTO DI CERTE PERCENTUALI DI INPUT PER VELOCITA, CONSUMO E DISTANZA
				
		return pesoComplessivo;		
	}
	
	public Map<String, Citta> getMappaCitta() {
		return this.mapCitta;
	}
	
	public List<Mezzo> getListaMezzi() {
		return this.mezzi;
	}
	
	

}
