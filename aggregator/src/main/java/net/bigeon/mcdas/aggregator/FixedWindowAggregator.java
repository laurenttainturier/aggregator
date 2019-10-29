package net.bigeon.mcdas.aggregator;

/*-
 * #%L
 * aggregator
 * %%
 * Copyright (C) 2019 Bigeon
 * %%
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 * #L%
 */

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
