package com.velvetser.impl;

import com.velvetser.ClassSerializer;
import com.velvetser.impl.ser.CollectionSerializer;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;

public class SerializerRegistry implements ClassSerializerProvider {

    private final Map<Class<?>, ClassSerializer<?>> serializers = new IdentityHashMap<>();

    public SerializerRegistry() {
        serializers.put(ArrayList.class, new CollectionSerializer<>());
        serializers.put(LinkedList.class, new CollectionSerializer<>());
    }

    @Override
    public <T> ClassSerializer<T> get(Class<T> clazz) {
        return (ClassSerializer<T>) serializers.get(clazz);
    }
}
