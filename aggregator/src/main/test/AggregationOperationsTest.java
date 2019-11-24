import net.bigeon.mcdas.aggregator.AggregationOperations;
import net.bigeon.mcdas.data.DataPoint;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.bigeon.mcdas.aggregator.AggregationOperations.maxAggregator;
import static org.junit.Assert.*;


@RunWith(JUnit4.class)
public class AggregationOperationsTest {
    private Function<DataPoint, Double> operation;

    @Before
    public void operationInit() {
        Supplier<Function<DataPoint, Double>> supplier = AggregationOperations::maxAggregator;
        operation = supplier.get();
        Supplier<Function<DataPoint, Double>> supplierSVD = AggregationOperations::SDevAggregator;
        operationSVD = supplierSVD.get();
        Supplier<Function<DataPoint, Double>> supplierSum = AggregationOperations::sumAggregator
        operationSum = supplierSum.get();
        Supplier<Function<DataPoint, Double>> supplierMin = AggregationOperations::minAggregator
        operationMin = supplierMin.get();
        Supplier<Function<DataPoint, Double>> supplierMean = AggregationOperations::meanAggregator
        operationMean = supplierMean.get();
    }

    @Test
    public void maxAggregationTest1() {
        DataPoint pt1 = new DataPoint(LocalDateTime.now(), 1, 2);
        DataPoint pt2 = new DataPoint(LocalDateTime.now(), 1, 1);

        assertEquals((Double) 2.0, operation.apply(pt1));
        assertEquals((Double) 2.0, operation.apply(pt2));
    }

    @Test
    public void maxAggregationTest2() {
        DataPoint pt1 = new DataPoint(LocalDateTime.now(), 4, 2);
        DataPoint pt2 = new DataPoint(LocalDateTime.now(), 1, 3);
        DataPoint pt3 = new DataPoint(LocalDateTime.now(), 5, 3);
        DataPoint pt4 = new DataPoint(LocalDateTime.now(), 10, 40);

        assertEquals((Double) .5, operation.apply(pt1));
        assertEquals((Double) 3.0, operation.apply(pt2));
        assertEquals((Double) 3.0, operation.apply(pt3));
        assertEquals((Double) 4.0, operation.apply(pt4));
    }

    @Test
    public void maxAggregationTest3() {
        DataPoint pt = new DataPoint(LocalDateTime.now(), 0, 1);

        assertEquals((Double) Double.POSITIVE_INFINITY, operation.apply(pt));
    }

    @Test
    public void SVDAggregationTest() {
        DataPoint pt1 = new DataPoint(LocalDateTime.now(), 1, 2);
        DataPoint pt2 = new DataPoint(LocalDateTime.now(), 1, 4);

        assertEquals((Double) 0.0, operationMean.apply(pt1));
        assertEquals((Double) 1.0, operationMean.apply(pt2));

        /*
        mean        2   3
        squaredSum  4   20
        value       0   1
        */
    }

    @Test
    public void SumAggregationTest1() {
        DataPoint pt1 = new DataPoint(LocalDateTime.now(), 1, 2);
        DataPoint pt2 = new DataPoint(LocalDateTime.now(), 1, 1);

        assertEquals((Double) 2.0, operationSum.apply(pt1));
        assertEquals((Double) 3.0, operationSUM.apply(pt2));
    }

    @Test
    public void MinAggregationTest1() {
        DataPoint pt1 = new DataPoint(LocalDateTime.now(), 1, 2);
        DataPoint pt2 = new DataPoint(LocalDateTime.now(), 1, 1);

        assertEquals((Double) 2.0, operationMin.apply(pt1));
        assertEquals((Double) 1.0, operationMin.apply(pt2));
    }

    @Test
    public void MeanAggregationTest1() {
        DataPoint pt1 = new DataPoint(LocalDateTime.now(), 1, 2);
        DataPoint pt2 = new DataPoint(LocalDateTime.now(), 1, 4);

        assertEquals((Double) 2.0, operationMean.apply(pt1));
        assertEquals((Double) 3.0, operationMean.apply(pt2));
    }
}
