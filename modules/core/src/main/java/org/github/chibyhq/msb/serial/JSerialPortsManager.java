package org.github.chibyhq.msb.serial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.github.chibyhq.msb.dto.DeviceOutput;
import org.github.chibyhq.msb.dto.PortInfo;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import lombok.extern.java.Log;

/**
 * Manages access to the host serial ports, such as listing available ports, opening and closing
 * ports, associating listeners and forwarding messages back and forth.
 */
@Log
public class JSerialPortsManager implements SerialPortDataListener, SerialPortsManager {

    Map<String, SerialPort> activePorts = new HashMap<>();

    Multimap<String, SerialMessageListener> portToListeners = MultimapBuilder.hashKeys().arrayListValues().build();

    @Override
	public List<PortInfo> onGetPorts() {
        List<PortInfo> portsResult = new ArrayList<>();
        for (SerialPort port : SerialPort.getCommPorts()) {
            PortInfo portInfo = new PortInfo();
            portInfo.setSystemPortName(port.getSystemPortName());
            portInfo.setOpen(port.isOpen());
            portInfo.setDescriptivePortName(port.getDescriptivePortName());
            if (port.isOpen()) {
                portInfo.setBaudRate(port.getBaudRate());
                portInfo.setDsr(port.getDSR());
                portInfo.setParity(port.getParity());
            }
            portsResult.add(portInfo);
        }
        return portsResult;
    }
    
    @Override
	public boolean addListener(String commPort, SerialMessageListener listener) {
        return portToListeners.put(commPort, listener);
    }
    
    @Override
	public boolean removeListener(String commPort, SerialMessageListener listener) {
       return portToListeners.remove(commPort, listener);
    }

    @Override
	public boolean onOpenPort(String commPort, Map<String, String> params) {
        if (!params.containsKey(ParamEnum.BAUD_RATE.toString())) {
            params.put(ParamEnum.BAUD_RATE.toString(), "9600");
        }
        // TODO : Add support for more port parameters
        
        SerialPort p = SerialPort.getCommPort(commPort);

        if (p.isOpen()) {
            // If the port is open with different parameters than
            // requested, send an exception
            if (p.getBaudRate() != Integer.valueOf(params.get(ParamEnum.BAUD_RATE.toString()))) {
                throw new IllegalStateException("Port " + commPort + " is already open with different baud rate");
            }
            return true;
        } else {
            p.setBaudRate(Integer.valueOf(params.get(ParamEnum.BAUD_RATE.toString())));
            return p.openPort();
        }
    }

    @Override
	public void onClosePort(String commPort) {
        SerialPort port = activePorts.get(commPort);
        if (port != null && port.isOpen()) {
            port.closePort();
        }
    }

    @Override
	public void onIncomingMessage(String commPort, String message) throws IOException {
        SerialPort port = activePorts.get(commPort);
        if (port != null && port.isOpen()) {
            port.getOutputStream().write((message + "\n").getBytes());
        }
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        SerialPort port = event.getSerialPort();
        switch (event.getEventType()) {
        case SerialPort.LISTENING_EVENT_DATA_AVAILABLE:
            byte[] newData = new byte[port.bytesAvailable()];
            int numRead = port.readBytes(newData, newData.length);
            if (numRead > 0) {
                String portName = port.getSystemPortName();
                final DeviceOutput out = DeviceOutput.builder().port(portName).timestamp(System.currentTimeMillis())
                        .line(new String(newData)).build();
                portToListeners.get(portName).forEach(listener -> {
                    try{
                        listener.onMessage(out);
                    }catch(Exception e) {
                        log.log(Level.SEVERE, String.format("Could not relay message to listener {}", listener.getName()));
                        log.log(Level.FINER, e.getMessage());
                    }
                });
            }
            break;
        default:
            break;
        }
    }

}
