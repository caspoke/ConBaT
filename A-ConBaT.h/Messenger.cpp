#include "Messenger.h"

Messenger::Messenger() {
	newData = false;
	msgMaxSize = 50;
	msgIndex = 0;
	delimiter = '\n';
	receiveMsgTimeout = 1000;
	msg = new char[msgMaxSize];
}

void Messenger::start() {
    delay(100);
	Serial.begin(250000);
	while(!Serial);
}

//Receives a pointer to a char (code of the msg) and to an array of char (content of the msg)
//Keeps reading buffer until
//(1) A new msg was found
//	(1.1) It is a valid msg 	-> return true
//	(1.2) The msg is invalid 	-> return false
//(2) Timeout is reached		-> return false
bool Messenger::receiveMsg(char* code, char** content) {
	long start = millis();
	while (!newData && millis()-start < receiveMsgTimeout) {
		readBuffer();
	}
	if (newData) {
		int size = msgIndex;
		newData = false;
		msgIndex = 0;
		if (size > 3 && msg[1] == ':') {
			*code = msg[0];
			delete[] *content;
			size -= 2;
			*content = new char[size];
			strncpy(*content, msg+2, size);
			(*content)[size-1] = '\0';
			return true;
		} else {
			return false; //INVALID MSG
		}
	} else {
		return false; //TIMEOUT
	}
}

void Messenger::readBuffer() {
	char c;
	while (Serial.available() > 0 && newData == false) {
		if (msgIndex == msgMaxSize) {
			setMaxSize(msgMaxSize*2);
		}
		c = Serial.read();
		if (c != delimiter) {
			msg[msgIndex++] = c;
		}
		else {
			msg[msgIndex++] = '\0';
			newData = true;
		}
	}
}

void Messenger::sendMsg(char code, const char* content) {
    if (content!=nullptr) {
        int size = 0;
        char c = '.';
        while (c != '\0') {
            c = content[size];
            size++;
        }
        size += 2;
        char* response = (char*) calloc(size, sizeof(char));
        response[0] = code;
        response[1] = ':';
        for (int i=2; i<size; i++) {
            response[i] = content[i-2];
        }
        Serial.println(response);
        delete[] content;
        delete[] response;
    }
}

void Messenger::sendMsg(char code, String content) {
	Serial.print(code);
	Serial.print(":");
	Serial.println(content);
}

void Messenger::setMaxSize(int newSize) {
	char* newMsg = new char[newSize];
	for (int i=0; i<msgIndex; i++) {
		newMsg[i] = msg[i];
	}
	delete[] msg;
	msg = newMsg;
	msgMaxSize = newSize;
}

void Messenger::setDelimiter(char delimiter) {
	this-> delimiter = delimiter;
}

void Messenger::setTimeout(long timeout) {
	this->receiveMsgTimeout = timeout;
}
