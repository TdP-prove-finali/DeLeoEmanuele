package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.KShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.EppsteinKShortestPath;
import org.jgrapht.alg.shortestpath.YenKShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.SimulatoreTrasportoMerce.DAO.DAO;

public class Model {

	public Graph<Citta, Arco> grafo;
	DAO dao;
	Map<String, Mezzo> mapMezziConSpecifiche;
	List<Citta> listaMetropoli;
	Map<String, Citta> mapCitta;
	Map<Citta, List<Ordine>> mappaOrdiniConPartenza;
	DijkstraShortestPath<Citta, Arco> dijkstra;

	public Model() {
		dao = new DAO();
		listaMetropoli = new ArrayList<Citta>(); // METROPOLI : CITTA' IN CUI RISIEDONO GLI AEROPORTI
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

	public String creaGrafo(double percentuale) {
		int codiciMezzi = 1;
		Arco arco = null;
		this.grafo = new DirectedWeightedMultigraph<Citta, Arco>(Arco.class);
		Graphs.addAllVertices(this.grafo, mapCitta.values());
		for (Tratta t : dao.getTratte(mapMezziConSpecifiche.values(), mapCitta)) {
			if (grafo.containsVertex(t.getSorgente()) && grafo.containsVertex(t.getDestinazione())) { // L'ARCO ESTENDE
																										// IL DEFAULT
																										// WEIGHTED EDGE
																										// E CONSERVA IL
																										// TIPO DI MEZZO

				arco = grafo.addEdge(t.getSorgente(), t.getDestinazione()); // OGNI ARCO E' PESATO IN BASE AL PESO
																			// COMPLESSIVO
				arco.setDistanza(t.getDistanza()); // CHE TIENE TRACCIA DELLA VELOCITA' E DEL COSTO DEL CARBURANTE DEL
													// VEICOLO IN QUESTIONE
				arco.setId(codiciMezzi);
				arco.setTipo(t.getMezzoTrasporto());

				grafo.setEdgeWeight(arco,
						this.getPesoComplessivo(t.getDistanza(),
								mapMezziConSpecifiche.get(arco.getTipo()).getVelocitaMedia(),
								mapMezziConSpecifiche.get(arco.getTipo()).getCostoCarburante(), percentuale));

				codiciMezzi++;
			}

		}
		dijkstra = new DijkstraShortestPath<Citta, Arco>(grafo); // L'ALGORITMO DI DIJKSTRA SI BASERA' SUI SINGOLI PESI

		return String.format("Grafo creato con:\n%d vertici e %d archi\n", this.grafo.vertexSet().size(),
				this.grafo.edgeSet().size());

	}

	public double getPesoComplessivo(double distanza, double velocita, double costoCarburante, double percentuale) {

		double pesoComplessivo = ((distanza / velocita) * (percentuale / 100))
				* (costoCarburante * ((100 - percentuale) / 100)); // PESO
		// UNICO
		// PER
		// OGNI
		// PARAMETRO

		return Math.round(pesoComplessivo * 100.0) / 100.0;
	}

	public Map<String, Citta> getMappaCitta() {
		return this.mapCitta;
	}

	public void generaMezzo(String tipo, double pesoMax, double spazioMax, double velocitaMedia, // METODO PER
																									// SPECIFICARE I
																									// PARAMETRI DI OGNI
																									// MEZZO (UNIVOCI
																									// PER OGNI MEZZO
																									// UNA VOLTA CREATI
																									// ---> L'IDEA E' DI
																									// UTILIZZARE TIR
																									// (nel db
																									// "Autobus") E
																									// AEREI
			double costoCarburante) { // PER TENERE TRACCIA USO UNA MAPPA < "tipo veicolo" , veicolo >
		Citta c = null;
		Mezzo nuovo = new Mezzo(mapMezziConSpecifiche.size(), tipo, pesoMax, spazioMax, velocitaMedia, costoCarburante,
				c, null);
		mapMezziConSpecifiche.remove(tipo);
		mapMezziConSpecifiche.put(tipo, nuovo);
		System.out.println("mezzo aggiunto correttamente");
	}

	public Map<String, Mezzo> getMezziConSpecifiche() {
		return this.mapMezziConSpecifiche;
	}

	public String getOrdini() {
		return dao.getOrdini();
	}

	public DijkstraShortestPath<Citta, Arco> getDijkstra() {
		return this.dijkstra;
	}

	public void clearTableOrdini() {
		dao.clearTableOrdini();
	}

	public void clearTableOrdiniConsegnati() {
		dao.clearTableOrdiniConsegnati();
	}

	public List<Citta> getMetropoli() {
		return this.listaMetropoli;
	}

	public void tracciaOrdine(int id) {

		Ordine ordineTracciato = dao.getOrdineById(id, mapCitta);

		KShortestPathAlgorithm<Citta, Arco> pathInspector = new YenKShortestPath<Citta, Arco>(grafo); //EppsteinKShortestPath<Citta, Arco>(grafo);

		List<GraphPath<Citta, Arco>> paths = pathInspector.getPaths(ordineTracciato.getSorgente(),
				ordineTracciato.getDestinazione(), 2);

		List<Citta> bestPath = paths.get(0).getVertexList();
		List<Citta> bestSecondPath = paths.get(1).getVertexList();

		List<Arco> edgeBestPath = paths.get(0).getEdgeList();
		List<Arco> edgeBestSecondPath = paths.get(1).getEdgeList();
		
		
		System.out.println("Best path" + bestPath);
		
		double costo = edgeBestPath.stream().mapToDouble(a -> {
			return getPesoComplessivo(a.getDistanza(), mapMezziConSpecifiche.get(a.getTipo()).getVelocitaMedia(), mapMezziConSpecifiche.get(a.getTipo()).getCostoCarburante(), 50);
		}).reduce(0, (a,b) -> a+b);
		
		System.out.println("Costo best path" + costo); 
		
		System.out.println("Second Best path" + bestSecondPath);

		double costoSecond = edgeBestSecondPath.stream().mapToDouble(a -> {
			return getPesoComplessivo(a.getDistanza(), mapMezziConSpecifiche.get(a.getTipo()).getVelocitaMedia(), mapMezziConSpecifiche.get(a.getTipo()).getCostoCarburante(), 50);
		}).reduce(0, (a,b) -> a+b);
		
		System.out.println("Costo second path" + costoSecond);

//		double costoViaggio = 0.0;
//		String output = dao.getOrdineById(id, mapCitta) + "\n";
//		output += dao.tracciaOrdine(id);
//		Ordine ordineTracciato = dao.getOrdineById(id, mapCitta);
//		LocalDateTime dataNuovoOrdine = ordineTracciato.getDataOra();
//
//		for (Arco arco : dijkstra.getPath(ordineTracciato.getSorgente(), ordineTracciato.getDestinazione())
//				.getEdgeList()) {
//			costoViaggio += arco.getDistanza() * mapMezziConSpecifiche.get(arco.getTipo()).getCostoCarburante();
//			ordineTracciato.setDataOra(ordineTracciato.getDataOra().plusSeconds(Math.round(
//					(arco.getDistanza() / mapMezziConSpecifiche.get(arco.getTipo()).getVelocitaMedia()) * 3600)));
//		}
//
//		output += "\nCOSTO=" + costoViaggio + "  DURATA: "
//				+ Duration.between(dataNuovoOrdine, ordineTracciato.getDataOra()).getSeconds() / 60 + " minuti";
//		if (dijkstra.getPath(ordineTracciato.getSorgente(), ordineTracciato.getDestinazione()).getEdgeList()
//				.size() > 1) {
//
//			if (!grafo.getAllEdges(ordineTracciato.getSorgente(), ordineTracciato.getDestinazione()).isEmpty()) {
//				int i = 1;
//				for (Arco tratta : grafo.getAllEdges(ordineTracciato.getSorgente(),
//						ordineTracciato.getDestinazione())) {
//					output += "\n\n PERCORSO ALTERNATIVO " + i + ":\n" + (Citta) tratta.getDestinazione() + " mezzo: "
//							+ tratta.getTipo() + " COSTO="
//							+ tratta.getDistanza() * mapMezziConSpecifiche.get(tratta.getTipo()).getCostoCarburante()
//							+ " DURATA: "
//							+ Duration.between(dataNuovoOrdine, ordineTracciato.getDataOra()).getSeconds() / 60
//							+ " minuti";
//					i++;
//				}
//			}
//		}
//		return output;
	}
	
	
}
