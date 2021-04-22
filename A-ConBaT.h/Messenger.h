#ifndef Messenger_h
#define Messenger_h

#if defined(ARDUINO) && ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

#include <Wire.h>

class Messenger {
  private:
    char* msg;
    bool newData;
    int msgMaxSize;
    int msgIndex;
    char delimiter;
    long receiveMsgTimeout;

    void readBuffer();

  public:
    Messenger();
    void start();
    bool receiveMsg(char* code, char** content);
    void sendMsg(char code, const char* content);
    void sendMsg(char code, String content);
    void setMaxSize(int newSize);
    void setDelimiter(char delimiter);
    void setTimeout(long timeout);
};

#endif