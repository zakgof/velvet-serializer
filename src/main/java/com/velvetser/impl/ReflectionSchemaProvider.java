package com.velvetser.impl;

import com.velvetser.ClassSchema;
import com.velvetser.HotClassProvider;
import com.velvetser.SchemaProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import static com.velvetser.ClassSchema.FieldType.*;

@RequiredArgsConstructor
public class ReflectionSchemaProvider implements SchemaProvider {

    private static final Map<Class<?>, ClassSchema.FieldType> BASECLASS_MAPPING = Map.of(
            byte.class, Byte,
            short.class, Short,
            int.class, Int,
            long.class, Long,
            boolean.class, Bool,
            char.class, Char,
            String.class, String
    );
    private static final Map<Class<?>, ClassSchema.FieldType> ARRAYCLASS_MAPPING = Map.of(
            byte.class, ByteArray,
            short.class, ShortArray,
            int.class, IntArray,
            long.class, LongArray,
            boolean.class, BoolArray,
            char.class, CharArray
    );
    private final HotClassProvider hotCache;

    @Override
    public <T> ClassSchema<T> get(Class<T> clazz) {
        FieldsProvider<T> fp = (FieldsProvider<T>) hotCache.get(clazz);
        Field[] fields = fp.fields();
        ClassSchema.Field<?>[] schemaFields = new ClassSchema.Field<?>[fields.length];
        for (int f=0; f<fields.length; f++) {
            schemaFields[f] = createSchemaField(fields[f], f);
        }
        return () -> schemaFields;
    }

    @Override
    public <T> ClassSchema.Field<T> top(Class<T> clazz) {
        return new DefaultSchemaField<>(0,"", clazz, null, fieldType(clazz), null);
    }

    private ClassSchema.Field<?> createSchemaField(Field field, int index) {
        Class<?> fieldClazz = field.getType();
        String name = field.getName();
        Type genericType = field.getGenericType();
        Type[] params = (genericType instanceof ParameterizedType) ? ((ParameterizedType)genericType).getActualTypeArguments() : null;
        ClassSchema.FieldType type = fieldType(fieldClazz);
        return new DefaultSchemaField<>(index, name, fieldClazz, params, type, null);
    }

    private static ClassSchema.FieldType fieldType(Class<?> fieldClazz) {
        ClassSchema.FieldType type = BASECLASS_MAPPING.get(fieldClazz);
        if (type == null) {
            if (fieldClazz.isArray()) {
                Class<?> componentClazz = fieldClazz.getComponentType();
                type = ARRAYCLASS_MAPPING.get(componentClazz);
                if (type == null) {
                    if ((componentClazz.getModifiers() & Modifier.FINAL) != 0) {
                        type = FinalObjectArray;
                    } else {
                        type = PolyObjectArray;
                    }
                }
            } else {
                if ((fieldClazz.getModifiers() & Modifier.FINAL) != 0) {
                    type = FinalObject;
                } else {
                    type = PolyObject;
                }
            }
        }
        return type;
    }

    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    private static class DefaultSchemaField<F> implements ClassSchema.Field<F> {
        private final int index;
        private final String name;
        private final Class<F> clazz;
        private final Type[] typeParams;
        private final ClassSchema.FieldType type;
        private final ClassSchema.FieldDetail detail;
    }
}
