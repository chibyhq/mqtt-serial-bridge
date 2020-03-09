package org.github.chibyhq.msb.service;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.github.chibyhq.msb.mqtt.MqttErrorHandler;
import org.github.chibyhq.msb.mqtt.MqttForwardListener;
import org.github.chibyhq.msb.mqtt.MqttNamingStrategy;
import org.github.chibyhq.msb.serial.SerialMessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
@Primary
@ConditionalOnProperty("msb.mqtt.forward")
public class MqttForwardListenerBean extends MqttForwardListener{
	@Autowired MqttNamingStrategy namingStrategy;
	@Autowired MqttClient mqttClient;
	
	@Autowired SerialMessageFormatter formatter;
	@Autowired MqttErrorHandler mqttErrorHandler;
	
	
	public MqttForwardListenerBean(MqttNamingStrategy namingStrategy, MqttClient mqttClient,
			SerialMessageFormatter formatter, MqttErrorHandler mqttErrorHandler) {
		super(namingStrategy, mqttClient, formatter, mqttErrorHandler);
	}

    
}
