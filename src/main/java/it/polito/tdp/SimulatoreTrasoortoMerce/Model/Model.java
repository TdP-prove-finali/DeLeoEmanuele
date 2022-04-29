package it.polito.tdp.SimulatoreTrasoortoMerce.Model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.SimulatoreTrasportoMerce.DAO.DAO;

public class Model {
	
	private Graph<Citta, Arco> grafo;
	DAO dao;
	List<Mezzo> mezzi;
	List<Tratta> tratte;
	Map<String,Citta> mapCitta;
	DijkstraShortestPath<Citta, Arco> dijkstra;
	
	public Model() {
	dao = new DAO();
	mezzi = new LinkedList<Mezzo>();
	this.mapCitta = new TreeMap<String, Citta>();
	dao.getCitta(mapCitta);
	}

	public String creaGrafo() {
	Arco a = null;
	this.tratte=dao.getTratte(mezzi, mapCitta);	
	this.grafo = new SimpleWeightedGraph<Citta, Arco>(Arco.class) ;
	Graphs.addAllVertices(this.grafo, mapCitta.values());	
		
	for (Tratta t : tratte) {
		if (grafo.containsVertex(t.getSorgente()) && grafo.containsVertex(t.getDestinazione())) {
		a = new Arco(t.getSorgente(),t.getDestinazione(),t.getDistanza(), t.getMezzoTrasporto()); 
	    grafo.addEdge(t.getSorgente(), t.getDestinazione(), a);
		grafo.setEdgeWeight(a, t.getDistanza());
 		}
		
	}	
	
	dijkstra = new DijkstraShortestPath<Citta, Arco>(grafo);
		return String.format("Grafo creato con %d vertici e %d archi\n",
				this.grafo.vertexSet().size(),
				this.grafo.edgeSet().size()) ;
		
	}
	
	public void aggiungiMezzo(Mezzo m) {
		m.setId(mezzi.size()+1);
		mezzi.add(m);
	} 
	
	public Mezzo getMezzoMigliore(Ordine o, List<Mezzo> mezzi, double percentualeDistanza, double percentualeVelocita, double percentualeConsumo) {
		
		Mezzo mezzoMigliore = null;
		Double pesoMigliore = 99999.9;
		
		for (Mezzo m : mezzi) {
			if (grafo.containsVertex(m.getCitta())) {
			Double peso = getPesoComplessivo(dijkstra.getPathWeight(m.getCitta(), o.getSorgente()), m.getVelocitaMedia(), m.getCostoCarburante()*dijkstra.getPathWeight(m.getCitta(), o.getSorgente()), percentualeDistanza, percentualeVelocita, percentualeConsumo);
			if (peso<=pesoMigliore && m.getSpazioOccupato()<m.getSpazioMax() && m.getPesoOccupato()<m.getPesoMax()) {
				mezzoMigliore = m;
				pesoMigliore = peso;
			}
			}
		}
		
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
	
	public void simulaOrdine(int nOrdini, LocalDateTime dataOrdine) {
		List<Citta> listaCitta = new ArrayList<Citta>(mapCitta.values());
		Random rand = new Random();
		double minPeso = 0.5;
		double maxPeso = 0.0;
		double minVolume = 0.5;
		double maxVolume = 0.0;
		for (Mezzo m : mezzi) {
			if (m.getPesoMax()>=maxPeso) {
				maxPeso = m.getPesoMax();
			}
			if (m.getSpazioMax()>=maxVolume) {
				maxVolume = m.getSpazioMax();
			}
		}
		
		for (int i=1; i<=nOrdini; i++) {
		Citta sorgente = listaCitta.get(rand.nextInt(listaCitta.size()));
		listaCitta.remove(sorgente);
		Citta destinazione = listaCitta.get(rand.nextInt(listaCitta.size()));
		listaCitta.add(sorgente);
		double peso= rand.nextDouble() * (maxPeso - minPeso) + minPeso;
		double pesoApprossimato = Math.round(peso*100.0)/100.0;
		double volume= rand.nextDouble() * (maxVolume - minVolume) + minVolume;
		double volumeApprossimato = Math.round(volume*100.0)/100.0;
		
		Ordine o = new Ordine(i,sorgente,destinazione,pesoApprossimato,volumeApprossimato,dataOrdine);
		dao.addOrdine(o);
		
		}
		
	
	
	}
	

}
