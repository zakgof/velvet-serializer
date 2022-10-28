package com.velvetser.impl;

import java.util.HashMap;
import java.util.Map;

public class WriteContext {

    private Map<String, Integer> nameToid = new HashMap<>();
    public Integer getKnownClassId(String className) {
        return nameToid.get(className);
    }

    public int putKnownClass(String className) {
        int id = nameToid.size();
        nameToid.put(className, id);
        return id;
    }
}
