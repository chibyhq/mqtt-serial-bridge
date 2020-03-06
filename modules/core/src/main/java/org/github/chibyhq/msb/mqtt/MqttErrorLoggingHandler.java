package org.github.chibyhq.msb.mqtt;

import lombok.extern.slf4j.Slf4j;

/**
 * Simple error handler that logs exceptions;
 */
@Slf4j
public class MqttErrorLoggingHandler implements MqttErrorHandler {

    @Override
    public void onException(Exception e) {
       log.error(e.getClass().getName()+" : "+e.getMessage(), e);
    }

}
