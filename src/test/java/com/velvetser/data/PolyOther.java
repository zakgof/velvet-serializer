package com.velvetser.data;

import java.util.Random;

@lombok.Data
public final class PolyOther implements InnerInterface {

    private final String str;

    public PolyOther(long seed) {
        Random random = new Random(seed);
        this.str = "PO" + random.nextInt(1000);
     }
}
