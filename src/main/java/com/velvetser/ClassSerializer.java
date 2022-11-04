package com.velvetser;

import com.velvetser.impl.ReadContext;
import com.velvetser.impl.WriteContext;
import com.velvetser.stream.BetterInputStream;
import com.velvetser.stream.BetterOutputStream;

import java.lang.reflect.Type;

public interface ClassSerializer<T> {
    void serialize(T object, Class<T> clazz, Type[] typeParams, BetterOutputStream bos, WriteContext context, CommonSerializer commonSerializer);

    T deserialize(BetterInputStream bis, Class<T> clazz, Type[] typeParams, ReadContext context, CommonDeserializer commonDeserializer);
}
