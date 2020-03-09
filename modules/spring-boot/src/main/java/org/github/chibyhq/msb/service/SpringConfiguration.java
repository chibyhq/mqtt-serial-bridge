package org.github.chibyhq.msb.service;

import org.github.chibyhq.msb.mqtt.MqttNamingStrategy;
import org.github.chibyhq.msb.mqtt.homie.HomieNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfiguration {

	@Bean MqttNamingStrategy getNamingStrategy() {
		return new HomieNamingStrategy("localhost");
	}
	

}
