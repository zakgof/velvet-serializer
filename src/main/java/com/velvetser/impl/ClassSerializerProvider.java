package com.velvetser.impl;

import com.velvetser.ClassSerializer;

public interface ClassSerializerProvider {
    <T> ClassSerializer<T> get(Class<T> clazz);
}
