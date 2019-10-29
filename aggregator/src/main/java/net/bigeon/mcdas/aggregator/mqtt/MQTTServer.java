/**
 * aggregator
 * File: net.bigeon.mcdas.aggregator.mqtt.MQTTServer.java
 * Created on: Oct. 22, 2019
 */
package net.bigeon.mcdas.aggregator.mqtt;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import net.bigeon.mcdas.data.DataPoint;

/** A link to an MQTT server for publishing.
 * 
 * @author Emmanuel Bigeon */
public class MQTTServer implements Runnable {

    private static final long MINUTE  = 60_000;
    private MqttClient        client;
    private final Object            lock    = new Object();
    private boolean           running = true;
    private final String      publishServerURL = "tcp://localhost";

    /** Publish a message
     *
     * @param topic the topic
     * @param point the message. */
    public void publish(String topic, DataPoint point) {
        synchronized (lock) {
            if (!running) {
                return;
            }
            MqttMessage message = new MqttMessage();
            try {
                StringBuilder msg = new StringBuilder("{\"date\":\"");
                msg.append(ZonedDateTime.of(point.date, ZoneId.systemDefault()).format(DateTimeFormatter.ISO_DATE_TIME));
                msg.append("\",\"duration\":");
                msg.append(point.duration);
                msg.append(",\"value\":");
                msg.append(point.value);
                msg.append("}");
                message.setPayload(msg.toString()
                        .getBytes(StandardCharsets.UTF_8));
                client.publish(topic, message);
            } catch (MqttException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run() */
    @Override
    public void run() {
        try {
            client = new MqttClient(publishServerURL,
                    MqttClient.generateClientId());
            client.connect();
            synchronized (lock) {
                while (running) {
                    try {
                        lock.wait(MINUTE);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            client.disconnect();
        } catch (MqttException e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    /** Stop the server */
    public void stop() {
        synchronized (lock) {
            running = false;
            lock.notifyAll();
        }
    }
}
