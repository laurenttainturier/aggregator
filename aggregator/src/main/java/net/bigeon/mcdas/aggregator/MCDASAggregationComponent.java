/**
 * aggregator
 * File: net.bigeon.mcdas.aggregator.MCDASAggregationComponent.java
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

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import net.bigeon.mcdas.admin.AggregatorManager;
import net.bigeon.mcdas.aggregator.mqtt.MQTTServer;
import net.bigeon.mcdas.aggregator.mqtt.MQTTSource;
import net.bigeon.mcdas.cgmu.CGMUCacheMonitor;

/** The aggregation component backend.
 * 
 * @author Emmanuel Bigeon */
public class MCDASAggregationComponent {

    private final CGMUCacheMonitor  cacheMonitor;
    private final MqttClient        client;
    private final MQTTServer        server;
    private final AggregatorFactory factory;
    private final AggregatorManager manager;

    /** Create the component
     *
     * @throws MqttException if the connection to the city does not work */
    public MCDASAggregationComponent() throws MqttException {
        this("tcp://mqtt.cgmu.io");
    }

    /** Create the component
     *
     * @throws MqttException if the connection to the city does not work */
    public MCDASAggregationComponent(String sourceURL) throws MqttException {
        cacheMonitor = new CGMUCacheMonitor();

        // Start connection to cgmu
        client = new MqttClient(sourceURL, MqttClient.generateClientId());
        client.setCallback(new MQTTSource(cacheMonitor));
        client.connect();
        client.subscribe("#");

        // Start connection to MQTT server for publishing
        server = new MQTTServer();
        Thread thread = new Thread(server, "MQTT MCDAS publisher");
        thread.start();

        // aggregator management
        factory = new AggregatorFactory(cacheMonitor);
        manager = new AggregatorManager(server, factory);
    }

    /** Get the manager.
     *
     * @return the manager */
    public AggregatorManager getManager() {
        return manager;
    }
}
