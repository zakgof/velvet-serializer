package com.velvetser.impl;

import com.velvetser.ClassSchema;
import com.velvetser.SchemaProvider;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

public class CachedSchemaProvider implements SchemaProvider {

    private final Map<Class<?>, ClassSchema<?>> cacheSchema = new IdentityHashMap<>();
    private final Map<Class<?>, ClassSchema.Field<?>> cacheTop = new IdentityHashMap<>();
    private final Function<Class<?>, ClassSchema<?>> providerGetFunc;
    private final Function<Class<?>, ClassSchema.Field<?>> providerTopFunc;

    public CachedSchemaProvider(SchemaProvider provider) {
        this.providerGetFunc = provider::get;
        this.providerTopFunc = provider::top;
    }

    @Override
    public <T> ClassSchema<T> get(Class<T> clazz) {
        return (ClassSchema<T>) cacheSchema.computeIfAbsent(clazz, providerGetFunc);
    }

    @Override
    public <T> ClassSchema.Field<T> top(Class<T> clazz) {
        return (ClassSchema.Field<T>)cacheTop.computeIfAbsent(clazz, providerTopFunc);
    }
}
