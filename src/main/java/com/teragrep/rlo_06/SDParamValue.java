package com.teragrep.rlo_06;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public final class SDParamValue implements Consumer<Stream>, Clearable {
    private final ByteBuffer value;

    SDParamValue() {
        this.value = ByteBuffer.allocateDirect(8 * 1024);
    }

    @Override
    public void accept(Stream stream) {
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
    }

    @Override
    public void clear() {
        value.clear();
    }

    @Override
    public String toString() {
        value.flip();
        return StandardCharsets.UTF_8.decode(value).toString();
    }
}