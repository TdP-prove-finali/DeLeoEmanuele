package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Event.EventType;
import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Mezzo.Stato;
import it.polito.tdp.SimulatoreTrasportoMerce.DAO.DAO;

public class Simulator {

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

			if (diff >= 5) {
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

		long time = 0;
		double distanza = 0.0;

		for (int id : mapMezzi.keySet()) {

			if (mapMezzi.get(id).getStato().equals(Stato.OCCUPATO)) {

				mapMezzi.get(id).setStato(Stato.DISPONIBILE);
			}

			if ((mapMezzi.get(id).getStato().equals(Stato.DISPONIBILE)
					&& mapMezzi.get(id).getSpazioOccupato() >= (percentualeRiempimento / 100)
							* mapMezzi.get(id).getSpazioMax()
					&& mapMezzi.get(id).getPesoOccupato() >= (percentualeRiempimento / 100)
							* mapMezzi.get(id).getPesoMax())
					|| mapMezzi.get(id).isFlagPieno() == true) {

				mapMezzi.get(id).setStato(Stato.OCCUPATO);
				mapMezzi.get(id).setFlagPieno(false);
				mapMezzi.get(id).aggiungiVIaggio();
				costoTotale += mapMezzi.get(id).getOrdiniMezzo().peek().getProssimaTratta().getDistanza()
						* mapMezzi.get(id).getCostoCarburante();

				while (!mapMezzi.get(id).getOrdiniMezzo().isEmpty()) {
					Ordine ordineDaGestire = mapMezzi.get(id).getOrdiniMezzo().poll();
					long secondi = Math.round(
							(ordineDaGestire.getProssimaTratta().getDistanza() / mapMezzi.get(id).getVelocitaMedia())
									* 3600);
					distanza = (double) ordineDaGestire.getProssimaTratta().getDistanza();
					time = secondi;
					ordineDaGestire.setDataOra(mapMezzi.get(id).getDataMezzo().plusSeconds(secondi));

					if (mapMezzi.get(id).getDestinazione().equals(ordineDaGestire.getDestinazione())) {
						System.out.println("ordine " + ordineDaGestire.getId() + " CONSEGNATO");
						dao.addOrdineConsegnato(ordineDaGestire, ordineDaGestire.getDestinazione(), mapMezzi.get(id));
						nOrdiniCompletati++;
					} else {

						System.out.println(
								"step ordine= " + ordineDaGestire.getId() + " a " + mapMezzi.get(id).getDestinazione());
						ordineDaGestire.setSorgente(mapMezzi.get(id).getDestinazione());
						dao.addOrdineConsegnato(ordineDaGestire, mapMezzi.get(id).getDestinazione(), mapMezzi.get(id));
						ordineDaGestire.rimuoviTratta();
						this.queue.add(new Event(ordineDaGestire, EventType.ORDINE_IN_CORSO,
								ordineDaGestire.getDataOra().plusHours(1)));
					}
				}
				costoTotale += distanza * mapMezzi.get(id).getCostoCarburante();
				mapMezzi.get(id).setDataMezzo(mapMezzi.get(id).getDataMezzo().plusSeconds(time));
				mapMezzi.get(id).setPesoOccupato(0.0);
				mapMezzi.get(id).setSpazioOccupato(0.0);
			}

		}

	}

	private void processEvent(Event e) {

		Ordine nuovoOrdine = e.getOrdine();
		Ordine currentOnDB = dao.getOrdineById(nuovoOrdine.getId(), mapCitta);

		switch (e.getTipo()) {

		case NUOVO_ORDINE:

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

			} else {

				this.queue.add(
						new Event(currentOnDB, EventType.ORDINE_IN_CORSO, currentOnDB.getDataOra().plusHours(timeout)));

			}

			break;
		case ORDINE_IN_CORSO:

			currentOnDB = nuovoOrdine;
			currentOnDB.setTimeout(true);
			Arco passo = currentOnDB.getProssimaTratta();

			if (passo == null) {
				System.out.println("ordine " + currentOnDB.getId() + " consegnato a " + currentOnDB.getDestinazione()
						+ " da " + currentOnDB.getSorgente() + " \n\n tappe: " + currentOnDB.getPercorso());
				dao.addOrdineConsegnato(currentOnDB, currentOnDB.getDestinazione(), currentOnDB.getMezzo());
				break;
			}

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

			if (!mapMezzi.get(passo.getId()).getOrdiniMezzo().contains(currentOnDB)) {
				if (currentOnDB.isProcessable()) {

					mapMezzi.get(passo.getId()).assegnaOrdine(currentOnDB);
					mapMezzi.get(passo.getId()).setDataMezzo(currentOnDB.getDataOra());

				} else {
					this.queue.add(
							new Event(currentOnDB, EventType.ORDINE_IN_CORSO, currentOnDB.getDataOra().plusHours(1)));

				}
			}

			break;
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
			if (mapMezzi.get(id).getTipo().equals("Autobus") && mapMezzi.get(id).getNumeroVIaggi() >= 1) {
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
			if (mapMezzi.get(id).getTipo().equals("Aereo") && mapMezzi.get(id).getNumeroVIaggi() >= 1) {
				aerei++;
			}
		}
		return aerei;
	}

	public void setDataInizio(LocalDate dataInizio) {
		this.dataInizio = dataInizio;
	}

	/**
	 * @return the costoTotale
	 */
	public String getCostoTotale() {
		return String.format("%.0f", this.costoTotale);
	}

	/**
	 * @param percentualeRiempimento the percentualeRiempimento to set
	 */
	public void setPercentualeRiempimento(double percentualeRiempimento) {
		this.percentualeRiempimento = percentualeRiempimento;
	}

}
