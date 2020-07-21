package org.github.chibyhq.msb.serial;

import java.util.Optional;

import org.github.chibyhq.msb.dto.DeviceOutput;
import org.github.chibyhq.msb.dto.PortInfo;

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
	    Optional<PortInfo> portInfo = getPort(commPort);
	    listener.setSerialPortsManager(this);
        if(portInfo.isPresent()) {
	      listener.onPortOpen(portInfo.get());
	    }
	    return portToListeners.put(commPort, listener);
	}

	@Override
	public boolean removeListener(String commPort, SerialMessageListener listener) {
	   return portToListeners.remove(commPort, listener);
	}

	protected void updateListenersWithMessage(String portName, final DeviceOutput deviceOutput) {
		portToListeners.get(portName).forEach(listener -> {
		    try{
		        listener.onSerialMessage(deviceOutput);
		    }catch(Exception e) {
		        log.error(String.format("Could not relay message to listener {}", listener.getName()));
		        log.debug(e.getMessage());
		    }
		});
	}
	
    protected void updateListenersWithOpenPort(String portName) {
        portToListeners.get(portName).forEach(listener -> {
            try{
                PortInfo port = getPort(portName).get();
                if(port.isOpen()) {
                  listener.onPortOpen(port);
                }
            }catch(Exception e) {
                log.error(String.format("Could not relay message to listener {}", listener.getName()));
                log.debug(e.getMessage());
            }
        });
    }
	
	@Override
	public Optional<PortInfo> getPort(final String commPort) {
	    return getPorts(false)
	            .stream()
	            .filter(port -> commPort.equalsIgnoreCase(port.getSystemPortName()))
	            .findFirst();
	}

}