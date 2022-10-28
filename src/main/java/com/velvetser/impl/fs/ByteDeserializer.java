package com.velvetser.impl.fs;

import com.velvetser.stream.BetterInputStream;

public interface ByteDeserializer {
    byte deserialize(BetterInputStream stream);
}
