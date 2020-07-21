package org.github.chibyhq.msb.service;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.github.chibyhq.msb.lineprotocol.RawLineProtocolMessageFormatter;
import org.github.chibyhq.msb.mqtt.MqttErrorHandler;
import org.github.chibyhq.msb.mqtt.MqttErrorLoggingHandler;
import org.github.chibyhq.msb.mqtt.MqttNamingStrategy;
import org.github.chibyhq.msb.mqtt.SerialToMqttForwardingListener;
import org.github.chibyhq.msb.mqtt.homie.HomieNamingStrategy;
import org.github.chibyhq.msb.serial.SerialMessageFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class SpringConfiguration {

    @Bean
    MqttNamingStrategy mqttNamingStrategy() {
        return new HomieNamingStrategy();
    }

    @Bean
    @ConditionalOnProperty("msb.mqtt.forward")
    MqttClient mqttClient(@Value("${msb.mqtt.client.server.uri:tcp://0.0.0.0:1883}") String serverURI,
            @Value("${msb.mqtt.client.id}") String clientId) throws MqttException {
        log.debug("Creating MQTT client to URI {}, client ID {}", serverURI, clientId);
        MqttClient mqttClient = new MqttClient(serverURI, clientId);
        return mqttClient;
    }

    @Bean
    SerialMessageFormatter messageFormatter() {
        return new RawLineProtocolMessageFormatter();
    }

    @Bean
    MqttErrorHandler errorHandler() {
        return new MqttErrorLoggingHandler();
    }

    @Bean
    @Primary
    @ConditionalOnProperty("msb.mqtt.forward")
    SerialToMqttForwardingListener serialToMqttForwardingListener(MqttNamingStrategy namingStrategy,
            MqttClient mqttClient, SerialMessageFormatter formatter, MqttErrorHandler mqttErrorHandler) {
        return new SerialToMqttForwardingListener(namingStrategy, mqttClient, formatter, mqttErrorHandler);
    }
    
}
