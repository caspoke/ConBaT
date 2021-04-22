#include "List.h"

template <class T>
List<T>::List() {
	list = NULL;
	makeEmpty();
}

//SIZE SIGNIFICA O NUMERO DE ELEMENTOS NA LISTA E A POSIÇÃO DO PROXIMO A SER INSERIDO

template <class T>
void List<T>::add(T item) {
	if (isEmpty()) {
		makeEmpty();
	}
	if (isFull()) {
		doubleCapacity();
	}
	list[size] = item;
	size++;
}

template <class T>
void List<T>::doubleCapacity() {
	T* tempArray = new T[capacity*2];
	for (int i=0; i < capacity; i++) {
		tempArray[i] = list[i];
	}
	delete[] list;
	list = tempArray;
	capacity *= 2;
}

template <class T>
int List<T>::length() {
	return size;
}

template <class T>
bool List<T>::isFull() {
	return (size == capacity);
}

template <class T>
bool List<T>::isEmpty() {
	return size == 0;
}

template <class T>
T* List<T>::get(int index) {
	if (index < size) {
		return &list[index];
	} else
		return NULL;
}

template <class T>
void List<T>::makeEmpty() {
	size = 0;
	capacity = 1;
	delete[] list;
	list = new T[capacity];
}