package com.velvetser.data;

import java.util.Random;

@lombok.Data
public final class InnerFinal implements InnerInterface {
    private final short innerShort;
    private final String innerStr;

    public InnerFinal(long seed) {
        Random random = new Random(seed);
        this.innerShort = (short)random.nextInt();
        this.innerStr = "I" + random.nextInt(1000);
     }
}
