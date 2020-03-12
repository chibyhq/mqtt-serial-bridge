package org.github.chibyhq.msb.serial;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.fazecast.jSerialComm.SerialPort;

public class JSerialPortsManagerTest {

	private SerialPort serialPort;

	@Before
	public void setup(){
		SerialPort.getCommPorts();
		serialPort = SerialPort.getCommPort("/dev/ttyACM0");
		serialPort.setBaudRate(9600);
	}
	
	
	@Test
	public void test() {
		assertTrue(serialPort.openPort());
		JSerialPortsManager mgr = new JSerialPortsManager();
		assertNotNull(mgr.onGetPorts());
	}

}
