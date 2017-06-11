package de.quoss.example.httpexample;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

class HttpServer2Example {

	private static final String CLASS_NAME = HttpServer2Example.class.getName();

	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

	private static final String REQUEST_METHOD_GET = "GET";

	private static final String REQUEST_METHOD_POST = "POST";

	private HttpServer2Example() throws Exception {
		super();
		HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
		httpServer.createContext("/test", new HttpHandlerExample());
		httpServer.start();
	}

	private class HttpHandlerExample implements HttpHandler {

		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
			String requestMethod = httpExchange.getRequestMethod();
			LOGGER.log(Level.INFO, " [requestMethod={0}]", new Object[] { requestMethod });
			Headers headers = httpExchange.getRequestHeaders();
			LOGGER.log(Level.INFO, " [headers.entrySet()={0}]", new Object[] { headers.entrySet() });
			String response;
			int responseCode;
			if (REQUEST_METHOD_GET.equals(requestMethod)) {
				Map<String, String> query = queryToMap(httpExchange.getRequestURI().getQuery());
				LOGGER.log(Level.INFO, " [query={0}]", new Object[] { query });
				response = "This is the response";
				responseCode = 200;
				httpExchange.sendResponseHeaders(200, response.length());
				OutputStream outputStream = httpExchange.getResponseBody();
				outputStream.write(response.getBytes());
				outputStream.close();
			} else if (REQUEST_METHOD_POST.equals(requestMethod)) {
				Map<String, String> query = queryToMap(httpExchange.getRequestURI().getQuery());
				LOGGER.log(Level.INFO, " [query={0}]", new Object[] { query });
				String filename = query.get("filename");
				InputStream inputStream = httpExchange.getRequestBody();
				int bytesRead;
				byte[] b = new byte[1024];
				StringBuilder stringBuilder = new StringBuilder();
				while ((bytesRead = inputStream.read(b)) != -1) {
					stringBuilder.append(new String(b).substring(0, bytesRead));
				}
				LOGGER.log(Level.INFO, " [stringBuilder={0}]", new Object[] { stringBuilder.toString() });
				if (filename != null) {
					FileWriter fileWriter = new FileWriter("output\\".concat(filename));
					fileWriter.write(stringBuilder.toString());
					fileWriter.close();
					response = String.format("File received: %s", filename);
				} else {
					response = "No file received.";
				}
				responseCode = 200;
			} else {
				LOGGER.log(Level.INFO, "not supported [requestMethod={0}]", new Object[] { requestMethod });
				response = String.format("Request method not supported: %s", requestMethod);
				responseCode = 501;
			}
			httpExchange.sendResponseHeaders(responseCode, response.length());
			OutputStream outputStream = httpExchange.getResponseBody();
			outputStream.write(response.getBytes());
			outputStream.close();
		}

		private Map<String, String> queryToMap(String query) {
			Map<String, String> result = new HashMap<String, String>();
			for (String param : query.split("&")) {
				String[] pair = param.split("=");
				if (pair.length > 1) {
					result.put(pair[0], pair[1]);
				} else {
					result.put(pair[0], "");
				}
			}
			return result;
		}

	}

	public static void main(String[] args) throws Exception {
		new HttpServer2Example();
	}

}
