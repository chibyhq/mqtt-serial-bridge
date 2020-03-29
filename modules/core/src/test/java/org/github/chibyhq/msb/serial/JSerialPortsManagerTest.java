package org.github.chibyhq.msb.serial;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Callable;

import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.github.chibyhq.msb.dto.DeviceOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import com.fazecast.jSerialComm.SerialPort;

@DisabledIfEnvironmentVariable(named="GITHUB_RUN_ID", matches=".*")
public class JSerialPortsManagerTest implements SerialMessageListener {

	private SerialPort serialPort;
	private Integer counter;

	@BeforeEach
	public void setup(){
		SerialPort.getCommPorts();
		serialPort = SerialPort.getCommPort("/dev/ttyACM0");
		serialPort.setBaudRate(115200);
		counter = 0;
	}
	
	
	@Test
	public void test() throws InterruptedException {
		assertTrue(serialPort.openPort());
		JSerialPortsManager mgr = new JSerialPortsManager();
		assertNotNull(mgr.onGetPorts());
		mgr.addListener("/dev/ttyACM0", this);
		mgr.onOpenPort("/dev/ttyACM0", null);
		Awaitility.await()
	    .atLeast(Duration.ONE_HUNDRED_MILLISECONDS)
	    .atMost(Duration.FIVE_SECONDS)
	  .with()
	    .pollInterval(Duration.ONE_HUNDRED_MILLISECONDS)
	    .until(hasReceivedMessage());
		assertTrue(counter>0);
	}


	private Callable<Boolean> hasReceivedMessage() {
		return new Callable<>() {
			@Override
			public Boolean call() throws Exception {
				return counter > 0;
			}
		};
	}


	@Override
	public String getName() {
		return this.getClass().getName();
	}


	@Override
	public void onMessage(DeviceOutput output) {
		counter++;
	}

}
