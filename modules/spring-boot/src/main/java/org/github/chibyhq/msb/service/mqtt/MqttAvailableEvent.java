package org.github.chibyhq.msb.service.mqtt;

import org.springframework.context.ApplicationEvent;

public class MqttAvailableEvent extends ApplicationEvent {

    private static final long serialVersionUID = -6199044686028597534L;

    public MqttAvailableEvent(Object source) {
        super(source);
    }

}
