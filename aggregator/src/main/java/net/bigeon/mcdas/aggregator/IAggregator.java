package net.bigeon.mcdas.aggregator;

import java.util.function.Consumer;

import net.bigeon.mcdas.data.DataPoint;

/** Interface of the agregation functions.
 *
 * @author Emmanuel Bigeon */
public interface IAggregator extends Runnable {

    /** Add a consumer for the aggregated value
     *
     * @param e the consumer
     * @return if the element was added */
    boolean add(Consumer<DataPoint> e);

    /** Remove a consumer of aggregated values
     *
     * @param o the consumer
     * @return if the element was removed */
    boolean remove(Consumer<DataPoint> o);

    /** Stop the aggregation through this aggregator. */
    void stop();
}
