package org.github.chibyhq.msb.lineprotocol;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.github.chibyhq.msb.dto.DeviceOutput;
import org.github.chibyhq.msb.serial.SerialMessageFormatter;

public class SimpleLineProtocolFormatter implements SerialMessageFormatter {

    @Override
    public MqttMessage getMessageForDeviceOutput(DeviceOutput deviceOutput) {
        Metric m = Metric.builder().name("out").tag("port",deviceOutput.getPort())
                      .value("text", deviceOutput.getLine())
                      .timestamp(deviceOutput.getTimestamp())
                      .build();
        return new MqttMessage(m.toString().getBytes());
    }

}
