package net.bigeon.mcdas.cgmu;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.bigeon.mcdas.aggregator.SingleCache;

public class CGMUCacheMonitor {

    private Map<String, SingleCache> caches = new ConcurrentHashMap<>();

    public SingleCache get(String key) {
        return caches.get(key);
    }

    /** Add a cache for the given topic (if it exists).
     * 
     * @param topic */
    public void add(String topic) {
        if (caches.containsKey(topic))
            return;
        caches.put(topic, new SingleCache());
    }
}
