package net.bigeon.mcdas.aggregator;

import java.time.LocalDateTime;

import net.bigeon.mcdas.data.DataPoint;

/** Interface for data sources.
 *
 * @author Emmanuel Bigeon */
public interface DataSource {

    /** Get the first data point starting after this date. The validity of the value
     * is given by the duration field of the data point
     *
     * @param date the date to get data at
     * @return the data point */
    DataPoint get(LocalDateTime date);
}
