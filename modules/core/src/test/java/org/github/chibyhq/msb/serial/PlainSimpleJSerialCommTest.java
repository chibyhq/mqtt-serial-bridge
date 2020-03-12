package org.github.chibyhq.msb.serial;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fazecast.jSerialComm.SerialPort;

public class PlainSimpleJSerialCommTest {

	public static void main(String args[]) {
		SerialPort comPort = SerialPort.getCommPort("/dev/ttyACM0");
		comPort.setComPortParameters(115200, 8, 1, 0);
		comPort.openPort();

		try {
			while (true) {
				while (comPort.bytesAvailable() <= 0)
					Thread.sleep(20);
				System.out.println(comPort.isOpen());
				System.out.println(comPort.bytesAvailable());
				byte[] readBuffer = new byte[comPort.bytesAvailable()];
				int numRead = comPort.readBytes(readBuffer, readBuffer.length);
				System.out.println("Read " + numRead + " bytes.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
