package com.velvetser.stream;

import com.velvetser.VelvetSerializerException;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

public class VelvetInputs {

    public static VelvetInput from(InputStream inputStream) {
        return new VelvetInputStream(inputStream);
    }

    public static VelvetInput from(byte[] byteArray) {
        return new VelvetInputByteArray(byteArray);
    }

    @RequiredArgsConstructor
    private static class VelvetInputByteArray implements VelvetInput {

        private final byte[] byteArray;
        private int position;

        @Override
        public byte readByte() {
            if (position == byteArray.length) {
                throw new VelvetSerializerException("End of data while reading");
            }
            return byteArray[position++];
        }

        @Override
        public void readBytes(byte[] buffer, int length) {
            if (position + length > byteArray.length) {
                throw new VelvetSerializerException("End of data while reading");
            }
            System.arraycopy(byteArray, position, buffer, 0, length);
            position += length;
        }
    }

    @RequiredArgsConstructor
    private static class VelvetInputStream implements VelvetInput {

        private final InputStream inputStream;

        @Override
        public byte readByte() {
            try {
                return (byte) inputStream.read();
            } catch (IOException e) {
                throw new VelvetSerializerException("Read error", e);
            }
        }

        @Override
        public void readBytes(byte[] dest, int length) {
            try {
                for (int i = 0; i < length; i++)
                    dest[i] = (byte) inputStream.read();
            } catch (IOException e) {
                throw new VelvetSerializerException("Read error", e);
            }
        }
    }
}
