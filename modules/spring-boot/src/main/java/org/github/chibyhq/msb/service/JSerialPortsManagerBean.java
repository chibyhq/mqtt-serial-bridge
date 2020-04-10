package org.github.chibyhq.msb.service;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.github.chibyhq.msb.serial.JSerialPortsManager;
import org.github.chibyhq.msb.serial.SerialMessageListener;
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

	public JSerialPortsManagerBean(SerialMessageListener serialMessageListener) {
		super();
		this.serialMessageListener = serialMessageListener;
	}
	
	@PostConstruct
	public void startListener() {
		log.info("Starting port listener {}", serialPort);
		this.addListener(serialPort, serialMessageListener);
		this.onOpenPort(serialPort, new HashMap<String, String>());
	}
	
	
	
	
}
