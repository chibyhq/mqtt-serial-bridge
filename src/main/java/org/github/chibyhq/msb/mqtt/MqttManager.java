package org.github.chibyhq.msb.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MqttManager {
    MqttClient client;

    MqttNamingStrategy namingStrategy;
}
