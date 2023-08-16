package com.teragrep.rlo_06;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public final class SDParamKey implements Consumer<Stream>, Clearable, Matchable {
    private final ByteBuffer key;

    private FragmentState fragmentState;

    SDParamKey() {
        this.key = ByteBuffer.allocateDirect(32);
        this.fragmentState = FragmentState.EMPTY;
    }

    @Override
    public void accept(Stream stream) {
        if (fragmentState != FragmentState.EMPTY) {
            throw new IllegalStateException("fragmentState != FragmentState.EMPTY");
        }
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
            key.flip();
        fragmentState = FragmentState.WRITTEN;

    }

    @Override
    public void clear() {
        key.clear();
        fragmentState = FragmentState.EMPTY;
    }

    @Override
    public String toString() {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }
        String string = StandardCharsets.UTF_8.decode(key).toString();
        key.rewind();
        return string;
    }

    @Override
    public boolean matches(ByteBuffer buffer) {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }
        return key.equals(buffer);
    }
}
