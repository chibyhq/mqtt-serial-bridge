package org.github.chibyhq.msb.naming;

import java.util.Map;

public interface MqttNamingAdapter {

    String getTopicNameForAttributes(Map<String,String> attributes);
}
