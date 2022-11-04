package com.velvetser;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.velvetser.data.Data;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayOutputStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class PerfTest {

    private static final Data[] original = IntStream.range(0, 10_000)
            .mapToObj(Data::new)
            .toArray(Data[]::new);


    private static final int[] ints = new int[100000];
    private static byte[] VELVET_BYTES;
    private static byte[] KRYO_BYTES;

    @BeforeAll
    public static void prepareBytes() {
        VelvetSerializer serializer = VelvetSerializerBuilder.create()
                .build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        serializer.serialize(original, baos);
        VELVET_BYTES = baos.toByteArray();

        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        kryo.writeObject(output, original);
        output.flush();
        KRYO_BYTES = baos.toByteArray();
    }

    @Test
    void velvetSerializeIntArray() throws InterruptedException {
        velvetSerialize(ints, 10_000, 1_000_000);
    }

    @Test
    void kryoSerializeIntArray() throws InterruptedException {
        kryoSerialize(ints, 10_000, 1_000_000);
    }


    @ParameterizedTest
    @MethodSource("stringsSamples")
    void velvetSerializeString(String original) {
        velvetSerialize(original, 10_000_000, 1024);
    }

    @ParameterizedTest
    @MethodSource("stringsSamples")
    void kryoSerializeString(String original) {
        kryoSerialize(original, 10_000_000, 1024);
    }

    @Test
    void velvetDeserializeAll() {
        velvetDeserialize(VELVET_BYTES, original.getClass(), 10000);
    }

    @Test
    void kryoDeserializeAll() {
        kryoDeserialize(KRYO_BYTES, original.getClass(), 10000);
    }

    @Test
    void velvetSerializeAll() throws InterruptedException {
        velvetSerialize(original, 10_000, 20_000_000);
    }

    @Test
    void kryoSerializeAll() throws InterruptedException {
        kryoSerialize(original, 10_000, 20_000_000);
    }

    private static Stream<Arguments> stringsSamples() {
        return Stream.of(
                Arguments.of("Some ascii string!!!11100"),
                Arguments.of("空间站梦天实验舱发射任务取得圆满成功NASA公布太阳表面图像 \uD83D\uDC08")
        );
    }

    private <T> void velvetSerialize(T input, int repeats, int buffersize) {
        VelvetSerializer serializer = VelvetSerializerBuilder.create()
                .build();
        for (int i = 0; i < repeats; i++) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(buffersize);
            serializer.serialize(input, baos);
            byte[] bytes = baos.toByteArray();
            if (i == 0)
                System.err.println(bytes.length);
        }
    }

    private <T> void velvetDeserialize(byte[] bytes, Class<T> clazz, int repeats) {
        VelvetSerializer serializer = VelvetSerializerBuilder.create()
                .build();
        for (int i = 0; i < repeats; i++) {
            T restored = serializer.deserialize(bytes, clazz);
            restored.toString();
        }
    }

    private <T> void kryoSerialize(T input, int repeats, int buffersize) {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        for (int i = 0; i < repeats; i++) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(buffersize);
            Output output = new Output(baos);
            kryo.writeObject(output, input);
            output.flush();
            byte[] bytes = baos.toByteArray();
            if (i == 0)
                System.err.println(bytes.length);
        }
    }

    private <T> void kryoDeserialize(byte[] bytes, Class<T> clazz, int repeats) {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        for (int i = 0; i < repeats; i++) {
            Input inp = new Input(bytes);
            T restored = kryo.readObject(inp, clazz);
            restored.toString();
        }
    }
}
