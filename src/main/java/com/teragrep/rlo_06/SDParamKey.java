package com.teragrep.rlo_06;

import java.util.function.Consumer;

final class SDParamKey implements Consumer<Stream> {
    private final ParserResultSet resultset;

    SDParamKey(ParserResultSet resultset) {
        this.resultset = resultset;
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
                resultset.sdElementIterator.put(b);
                sdElemKey_max_left--;

                if (!stream.next()) {
                    throw new ParseException("SD is too short, can't continue");
                }
                b = stream.get();
            }
            resultset.sdElementIterator.flip(); // flip to READ so the compare works
    }
}
