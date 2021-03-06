package org.github.chibyhq.msb.serial.jserial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.github.chibyhq.msb.dto.DeviceOutput;
import org.github.chibyhq.msb.dto.PortInfo;
import org.github.chibyhq.msb.serial.PortParameters;
import org.github.chibyhq.msb.serial.SerialPortsManagerAdapter;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * JSerial implementation of the Serial Port manager. Manages access to the host
 * serial ports, such as listing available ports, opening and closing ports,
 * associating listeners and forwarding messages back and forth.
 */
@Slf4j
public class JSerialPortsManager extends SerialPortsManagerAdapter {

    Map<String, SerialPort> serialPorts = new HashMap<>();

    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);

    private Map<String, PortParameters> portsThatShouldBeOpen = new HashMap<>();

    private boolean schedulerInitialized = false;

    public JSerialPortsManager() {
        super();
        refreshPorts();
    }

    /**
     * Query the ports via the JSerialComm method. 
     * We need to merge the JSerialComm query results with the current
     * known port instances we already have : This is because
     * JSerialComm creates new SerialPort object instance everytime
     * you query ports.
     * For further details,
     * have a look at :
     * <a href="https://github.com/Fazecast/jSerialComm/issues/272">issue
     * 272</a>.
     */
    public void refreshPorts() {
        log.trace("Querying available ports...");
        // We must now :
        //   * Step 1 : detect removed ports and clean them up
        //   * Step 2 : merge existing and open ports with the ones we have
        Map<String, SerialPort> scanResults = Arrays.stream(SerialPort.getCommPorts())
                .collect(Collectors.toMap(SerialPort::getSystemPortName, Function.identity()));
        
        synchronized(serialPorts) {
            List<String> portsToRemove = new ArrayList<>();
            for(String knownPortName : serialPorts.keySet()) {
                if(!scanResults.containsKey(knownPortName)) {
                    log.error("Port {} was closed unexpectedly", knownPortName);
                    // The port has now been removed
                    notifyListenersOfClosedPort(knownPortName);
                    portsToRemove.add(knownPortName);
                }
            }
            for(String portToRemove : portsToRemove) {
                serialPorts.remove(portToRemove);
                // Release the port from the operating system, so it can be reused
                // with the same identifier if the device is plugged in again
                SerialPort.getCommPort(portToRemove).closePort();
            }
            
            for(String newPortName : scanResults.keySet()) {
                if(serialPorts.containsKey(newPortName)) {
                    if(log.isDebugEnabled()) {
                        log.trace("Port {} already present, not reinstantiating it", newPortName);
                    }
                }else {
                    // This is a new port, we add it the list
                    serialPorts.put(newPortName, scanResults.get(newPortName));
                }
            }
        }
        
        log.trace("Found {} ports", serialPorts.size());
    }

    @Override
    public List<PortInfo> getPorts(boolean forceRequery) {
        if (forceRequery) {
            refreshPorts();
        }
        List<PortInfo> portsResult = new ArrayList<>();

        synchronized(serialPorts) {
            for (SerialPort port : serialPorts.values()) {
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
        }
        return portsResult;
    }

    @Override
    public boolean openPort(String commPort, final PortParameters params) {
        if (!schedulerInitialized) {
            executorService.scheduleWithFixedDelay(portWatchdog, params.getRetryIntervalInMs(),
                    params.getRetryIntervalInMs(), TimeUnit.MILLISECONDS);
            schedulerInitialized = true;
        }

        log.debug("Attempting to open port {}", commPort);
        boolean openPortSuccessful = false;
        synchronized (portsThatShouldBeOpen) {
            if (params.getKeepTryingUponFailure()) {
                portsThatShouldBeOpen.put(commPort, params);
            }
        }
        try {
            SerialPort p = serialPorts.get(commPort);
            if (p != null) {
                p.addDataListener(this);
                p.setComPortParameters(params.getBaudRate(), params.getDataBits(), params.getStopBits(),
                        params.getParity());
                p.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, params.getReadTimeout(),
                        params.getWriteTimeout());

                openPortSuccessful = p.openPort();
                if (openPortSuccessful) {
                    log.debug("Opened Port {} successfully !", commPort);
                }
                updateListenersWithOpenPort(p.getSystemPortName());
            }

        } catch (Exception e) {
            log.error("Port {} could not be open due to exception", commPort, e);
        }

        if (!openPortSuccessful) {
            log.debug("Port {} does not exist currently on the host system", commPort);
        }

        return openPortSuccessful;

    }

    /**
     * Explicitly close the given port. We will not reattempt to
     * reconnect to port, even if it comes online at a later stage.
     */
    @Override
    public boolean closePort(String commPort) {
        log.debug("Attempting to close port {}", commPort);
        synchronized (portsThatShouldBeOpen) {
            portsThatShouldBeOpen.remove(commPort);
        }
        SerialPort port = serialPorts.get(commPort);
        if (port != null && port.isOpen()) {
            boolean closePortResult = port.closePort();
            log.debug("Port {} closed ? {}", commPort, closePortResult);
            return closePortResult;
        }
        return false;
    }

    @Override
    public void onIncomingSerialMessage(String commPort, String message) throws IOException {
        SerialPort port = serialPorts.get(commPort);
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
        return new byte[] { (byte) 0x0D, (byte) 0x0A };
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
            updateListenersWithMessage(portName, out);
            break;
        default:
            break;
        }
    }

    private Runnable portWatchdog = new Runnable() {
        @Override
        public void run() {
            try {
                synchronized (portsThatShouldBeOpen) {
                    getPorts(true).stream().forEach(portInfo -> {
                        try {
                            if (!portInfo.isOpen()
                                    && portsThatShouldBeOpen.keySet().contains(portInfo.getSystemPortName())) {
                                openPort(portInfo.getSystemPortName(),
                                        portsThatShouldBeOpen.get(portInfo.getSystemPortName()));
                            }
                        } catch (Exception e) {
                            log.error("Port watchdog, error while opening port {} ", portInfo.getSystemPortName(), e);
                        }
                    });
                }
            } catch (Exception outerException) {
                log.error("Port watchdog run error : ", outerException);
            }
        }
    };

}
