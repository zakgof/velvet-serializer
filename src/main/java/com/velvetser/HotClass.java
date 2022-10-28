package com.velvetser;

public interface HotClass<T> {

    T instantiate();
    <F> F get(String fieldName, T object, Class<F> clazz);

    byte getByte(String fieldName, T object);

    short getShort(String fieldName, T object);

    <F> void set(String fieldName, T object, F fieldValue);

    void setByte(String fieldName, T object, byte value);

    void setShort(String fieldName, T object, short value);
}
