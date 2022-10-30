package com.velvetser.impl;

import java.util.IdentityHashMap;
import java.util.Map;

public class WriteContext {

    private final Map<Class<?>, Integer> nameToid = new IdentityHashMap<>();

    public Integer getKnownClassId(Class<?> clazz) {
        return nameToid.get(clazz);
    }

    public int putKnownClass(Class<?> clazz) {
        int id = nameToid.size();
        nameToid.put(clazz, id);
        return id;
    }
}
