package org.github.chibyhq.msb.serial;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.github.chibyhq.msb.dto.DeviceOutput;

public interface SerialMessageFormatter {
   MqttMessage getMessageForDeviceOutput(DeviceOutput deviceOutput);
}
