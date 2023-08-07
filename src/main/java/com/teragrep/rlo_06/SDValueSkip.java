package com.teragrep.rlo_06;

import java.util.function.Consumer;

final class SDValueSkip implements Consumer<Stream> {

    @Override
    public void accept(Stream stream) {
        byte b;
        // skip through, no subscription for this sdElem

        if (!stream.next()) {
            throw new ParseException("SD is too short, can't continue");
        }
        b = stream.get();
        if (b != 34) { // '"'
            throw new StructuredDataParseException("\" missing after SD_KEY EQ");
        }

        if (!stream.next()) {
            throw new ParseException("SD is too short, can't continue");
        }
        b = stream.get();
        while (b != 34) { // '"'
            // escaped are special: \" \\ \] ...
            if (b == 92) { // \
                // read next

                if (!stream.next()) {
                    throw new ParseException("SD is too short, can't continue");
                }
                b = stream.get();
                // if it is a '"' then it must be taken care of, loop can do the rest
                if (b == 34) {

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
