/**
 * aggregator
 * File: net.bigeon.mcdas.aggregator.AggregationOperations.java
 * Created on: Oct. 22, 2019
 */
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

    /** @return a function that gets the standard deviation of the values it is called with */
    public static Function<DataPoint, Double> SDevAggregator() {
            return new Function<DataPoint, Double>() {
                double value = 0.;
                int    card  = 0;
                double mean = 0.;
                double squaredSum = 0.;


                @Override
                public Double apply(DataPoint t) {
                    mean = (mean * card + t.value / t.duration) / ++card;
                    squaredSum += t.value * t.value;
                    value = Math.sqrt((squaredSum / card) - (mean * mean));
                    return value;
                }
            };
    }
}
