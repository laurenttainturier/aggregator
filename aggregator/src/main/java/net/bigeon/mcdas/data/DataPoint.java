package net.bigeon.mcdas.data;

import java.time.LocalDateTime;

/** Definition of a data point in real time data
 * 
 * @author Emmanuel Bigeon */
public class DataPoint {
    /** Date of beginning of validity of the data */
    public final LocalDateTime date;
    /** Duration (in seconds) of the data validity period */
    public final int           duration;
    /** Actual value of the data. */
    public final double        value;

    /** Create the data
     * 
     * @param date starting date
     * @param duration validity span
     * @param value the value */
    public DataPoint(LocalDateTime date, int duration, double value) {
        super();
        this.date = date;
        this.duration = duration;
        this.value = value;
    }
}
