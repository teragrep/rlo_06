package com.teragrep.rlo_06;

import java.io.IOException;
import java.io.InputStream;

final class Stream {

    private final InputStream inputStream;

    private final byte[] buffer = new byte[256 * 1024];
    private int pointer = -1;
    private int read = -1;

    private Boolean EOF = false;

    Stream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    byte readBuffer() throws IOException {
        byte b;

        if (pointer == read) {
            read = inputStream.read(buffer, 0, buffer.length);
            if (read == -1 && !this.EOF) {
                // EOF met
                this.EOF = true;
            }
            pointer = 0;
        }

        b = buffer[pointer++];

        return b;
    }

    boolean isEOF() {
        return EOF;
    }
}
