package it.polito.tdp.SimulatoreTrasportoMerce.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Citta;


public class DAO {
	
	public List<Citta> getAllCitta() {
		
		
		final String sql = "SELECT DISTINCT Partenza "
				+ "FROM tratte "
				+ "WHERE  Mezzo_di_trasporto = 'Aereo' OR Mezzo_di_trasporto = 'Autobus' OR Mezzo_di_trasporto = 'Treno' "
				+ "UNION "
				+ "SELECT DISTINCT Destinazione "
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
