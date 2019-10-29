/**
 * aggregator
 * File: net.bigeon.mcdas.aggregator.MCDASAggregationComponent.java
 * Created on: Oct. 22, 2019
 */
package net.bigeon.mcdas.aggregator;

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
