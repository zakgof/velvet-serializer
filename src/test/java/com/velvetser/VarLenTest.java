package com.velvetser;

import com.velvetser.stream.BetterInputStream;
import com.velvetser.stream.BetterOutputStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VarLenTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 63, 64, 127, -127, 128, -128, 8191, 8192,
            1048575, 1048576, 134217727, 134217728,
            -1, -2, -63, -64, -65, -8191, -8192, -8193,
            -1048576, -1048577, -134217728, -134217729,
            Integer.MAX_VALUE - 1, Integer.MAX_VALUE,
            Integer.MIN_VALUE + 1, Integer.MIN_VALUE})
    void varIntRoundtrip(int i) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BetterOutputStream bos = new BetterOutputStream(baos);
        bos.writeVarInt(i);
        bos.flush();
        byte[] bytes = baos.toByteArray();
        BetterInputStream bis = new BetterInputStream(new ByteArrayInputStream(bytes));
        int actual = bis.readVarInt();
        System.out.println("" + i + " -> " + bytesToHex(bytes) + " -> " + actual);
        assertEquals(i, actual);
    }

    @ParameterizedTest
    @MethodSource("longValues")
    void varLongRoundtrip(long l) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BetterOutputStream bos = new BetterOutputStream(baos);
        bos.writeVarLong(l);
        bos.flush();
        byte[] bytes = baos.toByteArray();
        BetterInputStream bis = new BetterInputStream(new ByteArrayInputStream(bytes));
        long actual = bis.readVarLong();
        System.out.println("" + l + " -> " + bytesToHex(bytes) + " -> " + actual);
        assertEquals(l, actual);
    }

    public static Stream<Arguments> longValues() {
        return LongStream.concat(
                        LongStream.of(Long.MAX_VALUE, Long.MAX_VALUE - 1, Long.MIN_VALUE, Long.MIN_VALUE + 1),
                        LongStream.range(0, 63)
                                .map(s -> 1L << s)
                                .flatMap(v -> LongStream.of(v, -v, v - 1, -v - 1)))
                .sorted()
                .mapToObj(Arguments::of);
    }

    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
}
