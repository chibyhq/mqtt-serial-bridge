package org.github.chibyhq.msb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.github.chibyhq.msb.dto.DeviceOutput;
import org.github.chibyhq.msb.dto.PortInfo;
import org.github.chibyhq.msb.serial.SerialMessageListener;
import org.github.chibyhq.msb.serial.SerialPortsManager;
import org.springframework.stereotype.Component;

public class AccumulatingMessageListener implements SerialMessageListener {

	AtomicInteger counter = new AtomicInteger();
	List<DeviceOutput> messages = new ArrayList<>();
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void onSerialMessage(DeviceOutput output) {
		counter.getAndIncrement();
		messages.add(output);
		System.out.println(output.getPort()+">> "+output.getLine());
	}

    @Override
    public void onPortOpen(PortInfo port) {
        System.out.println(String.format(">> {} now open",port.getSystemPortName()));
    }

    @Override
    public void setSerialPortsManager(SerialPortsManager serialPortsManagerAdapter) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPortClosed(String port) {
        // TODO Auto-generated method stub
        
    }
    
	

}
