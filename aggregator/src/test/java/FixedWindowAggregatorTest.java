import net.bigeon.mcdas.aggregator.AggregationOperations;
import net.bigeon.mcdas.aggregator.FixedWindowAggregator;
import net.bigeon.mcdas.aggregator.IAggregator;
import net.bigeon.mcdas.aggregator.SingleCache;
import net.bigeon.mcdas.aggregator.mqtt.MQTTServer;
import net.bigeon.mcdas.cgmu.CGMUCacheMonitor;
import net.bigeon.mcdas.data.DataPoint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDateTime;

@RunWith(JUnit4.class)
public class FixedWindowAggregatorTest {

    @Test
    public void FixedWindowMaxAggregatorTest() {
        int periods = 60;
        MQTTServer server = new MQTTServer();
        CGMUCacheMonitor caches = new CGMUCacheMonitor();
        SingleCache cache = caches.get("test");

        FixedWindowAggregator windowAggregator = new FixedWindowAggregator(
                cache, periods, AggregationOperations::SDevAggregator);

        windowAggregator.add((DataPoint pt)->server.publish("test", pt));
    }
}
