package com.teragrep.rlo_06;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.function.Supplier;

final class Stream implements Supplier<Byte> {

    private final InputStream inputStream;

    private final byte[] buffer = new byte[256 * 1024];
    private int pointer = -1;
    private int bytesInBuffer = -1;
    private byte b;

    Stream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Byte get() {
        return b;
    }

    boolean next() {
        if (pointer == bytesInBuffer) {
            int read;
            try {
                read = inputStream.read(buffer, 0, buffer.length);
            } catch (IOException ioException) {
                throw new UncheckedIOException(ioException);
            }
            if (read <= 0) { // EOF
                pointer = bytesInBuffer;
                return false;
            }

            bytesInBuffer = read;
            pointer = 0;
        }
        b = buffer[pointer++];
        return true;
    }
}
