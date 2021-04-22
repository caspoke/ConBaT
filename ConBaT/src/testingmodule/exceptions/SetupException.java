package testingmodule.exceptions;

public class SetupException extends RuntimeException {
	public SetupException(String msg) {
		super(">Test setup failed!\n>>" + msg);
	}

	private static final long serialVersionUID = 1L;

}
