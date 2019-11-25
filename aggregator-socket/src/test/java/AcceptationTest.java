import net.bigeon.mcdas.aggregator.MCDASAggregationComponent;
import net.bigeon.mcdas.aggregator.socket.AdministrationServer;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;


public class AcceptationTest {

    private MockServerSocket serverSocket;
    private AdministrationServer server;

    @Before
    public void init() throws IOException, MqttException {
        this.serverSocket = new MockServerSocket();
        MCDASAggregationComponent aggregator = new MCDASAggregationComponent();
        this.server = new AdministrationServer(aggregator.getManager());
    }

    private String sendRequest(String message) throws IOException {
        serverSocket.sendMessage(message);
        server.handleConnection(serverSocket);
        MockOutputStream mos = (MockOutputStream) serverSocket.accept().getOutputStream();
        return mos.toString();
    }

    @Test
    public void correctStartTest() throws IOException, MqttException {
        String message = "start 1 min 30 odtf1_1ca_1qc_1mtl_1mobil_1traf_1detector_1det1_1det-00971-02_1zone1_1class2_1vehicle-speed";
        Assert.assertEquals("0", sendRequest(message));
    }

    @Test
    @Ignore
    public void invalidIdStartTest() throws IOException, MqttException {
        String message = "start *&%# min 30 odtf1_1ca_1qc_1mtl_1mobil_1traf_1detector_1det1_1det-00971-02_1zone1_1class2_1vehicle-speed";
        Assert.assertEquals("2", sendRequest(message));
    }

    @Test
    public void invalidAggregateStartTest() throws IOException, MqttException {
        String message = "start 1 mini 30 odtf1_1ca_1qc_1mtl_1mobil_1traf_1detector_1det1_1det-00971-02_1zone1_1class2_1vehicle-speed";
        Assert.assertEquals("3", sendRequest(message));
    }

    @Test
    @Ignore
    public void maformedNodeStartTest() throws IOException, MqttException {
        String message = "start 1 min 30 odtf1/ca";
        Assert.assertEquals("4", sendRequest(message));
    }

    @Test
    public void nullDurationStartTest() throws IOException, MqttException {
        String message = "start 1 min -10 odtf1_1ca_1qc_1mtl_1mobil_1traf_1detector_1det1_1det-00971-02_1zone1_1class2_1vehicle-speed";
        Assert.assertEquals("5", sendRequest(message));
    }

    @Test
    public void negativeDurationStartTest() throws IOException, MqttException {
        String message = "start 1 min -1 odtf1_1ca_1qc_1mtl_1mobil_1traf_1detector_1det1_1det-00971-02_1zone1_1class2_1vehicle-speed";
        Assert.assertEquals("5", sendRequest(message));
    }

    @Test
    @Ignore
    public void alreadyPresentIdStartTest() throws IOException, MqttException {
        String message = "start 1 min 10 odtf1_1ca_1qc_1mtl_1mobil_1traf_1detector_1det1_1det-00971-02_1zone1_1class2_1vehicle-speed";
        Assert.assertEquals("2", sendRequest(message));
    }

    @Test
    public void noAggregatorListTest() throws IOException, MqttException {
        String message = "list";
        Assert.assertEquals("{elements = {}}", sendRequest(message));
    }
}
