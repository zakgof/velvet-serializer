package com.velvetser.stream;

public interface VelvetInput {
    byte readByte();

    void readBytes(byte[] buffer, int length);
}
