package org.github.chibyhq.msb.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

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
//		try {
//			while (true) {
//				System.out.println("Reading... " );
//				while (comPort.bytesAvailable() <= 0)
//					Thread.sleep(20);
//				System.out.println(comPort.isOpen());
//				System.out.println(comPort.bytesAvailable());
//				byte[] readBuffer = new byte[comPort.bytesAvailable()];
//				int numRead = comPort.readBytes(readBuffer, readBuffer.length);
//				System.out.println("Read " + numRead + " bytes.");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

}
