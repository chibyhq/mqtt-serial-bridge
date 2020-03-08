package org.github.chibyhq.msb.service;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.github.chibyhq.msb")
public class Application {
    
    public static void main(String[] args) throws IOException {
        @SuppressWarnings("unused")
		SpringApplication application = new SpringApplication(Application.class);
    }
}
