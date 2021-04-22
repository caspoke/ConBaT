#ifndef ContextCollectorController_h
#define ContextCollectorController_h

#if defined(ARDUINO) && ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

#include <Wire.h>
#include <Messenger.h>

class ContextCollectorController {
  private:
  	Messenger messenger;

  	long counter;


    long duration;
    long captureInterval;

  	int* analogSensors;
  	int* digitalSensors;
  	int analogSensorsSize;
  	int digitalSensorsSize;

    long collectingStartTime;
    long lastReadingStartTime;
    bool firstLoop = true;

    void getSetupInfo();
    bool saveParameters(char* content);

    long stol(char* n);

  public:
  	ContextCollectorController();
    void setup();
    void loop();
};

#endif
