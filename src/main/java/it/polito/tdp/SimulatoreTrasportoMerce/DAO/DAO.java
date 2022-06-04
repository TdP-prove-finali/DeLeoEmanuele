package it.polito.tdp.SimulatoreTrasportoMerce.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Citta;
import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Mezzo;
import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Ordine;
import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Tratta;

public class DAO {

	public void getCitta(Map<String, Citta> mapCitta) {                                      // RESTITUISCE TUTTE LE CITTA' 

		final String sql = "SELECT Partenza, Destinazione " + "FROM tratte "
				+ "WHERE  Mezzo_di_trasporto = 'Aereo' OR Mezzo_di_trasporto = 'Autobus' OR Mezzo_di_trasporto = 'Treno' "
				+ ";";

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Citta c1 = new Citta(rs.getString("Partenza"));
				Citta c2 = new Citta(rs.getString("Destinazione"));

				if (!mapCitta.values().contains(c1)) {
					mapCitta.put(c1.getNome(), c1);
				}

				if (!mapCitta.values().contains(c2)) {
					mapCitta.put(c2.getNome(), c2);
				}
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

	}

	public void addOrdine(Ordine o) {                                                             // AGGIUNGE ORDINE SIMULATO NEL DB

		try {
			Connection conn = ConnectDB.getConnection();
			Statement statement = conn.createStatement();
			statement.executeUpdate("INSERT INTO ordini (Sorgente, Destinazione, Peso, Volume, Data) " + "VALUES ('"
					+ o.getSorgente().getNome() + "','" + o.getDestinazione().getNome() + "','" + o.getPeso() + "','"
					+ o.getVolume() + "','" + Timestamp.valueOf(o.getDataOra()) + "');");
			statement.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

	}

	public void getOrdini(Map<Citta, List<Ordine>> mapOrdini, Map<String, Citta> mapCitta) {       // LEGGE GLI ORDINI SIMULATI DAL DB

		final String sql = "SELECT* FROM ordini ORDER BY Data;";

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Ordine o = new Ordine(rs.getInt("ID"), mapCitta.get(rs.getString("Sorgente")),
						mapCitta.get(rs.getString("Destinazione")), rs.getDouble("Peso"), rs.getDouble("Volume"),
						rs.getTimestamp("Data").toLocalDateTime());

				if (!mapOrdini.containsKey(o.getSorgente())) {
					List<Ordine> listaOrdini = new LinkedList<Ordine>();
					listaOrdini.add(o);
					mapOrdini.put(o.getSorgente(), listaOrdini);
				} else {
					mapOrdini.get(o.getSorgente()).add(o);
				}
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}
	}

	public List<Tratta> getTratte(Collection<Mezzo> mezzi, Map<String, Citta> mapCitta) {        // RESTITUISCE LE TRATTE DAL DB PER I MEZZI SPECIFICATI 

		List<Tratta> tratte = new ArrayList<Tratta>();
		final String sql = "SELECT* FROM tratte;";
		try {

			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				for (Mezzo m : mezzi) {

					if (m.getTipo().compareTo(rs.getString("Mezzo_di_trasporto")) == 0) {

						Tratta trattaInversa = new Tratta(mapCitta.get(rs.getString("Destinazione")),
								mapCitta.get(rs.getString("Partenza")),
								Double.parseDouble(rs.getString("Distanza_km").replace(",", ".")),
								rs.getString("Mezzo_di_trasporto"), rs.getInt("Emissioni_g"));
						Tratta newTratta = new Tratta(mapCitta.get(rs.getString("Partenza")),
								mapCitta.get(rs.getString("Destinazione")),
								Double.parseDouble(rs.getString("Distanza_km").replace(",", ".")),
								rs.getString("Mezzo_di_trasporto"), rs.getInt("Emissioni_g"));
						if (!tratte.contains(newTratta) && !tratte.contains(trattaInversa)) {
							tratte.add(newTratta);
						}
					}
				}
				st.close();
				conn.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}
	
		return tratte;
	}

	public void clearTableOrdini() {                                                // METODI PER PULIRE LE TABELLE

		try {
			Connection conn = ConnectDB.getConnection();
			Statement statement = conn.createStatement();
			statement.executeUpdate("TRUNCATE TABLE ordini;");
			statement.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}
	}

	public void clearTableOrdiniConsegnati() {

		try {
			Connection conn = ConnectDB.getConnection();
			Statement statement = conn.createStatement();
			statement.executeUpdate("TRUNCATE TABLE ordini_consegnati;");
			statement.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}
	}
	
	public void addOrdineConsegnato(Ordine o) {                                   // AGGIUNGE ORDINE CONSEGNATO SUL DB

		try {
			Connection conn = ConnectDB.getConnection();
			Statement statement = conn.createStatement();
			statement.executeUpdate("INSERT INTO ordini_consegnati (ID, citta_consegna, data) " + "VALUES ('"
					+ o.getId() + "','" + o.getDestinazione() + "','" + o.getDataOra() + "');");
			statement.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

	}

}
