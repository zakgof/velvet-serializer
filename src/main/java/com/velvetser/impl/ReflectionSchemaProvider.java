package com.velvetser.impl;

import com.velvetser.ClassSchema;
import com.velvetser.SchemaProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ReflectionSchemaProvider implements SchemaProvider {
    private final ReflectionHotClassProvider hotCache;

    @Override
    public <T> ClassSchema<T> get(Class<T> clazz) {
        ReflectionHotClassProvider.ReflectionHotClass<T> hotClass = (ReflectionHotClassProvider.ReflectionHotClass<T>) hotCache.get(clazz);
        List<ClassSchema.Field<?>> schemaFields = hotClass.fields()
                .values()
                .stream()
                .map(this::createSchemaField)
                .collect(Collectors.toList());
        return () -> schemaFields;
    }

    @Override
    public <T> ClassSchema.Field<T> top(Class<T> clazz) {
        return new DefaultSchemaField<>("", clazz, fieldDef(clazz));
    }

    private ClassSchema.Field<?> createSchemaField(Field field) {
        Class<?> fieldClazz = field.getType();
        String name = field.getName();

        ClassSchema.FieldDef def = fieldDef(fieldClazz);
        return new DefaultSchemaField<>(name, fieldClazz, def);
    }

    private static ClassSchema.FieldDef fieldDef(Class<?> fieldClazz) {
        Map<Class<?>, ClassSchema.FieldDef> primivites = Map.of(
                byte.class, ClassSchema.ByteFieldDef.INSTANCE,
                short.class, ClassSchema.ShortFieldDef.INSTANCE,
                int.class, ClassSchema.IntFieldDef.INSTANCE,
                String.class, ClassSchema.StringFieldDef.INSTANCE
        );
        ClassSchema.FieldDef def = primivites.get(fieldClazz);
        if (def == null) {
            if (fieldClazz.isArray()) {
                Class<?> componentClazz = fieldClazz.getComponentType();
                if ((componentClazz.getModifiers() & Modifier.FINAL) != 0) {
                    def = new ClassSchema.FinalObjectArrayFieldDef();
                } else {
                    def = new ClassSchema.PolyObjectArrayFieldDef();
                }
            } else {
                if ((fieldClazz.getModifiers() & Modifier.FINAL) != 0) {
                    def = new ClassSchema.FinalFieldDef();
                } else {
                    def = new ClassSchema.PolymorphicFieldDef();
                }
            }
        }
        return def;
    }

    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    private static class DefaultSchemaField<F> implements ClassSchema.Field<F> {
        private final String name;
        private final Class<F> clazz;
        private final ClassSchema.FieldDef def;
    }
}
