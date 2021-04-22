package collectingmodule;

import java.util.ArrayList;

import core.communication.ArduinoHandler;
import core.communication.Codes;
import core.expections.ContextEndedException;

public class ContextCollectorHandler extends ArduinoHandler {

	private ArrayList<String> readings;
	
	private ArrayList<Integer> analogSensorPins;
	private ArrayList<Integer> digitalSensorPins;
	private long duration;
	private int captureInterval;

	private boolean started;

	public ContextCollectorHandler(ArrayList<String> readings, ArrayList<Integer> analogSensorPins, ArrayList<Integer> digitalSensorPins,
			long duration, int captureInterval) {
		this.analogSensorPins = analogSensorPins;
		this.digitalSensorPins = digitalSensorPins;
		this.readings = readings;
		this.duration = duration;
		this.captureInterval = captureInterval;

		started = false;
	}

	public void handle(String msg) {
		if (msg.charAt(0)=='>')
			System.out.println("Mensagem recebida: " + msg);
		if (msg.length() > 2 && msg.charAt(1) == ':') {
			byte[] reply;
			char code = msg.charAt(0);
			String body = msg.substring(2);

			if (!started) {
				if (code == Codes.CONNECTION_CODE) {
					if (body.equals("CON")) {
						reply = makeMsg(Codes.SETUP_CODE, getCollectingInfo());
						port.writeBytes(reply, reply.length);
					}
				} else if (code == Codes.SETUP_CODE) {
					if (!started && body.contentEquals("OK")) {
						started = true;
						reply = makeMsg(Codes.CONNECTION_CODE, "SUC");
						port.writeBytes(reply, reply.length);
					}
				}
			} else {
				if (code == Codes.READING_CODE) {
					readings.add(body);
				} else if (code == Codes.EXCEPTION_CODE) {
					if (body.contentEquals("TIMEOUT")) {
						throw new ContextEndedException();
					}
				}
			}
		}
	}

	private byte[] makeMsg(char code, String body) {
		String msg = code + ":" + body + "\n\n";
		System.out.print(">>" + msg);
		return msg.getBytes();
	}

	private String getCollectingInfo() {
		StringBuilder content = new StringBuilder();
		boolean first;
		if (analogSensorPins.size()>0) {
			first = true;
			for (int pin : analogSensorPins) {
				if (!first)
					content.append(",");
				else
					first = false;
				content.append(pin);
			}
		} else
			content.append("null");
		content.append(";");	

		if (digitalSensorPins.size()>0) {
			first = true;	
			for (int pin : digitalSensorPins) {
				if (!first)
					content.append(",");
				else
					first = false;
				content.append(pin);
			}
		} else
			content.append("null");
		content.append(";");
		content.append(duration);
		content.append(";");
		content.append(captureInterval);
		return content.toString();
	}
}
