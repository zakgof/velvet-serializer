package com.velvetser;

public interface ClassSchema<T> {

    Field<?>[] fields();

    public interface Field<F> {

        int index();

        String name();

        Class<F> clazz();

        FieldType type();

        FieldDetail detail();
    }

    public enum FieldType {
        Byte,
        Short,
        Int,
        FinalObject,
        PolyObject,
        FinalObjectArray,
        PolyObjectArray,
        String
    }

    public interface FieldDetail {
    }
}
