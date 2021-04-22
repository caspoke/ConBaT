package testingmodule;

import java.util.ArrayList;

import core.expections.ContextEndedException;
import testingmodule.conditions.ConditionSet;
import testingmodule.conditions.LoopCondition;
import testingmodule.conditions.TriggeredCondition;
import testingmodule.enums.CheckingTime;
import testingmodule.exceptions.AllConditionsMetException;
import testingmodule.exceptions.TestFailException;

public class ConditionChecker {

	ConditionSet conditions;

	// LOOP CONDITIONS
	ArrayList<ArrayList<LoopCondition>> loopConditionsByKeyId;
	StringBuilder variableValues[];

	// TRIGGERED CONDITIONS
	ArrayList<ArrayList<TriggeredCondition>> triggeredConditionsByKeyId;
	StringBuilder methodValues[];

	int anytimeConditionsSize;
	int endedAnyTimeConditions;
	boolean checkingUntilContextEnds;

	public ConditionChecker(ConditionSet conditions) {
		this.conditions = conditions;
		loopConditionsByKeyId = new ArrayList<ArrayList<LoopCondition>>();
		triggeredConditionsByKeyId = new ArrayList<ArrayList<TriggeredCondition>>();
		anytimeConditionsSize = 0;
		endedAnyTimeConditions = 0;
		checkingUntilContextEnds = false;
		organizeConditionsByKeyId();
	}

	/*
	 * Reorganize conditions by grouping them by key, so when there is a value
	 * change for that key, only those related conditions are updated; *
	 */
	private void organizeConditionsByKeyId() {
		LoopCondition lc;
		TriggeredCondition tc;
		String key;
		int keyId;
		ArrayList<String> requestedKeys;
		int keyIdSize;
		ArrayList<LoopCondition> relatedLoopConditions;
		ArrayList<TriggeredCondition> relatedTriggeredConditions;

		// ORGANIZING LOOP CONDITIONS

		keyIdSize = 0;
		requestedKeys = new ArrayList<String>();
		ArrayList<LoopCondition> loopConditions = conditions.getLoopConditions();
		for (int i = 0; i < loopConditions.size(); i++) {
			lc = loopConditions.get(i);
			key = lc.getKey();
			keyId = requestedKeys.indexOf(key);
			if (keyId == -1) {
				requestedKeys.add(key);
				keyId = keyIdSize;
				keyIdSize++;
				loopConditionsByKeyId.add(new ArrayList<LoopCondition>());
			}
			loopConditionsByKeyId.get(keyId).add(lc);
			if (lc.getCheckingTime() == CheckingTime.ANYTIME) {
				anytimeConditionsSize++;
			} else if (!checkingUntilContextEnds) {
				checkingUntilContextEnds = true;
			}
		}
		variableValues = new StringBuilder[keyIdSize];
		for (int i = 0; i < keyIdSize; i++) {
			variableValues[i] = new StringBuilder("");
			relatedLoopConditions = loopConditionsByKeyId.get(i);
			for (int j = 0; j < relatedLoopConditions.size(); j++) {
				relatedLoopConditions.get(j).setValueHistory(variableValues[i]);
			}
		}

		// ORGANIZING TRIGGERED CONDITIONS

		keyIdSize = 0;
		requestedKeys = new ArrayList<String>();
		ArrayList<TriggeredCondition> triggeredConditions = conditions.getTriggeredConditions();
		for (int i = 0; i < triggeredConditions.size(); i++) {
			tc = triggeredConditions.get(i);
			key = tc.getKey();
			keyId = requestedKeys.indexOf(key);
			if (keyId == -1) {
				requestedKeys.add(key);
				keyId = keyIdSize;
				keyIdSize++;
				triggeredConditionsByKeyId.add(new ArrayList<TriggeredCondition>());
			}
			triggeredConditionsByKeyId.get(keyId).add(tc);
			if (tc.getCheckingTime() == CheckingTime.ANYTIME) {
				anytimeConditionsSize++;
			} else if (!checkingUntilContextEnds) {
				checkingUntilContextEnds = true;
			}
		}
		methodValues = new StringBuilder[keyIdSize];
		for (int i = 0; i < keyIdSize; i++) {
			methodValues[i] = new StringBuilder("");
			relatedTriggeredConditions = triggeredConditionsByKeyId.get(i);
			for (int j = 0; j < relatedTriggeredConditions.size(); j++) {
				relatedTriggeredConditions.get(j).setValueHistory(methodValues[i]);
			}
		}
	}

	/*
	 * Creates a request msg that includes: - List of variables used in loop
	 * condition checkers - List of methods using in triggered condition checkers *
	 */
	public String getConditionsRequestList() {
		StringBuilder request = new StringBuilder();
		LoopCondition lc;
		TriggeredCondition tc;
		String key;
		boolean first;
		int size;

		ArrayList<String> requestedVariables = new ArrayList<String>();
		ArrayList<LoopCondition> loopConditions = conditions.getLoopConditions();
		first = true;
		size = loopConditions.size();
		if (size == 0) {
			request.append("NULL");
		} else {
			for (int i = 0; i < size; i++) {
				lc = loopConditions.get(i);
				key = lc.getKey();
				if (!requestedVariables.contains(key)) {
					requestedVariables.add(key);
					if (!first)
						request.append(",");
					else
						first = false;
					request.append(key);
				}
			}
		}
		request.append(";");

		ArrayList<String> requestedMethods = new ArrayList<String>();
		ArrayList<TriggeredCondition> triggeredConditions = conditions.getTriggeredConditions();
		first = true;
		size = triggeredConditions.size();
		if (size == 0) {
			request.append("NULL");
		} else {
			for (int i = 0; i < size; i++) {
				tc = triggeredConditions.get(i);
				key = tc.getKey();
				if (!requestedMethods.contains(key)) {
					if (!first)
						request.append(",");
					else
						first = false;
					request.append(key);
				}
			}
		}
		return request.toString();
	}

	public void handleLoopUpdate(String variableList) throws TestFailException, ContextEndedException {
		String[] elements = variableList.split(",");
		String value;
		ArrayList<LoopCondition> relatedConditions;
		boolean success = false;
		for (int i = 0; i < elements.length; i++) {
			value = elements[i];
			variableValues[i].append("," + value);
			relatedConditions = loopConditionsByKeyId.get(i);
			for (int j = 0; j < relatedConditions.size(); j++) {
				success = relatedConditions.get(j).check(value);
				if (success) {
					endedAnyTimeConditions++;
					if (!checkingUntilContextEnds && endedAnyTimeConditions == anytimeConditionsSize) {
						throw new AllConditionsMetException();
					}
				}
			}
		}
	}

	public void handleTriggeredEvent(String info) throws TestFailException, ContextEndedException {
		String[] elements = info.split(";");
		int keyId = Integer.parseInt(elements[0]);
		String value = elements[1];
		ArrayList<TriggeredCondition> relatedConditions = triggeredConditionsByKeyId.get(keyId);
		boolean success = false;
		for (int i = 0; i < relatedConditions.size(); i++) {
			success = relatedConditions.get(i).check(value);
			if (success) {
				endedAnyTimeConditions++;
				if (!checkingUntilContextEnds && endedAnyTimeConditions == anytimeConditionsSize) {
					throw new AllConditionsMetException();
				}
			}
		}

	}
}
