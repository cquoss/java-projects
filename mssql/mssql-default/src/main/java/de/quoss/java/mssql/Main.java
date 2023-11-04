package de.quoss.java.mssql;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String AUTHENTICATION = "ActiveDirectoryPassword";

    private static final String DATABASE_NAME = "sqldb-ladezonecrm-dev";

    private static final String PASSWORD = ";U#}4kg+";

    private static final int PORT_NUMBER = 1433;
    
    private static final String SERVER_NAME = "db323b465c.database.windows.net";
    
    private static final String USER = "COSVC-EE07D2B@unioninvestment.onmicrosoft.com";
    
    // FIXME url geht so nicht (user / password invalid)
    private static final String URL = String.format("jdbc:sqlserver://%s:%s;"
            + "databaseName=%s;authentication=%s;user=%s;password=%s",
            SERVER_NAME, PORT_NUMBER, DATABASE_NAME, AUTHENTICATION, USER,
            URLEncoder.encode(PASSWORD, StandardCharsets.UTF_8));
    
    private void run() throws SQLException {
        System.setProperty("java.util.logging.config.file", "logging.properties");
        final File file = new File("test.txt");
        if (file.isFile()) {
            LOGGER.info("File {} does exist.", file.getAbsolutePath());
        } else {
            LOGGER.info("File {} does not exist.", file.getAbsolutePath());
        }
        LOGGER.info("Start");
        LOGGER.trace("TRACE enabled.");
        LOGGER.debug("DEBUG enabled.");
        LOGGER.info("INFO enabled.");
        LOGGER.warn("WARN enabled.");
        LOGGER.error("ERROR enabled.");
        System.setProperty("https.proxyHost", "cproxy.intern.union-investment.de");
        System.setProperty("https.proxyPort", "8080");
        System.setProperty("http.proxyUser", "quossc");
        System.setProperty("http.proxyPassword", "SetCl#46ProgName");
        final SQLServerDataSource dataSource = new SQLServerDataSource();
        dataSource.setAuthentication(AUTHENTICATION);
        dataSource.setDatabaseName(DATABASE_NAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setPortNumber(PORT_NUMBER);
        dataSource.setServerName(SERVER_NAME);
        dataSource.setUser(USER);
        // FIXME siehe oben bei URL
        // try (final Connection connection = DriverManager.getConnection(URL)) {
        try (final Connection connection = dataSource.getConnection()) {
            LOGGER.info("run() [connection={}]", connection);
        }
        LOGGER.info("End");
    }
    
    public static void main(final String[] args) throws SQLException {
        new Main().run();
    }
    
}
