package net.bigeon.mcdas.admin;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.bigeon.mcdas.aggregator.AggregatorFactory;
import net.bigeon.mcdas.aggregator.AggregatorType;
import net.bigeon.mcdas.aggregator.IAggregator;
import net.bigeon.mcdas.aggregator.mqtt.MQTTServer;
import net.bigeon.mcdas.data.DataPoint;

public class AggregatorManager {
    private MQTTServer server;
    private final Map<String, IAggregator> aggregators = new HashMap<>();
    private AggregatorFactory        factory;
    
    public AggregatorManager(MQTTServer server, AggregatorFactory factory) {
        super();
        this.server = server;
        this.factory = factory;
    }

    /** Delete an aggregator.
     * <p>
     * Deleting an aggregator consists in removing it from this monitor after
     * requesting it to stop.
     * 
     * @param id the aggregator identifier
     * @return if an aggregator has been deleted */
    public synchronized boolean delete(String id) {
        IAggregator remove = aggregators.remove(id);
        remove.stop();
        return remove != null;
    }

    /** Create a new aggregator and register it
     * 
     * @param id the register key
     * @param type the aggregator type
     * @param topic the topic to aggregate
     * @param periods the length of aggregation in 15 seconds periods
     * @return if an aggregator was created and registered. */
    public synchronized boolean add(String id, AggregatorType type, String topic, int periods) {
        if (aggregators.containsKey(id)) {
            return false;
        }
        final IAggregator generate = factory.generate(type, topic, periods);
        if (generate == null)
            return false;
        generate.add((DataPoint pt)->server.publish(id, pt));
        aggregators.put(id, generate);
        Thread runner = new Thread(generate, id);
        runner.start();
        return true;
    }
    
    public Set<String> list() {
        return aggregators.keySet();
    }
}
