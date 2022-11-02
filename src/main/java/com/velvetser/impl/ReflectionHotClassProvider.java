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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectionHotClassProvider implements HotClassProvider {

    private final Objenesis objenesis = new ObjenesisStd();

    @Override
    public <T> HotClass<T> get(Class<T> clazz) {
        return new ReflectionHotClass<>(clazz, objenesis.getInstantiatorOf(clazz));
    }

    static class ReflectionHotClass<T> implements HotClass<T>, FieldsProvider<T> {

        @Getter
        @Accessors(fluent = true)
        private final Map<String, Integer> fieldIndexing;
        private final Field[] fields;
        private final ObjectInstantiator<T> instantiator;

        public ReflectionHotClass(Class<T> clazz, ObjectInstantiator<T> instantiator) {
            this.fields = getAllFields(clazz);
            this.fieldIndexing = new HashMap<>();
            for (int f = 0; f < fields.length; f++)
                fieldIndexing.put(fields[f].getName(), f);
            this.instantiator = instantiator;
        }

        private static Field[] getAllFields(Class<?> clazz) {
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
            try {
                return clazz.cast(fields[fieldIndex].get(object));
            } catch (ReflectiveOperationException e) {
                throw new VelvetSerializerException(e);
            }
        }

        @Override
        public byte getByte(int fieldIndex, T object) {
            try {
                return fields[fieldIndex].getByte(object);
            } catch (ReflectiveOperationException e) {
                throw new VelvetSerializerException(e);
            }
        }

        @Override
        public short getShort(int fieldIndex, T object) {
            try {
                return fields[fieldIndex].getShort(object);
            } catch (ReflectiveOperationException e) {
                throw new VelvetSerializerException(e);
            }
        }

        @Override
        public int getInt(int fieldIndex, T object) {
            try {
                return fields[fieldIndex].getInt(object);
            } catch (ReflectiveOperationException e) {
                throw new VelvetSerializerException(e);
            }
        }

        @Override
        public long getLong(int fieldIndex, T object) {
            try {
                return fields[fieldIndex].getLong(object);
            } catch (ReflectiveOperationException e) {
                throw new VelvetSerializerException(e);
            }
        }

        @Override
        public boolean getBoolean(int fieldIndex, T object) {
            try {
                return fields[fieldIndex].getBoolean(object);
            } catch (ReflectiveOperationException e) {
                throw new VelvetSerializerException(e);
            }
        }

        @Override
        public char getChar(int fieldIndex, T object) {
            try {
                return fields[fieldIndex].getChar(object);
            } catch (ReflectiveOperationException e) {
                throw new VelvetSerializerException(e);
            }
        }

        @Override
        public <F> void set(int fieldIndex, T object, F fieldValue) {
            try {
                fields[fieldIndex].set(object, fieldValue);
            } catch (ReflectiveOperationException e) {
                throw new VelvetSerializerException(e);
            }
        }

        @Override
        public void setByte(int fieldIndex, T object, byte fieldValue) {
            try {
                fields[fieldIndex].setByte(object, fieldValue);
            } catch (ReflectiveOperationException e) {
                throw new VelvetSerializerException(e);
            }
        }

        @Override
        public void setShort(int fieldIndex, T object, short fieldValue) {
            try {
                fields[fieldIndex].setShort(object, fieldValue);
            } catch (ReflectiveOperationException e) {
                throw new VelvetSerializerException(e);
            }
        }

        @Override
        public void setInt(int fieldIndex, T object, int fieldValue) {
            try {
                fields[fieldIndex].setInt(object, fieldValue);
            } catch (ReflectiveOperationException e) {
                throw new VelvetSerializerException(e);
            }
        }

        @Override
        public void setLong(int fieldIndex, T object, long fieldValue) {
            try {
                fields[fieldIndex].setLong(object, fieldValue);
            } catch (ReflectiveOperationException e) {
                throw new VelvetSerializerException(e);
            }
        }

        @Override
        public void setBoolean(int fieldIndex, T object, boolean fieldValue) {
            try {
                fields[fieldIndex].setBoolean(object, fieldValue);
            } catch (ReflectiveOperationException e) {
                throw new VelvetSerializerException(e);
            }
        }

        @Override
        public void setChar(int fieldIndex, T object, char fieldValue) {
            try {
                fields[fieldIndex].setChar(object, fieldValue);
            } catch (ReflectiveOperationException e) {
                throw new VelvetSerializerException(e);
            }
        }

        public Field[] fields() {
            return fields;
        }
    }
}
