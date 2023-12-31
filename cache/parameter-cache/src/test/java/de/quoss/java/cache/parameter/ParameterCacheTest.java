package de.quoss.java.cache.parameter;

import java.sql.Connection;
import java.util.List;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParameterCacheTest {

    @Test
    void testParameter() throws Exception {
        final JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        try (final Connection connection = dataSource.getConnection()) {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS parameter (type VARCHAR(255), name VARCHAR(255), data0 VARCHAR(255), data1 VARCHAR(255), data2 VARCHAR(255), data3 VARCHAR(255))");
            connection.createStatement().execute("INSERT INTO parameter (type, name, data0, data1, data2, data3) VALUES ('type', 'name', 'data0', 'data1', 'data2', 'data3')");
        }
        try (final ParameterCache cache = new ParameterCache(120000L, 600000L, dataSource, 20L, 100L)) {
            final Parameter parameter = cache.get("type", "name");
            Assertions.assertNotNull(parameter);
        }
    }
    
    @Test
    void testList() throws Exception {
        final JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        try (final Connection connection = dataSource.getConnection()) {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS parameter (type VARCHAR(255), name VARCHAR(255), data0 VARCHAR(255), data1 VARCHAR(255), data2 VARCHAR(255), data3 VARCHAR(255))");
            connection.createStatement().execute("INSERT INTO parameter (type, name, data0, data1, data2, data3) VALUES ('type', 'name', 'data0', 'data1', 'data2', 'data3')");
        }
        try (final ParameterCache cache = new ParameterCache(120000L, 600000L, dataSource, 20L, 100L)) {
            final List<Parameter> list = cache.getList("type");
            Assertions.assertFalse(list.isEmpty());
        }
    }
    
}
