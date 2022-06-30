package it.polito.tdp.SimulatoreTrasportoMerce.DAO;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Citta;
import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Mezzo;
import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Ordine;
import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Tratta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DAO {

	public void getCitta(Map<String, Citta> mapCitta) { // RESTITUISCE TUTTE LE CITTA'

		final String sql = "SELECT Partenza, Destinazione " + "FROM tratte "
				+ "WHERE  Mezzo_di_trasporto = 'Aereo' OR Mezzo_di_trasporto = 'Autobus'" + ";";

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

	public void addOrdine(Ordine o) { // AGGIUNGE ORDINE SIMULATO NEL DB

		try {
			Connection conn = ConnectDB.getConnection();
			Statement statement = conn.createStatement();
			statement.executeUpdate("INSERT INTO ordini (ID, Peso, Volume, Sorgente, Destinazione, dataPartenza) "
					+ "VALUES ('" + o.getId() + "','" + o.getPeso() + "','" + o.getVolume() + "','"
					+ o.getSorgente().getNome() + "','" + o.getDestinazione().getNome() + "','"
					+ Timestamp.valueOf(o.getDataOra()) + "');");
			statement.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

	}

	public ObservableList<Ordine> getOrdini(Map<String, Citta> mapCitta) { // LEGGE GLI ORDINI SIMULATI DAL DB

		ObservableList<Ordine> ordini = FXCollections.observableArrayList();
		final String sql = "SELECT* FROM ordini ORDER BY DataPartenza;";

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				String data = "";

				if (rs.getTimestamp("dataArrivo") != null) {
					data = rs.getTimestamp("dataArrivo").toLocalDateTime()
							.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")).toString();

					ordini.add(new Ordine(rs.getInt("ID"), mapCitta.get(rs.getString("Sorgente")),
							mapCitta.get(rs.getString("Destinazione")), rs.getDouble("Peso"), rs.getDouble("Volume"),
							rs.getTimestamp("dataPartenza").toLocalDateTime()
									.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")).toString(),
							data));

				}
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}
		return ordini;
	}

	public Ordine getOrdineById(int id, Map<String, Citta> cittaMap) {

		final String sql = "SELECT * FROM ordini WHERE id = " + id;
		Ordine ordine = null;

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				Citta sorgente = cittaMap.get(rs.getString("Sorgente"));
				Citta destinazione = cittaMap.get(rs.getString("Destinazione"));
				ordine = new Ordine(rs.getInt("ID"), sorgente, destinazione, rs.getDouble("Peso"),
						rs.getDouble("Volume"), rs.getTimestamp("DataPartenza").toLocalDateTime());
				if (rs.getTimestamp("dataArrivo") == null) {
					ordine.setDataArrivo(null);
				}
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}
		return ordine;
	}

	public List<Tratta> getTratte(Collection<Mezzo> mezzi, Map<String, Citta> mapCitta) { 
		
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

	public void clearTableOrdini() { // METODI PER PULIRE LE TABELLE

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

	public void addOrdineConsegnato(Ordine o, Citta citta, Mezzo mezzo) { // AGGIUNGE ORDINE CONSEGNATO SUL DB

		try {
			Connection conn = ConnectDB.getConnection();
			Statement statement = conn.createStatement();
			String tipo = mezzo.getTipo();

			if (tipo.equals("Autobus")) {
				tipo = "Tir";
			}
			statement.executeUpdate("INSERT INTO ordini_consegnati (ID, citta_consegna, data, ID_mezzo, tipo_mezzo) "
					+ "VALUES ('" + o.getId() + "','" + citta.getNome() + "','" + o.getDataOra() + "','" + mezzo.getId()
					+ "','" + tipo + "');");
			statement.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

	}

	public String tracciaOrdine(int id) { // AGGIUNGE ORDINE CONSEGNATO SUL DB

		String output = "";
		final String sql = "SELECT* FROM ordini_consegnati WHERE ID = " + id + ";";
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				output += rs.getString("citta_consegna") + "  Mezzo: " + rs.getInt("ID_mezzo") + " "
						+ rs.getString("tipo_mezzo") + "  Data: " + rs.getTimestamp("data").toLocalDateTime()
								.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")).toString()
						+ "\n";
			}

			if (output.equals("")) {
				output += "Ordine non ancora partito\n";
			}
			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return output;
	}

	public void addConsegnaEsterna(Ordine ordine) {
		try {
			Connection conn = ConnectDB.getConnection();
			Statement statement = conn.createStatement();
			statement.executeUpdate("INSERT INTO ordini_consegnati (ID, citta_consegna, data, ID_mezzo, tipo_mezzo) "
					+ "VALUES ('" + ordine.getId() + "','" + ordine.getSorgente().getNome() + "','"
					+ ordine.getDataOra() + "','" + 0 + "','" + "CONSEGNA ESTERNA" + "');");
			statement.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}
	}

	public void inserisciDataArrivo(int id, LocalDateTime data) {

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn
					.prepareStatement("UPDATE ordini SET dataArrivo='" + data + "' WHERE ID=" + id + ";");
			st.executeUpdate();
			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

	}

}
