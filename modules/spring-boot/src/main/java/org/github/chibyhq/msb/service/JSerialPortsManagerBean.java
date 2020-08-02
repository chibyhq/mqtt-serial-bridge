package org.github.chibyhq.msb.service;

import javax.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.github.chibyhq.msb.serial.PortParameters;
import org.github.chibyhq.msb.serial.SerialMessageListener;
import org.github.chibyhq.msb.serial.jserial.JSerialPortsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JSerialPortsManagerBean extends JSerialPortsManager {

	@Autowired
	SerialMessageListener serialMessageListener;
	
	
	@Value("${msb.serial.port:ttyACM0}")
	String serialPort;
	
    @Value("${msb.serial.baudrate:115200}")
    Integer rate;

	public JSerialPortsManagerBean(SerialMessageListener serialMessageListener) {
		super();
		this.serialMessageListener = serialMessageListener;
	}
	
	@PostConstruct
	public void startListener() throws MqttSecurityException, MqttException {
		log.info("Starting port listener {}", serialPort);
		this.addListener(serialPort, serialMessageListener);
		this.openPort(serialPort, PortParameters.builder().baudRate(rate).build());
	}
	
}
