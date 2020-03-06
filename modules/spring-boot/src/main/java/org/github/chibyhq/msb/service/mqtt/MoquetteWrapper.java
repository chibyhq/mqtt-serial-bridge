package org.github.chibyhq.msb.service.mqtt;
import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.moquette.BrokerConstants;
import io.moquette.server.Server;
import io.moquette.server.config.MemoryConfig;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MoquetteWrapper {


    @Value("${mqtt.serverport:1883}")
    private int serverPort;

    @Value("${mqtt.host:0.0.0.0}")
    private String host;

    @Value("${mqtt.websocket_port:8080}")
    private int websocketPort;

    private Server server;
    private MemoryConfig config;

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
    }

    public void stop() {
        server.stopServer();
    }
    
}
