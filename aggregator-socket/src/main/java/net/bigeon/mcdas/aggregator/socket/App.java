package net.bigeon.mcdas.aggregator.socket;

import java.io.IOException;
import java.net.UnknownHostException;

import org.eclipse.paho.client.mqttv3.MqttException;

import net.bigeon.mcdas.aggregator.MCDASAggregationComponent;

/** Main application */
public class App {
    public static void main(String[] args) throws UnknownHostException, IOException, MqttException {
        MCDASAggregationComponent aggregator = new MCDASAggregationComponent();
        AdministrationServer server = new AdministrationServer(aggregator.getManager());
        Thread th = new Thread(server, "Administration");
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
