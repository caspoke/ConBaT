package testingmodule.exceptions;

public class BadContextFileException extends RuntimeException {
	public BadContextFileException(String msg) {
		super(msg);
	}

	private static final long serialVersionUID = 1L;
}
