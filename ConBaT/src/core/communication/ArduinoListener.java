package core.communication;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortIOException;
import com.fazecast.jSerialComm.SerialPortTimeoutException;

public class ArduinoListener {

	private static ArduinoListener INSTANCE;

	protected ArrayList<SerialPort> portList;
	protected SerialPort currentPort;
	protected ArduinoHandler handler;
	protected String portName;
	protected InputStream in;
	protected boolean newData;
	protected StringBuilder message;

	long timeoutLimit;
	long startTime;

	public static ArduinoListener getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ArduinoListener();
		}
		return INSTANCE;
	}

	private ArduinoListener() {
		loadPorts();
	}

	protected void loadPorts() {
		portList = new ArrayList<SerialPort>();
		SerialPort[] portArray = SerialPort.getCommPorts();
		for (SerialPort p : portArray) {
			portList.add(p);
		}
	}

	public void setHandler(ArduinoHandler handler) {
		this.handler = handler;
	}

	// Connection related methods

	protected void openConnection() {
		openPort();
		currentPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 5000, 0);
		currentPort.setBaudRate(250000);
		in = currentPort.getInputStream();
		handler.setPort(currentPort);
		startTime = System.currentTimeMillis();
	}

	protected void openPort() {
		boolean found = false;
		if (portName != null) {
			for (SerialPort p : portList) {
				if (portName.equals(p.getSystemPortName())) {
					currentPort = p;
					found = true;
				}
			}
			if (!found) {
				System.out.println("FALHA AO SE CONNECTAR. PORTA " + portName + " NAO ENCONTRADA");
			}
		}
		if (!found) {
			Scanner in = new Scanner(System.in);
			int porta = -1;
			System.out.println("Ports found:");
			System.out.println("_____________");
			int count = 0;
			for (SerialPort p : portList) {
				System.out.println("(" + count + ") " + p.getSystemPortName());
				count++;
			}
			System.out.println("_____________");
			do {
				System.out.print("Select a port: ");
				porta = in.nextInt();
			} while (porta < 0 || porta >= portList.size());
			currentPort = portList.get(porta);
			portName = currentPort.getSystemPortName();
		}
		currentPort.openPort();
		System.out.println("Connection with port " + currentPort.getSystemPortName() + " was sucessful.");
	}

	protected void closeConnection() {
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		currentPort.closePort();
	}

	public void setPort(String portName) {
		this.portName = portName;
	}

	// Communication related methods

	public void start(long timeoutLimit) throws TimeoutException {
		this.timeoutLimit = timeoutLimit;
		newData = false;
		message = new StringBuilder("");

		openConnection();
		try {
			while (checkTimeout()) {
				readBuffer();
				if (newData) {
					handler.handle(getMsg());
				}
			}
		} catch (TimeoutException | RuntimeException e) {
			closeConnection();
			throw e;
		} finally {
			closeConnection();
		}
	}

	public void start() throws Exception {
		start(-1);
	}

	protected boolean checkTimeout() throws TimeoutException {
		if (timeoutLimit != -1 && System.currentTimeMillis() - startTime >= timeoutLimit) {
			closeConnection();
			throw new TimeoutException();
		}
		return true;
	}

	protected String getMsg() {
		newData = false;
		String msg = message.toString();
		message = new StringBuilder("");
		return msg;
	}

	protected void readBuffer() {
		int i;
		char c = '\0';
		char delimiter = '\n';
		try {
			i = in.read();
			if (i != -1) {
				c = (char) i;
				if (c == delimiter) {
					newData = true;
				} else if (c != '\r') {
					message.append(c);
				}
			}
		} catch (SerialPortIOException | SerialPortTimeoutException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
