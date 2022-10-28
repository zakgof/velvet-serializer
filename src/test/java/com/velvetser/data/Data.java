package com.velvetser.data;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@lombok.Data
public class Data {
    private final byte primitiveByte;
    private final short primitiveShort;
    private final InnerFinal innerFinal;
    private final InnerFinal innerFinalNull;

    private final InnerInterface innerInterfaceNull;

    private final InnerInterface innerInterface;
    private final InnerInterface innerInterface2;

    private final InnerInterface[] objArrayNull;

    private final InnerInterface[] objArray;

    // private final LocalDateTime localDateTime;

//    private final char primitiveChar;
//    private final int primitiveInt;
//    private final long primitiveLong;
//    private final boolean primitiveBoolean;

//    private final Byte objectByte;
//    private final Short objectShort;
//    private final Character objectCharacter;
//    private final Integer objectInteger;
//    private final Long objectLong;
//    private final Boolean objectBoolean;

    private final String str;

    public Data(long seed) {
        Random random = new Random(seed);
        this.primitiveByte = (byte)random.nextInt();
        this.primitiveShort = (short) random.nextInt();

        this.innerFinal = new InnerFinal(seed+1);
        this.innerFinalNull = null;
        this.innerInterface = new InnerFinal(seed+2);
        this.innerInterface2 = new PolyOther(seed+3);
        this.innerInterfaceNull = null;
        this.objArrayNull = null;
        this.objArray = new InnerInterface[] {new InnerFinal(seed+4), null, new PolyOther(seed+5)};
        // this.localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(random.nextLong()), ZoneId.systemDefault());

        this.str = IntStream.range(0, 10)
                .map(i -> (int)'A' + random.nextInt(26))
                .mapToObj(c -> Character.toString((char)c))
                .collect(Collectors.joining());
    }
}
