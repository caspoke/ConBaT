package collectingmodule.exceptions;

public class NotEnoughParametersException extends RuntimeException {
	public NotEnoughParametersException(String msg) {
		super(msg);
	}

	private static final long serialVersionUID = 1L;
}
