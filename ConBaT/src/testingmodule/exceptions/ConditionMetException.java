package testingmodule.exceptions;

public class ConditionMetException extends Exception {
	public ConditionMetException(String msg) {
		super(">CONDITION MET!\n>>" + msg);
	}

	private static final long serialVersionUID = 1L;
}
