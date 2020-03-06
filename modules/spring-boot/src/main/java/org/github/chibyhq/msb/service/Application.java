package org.github.chibyhq.msb.service;

import java.io.IOException;

import org.apache.logging.log4j.message.AsynchronouslyFormattable;
import org.github.chibyhq.msb.service.mqtt.MoquetteWrapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ComponentScan
@Slf4j
public class Application {
    
    public static void main(String[] args) throws IOException {
        SpringApplication application = new SpringApplication(Application.class);
        final ApplicationContext context = application.run(args);
        MoquetteWrapper server = context.getBean(MoquetteWrapper.class);
        server.start();
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.stop();
                log.info("Moquette Server stopped");
            }
        });
    }
}
