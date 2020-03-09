package org.github.chibyhq.msb.serial;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.github.chibyhq.msb.dto.PortInfo;

public interface SerialPortsManager {

	List<PortInfo> onGetPorts();

	boolean addListener(String commPort, SerialMessageListener listener);

	boolean removeListener(String commPort, SerialMessageListener listener);

	boolean onOpenPort(String commPort, Map<String, String> params);

	void onClosePort(String commPort);

	void onIncomingMessage(String commPort, String message) throws IOException;

}