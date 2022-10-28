package com.velvetser.stream;

import com.velvetser.VelvetSerializerException;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class BetterInputStream {

    private final InputStream stream;
    private final byte[] buffer = new byte[8];

    public byte readByte() {
        bufferRead(1);
        return buffer[0];
    }

    public short readShort() {
        bufferRead(2);
        return (short) ((buffer[1] & 0xFF) << 8 | (buffer[0] & 0xFF));
    }

    private void bufferRead(int len) {
        safeRead(() -> {
            if (len != stream.readNBytes(buffer, 0, len)) {
                throw new VelvetSerializerException("End of read stream while deserializing");
            }
        });
    }

    public int readVarInt() {
        byte b = readByte();
        boolean negative = (b & 0x40) != 0;
        int result = b & 0x3F;
        for (int shift = 6; shift <= 27 && (b & 0x80) != 0; shift += 7) {
            b = readByte();
            result |= ((b & 0x7F) << shift);
        }
        return negative ? -(result + 1) : result;
    }

    public long readVarLong() {
        byte b = readByte();
        boolean negative = (b & 0x40) != 0;
        long result = b & 0x3F;
        for (int shift = 6; shift <= 55 && (b & 0x80) != 0; shift += 7) {
            b = readByte();
            result |= ((long) (b & (shift == 55 ? 0xFF : 0x7F)) << shift);
        }
        return negative ? -(result + 1) : result;
    }

    public String readString() {
        int control = readVarInt();
        if (control == -1) {
            return null;
        }
        if (control == 0) {
            return "";
        }
        byte[] dest;
        if (control <= buffer.length)
            dest = buffer;
        else
            dest = new byte[control];
        safeReadBytes(dest, control);
        return new String(dest, 0, control, StandardCharsets.UTF_8);
    }

    private void safeReadBytes(byte[] dest, int length) {
        safeRead(() -> {
            if (length != stream.readNBytes(dest, 0, length)) {
                throw new VelvetSerializerException("End of read stream while deserializing");
            }
        });
    }

    interface ReadRunnable {
        void run() throws IOException;
    }

    private void safeRead(ReadRunnable runnable) {
        try {
            runnable.run();
        } catch (IOException e) {
            throw new VelvetSerializerException("Read error", e);
        }
    }
}
