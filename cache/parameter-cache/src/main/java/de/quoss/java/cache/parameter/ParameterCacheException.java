package de.quoss.java.cache.parameter;

public class ParameterCacheException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public ParameterCacheException() {
        super();
    }
    
    public ParameterCacheException(final String message) {
        super(message);
    }

    public ParameterCacheException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ParameterCacheException(final Throwable cause) {
        super(cause);
    }

}
