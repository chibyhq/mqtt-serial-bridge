package org.github.chibyhq.msb.serial;

import org.github.chibyhq.msb.dto.DeviceOutput;

public interface SerialMessageListener {
   String getName();
   void onMessage(DeviceOutput output);
}
