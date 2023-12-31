package de.quoss.java.cache.parameter;

class CacheEntry {

    private final String name; 

    private final long lastAccessed;

    private final Parameter parameter;

    public CacheEntry(final String name, final Parameter parameter) {
        this.name = name;
        this.lastAccessed = System.currentTimeMillis();
        this.parameter = parameter;
    }
    
    String getName() {
        return name;
    }

    long getLastAccessed() {
        return lastAccessed;
    }

    Parameter getParameter() {
        return parameter;
    }

    boolean isExpired(final long timeToLive) {
        return System.currentTimeMillis() - lastAccessed > timeToLive;
    }

}
