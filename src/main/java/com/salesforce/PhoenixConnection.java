package com.salesforce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Phoenix Connection
 * @author mchohan
 *
 */
class PhoenixConnection {

	private static String CONNECTION_STRING;

	public static void setConnectionString(String connectionString) {
		CONNECTION_STRING = connectionString;
	}
	
	protected static Connection getConnection() throws SQLException {
		Properties prop = new Properties();
		return DriverManager.getConnection(CONNECTION_STRING, prop);
	}
}
