package org.github.chibyhq.msb.serial;

import org.github.chibyhq.msb.dto.DeviceOutput;
import org.github.chibyhq.msb.dto.PortInfo;

public interface SerialMessageListener {
   String getName();
   void onSerialMessage(DeviceOutput output);
   void onPortOpen(PortInfo port);
   void setSerialPortsManager(SerialPortsManager serialPortsManagerAdapter);
}
