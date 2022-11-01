package com.velvetser.stream;

import com.velvetser.VelvetSerializerException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BetterOutputStream {

    private final OutputStream stream;
    private final byte[] buffer = new byte[4096];
    private final byte[] small = new byte[9];
    private int cursor = 0;

    public BetterOutputStream(OutputStream stream) {
        this.stream = stream;
    }

    public void require(int length) {
        if (buffer.length - cursor < length) {
            flush();
        }
    }

    public boolean checkRequire(int length) {
        if (buffer.length - cursor < length) {
            flush();
        }
        return length <= buffer.length;
    }

    public void flush() {
        try {
            stream.write(buffer, 0, cursor);
            cursor = 0;
        } catch (IOException e) {
            throw new VelvetSerializerException("Write error", e);
        }
    }

    public void writeByte(byte value) {
        require(1);
        buffer[cursor] = value;
        cursor++;
    }

    public void writeShort(short value) {
        require(2);
        buffer[cursor++] = (byte) value;
        buffer[cursor++] = (byte) (value >> 8);
    }

    public void writeChar(char value) {
        require(2);
        buffer[cursor++] = (byte) value;
        buffer[cursor++] = (byte) (value >> 8);
    }

    public void writeString(String value, boolean canBeNull) {
        if (canBeNull && value == null) {
            writeVarInt(-1);
        } else {
            byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            writeVarInt(bytes.length);
            writeBytes(bytes);
        }
    }

    public void writeVarInt(int value) {
        require(5);
        int sign = (value >>> 25) & 0x40;
        int working = sign == 0 ? value : -value - 1;
        buffer[cursor] = (byte) ((working & 0x3F) | sign);
        int len = 1;
        for (; len < 5; len++) {
            int shift = len * 7 - 1;
            if (working <= 1 << shift - 1) {
                break;
            }
            buffer[cursor + len - 1] |= 0x80;
            buffer[cursor + len] = (byte) ((working >>> shift) & 0x7F);
        }
        cursor += len;
    }

    public void writeVarLong(long value) {
        require(9);
        long sign = (value >>> 57) & 0x40;
        long working = sign == 0 ? value : -value - 1;
        buffer[cursor] = (byte) ((working & 0x3F) | sign);
        int len = 1;
        for (; len < 9; len++) {
            int shift = len * 7 - 1;
            if (working <= 1L << shift - 1) {
                break;
            }
            buffer[cursor + len - 1] |= 0x80;
            buffer[cursor + len] = (byte) ((working >>> shift) & (len == 8 ? 0xFF : 0x7F));
        }
        cursor += len;
    }

    public void writeBytes(byte[] bytes) {
        writeBytes(bytes, bytes.length);
    }

    private void writeSafeBytes(byte[] bytes, int len) {
        System.arraycopy(bytes, 0, buffer, cursor, len);
        cursor += len;
    }

    public void writeBytes(byte[] bytes, int len) {
        if (checkRequire(len)) {
            writeSafeBytes(bytes, len);
        } else {
            flush();
            try {
                stream.write(bytes, 0, len);
            } catch (IOException e) {
                throw new VelvetSerializerException("Write error", e);
            }
        }
    }

    public void writeVarInts(int[] fieldValue) {
        for (int element : fieldValue) {
            writeVarInt(element);
        }
    }
}