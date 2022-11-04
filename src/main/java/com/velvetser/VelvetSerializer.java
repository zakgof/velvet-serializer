package com.velvetser;

import com.velvetser.stream.VelvetInput;
import com.velvetser.stream.VelvetInputs;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public interface VelvetSerializer {

    <T> void serialize(T object, OutputStream stream);

    default <T> byte[] serialize(T object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        serialize(object, baos);
        return baos.toByteArray();
    }

    <T> T deserialize(VelvetInput stream, Class<T> clazz);

    default <T> T deserialize(InputStream stream, Class<T> clazz) {
        return deserialize(VelvetInputs.from(stream), clazz);
    }

    default <T> T deserialize(byte[] inputBytes, Class<T> clazz) {
        return deserialize(VelvetInputs.from(inputBytes), clazz);
    }
}
