package com.teragrep.rlo_06;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

final class Stream {

    private final InputStream inputStream;

    private final byte[] buffer = new byte[256 * 1024];
    private int pointer = -1;
    private int bytesInBuffer = -1;
    private byte b;

    Stream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    byte get() {
        // System.out.println("GETS B <"+ new String(new byte[]{b}, StandardCharsets.UTF_8)+">");
        return b;
    }

    boolean next() throws IOException {
        if (pointer == bytesInBuffer) {
            int read = inputStream.read(buffer, 0, buffer.length);
            if (read <= 0) { // EOF
                pointer = bytesInBuffer;
                // System.out.println("NEXT RV <false> pointer <"+pointer+"> bytesInBuffer <"+ bytesInBuffer +"> b <"+new String(new byte[]{b}, StandardCharsets.UTF_8)+">");
                return false;
            }

            bytesInBuffer = read;
            pointer = 0;
        }
        b = buffer[pointer++];
        // System.out.println("NEXT RV <true> pointer <"+pointer+"> bytesInBuffer <"+ bytesInBuffer +">  b <"+new String(new byte[]{b}, StandardCharsets.UTF_8)+">");
        return true;
    }
}
