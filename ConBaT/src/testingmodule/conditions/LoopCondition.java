package testingmodule.conditions;

import testingmodule.enums.CheckingTime;
import testingmodule.enums.ConditionType;

public abstract class LoopCondition extends Condition {

	public LoopCondition(String key, String value, ConditionType conditionType, CheckingTime checkingTime) {
		super(key, value, conditionType, checkingTime);
	}

	public LoopCondition(String key, String expectedValue) {
		this(key, expectedValue, defaultConditionType, defaultCheckingTime);
	}

	public LoopCondition(String key, String expectedValue, ConditionType conditionType) {
		this(key, expectedValue, conditionType, defaultCheckingTime);
	}

	public LoopCondition(String key, String expectedValue, CheckingTime checkingTime) {
		this(key, expectedValue, defaultConditionType, checkingTime);
	}
}