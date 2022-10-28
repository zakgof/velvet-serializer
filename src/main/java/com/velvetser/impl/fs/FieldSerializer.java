package com.velvetser.impl.fs;

import com.velvetser.stream.BetterOutputStream;

public interface FieldSerializer<F> {
    void serialize(F fieldValue, BetterOutputStream stream);
}
