package core.communication;

import com.fazecast.jSerialComm.SerialPort;

import core.expections.ContextEndedException;
import testingmodule.exceptions.TestFailException;

public abstract class ArduinoHandler {
	protected SerialPort port;

	public void setPort(SerialPort port) {
		this.port = port;
	}

	public abstract void handle(String msg) throws ContextEndedException, TestFailException;
}
