package org.github.chibyhq.msb.serial;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import com.fazecast.jSerialComm.SerialPort;

@DisabledIfEnvironmentVariable(named = "GITHUB_RUN_ID", matches = ".*")
class TrivialJSerialCommTest {
	private static final String TTY_ACM0 = "ttyACM0";

	@Test
	void testSameInstance() {
		SerialPort port = Arrays.stream(SerialPort.getCommPorts())
				.filter(p -> p.getSystemPortName().equals(TTY_ACM0)).findFirst().get();
		assertFalse(port.isOpen());
		assertTrue(port.openPort());
		assertTrue(port.isOpen());
		assertTrue(port.closePort());
		assertFalse(port.isOpen());
		// All good !
		
	}
	
	@Test
	void testNewInstance() {
		SerialPort port = Arrays.stream(SerialPort.getCommPorts())
				.filter(p -> p.getSystemPortName().equals(TTY_ACM0)).findFirst().get();
		assertFalse(port.isOpen());
		assertTrue(port.openPort());
		
		// Ask JSerialComm to enumerate all ports again
		port = Arrays.stream(SerialPort.getCommPorts())
				.filter(p -> p.getSystemPortName().equals(TTY_ACM0)).findFirst().get();
		// This assertion fails !!!
		assertTrue(port.isOpen());
		
	}

}
