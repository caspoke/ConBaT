package testingmodule.conditions;

import testingmodule.enums.CheckingTime;
import testingmodule.enums.ConditionType;

public class TriggeredCondition extends Condition {

	public TriggeredCondition(String key, String value, ConditionType conditionType, CheckingTime checkingTime) {
		super(key, value, conditionType, checkingTime);
	}

	public TriggeredCondition(String key, String expectedValue) {
		this(key, expectedValue, defaultConditionType, defaultCheckingTime);
	}

	public TriggeredCondition(String key, String expectedValue, ConditionType conditionType) {
		this(key, expectedValue, conditionType, defaultCheckingTime);
	}

	public TriggeredCondition(String key, String expectedValue, CheckingTime checkingTime) {
		this(key, expectedValue, defaultConditionType, checkingTime);
	}
}
