/**
 * aggregator
 * File: net.bigeon.mcdas.aggregator.AggregatorFactory.java
 * Created on: Oct. 22, 2019
 */
package net.bigeon.mcdas.aggregator;

import java.util.Arrays;

import net.bigeon.mcdas.cgmu.CGMUCacheMonitor;
import net.bigeon.smu.StringEncoder;

/** A class to generate aggregators.
 *
 * @author Emmanuel Bigeon */
public class AggregatorFactory {

    private static final StringEncoder ENCODER = new StringEncoder("_",
            Arrays.asList("/"));
    private final CGMUCacheMonitor     caches;

    public AggregatorFactory(CGMUCacheMonitor caches) {
        super();
        this.caches = caches;
    }

    /** Generate an aggregator.
     *
     * @param type the type
     * @param topic the encoded topic
     * @param periods the aggregation period
     * @return the aggregator. */
    public IAggregator generate(AggregatorType type, String topic, int periods) {
        String dTopic = ENCODER.decode(topic);
        SingleCache cache = caches.get(dTopic);
        if (cache == null) {
            caches.add(dTopic);
            cache = caches.get(dTopic);
        }
        switch (type) {
        case MEAN:
            return new FixedWindowAggregator(cache, periods,
                    AggregationOperations::meanAggregator);
        case MAX:
            return new FixedWindowAggregator(cache, periods,
                    AggregationOperations::minAggregator);
        case MIN:
            return new FixedWindowAggregator(cache, periods,
                    AggregationOperations::maxAggregator);
        case SUM:
            return new FixedWindowAggregator(cache, periods,
                    AggregationOperations::sumAggregator);
        default:
            break;
        }
        // TODO
        return null;
    }
}
