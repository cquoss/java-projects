package de.quoss.example.h2example;

import java.io.FileReader;
import java.io.IOException;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * example how to use the h2 database
 * 
 * @author Clemens Quoss
 *
 */
public class H2Example {

	/**
	 * class name
	 */
	private static final String CLASS_NAME = H2Example.class.getName();

	/**
	 * log
	 */
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

	/**
	 * properties
	 */
	private Properties properties = new Properties();

	/**
	 * driver class name
	 */
	private String driverClassName = null;

	/**
	 * connection
	 */
	private Connection connection = null;

	/**
	 * user
	 */
	private String user = null;

	/**
	 * password
	 */
	private String password = null;

	/**
	 * url
	 */
	private String url = null;

	/**
	 * statement
	 */
	private Statement statement = null;

	/**
	 * private working constructor
	 */
	private H2Example() throws H2ExampleException {

		// call super
		super();

		// start message
		LOGGER.log(Level.INFO, "start");

		// load properties
		try {
			properties.load(new FileReader(CLASS_NAME.concat(".properties")));
		} catch (IOException e) {
			throw new H2ExampleException(e);
		}

		// load driver
		driverClassName = getProperty("driverClassName", null);
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			throw new H2ExampleException(e);
		}

		// get connection
		url = getProperty("url", null);
		user = getProperty("user", null);
		password = getProperty("password", null);
		try {
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			throw new H2ExampleException(e);
		}

		// create statement
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			throw new H2ExampleException(e);
		}

		// create table
		String tableName = "user";
		String sql = String.format(
				"CREATE TABLE %s ( id DECIMAL(9,0) PRIMARY KEY, name VARCHAR(255), christian_name VARCHAR(255) )", tableName);
		try {
			statement.execute(sql);
		} catch (SQLException e) {
			int errorCode = e.getErrorCode();
			// LOGGER.log(Level.INFO, "[errorCode={0,number,#}]", new Object[] {
			// errorCode});
			if (errorCode == 42101) {
				LOGGER.log(Level.INFO, "Table {0} already exists", tableName);
			} else {
				throw new H2ExampleException(e);
			}
		}

		// insert row into table
		sql = "INSERT INTO user VALUES ( 0., 'Quoﬂ', 'Clemens' )";
		try {
			statement.execute(sql);
		} catch (SQLException e) {
			throw new H2ExampleException(e);
		}

		// try to insert again
		sql = "INSERT INTO user VALUES ( 0., 'Quoﬂ', 'Clemens' )";
		try {
			statement.execute(sql);
		} catch (SQLException e) {
			throw new H2ExampleException(e);
		}

		// drop table
		tableName = "user";
		sql = String.format("DROP TABLE %s", tableName);
		try {
			statement.execute(sql);
		} catch (SQLException e) {
			throw new H2ExampleException(e);
		}

		// close statement
		try {
			statement.close();
		} catch (SQLException e) {
			throw new H2ExampleException(e);
		}

		// close connection
		try {
			connection.close();
		} catch (SQLException e) {
			throw new H2ExampleException(e);
		}

		// end message
		LOGGER.log(Level.INFO, "end");

	}

	private String getProperty(String key, String defaultValue) throws H2ExampleException {

		String fullKey = CLASS_NAME.concat(".").concat(key);
		String value = properties.getProperty(fullKey, defaultValue);
		if (value == null) {
			throw new H2ExampleException("Mandatory property not provided: ".concat(fullKey));
		}
		return value;
	}

	/**
	 * main method
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {

		// start message
		LOGGER.log(Level.INFO, "start");

		try {

			// call working constructor
			new H2Example();

		} catch (H2ExampleException e) {

			// log exception
			LOGGER.log(Level.SEVERE, "", e);

			// return with error
			System.exit(1);

		}

		// end message
		LOGGER.log(Level.INFO, "end");

	}

}
