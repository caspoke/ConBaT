package testingmodule.exceptions;

import junit.framework.AssertionFailedError;

public class TestFailException extends AssertionFailedError {
	public TestFailException(String msg) {
		super("TEST FAILED!\n" + msg);
	}

	private static final long serialVersionUID = 1L;
}
