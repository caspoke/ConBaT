import collectingmodule.ContextCollector;

public class CollectingTest {
	public static void main (String args[]) {
		ContextCollector c = new ContextCollector("context");
		c.setPort("COM4");
		
		c.setDuration(10000);
		c.addAnalogSensor(1, "Piezo element, wired with 20kohm resistor");
		c.setCaptureInterval(5);
		//context file parameters
		c.setContextName("C010");
		c.setContextDescription("jaquepatombá beat");
		
		c.collectAndSave();
	}
}
