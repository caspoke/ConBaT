package testingmodule;

import core.Context;
import core.communication.ArduinoHandler;
import core.communication.Codes;
import core.expections.ContextEndedException;
import testingmodule.conditions.ConditionSet;
import testingmodule.exceptions.SetupException;
import testingmodule.exceptions.TestFailException;

public class ContextTestHandler extends ArduinoHandler {

	private ConditionChecker conditionChecker;
	private ContextSimulator contextRequestHandler;

	private long startTime;
	private boolean started;
	
	public ContextTestHandler(ConditionSet conditions, Context context, int pinMap[]) {
		conditionChecker = new ConditionChecker(conditions);
		contextRequestHandler = new ContextSimulator(context, pinMap);
		startTime = -1;
		started = false;
	}

	public void handle(String msg) throws ContextEndedException, TestFailException {
		if (msg.charAt(0) == '>')
			System.out.println(msg);
		if (msg.length() > 2 && msg.charAt(1) == ':') {
			byte[] reply;
			char code = msg.charAt(0);
			String body = msg.substring(2);

			if (code == Codes.EXCEPTION_CODE) {
				if (body.contentEquals("VARIABLENOTFOUND"))
					throw new SetupException("Requested variable was not found");
				else if (body.contentEquals("METHODNOTFOUND"))
					throw new SetupException("Requested method was not found");
			}			
			else if (!started) {
				if (code == Codes.CONNECTION_CODE) {
					if (body.equals("CON")) {
						reply = makeMsg(Codes.SETUP_CODE, conditionChecker.getConditionsRequestList());
						port.writeBytes(reply, reply.length);
					}
				} else if (code == Codes.SETUP_CODE) {
					if (!started && body.equals("OK")) {
						started = true;
						// conditionChecker.processRequestConfirmation(body);
						System.out.println("Variaveis salvas com sucesso");
						reply = makeMsg(Codes.CONNECTION_CODE, "SUC");
						port.writeBytes(reply, reply.length);
					}
				}
			} else {
				if (code == Codes.READING_REQUEST_CODE) {
					if (startTime == -1) {
						startTime = System.currentTimeMillis();
					}
					//System.out.println("elapsed time = "+(System.currentTimeMillis()-startTime));
					String reading = "";					
					reading = contextRequestHandler.getReading(System.currentTimeMillis() - startTime,
							Integer.valueOf(body));
					reply = makeMsg(Codes.READING_REQUEST_CODE, reading);
					port.writeBytes(reply, reply.length);
				} else if (code == Codes.LOOP_UPDATE_CODE) {
					conditionChecker.handleLoopUpdate(body);
				} else if (code == Codes.TRIGGERED_UPDATE_CODE) {
					conditionChecker.handleTriggeredEvent(body);
				} else {
					reply = makeMsg('R', "");
					port.writeBytes(reply,  reply.length);
				}
			}
		}
	}

	private byte[] makeMsg(char code, String body) {
		String msg = code + ":" + body + "\n\n";
		//System.out.print(">>" + msg);
		return msg.getBytes();
	}
}
