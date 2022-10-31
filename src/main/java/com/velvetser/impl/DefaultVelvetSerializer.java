package com.velvetser.impl;

import com.velvetser.ClassSchema;
import com.velvetser.HotClass;
import com.velvetser.HotClassProvider;
import com.velvetser.SchemaProvider;
import com.velvetser.VelvetSerializer;
import com.velvetser.VelvetSerializerException;
import com.velvetser.stream.BetterInputStream;
import com.velvetser.stream.BetterOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.function.Function;

import static com.velvetser.impl.Constants.Control.INDEXED_CLASS;
import static com.velvetser.impl.Constants.Control.NAMED_CLASS;
import static com.velvetser.impl.Constants.Control.NULL;
import static com.velvetser.impl.Constants.Control.SAME_CLASS;

@Slf4j
@RequiredArgsConstructor
public class DefaultVelvetSerializer implements VelvetSerializer {

    private final HotClassProvider hotClassProvider;
    private final SchemaProvider schemaProvider;

    @Override
    public <T> void serialize(T object, OutputStream stream) {
        Class clazz = object.getClass();
        ClassSchema.Field field = schemaProvider.top(clazz);
        serializeObjectFieldValue(field, new BetterOutputStream(stream), new WriteContext(), object);
    }

    @Override
    public <T> T deserialize(InputStream stream, Class<T> clazz) {
        ClassSchema.Field<T> field = schemaProvider.top(clazz);
        return deserializeFieldValue(field, new BetterInputStream(stream), new ReadContext());
    }

    private <T> void serializeFinalObject(T object, Class<? extends T> clazz, BetterOutputStream
            bos, WriteContext context) {
        if (writeNullOr(object, bos)) {
            bos.writeVarInt(SAME_CLASS);
            serializeObjectContent(object, (Class) clazz, bos, context);
        }
    }

    private <T> boolean writeNullOr(T object, BetterOutputStream bos) {
        if (object == null) {
            bos.writeVarInt(NULL);
            return false;
        }
        return true;
    }

    private <T> void serializeObjectContent(T object, Class<T> clazz, BetterOutputStream bos, WriteContext
            context) {
        ClassSchema<T> schema = schemaProvider.get(clazz);
        HotClass<T> hotClass = hotClassProvider.get(clazz);
        for (ClassSchema.Field<?> schemaField : schema.fields()) {
            serializeField(object, bos, hotClass, schemaField, context);
        }
    }

    private <T> void serializePolyObject(T object, Class<T> clazz, BetterOutputStream bos, WriteContext context) {
        if (object == null) {
            bos.writeVarInt(NULL);
            return;
        }
        var actualClass = object.getClass();
        if (actualClass.equals(clazz)) {
            bos.writeVarInt(SAME_CLASS);
        } else {
            serializeClass(bos, actualClass, context);
        }
        serializeObjectContent(object, (Class) actualClass, bos, context);
    }

    private void serializeClass(BetterOutputStream bos, Class<?> actualClass, WriteContext context) {
        Integer classId = context.getKnownClassId(actualClass);
        if (classId == null) {
            classId = context.putKnownClass(actualClass);
            bos.writeVarInt(NAMED_CLASS);
            bos.writeVarInt(classId);
            bos.writeString(actualClass.getName(), false);
        } else {
            bos.writeVarInt(INDEXED_CLASS);
            bos.writeVarInt(classId);
        }
    }

    private <T> void serializeField(T object, BetterOutputStream
            bos, HotClass<T> hotClass, ClassSchema.Field<?> schemaField, WriteContext context) {
        int fieldIndex = schemaField.index();
        switch (schemaField.type()) {
            case Byte:
                bos.writeByte(hotClass.getByte(fieldIndex, (T) object));
                break;
            case Short:
                bos.writeShort(hotClass.getShort(fieldIndex, object));
                break;
            case Int:
                bos.writeVarInt(hotClass.getInt(fieldIndex, object));
                break;
            case Long:
                bos.writeVarLong(hotClass.getLong(fieldIndex, object));
                break;
            case Bool:
                bos.writeByte(hotClass.getBoolean(fieldIndex, object) ? (byte) 1 : 0);
                break;
            case Char:
                bos.writeChar(hotClass.getChar(fieldIndex, object));
                break;
            default:
                serializeObjectField(object, schemaField, hotClass, bos, context);
        }
    }

    private <T, F> void serializeObjectField(T object, ClassSchema.Field<F> schemaField, HotClass<T> hotClass, BetterOutputStream bos, WriteContext
            context) {
        F fieldValue = hotClass.get(schemaField.index(), object, schemaField.clazz());
        serializeObjectFieldValue(schemaField, bos, context, fieldValue);
    }

    private <F> void serializeObjectFieldValue(ClassSchema.Field<F> schemaField, BetterOutputStream
            bos, WriteContext context, F fieldValue) {
        switch (schemaField.type()) {
            case String:
                bos.writeString((String) fieldValue, false);
                break;
            case FinalObject:
                serializeFinalObject(fieldValue, schemaField.clazz(), bos, context);
                break;
            case PolyObject:
                serializePolyObject(fieldValue, schemaField.clazz(), bos, context);
                break;
            case FinalObjectArray:
                serializeFinalObjectArray(fieldValue, schemaField.clazz(), bos, context);
                break;
            case PolyObjectArray:
                serializePolyObjectArray(fieldValue, schemaField.clazz(), bos, context);
                break;
            case ByteArray:
                serializeByteArray((byte[]) fieldValue, bos, context);
                break;
            case ShortArray:
                serializeShortArray((short[]) fieldValue, bos, context);
                break;
            case IntArray:
                serializeIntArray((int[]) fieldValue, bos, context);
                break;
            case LongArray:
                serializeLongArray((long[]) fieldValue, bos, context);
                break;
            case BoolArray:
                serializeBoolArray((boolean[]) fieldValue, bos, context);
                break;
            case CharArray:
                serializeCharArray((char[]) fieldValue, bos, context);
                break;
            default:
                throw new VelvetSerializerException("Unknown field definition");
        }
    }

    private void serializeByteArray(byte[] fieldValue, BetterOutputStream bos, WriteContext context) {
        if (writeNullOr(fieldValue, bos)) {
            bos.writeVarInt(fieldValue.length);
            bos.writeBytes(fieldValue);
        }
    }

    private void serializeShortArray(short[] fieldValue, BetterOutputStream bos, WriteContext context) {
        if (writeNullOr(fieldValue, bos)) {
            bos.writeVarInt(fieldValue.length);
            for (short element : fieldValue) {
                bos.writeShort(element);
            }
        }
    }

    private void serializeIntArray(int[] fieldValue, BetterOutputStream bos, WriteContext context) {
        if (writeNullOr(fieldValue, bos)) {
            bos.writeVarInt(fieldValue.length);
            for (int element : fieldValue) {
                bos.writeVarInt(element);
            }
        }
    }

    private void serializeLongArray(long[] fieldValue, BetterOutputStream bos, WriteContext context) {
        if (writeNullOr(fieldValue, bos)) {
            bos.writeVarInt(fieldValue.length);
            for (long element : fieldValue) {
                bos.writeVarLong(element);
            }
        }
    }

    private void serializeBoolArray(boolean[] fieldValue, BetterOutputStream bos, WriteContext context) {
        if (writeNullOr(fieldValue, bos)) {
            bos.writeVarInt(fieldValue.length);
            for (boolean element : fieldValue) {
                bos.writeByte(element ? (byte)1 : 0);
            }
        }
    }

    private void serializeCharArray(char[] fieldValue, BetterOutputStream bos, WriteContext context) {
        if (writeNullOr(fieldValue, bos)) {
            bos.writeVarInt(fieldValue.length);
            for (char element : fieldValue) {
                bos.writeChar(element);
            }
        }
    }

    private <F> void serializePolyObjectArray(F fieldValue, Class<F> clazz, BetterOutputStream
            bos, WriteContext context) {
        if (writeNullOr(fieldValue, bos)) {
            Object[] array = (Object[]) fieldValue;
            int length = array.length;
            bos.writeVarInt(length);
            Class elementClazz = clazz.getComponentType();
            for (Object element : array) {
                serializePolyObject(element, elementClazz, bos, context);
            }
        }
    }

    private <F, T> void serializeFinalObjectArray(F fieldValue, Class<? extends F> clazz, BetterOutputStream
            bos, WriteContext context) {
        if (writeNullOr(fieldValue, bos)) {
            Object[] array = (Object[]) fieldValue;
            int length = array.length;
            bos.writeVarInt(length);
            Class elementClazz = clazz.getComponentType();
            for (Object element : array) {
                serializeFinalObject(element, elementClazz, bos, context);
            }
        }
    }

    private <T> T deserializeFinalObject(BetterInputStream bis, Class<T> clazz, ReadContext context) {
        return readNullOr(bis, control -> {
            if (control != SAME_CLASS) {
                throw new VelvetSerializerException("Unknown final object control byte: " + control);
            }
            return deserializeObjectContent(bis, clazz, context);
        });
    }

    private <T> T deserializePolyObject(BetterInputStream bis, Class<T> clazz, ReadContext context) {
        return readNullOr(bis, control -> {
            if (control == SAME_CLASS) {
                return deserializeObjectContent(bis, clazz, context);
            }
            Class<?> actualClass = deserializeClass(bis, context, control);
            return (T) deserializeObjectContent(bis, actualClass, context);
        });
    }

    private Class<?> deserializeClass(BetterInputStream bis, ReadContext context, int control) {
        if (control == INDEXED_CLASS) {
            int classId = bis.readVarInt();
            return context.loadById(classId);
        }
        if (control == NAMED_CLASS) {
            int classId = bis.readVarInt();
            String name = bis.readString();
            return context.putAndLoad(classId, name);
        }
        throw new VelvetSerializerException("Unknown control byte when reading polymorphic class " + control);
    }

    private <T> T deserializeObjectContent(BetterInputStream bis, Class<T> clazz, ReadContext context) {
        ClassSchema<T> schema = schemaProvider.get(clazz);
        HotClass<T> hotClass = hotClassProvider.get(clazz);
        T object = hotClass.instantiate();
        for (ClassSchema.Field<?> schemaField : schema.fields()) {
            int fieldIndex = schemaField.index();
            switch (schemaField.type()) {
                case Byte:
                    hotClass.setByte(fieldIndex, object, bis.readByte());
                    break;
                case Short:
                    hotClass.setShort(fieldIndex, object, bis.readShort());
                    break;
                case Int:
                    hotClass.setInt(fieldIndex, object, bis.readVarInt());
                    break;
                case Long:
                    hotClass.setLong(fieldIndex, object, bis.readVarLong());
                    break;
                case Bool:
                    hotClass.setBoolean(fieldIndex, object, bis.readByte() == 1);
                    break;
                case Char:
                    hotClass.setChar(fieldIndex, object, bis.readChar());
                    break;
                default:
                    deserializeField(object, schemaField, hotClass, bis, context);
            }
        }
        return object;
    }

    private <T, F> void deserializeField(T
                                                 object, ClassSchema.Field<F> schemaField, HotClass<T> hotClass, BetterInputStream bis, ReadContext context) {
        F fieldValue = deserializeFieldValue(schemaField, bis, context);
        hotClass.set(schemaField.index(), object, fieldValue);
    }

    private <F> F
    deserializeFieldValue(ClassSchema.Field<F> schemaField, BetterInputStream bis, ReadContext context) {
        switch (schemaField.type()) {
            case String:
                return (F) bis.readString();
            case FinalObject:
                return deserializeFinalObject(bis, schemaField.clazz(), context);
            case PolyObject:
                return deserializePolyObject(bis, schemaField.clazz(), context);
            case FinalObjectArray:
                return deserializeFinalObjectArray(bis, schemaField.clazz(), context);
            case PolyObjectArray:
                return deserializePolyObjectArray(bis, schemaField.clazz(), context);
            case ByteArray:
                return (F) deserializeByteArray(bis, context);
            case ShortArray:
                return (F) deserializeShortArray(bis, context);
            case IntArray:
                return (F) deserializeIntArray(bis, context);
            case LongArray:
                return (F) deserializeLongArray(bis, context);
            case BoolArray:
                return (F) deserializeBoolArray(bis, context);
            case CharArray:
                return (F) deserializeCharArray(bis, context);
            default:
                throw new VelvetSerializerException("Unknown field definition");
        }
    }

    private byte[] deserializeByteArray(BetterInputStream bis, ReadContext context) {
        return readNullOr(bis, length -> {
            byte[] value = new byte[length];
            bis.readBytes(length, value);
            return value;
        });
    }

    private short[] deserializeShortArray(BetterInputStream bis, ReadContext context) {
        return readNullOr(bis, length -> {
            short[] value = new short[length];
            for (int i = 0; i < length; i++)
                value[i] = bis.readShort();
            return value;
        });
    }

    private int[] deserializeIntArray(BetterInputStream bis, ReadContext context) {
        return readNullOr(bis, length -> {
            int[] value = new int[length];
            for (int i = 0; i < length; i++)
                value[i] = bis.readVarInt();
            return value;
        });
    }

    private long[] deserializeLongArray(BetterInputStream bis, ReadContext context) {
        return readNullOr(bis, length -> {
            long[] value = new long[length];
            for (int i = 0; i < length; i++)
                value[i] = bis.readVarLong();
            return value;
        });
    }

    private char[] deserializeCharArray(BetterInputStream bis, ReadContext context) {
        return readNullOr(bis, length -> {
            char[] value = new char[length];
            for (int i = 0; i < length; i++)
                value[i] = bis.readChar();
            return value;
        });
    }

    private boolean[] deserializeBoolArray(BetterInputStream bis, ReadContext context) {
        return readNullOr(bis, length -> {
            boolean[] value = new boolean[length];
            for (int i = 0; i < length; i++)
                value[i] = bis.readByte() == 1;
            return value;
        });
    }

    private <F> F deserializeFinalObjectArray(BetterInputStream bis, Class<F> clazz, ReadContext context) {
        return readNullOr(bis, length -> {
            Class<?> componentClazz = clazz.getComponentType();
            Object[] array = (Object[]) Array.newInstance(componentClazz, length);
            for (int i = 0; i < length; i++) {
                array[i] = deserializeFinalObject(bis, componentClazz, context);
            }
            return (F) array;
        });
    }

    private <F> F deserializePolyObjectArray(BetterInputStream bis, Class<F> clazz, ReadContext context) {
        return readNullOr(bis, length -> {
            Class<?> componentClazz = clazz.getComponentType();
            Object[] array = (Object[]) Array.newInstance(componentClazz, length);
            for (int i = 0; i < length; i++) {
                array[i] = deserializePolyObject(bis, componentClazz, context);
            }
            return (F) array;
        });
    }

    private <F> F readNullOr(BetterInputStream bis, Function<Integer, F> nonNullAction) {
        int control = bis.readVarInt();
        if (control == NULL) {
            return null;
        }
        return nonNullAction.apply(control);
    }

    private <F> F deserializeList(BetterInputStream bis) {
        return null;
    }

}
