package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import it.polito.tdp.SimulatoreTrasportoMerce.DAO.DAO;

public class Model {

	public Graph<Citta, Arco> grafo;
	DAO dao;
	Map<String, Mezzo> mapMezziConSpecifiche;
	List<Mezzo> mezzi;
	List<Citta> listaMetropoli;
	Map<String, Citta> mapCitta;
	Map<Citta, List<Ordine>> mappaOrdiniConPartenza;
	DijkstraShortestPath<Citta, Arco> dijkstra;

	public Model() {
		dao = new DAO();
		mezzi = new ArrayList<Mezzo>();
		listaMetropoli = new ArrayList<Citta>();
		mapMezziConSpecifiche = new TreeMap<String, Mezzo>();
		this.mapCitta = new TreeMap<String, Citta>();
		dao.getCitta(mapCitta);
		listaMetropoli.add(mapCitta.get("Roma"));
		listaMetropoli.add(mapCitta.get("Milano"));
		listaMetropoli.add(mapCitta.get("Genova"));
		listaMetropoli.add(mapCitta.get("Torino"));
		listaMetropoli.add(mapCitta.get("Firenze"));
		listaMetropoli.add(mapCitta.get("Bologna"));
		listaMetropoli.add(mapCitta.get("Napoli"));
		listaMetropoli.add(mapCitta.get("Palermo"));

	}

	public String creaGrafo() {
		Arco arco = null;
		this.grafo = new DirectedWeightedMultigraph<Citta, Arco>(Arco.class);
		Graphs.addAllVertices(this.grafo, mapCitta.values());
		for (Tratta t : dao.getTratte(mapMezziConSpecifiche.values(), mapCitta)) {
			if (grafo.containsVertex(t.getSorgente()) && grafo.containsVertex(t.getDestinazione())) {

				arco = grafo.addEdge(t.getSorgente(), t.getDestinazione());
				arco.setDistanza(t.getDistanza());
				arco.setTipo(t.getMezzoTrasporto());
				grafo.setEdgeWeight(arco,
						this.getPesoComplessivo(t.getDistanza(),
								mapMezziConSpecifiche.get(arco.getTipo()).getVelocitaMedia(),
								mapMezziConSpecifiche.get(arco.getTipo()).getCostoCarburante()));

			}

		}
		dijkstra = new DijkstraShortestPath<Citta, Arco>(grafo);
		
		 System.out.println(dijkstra.getPath(mapCitta.get("Avellino"),
		 mapCitta.get("Pistoia")));
		// System.out.println(dijkstra.getPath(mapCitta.get("Torino"),
		// mapCitta.get("Roma")).getVertexList());
		// System.out.println(dijkstra.getPathWeight(mapCitta.get("Torino"),
		// mapCitta.get("Roma")));
		return String.format("Grafo creato con %d vertici e %d archi\n", this.grafo.vertexSet().size(),
				this.grafo.edgeSet().size());

	}

	public void aggiungiMezzo(Mezzo m) {
		// m.setId(mezzi.size() + 1);
		mezzi.add(m);
	}

	public double getPesoComplessivo(double distanza, double velocita, double costoCarburante) { // SE
																									// NON
																									// SI
																									// SCELGONO
																									// EMISSIONI,
																									// EMISS
																									// = 1
		double pesoComplessivo = distanza * velocita * costoCarburante; // FORMULA MATEMATICA DA STRUTTURARE
																		// PER DEFINIRE IL PESO TOTALE DEL
																		// CAMMINO PER UN MEZZO CHE TENGA
																		// CONTO DI CERTE PERCENTUALI DI
																		// INPUT PER VELOCITA, CONSUMO E
																		// DISTANZA

		return Math.round(pesoComplessivo * 100.0) / 100.0;
	}

	public Map<String, Citta> getMappaCitta() {
		return this.mapCitta;
	}

	public void generaMezzo(String tipo, double pesoMax, double spazioMax, double velocitaMedia,
			double costoCarburante) {
		Citta c = null;
		Mezzo nuovo = new Mezzo(mapMezziConSpecifiche.size(), tipo, pesoMax, spazioMax, velocitaMedia, costoCarburante,
				c);
		mapMezziConSpecifiche.remove(tipo);
		mapMezziConSpecifiche.put(tipo, nuovo);
		System.out.println("mezzo aggiunto correttamente");
	}

	public List<Mezzo> getListaMezzi() {
		return this.mezzi;
	}

	public Map<String, Mezzo> getMezziConSpecifiche() {
		return this.mapMezziConSpecifiche;
	}

	public List<Citta> getMetropoli() {
		return this.listaMetropoli;
	}

	public void caricaOrdini() {
		mappaOrdiniConPartenza = new LinkedHashMap<Citta, List<Ordine>>();
		dao.getOrdini(mappaOrdiniConPartenza, mapCitta);

	}
	
	public DijkstraShortestPath<Citta, Arco> getDijkstra() {
		return this.dijkstra;
	}

	public Map<Citta, List<Ordine>> getMappaOrdini() {
		return this.mappaOrdiniConPartenza;
	}
}
