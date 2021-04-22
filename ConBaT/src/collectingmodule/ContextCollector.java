package collectingmodule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import collectingmodule.exceptions.NotEnoughParametersException;
import core.Context;
import core.ContextUtil;
import core.communication.ArduinoListener;
import core.expections.ContextEndedException;

public class ContextCollector {
	private final int DEFAULT_DELAY = 0;
	private final int DEFAULT_DURATION = 10000;

	private ArduinoListener listener;
	private ArrayList<String> readings;

	//Parameters related to context collecting
	private ArrayList<Integer> analogSensorPins;		
	private ArrayList<Integer> digitalSensorPins;										
	private long duration;					
	private int captureInterval;				
	
	//Parameters related to context file creation
	private ArrayList<String> analogSensorDescriptions;	
	private ArrayList<String> digitalSensorDescriptions;
	private String path;								
	private String name;								
	private String description;							
	private Map<String, String> additionalInfo;			


	public ContextCollector(String path) {
		resetParameters();		
		this.path = path;
	}
	
	public void setPort(String portName) {
		listener.setPort(portName);
	}

	public void resetParameters() {
		listener = ArduinoListener.getInstance();		
		readings = new ArrayList<String>();	
		
		//Parameters related to context collecting
		analogSensorPins = new ArrayList<Integer>();
		digitalSensorPins = new ArrayList<Integer>();		
		captureInterval = DEFAULT_DELAY;
		duration = DEFAULT_DURATION;
		
		//Parameters related to context file creation
		analogSensorDescriptions = new ArrayList<String>();
		digitalSensorDescriptions = new ArrayList<String>();
		path = null;
		name = null;
		description = null;
		additionalInfo = new HashMap<String,String>();	
	}
	
	public void collectAndSave(long duration) {
		this.duration = duration;
		collectAndSave();
	}

	public void collectAndSave() {
		if (analogSensorPins.size() == 0 && digitalSensorPins.size() == 0) {
			throw new NotEnoughParametersException("You need to add least one sensor");
		} else if (name == null) {
			throw new NotEnoughParametersException("Your context needs a name");
		} else if (path == null) {
			throw new NotEnoughParametersException("Context path not specified");
		}

		listener.setHandler(new ContextCollectorHandler(readings, analogSensorPins,
										digitalSensorPins, duration, captureInterval));
		try {
			listener.start();
		} catch (ContextEndedException e) {
			buildAndSaveContext();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	// DATA COLLECTING INFO
	public void addAnalogSensor(Integer pin, String description) {
		this.analogSensorPins.add(pin);
		this.analogSensorDescriptions.add(description);
	}
	public void addDigitalSensor(Integer pin, String description) {
		this.digitalSensorPins.add(pin);
		this.digitalSensorDescriptions.add(description);
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public void setCaptureInterval(int captureInterval) {
		this.captureInterval = captureInterval;
	}

	// CONTEXT FILE INFO
	public void setContextDescription(String description) {
		this.description = description;
		//this.context.setDescription(description);
	}
	public void setContextName(String name) {
		this.name = name;
		//this.context.setName(name);
	}
	public void addAdditionalInfo(String name, String value) {
		this.additionalInfo.put(name, value);
		//this.context.getAdditionalInfo().put(name, value);
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
	private void buildAndSaveContext() {
		Context context = new Context();
		
		int sensorAmount = digitalSensorDescriptions.size() + analogSensorDescriptions.size();
		String[] readingSplit;
		ArrayList< ArrayList<String>> sensorReadings = new ArrayList<ArrayList<String>>();
		ArrayList<String> dataInfo = new ArrayList<String>();
		ArrayList<String> line = new ArrayList<String>();
		for (String readingLine : readings) {
			readingSplit = readingLine.split(",");
			line = new ArrayList<String>();
			for (String reading : readingSplit) {
				line.add(reading);
			}
			sensorReadings.add(line);
		}
		for (String d : analogSensorDescriptions) {
			dataInfo.add(d);
		}
		for (String d : digitalSensorDescriptions) {
			dataInfo.add(d);
		}
		
		context.setName(name);
		context.setDescription(description);
		context.setAdditionalInfo(additionalInfo);
		context.setSensorAmount(sensorAmount);
		context.setSensorReadings(sensorReadings);
		context.setDuration(duration);
		context.setCaptureInterval(duration / readings.size());
		context.setDataInfo(dataInfo);
		
		ContextUtil.saveToFile(context, path+System.getProperty("file.separator")+name+".json");
	}
}
