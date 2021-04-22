#ifndef VariableInfo_h
#define VariableInfo_h

#if defined(ARDUINO) && ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

class VariableInfo {
private:  
  static const char INT_TYPE = '0', STRING_TYPE = '1', LONG_TYPE = '2', BOOL_TYPE = '3';
  String name;  
  char typeCode;
  union variablePointer {
    int* intPtr;
    char** stringPtr;
    long* longPtr;
    bool* boolPtr;
  } valuePtr;


public: 
  VariableInfo();
  VariableInfo(const char* nome, int* pointer);
  VariableInfo(const char* nome, char** pointer);
  VariableInfo(const char* nome, long* pointer);
  VariableInfo(const char* nome, bool* pointer);
  String getValue();
  String getName();
};

#endif