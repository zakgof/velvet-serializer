package com.velvetser;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.velvetser.data.Data;
import org.junit.jupiter.api.Test;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BasicTest {
    @Test
    void testMatch() {
        Data original = new Data(1L);

        VelvetSerializer serializer = VelvetSerializerBuilder.create()
                .build();
        byte[] bytes = serializer.serialize(original);
        Data restored = serializer.deserialize(new ByteArrayInputStream(bytes), Data.class);

        System.out.println(original);
        System.out.println(restored);

        System.out.println(bytes.length);

        assertEquals(original, restored);
    }

    @Test
    void kryo() {
        Data original = new Data(1L);

        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        kryo.writeObject(output, original);
        output.flush();
        byte[] bytes = outputStream.toByteArray();
        Data restored = kryo.readObject(new Input(new ByteArrayInputStream(bytes)), Data.class);

        System.out.println(bytes.length);
        assertEquals(original, restored);
    }
}
