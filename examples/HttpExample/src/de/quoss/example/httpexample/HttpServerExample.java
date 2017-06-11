package de.quoss.example.httpexample;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Properties;
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
	 * properties
	 */
	private Properties properties = new Properties();

	/**
	 * private working constructor
	 * 
	 * @throws HttpExampleException
	 *             in case of error
	 */
	private HttpServerExample() throws HttpExampleException {

		// start message
		LOGGER.log(Level.INFO, "start");

		// try to load properties
		try {
			String propertiesFileName = CLASS_NAME.concat(".properties");
			FileInputStream fileInputStream = new FileInputStream(propertiesFileName);
			properties.load(fileInputStream);
		} catch (IOException e) {
			throw new HttpExampleException(e);
		}

		// try to create http server
		HttpServer httpServer = null;
		int port = Integer.parseInt(getProperty("port", "8080"));
		LOGGER.log(Level.INFO, "Starting http server on port: {0}", new Object[] { port });
		try {
			httpServer = HttpServer.create(new InetSocketAddress(port), 0);
		} catch (IOException e) {
			throw new HttpExampleException(e);
		}

		// set root context with example http handler
		String context = getProperty("context", "/");
		LOGGER.log(Level.INFO, "Using context: {0}", new Object[] { context });
		httpServer.createContext(context, new HttpHandlerExample());

		// start server
		httpServer.start();

		// end message
		LOGGER.log(Level.INFO, "end");

	}

	/**
	 * get property with default value
	 * 
	 * @param key
	 *            key to property
	 * @param defaultValue
	 *            default value
	 * @return value or if null, default value
	 */
	private String getProperty(String key, String defaultValue) {

		// build full key
		String fullKey = CLASS_NAME.concat(".").concat(key);

		// get value from properties
		String value = properties.getProperty(fullKey);

		// check whether to return value or default value
		if (value == null) {
			return defaultValue;
		} else {
			return value;
		}

	}

	/**
	 * 
	 * class to handle the http requests
	 * 
	 * @author Clemens Quoss
	 *
	 */
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
