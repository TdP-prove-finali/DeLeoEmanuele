package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import it.polito.tdp.SimulatoreTrasportoMerce.DAO.DAO;
import javafx.scene.control.ProgressBar;

public class SimulatoreProvvisorio {

	public Date startDate = null;
	public DAO dao;
	public LocalDateTime tempo;
	Graph<Citta, Arco> grafo;
	DijkstraShortestPath<Citta, Arco> dijkstra;
	Map<String, Mezzo> mapMezziConSpecifiche;
	Map<Integer, Mezzo> mapMezzi;
	Map<String, Citta> mapCitta;
	double percentualeRiempimento;

	// Eventi

	private PriorityQueue<Event> queue;

	// Parametri

	private int nOrdiniGiornalieri;
	private int nGiorni;
	private LocalDate dataInizio;
	private LocalTime oraInizio;
	private LocalTime oraFine;
	private int timeout;

	// Stato del mondo

	private int nOrdiniInAttesa;
	private int nOrdiniCompletati;
	private double costoTotale;

	// Impostazione parametri iniziali

	public void init(DijkstraShortestPath<Citta, Arco> dijkstra, Graph<Citta, Arco> grafo,
			Map<String, Mezzo> mezziConSpecifiche, Map<String, Citta> citta, List<Citta> listaMetropoli,
			double percentualeRiempimento) {

		startDate = new Date();
		dao = new DAO();
		this.grafo = grafo;
		this.dijkstra = dijkstra;
		this.mapMezziConSpecifiche = mezziConSpecifiche;
		this.mapCitta = citta;
		List<Citta> listaCittaTotali = new ArrayList<Citta>(citta.values());
		this.mapMezzi = new TreeMap<Integer, Mezzo>();
		this.queue = new PriorityQueue<Event>();
		this.nOrdiniCompletati = 0;
		this.costoTotale = 0.0;
		this.percentualeRiempimento = percentualeRiempimento;

		int contatoreId = 1; // COUNTER PER GLI ID DEGLI ORDINI SIMULATI
		Citta sorgente = null; // DATI UTILIZZATI PER SIMULARE GLI ORDINI
		Citta destinazione = null;
		Ordine newOrdine = null;
		Random rand = new Random();
		double minPeso = 0.5;
		double maxPeso = 0.0;
		double minVolume = 0.5;
		double maxVolume = 0.0;
		long oreGiornaliere = ChronoUnit.HOURS.between(oraInizio, oraFine);
		LocalDate data = this.dataInizio;

		for (Mezzo m : mezziConSpecifiche.values()) { // PESI E VOLUMI MASSIMI GLI ORDINI SONO VINCOLATI ALLA META DEI
														// PESI E VOLUMI
			if (m.getPesoMax() >= maxPeso) { // DISPONIBILI PER I TIPI DI MEZZI GENERATI
				maxPeso = m.getPesoMax() / 10;
			}
			if (m.getSpazioMax() >= maxVolume) {
				maxVolume = m.getSpazioMax() / 10;
			}
		}

		while (data.isBefore(dataInizio.plusDays(nGiorni))) { // CICLO DI CREAZIONE DEGLI ORDINI
			int contatoreOrdini = 1;
			LocalTime ora = oraInizio;
			while (contatoreOrdini <= this.nOrdiniGiornalieri) {

				int minutes = rand.nextInt(60 * ((int) oreGiornaliere));
				ora = ora.plusMinutes(minutes);
				boolean isCittaMinore1 = rand.nextInt(4) == 0; // valori booleani per sapere se si tratta di citta
																// minori o metropoli
				boolean isCittaMinore2 = rand.nextInt(4) == 0;
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

				if (ora.isBefore(oraFine) && ora.isAfter(oraInizio) && !sorgente.equals(destinazione)) {
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

		boolean check = true;

		while (check) {

			Date currentDate = new Date();
			long diffInMillies = Math.abs(currentDate.getTime() - startDate.getTime());
			long diff = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);

			System.out.println(diff);
			if (diff >= 1) {
				runMezzo();
			}

			if (!this.queue.isEmpty()) {
				Event nuovoEvento = this.queue.poll();
				processEvent(nuovoEvento);
			} else {
				check = false;
			}
		}

	}

	public void runMezzo() {

		// TODO logica dei mezzi
		// STEP 1 recupero i mezzi utilizzati, verifico se sono arrivati a destinazione

		for (int id : mapMezzi.keySet()) {

			if (mapMezzi.get(id).getStato().equals(Stato.OCCUPATO)) { // aggiungere controllo tempo (se ha finito
																		// torna disponibile
				mapMezzi.get(id).setStato(Stato.DISPONIBILE);
			}

			if (mapMezzi.get(id).getStato().equals(Stato.DISPONIBILE)
					&& mapMezzi.get(id).getOrdiniMezzo().size() >= 1) {

				mapMezzi.get(id).setStato(Stato.OCCUPATO);

				while (!mapMezzi.get(id).getOrdiniMezzo().isEmpty()) {
					Ordine ordineDaGestire = mapMezzi.get(id).getOrdiniMezzo().poll();
					long secondi = Math.round(
							(ordineDaGestire.getProssimaTratta().getDistanza() / mapMezzi.get(id).getVelocitaMedia())
									* 3600);
					ordineDaGestire.setDataOra(ordineDaGestire.getDataOra().plusSeconds(secondi));
					mapMezzi.get(id).setDataMezzo(ordineDaGestire.getDataOra());

					if (mapMezzi.get(id).getDestinazione().equals(ordineDaGestire.getDestinazione())) {
						System.out.println("ordine " + ordineDaGestire.getId() + " CONSEGNATO");
						nOrdiniCompletati++;
					} else {

						System.out.println("step");
						ordineDaGestire.setSorgente(mapMezzi.get(id).getDestinazione());
						ordineDaGestire.rimuoviTratta();
						this.queue.add(new Event(ordineDaGestire, EventType.ORDINE_IN_CORSO,
								ordineDaGestire.getDataOra().plusHours(timeout)));
					}
				}
			}

		}
		// se non sono sono arrivati a destinazione esco dalla funzione, se sono
		// arrivati a destinazione o non sono mai partiti
		// metto lo stato a DISPONIBILE (l'altro stato è IN_VIAGGIO)

		// STEP 2 aggiorno gli ordini che ho dentro con il nuovo stato (ordine IN
		// ELABRORAZIONE o IN ATTESA)

	}

	private void processEvent(Event e) {

		Ordine nuovoOrdine = e.getOrdine();
		Ordine currentOnDB = dao.getOrdineById(nuovoOrdine.getId(), mapCitta);

		switch (e.getTipo()) {

		case NUOVO_ORDINE:

			// ho una code, una degli ordini non evasi
			// step 1 ho un ordine, eseguo dikstra per calcolare il percorso

			currentOnDB.setPercorso(
					dijkstra.getPath(currentOnDB.getSorgente(), currentOnDB.getDestinazione()).getEdgeList());
			Arco prossimo = currentOnDB.getProssimaTratta();

			if (!mapMezzi.containsKey(prossimo.getId())) {
				mapMezzi.put(prossimo.getId(),
						new Mezzo(prossimo.getId(), prossimo.getTipo(),
								mapMezziConSpecifiche.get(prossimo.getTipo()).getPesoMax(),
								mapMezziConSpecifiche.get(prossimo.getTipo()).getSpazioMax(),
								mapMezziConSpecifiche.get(prossimo.getTipo()).getVelocitaMedia(),
								mapMezziConSpecifiche.get(prossimo.getTipo()).getCostoCarburante(),
								(Citta) prossimo.getSorgente(), Stato.DISPONIBILE));
				mapMezzi.get(prossimo.getId()).setDestinazione((Citta) prossimo.getDestinazione());

			}

			currentOnDB.setMezzo(mapMezzi.get(prossimo.getId()));

			if (currentOnDB.isProcessable()) {

				currentOnDB.getMezzo().assegnaOrdine(currentOnDB);
				// step 2 faccio update dell'ordine aggiungendo il mezzo dove è stato
				// posizionato) - quindi l'ordine ha un oggetto mezzo all'interno e il mezzo ha
				// al suo interno un indentificativo

				// step 3 salvo l'ordine su DB tra gli ordini evasi

				// step 4 se destinazione del mezzo è uguale a destinazione finale dell'ordine
				// ok, altrimenti devi riaggiungere l'ordine alla coda con sorgente cambiata

				// Step 5
				// Update Mezzo aggiungendo l'ordine appena gestito

			} else {

				this.queue.add(
						new Event(currentOnDB, EventType.ORDINE_IN_CORSO, currentOnDB.getDataOra().plusHours(timeout)));

			}

			break;
		case ORDINE_IN_CORSO:

			Arco passo = currentOnDB.getProssimaTratta();

			if (!mapMezzi.containsKey(passo.getId())) {
				mapMezzi.put(passo.getId(),
						new Mezzo(passo.getId(), passo.getTipo(),
								mapMezziConSpecifiche.get(passo.getTipo()).getPesoMax(),
								mapMezziConSpecifiche.get(passo.getTipo()).getSpazioMax(),
								mapMezziConSpecifiche.get(passo.getTipo()).getVelocitaMedia(),
								mapMezziConSpecifiche.get(passo.getTipo()).getCostoCarburante(),
								(Citta) passo.getSorgente(), Stato.DISPONIBILE));
				mapMezzi.get(passo.getId()).setDestinazione((Citta) passo.getDestinazione());

			}

			currentOnDB.setMezzo(mapMezzi.get(passo.getId()));

			if (!currentOnDB.getMezzo().getOrdiniMezzo().contains(currentOnDB)) {
				if (currentOnDB.isProcessable()) {

					// TODO update mezzo con il nuovo ordine dentro
					currentOnDB.getMezzo().assegnaOrdine(currentOnDB);
					currentOnDB.getMezzo().setDataMezzo(currentOnDB.getDataOra());

				} else {
					this.queue.add(new Event(currentOnDB, EventType.ORDINE_IN_CORSO,
							currentOnDB.getDataOra().plusHours(timeout)));

				}
			}

			break;
		case TIMEOUT:
			nuovoOrdine.setTimeout(true);
			break;

		case ORDINE_CONSEGNATO:

		}

	}

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
		this.timeout = ore;
	}

	/**
	 * @return the nOrdiniCompletati
	 */
	public int getnOrdiniCompletati() {
		return nOrdiniCompletati;
	}

	/**
	 * @param nGiorni the nGiorni to set
	 */
	public void setnGiorni(int nGiorni) {
		this.nGiorni = nGiorni;
	}

	public int getNtir() {
		int tir = 0;
		for (int id : mapMezzi.keySet()) {
			if (mapMezzi.get(id).getTipo().equals("Autobus")) {
				tir++;
			}
		}
		return tir;
	}

	/**
	 * @return the dataInizio
	 */
	public int getNaerei() {
		int aerei = 0;
		for (int id : mapMezzi.keySet()) {
			if (mapMezzi.get(id).getTipo().equals("Aereo")) {
				aerei++;
			}
		}
		return aerei;
	}

	public void setDataInizio(LocalDate dataInizio) {
		this.dataInizio = dataInizio;
	}

}
