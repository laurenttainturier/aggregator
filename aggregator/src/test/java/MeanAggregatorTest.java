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
public class MeanAggregatorTest {
    private Function<DataPoint, Double> operation;

    @Before
    public void operationInit() {
        Supplier<Function<DataPoint, Double>> supplier = AggregationOperations::meanAggregator;
        operation = supplier.get();
    }

    @Test
    public void MeanAggregationTest1() {
        DataPoint pt1 = new DataPoint(LocalDateTime.now(), 1, 2);
        DataPoint pt2 = new DataPoint(LocalDateTime.now(), 1, 4);

        assertEquals((Double) 2.0, operation.apply(pt1));
        assertEquals((Double) 3.0, operation.apply(pt2));
    }
}
