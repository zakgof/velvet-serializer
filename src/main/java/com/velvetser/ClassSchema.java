package com.velvetser;

import java.lang.reflect.Type;

public interface ClassSchema<T> {

    Field<?>[] fields();

    interface Field<F> {

        int index();

        String name();

        Class<F> clazz();

        Type[] typeParams();

        FieldType type();

        FieldDetail detail();
    }

    enum FieldType {
        Byte,
        Short,
        Int,
        Long,
        Char,
        Bool,
        ByteArray,
        ShortArray,
        IntArray,
        LongArray,
        CharArray,
        BoolArray,
        FinalObject,
        PolyObject,
        FinalObjectArray,
        PolyObjectArray,
        String
    }

    interface FieldDetail {
    }
}

