package org.github.chibyhq.msb.mqtt;

import java.util.Map;

public interface MqttNamingStrategy {
    String getMqttTopicNameForAttributes(Map<String,String> attributes);
}
