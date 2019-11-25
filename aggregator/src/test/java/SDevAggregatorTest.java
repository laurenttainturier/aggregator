import net.bigeon.mcdas.aggregator.AggregationOperations;
import net.bigeon.mcdas.data.DataPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;


@RunWith(JUnit4.class)
public class SDevAggregatorTest {
    private Function<DataPoint, Double> operation;

    @Before
    public void operationInit() {
        Supplier<Function<DataPoint, Double>> supplier = AggregationOperations::SDevAggregator;
        operation = supplier.get();
    }

    @Test
    public void SVDAggregationTest1() {
        DataPoint pt1 = new DataPoint(LocalDateTime.now(), 1, 2);
        DataPoint pt2 = new DataPoint(LocalDateTime.now(), 1, 4);

        assertEquals((Double) 0.0, operation.apply(pt1));
        assertEquals((Double) 1.0, operation.apply(pt2));

        /*
        mean        2   3
        squaredSum  4   20
        value       0   1
        */
    }
}
