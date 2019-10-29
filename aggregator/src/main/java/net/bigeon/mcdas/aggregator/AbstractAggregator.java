package net.bigeon.mcdas.aggregator;

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
