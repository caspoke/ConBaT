package testingmodule.conditions;

import testingmodule.enums.CheckingTime;
import testingmodule.enums.ConditionType;

public class VariableCheckCondition extends LoopCondition {

	public VariableCheckCondition(String variableName, int value, ConditionType conditionType,
			CheckingTime checkingTime) {
		super(variableName, Integer.toString(value), conditionType, checkingTime);
	}

	public VariableCheckCondition(String variableName, int value, ConditionType conditionType) {
		super(variableName, Integer.toString(value), conditionType);
	}

	public VariableCheckCondition(String variableName, int value, CheckingTime checkingTime) {
		super(variableName, Integer.toString(value), checkingTime);
	}

	public VariableCheckCondition(String variableName, int value) {
		super(variableName, Integer.toString(value));
	}

}
