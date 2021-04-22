package testingmodule.conditions;

import testingmodule.enums.CheckingTime;
import testingmodule.enums.ConditionType;
import testingmodule.exceptions.TestFailException;

public abstract class Condition {

	protected String lastValue;		//NECESSÁRIO?
	protected boolean result;		//NECESSÁRIO?
	
	protected boolean ended;

	protected String key;
	protected String expectedValue;
	protected StringBuilder valueHistory;
	protected ConditionType conditionType;
	protected CheckingTime checkingTime;

	protected static ConditionType defaultConditionType = ConditionType.SUCCESS;
	protected static CheckingTime defaultCheckingTime = CheckingTime.ANYTIME;

	protected Condition(String key, String expectedValue, ConditionType conditionType, CheckingTime checkingTime) {
		this.key = key;
		this.expectedValue = expectedValue;
		this.conditionType = conditionType;
		this.checkingTime = checkingTime;

		// this.result = false;
		this.ended = false;
	}

	public Condition(String key, String expectedValue) {
		this(key, expectedValue, defaultConditionType, defaultCheckingTime);
	}

	public Condition(String key, String expectedValue, ConditionType conditionType) {
		this(key, expectedValue, conditionType, defaultCheckingTime);
	}

	public Condition(String key, String expectedValue, CheckingTime checkingTime) {
		this(key, expectedValue, defaultConditionType, checkingTime);
	}

	public String toString() {
		StringBuilder s = new StringBuilder("\t[" + conditionExpression() + "] ~ " + (result ? "PASSED" : "FAILED"));
		s.append("\n\t- " + (ended
				? ((result && conditionType == ConditionType.SUCCESS)
						|| (!result && conditionType == ConditionType.FAIL) ? "Condition met" : "Condition not met")
				: "Test failed before condition was met"));
		s.append(
				"\n\t- Condition type: " + (conditionType == ConditionType.SUCCESS ? "Success if met" : "Fail if met"));
		s.append("\n\t- Checking time: " + (checkingTime == CheckingTime.ANYTIME ? "Anytime" : "At the end") + "\n");
		return s.toString();
	}

	protected String conditionExpression() {
		return key + " = " + expectedValue;
	}

	public ConditionType getConditionType() {
		return this.conditionType;
	}

	public String getKey() {
		return this.key;
	}

	public StringBuilder getValueHistory() {
		return this.valueHistory;
	}

	public void setValueHistory(StringBuilder valueHistory) {
		this.valueHistory = valueHistory;
	}

	public CheckingTime getCheckingTime() {
		return this.checkingTime;
	}

	public boolean check(String value) throws TestFailException {
		if (!ended && value.equals(expectedValue)) {
			conditionMet();
			return true;
		} else
			return false;
	}

	public void finalCheck() throws TestFailException {
		if (!ended) {
			if (checkingTime == CheckingTime.ANYTIME) {
				forceEnd();
			} else {
				String values[] = valueHistory.toString().split(",");
				for (int i = 0; i < values.length; i++) {
					check(values[i]);
					if (ended)
						i = values.length;
				}
				if (!ended) {
					forceEnd();
				}
			}
		}
	}

	protected void forceEnd() throws TestFailException {
		if (conditionType == ConditionType.SUCCESS) {
			fail();
			throw new TestFailException("Condition not met: " + key + " == " + expectedValue);
		} else {
			success();
		}
	}

	protected void conditionMet() throws TestFailException {
		if (conditionType == ConditionType.SUCCESS) {
			success();
		} else {
			fail();
			throw new TestFailException("Fail condition met: " + key + " == " + expectedValue);
		}
	}

	protected void success() {
		ended = true;
		result = true;
	}

	protected void fail() {
		ended = true;
		result = false;
	}

	public boolean ended() {
		return this.ended;
	}
}
