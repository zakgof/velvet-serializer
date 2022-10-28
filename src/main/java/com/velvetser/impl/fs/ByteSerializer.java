package com.velvetser.impl.fs;

import com.velvetser.stream.BetterOutputStream;

public interface ByteSerializer {
    void serialize(byte fieldValue, BetterOutputStream stream);
}
