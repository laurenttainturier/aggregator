/**
 * aggregator
 * File: net.bigeon.mcdas.aggregator.CacheListener.java
 * Created on: Oct. 22, 2019
 */
package net.bigeon.mcdas.aggregator;

/** A listener for a cache.
 * <p>
 * Cache listeners will be notified of new data as well as flushes of the cache.
 *
 * @author Emmanuel Bigeon */
public interface CacheListener {
    /** Indicate new data in the cache
     * 
     * @param source the cache source */
    void addedData(Cache source);

    /** The new data source.
     * 
     * @param source */
    void flushed(Cache source);
}
