package net.bigeon.mcdas.aggregator.socket;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.paho.client.mqttv3.MqttException;

import net.bigeon.mcdas.aggregator.MCDASAggregationComponent;

/** Main application */
public class App {
    public static void main(String[] args) throws UnknownHostException, IOException, MqttException {
        try {
            Path configuration = Paths.get(App.class.getProtectionDomain().getCodeSource()
                    .getLocation().toURI());
        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

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
