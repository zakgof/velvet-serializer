package com.velvetser.impl.fs;

import com.velvetser.stream.BetterInputStream;

public interface ShortDeserializer {
    short deserialize(BetterInputStream stream);
}
