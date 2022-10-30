package com.velvetser;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.velvetser.data.Data;
import org.junit.jupiter.api.Test;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayOutputStream;
import java.util.stream.IntStream;

class PerfTest {

    private static final Data[] original = IntStream.range(0, 600_000)
            .mapToObj(Data::new)
            .toArray(Data[]::new);

    @Test
    void bigArray() throws InterruptedException {

        // Thread.sleep(10000);

        VelvetSerializer serializer = VelvetSerializerBuilder.create()
                .build();

        for (int i=0; i<100; i++) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(50_000_000);
            serializer.serialize(original, baos);
            byte[] bytes = baos.toByteArray();
        }
        //System.out.println(bytes.length);

//        Data[] restored = serializer.deserialize(new ByteArrayInputStream(bytes), Data[].class);
//
//        System.out.println(bytes.length);
//
//        assertArrayEquals(original, restored);
    }

    @Test
    void bigArrayKryo() throws InterruptedException {
        // Thread.sleep(10000);

        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());

        for (int i=0; i<100; i++) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(50_000_000);
            Output output = new Output(outputStream);
            kryo.writeObject(output, original);
            output.flush();
            byte[] bytes = outputStream.toByteArray();
        }
//        Data[] restored = kryo.readObject(new Input(new ByteArrayInputStream(bytes)), Data[].class);
//
//
//        assertArrayEquals(original, restored);
    }

}
