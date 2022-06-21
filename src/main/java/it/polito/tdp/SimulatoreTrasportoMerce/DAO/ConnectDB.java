package it.polito.tdp.SimulatoreTrasportoMerce.DAO;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;

public class ConnectDB {

	// connessione modificat con utilizzo del data source per non aprire e chiudere
	// continuamente la connesione al DBMS

	static private final String jdbcURL = "jdbc:mysql://localhost/tesi?user=root&password=4826";
	static private HikariDataSource ds = null;

	public static Connection getConnection() throws SQLException {

		if (ds == null) {
			ds = new HikariDataSource();
			ds.setJdbcUrl(jdbcURL);
			ds.setPoolName("Pool-DB");
			ds.setMaximumPoolSize(100);
		}

		try {
			Connection connection = ds.getConnection();
			return connection;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot get a connection " + jdbcURL, e);
		}

	}

}