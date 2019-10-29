package net.bigeon.mcdas.aggregator;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import net.bigeon.mcdas.data.DataPoint;

/** An aggregator with fixed windows.
 * 
 * @author Emmanuel Bigeon */
public class FixedWindowAggregator extends AbstractAggregator implements Runnable {

    private final SingleCache   cache;
    private LocalDateTime currentBegin;
    private final long          period;
    private final CacheListener updater;
    private final Supplier<Function<DataPoint, Double>> operationProvider;

    /** Create the aggregator
     * 
     * @param source the data source
     * @param duration the aggregation period
     * @param provider the aggregation function provider. */
    public FixedWindowAggregator(SingleCache source, int duration, Supplier<Function<DataPoint, Double>> provider) {
        period = duration;
        cache = source;
        updater = new CacheListener() {

            @Override
            public void addedData(Cache source) {
                // test data
                testCachedData();
            }

            @Override
            public void flushed(Cache source) {
                // ignored
            }
        };
        this.operationProvider = provider;
    }

    /* (non-Javadoc)
     * @see net.bigeon.mcdas.aggregator.AbstractAggregator#doStart()
     */
    @Override
    protected void doStart() {
        currentBegin = LocalDateTime.now();
        cache.lock(currentBegin);
        cache.addListener(updater);
    }

    /* (non-Javadoc)
     * @see net.bigeon.mcdas.aggregator.AbstractAggregator#doStop()
     */
    @Override
    protected void doStop() {
        cache.release(currentBegin);
        cache.removeListener(updater);
    }

    public void testCachedData() {
        DataPoint pt = cache.get(currentBegin);
        if (pt == null) {
            // No data yet...
            return;
        }
        LocalDateTime aCurrentBegin = pt.date;
        Function<DataPoint, Double> operation = operationProvider.get();
        // Value is the value per second
        double value = operation.apply(pt);
        LocalDateTime current = aCurrentBegin.plus(pt.duration, ChronoUnit.SECONDS);
        while ((pt = cache.get(current)) != null) {
            current = current.plus(pt.duration, ChronoUnit.SECONDS);
            value = operation.apply(pt);
            int aPeriod = (int) aCurrentBegin.until(current, ChronoUnit.SECONDS);
            if (period <= aPeriod) {
                // publish and release
                cache.lock(current);
                cache.release(currentBegin);
                publish(new DataPoint(aCurrentBegin, aPeriod, value));
                currentBegin = current;
                aCurrentBegin = current;
            }
        }
        if (pt == null) {
            // interrupted
            int aPeriod = (int) aCurrentBegin.until(current, ChronoUnit.SECONDS);
            if (period <= aPeriod) {
                // publish and release
                cache.lock(current);
                cache.release(currentBegin);
                publish(new DataPoint(aCurrentBegin, aPeriod, value));
                currentBegin = current;
            }
        }
    }
}
