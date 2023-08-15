package com.teragrep.rlo_06;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public final class SDParamKey implements Consumer<Stream>, Clearable, Matchable {
    private final ByteBuffer key;

    SDParamKey() {
        this.key = ByteBuffer.allocateDirect(32);
    }

    @Override
    public void accept(Stream stream) {
        byte b;

            short sdElemKey_max_left = 32;

            if (!stream.next()) {
                throw new ParseException("SD is too short, can't continue");
            }
            b = stream.get();
            while (sdElemKey_max_left > 0 && b != 61) { // '='
                key.put(b);
                sdElemKey_max_left--;

                if (!stream.next()) {
                    throw new ParseException("SD is too short, can't continue");
                }
                b = stream.get();
            }
        key.flip(); // for reads
    }

    @Override
    public void clear() {
        key.clear();
    }

    @Override
    public String toString() {
        String string = StandardCharsets.UTF_8.decode(key).toString();
        key.flip();
        return string;
    }

    @Override
    public boolean matches(ByteBuffer buffer) {
        return key.equals(buffer);
    }
}
