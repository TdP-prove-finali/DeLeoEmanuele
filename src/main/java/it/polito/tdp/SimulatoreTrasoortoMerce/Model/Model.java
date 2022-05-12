package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.time.LocalDateTime;

import java.util.ArrayList;
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
	List<Tratta> tratte;
	Map<String, Citta> mapCitta;
	Map<Citta, List<Ordine>> mappaOrdiniConPartenza;
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
		this.tratte = dao.getTratte(mezzi, mapCitta);
		this.grafo = new DirectedWeightedMultigraph<Citta, Arco>(Arco.class);
		Graphs.addAllVertices(this.grafo, mapCitta.values());

		for (Tratta t : tratte) {
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
	//	System.out.println(dijkstra.getPath(mapCitta.get("Torino"), mapCitta.get("Roma")));
	//	System.out.println(dijkstra.getPath(mapCitta.get("Torino"), mapCitta.get("Roma")).getVertexList());
	//	System.out.println(dijkstra.getPathWeight(mapCitta.get("Torino"), mapCitta.get("Roma")));
		return String.format("Grafo creato con %d vertici e %d archi\n", this.grafo.vertexSet().size(),
				this.grafo.edgeSet().size());

	}

	// 1) getShortestPath(Citta 1 , Citta 2) ---> dijkstra = new
	// DijkstraShortestPath<Citta, Arco>(grafo);

	public void aggiungiMezzo(Mezzo m) {
		m.setId(mezzi.size() + 1);
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
		mapMezziConSpecifiche.put(tipo, nuovo);
		System.out.println("mezzo aggiunto correttamente");
	}

	public List<Mezzo> getListaMezzi() {
		return this.mezzi;
	}

	public void simulaOrdini(int nOrdini, int nGiorni, LocalDateTime start, int oreGiornaliere) {
		List<Citta> listaCittaTotali = new ArrayList<Citta>(mapCitta.values());
		List<Citta> listaMetropoli = new ArrayList<Citta>();
		List<Ordine> listaOrdiniSimulati = new ArrayList<Ordine>();
		listaMetropoli.add(mapCitta.get("Roma"));
		listaMetropoli.add(mapCitta.get("Milano"));
		listaMetropoli.add(mapCitta.get("Genova"));
		listaMetropoli.add(mapCitta.get("Torino"));
		listaMetropoli.add(mapCitta.get("Firenze"));
		listaMetropoli.add(mapCitta.get("Bologna"));
		listaMetropoli.add(mapCitta.get("Napoli"));
		Citta sorgente = null;
		Citta destinazione = null;
		Ordine newOrdine = null;
		Random rand = new Random();
		double minPeso = 0.5;
		double maxPeso = 0.0;
		double minVolume = 0.5;
		double maxVolume = 0.0; // IMPLEMENTARE LA PROBABILITA' PER FAR SI' CHE VENGANO PRINCIPALMENTE SCELTE
								// GRANDI METROPOLI
								// ES. RENDERE PIU' PROBABILI CITTA' COME ROMA, MILANO, TORINO, GENOVA, FIRENZE,
								// PALERMO
		for (Mezzo m : mezzi) {
			if (m.getPesoMax() >= maxPeso) {
				maxPeso = m.getPesoMax() / 2;
			}
			if (m.getSpazioMax() >= maxVolume) {
				maxVolume = m.getSpazioMax() / 2;
			}
		}

		for (int giorno=1; giorno<=nGiorni; giorno++) {
		for (int ordine = 1; ordine <= nOrdini; ordine++) {
			int minutes = rand.nextInt(60*oreGiornaliere*giorno);
			LocalDateTime arrivo = start.plusMinutes(minutes);
			boolean isCittaMinore1 = rand.nextInt(5) == 0;
			boolean isCittaMinore2 = rand.nextInt(5) == 0;
			double peso = rand.nextDouble() * (maxPeso - minPeso) + minPeso;
			double pesoApprossimato = Math.round(peso * 100.0) / 100.0;
			double volume = rand.nextDouble() * (maxVolume - minVolume) + minVolume;
			double volumeApprossimato = Math.round(volume * 100.0) / 100.0;

			if (isCittaMinore1 == false && isCittaMinore2 == false) {
				sorgente = listaMetropoli.get(rand.nextInt(listaMetropoli.size()));
				listaMetropoli.remove(sorgente);
				destinazione = listaMetropoli.get(rand.nextInt(listaMetropoli.size()));
				listaMetropoli.add(sorgente);

			}

			if (isCittaMinore1 == true && isCittaMinore2 == true) {
				sorgente = listaCittaTotali.get(rand.nextInt(listaCittaTotali.size()));
				listaCittaTotali.remove(sorgente);
				destinazione = listaCittaTotali.get(rand.nextInt(listaCittaTotali.size()));
				listaCittaTotali.add(sorgente);
			}

			if (isCittaMinore1 == true && isCittaMinore2 == false) {
				sorgente = listaCittaTotali.get(rand.nextInt(listaCittaTotali.size()));
				destinazione = listaMetropoli.get(rand.nextInt(listaMetropoli.size()));
			}

			if (isCittaMinore1 == false && isCittaMinore2 == true) {
				sorgente = listaMetropoli.get(rand.nextInt(listaMetropoli.size()));
				destinazione = listaCittaTotali.get(rand.nextInt(listaCittaTotali.size()));
			}
			newOrdine = new Ordine(ordine, sorgente, destinazione, pesoApprossimato, volumeApprossimato, arrivo);
			listaOrdiniSimulati.add(newOrdine);

		}

	}
		Collections.sort(listaOrdiniSimulati);
		int i = 1;
		for (Ordine ord : listaOrdiniSimulati) {
			ord.setId(i);
			dao.addOrdine(ord);
			i++;
		}
		
	}

	public void caricaOrdini() {
		mappaOrdiniConPartenza = new LinkedHashMap<Citta, List<Ordine>>();
		dao.getOrdini(mappaOrdiniConPartenza, mapCitta);

	}

	public Map<Citta, List<Ordine>> getMappaOrdini() {
		return this.mappaOrdiniConPartenza;
	}
}
