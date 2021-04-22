package testingmodule.conditions;

import testingmodule.enums.CheckingTime;
import testingmodule.enums.ConditionType;

public class DigitalWriteCheckCondition extends TriggeredCondition {
	int pin;

	public DigitalWriteCheckCondition(int pin, int value, ConditionType conditionType, CheckingTime checkingTime) {
		super("DW=" + pin, Integer.toString(value), conditionType, checkingTime);
		this.pin = pin;
	}

	public DigitalWriteCheckCondition(int pin, int value, ConditionType conditionType) {
		super("DW=" + pin, Integer.toString(value), conditionType);
		this.pin = pin;
	}

	public DigitalWriteCheckCondition(int pin, int value, CheckingTime checkingTime) {
		super("DW=" + pin, Integer.toString(value), checkingTime);
		this.pin = pin;
	}

	public DigitalWriteCheckCondition(int pin, int value) {
		super("DW=" + pin, Integer.toString(value));
		this.pin = pin;
	}

	public DigitalWriteCheckCondition(int pin, String value, ConditionType conditionType, CheckingTime checkingTime) {
		super("DW=" + pin, value.contentEquals("HIGH") ? "1" : value.contentEquals("LOW") ? "0" : value, conditionType,
				checkingTime);
		this.pin = pin;
	}

	public DigitalWriteCheckCondition(int pin, String value, ConditionType conditionType) {
		super("DW=" + pin, value.contentEquals("HIGH") ? "1" : value.contentEquals("LOW") ? "0" : value, conditionType);
		this.pin = pin;
	}

	public DigitalWriteCheckCondition(int pin, String value, CheckingTime checkingTime) {
		super("DW=" + pin, value.contentEquals("HIGH") ? "1" : value.contentEquals("LOW") ? "0" : value, checkingTime);
		this.pin = pin;
	}

	public DigitalWriteCheckCondition(int pin, String value) {
		super("DW=" + pin, value.contentEquals("HIGH") ? "1" : value.contentEquals("LOW") ? "0" : value);
		this.pin = pin;
	}

	@Override
	protected String conditionExpression() {
		return "Digital write of " + expectedValue + " on pin " + pin;
	}

}
