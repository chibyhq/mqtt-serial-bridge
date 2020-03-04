package org.github.chibyhq.msb.mqtt;

import org.github.chibyhq.msb.dto.DeviceOutput;
import org.github.chibyhq.msb.dto.PortInfo;

/**
 * Implement this interface to let the bridge know how to format
 * MQTT topic names. You can for instance use the contents of the 
 * output message to publish to a given topic, or use the port name
 * or the host name, or a combination of all of these.<br>
 * You can also specify on which MQTT topic to expect serial ports to be
 * remotely controlled.
 */
public interface MqttNamingStrategy {
    String getTopicName(DeviceOutput output);
    String getIncomingMqttCommandTopicForPort(PortInfo portInfo);
}
