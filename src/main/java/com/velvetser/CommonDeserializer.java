package com.velvetser;

import com.velvetser.impl.ReadContext;
import com.velvetser.stream.BetterInputStream;

import java.lang.reflect.Type;

public interface CommonDeserializer {
    <T> T deserializeFinalObject(BetterInputStream bis, Class<T> clazz, Type[] typeParams, ReadContext context);

    <T> T deserializePolyObject(BetterInputStream bis, Class<T> clazz, Type[] typeParams, ReadContext context);
}
