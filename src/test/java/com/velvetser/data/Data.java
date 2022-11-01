package com.velvetser.data;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@lombok.Data
public class Data {
    private final byte primitiveByte;
    private final short primitiveShort;
    private final int primitiveInt;
    private final long primitiveLong;
    private final char primitiveChar;
    private final boolean primitiveBool;
    private final InnerFinal innerFinal;
    private final InnerFinal innerFinalNull;

    private final InnerInterface innerInterfaceNull;

    private final InnerInterface innerInterface;
    private final InnerInterface innerInterface2;

    private final InnerInterface[] objArrayNull;
    private final InnerInterface[] objArray;

    private final byte[] byteArrayNull;
    private final byte[] byteArray;
    private final short[] shortArrayNull;
    private final short[] shortArray;
    private final int[] intArrayNull;
    private final int[] intArray;
    private final long[] longArrayNull;
    private final long[] longArray;
    private final boolean[] booleanArrayNull;
    private final boolean[] booleanArray;
    private final char[] charArrayNull;
    private final char[] charArray;
    private final String str;

    // private final LocalDateTime localDateTime;
//    private final Byte objectByte;
//    private final Short objectShort;
//    private final Character objectCharacter;
//    private final Integer objectInteger;
//    private final Long objectLong;
//    private final Boolean objectBoolean;


    public Data(long seed) {
        Random random = new Random(seed);
        this.primitiveByte = (byte)random.nextInt();
        this.primitiveShort = (short) random.nextInt();
        this.primitiveInt = random.nextInt();
        this.primitiveLong = (short) random.nextLong();
        this.primitiveBool = random.nextInt(2) == 0;
        this.primitiveChar = (char)(random.nextInt(Character.MAX_VALUE + 1));

        this.innerFinal = new InnerFinal(seed+1);
        this.innerFinalNull = null;
        this.innerInterface = new InnerFinal(seed+2);
        this.innerInterface2 = new PolyOther(seed+3);
        this.innerInterfaceNull = null;
        this.objArrayNull = null;
        this.objArray = new InnerInterface[] {new InnerFinal(seed+4), null, new PolyOther(seed+5)};
        // this.localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(random.nextLong()), ZoneId.systemDefault());
        this.byteArrayNull = null;
        this.byteArray = new byte[] {0, 1, -1, 127, -128};
        this.shortArrayNull = null;
        this.shortArray = new short[] {0, 1, -1, Short.MAX_VALUE, Short.MIN_VALUE};
        this.intArrayNull = null;
        this.intArray = new int[] {0, 1, -1, Integer.MAX_VALUE, Integer.MIN_VALUE};
        this.longArrayNull = null;
        this.longArray = new long[] {0, 1, -1, Long.MAX_VALUE, Long.MIN_VALUE};
        this.charArrayNull = null;
        this.charArray = new char[] {0, 1, 2, Character.MAX_VALUE, Character.MIN_VALUE};
        this.booleanArrayNull = null;
        this.booleanArray = new boolean[] {true, false};
        this.str = IntStream.range(0, 10)
                .map(i -> (int)'A' + random.nextInt(26))
                .mapToObj(c -> Character.toString((char)c))
                .collect(Collectors.joining());

    }
}

