package com.teragrep.rlo_06;

import java.util.function.Consumer;

final class SDKeyValuePair implements Consumer<Stream> {
    private final ParserResultSet resultset;
    private final SDValueSkip sdValueSkip;
    private final SDKey sdKey;
    private final SDValue sdValue;

    SDKeyValuePair(ParserResultSet resultset) {
        this.resultset = resultset;
        this.sdValueSkip = new SDValueSkip();
        this.sdKey = new SDKey(resultset);
        this.sdValue = new SDValue(resultset);
    }

    @Override
    public void accept(Stream stream) {
        byte b;
        // check if we are interested in this sdId at all or skip to next sdId block
        b = stream.get();

        while (b == 32) { // multiple ' ' separated sdKey="sdValue" pairs may exist
            sdKey.accept(stream);

            b = stream.get();
            if (b != 61) { // '='
                throw new StructuredDataParseException("EQ missing after SD_KEY or SD_KEY too long");
            }

            // check if this is for us
            if (resultset.sdSubscription.isSubscribedSDElement(resultset.sdIdIterator, resultset.sdElementIterator)) {
                sdValue.accept(stream);
            } else {
                sdValueSkip.accept(stream);
            }

            // clean up sdElementIterator for the next one
            resultset.sdElementIterator.flip();
            resultset.sdElementIterator.clear();

            // take next one for the while to check if ' ' or if to break it

            if (!stream.next()) {
                throw new ParseException("SD is too short, can't continue");
            }
            b = stream.get();
        }

    }
}
