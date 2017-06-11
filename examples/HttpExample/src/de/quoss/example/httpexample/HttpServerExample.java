package de.quoss.example.httpexample;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * <p>
 * example of a server handling http requests
 * </p>
 * 
 * <p>
 * TODO integrate server 2 example
 * </p>
 *
 * @author Clemens Quoss
 *
 */
class HttpServerExample {

	/**
	 * class name
	 */
	private static final String CLASS_NAME = HttpServerExample.class.getName();

	/**
	 * logger
	 */
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

	/**
	 * private working constructor
	 * 
	 * @throws HttpExampleException
	 *             in case of error
	 */
	private HttpServerExample() throws HttpExampleException {

		// start message
		LOGGER.log(Level.INFO, "start");

		// try to create http server
		HttpServer httpServer = null;
		try {
			httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
		} catch (IOException e) {
			throw new HttpExampleException(e);
		}

		// set root context with example http handler
		httpServer.createContext("/", new HttpHandlerExample());

		// start server
		httpServer.start();

		// end message
		LOGGER.log(Level.INFO, "end");

	}

	private class HttpHandlerExample implements HttpHandler {

		/**
		 * handle http request
		 */
		public void handle(HttpExchange httpExchange) throws IOException {

			// inspect and log request
			String requestMethod = httpExchange.getRequestMethod();
			LOGGER.log(Level.INFO, "Request method: {0}", new Object[] { requestMethod });

			// format response
			String response = String.format("Request with method %s received", requestMethod);
			httpExchange.sendResponseHeaders(200, response.length());
			OutputStream outputStream = httpExchange.getResponseBody();
			outputStream.write(response.getBytes());
			outputStream.close();

		}

	}

	/**
	 * main method
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {

		// try calling private working constructor
		try {
			new HttpServerExample();
		} catch (HttpExampleException e) {
			LOGGER.log(Level.SEVERE, "", e);
			System.exit(1);
		}

	}

}
