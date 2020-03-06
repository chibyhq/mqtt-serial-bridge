package org.github.chibyhq.msb.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.github.chibyhq.msb.dto.DeviceOutput;
import org.github.chibyhq.msb.serial.SerialMessageFormatter;
import org.github.chibyhq.msb.serial.SerialMessageListener;

public class MqttForwardListener implements SerialMessageListener {

    MqttNamingStrategy namingStrategy;
    
    MqttClient mqttClient;
    
    SerialMessageFormatter formatter;
    
    MqttErrorHandler mqttErrorHandler;
    
    public MqttForwardListener(MqttNamingStrategy namingStrategy, MqttClient mqttClient,
            SerialMessageFormatter formatter, MqttErrorHandler mqttErrorHandler) {
        super();
        this.namingStrategy = namingStrategy;
        this.mqttClient = mqttClient;
        this.formatter = formatter;
        this.mqttErrorHandler = mqttErrorHandler;
    }

    public MqttForwardListener(MqttNamingStrategy namingStrategy, MqttClient mqttClient,
            SerialMessageFormatter formatter) {
        this(namingStrategy,mqttClient,formatter, new MqttErrorLoggingHandler());
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void onMessage(DeviceOutput output) {
        String topic = namingStrategy.getTopicName(output);
        
        try {
            mqttClient.publish(topic, formatter.getMessageForDeviceOutput(output));
        } catch (Exception e) {
            mqttErrorHandler.onException(e);
        }
    }

}
