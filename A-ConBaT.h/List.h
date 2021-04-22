#ifndef List_h
#define List_h

#if defined(ARDUINO) && ARDUINO >= 100
  #include "Arduino.h"
#else
  #include "WProgram.h"
#endif

#include <VariableInfo.h>

template<class T> class List {
private:
	T* list;
	int size;
	int capacity;

	void doubleCapacity();
public:
	List();
	void add(T item);
	bool isFull();
	bool isEmpty();
	int length();
	void makeEmpty();
	T* get(int index);
};
template class List<VariableInfo>;

#endif