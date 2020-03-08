package org.github.chibyhq.msb.service;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.github.chibyhq.msb.mqtt.MqttForwardListener;
import org.github.chibyhq.msb.serial.SerialPortsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SerialPortsManagerBean extends SerialPortsManager {

	@Autowired
	MqttForwardListener mqttForwardListener;
	
	@Value("msb.serial.port")
	String serialPort;

	public SerialPortsManagerBean(MqttForwardListener mqttForwardListener) {
		super();
		this.mqttForwardListener = mqttForwardListener;
	}
	
	@PostConstruct
	public void startListener() {
		log.info("Starting port listener {}", serialPort);
		this.addListener(serialPort, mqttForwardListener);
		this.onOpenPort(serialPort, new HashMap<String, String>());
	}
	
	
	
	
}
