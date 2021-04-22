package testingmodule;

import java.util.concurrent.TimeoutException;

import core.Context;
import core.ContextUtil;
import core.communication.ArduinoListener;
import core.expections.ContextEndedException;
import testingmodule.conditions.Condition;
import testingmodule.conditions.ConditionSet;
import testingmodule.exceptions.AllConditionsMetException;
import testingmodule.exceptions.TestFailException;

//CLASE PARA TESTES ALEATORIOS

public class ContextTest {
	protected final int MAX_PIN_NUMBER = 100;
	protected final int DEFAULT_TIMEOUT_LIMIT = 20000;
	protected final int CONNECTION_TIMEOUT = 30000;
	
	protected ArduinoListener listener;	
	protected TestState testState;

	//Parameters set by the tester
	protected ConditionSet conditions;
	protected Context context;
	protected int pinMap[];

	public ContextTest() {
		//testController = new ContextTestController();
		listener = ArduinoListener.getInstance();
		conditions = new ConditionSet();
		pinMap = new int[MAX_PIN_NUMBER];
		for (int i=0; i<MAX_PIN_NUMBER; i++) {
			pinMap[i] = -1;
		}
		testState = new TestState();
		testState.setTimeoutLimit(DEFAULT_TIMEOUT_LIMIT);
	}
	
	public void runTest() {
		if (conditions.isEmpty())
			throw new TestFailException("No conditions were specified");
		
		boolean result = true;
		testState.start();

		listener.setHandler(new ContextTestHandler(conditions, context, pinMap));
		try {
			listener.start(testState.getTimeoutLimit());
		} catch (TestFailException e) {
			System.out.println("condition of conditionType.FAIL was met");
			result = false;
			throw e;
		} catch (TimeoutException | ContextEndedException e) {
			try {
				//e.printStackTrace();
				conditions.checkAll();
			} catch (TestFailException j) {
				System.out.println("test fail on final check");
				result = false;
				throw j;
			}

		} catch (AllConditionsMetException e) {
			// DO NOTHING
			// This is thrown when there are no ATTHEEND conditions and all ANYTIME
			// conditions were checked already
			// So no need to double check - Test passed
		}
		testState.setResult(result);
		testState.stop();
		
		printTestResult();
	}
	
	public void setPort(String portName) {
		listener.setPort(portName);
	}
	

	//SETUP METHODS
	public void setContext(String contextFilePath) {
		try {
			this.context = ContextUtil.readFromFile(contextFilePath);
			testState.setTimeoutLimit(context.getDuration() + CONNECTION_TIMEOUT);
		} catch (Exception e) {
			this.context = null;
		}
	}

	public void setContext(Context context) {
		this.context = context;
		testState.setTimeoutLimit(context.getDuration() + CONNECTION_TIMEOUT);
	}
	
	public void addCondition(Condition c) {
		conditions.add(c);
	}
	
	public void resetConditionsAndState() {
		conditions = new ConditionSet();
		testState = new TestState();
	}

	public void mapSensor(int systemSensorPin, int contextSensorIndex) {
		System.out.println("colocando na coluna"+systemSensorPin);
		pinMap[systemSensorPin] = contextSensorIndex;
	}

	
	
	
	public void printTestResult() {
		System.out.println("------------------------\n");
		System.out.println(testState.toString());
		System.out.println("------------------------\n");
		System.out.println(conditions.toString());
	}
}