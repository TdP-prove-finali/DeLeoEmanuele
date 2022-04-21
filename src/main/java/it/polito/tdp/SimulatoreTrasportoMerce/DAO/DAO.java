package it.polito.tdp.SimulatoreTrasportoMerce.DAO;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Citta;
import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Mezzo;
import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Ordine;
import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Tratta;

public class DAO {
	
	public List<Citta> getAllCitta() {
		
		
		final String sql = "SELECT DISTINCT Partenza "
				+ "FROM tratte "
				+ "WHERE  Mezzo_di_trasporto = 'Aereo' OR Mezzo_di_trasporto = 'Autobus' OR Mezzo_di_trasporto = 'Treno' "
				+ ";";
		
		List<Citta> citta = new ArrayList<Citta>();
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Citta c = new Citta(rs.getString("Partenza"));
				citta.add(c);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}
		return citta;
	}
	
	
	public void addOrdine(Ordine o) {
		
		LocalDateTime date = o.getData(); 
		
		try {
			Connection conn = ConnectDB.getConnection();
			Statement statement = conn.createStatement();
			statement.executeUpdate("INSERT INTO ordini (ID, Sorgente, Destinazione, Peso, Volume, Data) "
					+ "VALUES ('"+o.getId()+"','"+o.getSorgente()+"','"+o.getDestinazione()+"','"+o.getPeso()+"','"+o.getVolume()+"','"+Timestamp.valueOf(date)+"')");
			statement.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}
		
	}
	
	public Ordine getOrdine(int id){
		
		Ordine o = null;
		final String sql = "SELECT* FROM ordini WHERE ID='"+id+"';";

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
		o = new Ordine(rs.getInt("ID"), rs.getString("Sorgente"), rs.getString("Destinazione"), Double.parseDouble(""+rs.getFloat("Peso")+""),Double.parseDouble(""+rs.getFloat("Volume")+""),rs.getTimestamp("Data").toLocalDateTime());
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}
		
		return o;
	}
	
	public List<Tratta> getTratte(List<Mezzo> mezzi, List<Citta> citta) {
		
		Map<String,Citta> mapCitta = new HashMap<String, Citta>();
		
		for (Citta c : citta) {
			mapCitta.put(c.getNome(), c);
		}
		List<Tratta> tratte = new ArrayList<Tratta>();
		final String sql = "SELECT* FROM tratte;";
		try {
			
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
	
			while (rs.next()) {
				
				for(Mezzo m : mezzi) {
					
					if (m.getTipo().compareTo(rs.getString("Mezzo_di_trasporto"))==0) {
						
						Tratta newTratta = new Tratta(mapCitta.get(rs.getString("Partenza")), mapCitta.get(rs.getString("Destinazione")) ,Double.parseDouble(rs.getString("Distanza_km").replace(",", ".")), rs.getString("Mezzo_di_trasporto"), rs.getInt("Emissioni_g"));
						if (!tratte.contains(newTratta)) { 
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
	
	

}
