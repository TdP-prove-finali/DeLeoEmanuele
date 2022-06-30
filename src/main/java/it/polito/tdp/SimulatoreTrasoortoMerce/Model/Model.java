package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.time.Duration;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.KShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.YenKShortestPath;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import it.polito.tdp.SimulatoreTrasportoMerce.DAO.DAO;
import javafx.collections.ObservableList;

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
			
			if (grafo.containsVertex(t.getSorgente()) && grafo.containsVertex(t.getDestinazione())) { 
				
				arco = grafo.addEdge(t.getSorgente(), t.getDestinazione());
				arco.setDistanza(t.getDistanza()); 
				arco.setId(codiciMezzi);
				arco.setTipo(t.getMezzoTrasporto());

				grafo.setEdgeWeight(arco,
						this.getPesoComplessivo(t.getDistanza(),
								mapMezziConSpecifiche.get(arco.getTipo()).getVelocitaMedia(),
								mapMezziConSpecifiche.get(arco.getTipo()).getCostoCarburante(), percentuale));

				codiciMezzi++;
			}

		}
		dijkstra = new DijkstraShortestPath<Citta, Arco>(grafo); 

		return String.format(" GRAFO CREATO\n\n - %d vertici\n - %d archi\n", this.grafo.vertexSet().size(),
				this.grafo.edgeSet().size());

	}

	public double getPesoComplessivo(double distanza, double velocita, double costoCarburante, double percentuale) {

		double pesoComplessivo = ((distanza / velocita) * (percentuale / 100))
				* (costoCarburante * ((100 - percentuale) / 100)); // PESO
		
		return Math.round(pesoComplessivo * 100.0) / 100.0;
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

	public ObservableList<Ordine> getOrdini() {
		return dao.getOrdini(this.mapCitta);
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
	
	public Map<String, Citta> getMappaCitta() {
		return this.mapCitta;
	}


	public String tracciaOrdine(int id) {

		String output = "";

		Ordine ordineTracciato = dao.getOrdineById(id, mapCitta);
		output += ordineTracciato + "\n\n";
		output += "STORICO\n";
		output += dao.tracciaOrdine(id) + "\n\n";

		KShortestPathAlgorithm<Citta, Arco> pathInspector = new YenKShortestPath<Citta, Arco>(grafo);
		List<GraphPath<Citta, Arco>> paths = pathInspector.getPaths(ordineTracciato.getSorgente(),
				ordineTracciato.getDestinazione(), 2);

		List<Arco> edgeBestPath = paths.get(0).getEdgeList();
		List<Arco> edgeBestSecondPath = paths.get(1).getEdgeList();

		output += "PERCORSO MIGLIORE: \n";
		// System.out.println("Best path" + edgeBestPath);

		double costo1 = 0.0;
		double costoEuro1 = 0.0;
		LocalDateTime dataArrivo = ordineTracciato.getDataOra();

		for (Arco arco : edgeBestPath) {
			output += "" + (Citta) arco.getSorgente() + " - " + (Citta) arco.getDestinazione() + "  mezzo="
					+ arco.getTipo() + " id=" + arco.getId() + "\n";
			costo1 += getPesoComplessivo(arco.getDistanza(),
					mapMezziConSpecifiche.get(arco.getTipo()).getVelocitaMedia(),
					mapMezziConSpecifiche.get(arco.getTipo()).getCostoCarburante(), 50);
			costoEuro1 += arco.getDistanza() * mapMezziConSpecifiche.get(arco.getTipo()).getCostoCarburante();
			ordineTracciato.setDataOra(ordineTracciato.getDataOra().plusSeconds(Math.round(
					(arco.getDistanza() / mapMezziConSpecifiche.get(arco.getTipo()).getVelocitaMedia()) * 3600)));

		}
		
		output += "\nPESO: " + Math.round(costo1 * 100.0) / 100.0 + "     COSTO: "
				+ Math.round(costoEuro1 * 100.00) / 100.00 + " €     DURATA: "
				+ Duration.between(dataArrivo, ordineTracciato.getDataOra()).toMinutes() + " minuti\n\n";
		

		output += "PERCORSO ALTERNATIVO: \n";
		double costo2 = 0.0;
		double costoEuro2 = 0.0;

		Ordine ordineTracciato2 = dao.getOrdineById(id, mapCitta);

		for (Arco arco : edgeBestSecondPath) {

			output += "" + (Citta) arco.getSorgente() + " - " + (Citta) arco.getDestinazione() + "  mezzo="
					+ arco.getTipo() + " id=" + arco.getId() + "\n";
			costo2 += getPesoComplessivo(arco.getDistanza(),
					mapMezziConSpecifiche.get(arco.getTipo()).getVelocitaMedia(),
					mapMezziConSpecifiche.get(arco.getTipo()).getCostoCarburante(), 50);
			costoEuro2 += arco.getDistanza() * mapMezziConSpecifiche.get(arco.getTipo()).getCostoCarburante();
			ordineTracciato2.setDataOra(ordineTracciato2.getDataOra().plusSeconds(Math.round(
					(arco.getDistanza() / mapMezziConSpecifiche.get(arco.getTipo()).getVelocitaMedia()) * 3600)));

		}

		output += "\nPESO: " + Math.round(costo2 * 100.0) / 100.0 + "     COSTO: "
				+ Math.round(costoEuro2 * 100.00) / 100.00 + " €     DURATA: "
				+ Duration.between(dataArrivo, ordineTracciato2.getDataOra()).toMinutes() + " minuti";

		output.replace("Autobus", "Tir");
		return output;

		// System.out.println("costo2=" + costo2);

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
