package net.bigeon.mcdas.admin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.bigeon.mcdas.aggregator.AggregatorFactory;
import net.bigeon.mcdas.aggregator.AggregatorType;
import net.bigeon.mcdas.aggregator.IAggregator;
import net.bigeon.mcdas.aggregator.mqtt.MQTTServer;
import net.bigeon.mcdas.data.DataPoint;

public class AggregatorManager {
    private MQTTServer server;
    private final Map<String, IAggregator> aggregators = new HashMap<>();
    private AggregatorFactory        factory;
    
    public AggregatorManager(MQTTServer server, AggregatorFactory factory) {
        super();
        this.server = server;
        this.factory = factory;
    }

    /** Delete an aggregator.
     * <p>
     * Deleting an aggregator consists in removing it from this monitor after
     * requesting it to stop.
     * 
     * @param id the aggregator identifier
     * @return if an aggregator has been deleted */
    public synchronized boolean delete(String id) {
        IAggregator remove = aggregators.remove(id);
        remove.stop();
        return remove != null;
    }

    /** Create a new aggregator and register it
     * 
     * @param id the register key
     * @param type the aggregator type
     * @param topic the topic to aggregate
     * @param periods the length of aggregation in 15 seconds periods
     * @return if an aggregator was created and registered. */
    public synchronized boolean add(String id, AggregatorType type, String topic, int periods) {
        if (aggregators.containsKey(id)) {
            return false;
        }
        final IAggregator generate = factory.generate(type, topic, periods);
        if (generate == null)
            return false;
        generate.add((DataPoint pt)->server.publish(id, pt));
        aggregators.put(id, generate);
        Thread runner = new Thread(generate, id);
        runner.start();
        return true;
    }
    
    public Set<String> list() {
        return aggregators.keySet();
    }
}
