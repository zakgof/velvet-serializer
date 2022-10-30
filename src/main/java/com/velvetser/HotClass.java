package com.velvetser;

public interface HotClass<T> {

    T instantiate();
    <F> F get(int fieldIndex, T object, Class<F> clazz);

    byte getByte(int fieldIndex, T object);

    short getShort(int fieldIndex, T object);

    <F> void set(int fieldIndex, T object, F fieldValue);

    void setByte(int fieldIndex, T object, byte value);

    void setShort(int fieldIndex, T object, short value);
}
