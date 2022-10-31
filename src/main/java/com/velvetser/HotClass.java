package com.velvetser;

public interface HotClass<T> {

    T instantiate();
    <F> F get(int fieldIndex, T object, Class<F> clazz);

    byte getByte(int fieldIndex, T object);

    short getShort(int fieldIndex, T object);

    int getInt(int fieldIndex, T object);

    long getLong(int fieldIndex, T object);

    boolean getBoolean(int fieldIndex, T object);

    char getChar(int fieldIndex, T object);

    <F> void set(int fieldIndex, T object, F fieldValue);

    void setByte(int fieldIndex, T object, byte value);

    void setShort(int fieldIndex, T object, short value);

    void setInt(int fieldIndex, T object, int value);

    void setLong(int fieldIndex, T object, long value);

    void setBoolean(int fieldIndex, T object, boolean value);

    void setChar(int fieldIndex, T object, char value);

}
