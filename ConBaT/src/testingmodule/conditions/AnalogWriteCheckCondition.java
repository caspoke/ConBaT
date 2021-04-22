package testingmodule.conditions;

import testingmodule.enums.CheckingTime;
import testingmodule.enums.ConditionType;

public class AnalogWriteCheckCondition extends TriggeredCondition {
	int pin;

	public AnalogWriteCheckCondition(int pin, int value, ConditionType conditionType, CheckingTime checkingTime) {
		super("AW=" + pin, Integer.toString(value), conditionType, checkingTime);
		this.pin = pin;
	}

	public AnalogWriteCheckCondition(int pin, int value, ConditionType conditionType) {
		super("AW=" + pin, Integer.toString(value), conditionType);
		this.pin = pin;
	}

	public AnalogWriteCheckCondition(int pin, int value, CheckingTime checkingTime) {
		super("AW=" + pin, Integer.toString(value), checkingTime);
		this.pin = pin;
	}

	public AnalogWriteCheckCondition(int pin, int value) {
		super("AW=" + pin, Integer.toString(value));
		this.pin = pin;
	}

	@Override
	protected String conditionExpression() {
		return "Digital write of " + expectedValue + " on pin " + pin;
	}
}
