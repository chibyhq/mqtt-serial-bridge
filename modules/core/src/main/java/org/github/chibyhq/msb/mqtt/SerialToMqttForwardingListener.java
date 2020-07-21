package org.github.chibyhq.msb.mqtt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.github.chibyhq.msb.dto.DeviceOutput;
import org.github.chibyhq.msb.dto.PortInfo;
import org.github.chibyhq.msb.serial.SerialMessageFormatter;
import org.github.chibyhq.msb.serial.SerialMessageListener;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class SerialToMqttForwardingListener implements SerialMessageListener, IMqttMessageListener {

    MqttNamingStrategy namingStrategy;
    
    MqttClient mqttClient;
    
    SerialMessageFormatter formatter;
    
    MqttErrorHandler mqttErrorHandler;
    
    List<String> subscribedTopics = new ArrayList<>();
    
    Boolean enabled = false;
    
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
    public void onSerialMessage(DeviceOutput output) {
        String destinationTopic = namingStrategy.getTopicName(output);
        try {
            if(!mqttClient.isConnected()) {
                mqttClient.connect();
            }
        }catch(Exception e) {
            log.warn("MQTT client could not connect upon message : {}",output);
        }
        if(mqttClient.isConnected()) {
            try {
                mqttClient.publish(destinationTopic, formatter.getMessageForDeviceOutput(output));
            } catch (Exception e) {
                mqttErrorHandler.onException(e);
            }
        }else {
            log.debug("MQTT client not connected, message ignored : {}",output);
        }
    }
    
    @Override
    public void onPortOpen(PortInfo portInfo) {
        try {
            if(!mqttClient.isConnected()) {
                mqttClient.connect();
            }
            // Immediately subscribe to the corresponding incoming MQTT topic for this port
            String mqttTopic = namingStrategy.getIncomingMqttCommandTopicForPort(portInfo);
            if(!subscribedTopics.contains(mqttTopic)) {
                mqttClient.subscribe(mqttTopic, this);
                subscribedTopics.add(mqttTopic);
            }
            enabled = true;
        } catch (MqttException e) {
            log.error("Unable to subscribe to command topic for port ", portInfo.toString(), e);
        }
        
    }

    @Override
    public void messageArrived(String mqttTopic, MqttMessage message) throws Exception {
        // TODO Auto-generated method stub
        
    }

}
