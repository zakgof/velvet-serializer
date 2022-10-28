package com.velvetser.impl;

import com.velvetser.VelvetSerializerException;

import java.util.HashMap;
import java.util.Map;

public class ReadContext {

    private Map<Integer, String> idToName = new HashMap<>();


    public Class<?> loadById(int classId) {
        String className = idToName.get(classId);
        return loadClass(className);
    }

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new VelvetSerializerException("Cannot load class " + className, e);
        }
    }

    public Class<?> putAndLoad(int classId, String name) {
        idToName.put(classId, name);
        return loadClass(name);
    }
}
