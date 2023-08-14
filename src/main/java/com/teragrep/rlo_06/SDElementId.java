package com.teragrep.rlo_06;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public final class SDElementId implements Consumer<Stream>, Clearable, Matchable {

    private final ByteBuffer sdId;


    SDElementId() {
        this.sdId = ByteBuffer.allocateDirect(32);
    }

    @Override
    public void accept(Stream stream) {
        byte b;

        // parse the sdId
        short sdId_max_left = 32;
                    /*
                              vvvvvv
                    Payload:'[ID_A@1 u="3" e="t"][ID_B@2 n="9"] '
                    Payload:'[ID_A@1]'
                    */

        if (!stream.next()) {
            throw new ParseException("SD is too short, can't continue");
        }
        b = stream.get();
        while (sdId_max_left > 0 && b != 32 && b != 93) { // ' ' nor ']'
            sdId.put(b);
            sdId_max_left--;

            if (!stream.next()) {
                throw new ParseException("SD is too short, can't continue");
            }
            b = stream.get();
        }
    }

    @Override
    public void clear() {
        sdId.clear();
    }

    @Override
    public String toString() {
        sdId.flip();
        return StandardCharsets.UTF_8.decode(sdId).toString();
    }

    @Override
    public boolean matches(ByteBuffer buffer) {
        sdId.flip();
        return sdId.equals(buffer);
    }
}
