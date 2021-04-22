

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import core.Context;
import core.ContextUtil;
import testingmodule.ContextTest;
import testingmodule.conditions.DigitalWriteCheckCondition;

public class C003Test extends ContextTest{

	public static Context c;
	
	@BeforeClass
	public static void beforeClass() {
		c = ContextUtil.readFromFile("context/C003.json");
	}
	
	@Before
	public void before() {
		setPort("COM4");
		setContext(c);
		mapSensor(15, 0);
	}
	
	@Test
	public void T001() {
		addCondition(new DigitalWriteCheckCondition(13, "LOW"));
		runTest();
	}
	
}