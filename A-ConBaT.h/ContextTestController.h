#ifndef ContextTestController_h
#define ContextTestController_h

#if defined(ARDUINO) && ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

#include <Wire.h>
#include <VariableInfo.h>
#include <List.h>
#include <Messenger.h>

class ContextTestController {
  private:
    Messenger messenger;
    bool underTesting;

	  int* pinToWriteId;
    List<VariableInfo> variables;

    void getSetupInfo();
    bool loadRequestedVariables(char* variableList);
    bool loadRequestedMethods(char* methodList);
    int requestReading(int pin);

  public:
  	ContextTestController();

    //SETUP METHODS
    void start();
    void addVariable(const char* name, int* pointer);
    void addVariable(const char* name, char** pointer);
    void addVariable(const char* name, long* pointer);
    void addVariable(const char* name, bool* pointer);
    void setUnderTesting(bool underTesting);

    //LOOP METHODS
    void contextAnalogWrite(int pin, int value);
    void contextDigitalWrite(int pin, int value);
    int contextAnalogRead(int pin);
    int contextDigitalRead(int pin);
    void sendLoopUpdate();

    //UTIL
    int stoi(char* n);
    char* itoc (int n);
};

#endif
