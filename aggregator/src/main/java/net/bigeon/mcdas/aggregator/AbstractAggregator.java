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

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

import net.bigeon.mcdas.data.DataPoint;

/** Abstract implementation of the aggregator.
 *
 * @author Emmanuel Bigeon */
public abstract class AbstractAggregator implements IAggregator {
    private static final long       MINUTE    = 60_000;
    Collection<Consumer<DataPoint>> consumers = new HashSet<>();
    private boolean                 running   = true;
    private final Object            lock      = new Object();

    /* (non-Javadoc)
     * @see
     * net.bigeon.mcdas.aggregator.IAggregator#add(java.util.function.Consumer) */
    @Override
    public final boolean add(Consumer<DataPoint> e) {
        return consumers.add(e);
    }

    /** Actually start the aggregator */
    protected abstract void doStart();

    /** Stop the aggregator on exit. */
    protected abstract void doStop();

    /** Test the running status
     * 
     * @return if the aggregator is running and should continue. */
    public final boolean isRunning() {
        return running;
    }

    /** Publish an aggregated point
     * 
     * @param point */
    protected final void publish(DataPoint point) {
        consumers.forEach((Consumer<DataPoint> c) -> c.accept(point));
    }

    /* (non-Javadoc)
     * @see
     * net.bigeon.mcdas.aggregator.IAggregator#remove(java.util.function.Consumer) */
    @Override
    public final boolean remove(Consumer<DataPoint> o) {
        return consumers.remove(o);
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run() */
    @Override
    public void run() {
        doStart();
        synchronized (lock) {
            while (running) {
                try {
                    lock.wait(MINUTE);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        doStop();
    }

    /* (non-Javadoc)
     * @see net.bigeon.mcdas.aggregator.IAggregator#stop() */
    @Override
    public final void stop() {
        synchronized (lock) {
            this.running = false;
            lock.notifyAll();
        }
    }
}
