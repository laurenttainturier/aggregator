/**
 * aggregator
 * File: net.bigeon.mcdas.aggregator.mqtt.MQTTServer.java
 * Created on: Oct. 22, 2019
 */
package net.bigeon.mcdas.aggregator.mqtt;

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
