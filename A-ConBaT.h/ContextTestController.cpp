#include "ContextTestController.h"
#include "Code.cpp"

ContextTestController::ContextTestController() {
	underTesting = true;

	pinToWriteId = new int[100];
	for (int i=0; i<100; i++) {
		pinToWriteId[i] = -1;
	}
}
//--------------------------------------------------------


//SETUP METHODS
void ContextTestController::addVariable(const char* name, int* pointer) {
	variables.add(VariableInfo(name, pointer));
}
void ContextTestController::addVariable(const char* name, bool* pointer) {
	variables.add(VariableInfo(name, pointer));
}
void ContextTestController::addVariable(const char* name, char** pointer) {
	variables.add(VariableInfo(name, pointer));
}
void ContextTestController::addVariable(const char* name, long* pointer) {
	variables.add(VariableInfo(name, pointer));
}

void ContextTestController::start() {
	if (underTesting) {
		messenger.start();
		getSetupInfo();
	} else {
		variables.makeEmpty();
	}
}

void ContextTestController::getSetupInfo() {
	char code;
	char* content = NULL;
	char* variableList;
	char* methodList;
    Serial.println("OBTENDO INFORMACAO DO SETUP");
	do {
		messenger.sendMsg(CONNECTION_CODE, "CON");
		messenger.receiveMsg(&code, &content);
		delay(500);
	} while(code!=SETUP_CODE);
	variableList = strtok(content, ";");
	methodList = strtok(NULL, ";");
	if (loadRequestedVariables(variableList) && loadRequestedMethods(methodList)) {
		messenger.sendMsg(SETUP_CODE, "OK");
		do {
			messenger.receiveMsg(&code, &content);
		} while(code!=CONNECTION_CODE);
	}
	delete[] content;
}

bool ContextTestController::loadRequestedVariables(char* variableList) {
	char* var = strtok(variableList, ",");
	List<VariableInfo> aux;
	if (var != NULL && strcmp(var, "NULL") != 0) {
		bool found = false;
		while (var != NULL) {
			found = false;
			for (int i=0; i<variables.length(); i++) {
				if (variables.get(i)->getName().compareTo(var) == 0) {
					found = true;
					//requestedVariables.add(*allVariables.get(i));
					aux.add(*variables.get(i));
				}
			}
	    	if (!found) {
	    		messenger.sendMsg(EXCEPTION_CODE, "VARIABLENOTFOUND");
	    		return false;
			}
	    	var = strtok(NULL, ",");
	    }
	    variables.makeEmpty();
	    variables = aux;
		Serial.println("VARIAVEIS SOLICITADAS COM SUCESSO");
	}
	else {
        variables.makeEmpty();
		Serial.println("NENHUMA VARIAVEL SOLICITADA");
	}
	return true;
}

bool ContextTestController::loadRequestedMethods(char* methodList) {
    if (methodList != nullptr && strcmp(methodList, "NULL") != 0) {
        char* name = strtok(methodList, ",");
        Serial.println(name);
        if (name != NULL && name != nullptr) {
            bool found;
            char* pin;
            int count = 0;
            while (name != NULL) {
                found = false;
                if (strlen(name)>3 && name[2]=='=' && name[1]=='W') {
                    if (name[0]=='D') {
                        pin = strtok(name, "=");
                        pin = strtok(NULL, "=");
                        pinToWriteId[stoi(pin)] = count;
                        count++;
                        found = true;
                    } else if (name[0]=='A') {
                        pin = strtok(name, "=");
                        pin = strtok(NULL, "=");
                        pinToWriteId[A0+stoi(pin)] = count;
                        count++;
                        found = true;
                    }
                }
                else {
                    //IMPLEMENTAR AQUI O TRATAMENTO DE OUTROS MÉTODOS,
                    //QUE NAO SEJAM DIGITAL/ANALOG WRITE.
                }

                if (!found) {
                    messenger.sendMsg(EXCEPTION_CODE, "METHODNOTFOUND");
                    return false;
                }
                name = strtok(NULL, ",");
            }
            Serial.println("MÉTODOS SOLICITADOS COM SUCESSO");
        } else {
            Serial.println("NENHUM METODO SOLICITADO");
        }
    }
	return true;
}

void ContextTestController::setUnderTesting(bool underTesting) {
  	this->underTesting = underTesting;
}







//LOOP METHODS
void ContextTestController::contextAnalogWrite(int pin, int value) {
	if (underTesting && pinToWriteId[pin]!=-1) {
		String content = String(pinToWriteId[pin])+";"+value;
		messenger.sendMsg(TRIGGERED_UPDATE_CODE, content);
	}
	else
		analogWrite(pin, value);
}

void ContextTestController::contextDigitalWrite(int pin, int value) {
	if (underTesting && pinToWriteId[pin]!=-1) {
		String content = String(pinToWriteId[pin])+";"+value;
		messenger.sendMsg(TRIGGERED_UPDATE_CODE, content);
	}
	else
		digitalWrite(pin, value);
}

int ContextTestController::contextAnalogRead(int pin) {
	if (underTesting) {
		return requestReading(pin);
	}
	else return analogRead(pin);
}

int ContextTestController::contextDigitalRead(int pin) {
	if (underTesting) {
		return requestReading(pin);
	}
	else return digitalRead(pin);
}

int ContextTestController::requestReading(int pin) {
	char code = 'Z';
	char* content = NULL;
	messenger.sendMsg(READING_REQUEST_CODE, itoc(pin));
	while(!messenger.receiveMsg(&code, &content)) {
        if (code == 'R') {
            delete[] content;
            return requestReading(pin);
        }
	}
	int result = stoi(content);
	delete[] content;
	return result;
}

char* ContextTestController::itoc (int n) {
    int size = 0;
    if (n<10)
        size = 1;
    else if (n<100)
        size = 2;
    else if (n<1000)
        size = 3;
    else if (n<10000)
        size = 4;
    else if (n<100000)
        size = 5;
    //fazer ate 10 casas
    size++;
    char* result = (char*) calloc(size, sizeof(char));
    int digito;
    for (int i=size-2; i>=0; i--) {
        digito = n-((n/10)*10);
        n = n/10;
        result[i] = digito + '0';
    }
    result[size-1] = '\0';
    return result;
}

void ContextTestController::sendLoopUpdate() {
	if (!variables.isEmpty()) {
		String currentValues = "";

		bool first = true;
		for (int i=0; i<variables.length(); i++) {
			if (!first)
				currentValues += ",";
			else
				first = false;
			currentValues += variables.get(i)->getValue();
		}
		messenger.sendMsg(LOOP_UPDATE_CODE, currentValues);
	}
}

/*
void ContextTestController::sendVariables() {
	if (requestedVariablesSize > 0) {
		int index;
		bool first = false;
		int type;
	    int intIndex = 0;
	    int stringIndex = 0;
	    int longIndex = 0;
	    int boolIndex = 0;

		Serial.print(LOOP_UPDATE_CODE);
		Serial.print(":");

		for (int i=0; i<requestedVariablesSize; i++) {
			type = requestedVariablesType[i];
			if (!first)
				first = true;
			else
				Serial.print(",");
			if (type == INT_TYPE){
				index = requestedIntVariablesIndex[intIndex];
				Serial.print(*intValues[index]);
				intIndex++;
			} else if (type == STRING_TYPE){
				index = requestedStringVariablesIndex[stringIndex];
				Serial.print(*stringValues[index]);
				stringIndex++;
			} else 	if (type == LONG_TYPE){
				index = requestedLongVariablesIndex[longIndex];
				Serial.print(*longValues[index]);
				longIndex++;
			} else if (type == BOOL_TYPE){
				index = requestedBoolVariablesIndex[boolIndex];
				Serial.print(*boolValues[index]);
				boolIndex++;
			}
		}
		Serial.println("");
	}
}*/



//UTIL
int ContextTestController::stoi(char* n) {
	int number=0;
	for (int i=0; i<strlen(n); i++) {
		if (n[i]>='0' && n[i]<='9') {
			number = number*10 + (n[i] - '0');
		}
	}
	return number;
}



//OLD?
/*
void ContextTestController::sendRequestConfirmation() {
	int index;
	bool first = false;
	int type;
    int intIndex = 0;
    int stringIndex = 0;
    int longIndex = 0;
    int boolIndex = 0;

	Serial.print(SETUP_CODE);
	Serial.print(":");
	Serial.print(requestedIntSize+requestedStringSize+requestedLongSize+requestedBoolSize);
	Serial.print(";");

	for (int i=0; i<requestedVariablesSize; i++) {
		type = requestedVariablesType[i];
		if (!first)
			first = true;
		else
			Serial.print(",");
		if (type == INT_TYPE){
			index = requestedIntVariablesIndex[intIndex];
			Serial.print(intNames[index]);
			Serial.print("=");
			Serial.print(*intValues[index]);
			intIndex++;
		} else if (type == STRING_TYPE){
			index = requestedStringVariablesIndex[stringIndex];
			Serial.print(stringNames[index]);
			Serial.print("=");
			Serial.print(*stringValues[index]);
			stringIndex++;
		} else 	if (type == LONG_TYPE){
			index = requestedLongVariablesIndex[longIndex];
			Serial.print(longNames[index]);
			Serial.print("=");
			Serial.print(*longValues[index]);
			longIndex++;
		} else if (type == BOOL_TYPE){
			index = requestedBoolVariablesIndex[boolIndex];
			Serial.print(boolNames[index]);
			Serial.print("=");
			Serial.print(*boolValues[index]);
			boolIndex++;
		}
	}
	Serial.println("");
}
*/
//old
/*
bool ContextTestController::receiveMsg(char* code, char** content) {
	char delimiter = '\n';
	char c;
	newData = false;
	int size = 0;

	while (Serial.available() > 0 && newData == false) {
		c = Serial.read();
		//Serial.print(msgIndex);
		//Serial.println(c);
		if (c != delimiter) {
			msg[msgIndex++] = c;
		}
		else {
			msg[msgIndex] = '\0';
			newData = true;
			size = msgIndex + 1;
			msgIndex = 0;

		}
	}
	//Serial.println("SAIU");
	if (newData) {
		if (size > 3) {
			Serial.print(ECHO_CODE);
			Serial.print(":");
			Serial.println(msg);
			newData = false;
			if (msg[1] != ':') {
				return false;
			}
			else {
				//Serial.println("Msg valida");
				*code = msg[0];
				size = size-2;
				*content = new char[size];
				strncpy(*content, msg+2, size);
				(*content)[size-1] = '\0';
				return true;
			}
		}
	}
	else
		return false;
}

void ContextTestController::extractMsgInfo(char* msg, char* code, char** content) {
	*code = msg[0];
	int size=strlen(msg)-1;
	*content = new char[size];
	strncpy(*content, msg+2, size);
	*content[size-1] = '\0';
}

void ContextTestController::print() {
	for (int i=0; i<intSize; i++) {
		Serial.print(intNames[i]);
		Serial.print(": ");
		Serial.println(*intValues[i]);
	}
	for (int i=0; i<stringSize; i++) {
		Serial.print(stringNames[i]);
		Serial.print(": ");
		Serial.println(*stringValues[i]);
	}
	for (int i=0; i<longSize; i++) {
		Serial.print(longNames[i]);
		Serial.print(": ");
		Serial.println(*longValues[i]);
	}
	for (int i=0; i<boolSize; i++) {
		Serial.print(boolNames[i]);
		Serial.print(": ");
		Serial.println(*boolValues[i]);
	}

}*/
