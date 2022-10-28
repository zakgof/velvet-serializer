package com.velvetser;

import java.util.List;

public interface ClassSchema<T> {

    List<Field<?>> fields();

    public interface Field<F> {
        String name();

        Class<F> clazz();

        FieldDef def();
    }

    public interface FieldDef {
    }

    class ByteFieldDef implements FieldDef {
        public static ByteFieldDef INSTANCE = new ByteFieldDef();
    }

    class ShortFieldDef implements FieldDef {
        public static final ShortFieldDef INSTANCE = new ShortFieldDef();
    }

    class IntFieldDef implements FieldDef {
        public static final IntFieldDef INSTANCE = new IntFieldDef();
    }

    class FinalFieldDef implements FieldDef {
    }

    class PolymorphicFieldDef implements FieldDef {
    }

    class PolyObjectArrayFieldDef implements FieldDef {
    }

    class FinalObjectArrayFieldDef implements FieldDef {
    }

    class StringFieldDef implements FieldDef {
        public static final StringFieldDef INSTANCE = new StringFieldDef();
    }

    class ListFieldDef implements FieldDef {
        public static final ListFieldDef INSTANCE = new ListFieldDef();
    }


}
