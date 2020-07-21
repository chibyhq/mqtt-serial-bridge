package org.github.chibyhq.msb.serial;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.github.chibyhq.msb.dto.PortInfo;

public interface SerialPortsManager {

	List<PortInfo> getPorts(boolean forceRequery);

	boolean addListener(String commPort, SerialMessageListener listener);

	boolean removeListener(String commPort, SerialMessageListener listener);

	boolean openPort(String commPort, Map<String, String> params);

	boolean closePort(String commPort);
	
	Optional<PortInfo> getPort(String commPort);

	void onIncomingSerialMessage(String commPort, String message) throws IOException;
	
	void onOutgoingSerialMessage(String commPort, String message) throws IOException;

}