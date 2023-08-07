package com.teragrep.rlo_06;

import java.util.function.Consumer;

final class SDSkip implements Consumer<Stream> {

    @Override
    public void accept(Stream stream) {
        byte b;

        // skip through to next block

        if (!stream.next()) {
            throw new ParseException("SD is too short, can't continue");
        }
        b = stream.get();
        while (b != 93) { // ']'
            // escaped '\]' are special:
            if (b == 92) { // \
                // read next

                if (!stream.next()) {
                    throw new ParseException("SD is too short, can't continue");
                }
                b = stream.get();
                // if it is a ']' then it must be taken care of, loop can do the rest
                if (b == 93) {

                    if (!stream.next()) {
                        throw new ParseException("SD is too short, can't continue");
                    }
                    b = stream.get();
                }
            } else {

                if (!stream.next()) {
                    throw new ParseException("SD is too short, can't continue");
                }
                b = stream.get();
            }
        }
    }
}
