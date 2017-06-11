package de.quoss.example.httpexample;

/**
 * exception for handling errors
 * 
 * @author Clemens Quoß
 *
 */
class HttpExampleException extends Exception {

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
	HttpExampleException(String msg) {

		// call super
		super(msg);

	}

	/**
	 * constructor with exception
	 * 
	 * @param e
	 *            exception
	 */
	HttpExampleException(Exception e) {

		// call super
		super(e);

	}

}
