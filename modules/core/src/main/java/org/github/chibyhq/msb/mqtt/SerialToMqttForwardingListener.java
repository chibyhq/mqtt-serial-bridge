package org.github.chibyhq.msb.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.github.chibyhq.msb.dto.DeviceOutput;
import org.github.chibyhq.msb.serial.SerialMessageFormatter;
import org.github.chibyhq.msb.serial.SerialMessageListener;

/**
 * Forwards incoming serial messages to an MQTT topic, via :
 * <ul>
 *   <li>A pre-configured MQTT client instance
 *   <li>A MQTT destination topic naming strategy (to parse Serial messages and forward them)
 *   <li>A Message Formatter (to convert incoming serial messages into an MQTT message format)
 *   <li>A pre-configured MQTT error handler, in case messages cannot be forwarded
 * </ul>
 * @author bcopy
 *
 */
public class SerialToMqttForwardingListener implements SerialMessageListener {

    MqttNamingStrategy namingStrategy;
    
    MqttClient mqttClient;
    
    SerialMessageFormatter formatter;
    
    MqttErrorHandler mqttErrorHandler;
    
    public SerialToMqttForwardingListener(MqttNamingStrategy namingStrategy, MqttClient mqttClient,
            SerialMessageFormatter formatter, MqttErrorHandler mqttErrorHandler) {
        super();
        this.namingStrategy = namingStrategy;
        this.mqttClient = mqttClient;
        this.formatter = formatter;
        this.mqttErrorHandler = mqttErrorHandler;
    }

    public SerialToMqttForwardingListener(MqttNamingStrategy namingStrategy, MqttClient mqttClient,
            SerialMessageFormatter formatter) {
        this(namingStrategy,mqttClient,formatter, new MqttErrorLoggingHandler());
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void onMessage(DeviceOutput output) {
        String destinationTopic = namingStrategy.getTopicName(output);
        
        try {
            mqttClient.publish(destinationTopic, formatter.getMessageForDeviceOutput(output));
        } catch (Exception e) {
            mqttErrorHandler.onException(e);
        }
    }

}
