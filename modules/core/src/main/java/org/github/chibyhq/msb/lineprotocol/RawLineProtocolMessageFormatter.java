package org.github.chibyhq.msb.lineprotocol;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.github.chibyhq.msb.dto.DeviceOutput;
import org.github.chibyhq.msb.serial.SerialMessageFormatter;

final class RawLineProtocolMessageFormatter implements SerialMessageFormatter {
    @Override
    public MqttMessage getMessageForDeviceOutput(DeviceOutput deviceOutput) {
        String message = deviceOutput.getLine()+" "+deviceOutput.getTimestamp();
        return new MqttMessage(message.getBytes());
    }
}