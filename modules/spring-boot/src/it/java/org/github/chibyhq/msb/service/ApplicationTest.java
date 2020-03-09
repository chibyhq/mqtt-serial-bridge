package org.github.chibyhq.msb.service;

import org.github.chibyhq.msb.serial.SerialPortsManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

	@Autowired
	SerialPortsManager serialPortsManager;
	
	@Test
	public void test() {
		// Nothing to do for now
	}

}
