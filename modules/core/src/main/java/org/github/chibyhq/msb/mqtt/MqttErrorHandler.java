package org.github.chibyhq.msb.mqtt;

public interface MqttErrorHandler {
   public void onException(Exception e);
}
