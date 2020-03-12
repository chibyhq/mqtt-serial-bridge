package org.github.chibyhq.msb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.github.chibyhq.msb.dto.DeviceOutput;
import org.github.chibyhq.msb.serial.SerialMessageListener;
import org.springframework.stereotype.Component;

@Component
public class AccumulatingMessageListener implements SerialMessageListener {

	AtomicInteger counter = new AtomicInteger();
	List<DeviceOutput> messages = new ArrayList<>();
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void onMessage(DeviceOutput output) {
		counter.getAndIncrement();
		messages.add(output);
		System.out.println(output.getPort()+">> "+output.getLine());
	}
	

}
