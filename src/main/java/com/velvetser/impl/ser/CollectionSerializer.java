package com.velvetser.impl.ser;

import com.velvetser.ClassSerializer;
import com.velvetser.CommonDeserializer;
import com.velvetser.CommonSerializer;
import com.velvetser.impl.ReadContext;
import com.velvetser.impl.WriteContext;
import com.velvetser.stream.BetterInputStream;
import com.velvetser.stream.BetterOutputStream;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public class CollectionSerializer<E, T extends Collection<E>> implements ClassSerializer<T> {

    @Override
    public void serialize(T collection, Class<T> clazz, Type[] typeParams, BetterOutputStream bos, WriteContext context, CommonSerializer commonSerializer) {
        bos.writeVarInt(collection.size());
        for (E element : collection) {
            commonSerializer.serializePolyObject(element, (Class<?>) typeParams[0], null, bos, context);
        }
    }

    @Override
    public T deserialize(BetterInputStream bis, Class<T> clazz, Type[] typeParams, ReadContext context, CommonDeserializer commonDeserializer) {
        T collection = (T)new ArrayList();
        Class<?> elementClass = typeParams == null ? Object.class : (Class<?>) typeParams[0];
        int length = bis.readVarInt();
        for (int i = 0; i < length; i++) {
            E element = (E)commonDeserializer.deserializePolyObject(bis, elementClass, typeParams, context);
            collection.add(element);
        }
        return collection;
    }
}
