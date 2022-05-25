package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeMap;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Event.EventType;
import it.polito.tdp.SimulatoreTrasportoMerce.DAO.DAO;

public class Simulator {

	public DAO dao;
	public List<Citta> listaMetropoli;
	Graph<Citta, Arco> grafo;
	DijkstraShortestPath<Citta, Arco> dijkstra;
	Map<String, Mezzo> mapMezziConSpecifiche;
	Map<Citta, List<Mezzo>> mappaMezzi;
	Map<Citta, PriorityQueue<Ordine>> ordiniVoli;;
	List<Citta> shortestPath;
	int i;
	// Eventi

	private PriorityQueue<Event> queue;

	// Parametri

	private int nOrdiniGiornalieri;
	private int nGiorni;
	private LocalDate dataInizio;
	private LocalTime oraInizio; // ORE DI TRASPORTO GIORNALIERE
	private LocalTime oraFine; // = LocalTime.of(8, 00); INPUT
	private Duration timeout; // = Duration.ofMinutes(x)

	// Stato del mondo

	private LocalDateTime tempo;
	private int nOrdiniInAttesa;
	private Map<Citta, PriorityQueue<Ordine>> mappaOrdiniInAttesa;

	// Misure in uscita

	private int nOrdiniCompletati;
	private int nTir;
	private int nAerei;

	// Impostazione parametri iniziali

	public void init(DijkstraShortestPath<Citta, Arco> dijkstra, Graph<Citta, Arco> grafo,
			Map<String, Mezzo> mezziConSpecifiche, Collection<Citta> citta, List<Citta> listaMetropoli) {
		dao = new DAO();
		i = 0;
		this.grafo = grafo;
		this.dijkstra = dijkstra;
		this.listaMetropoli = listaMetropoli;
		this.mapMezziConSpecifiche = mezziConSpecifiche;
		List<Citta> listaCittaTotali = new ArrayList<Citta>(citta);
		this.mappaOrdiniInAttesa = new TreeMap<Citta, PriorityQueue<Ordine>>();
		mappaMezzi = new TreeMap<Citta, List<Mezzo>>();
		ordiniVoli = new TreeMap<Citta, PriorityQueue<Ordine>>();
		this.queue = new PriorityQueue<Event>(); // Inizializzo coda eventi

		this.nOrdiniInAttesa = 0; // Inizializzo modello del mondo
		this.nOrdiniCompletati = 0;
		this.nTir = 0;

		int contatoreId = 1;
		Citta sorgente = null;
		Citta destinazione = null;
		Ordine newOrdine = null;
		Random rand = new Random();
		double minPeso = 0.5;
		double maxPeso = 0.0;
		double minVolume = 0.5;
		double maxVolume = 0.0;
		long oreGiornaliere = ChronoUnit.HOURS.between(oraInizio, oraFine);
		LocalDate data = this.dataInizio;

		for (Mezzo m : mezziConSpecifiche.values()) {
			if (m.getPesoMax() >= maxPeso) {
				maxPeso = m.getPesoMax() / 2;
			}
			if (m.getSpazioMax() >= maxVolume) {
				maxVolume = m.getSpazioMax() / 2;
			}
		}

		while (data.isBefore(dataInizio.plusDays(nGiorni))) {
			int contatoreOrdini = 1;
			LocalTime ora = oraInizio;
			while (contatoreOrdini <= this.nOrdiniGiornalieri) {

				int minutes = rand.nextInt(60 * ((int) oreGiornaliere));
				ora = ora.plusMinutes(minutes);
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

				if (ora.isAfter(oraFine)) {
					ora = oraInizio.plusMinutes(minutes);
				}

				if (ora.isBefore(oraFine) && ora.isAfter(oraInizio)) {
					newOrdine = new Ordine(contatoreId, sorgente, destinazione, pesoApprossimato, volumeApprossimato,
							LocalDateTime.of(data, ora));
					this.queue.add(new Event(newOrdine, EventType.NUOVO_ORDINE, LocalDateTime.of(data, ora)));
					dao.addOrdine(newOrdine);
					contatoreOrdini++;
					contatoreId++;
				}
			}
			data = data.plusDays(1);

		}
	}

	// ESECUZIONE

	public void run() {

		while (!this.queue.isEmpty()) {
			Event nuovoEvento = this.queue.poll();
			processEvent(nuovoEvento);

		}
	}

	private void processEvent(Event e) {

		Ordine nuovoOrdine = e.getOrdine();
		Mezzo mezzoTemporaneo = null;

		switch (e.getTipo()) {

		case NUOVO_ORDINE:

			this.queue.add(new Event(nuovoOrdine, EventType.TIMEOUT, nuovoOrdine.getDataOra().plus(timeout)));
			Citta metropoliVicina = this.cercaMetropoliPiùVicina(nuovoOrdine.getSorgente());

			if (!mappaOrdiniInAttesa.containsKey(metropoliVicina)) {
				mappaOrdiniInAttesa.put(metropoliVicina, new PriorityQueue<Ordine>());
			}
			mappaOrdiniInAttesa.get(metropoliVicina).add(nuovoOrdine);

			for (Citta metropoli : mappaOrdiniInAttesa.keySet()) {

				i = 0;

				while (!mappaOrdiniInAttesa.get(metropoli).isEmpty()) {
					Ordine ordineDaGestire = mappaOrdiniInAttesa.get(metropoli).poll();
					ordineDaGestire.setProssimaCitta(ordineDaGestire.getDestinazione());

					for (Arco a : dijkstra.getPath(ordineDaGestire.getSorgente(), ordineDaGestire.getDestinazione())
							.getEdgeList()) {

						if (a.getTipo().equals("Aereo")) {

							Citta metropoliDestinazione = (Citta) a.getDestinazione();
							ordineDaGestire.setProssimaCitta(metropoliDestinazione);
							break;
						}
					}

					if (ordineDaGestire.getDestinazione().equals(ordineDaGestire.getProssimaCitta())
							|| (!ordineDaGestire.getDestinazione().equals(ordineDaGestire.getProssimaCitta())
									&& !ordineDaGestire.getSorgente().equals(metropoli))) {

						if (!mappaMezzi.containsKey(metropoli)) {
							mappaMezzi.put(metropoli, new ArrayList<Mezzo>());
							mappaMezzi.get(metropoli).add(mapMezziConSpecifiche.get("Autobus"));
							mappaMezzi.get(metropoli).get(i).setCitta(metropoli);
							mappaMezzi.get(metropoli).get(i).setDataMezzo(ordineDaGestire.getDataOra());
							i++;
							nTir++;

						} else {

							for (Mezzo m : mappaMezzi.get(metropoli)) {

								if (m.getDataMezzo().isBefore(ordineDaGestire.getDataOra())) {
									mezzoTemporaneo = m;

									if (m.assegnaOrdine(ordineDaGestire) == false
											|| m.areTuttiGliOrdiniInTimeout() == true) {

										percorsoPresaOrdini(m.getCitta(), m);

									}

								}

							}

							if (mezzoTemporaneo == null) {
								mappaMezzi.get(metropoli).add(mapMezziConSpecifiche.get("Autobus"));
								mappaMezzi.get(metropoli).get(i).setCitta(metropoli);
								mappaMezzi.get(metropoli).get(i).setDataMezzo(ordineDaGestire.getDataOra());
								mappaMezzi.get(metropoli).get(i).assegnaOrdine(ordineDaGestire);
								i++;
								nTir++;

							}

						}

					}

					if (!ordineDaGestire.getDestinazione().equals(ordineDaGestire.getProssimaCitta())
							&& ordineDaGestire.getSorgente().equals(metropoli)) { // AGGIUNGO DIRETTAMENTE AI VOLI IN
																					// ATTESA
						if (!ordiniVoli.containsKey(metropoli)) { // ALTRIMENTI LO PRENDE IL TIR E LO LASCIA NELLA
																	// METROPOLI
							ordiniVoli.put(metropoli, new PriorityQueue<Ordine>());
						}
						ordiniVoli.get(metropoli).add(ordineDaGestire);

					}
				}

			}

			break;

		case TIMEOUT:
			System.out.println("TIMEOUT ORDINE: " + nuovoOrdine.getId());
			nuovoOrdine.setTimeout(true);
			break;

		case ORDINE_CONSEGNATO:
			break;

		}

	}

	private void percorsoPresaOrdini(Citta start, Mezzo mezzo) {
		List<Arco> percorso = new LinkedList<Arco>();
		for (int c = 1; c < mezzo.getOrdiniMezzo().size(); c++) {
			if (c == 1) {
				percorso.addAll(dijkstra.getPath(start, mezzo.getOrdiniMezzo().get(c - 1).getSorgente()).getEdgeList());
			}
			percorso.addAll(dijkstra.getPath(mezzo.getOrdiniMezzo().get(c - 1).getSorgente(),
					mezzo.getOrdiniMezzo().get(c).getSorgente()).getEdgeList());
		}

		for (Arco passo : percorso) {
			cammino(mezzo, passo, (int) Math.round(passo.getDistanza() / mezzo.getVelocitaMedia()));
		}

	}

	public void cammino(Mezzo m, Arco arco, int ora) {
		m.setCitta((Citta) arco.getDestinazione());
		m.setDataMezzo(m.getDataMezzo().plusSeconds(ora));
		Collections.sort(m.getOrdiniMezzo());
		for (Ordine ordineMezzo : m.getOrdiniMezzo()) {
			ordineMezzo.setDataOra(ordineMezzo.getDataOra().plusSeconds(ora));
			if (ordineMezzo.getSorgente().equals((Citta) arco.getDestinazione())) {
				if (!ordineMezzo.getDestinazione().equals(ordineMezzo.getProssimaCitta())) {
					ordineMezzo.setProssimaCitta(this.cercaMetropoliPiùVicina(ordineMezzo.getSorgente()));
				}
				System.out.println("Preso ordine " + ordineMezzo.getId() + " il " + ordineMezzo.getDataOra());
			}

		}
	}
	
	public void percorsoConsegnaOrdini(Citta start, Mezzo mezzo) {
		
	}

	/**
	 * @param nOrdiniGiornalieri the nOrdiniGiornalieri to set
	 */
	public void setnOrdiniGiornalieri(int nOrdiniGiornalieri) {
		this.nOrdiniGiornalieri = nOrdiniGiornalieri;
	}

	/**
	 * @param oraInizio the oraInizio to set
	 */
	public void setOraInizio(int ora, int minuti) {
		this.oraInizio = LocalTime.of(ora, minuti);
	}

	/**
	 * @param oraFine the oraFine to set
	 */
	public void setOraFine(int ora, int minuti) {
		this.oraFine = LocalTime.of(ora, minuti);
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(int ore) {
		this.timeout = Duration.ofHours(ore);
	}

	/**
	 * @return the nOrdiniInAttesa
	 */
	public int getnOrdiniInAttesa() {
		return nOrdiniInAttesa;
	}

	/**
	 * @return the nOrdiniCompletati
	 */
	public int getnOrdiniCompletati() {
		return nOrdiniCompletati;
	}

	/**
	 * @return the nGiorni
	 */
	public int getnGiorni() {
		return nGiorni;
	}

	/**
	 * @param nGiorni the nGiorni to set
	 */
	public void setnGiorni(int nGiorni) {
		this.nGiorni = nGiorni;
	}

	/**
	 * @return the timeout
	 */
	public Duration getTimeout() {
		return timeout;
	}

	/**
	 * @return the dataInizio
	 */

	public void setDataInizio(LocalDate dataInizio) {
		this.dataInizio = dataInizio;
	}

	/**
	 * @return the nTir
	 */
	public int getnTir() {
		return nTir;
	}

	/**
	 * @param nTir the nTir to set
	 */
	public void setnTir(int nTir) {
		this.nTir = nTir;
	}

	public Citta cercaMetropoliPiùVicina(Citta citta) {
		Citta piuVicina = null;
		int passiMin = 200;
		int passi = 0;

		for (Citta c : listaMetropoli) {

			if (citta.equals(c)) {
				return citta;
			}
			passi = dijkstra.getPath(citta, c).getVertexList().size();
			if (passi < passiMin) {
				passiMin = passi;
				piuVicina = c;
			}

		}
		return piuVicina;
	}

}
