package com.teragrep.rlo_06;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

final class SDValue implements Consumer<Stream> {
    private final ParserResultSet resultset;

    SDValue(ParserResultSet resultset) {
        this.resultset = resultset;
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

        ByteBuffer elementValue = resultset.sdSubscription.getSubscribedSDElementBuffer(resultset.sdIdIterator, resultset.sdElementIterator);
        short sdElemVal_max_left = 8 * 1024;

        if (!stream.next()) {
            throw new ParseException("SD is too short, can't continue");
        }
        b = stream.get();

        while (sdElemVal_max_left > 0 && b != 34) { // '"'
            // escaped are special: \" \\ \] ...
            if (b == 92) { // \
                // insert
                elementValue.put(b);
                sdElemVal_max_left--;
                // read next

                if (!stream.next()) {
                    throw new ParseException("SD is too short, can't continue");
                }
                b = stream.get();

                // if it is a '"' then it must be taken care of, loop can do the rest
                if (b == 34) {
                    if (sdElemVal_max_left > 0) {
                        elementValue.put(b);
                        sdElemVal_max_left--;

                        if (!stream.next()) {
                            throw new ParseException("SD is too short, can't continue");
                        }
                        b = stream.get();
                    }
                }
            } else {
                elementValue.put(b);
                sdElemVal_max_left--;

                if (!stream.next()) {
                    throw new ParseException("SD is too short, can't continue");
                }
                b = stream.get();
            }
        }
    }
}
