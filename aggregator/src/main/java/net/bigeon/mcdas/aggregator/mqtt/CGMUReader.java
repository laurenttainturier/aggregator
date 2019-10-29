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
