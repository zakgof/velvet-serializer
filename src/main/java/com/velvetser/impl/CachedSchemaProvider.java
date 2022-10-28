package com.velvetser.impl;

import com.velvetser.ClassSchema;
import com.velvetser.SchemaProvider;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class CachedSchemaProvider implements SchemaProvider {

    private final Map<String, ClassSchema<?>> cacheSchema = new ConcurrentHashMap<>();
    private final Map<String, ClassSchema.Field<?>> cacheTop = new ConcurrentHashMap<>();
    private final SchemaProvider provider;

    @Override
    public <T> ClassSchema<T> get(Class<T> clazz) {
        return (ClassSchema<T>) cacheSchema.computeIfAbsent(clazz.getName(), name -> provider.get(clazz));
    }

    @Override
    public <T> ClassSchema.Field<T> top(Class<T> clazz) {
        return (ClassSchema.Field<T>)cacheTop.computeIfAbsent(clazz.getName(), name -> provider.top(clazz));
    }
}
