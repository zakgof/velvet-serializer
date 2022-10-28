package com.velvetser.impl.fs;

import com.velvetser.stream.BetterOutputStream;

public interface ShortSerializer {
    void serialize(short fieldValue, BetterOutputStream stream);
}
