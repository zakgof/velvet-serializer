package com.velvetser;

import com.velvetser.impl.WriteContext;
import com.velvetser.stream.BetterOutputStream;

import java.lang.reflect.Type;

public interface CommonSerializer {
    <T> void serializePolyObject(T object, Class<? extends T> clazz, Type[] typeParams, BetterOutputStream bos, WriteContext context);
    <T> void serializeFinalObject(T object, Class<? extends T> clazz, Type[] typeParams, BetterOutputStream bos, WriteContext context);
}
