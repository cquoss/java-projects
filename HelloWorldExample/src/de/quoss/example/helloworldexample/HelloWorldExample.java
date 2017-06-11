package de.quoss.example.helloworldexample;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * example of logging 'Hello, World!'
 * 
 * @author Clemens Quoss
 *
 */
class HelloWorldExample {

	/**
	 * class name
	 */
	private static final String CLASS_NAME = HelloWorldExample.class.getName();

	/**
	 * logger
	 */
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

	/**
	 * private working constructor
	 */
	private HelloWorldExample() {

		// call super
		super();

		// log message
		LOGGER.log(Level.INFO, "Hello, World!");

	}

	/**
	 * main method
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		
		// instantiate private working constructor
		new HelloWorldExample();
		
	}

}
