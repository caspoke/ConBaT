package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import core.expections.ContextEndedException;

public class Context {

	private String name, description;
	private long captureInterval;
	private long duration;
	Map<String, String> additionalInfo;
	private ArrayList<String> dataInfo;
	private ArrayList<ArrayList<String>> sensorReadings;
	private int sensorAmount;

	public Context() {
		additionalInfo = new HashMap<String, String>();
		dataInfo = new ArrayList<String>();
		sensorReadings = new ArrayList<ArrayList<String>>();
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		
		//CONTEXT INFO
		s.append("#Context info\n");
		if (name!=null)
			s.append("name=" + name + "\n");
		if (description!=null)
			s.append("description=" + description + "\n");
		for (String info : additionalInfo.keySet()) {
			s.append(info + "=" + additionalInfo.get(info) + "\n");
		}
		s.append("capture_interval=" + captureInterval + "\n");
		s.append("duration=" + duration + "\n");
		
		//SENSOR INFO		
		s.append("\n" + "#Sensor info\n");
		for (int i = 0; i < dataInfo.size(); i++) {
			s.append(i + "=" + dataInfo.get(i) + "\n");
		}
		
		//READINGS
		s.append("\n" + "#Readings\n");
		String readingsLine = "";
		for (ArrayList<String> readings : sensorReadings) {
			readingsLine = "";
			for (int i = 0; i < sensorAmount; i++) {
				readingsLine += readings.get(i);
				if (i < sensorAmount - 1)
					readingsLine += ",";
			}
			s.append(readingsLine + "\n");
		}
		return s.toString();
	}

	public ArrayList<String> getReadingsByIndex(int index) {
		return this.sensorReadings.get(index);
	}

	public ArrayList<String> getReadingsByLapsedTime(long lapsedTime) throws ContextEndedException {
		// System.out.println("indice = "+lapsedTime/captureInterval);
		if (lapsedTime < duration) {
			int index = (int) (lapsedTime / captureInterval);
			return this.sensorReadings.get(index);
		} else
			throw new ContextEndedException();
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getDuration() {
		if (duration == 0)
			return sensorReadings.size() * captureInterval;
		else
			return duration;
	}

	public long getCaptureInterval() {
		return this.captureInterval;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public ArrayList<String> getdataInfo() {
		return dataInfo;
	}

	public void setDataInfo(ArrayList<String> dataInfo) {
		this.dataInfo = dataInfo;
	}

	public ArrayList<ArrayList<String>> getSensorReadings() {
		return sensorReadings;
	}

	public void setSensorReadings(ArrayList<ArrayList<String>> sensorReadings) {
		this.sensorReadings = sensorReadings;
	}

	public int getSensorAmount() {
		return sensorAmount;
	}

	public void setSensorAmount(int sensorAmount) {
		this.sensorAmount = sensorAmount;
	}

	public void setCaptureInterval(long captureInterval) {
		this.captureInterval = captureInterval;
	}
}
