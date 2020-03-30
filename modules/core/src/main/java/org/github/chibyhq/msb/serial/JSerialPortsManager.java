package org.github.chibyhq.msb.serial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.github.chibyhq.msb.dto.DeviceOutput;
import org.github.chibyhq.msb.dto.PortInfo;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * Manages access to the host serial ports, such as listing available ports, opening and closing
 * ports, associating listeners and forwarding messages back and forth.
 */
@Slf4j
public class JSerialPortsManager extends SerialPortsManagerAdapter {

    @Override
	public List<PortInfo> onGetPorts() {
    	log.debug("Querying available ports...");
        List<PortInfo> portsResult = new ArrayList<>();
        log.debug("Found {} ports", portsResult.size());
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
	public boolean onOpenPort(String commPort, Map<String, String> params) {
    	log.debug("Opening port", commPort);
    	if (params == null) {
    		params = new HashMap<>();
    	}
//        if (!params.containsKey(ParamEnum.BAUD_RATE.toString())) {
//            params.put(ParamEnum.BAUD_RATE.toString(), "9600");
//        }
        // TODO : Add support for more port parameters
        
        SerialPort p = SerialPort.getCommPort(commPort);

        if (p.isOpen()) {
            // If the port is open with different parameters than
            // requested, send an exception
        	// TODO : Implement more comparisons for port parameters
            if (p.getBaudRate() != Integer.valueOf(params.get(ParamEnum.BAUD_RATE.toString()))) {
                throw new IllegalStateException("Port " + commPort + " is already open with different baud rate");
            }
            return true;
        } else {
//            p.setBaudRate(Integer.valueOf(params.get(ParamEnum.BAUD_RATE.toString())));
            p.addDataListener(this);
            p.setComPortParameters(115200, 8,1,0);
            p.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 200, 0);
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
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }
    
    @Override
    public byte[] getMessageDelimiter() {
      return new byte[] { (byte)0x0D, (byte)0x0A };
    }
    
    @Override
    public boolean delimiterIndicatesEndOfMessage() {
    	return true;
    }

	@Override
	public void serialEvent(SerialPortEvent event) {
		SerialPort port = event.getSerialPort();
		switch (event.getEventType()) {
		case SerialPort.LISTENING_EVENT_DATA_RECEIVED:
			String portName = port.getSystemPortName();
			final DeviceOutput out = DeviceOutput.builder().port(portName).timestamp(System.currentTimeMillis())
						.line(new String(event.getReceivedData())).build();
			updateListeners(portName, out);
			break;
		default:
			break;
		}
	}

}
