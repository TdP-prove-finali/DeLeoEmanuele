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
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import it.polito.tdp.SimulatoreTrasportoMerce.DAO.DAO;

public class Model {
	
	public Graph<Citta, Arco> grafo;
	DAO dao;
	Map<String,Mezzo> mapMezziConSpecifiche;
	List<Mezzo> mezzi;
	List<Tratta> tratte;
	Map<String,Citta> mapCitta;
	DijkstraShortestPath<Citta, Arco> dijkstra;
	
	public Model() {
	dao = new DAO();
	mezzi = new LinkedList<Mezzo>();
	mapMezziConSpecifiche = new TreeMap<String, Mezzo>();
	this.mapCitta = new TreeMap<String, Citta>();
	dao.getCitta(mapCitta);
	}

	public String creaGrafo() {
	Arco arco = null;
	this.tratte=dao.getTratte(mezzi, mapCitta);	
	this.grafo = new DirectedWeightedMultigraph<Citta, Arco>(Arco.class) ;
	Graphs.addAllVertices(this.grafo, mapCitta.values());	
		
	for (Tratta t : tratte) {
		if (grafo.containsVertex(t.getSorgente()) && grafo.containsVertex(t.getDestinazione())) {
		arco = grafo.addEdge(t.getSorgente(), t.getDestinazione());
		arco.setDistanza(t.getDistanza());
		arco.setTipo(t.getMezzoTrasporto());
		grafo.setEdgeWeight(arco, this.getPesoComplessivo(t.getDistanza(), mapMezziConSpecifiche.get(arco.getTipo()).getVelocitaMedia(), mapMezziConSpecifiche.get(arco.getTipo()).getCostoCarburante(), t.getEmissioni()));
 		}
	}	
	dijkstra = new DijkstraShortestPath<Citta, Arco>(grafo);
	System.out.println(dijkstra.getPath(mapCitta.get("Torino"), mapCitta.get("Roma")));
	System.out.println(dijkstra.getPath(mapCitta.get("Torino"), mapCitta.get("Roma")).getVertexList());
	System.out.println(dijkstra.getPathWeight(mapCitta.get("Torino"), mapCitta.get("Roma")));
		return String.format("Grafo creato con %d vertici e %d archi\n",
				this.grafo.vertexSet().size(),
				this.grafo.edgeSet().size()) ;
		
	}
	
	// 1) getShortestPath(Citta 1 , Citta 2) ---> dijkstra = new DijkstraShortestPath<Citta, Arco>(grafo);
	
	public void aggiungiMezzo(Mezzo m) {
		m.setId(mezzi.size()+1);
		mezzi.add(m);
	} 
	
	public double getPesoComplessivo(double distanza, double velocita, double costoCarburante, int emissioni) { //SE NON SI SCELGONO EMISSIONI, EMISS = 1
		double pesoComplessivo = distanza*velocita*costoCarburante*emissioni;       //FORMULA MATEMATICA DA STRUTTURARE PER DEFINIRE IL PESO TOTALE DEL CAMMINO PER UN MEZZO CHE TENGA CONTO DI CERTE PERCENTUALI DI INPUT PER VELOCITA, CONSUMO E DISTANZA
				
		return Math.round(pesoComplessivo*100.0)/100.0;		
	}
	
	public Map<String, Citta> getMappaCitta() {
		return this.mapCitta;
	}
	
	public void generaMezzo(String tipo, double pesoMax, double spazioMax, double velocitaMedia, double costoCarburante) {
		Citta c = null;
		Mezzo nuovo = new Mezzo(mapMezziConSpecifiche.size(), tipo, pesoMax, spazioMax, velocitaMedia, costoCarburante, c);
		mapMezziConSpecifiche.put(tipo, nuovo);
		System.out.println("mezzo aggiunto correttamente");
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
		double maxVolume = 0.0;                                                 // IMPLEMENTARE LA PROBABILITA' PER FAR SI' CHE VENGANO PRINCIPALMENTE SCELTE GRANDI METROPOLI
		                                                                        // ES. RENDERE PIU' PROBABILI CITTA' COME ROMA, MILANO, TORINO, GENOVA, FIRENZE, PALERMO 
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
