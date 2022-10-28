package com.velvetser.impl;

import com.velvetser.HotClass;
import com.velvetser.HotClassProvider;
import com.velvetser.VelvetSerializerException;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ReflectionHotClassProvider implements HotClassProvider {

    private final Objenesis objenesis = new ObjenesisStd();

    @Override
    public <T> HotClass<T> get(Class<T> clazz) {
        return new ReflectionHotClass<>(clazz, objenesis.getInstantiatorOf(clazz));
    }

    static class ReflectionHotClass<T> implements HotClass<T> {

        @Getter
        @Accessors(fluent = true)
        private final Map<String, Field> fields;
        private final ObjectInstantiator<T> instantiator;

        public ReflectionHotClass(Class<T> clazz, ObjectInstantiator<T> instantiator) {
            this.fields = getAllFields(clazz);
            this.instantiator = instantiator;
        }

        private static Map<String, Field> getAllFields(Class<?> clazz) {
            Map<String, Field> map = new HashMap<>();
            for (Class<?> cl = clazz; cl != null; cl = cl.getSuperclass()) {
                for (Field field : cl.getDeclaredFields()) {
                    if ((field.getModifiers() & (Modifier.STATIC | Modifier.TRANSIENT)) == 0) {
                        field.setAccessible(true);
                        map.put(field.getName(), field);
                    }
                }
            }
            return map;
        }

        @Override
        public T instantiate() {
            return instantiator.newInstance();
        }

        @Override
        public <F> F get(String fieldName, T object, Class<F> clazz) {
            return safeCalc(() -> clazz.cast(fields.get(fieldName).get(object)));
        }

        @Override
        public byte getByte(String fieldName, T object) {
            return safeCalc(() -> fields.get(fieldName).getByte(object));
        }

        @Override
        public short getShort(String fieldName, T object) {
            return safeCalc(() -> fields.get(fieldName).getShort(object));
        }

        @Override
        public <F> void set(String fieldName, T object, F fieldValue) {
            safeAction(() -> fields.get(fieldName).set(object, fieldValue));
        }

        @Override
        public void setByte(String fieldName, T object, byte fieldValue) {
            safeAction(() -> fields.get(fieldName).setByte(object, fieldValue));
        }

        @Override
        public void setShort(String fieldName, T object, short fieldValue) {
            safeAction(() -> fields.get(fieldName).setShort(object, fieldValue));
        }

        interface ReflectionCalc<R> {
            R run() throws ReflectiveOperationException;
        }

        interface ReflectionAction {
            void run() throws ReflectiveOperationException;
        }

        private <R> R safeCalc(ReflectionCalc<R> action) {
            try {
                return action.run();
            } catch (ReflectiveOperationException e) {
                throw new VelvetSerializerException(e);
            }
        }

        private void safeAction(ReflectionAction action) {
            try {
                action.run();
            } catch (ReflectiveOperationException e) {
                throw new VelvetSerializerException(e);
            }
        }

    }
}
