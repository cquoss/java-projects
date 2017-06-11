package de.quoss.example.h2example;

/**
 * class for handling errors in example
 * 
 * @author Clemens Quoss
 *
 */
class H2ExampleException extends Exception {

	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructor with message
	 * 
	 * @param msg
	 *            message
	 */
	H2ExampleException(String msg) {

		// call super
		super(msg);

	}

	/**
	 * constructor with exception
	 * 
	 * @param e
	 *            exception
	 */
	H2ExampleException(Exception e) {

		// call super
		super(e);

	}

}
