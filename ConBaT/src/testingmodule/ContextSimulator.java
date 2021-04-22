package testingmodule;

import core.Context;
import core.expections.ContextEndedException;

public class ContextSimulator {

	Context context;
	int pinMap[];
	
	long lastLapsedTime = 0;
	long counter = 0;
	
	public ContextSimulator(Context context, int pinMap[]) {
		this.context = context;
		this.pinMap = pinMap;
	}

	public String getReading(long lapsedTime, int sensorIndex) throws ContextEndedException {
		//System.out.println(++counter);
		//System.out.println("delay = "+(lapsedTime-lastLapsedTime));
		lastLapsedTime = lapsedTime;
		String result;
		try {
			result = context.getReadingsByLapsedTime(lapsedTime).get(pinMap[sensorIndex]);
		} catch (ArrayIndexOutOfBoundsException e) {
			result = "";
		}
		return result;
	}
}
