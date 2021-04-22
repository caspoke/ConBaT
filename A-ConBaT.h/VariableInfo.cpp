#include "VariableInfo.h"

VariableInfo::VariableInfo() {

}
VariableInfo::VariableInfo(const char* nome, int* pointer)  {
    this->name = String(nome);
    valuePtr.intPtr = pointer;
    typeCode = INT_TYPE;
 }
VariableInfo::VariableInfo(const char* nome, char** pointer) {
    this->name = String(nome);
    valuePtr.stringPtr = pointer;
    typeCode = STRING_TYPE;
 }
VariableInfo::VariableInfo(const char* nome, long* pointer) {
    this->name = String(nome);
    valuePtr.longPtr = pointer;
    typeCode = LONG_TYPE;
 }
VariableInfo::VariableInfo(const char* nome, bool* pointer) {
    this->name = String(nome);
    valuePtr.boolPtr = pointer;
    typeCode = BOOL_TYPE;
 }

String VariableInfo::getValue() {
  String s;
  switch (typeCode) {
    case (INT_TYPE):
      s = String(*valuePtr.intPtr); break;
    case STRING_TYPE:
      s = String(*valuePtr.stringPtr); break;
    case LONG_TYPE:
      s = String(*valuePtr.longPtr); break;
    case BOOL_TYPE:
      s = String(*valuePtr.boolPtr); break;
  }
  return s;
}

String VariableInfo::getName() {
  return name;
}