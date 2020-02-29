package org.github.chibyhq.msb.mqtt.homie;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.github.chibyhq.msb.mqtt.IMqttProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomieProperty implements IMqttProperty{
   
   @Builder.Default
   String prefix = "homie";
   String device;
   String node;
   String property;
   
   @Override
    public String getMqttTopicName() {
       return Arrays.asList(prefix,device, node, property).stream().collect(Collectors.joining("/"));
    }
}
