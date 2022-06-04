package it.polito.tdp.SimulatoreTrasoortoMerce.Model;

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
	Map<Citta, List<Mezzo>> mappaAerei;
	List<Mezzo> aereiPartiti;
	List<Ordine> ordiniInViaggio;
	int i;

	// Eventi

	private PriorityQueue<Event> queue;

	// Parametri

	private int nOrdiniGiornalieri;
	private int nGiorni;
	private LocalDate dataInizio;
	private LocalTime oraInizio; // ORE DI TRASPORTO GIORNALIERE
	private LocalTime oraFine; // = LocalTime.of(8, 00); INPUT
	private int timeout; // = Duration.ofMinutes(x)

	// Stato del mondo

	private int nOrdiniInAttesa;
	private Map<Citta, PriorityQueue<Ordine>> mappaOrdiniInAttesa; // OGNI METROPOLI E' UN'AREA IN CUI OPERANO I TIR
	Map<Citta, PriorityQueue<Ordine>> ordiniVoli; // IL SISTEMA SI OCCUPA DEGLI SMISTAMENTI (SE AD ESEMPIO UN TIR PRENDE
													// UN ORDINE
	// Misure in uscita // DESTINATO ALL'AREA DI UNA METROPOLI DIFFERENTE DA QUELLA
	// IN CUI OPERA

	private int nOrdiniCompletati;

	// Impostazione parametri iniziali

	public void init(DijkstraShortestPath<Citta, Arco> dijkstra, Graph<Citta, Arco> grafo,
			Map<String, Mezzo> mezziConSpecifiche, Collection<Citta> citta, List<Citta> listaMetropoli) {
		dao = new DAO();
		this.grafo = grafo;
		this.dijkstra = dijkstra;
		this.listaMetropoli = listaMetropoli;
		this.mapMezziConSpecifiche = mezziConSpecifiche;
		List<Citta> listaCittaTotali = new ArrayList<Citta>(citta);
		this.mappaOrdiniInAttesa = new TreeMap<Citta, PriorityQueue<Ordine>>();
		this.aereiPartiti = new ArrayList<Mezzo>();
		this.ordiniInViaggio = new ArrayList<Ordine>();
		mappaMezzi = new TreeMap<Citta, List<Mezzo>>(); // mappaMezzi contiene i tir
		mappaAerei = new TreeMap<Citta, List<Mezzo>>(); // mappaAerei contiene gli aerei
		ordiniVoli = new TreeMap<Citta, PriorityQueue<Ordine>>();
		this.queue = new PriorityQueue<Event>(); // Inizializzo coda eventi
		this.nOrdiniCompletati = 0;
		i = 0; // COUNTER PER GLI ID DEI MEZZI CREATI DINAMICAMENTE

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
				maxPeso = m.getPesoMax() / 50;
			}
			if (m.getSpazioMax() >= maxVolume) {
				maxVolume = m.getSpazioMax() / 50;
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

		while (!this.queue.isEmpty()) {
			Event nuovoEvento = this.queue.poll();
			processEvent(nuovoEvento);

		}
	}

	private void processEvent(Event e) {

		Ordine nuovoOrdine = e.getOrdine();
		Mezzo aereoTemporaneo = null;

		switch (e.getTipo()) {

		case NUOVO_ORDINE:

			this.queue.add(new Event(nuovoOrdine, EventType.TIMEOUT, nuovoOrdine.getDataOra().plusHours(timeout))); // FINCHE'
																													// ARRIVA
																													// UN
																													// ORDINE
																													// NUOVO
																													// LO
																													// AGGIUNGO
																													// AGLI
																													// ORDINI
																													// IN
																													// ATTESA
																													// DELLA
																													// METROPOLI
																													// PIU'
																													// VICINA
																													// E
																													// FACCIO
																													// IL
																													// TRASPORTO
																													// DI
																													// QUELLO
																													// CHE
																													// C'ERA
																													// PRIMA
			Citta metropoliVicina = this.cercaMetropoliPiùVicina(nuovoOrdine.getSorgente());

			if (!mappaOrdiniInAttesa.containsKey(metropoliVicina)) {
				mappaOrdiniInAttesa.put(metropoliVicina, new PriorityQueue<Ordine>());
			}
			mappaOrdiniInAttesa.get(metropoliVicina).add(nuovoOrdine);

			for (Citta metropoli : mappaOrdiniInAttesa.keySet()) { // CONTROLLO TUTTE LE AEREE E ORGANIZZO GLI ORDINI

				while (!mappaOrdiniInAttesa.get(metropoli).isEmpty()) {
					Ordine ordineDaGestire = mappaOrdiniInAttesa.get(metropoli).poll();

					ordineDaGestire.setProssimaCitta(ordineDaGestire.getDestinazione()); // IMPOSTO LA PROSSIMA CITTA'
																							// AGLI ORDINI

					if (!this.cercaMetropoliPiùVicina(ordineDaGestire.getDestinazione()).equals(metropoli)) {
						ordineDaGestire
								.setProssimaCitta(this.cercaMetropoliPiùVicina(ordineDaGestire.getDestinazione())); // SE
																													// LA
																													// DESTINAZIONE
																													// E'
																													// LONTANA
																													// IMPOSTO
																													// LA
																													// METROPOLI
																													// A
																													// CUI
																													// DOVRA'
																													// ARRIVARE
					}

					if ((ordineDaGestire.getSorgente().equals(metropoli)
							&& listaMetropoli.contains(ordineDaGestire.getDestinazione()))
							|| (ordineDaGestire.getSorgente().equals(metropoli) && !this
									.cercaMetropoliPiùVicina(ordineDaGestire.getDestinazione()).equals(metropoli))) { // SE
																														// SI
																														// TRATTA
																														// DI
																														// UN
																														// ORDINE
																														// BASE
																														// (ES.
																														// TORINO->ROMA
						if (!ordiniVoli.containsKey(metropoli)) { // LO METTO DIRETTAMENTE TRA GLI ORDINI IN VOLO
							ordiniVoli.put(metropoli, new PriorityQueue<Ordine>()); // CHE SONO A TORINO)
						}
						ordiniVoli.get(metropoli).add(ordineDaGestire);

						while (!ordiniVoli.get(metropoli).isEmpty()) {

							Ordine ordineInVolo = ordiniVoli.get(metropoli).poll();

							if (!mappaAerei.containsKey(metropoli)) { // E CERCO DI ASSEGNARLO AD UN AEREO IN QUELLA
																		// METROPOLI
																		// (SE ESISTE)
								mappaAerei.put(metropoli, new ArrayList<Mezzo>());
								mappaAerei.get(metropoli)
										.add(new Mezzo(i, "Aereo", 100.0, 100.0, 200.0, 8.0, metropoli));
								mappaAerei.get(metropoli).get(mappaAerei.get(metropoli).size() - 1)
										.assegnaOrdine(ordineInVolo);
								mappaAerei.get(metropoli).get(mappaAerei.get(metropoli).size() - 1) // SE SONO DENTRO
																									// ALL'IF IMPOSTO
																									// COME DESTINAZIONE
										.setDestinazione(ordineInVolo.getProssimaCitta()); // DELL'AEREO LA PROSSIMA
																							// CITTA' DELL'ORDINE
								mappaAerei.get(metropoli).get(mappaAerei.get(metropoli).size() - 1)
										.setDataMezzo(ordineInVolo.getDataOra());
								;
								i++;
							}

							else { // ALTRIMENTI FACCIO UN CHECK DEGLI AEREI CHE CI SONO

								for (Mezzo aereo : mappaAerei.get(metropoli)) {

									if ((aereo.getDataMezzo().isBefore(ordineInVolo.getDataOra())
											&& aereo.getDestinazione() == null)
											|| (aereo.getDataMezzo().isBefore(ordineInVolo.getDataOra()) && aereo
													.getDestinazione().equals(ordineInVolo.getProssimaCitta()))) {

										if (aereo.assegnaOrdine(ordineInVolo) == false) { // L'AEREO E' PIENO E PUO'
																							// PARTIRE
											ordiniInViaggio
													.addAll(consegnaAerei(metropoli, aereo.getDestinazione(), aereo));

										} else {

											if (aereo.getDestinazione() == null) { // SE HA DESTINAZIONE NULLA VUOL DIRE
																					// CHE ARRIVA DA UN VIAGGIO
												aereo.setDestinazione(ordineInVolo.getProssimaCitta()); // ED E'
																										// DISPONIBILE
																										// (VEDI IN
																										// BASSO
																										// consegnaAerei()
											}
											aereoTemporaneo = aereo;
											System.out.println("Preso ordine " + ordineInVolo.getId()
													+ " cittaMetropoli:" + metropoli);
											break;

										}

									}
								}

								if (!aereiPartiti.isEmpty()) {

									for (Mezzo aereo : aereiPartiti) { // AGGIORNO MAPPA AEREI CON GLI AEREI CHE SONO
																		// PARTITI E CAMBIANO
																		// RESIDENZA
										if (!mappaAerei.containsKey(aereo.getCitta())) {
											mappaAerei.put(aereo.getCitta(), new ArrayList<Mezzo>());
										}
										mappaAerei.get(aereo.getCitta()).add(aereo);
									}
									mappaAerei.get(metropoli).removeAll(aereiPartiti);
									aereiPartiti.clear();
								}

								if (aereoTemporaneo == null) { // SE NON HO TROVATO UN AEREO PER L'ORDINE, NE CREO UNO
									mappaAerei.get(metropoli)
											.add(new Mezzo(i, "Autobus", 100.0, 100.0, 80.0, 8.0, metropoli));
									mappaAerei.get(metropoli).get(mappaAerei.get(metropoli).size() - 1)
											.setDataMezzo(ordineInVolo.getDataOra());
									mappaAerei.get(metropoli).get(mappaAerei.get(metropoli).size() - 1)
											.setDestinazione(ordineInVolo.getProssimaCitta());
									mappaAerei.get(metropoli).get(mappaAerei.get(metropoli).size() - 1)
											.assegnaOrdine(ordineInVolo);
									i++;
								}

							}

						}
					}

					else { // SE L'ORDINE NON ERA BANALE (tipo Alessanria -> Asti
							// OPPURE Alessanria (Piemonte) -> Latina (Lazio)
						if (!mappaMezzi.containsKey(metropoli)) { // PRENDERA' A PRESCINDERE UN TIR
							mappaMezzi.put(metropoli, new ArrayList<Mezzo>());
							mappaMezzi.get(metropoli).add(new Mezzo(i, "Autobus", 100.0, 100.0, 80.0, 8.0, metropoli));
							mappaMezzi.get(metropoli).get(mappaMezzi.get(metropoli).size() - 1).setId(i);
							mappaMezzi.get(metropoli).get(mappaMezzi.get(metropoli).size() - 1).setCitta(metropoli);
							mappaMezzi.get(metropoli).get(mappaMezzi.get(metropoli).size() - 1)
									.setDataMezzo(ordineDaGestire.getDataOra());
							mappaMezzi.get(metropoli).get(mappaMezzi.get(metropoli).size() - 1)
									.assegnaOrdine(ordineDaGestire);
							i++;

						} else {

							assegnaOrdiniTir(metropoli, ordineDaGestire);
						}

					}
				}

			}

			for (Ordine o : ordiniInViaggio) {

				if (!mappaOrdiniInAttesa.containsKey(o.getSorgente())) {
					mappaOrdiniInAttesa.put(o.getSorgente(), new PriorityQueue<Ordine>());
				}

				mappaOrdiniInAttesa.get(o.getSorgente()).add(o);
			}

			ordiniInViaggio.clear();

			break;

		case TIMEOUT:
			System.out.println("TIMEOUT ORDINE: " + nuovoOrdine.getId());
			nuovoOrdine.setTimeout(true);
			break;

		case ORDINE_CONSEGNATO:

		}

	}

	private void percorsoPresaOrdini(Citta start, Mezzo mezzo) { // PERCORSO PER I TIR PER PRELEVARE GLI ORDINI DI
																	// COMPETENZA DELLA LORO SEDE
		List<Citta> percorsoCitta = new LinkedList<Citta>(); // CHE GLI SONO STATI ASSEGNATI
		percorsoCitta.add(start);
		List<Arco> percorso = new LinkedList<Arco>();

		for (Ordine ordineMezzo : mezzo.getOrdiniMezzo()) {
			if (!percorsoCitta.contains(ordineMezzo.getSorgente())) { // ESSI ORGANIZZANO IL PERCORSO PIU' BREVE
																		// FILTRANDO LE SORGENTI DEGLI ORDINI
				percorsoCitta.add(ordineMezzo.getSorgente());
			}
		}

		for (int c = 1; c < percorsoCitta.size(); c++) {
			percorso.addAll(dijkstra.getPath(percorsoCitta.get(c - 1), percorsoCitta.get(c)).getEdgeList()); // LO
																												// SHORTEST
																												// PATH
																												// IMPLEMENTATO
																												// DALL'ALGORITMO
																												// DI
																												// DIJKSTRA
																												// RESTITUISCE
																												// IL
																												// PERCORSO
																												// TRA
																												// LE
																												// CITTA'
		}
		mezzo.setDataMezzo(mezzo.getDataPartenza());
		for (Arco passo : percorso) {
			camminoPresaOrdini(mezzo, passo,
					(int) Math.round(((passo.getDistanza() / mezzo.getVelocitaMedia()) * 3600)));
		}

	}

	public void camminoPresaOrdini(Mezzo m, Arco arco, int ora) { // OGNI VOLTA CHE ATTRAVERSA UN NODO CONTROLLA SE C'E'
																	// L'ORDINE DA PRENDERE
		m.setCitta((Citta) arco.getDestinazione()); // ED AGGIORNA LA SUA CITTA' E LA SUA DATA
		m.setDataMezzo(m.getDataMezzo().plusSeconds(ora));
		Collections.sort(m.getOrdiniMezzo());
		for (Ordine ordineMezzo : m.getOrdiniMezzo()) {
			ordineMezzo.setDataOra(m.getDataMezzo());
			if (ordineMezzo.getSorgente().equals((Citta) arco.getDestinazione())) {
				System.out.println("Preso ordine " + ordineMezzo.getId() + " il " + ordineMezzo.getDataOra());
				System.out.println(m.getCitta());
			}

		}
	}

	public void percorsoConsegnaOrdini(Citta start, Mezzo mezzo) { // IN GRAN PARTE E' LA FOTOCOPIA DI
																	// percorsoPresaOrdini()
																	// MA QUI CAMBIA IL CAMMINO
		List<Citta> percorsoCitta = new LinkedList<Citta>();
		percorsoCitta.add(start);
		List<Arco> percorso = new LinkedList<Arco>();

		for (Ordine ordineMezzo : mezzo.getOrdiniMezzo()) {

			if (ordineMezzo.getDestinazione().equals(ordineMezzo.getProssimaCitta())) {
				if (!percorsoCitta.contains(ordineMezzo.getDestinazione())) {
					percorsoCitta.add(ordineMezzo.getDestinazione());
				}
			} else {
				if (!percorsoCitta.contains(this.cercaMetropoliPiùVicina(ordineMezzo.getSorgente()))) {
					percorsoCitta.add(this.cercaMetropoliPiùVicina(ordineMezzo.getSorgente()));
				}
			}
		}

		for (int c = 1; c < percorsoCitta.size(); c++) {

			percorso.addAll(dijkstra.getPath(percorsoCitta.get(c - 1), percorsoCitta.get(c)).getEdgeList());
		}

		for (Arco passo : percorso) {
			camminoConsegnaOrdini(mezzo, passo,
					(int) Math.round((passo.getDistanza() / mezzo.getVelocitaMedia())) * 3600);
		}

		mezzo.scaricaMerce();

	}

	private void camminoConsegnaOrdini(Mezzo mezzo, Arco passo, int ora) {

		List<Ordine> ordiniConsegnati = new ArrayList<Ordine>();
		mezzo.setCitta((Citta) passo.getDestinazione());
		mezzo.setDataMezzo(mezzo.getDataMezzo().plusSeconds(ora));

		for (Ordine ordineMezzo : mezzo.getOrdiniMezzo()) {
			ordineMezzo.setDataOra(mezzo.getDataMezzo());

			if (ordineMezzo.getDestinazione().equals((Citta) passo.getDestinazione())) {
				System.out.println("Consegna ordine " + ordineMezzo.getId() + " alle " + ordineMezzo.getDataOra()
						+ "con " + mezzo.getTipo() + " id:" + mezzo.getId() + " data: " + ordineMezzo.getDataOra());
				aggiungiOrdineConsegnato(ordineMezzo);
				ordiniConsegnati.add(ordineMezzo);
				nOrdiniCompletati++;
			}

			if ((!ordineMezzo.getProssimaCitta().equals(ordineMezzo.getDestinazione())
					&& mezzo.getCitta().equals(this.cercaMetropoliPiùVicina(ordineMezzo.getSorgente())))
					|| (listaMetropoli.contains(ordineMezzo.getDestinazione())
							&& mezzo.getCitta().equals(this.cercaMetropoliPiùVicina(ordineMezzo.getSorgente())))) {

				ordineMezzo.setSorgente(mezzo.getCitta());
				System.out.println("QUESTO ORDINE PRENDE L'AEREO : ID: " + ordineMezzo.getId() + "per "
						+ ordineMezzo.getProssimaCitta());

				if (!ordiniVoli.containsKey(mezzo.getCitta())) {
					ordiniVoli.put(mezzo.getCitta(), new PriorityQueue<Ordine>());
				}

				ordiniVoli.get(mezzo.getCitta()).add(ordineMezzo);

			}
		}

		mezzo.consegnaOrdini(ordiniConsegnati); // TOLGO GLI ORDINI DAL TIR
	}

	public List<Ordine> consegnaAerei(Citta metropoliSorgente, Citta metropoliDestinazione, Mezzo aereo) {

		List<Ordine> ordiniInViaggio = new ArrayList<Ordine>();
		aereo.setCitta(metropoliDestinazione);
		aereo.setDestinazione(null);
		Arco voloAereo = null;

		for (Arco passo : grafo.edgeSet()) {
			if (passo.getTipo().equals("Aereo") && passo.getSorgente().equals(metropoliSorgente)
					&& passo.getDestinazione().equals(metropoliDestinazione)) {
				voloAereo = passo;
				break;
			}
		}

		int secondi = (int) Math.round((voloAereo.getDistanza() / aereo.getVelocitaMedia()) * 3600);
		aereo.setDataMezzo(aereo.getDataPartenza().plusSeconds(secondi));
		for (Ordine ordineAereo : aereo.getOrdiniMezzo()) {

			ordineAereo.setDataOra(aereo.getDataMezzo());
			ordineAereo.setSorgente(metropoliDestinazione);

			if (ordineAereo.getDestinazione().equals(metropoliDestinazione)) {
				System.out.println("ordine " + ordineAereo.getId() + " consegnato il " + ordineAereo.getDataOra()
						+ " con aereo id: " + aereo.getId() + "a " + metropoliDestinazione);
				nOrdiniCompletati++;
			} else {
				ordiniInViaggio.add(ordineAereo);
				System.out
						.println("step metropoli ordine: " + ordineAereo.getId() + " città: " + metropoliDestinazione);

			}
		}

		aereo.scaricaMerce();
		aereiPartiti.add(aereo);

		return ordiniInViaggio;
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
		this.timeout = ore;
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
	public int getTimeout() {
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

		int s = 0;

		for (Citta c : mappaMezzi.keySet()) {
			s += mappaMezzi.get(c).size();
		}

		return s;
	}

	public int getNnAerei() {

		int n = 0;
		for (Citta c : mappaAerei.keySet()) {
			n += mappaAerei.get(c).size();
		}
		return n;
	}

	public Citta cercaMetropoliPiùVicina(Citta citta) {
		Citta piuVicina = null;
		double passiMin = 999999999.9;
		double passi = 0.0;

		for (Citta c : listaMetropoli) {

			if (citta.equals(c)) {
				return citta;
			}

			for (Arco a : dijkstra.getPath(c, citta).getEdgeList()) {

				passi += a.getDistanza();
			}

			if (passi < passiMin) {
				passiMin = passi;
				piuVicina = c;
			}
		}
		return piuVicina;
	}

	public void assegnaOrdiniTir(Citta metropoli, Ordine ordineDaGestire) {

		Mezzo mezzoTemporaneo = null;
		for (Mezzo m : mappaMezzi.get(metropoli)) {

			if (m.getDataMezzo().isBefore(ordineDaGestire.getDataOra())) {

				if (m.assegnaOrdine(ordineDaGestire) == false) {
					percorsoPresaOrdini(m.getCitta(), m);
					percorsoConsegnaOrdini(metropoli, m);

				} else {
					mezzoTemporaneo = m;
					break;

				}

			}

		}

		if (mezzoTemporaneo == null) {
			mapMezziConSpecifiche.get("Autobus").setId(i);
			mappaMezzi.get(metropoli).add(new Mezzo(i, "Autobus", 100.0, 100.0, 80.0, 8.0, metropoli));
			mappaMezzi.get(metropoli).get(mappaMezzi.get(metropoli).size() - 1).setId(i);
			mappaMezzi.get(metropoli).get(mappaMezzi.get(metropoli).size() - 1).setCitta(metropoli);
			mappaMezzi.get(metropoli).get(mappaMezzi.get(metropoli).size() - 1)
					.setDataMezzo(ordineDaGestire.getDataOra());
			mappaMezzi.get(metropoli).get(mappaMezzi.get(metropoli).size() - 1).assegnaOrdine(ordineDaGestire);
			i++;

		}
	}

	public void aggiungiOrdineConsegnato(Ordine ord) {
		dao.addOrdineConsegnato(ord);
	}
}
