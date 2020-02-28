package org.github.chibyhq.msb.mqtt;

import java.util.Map;

public class HomieMqttNamingStrategy implements MqttNamingStrategy {
    private String defaultPrefix = "homie/";
    
    @Override
    public String getMqttTopicNameForAttributes(Map<String, String> attributes) {
      StringBuilder sb = new StringBuilder();
      if(attributes.containsKey(HomieParamsEnum.PREFIX.toString())) {
          sb.append(attributes.get(HomieParamsEnum.PREFIX.toString())+"/");
      }else {
          sb.append(defaultPrefix);
      }
      sb.append(attributes.get(HomieParamsEnum.DEVICE.toString()));
      sb.append("/");
      sb.append(attributes.get(HomieParamsEnum.NODE.toString()));
      sb.append("/");
      sb.append(attributes.get(HomieParamsEnum.PROPERTY.toString()));
      return sb.toString();
    }

    public String getDefaultPrefix() {
        return defaultPrefix;
    }

    public void setDefaultPrefix(String defaultPrefix) {
        this.defaultPrefix = defaultPrefix;
    }
}
