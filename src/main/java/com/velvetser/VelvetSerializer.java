package com.velvetser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public interface VelvetSerializer {

    default public <T> byte[] serialize(T object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        serialize(object, baos);
        return baos.toByteArray();
    }

    public <T> T deserialize(InputStream stream, Class<T> clazz);

    public <T> void serialize(T object, OutputStream stream);
}
