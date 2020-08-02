package org.github.chibyhq.msb.service.mqtt;
import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import io.moquette.BrokerConstants;
import io.moquette.broker.Server;
import io.moquette.broker.config.MemoryConfig;
import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty("msb.mqtt.server.enable")
@Slf4j
public class MoquetteWrapper {


    @Value("${msb.mqtt.server.port:1883}")
    private int serverPort;

    @Value("${msb.mqtt.server.wsport:8883}")
    private int websocketPort;
    
    @Value("${msb.mqtt.server.host:0.0.0.0}")
    private String host;

    private Server server;
    private MemoryConfig config;

    @PostConstruct
    public void start() throws IOException {
        log.info("Starting MQTT on host: {} and port: {} with websocket port: {}", host, serverPort, websocketPort);

        config = new MemoryConfig(new Properties());
        config.setProperty("port", Integer.toString(serverPort));
        config.setProperty("websocket_port", Integer.toString(websocketPort));
        config.setProperty("host", host);
        config.setProperty(BrokerConstants.ALLOW_ANONYMOUS_PROPERTY_NAME, "true");

        server = new Server();
        server.startServer(config);
        log.info("Moquette started successfully");
        
        // TODO: Add a Spring Event to indicate when the MQTT broker is activated
        //       If server is enabled, the client should only try and connect then.
    }

    @PreDestroy
    public void stop() {
        server.stopServer();
    }
    
}
