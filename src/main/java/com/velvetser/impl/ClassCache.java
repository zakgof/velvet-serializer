package com.velvetser.impl;

import com.velvetser.ClassSchema;
import com.velvetser.ClassSerializer;
import com.velvetser.HotClass;
import com.velvetser.HotClassProvider;
import com.velvetser.SchemaProvider;
import lombok.RequiredArgsConstructor;

import java.util.IdentityHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ClassCache {
    private final HotClassProvider hotClassProvider;
    private final SchemaProvider schemaProvider;
    private final ClassSerializerProvider serializerProvider;

    private final Map<Class<?>, ClassContext<?>> cache = new IdentityHashMap<>();

    public <T> ClassContext<T> context(Class<T> clazz) {
        return (ClassContext<T>) cache.computeIfAbsent(clazz, ClassContext::new);
    }

    @RequiredArgsConstructor
    public class ClassContext<T> {
        private final Class<T> clazz;
        private ClassSchema<T> fieldSchema;
        private ClassSchema.Field<T> topSchema;
        private ClassSerializer<T> serializer;
        private boolean serializerLoaded = false;
        private HotClass<T> hotClass;

        public ClassSchema<T> fieldSchema() {
            if (fieldSchema == null)
                fieldSchema = schemaProvider.get(clazz);
            return fieldSchema;
        }

        public ClassSchema.Field<T> topSchema() {
            if (topSchema == null)
                topSchema = schemaProvider.top(clazz);
            return topSchema;
        }

        public ClassSerializer<T> serializer() {
            if (!serializerLoaded) {
                serializer = serializerProvider.get(clazz);
                serializerLoaded = true;
            }
            return serializer;
        }

        public HotClass<T> hotClass() {
            if (hotClass == null)
                hotClass = hotClassProvider.get(clazz);
            return hotClass;
        }

    }

}
