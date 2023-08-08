package com.teragrep.rlo_06;

import java.util.function.Consumer;

final class SDElementId implements Consumer<Stream> {

    private final ParserResultSet resultset;

    SDElementId(ParserResultSet resultset) {
        this.resultset = resultset;
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
            resultset.sdIdIterator.put(b);
            sdId_max_left--;

            if (!stream.next()) {
                throw new ParseException("SD is too short, can't continue");
            }
            b = stream.get();
        }
        resultset.sdIdIterator.flip(); // flip to READ so the compare works
    }
}
