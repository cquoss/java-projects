package de.quoss.java.cache.parameter;

public class H2SleepFunction {
    
    public void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

}
