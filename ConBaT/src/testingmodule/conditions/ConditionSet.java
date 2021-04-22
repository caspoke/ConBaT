package testingmodule.conditions;

import java.util.ArrayList;

import testingmodule.exceptions.TestFailException;

public class ConditionSet {

	private ArrayList<LoopCondition> loopConditions;
	private ArrayList<TriggeredCondition> triggeredConditions;

	public ConditionSet() {
		loopConditions = new ArrayList<LoopCondition>();
		triggeredConditions = new ArrayList<TriggeredCondition>();
	}

	public void add(Condition c) {
		if (c instanceof LoopCondition) {
			loopConditions.add((LoopCondition) c);
		} else if (c instanceof TriggeredCondition) {
			triggeredConditions.add((TriggeredCondition) c);
		}
	}

	public ArrayList<TriggeredCondition> getTriggeredConditions() {
		return this.triggeredConditions;
	}

	public ArrayList<LoopCondition> getLoopConditions() {
		return this.loopConditions;
	}

	public void checkAll() throws TestFailException {
		LoopCondition lc;
		TriggeredCondition tc;
		for (int i = 0; i < loopConditions.size(); i++) {
			lc = loopConditions.get(i);
			lc.finalCheck();
		}
		for (int i = 0; i < triggeredConditions.size(); i++) {
			tc = triggeredConditions.get(i);
			tc.finalCheck();
		}
	}

	public String toString() {
		StringBuilder s = new StringBuilder("Conditions info:\n\n");
		LoopCondition lc;
		TriggeredCondition tc;
		int count = 1;
		for (int i = 0; i < loopConditions.size(); i++) {
			lc = loopConditions.get(i);
			s.append("Condition #" + count++ + "\n");
			s.append(lc.toString());
			s.append("\n");
		}
		for (int i = 0; i < triggeredConditions.size(); i++) {
			tc = triggeredConditions.get(i);
			s.append("Condition #" + count++ + "\n");
			s.append(tc.toString());
			s.append("\n");
		}

		return s.toString();
	}

	public boolean isEmpty() {
		return loopConditions.size() == 0 && triggeredConditions.size() == 0;
	}
}
