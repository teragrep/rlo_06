package com.teragrep.rlo_06;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public final class SDParamValue implements Consumer<Stream>, Clearable, Byteable {
    private final ByteBuffer value;

    private FragmentState fragmentState;

    SDParamValue() {
        this.value = ByteBuffer.allocateDirect(8 * 1024);
        this.fragmentState = FragmentState.EMPTY;
    }

    @Override
    public void accept(Stream stream) {
        if (fragmentState != FragmentState.EMPTY) {
            throw new IllegalStateException("fragmentState != FragmentState.EMPTY");
        }

        byte b;

        if (!stream.next()) {
            throw new ParseException("SD is too short, can't continue");
        }
        b = stream.get();
        if (b != 34) { // '"'
            throw new StructuredDataParseException("\" missing after SD_KEY EQ");
        }

        short sdElemVal_max_left = 8 * 1024;

        if (!stream.next()) {
            throw new ParseException("SD is too short, can't continue");
        }
        b = stream.get();

        while (sdElemVal_max_left > 0 && b != 34) { // '"'
            // escaped are special: \" \\ \] ...
            if (b == 92) { // \
                // insert
                value.put(b);
                sdElemVal_max_left--;
                // read next

                if (!stream.next()) {
                    throw new ParseException("SD is too short, can't continue");
                }
                b = stream.get();

                // if it is a '"' then it must be taken care of, loop can do the rest
                if (b == 34) {
                    if (sdElemVal_max_left > 0) {
                        value.put(b);
                        sdElemVal_max_left--;

                        if (!stream.next()) {
                            throw new ParseException("SD is too short, can't continue");
                        }
                        b = stream.get();
                    }
                }
            } else {
                value.put(b);
                sdElemVal_max_left--;

                if (!stream.next()) {
                    throw new ParseException("SD is too short, can't continue");
                }
                b = stream.get();
            }
        }
        value.flip();
        fragmentState = FragmentState.WRITTEN;
    }

    @Override
    public void clear() {
        value.clear();
        fragmentState = FragmentState.EMPTY;
    }

    @Override
    public String toString() {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }

        String string = StandardCharsets.UTF_8.decode(value).toString();
        value.rewind();
        return string;
    }

    @Override
    public byte[] toBytes() {
        final byte[] bytes = new byte[value.remaining()];
        value.get(bytes);
        value.rewind();
        return bytes;
    }
}
