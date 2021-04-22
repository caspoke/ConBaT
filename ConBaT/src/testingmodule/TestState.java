package testingmodule;

public class TestState {
	private static long DEFAULT_TIMEOUT_LIMIT = 15000;

	private boolean result; // null if not ended. MUST be set as true/false by the TestController
	private String errorMsg; // null if success. CAN get Exception msg captured by TestController
	private long startTime; // null it test didn't start. MUST be inicialized by TestController before
							// running the ArduinoController
	private long duration; // null if test didn't end. MUST be set by TestController when test is over.
	private long timeoutLimit; // has a default value, but is set by the Test. Test has its own default value,
								// changes it when a context is added and can also be set directly to a custom
								// value

	public TestState() {
		timeoutLimit = DEFAULT_TIMEOUT_LIMIT;
	}

	public String toString() {
		StringBuilder s = new StringBuilder("Test info: \n");
		s.append("\tResult = " + (result ? "SUCCESS" : "FAIL") + "\n");
		s.append("\tDuration: " + duration + "\n");
		s.append("\tTimeout limit: " + timeoutLimit + "\n");
		s.append(errorMsg != null ? "\tError msg: " + errorMsg + "\n" : "");

		return s.toString();
	}

	public long getTimeoutLimit() {
		return this.timeoutLimit;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public boolean getResult() {
		return this.result;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String msg) {
		this.errorMsg = msg;
	}

	public void success() {
		result = true;
	}

	public void fail() {
		result = false;
	}

	public long getStartTime() {
		return this.startTime;
	}

	public void setTimeoutLimit(long timeoutLimit) {
		this.timeoutLimit = timeoutLimit;
	}

	public void start() {
		this.startTime = System.currentTimeMillis();
	}

	public void stop() {
		duration = System.currentTimeMillis() - startTime;
	}

	public long getDuration() {
		return this.duration;
	}
}
