package com.velvetser.impl;

import com.velvetser.HotClass;
import com.velvetser.HotClassProvider;
import com.velvetser.VelvetSerializerException;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnsafeHotClassProvider implements HotClassProvider {

    private final Objenesis objenesis = new ObjenesisStd();
    private final Unsafe unsafe;

    public UnsafeHotClassProvider() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new VelvetSerializerException("Error getting Unsafe", e);
        }
    }

    @Override
    public <T> HotClass<T> get(Class<T> clazz) {
        return new UnsafeHotClass<>(clazz, objenesis.getInstantiatorOf(clazz));
    }

    class UnsafeHotClass<T> implements HotClass<T>, FieldsProvider<T> {

        @Getter
        @Accessors(fluent = true)
        private final Map<String, Integer> fieldIndexing;
        private final ObjectInstantiator<T> instantiator;
        private final Field[] fields;
        private final long[] fieldOffsets;

        public UnsafeHotClass(Class<T> clazz, ObjectInstantiator<T> instantiator) {
            this.fields = getAllFields(clazz);
            this.fieldIndexing = new HashMap<>();
            this.fieldOffsets = new long[fields.length];
            for (int f = 0; f < fields.length; f++) {
                Field field = fields[f];
                fieldIndexing.put(field.getName(), f);
                fieldOffsets[f] = unsafe.objectFieldOffset(field);
            }
            this.instantiator = instantiator;
        }

        private Field[] getAllFields(Class<?> clazz) {
            List<Field> fieldList = new ArrayList<>(32);
            for (Class<?> cl = clazz; cl != null && cl != Object.class; cl = cl.getSuperclass()) {
                for (Field field : cl.getDeclaredFields()) {
                    if ((field.getModifiers() & (Modifier.STATIC | Modifier.TRANSIENT)) == 0) {
                        field.setAccessible(true);
                        fieldList.add(field);
                    }
                }
            }
            return fieldList.toArray(new Field[0]);
        }

        @Override
        public T instantiate() {
            return instantiator.newInstance();
        }

        @Override
        public <F> F get(int fieldIndex, T object, Class<F> clazz) {
            return (F)unsafe.getObject(object, fieldOffsets[fieldIndex]);
        }

        @Override
        public byte getByte(int fieldIndex, T object) {
            return unsafe.getByte(object, fieldOffsets[fieldIndex]);
        }

        @Override
        public short getShort(int fieldIndex, T object) {
            return unsafe.getShort(object, fieldOffsets[fieldIndex]);
        }

        @Override
        public int getInt(int fieldIndex, T object) {
            return unsafe.getInt(object, fieldOffsets[fieldIndex]);
        }

        @Override
        public long getLong(int fieldIndex, T object) {
            return unsafe.getLong(object, fieldOffsets[fieldIndex]);
        }

        @Override
        public boolean getBoolean(int fieldIndex, T object) {
            return unsafe.getBoolean(object, fieldOffsets[fieldIndex]);
        }

        @Override
        public char getChar(int fieldIndex, T object) {
            return unsafe.getChar(object, fieldOffsets[fieldIndex]);
        }

        @Override
        public <F> void set(int fieldIndex, T object, F fieldValue) {
            unsafe.putObject(object, fieldOffsets[fieldIndex], fieldValue);
        }

        @Override
        public void setByte(int fieldIndex, T object, byte fieldValue) {
            unsafe.putByte(object, fieldOffsets[fieldIndex], fieldValue);
        }

        @Override
        public void setShort(int fieldIndex, T object, short fieldValue) {
            unsafe.putShort(object, fieldOffsets[fieldIndex], fieldValue);
        }

        @Override
        public void setInt(int fieldIndex, T object, int fieldValue) {
            unsafe.putInt(object, fieldOffsets[fieldIndex], fieldValue);
        }

        @Override
        public void setLong(int fieldIndex, T object, long fieldValue) {
            unsafe.putLong(object, fieldOffsets[fieldIndex], fieldValue);
        }

        @Override
        public void setBoolean(int fieldIndex, T object, boolean fieldValue) {
            unsafe.putBoolean(object, fieldOffsets[fieldIndex], fieldValue);
        }

        @Override
        public void setChar(int fieldIndex, T object, char fieldValue) {
            unsafe.putChar(object, fieldOffsets[fieldIndex], fieldValue);
        }

        public Field[] fields() {
            return fields;
        }
    }
}
