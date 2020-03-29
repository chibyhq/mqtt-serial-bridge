package org.github.chibyhq.msb.serial;

import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

@DisabledIfEnvironmentVariable(named="GITHUB_RUN_ID", matches=".*")
public class ListenerSimpleJSerialCommTest {

	public static class Listener implements SerialPortMessageListener {

		@Override
		public int getListeningEvents() {
			return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
		}
		
		@Override
	    public byte[] getMessageDelimiter() { return new byte[] { (byte)0x0D, (byte)0x0A }; }

	    @Override
	    public boolean delimiterIndicatesEndOfMessage() { return true; }

		@Override
		public void serialEvent(SerialPortEvent event) {
			SerialPort port = event.getSerialPort();
			switch (event.getEventType()) {
			case SerialPort.LISTENING_EVENT_DATA_RECEIVED:
				String portName = port.getSystemPortName();
				System.out.println(portName+">> "+new String(event.getReceivedData()));
				
				break;
			default:
				break;
			}
		}

	}

	public static void main(String args[]) {
		SerialPort comPort = SerialPort.getCommPort("/dev/ttyACM0");
		comPort.setComPortParameters(115200, 8, 1, 0);
		comPort.addDataListener(new ListenerSimpleJSerialCommTest.Listener());
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 200, 0);
		comPort.openPort();
		System.out.println("Open ? "+comPort.isOpen());
		System.out.println("Now listening...");

	}

}
