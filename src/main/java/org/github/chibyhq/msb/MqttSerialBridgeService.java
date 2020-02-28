package org.github.chibyhq.msb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.github.chibyhq.msb.dto.DeviceOutput;
import org.github.chibyhq.msb.dto.PortInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import lombok.extern.java.Log;

@Log
public class MqttSerialBridgeService implements SerialPortDataListener {

    SerialPort broadcastedPort;

//    @Get
//    public void get(AtmosphereResource r) {
//        Map<String, String[]> params = new HashMap<String, String[]>();
//        params.putAll(r.getRequest().getParameterMap());
//        String commPort = getPortFromBroadcaster(r.getBroadcaster());
//
//        if (getPortFromBroadcaster(r.getBroadcaster()).equals(LIST_ALL_PORTS)) {
//            r.getBroadcaster().broadcast(getListOfPortsAsJson(), r);
//        } else {
//            List<PortInfo> portInfoList = getListOfPorts();
//            if (portInfoList.indexOf(PortInfo.builder().systemPortName(commPort).build()) == -1) {
//                // try {
//                // r.getResponse().sendError(404, );
//                r.getBroadcaster().broadcast("Serial port " + commPort + " does not exist", r);
//                // Close the connection, as the port does not exist
//                // r.resume();
//                // } catch (IOException e) {
//                // log.log(Level.WARNING, "Could not report non-existing
//                // CommPort to newly subscribed resource", e);
//                // }
//            } else {
//                if (!params.containsKey("baud")) {
//                    params.put("baud", new String[] { "9600" });
//                }
//                SerialPort port = SerialPort.getCommPort(commPort);
//
//                if (port.isOpen()) {
//                    // If the port is open with different parameters than
//                    // requested,
//                    // send an exception
//                    if (broadcastedPort.getBaudRate() != Integer.valueOf(params.get("baud")[0])) {
//                        throw new IllegalStateException(
//                                "Port " + commPort + " is already open with different baud rate");
//                    }
//                } else {
//                    port.setBaudRate(Integer.valueOf(params.get("baud")[0]));
//                    port.openPort();
//                    r.suspend();
//                }
//
//                if (broadcastedPort == null) {
//                    broadcastedPort = port;
//                }
//            }
//        }
//
//    }
//
//    private String getListOfPortsAsJson() {
//        String result = "";
//        List<PortInfo> portsResult = getListOfPorts();
//        try {
//            result = objectMapper.writeValueAsString(portsResult);
//        } catch (JsonProcessingException e) {
//            throw new IllegalStateException(e);
//        }
//        return result;
//    }


    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
        case SerialPort.LISTENING_EVENT_DATA_AVAILABLE:
            byte[] newData = new byte[broadcastedPort.bytesAvailable()];
            int numRead = broadcastedPort.readBytes(newData, newData.length);
            if (numRead > 0) {
                String portName = event.getSerialPort().getSystemPortName();
//                factory.get("/atmoserial/" + portName).broadcast(DeviceOutput.builder().port(portName)
//                        .timestamp(System.currentTimeMillis()).line(new String(newData)).build());
            }
            break;
        default:
            break;
        }
    }
}
