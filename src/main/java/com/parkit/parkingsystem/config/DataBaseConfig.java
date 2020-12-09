package com.parkit.parkingsystem.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to open and close connection.
 * 
 * @author JohnDWorld
 *
 */
public class DataBaseConfig {

	private static final Logger logger = LogManager.getLogger("DataBaseConfig");
	private static final String CREDENTIALS_SECURITY = "src/main/resources/SQL_credentials_security.properties";
	private String url;
	private String userName;
	private String password;

	/**
	 * Method to open MySQL DB connection.
	 * 
	 * @return DriverManager to get the connection
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		logger.info("Create DB connection");
		Class.forName("com.mysql.cj.jdbc.Driver");
		try (InputStream is = new FileInputStream(CREDENTIALS_SECURITY)) {
			Properties properties = new Properties();
			properties.load(is);
			url = properties.getProperty("url");
			userName = properties.getProperty("userName");
			password = properties.getProperty("password");
		} catch (FileNotFoundException fnf) {
			logger.error("File not found. Please verify credentials_file access root.", fnf);
		} catch (IOException ioex) {
			logger.error("Error during DB connection. Please check the contents file.", ioex);
		}
		return DriverManager.getConnection(url, userName, password);
	}

	/**
	 * Method to close MySQL DB connection.
	 * 
	 * @param con
	 */
	public void closeConnection(final Connection con) {
		if (con != null) {
			try {
				con.close();
				logger.info("Closing DB connection");
			} catch (SQLException e) {
				logger.error("Error while closing connection", e);
			}
		}
	}

	/**
	 * Method to close PreparedStatement.
	 * 
	 * @param ps
	 */
	public void closePreparedStatement(final PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
				logger.info("Closing Prepared Statement");
			} catch (SQLException e) {
				logger.error("Error while closing prepared statement", e);
			}
		}
	}

	/**
	 * Method to close ResultSet.
	 * 
	 * @param rs
	 */
	public void closeResultSet(final ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
				logger.info("Closing Result Set");
			} catch (SQLException e) {
				logger.error("Error while closing result set", e);
			}
		}
	}

}
