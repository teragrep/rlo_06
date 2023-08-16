package com.teragrep.rlo_06;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

public final class SDParam implements Consumer<Stream>, Clearable {
    public final SDParamKey sdParamKey;
    public final SDParamValue sdParamValue;

    private FragmentState fragmentState;

    SDParam() {
        this.sdParamKey = new SDParamKey();
        this.sdParamValue = new SDParamValue();
        this.fragmentState = FragmentState.EMPTY;
    }

    @Override
    public void accept(Stream stream) {
        if (fragmentState != FragmentState.EMPTY) {
            throw new IllegalStateException("fragmentState != FragmentState.EMPTY");
        }
        byte b;
        // check if we are interested in this sdId at all or skip to next sdId block
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
        fragmentState = FragmentState.WRITTEN;
    }

    @Override
    public void clear() {
        sdParamKey.clear();
        sdParamValue.clear();
        fragmentState = FragmentState.EMPTY;
    }

    public SDParamValue getSDParamValue(SDVector sdVector) {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }
        if (sdParamKey.matches(sdVector.sdParamKeyBB)) {
            return sdParamValue;
        }
        throw new NoSuchElementException(sdVector.toString());
    }

    @Override
    public String toString() {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }
        return "SDParam{" +
                "sdParamKey=" + sdParamKey +
                ", sdParamValue=" + sdParamValue +
                '}';
    }
}
