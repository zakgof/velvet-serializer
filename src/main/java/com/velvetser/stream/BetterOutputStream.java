package com.velvetser.stream;

import com.velvetser.VelvetSerializerException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BetterOutputStream {

    private final OutputStream stream;
    private final byte[] buffer = new byte[16];

    public BetterOutputStream(OutputStream stream) {
        this.stream = stream;
    }

    public void writeByte(byte value) {
        buffer[0] = value;
        bufferWrite(1);
    }

    public void writeShort(short value) {
        buffer[0] = (byte) value;
        buffer[1] = (byte) (value >> 8);
        bufferWrite(2);
    }

    private void bufferWrite(int len) {
        safeWrite(() -> stream.write(buffer, 0, len));
    }


    public void writeString(String value, boolean canBeNull) {
        if (canBeNull && value == null) {
            writeVarInt(-1);
        } else {
            byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            writeVarInt(bytes.length);
            safeWrite(() -> stream.write(bytes));
        }
    }

    public void writeVarInt(int value) {
        int sign = (value >>> 25) & 0x40;
        int working = sign == 0 ? value : -value - 1;
        buffer[0] = (byte) ((working & 0x3F) | sign);
        int len = 1;
        for (; len < 5; len++) {
            int shift = len * 7 - 1;
            if (working <= 1 << shift - 1) {
                bufferWrite(len);
                return;
            }
            buffer[len - 1] |= 0x80;
            buffer[len] = (byte) ((working >>> shift) & 0x7F);
        }
        bufferWrite(len);
    }

    public void writeVarLong(long value) {
        long sign = (value >>> 57) & 0x40;
        long working = sign == 0 ? value : -value - 1;
        buffer[0] = (byte) ((working & 0x3F) | sign);
        int len = 1;
        for (; len < 9; len++) {
            int shift = len * 7 - 1;
            if (working <= 1L << shift - 1) {
                bufferWrite(len);
                return;
            }
            buffer[len - 1] |= 0x80;
            buffer[len] = (byte) ((working >>> shift) & (len == 8 ? 0xFF : 0x7F));
        }
        bufferWrite(len);
    }

    interface WriteRunnable {
        void run() throws IOException;
    }

    private void safeWrite(WriteRunnable runnable) {
        try {
            runnable.run();
        } catch (IOException e) {
            throw new VelvetSerializerException("Write error", e);
        }
    }
}
