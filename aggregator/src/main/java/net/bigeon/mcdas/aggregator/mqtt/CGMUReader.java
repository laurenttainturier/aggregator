package net.bigeon.mcdas.aggregator.mqtt;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import net.bigeon.mcdas.cgmu.OpenDataNodeFormat1;
import net.bigeon.mcdas.data.DataPoint;

/** JSON reader for CGMU input.
 *
 * @author Emmanuel Bigeon */
public class CGMUReader {
    /** Extract a data point for a CGMU issued message.
     * 
     * @param message the message
     * @return the data point
     * @throws IOException if an error occured */
	public static DataPoint read(MqttMessage message) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		OpenDataNodeFormat1 node;
		node = mapper.readValue(message.getPayload(), OpenDataNodeFormat1.class);
		LocalDateTime create = LocalDateTime.parse(node.getCreateUtc()).plus(ZonedDateTime.now().getOffset().getTotalSeconds(), ChronoUnit.SECONDS);
		LocalDateTime expire = LocalDateTime.parse(node.getExpireUtc()).plus(ZonedDateTime.now().getOffset().getTotalSeconds(), ChronoUnit.SECONDS);
		return new DataPoint(create, (int) create.until(expire, ChronoUnit.SECONDS), node.getValue());
	}
}
