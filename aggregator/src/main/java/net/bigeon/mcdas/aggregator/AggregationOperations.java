/**
 * aggregator
 * File: net.bigeon.mcdas.aggregator.AggregationOperations.java
 * Created on: Oct. 22, 2019
 */
package net.bigeon.mcdas.aggregator;

import java.util.function.Function;

import net.bigeon.mcdas.data.DataPoint;

/** The aggregation typical functions.
 *
 * @author Emmanuel Bigeon */
public class AggregationOperations {

    /** @return a function that gets the highest of the values it is called with */
    public static Function<DataPoint, Double> maxAggregator() {
        return new Function<DataPoint, Double>() {
            double value = Double.NEGATIVE_INFINITY;

            @Override
            public Double apply(DataPoint t) {
                value = Math.max(value, t.value / t.duration);
                return value;
            }
        };
    }

    /** @return a function that gets the mean of the values it is called with */
    public static Function<DataPoint, Double> meanAggregator() {
        return new Function<DataPoint, Double>() {
            double value = 0.;
            int    card  = 0;

            @Override
            public Double apply(DataPoint t) {
                value = (value * card + t.value / t.duration) / ++card;
                return value;
            }
        };
    }

    /** @return a function that gets the lowest of the values it is called with */
    public static Function<DataPoint, Double> minAggregator() {
        return new Function<DataPoint, Double>() {
            double value = Double.POSITIVE_INFINITY;

            @Override
            public Double apply(DataPoint t) {
                value = Math.min(value, t.value / t.duration);
                return value;
            }
        };
    }

    /** @return a function that gets the sum of the values it is called with */
    public static Function<DataPoint, Double> sumAggregator() {
        return new Function<DataPoint, Double>() {
            double value = 0;

            @Override
            public Double apply(DataPoint t) {
                value += t.value;
                return value;
            }
        };
    }
}
