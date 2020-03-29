package org.github.chibyhq.msb.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.github.chibyhq.msb.serial.SerialPortsManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisabledIfEnvironmentVariable(named="GITHUB_RUN_ID", matches=".*")
public class ApplicationTest {

	@Autowired
	SerialPortsManager serialPortsManager;
	
	@Autowired
	AccumulatingMessageListener listener;
	
	
	@Test
	public void test() throws InterruptedException {
		Thread.sleep(2000);
		assertTrue(listener.counter.get() > 1);
	}

}
