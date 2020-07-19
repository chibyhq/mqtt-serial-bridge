package org.github.chibyhq.msb.serial;

import org.github.chibyhq.msb.dto.DeviceOutput;

import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SerialPortsManagerAdapter implements SerialPortMessageListener, SerialPortsManager {

	Multimap<String, SerialMessageListener> portToListeners = MultimapBuilder.hashKeys().arrayListValues().build();

	public SerialPortsManagerAdapter() {
		super();
	}

	@Override
	public boolean addListener(String commPort, SerialMessageListener listener) {
	    return portToListeners.put(commPort, listener);
	}

	@Override
	public boolean removeListener(String commPort, SerialMessageListener listener) {
	   return portToListeners.remove(commPort, listener);
	}

	protected void updateListeners(String portName, final DeviceOutput deviceOutput) {
		portToListeners.get(portName).forEach(listener -> {
		    try{
		        listener.onSerialMessage(deviceOutput);
		    }catch(Exception e) {
		        log.error(String.format("Could not relay message to listener {}", listener.getName()));
		        log.debug(e.getMessage());
		    }
		});
	}

}