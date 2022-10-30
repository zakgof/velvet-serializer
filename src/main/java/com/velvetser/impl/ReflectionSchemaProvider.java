package com.velvetser.impl;

import com.velvetser.ClassSchema;
import com.velvetser.HotClassProvider;
import com.velvetser.SchemaProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import static com.velvetser.ClassSchema.FieldType.Byte;
import static com.velvetser.ClassSchema.FieldType.FinalObject;
import static com.velvetser.ClassSchema.FieldType.FinalObjectArray;
import static com.velvetser.ClassSchema.FieldType.Int;
import static com.velvetser.ClassSchema.FieldType.PolyObject;
import static com.velvetser.ClassSchema.FieldType.PolyObjectArray;
import static com.velvetser.ClassSchema.FieldType.Short;
import static com.velvetser.ClassSchema.FieldType.String;

@RequiredArgsConstructor
public class ReflectionSchemaProvider implements SchemaProvider {

    private final HotClassProvider hotCache;

    @Override
    public <T> ClassSchema<T> get(Class<T> clazz) {
        ReflectionHotClassProvider.ReflectionHotClass<T> hotClass = (ReflectionHotClassProvider.ReflectionHotClass<T>) hotCache.get(clazz);
        Field[] fields = hotClass.fields();
        ClassSchema.Field<?>[] schemaFields = new ClassSchema.Field<?>[fields.length];
        for (int f=0; f<fields.length; f++) {
            schemaFields[f] = createSchemaField(fields[f], f);
        }
        return () -> schemaFields;
    }

    @Override
    public <T> ClassSchema.Field<T> top(Class<T> clazz) {
        return new DefaultSchemaField<>(0,"", clazz, fieldType(clazz), null);
    }

    private ClassSchema.Field<?> createSchemaField(Field field, int index) {
        Class<?> fieldClazz = field.getType();
        String name = field.getName();

        ClassSchema.FieldType type = fieldType(fieldClazz);
        return new DefaultSchemaField<>(index, name, fieldClazz, type, null);
    }

    private static ClassSchema.FieldType fieldType(Class<?> fieldClazz) {
        Map<Class<?>, ClassSchema.FieldType> primivites = Map.of(
                byte.class, Byte,
                short.class, Short,
                int.class, Int,
                String.class, String
        );
        ClassSchema.FieldType type = primivites.get(fieldClazz);
        if (type == null) {
            if (fieldClazz.isArray()) {
                Class<?> componentClazz = fieldClazz.getComponentType();
                if ((componentClazz.getModifiers() & Modifier.FINAL) != 0) {
                    type = FinalObjectArray;
                } else {
                    type = PolyObjectArray;
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
        private final ClassSchema.FieldType type;
        private final ClassSchema.FieldDetail detail;
    }
}
