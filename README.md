# ConBaT
## Collecting Context Data
To generate your own context files using Arduino, you first need to upload the collecting script to your Arduino unit:
```
#include <ContextCollectorController.h>

ContextCollectorController c;

void setup() {
  c.setup();
}

void loop() {
  c.loop();
}
```
This collecting script will be always the same - the configuration is done in Java, as in the following example:
```
public static void main (String args[]) {
  ContextCollector collector = new ContextController("path");
  collector.setPort("COM4");
  
  collector.setDuration(10000);
  collector.setCaptureIntervaL(5);
  collector.addAnalogSensor(0. "Piezo element, wired with 1Mohm resistor");
  
  collector.setName("my_context_name");
  collector.setDescription("my_context_description");  
}
```
The only class needed is the ContextCollector.
Using this class, it is possible to set the collecting parameters, like the port where the Arduino is connected to the computer, total collecting duration (in ms), the desired capture interval (in ms), the sensors used, context name and description.
Its constructor receives as parameter the path where the context file will saved to.
* It is important to note that the desired capture interval will not necessarily be the final capture interval. For instance, if the desired capture interval is set to 5ms, but Arduino needs 10ms to read all the sensors involved, the real capture interval, described in the generated context file will be 10ms.
* If not set, the capture interval will be 0ms - that is, the fastest the Arduino can read the sensors.
* The sensors are added through the method addSensor(int, String). The frist parameter represents the pin where the sensor is conected to the Arduino unit. The second parameter doesn't impact the collecting activity, but is important to make the generated context file easier to understand.
* The context description is not necessary, but it is important to describe what that collected data actually mean in a simple way.
After setting the collecting parameters, the method "collectAndSave()" is called. This method will start the Arduino script, send the collecting parameters and start collecting the data. The resulting context file will be saved where specified (in the example above, "path/my_context_name.json").

## Creating Context Based Tests
To test an Arduino system using ConBaT, it is necessary to adapt the system under tests (SUT) so it can communicate with the framework. The required adaptations are:
* Include the ContextTestController.h and instanciate it as a global variable.
* Call ContextTestController.start() at the end of the main setup method.
* Call ContextTestController.sendLoopUpdate() at the end of the main loop method.
* Identify any variable of interest for tests with ContexttestControler.addVariable("variable name", &pointerToTheVariable), in the main setup method (before start() is called).
* Replace Arduino's analog/digital read/write methods with ContextTestController's contextAnalogRead, contextDigitalRead, contextAnalogWrite and contextDigitalWrite methods.
The method ContextTestController.isUnderTest(bool) can alternate between production and testing versions of the SUT. If it is set as false, instead of reading data from context files, the system will read the real sensors and the test related methods will be ignored.

Context test cases are created though the class ContextTest and defined by 3 things:
* The context of execution (defined trough the setContext(Context) method)
* One or more success/failure conditions (defined through the addCondition(Condition) method)
* A map linking the context files to the SUT sensors (defined through the mapSensor(int, int) method, where the first parameter represents the pin where the sensor is connected to the Arduino and the second parameter represents the colummn in the context file - starting from 0 - where the data will be extracted from, during the sensor simulation)

It is also necessary the specify the port where the Arduino is connected to the computer, through the method setPort(String).

### Conditions

Conditions are used by the framework to identify if a test resulted in failure or success. They keep being verified during the whole test execution, until they are met or the test ends for another reason.
It means that conditions are events that the tester expects to happen AT SOME POINT during the test execution for the specified context.

Currently, there are 3 implemented conditions:
* VariableCheckCondition - used to verify if a specific variable of the SUT assumed a determined value. The first parameter is the variable name, the second one is the expected value.
* DigitalWriteCheckCondition - used to verify if the SUT sent a digital signal to a specific pin. The first parameter is the pin, the second one is the signal (1, 0, "HIGH", "LOW").
* AnalogWriteCheckCondition - used to verify if the SUT sent an analog signal to a specific pin. The first parameter is the pin, the second one is the signal.

#### Condition Types

Conditions can be success of failure conditions. This can be defined in the Condition constructor as an optional third parameter, written as:
* ConditionType.SUCESS (default) - means that if the condition is met (and no other condition forces the test to fail), the test will pass.
* ConditionType.FAIL - means that if the condition is met, the test will fail.

#### Condition Checking Times

It is possible to define when the verifications will be performed. Just like the condition types, it can be defined as an optional last parameter in conditions' constructor.
* CheckingTime.ANYTIME (default) - the conditions are constantly verified during the test execution and can interrupt it if all conditions (or a single failure condition) are met.
* CheckingTime.ATTHEEND - the conditions are ignored until the test ends for another reason (like timeout or when the simulated context ends). During the test execution, all information realed to conditions (calls to write methods, variable values...) are stored to ber later used to validate the conditions.

### Using JUnit

Context tests can be created in conjuction with JUnit.

It can be a good practice to create one test class (extending ContextTest) per context file. This way, the file can be read only once, during the @BeforeClass method. 
Then, common information to all test cases of that class (Arduino port, context used and sensor map) can be set in the @Before method, just like in the example below:

```
public class C001Test extends ContextTest{
  public static Context c;
  
  @BeforeClass
  public static void beforeClass() {
    c = ContextUtil.readFromFile("context/C001.json");
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
  
  @Test
  public void T002() {
    addCondition(new VariableCheckCondition("activationCounter", 6, ConditionType.FAIL));
    runTest();
  }
}
```
