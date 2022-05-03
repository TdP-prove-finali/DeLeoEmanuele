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
import it.polito.tdp.SimulatoreTrasportoMerce.DAO.DAO;

public class Model {
	
	public Graph<Citta, Arco> grafo;
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
	this.grafo = new DirectedMultigraph<Citta, Arco>(Arco.class) ;
	Graphs.addAllVertices(this.grafo, mapCitta.values());	
		
	for (Tratta t : tratte) {
		if (grafo.containsVertex(t.getSorgente()) && grafo.containsVertex(t.getDestinazione())) {
		a = new Arco(t.getSorgente(),t.getDestinazione(),t.getDistanza(), t.getMezzoTrasporto()); 
	    grafo.addEdge(t.getSorgente(), t.getDestinazione(), a);
 		}
		
	}	
	
	dijkstra = new DijkstraShortestPath<Citta, Arco>(grafo);
	System.out.println(dijkstra.getPath(mapCitta.get("Torino"), mapCitta.get("Roma")));
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
		List<Arco> percorsoMezzo = new LinkedList<Arco>();
		for (Mezzo m : mezzi) {
			if (grafo.containsVertex(m.getCitta())) {
				
			for (Arco a : dijkstra.getPath(m.getCitta(), o.getSorgente()).getEdgeList()) {                       // CONTROLLO ARCHI NEL CAMMINO OUTPUT DELL'ALGORITMO.
				Arco aTemp = new Arco(a.getSorgente(),a.getDestinazione(),a.getDistanza(),m.getTipo());          // CREO UN ARCO DEL TIPO DEL MEZZO CON LE ALTRE CARATTERISTICHE
				if (grafo.containsEdge(aTemp) ) {                                                                // UGUALI ALL'ARCO NEL CAMMINO
					percorsoMezzo.add(aTemp);                                                                    // SE ESISTE UN ARCO CHE VA DA LI' A LI' DEL TIPO DEL MEZZO
					                                                                                             // LO TENGO
				} else {
					for (Arco arcoAlternativo : grafo.edgeSet()) {                                                    // SENNO' CONTROLLO TUTTI GLI ARCHI CHE CONOSCO E VEDO SE TROVO
					                                                                                                  // L'ARCO CHE MI SERVE
						if (arcoAlternativo.getSorgente().equals(a.getSorgente()) && arcoAlternativo.getDestinazione().equals(a.getDestinazione()) && arcoAlternativo.getTipo().equals(m.getTipo()) && !arcoAlternativo.getSorgente().equals(a.getDestinazione()) && !arcoAlternativo.getDestinazione().equals(a.getSorgente())) {
				percorsoMezzo.add(arcoAlternativo);  
				                                                                                                  // FILTRO SU SORGENTE , DESTINAZIONE E SUL TIPO DELL'ARCO (MEZZO DI TRASPORTO IN QUESTIONE NELLO STEP DEL CICLO)
						}
					} 	
				}	
			}
			
			if (percorsoMezzo.size() != dijkstra.getPath(m.getCitta(), o.getSorgente()).getEdgeList().size()) {  //  SE IL FLAG NON E' UGUALE ALLA LUNGHEZZA DEL PATH
				System.out.println("Citta non raggiungibile da quel mezzo");                                     // --> NON MI HA TROVATO ALMENO UN ARCO   ---> Gestire il caso in cui 
			}                                                                                                    // posso usare un altro mezzo 
			
			Double distanza = this.getDistanzaPercorso(percorsoMezzo);
			Double peso = getPesoComplessivo(distanza, m.getVelocitaMedia(), m.getCostoCarburante()*distanza, percentualeDistanza, percentualeVelocita, percentualeConsumo);
			if (peso<=pesoMigliore && m.getSpazioOccupato()<m.getSpazioMax() && m.getPesoOccupato()<m.getPesoMax()) {
				mezzoMigliore = m;
				pesoMigliore = peso;
			} else {
				percorsoMezzo.removeAll(percorsoMezzo);
			}
			}
		//mezzoMigliore.assegnaOrdine(o);
		}
		System.out.println(percorsoMezzo+""+pesoMigliore);
		return mezzoMigliore;
	}
	
	public double getPesoComplessivo(double distanza, double velocita, double consumo, double percentualeDistanza, double percentualeVelocita, double percentualeConsumo) {
		double pesoComplessivo = percentualeDistanza*distanza*percentualeVelocita*velocita*percentualeConsumo*consumo;       //FORMULA MATEMATICA DA STRUTTURARE PER DEFINIRE IL PESO TOTALE DEL CAMMINO PER UN MEZZO CHE TENGA CONTO DI CERTE PERCENTUALI DI INPUT PER VELOCITA, CONSUMO E DISTANZA
				
		return Math.round(pesoComplessivo*100.0)/100.0;		
	}
	
	public double getDistanzaPercorso(List<Arco> archi) {
		double peso = 0.0;
		for (Arco a : archi) {
			peso += a.getDistanza();
		}
		return Math.round(peso*100.0)/100.0;
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
