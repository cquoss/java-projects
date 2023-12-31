package de.quoss.java.cache.parameter;

import java.util.HashMap;
import java.util.Map;

class CacheMap {

    private long lastAccessed;

    private final Map<String, CacheEntry> map = new HashMap<>();

    CacheMap() {
        lastAccessed = -1;
    }

    boolean isExpired(final long timeToLive) {
        return System.currentTimeMillis() - lastAccessed > timeToLive;
    }

    long getLastAccessed() {
        return lastAccessed;
    }

    void setLastAccessed(final long lastAccessed) {
        this.lastAccessed = lastAccessed;
    }
    
    Map<String, CacheEntry> getMap() {
        return map;
    }

}
