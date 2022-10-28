package com.velvetser;

public interface SchemaProvider {
    <T> ClassSchema<T> get(Class<T> clazz);

    <T> ClassSchema.Field<T> top(Class<T> clazz);
}
