package com.velvetser.stream;

import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class BetterInputStream {

    private final VelvetInput velvetInput;
    private final byte[] buffer = new byte[4096];

    public byte readByte() {
        return velvetInput.readByte();
    }

    public short readShort() {
        bufferRead(2);
        return (short) ((buffer[1] & 0xFF) << 8 | (buffer[0] & 0xFF));
    }

    public char readChar() {
        bufferRead(2);
        return (char) ((buffer[1] & 0xFF) << 8 | (buffer[0] & 0xFF));
    }

    private void bufferRead(int len) {
        velvetInput.readBytes(buffer, len);
    }

    public int readVarInt() {
        byte b = velvetInput.readByte();
        boolean negative = (b & 0x40) != 0;
        int result = b & 0x3F;
        for (int shift = 6; shift <= 27 && (b & 0x80) != 0; shift += 7) {
            b = velvetInput.readByte();
            result |= ((b & 0x7F) << shift);
        }
        return negative ? -(result + 1) : result;
    }

    public long readVarLong() {
        byte b = velvetInput.readByte();
        boolean negative = (b & 0x40) != 0;
        long result = b & 0x3F;
        for (int shift = 6; shift <= 55 && (b & 0x80) != 0; shift += 7) {
            b = velvetInput.readByte();
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
        velvetInput.readBytes(dest, control);
        return new String(dest, 0, control, StandardCharsets.UTF_8);
    }

    public void readBytes(byte[] value, int length) {
        velvetInput.readBytes(value, length);
    }
}
