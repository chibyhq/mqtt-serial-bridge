package org.github.chibyhq.msb.mqtt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.github.chibyhq.msb.dto.DeviceOutput;
import org.github.chibyhq.msb.dto.PortInfo;
import org.github.chibyhq.msb.serial.SerialMessageFormatter;
import org.github.chibyhq.msb.serial.SerialMessageListener;
import org.github.chibyhq.msb.serial.SerialPortsManager;

import lombok.extern.slf4j.Slf4j;

/**
 * Forwards incoming serial messages to an MQTT topic, via :
 * <ul>
 * <li>A pre-configured MQTT client instance
 * <li>A MQTT destination topic naming strategy (to parse Serial messages and
 * forward them)
 * <li>A Message Formatter (to convert incoming serial messages into an MQTT
 * message format)
 * <li>A pre-configured MQTT error handler, in case messages cannot be forwarded
 * </ul>
 * 
 * @author bcopy
 *
 */
@Slf4j
public class SerialToMqttForwardingListener implements SerialMessageListener, IMqttMessageListener {

    MqttNamingStrategy namingStrategy;

    MqttClient mqttClient;

    SerialMessageFormatter formatter;

    MqttErrorHandler mqttErrorHandler;

    SerialPortsManager serialPortsManager;

    /**
     * Indicates that we are ready to connect to MQTT. Unless this flag is set
     * to <code>true</code>, the MQTT client will not be connected or used at
     * all. This is useful for embedded MQTT servers, that may not be readily
     * available.
     */
    Boolean mqttReady = false;

    Set<String> subscribedTopicsSet = new HashSet<>();

    Boolean enabled = false;

    public SerialToMqttForwardingListener(MqttNamingStrategy namingStrategy, MqttClient mqttClient,
            SerialMessageFormatter formatter, MqttErrorHandler mqttErrorHandler) {
        super();
        this.namingStrategy = namingStrategy;
        this.mqttClient = mqttClient;
        this.formatter = formatter;
        this.mqttErrorHandler = mqttErrorHandler;
    }

    public SerialToMqttForwardingListener(MqttNamingStrategy namingStrategy, MqttClient mqttClient,
            SerialMessageFormatter formatter) {
        this(namingStrategy, mqttClient, formatter, new MqttErrorLoggingHandler());
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void onSerialMessage(DeviceOutput output) {
        if (mqttReady) {
            String destinationTopic = namingStrategy.getTopicName(output);
            try {
                if (!mqttClient.isConnected()) {
                    mqttClient.connect();
                }
            } catch (MqttException e) {
                log.warn("MQTT client could not connect upon message : {}", output, e);
            }
            if (mqttClient.isConnected()) {
                try {
                    mqttClient.publish(destinationTopic, formatter.getMessageForDeviceOutput(output));
                } catch (Exception e) {
                    mqttErrorHandler.onException(e);
                }
            } else {
                log.debug("MQTT client not connected, message ignored : {}", output);
            }
        } else {
            if (log.isTraceEnabled())
                log.trace("MQTT not ready, ignoring serial message");
        }
    }

    @Override
    public void onPortOpen(PortInfo portInfo) {
        String mqttTopic = namingStrategy.getIncomingMqttCommandTopicForPort(portInfo.getSystemPortName());
        // The set will not add a duplicate entry if it is already there
        subscribedTopicsSet.add(mqttTopic);

        if (mqttReady) {
            try {
                if (!mqttClient.isConnected()) {
                    mqttClient.connect();
                }
                // Immediately subscribe to the corresponding incoming MQTT
                // topic for this port
                subscribeToCommandTopic();
            } catch (MqttException e) {
                log.error("Could not connect MQTT client or to command topics upon port open {} ", portInfo.getSystemPortName());
            }
        }

    }

    public void subscribeToCommandTopic() {
        try {
            for (String topic : subscribedTopicsSet) {
                mqttClient.subscribe(topic, this);
            }
            enabled = true;
        } catch (MqttException e) {
            log.error("Unable to subscribe to command topics", e);
        }
    }

    @Override
    public void messageArrived(String mqttTopic, MqttMessage message) throws Exception {
        Optional<String> port = namingStrategy.getPortForTopicNameOrMessage(mqttTopic, message);
        if (port.isPresent()) {
            if (serialPortsManager != null) {
                try {
                    String messagePayload = new String(message.getPayload());
                    log.debug("Sending on port {} message : {}", port.get(), messagePayload);
                    serialPortsManager.onIncomingSerialMessage(port.get(), messagePayload);
                } catch (IOException ioe) {
                    log.error("Exception while publishing incoming MQTT message on serial port {}", port.get());
                }

            } else {
                log.error("Serial Ports manager is not set, cannot publish incoming MQTT message on serial port {}",
                        port.get());
            }
        }
    }

    public void setSerialPortsManager(SerialPortsManager serialPortsManager) {
        this.serialPortsManager = serialPortsManager;
    }

    /**
     * If the port is closed, we must unsubscribe from the corresponding MQTT
     * topic(s).
     */
    @Override
    public void onPortClosed(String port) {
        // Unsubscribe to the corresponding incoming MQTT topic
        // for this port
        String mqttTopic = namingStrategy.getIncomingMqttCommandTopicForPort(port);
        if (subscribedTopicsSet.contains(mqttTopic)) {
            try {
                mqttClient.unsubscribe(mqttTopic);
                subscribedTopicsSet.remove(mqttTopic);
                enabled = false;
            } catch (MqttException e) {
                log.error("Upon port {} disconnect : Could not unsubscribe MQTT topic {}", port, mqttTopic, e);
            }

        }
    }

    public void onMqttServerReady() {
        log.debug("MQTT now ready for SerialToMqtt listener");
        // Connect client if needed
        try {
            if (!mqttClient.isConnected()) {
                mqttClient.connect();
            }
        } catch (MqttException e) {
            log.debug("MQTT now ready but client could not connect", e);
        }

        // Subscribe to command topic if needed
        if (!enabled) {
            subscribeToCommandTopic();
        }
        mqttReady = true;
    }

}
