package com.teragrep.rlo_06;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

public final class SDParam implements Consumer<Stream>, Clearable {
    private final SDParamKey sdParamKey;
    private final SDParamValue sdParamValue;

    SDParam() {
        this.sdParamKey = new SDParamKey();
        this.sdParamValue = new SDParamValue();
    }

    @Override
    public void accept(Stream stream) {
        byte b;
        // check if we are interested in this sdId at all or skip to next sdId block
        b = stream.get();

        while (b == 32) { // multiple ' ' separated sdKey="sdValue" pairs may exist
            sdParamKey.accept(stream);

            b = stream.get();
            if (b != 61) { // '='
                throw new StructuredDataParseException("EQ missing after SD_KEY or SD_KEY too long");
            }
             sdParamValue.accept(stream);

            // take next one for the while to check if ' ' or if to break it
            if (!stream.next()) {
                throw new ParseException("SD is too short, can't continue");
            }
            b = stream.get();
        }
    }

    @Override
    public void clear() {
        sdParamKey.clear();
        sdParamValue.clear();
    }

    public SDParamValue getSDParamValue(SDVector sdVector) {
        if (sdParamKey.key.equals(sdVector.sdKeyBB)) {
            return sdParamValue;
        }
        throw new NoSuchElementException();
    }
}
