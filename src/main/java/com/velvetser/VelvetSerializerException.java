package com.velvetser;

public class VelvetSerializerException extends RuntimeException {
    public VelvetSerializerException(Throwable e) {
        super(e);
    }

    public VelvetSerializerException(String message) {
        super(message);
    }

    public VelvetSerializerException(String message, Throwable e) {
        super(message, e);
    }
}
