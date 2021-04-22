#include "ContextCollectorController.h"
#include "Arduino.h"
#include "Code.cpp"

ContextCollectorController::ContextCollectorController() {
	captureInterval = -1;
	duration = -1;
	lastReadingStartTime = -2147483647;
	counter = 0;
}

void ContextCollectorController::setup() {
	messenger.start();
	getSetupInfo();
	collectingStartTime = millis();
}

void ContextCollectorController::getSetupInfo() {
	char code;
	char* content = NULL;

	do {
		//messenger.sendMsg(CONNECTION_CODE, "CON");
		Serial.println("0:CON");
		messenger.receiveMsg(&code, &content);
		delay(500);
	} while(code!=SETUP_CODE);

	if (saveParameters(content)) {
		messenger.sendMsg(SETUP_CODE, "OK");
		do {
			messenger.receiveMsg(&code, &content);
		} while(code!=CONNECTION_CODE);
		if (strcmp(content, "SUC")!=0) {
			//LANÇA EXCEÇÃO - PROBLEMA NA CONEXAO
		}
	}
	else {
		//LANÇA EXCEÇÃO - PROBLEMA AO LER PARAMETROS
	}
	delete[] content;
}

bool ContextCollectorController::saveParameters(char* content) {
	char* analogSensorList;
	char* digitalSensorList;
	char* sensorAux;
	int sensorCount;

	analogSensorList = strtok(content, ";");
	digitalSensorList = strtok(NULL, ";");
	duration = stol(strtok(NULL, ";"));
	captureInterval = atoi(strtok(NULL, ";"));

	if (captureInterval < 0 || duration < 0 || (strcmp(analogSensorList, "null") == 0 && strcmp(digitalSensorList, "null") == 0)) {
		return false;
	}
	analogSensorsSize = 0;
	digitalSensorsSize = 0;

	if (strcmp(analogSensorList, "null")!=0) {
        sensorAux = strtok(analogSensorList, ",");
        while (sensorAux != NULL) {
            analogSensorsSize++;
            sensorAux = strtok(NULL, ",");
        }
        analogSensors = new int[analogSensorsSize];
        sensorAux = strtok(analogSensorList, ",");
        sensorCount = 0;
        while (sensorAux != NULL) {
            analogSensors[sensorCount] = A0 + atoi(sensorAux);
            sensorCount++;
            sensorAux = strtok(NULL, ",");
        }
	}

    if (strcmp(digitalSensorList, "null")!=0) {
        sensorAux = strtok(digitalSensorList, ",");
        while (sensorAux != NULL) {
            digitalSensorsSize++;
            sensorAux = strtok(NULL, ",");
        }
        digitalSensors = new int[digitalSensorsSize];
        sensorAux = strtok(digitalSensorList, ",");
        sensorCount = 0;
        while (sensorAux != NULL) {
            digitalSensors[sensorCount] = atoi(sensorAux);
            sensorCount++;
            sensorAux = strtok(NULL, ",");
        }
    }
    if (captureInterval == 0) {
        Serial.println("INTERVALO ZERO");
    }

	return true;
}

void ContextCollectorController::loop() {
	if (millis() - collectingStartTime >= duration) {
        Serial.print(">");
        Serial.println(counter);
		messenger.sendMsg(EXCEPTION_CODE, "TIMEOUT");
	}
	else if (millis() - lastReadingStartTime >= captureInterval) {
		lastReadingStartTime = millis();

		String msg = "";
		bool first = true;
		for (int i=0; i<analogSensorsSize; i++) {
			if (!first)
				msg += ",";
			else
				first = false;
			msg += analogRead(analogSensors[i]);
		}
		for (int i=0; i<digitalSensorsSize; i++) {
			if (!first)
				msg += ",";
			else
				first = false;
			msg += digitalRead(digitalSensors[i]);
		}
		counter++;
		//Serial.println("4:da8u9sidfoakdjfiuo4:da8u9sidfoakdjfiuo");
		messenger.sendMsg(READING_CODE, msg);
	}
}
long ContextCollectorController::stol(char* n) {
	long number=0;
	for (int i=0; i<strlen(n); i++) {
		if (n[i]>='0' && n[i]<='9') {
			number = number*10 + (n[i] - '0');
		}
	}
	return number;
}
