package de.quoss.java.cache.parameter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.sql.DataSource;

public class ParameterCache implements AutoCloseable {

    private static final Map<String, CacheMap> CACHE = new HashMap<>();
    
    private final long listTimeToLive; 

    private final long timeToLive;

    private final Connection connection;

    private final PreparedStatement listStatement;

    private final PreparedStatement parameterStatement;

    private final long fetchListTimeoutMillis;

    private final long fetchTimeoutMillis;

    private static final String PARAMETER_SELECT = "SELECT data0, data1, data2, data3 FROM parameter WHERE type = ? AND name = ?";

    private static final String TYPE_SELECT = "SELECT * FROM parameter WHERE type = ?";

    public ParameterCache(final long timeToLive, final long listTimeToLive, final DataSource dataSource, final long fetchListTimeoutMillis,
            final long fetchTimeoutMillis) {
        if (timeToLive < 0) {
            throw new IllegalArgumentException("Time to live must be greater than or equal to 0.");
        }
        if (listTimeToLive < 0) {
            throw new IllegalArgumentException("List time to live must be greater than or equal to 0.");
        }
        if (fetchListTimeoutMillis < 1) {
            throw new IllegalArgumentException("Fetch List timeout in milliseconds must be greater than 0.");
        }
        this.timeToLive = timeToLive;
        this.listTimeToLive = listTimeToLive;
        Objects.requireNonNull(dataSource, "Data source must not be null.");
        this.fetchListTimeoutMillis = fetchListTimeoutMillis;
        this.fetchTimeoutMillis = fetchTimeoutMillis;
        try {
            connection = dataSource.getConnection();
            listStatement = connection.prepareStatement(TYPE_SELECT);
            parameterStatement = connection.prepareStatement(PARAMETER_SELECT);
        } catch (final SQLException e) {
            throw new ParameterCacheException("Error while getting connection from data source.", e);
        }
    }

    /**
     * Returns all parameters of the given type.
     *
     * @param type the type of the parameters
     * @return list of all parameters of the given type
     */
    public List<Parameter> getList(final String type) {
        if (CACHE.containsKey(type)) {
            if (CACHE.get(type).isExpired(listTimeToLive)) {
                CACHE.put(type, createCacheMap(type));
            }
        } else {
            CACHE.put(type, createCacheMap(type));
        }
        if (CACHE.get(type).getMap().isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(mapToList(CACHE.get(type).getMap()));
    }

    public Parameter get(final String type, final String name) {
        if (CACHE.containsKey(type)) {
            if (CACHE.get(type).getMap().get(name) == null) {
                CACHE.get(type).getMap().put(name, createCacheEntry(type, name));
            } else {
                final CacheEntry entry = CACHE.get(type).getMap().get(name);
                if (entry.isExpired(timeToLive)) {
                    CACHE.get(type).getMap().put(name, createCacheEntry(type, name));
                }
            }
        } else {
            CACHE.put(type, new CacheMap());
            CACHE.get(type).getMap().put(name, createCacheEntry(type, name));
        }
        return CACHE.get(type).getMap().get(name).getParameter();
    }

    private List<Parameter> mapToList(final Map<String, CacheEntry> map) {
        return map.values().stream().map(CacheEntry::getParameter).collect(LinkedList::new, List::add, List::addAll);
    }

    private CacheEntry createCacheEntry(final String type, final String name) {
        try {
            parameterStatement.setString(1, type);
            parameterStatement.setString(2, name);
            Future<Parameter> future = Executors.newSingleThreadExecutor().submit(new SelectParameter(type, name));
            Parameter parameter;
            try {
                parameter = future.get(fetchTimeoutMillis, TimeUnit.MILLISECONDS);
            } catch (final TimeoutException e) {
                // cancel future and query
                parameterStatement.cancel();
                future.cancel(true);
                parameter = null;
                // TODO signal stale data to the caller (introducing cache result type)
            } catch (final Exception e) {
                throw new ParameterCacheException("Error while creating cache map for type " + type, e);
            }
            return new CacheEntry(name, parameter);
        } catch (final SQLException e) {
            throw new ParameterCacheException("Error while creating cache map for type " + type, e);
        }
    }

    private CacheMap createCacheMap(final String type) {
        final CacheMap result = new CacheMap();
        try {
            listStatement.setString(1, type);
            Future<Map<String, CacheEntry>> future = Executors.newSingleThreadExecutor().submit(new SelectList(type));
            try {
                result.getMap().putAll(future.get(fetchListTimeoutMillis, TimeUnit.MILLISECONDS));
            } catch (final TimeoutException e) {
                // cancel future and query
                listStatement.cancel();
                future.cancel(true);
                // TODO signal stale data to the caller (introducing cache result type)
            } catch (final Exception e) {
                throw new ParameterCacheException("Error selecting list for type " + type + ".", e);
            }
        } catch (final SQLException e) {
            throw new ParameterCacheException("Error while creating cache map for type " + type, e);
        }
        return result;
    }

    private class SelectParameter implements Callable<Parameter> {

        private final String type;

        private final String name;

        public SelectParameter(final String type, final String name) {
            this.type = type;
            this.name = name;
        }

        @Override
        public Parameter call() {
            try {
                if (parameterStatement.execute()) {
                    final ResultSet resultSet = parameterStatement.getResultSet();
                    Parameter parameter = null;
                    if (resultSet.next()) {
                        parameter = new Parameter(type, name, resultSet.getString("data0"), resultSet.getString("data1"),
                            resultSet.getString("data2"),  resultSet.getString("data3"));
                    }
                    if (resultSet.next()) {
                        throw new ParameterCacheException("More than one parameter " + name + " of type " + type + " found.");
                    }
                    return parameter;
                } else {
                    throw new ParameterCacheException("Wrong statement (no select).");
                }
            } catch (final SQLException e) {
                throw new ParameterCacheException("Error while fetching parameter " + name + " of type " + type, e);
            }
        }
    }

    private class SelectList implements Callable<Map<String, CacheEntry>> {

        private final String type;

        public SelectList(final String type) {
            this.type = type;
        }

        @Override
        public Map<String, CacheEntry> call() {
            Map<String, CacheEntry> result = new HashMap<>();
            try{
                if (listStatement.execute()) {
                    final ResultSet resultSet = listStatement.getResultSet();
                    while (resultSet.next()) {
                        final String name = resultSet.getString("name");
                        result.put(name, new CacheEntry(type, new Parameter(type, name,
                                resultSet.getString("data0"), resultSet.getString("data1"),
                                resultSet.getString("data2"), resultSet.getString("data3"))));
                    }
                } else {
                    throw new ParameterCacheException("No parameters found for type " + type);
                }
            } catch (final SQLException e) {
                throw new ParameterCacheException("Error while creating cache for type " + type, e);
            }
            if (result.isEmpty()) {
                return Collections.emptyMap();
            }
            return Collections.unmodifiableMap(result);
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
            if (listStatement != null) {
                listStatement.close();
            }
            if (parameterStatement != null) {
                parameterStatement.close();
            }
        } catch (final SQLException e) {
            throw new ParameterCacheException("Error while closing connection and statements.", e);
        }
    }

}
