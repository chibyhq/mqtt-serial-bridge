package org.github.chibyhq.msb.serial;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.Callable;

import org.awaitility.Awaitility;
import org.awaitility.Duration;
import org.github.chibyhq.msb.dto.DeviceOutput;
import org.github.chibyhq.msb.dto.PortInfo;
import org.github.chibyhq.msb.serial.jserial.JSerialPortsManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

@DisabledIfEnvironmentVariable(named="GITHUB_RUN_ID", matches=".*")
public class JSerialPortsManagerTest implements SerialMessageListener {

	private static final String TTY_ACM0 = "ttyACM0";
	private Integer counter;

	@BeforeEach
	public void setup(){
		counter = 0;
	}
	
	
	@Test
	public void testMessageReceived() throws InterruptedException {
		JSerialPortsManager mgr = new JSerialPortsManager();
		assertNotNull(mgr.getPorts(false));
		mgr.addListener(TTY_ACM0, this);
		assertTrue(mgr.openPort(TTY_ACM0, null));
		Awaitility.await().atLeast(Duration.ONE_HUNDRED_MILLISECONDS).atMost(Duration.ONE_SECOND).with()
				.pollInterval(Duration.ONE_HUNDRED_MILLISECONDS).until(hasReceivedMessage());
		assertTrue(counter > 0);
		{
			List<PortInfo> ports = mgr.getPorts(false);
			assertTrue(ports.size() > 0);
			assertTrue(ports.stream()
					.anyMatch(portInfo -> portInfo.getSystemPortName().equals(TTY_ACM0) && portInfo.isOpen()));

			assertTrue(mgr.closePort(TTY_ACM0));
		}

		assertTrue(mgr.getPorts(false).stream().anyMatch(portInfo -> portInfo.getSystemPortName().equals(TTY_ACM0)
                && (!portInfo.isOpen())));
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
	public void onSerialMessage(DeviceOutput output) {
		counter++;
	}


    @Override
    public void onPortOpen(PortInfo port) {
    }
    
    @Override
    public void setSerialPortsManager(SerialPortsManager serialPortsManagerAdapter) {
    }

}
