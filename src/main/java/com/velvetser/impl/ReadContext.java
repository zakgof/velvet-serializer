package com.velvetser.impl;

import com.velvetser.VelvetSerializerException;

import java.util.HashMap;
import java.util.Map;

public class ReadContext {

    private final Map<Integer, String> idToName = new HashMap<>();
    private final Map<String, Class<?>> nameToClass = new HashMap<>();

    public Class<?> loadById(int classId) {
        String className = idToName.get(classId);
        return getClassByName(className);
    }

    private Class<?> getClassByName(String className) {
        return nameToClass.computeIfAbsent(className, this::loadClass);
    }

    private Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new VelvetSerializerException("Cannot load class " + className, e);
        }
    }

    public Class<?> putAndLoad(int classId, String name) {
        idToName.put(classId, name);
        return getClassByName(name);
    }
}
