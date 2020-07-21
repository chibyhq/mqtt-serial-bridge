package org.github.chibyhq.msb.mqtt.homie;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.github.chibyhq.msb.dto.DeviceOutput;
import org.github.chibyhq.msb.dto.PortInfo;
import org.github.chibyhq.msb.mqtt.MqttNamingStrategy;

public class HomieNamingStrategy implements MqttNamingStrategy {

    String hostname;

    public HomieNamingStrategy(String host) {
        this.hostname = host;
        if (new String(hostname).isEmpty()) {
            initializeHostname();
        }
    }
    
    public HomieNamingStrategy() {
       initializeHostname();
    }

    private final void initializeHostname() {
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "unknown-host";
        }
    }

    @Override
    public String getTopicName(DeviceOutput output) {
        return String.format("homie/%s/serial/%s/out", hostname, output.getPort());
    }

    @Override
    public String getIncomingMqttCommandTopicForPort(PortInfo portInfo) {
        return String.format("homie/%s/serial/%s/in", hostname, portInfo.getDescriptor());
    }

}
