package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import testingmodule.exceptions.BadContextFileException;


public class ContextUtil {
	public static void main (String args[]) {
		ContextUtil.saveToFile(ContextUtil.readFromFile("context/C009.txt"), "context/C009.json");
	}
	public static Context readFromFile(String contextFilePath) throws BadContextFileException {
		if (contextFilePath.endsWith(".txt"))
			return TXTRead(contextFilePath);
		else if (contextFilePath.endsWith(".json"))
			return JSONRead(contextFilePath);
		else {
			System.out.println("File format not supported");
			return null;
		}
	}
	public static void saveToFile(Context context, String contextFilePath) {
		if (contextFilePath.endsWith(".txt"))
			TXTSave(context, contextFilePath);
		else if (contextFilePath.endsWith(".json"))
			JSONSave(context, contextFilePath);
		else {
			System.out.println("File format not supported");
		}
	}
	private static Context TXTRead(String contextFilePath) throws BadContextFileException {
		Context c = new Context();
		ArrayList<String> dataInfo = new ArrayList<String>();
		ArrayList<ArrayList<String>> sensorReadings = new ArrayList<ArrayList<String>>();
		Map<String, String> additionalInfo = new HashMap<String, String>();
		ArrayList<String> line = new ArrayList<String>();
		int sensorAmount = 0;

		int sectionId = -1;

		try {
			System.out.println("Trying to read " + contextFilePath);
			FileReader fileReader = new FileReader(contextFilePath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String linha, key, value;
			String title;
			String[] splitLine;
			String[] readingsAux;

			while ((linha = bufferedReader.readLine()) != null) {
				if (linha.length() > 0) {
					if (linha.charAt(0) == '#') {
						title = linha.substring(1);
						title = title.toUpperCase();
						if (title.contentEquals("CONTEXT INFO")) {
							sectionId = 0;
						} else if (title.contentEquals("DATA INFO")) {
							sectionId = 1;
						} else if (title.contentEquals("DATA")) {
							sectionId = 2;
						}
					} else {
						if (sectionId == 0) {
							try {
								splitLine = linha.split("=");
								key = splitLine[0];
								value = splitLine[1];
							} catch (ArrayIndexOutOfBoundsException e) {
								throw new BadContextFileException("Context info must be in the format [key]=[value]");
							}
							switch (key) {
							case "name":
								c.setName(value);
								break;
							case "description":
								c.setDescription(value);
								break;
							case "duration":
								c.setDuration(Long.valueOf(value));
								break;
							case "capture_interval":
								c.setCaptureInterval(Long.valueOf(value));
								break;
							default:
								additionalInfo.put(key, value);
							}
						} else if (sectionId == 1) {
							try {
								dataInfo.add(linha.split("=")[1]);
							} catch (ArrayIndexOutOfBoundsException e) {
								throw new BadContextFileException(
										"Sensor info must be in the format [sensor id]=[sensor info]");
							}
							sensorAmount++;

						} else if (sectionId == 2) {
							splitLine = linha.split(",");
							line = new ArrayList<String>();
							for (int i = 0; i < splitLine.length; i++) {
								line.add(splitLine[i]);
							}
							sensorReadings.add(line);
						}
					}
				}
			}
			c.setSensorAmount(sensorAmount);
			c.setAdditionalInfo(additionalInfo);
			c.setDataInfo(dataInfo);
			c.setSensorReadings(sensorReadings);

			bufferedReader.close();

			long currentDuration = c.getDuration();
			long currentReadingRate = c.getCaptureInterval();
			if (currentReadingRate > 0) {
				c.setDuration(sensorReadings.size() * c.getCaptureInterval());
			} else {
				if (currentDuration > 0) {
					c.setCaptureInterval(currentDuration / sensorReadings.size());
				} else
					throw new BadContextFileException("Not enough info to get capture rate");
			}
			System.out.println("Context \"" + contextFilePath + "\" was successfully loaded!");
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open context '" + contextFilePath + "'");
			c = null;
		} catch (IOException ex) {
			System.out.println("Error reading context '" + contextFilePath + "'");
			c = null;
		}

		return c;
	}

	private static void TXTSave(Context context, String contextFilePath) {
		BufferedWriter writer = null;
		try {
			File file = new File(contextFilePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			else {
				int count = 1;
				while (file.exists()) {
					file = new File(contextFilePath.replace(".txt", count+".txt"));
					count++;
				}
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			writer = new BufferedWriter(fw);
			writer.write(context.toString());
			System.out.println("Context file was successfully created!");

		} catch (IOException ioe) {
			System.out.println("Failed to save context file");
			ioe.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (Exception ex) {
				System.out.println("Error in closing the BufferedWriter" + ex);
			}
		}
	}

	private static void JSONSave(Context context, String contextFilePath) {
		JSONObject json = new JSONObject();
		Map<String,String> additionalInfo = context.getAdditionalInfo();
		ArrayList<String> dataInfo = context.getdataInfo();
		ArrayList<String[]> readings;
		
		json.put("name", context.getName());
		json.put("description", context.getDescription());
		json.put("capture_interval", context.getCaptureInterval());
		json.put("duration",  context.getDuration());
		for (String info : additionalInfo.keySet()) {
			json.put(info, additionalInfo.get(info));
		}
		json.put("data_info", dataInfo);
		json.put("data", context.getSensorReadings());
		
		//READINGS
		/*
		s.append("\n" + "#Readings\n");
		String readingsLine = "";
		for (String[] readings : sensorReadings) {
			readingsLine = "";
			for (int i = 0; i < sensorAmount; i++) {
				readingsLine += readings[i];
				if (i < sensorAmount - 1)
					readingsLine += ",";
			}
			s.append(readingsLine + "\n");
		}
		
		*/
		BufferedWriter writer = null;
		try {
			File file = new File(contextFilePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			else {
				int count = 1;
				while (file.exists()) {
					file = new File(contextFilePath.replace(".json", count+".json"));
					count++;
				}
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			writer = new BufferedWriter(fw);
			writer.write(json.toJSONString());
			System.out.println("Context file was successfully created!");

		} catch (IOException ioe) {
			System.out.println("Failed to save context file");
			ioe.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (Exception ex) {
				System.out.println("Error in closing the BufferedWriter" + ex);
			}
		}
	}
	
	private static Context JSONRead(String contextFilePath) throws BadContextFileException {
		Context c = new Context();
		JSONParser parser = new JSONParser();		
	
		ArrayList<String> dataInfo = new ArrayList<String>();
		ArrayList<ArrayList<String>> sensorReadings = new ArrayList<ArrayList<String>>();
		Map<String, String> additionalInfo = new HashMap<String, String>();
		int sensorAmount = 0;

		try {
			Object obj = parser.parse(new FileReader(contextFilePath));
			JSONObject jsonObject = (JSONObject) obj;
			for (Object o : jsonObject.entrySet()) {
				Map.Entry entry = (Map.Entry) o;
				switch ((String) entry.getKey()) {
					case("name"):
						c.setName((String) entry.getValue());
						break;
					case("description"):
						c.setDescription((String) entry.getValue());
						break;
					case("capture_interval"):
						c.setCaptureInterval((long) entry.getValue());
						break;
					case("duration"):
						c.setDuration((long) entry.getValue());
						break;
					case("data_info"):
						JSONArray dataInfoAux = (JSONArray) entry.getValue();
						Iterator<String> sensorItr = dataInfoAux.iterator();
						while (sensorItr.hasNext()) {
							dataInfo.add(sensorItr.next());
							sensorAmount++;
						}
						c.setSensorAmount(sensorAmount);
						c.setDataInfo(dataInfo);
						break;
					case("data"):
						JSONArray dataAux = (JSONArray) entry.getValue();
						Iterator<JSONArray> dataItr = dataAux.iterator();
						Iterator<String> readingItr;
						ArrayList<String> captureLine;
						int column=0;
						while (dataItr.hasNext()) {
							captureLine = new ArrayList();
							readingItr = dataItr.next().iterator();
							column=0;
							while (readingItr.hasNext()) {
								captureLine.add(readingItr.next());
								column++;
							}
							sensorReadings.add(captureLine);
						}
						c.setSensorReadings(sensorReadings);
						break;
					default:
						additionalInfo.put((String) entry.getKey(), (String) entry.getValue());
				}	
			}
			
			long currentDuration = c.getDuration();
			long currentReadingRate = c.getCaptureInterval();
			if (currentReadingRate > 0) {
				c.setDuration(sensorReadings.size() * c.getCaptureInterval());
			} else {
				if (currentDuration > 0) {
					c.setCaptureInterval(currentDuration / sensorReadings.size());
				} else
					throw new BadContextFileException("Not enough info to get capture rate");
			}
			System.out.println("Context \"" + contextFilePath + "\" was successfully loaded!");
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open context '" + contextFilePath + "'");
			c = null;
		} catch (IOException ex) {
			System.out.println("Error reading context '" + contextFilePath + "'");
			c = null;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return c;
	}
}
