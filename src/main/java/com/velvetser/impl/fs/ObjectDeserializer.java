package com.velvetser.impl.fs;

import com.velvetser.stream.BetterInputStream;

public interface ObjectDeserializer<F> {
    F deserialize(BetterInputStream stream);
}
