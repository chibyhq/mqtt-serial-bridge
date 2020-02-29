package org.github.chibyhq.msb.mqtt;

import org.github.chibyhq.msb.serial.SerialPortsManager;

public class SerialPortsManagerCommandExporter {
    SerialPortsManager manager;
    
    IMqttProperty commandProperty;

    public SerialPortsManagerCommandExporter(SerialPortsManager manager, IMqttProperty commandProperty) {
        this.manager = manager;
        this.commandProperty = commandProperty;
    }
}
