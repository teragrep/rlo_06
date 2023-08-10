package com.teragrep.rlo_06;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

public final class SDElement implements Consumer<Stream>, Clearable {

    public final SDElementId sdElementId;
    public final SDParam sdParam;

    SDElement() {
        this.sdElementId = new SDElementId();
        this.sdParam = new SDParam();
    }

    // structured data, oh wow the performance hit
    @Override
    public void accept(Stream stream) {
        byte b;

        // parse the sdId
        sdElementId.accept(stream);
        b = stream.get();

        if (b == 32) { // ' ', sdElement must exist
            sdParam.accept(stream);
        }
        else if (b == 93) { // ']', sdId only here: Payload:'[ID_A@1]' or Payload:'[ID_A@1][ID_B@1]'


            // MSG may not exist, no \n either, Parsing may be complete. get sets this.returnAfter to false
            // Total payload: '<14>1 2015-06-20T09:14:07.12345+00:00 host02 serverd DEA MSG-01 [ID_A@1]'
        }
        else {
            throw new StructuredDataParseException("SP missing after SD_ID or SD_ID too long");
        }
    }

    @Override
    public void clear() {
        sdElementId.clear();
        sdParam.clear();
    }

    public SDParamValue getSDParamValue(SDVector sdVector) {
        if (sdElementId.sdId.equals(sdVector.sdIdBB)) {
            return sdParam.getSDParamValue(sdVector);
        }
        throw new NoSuchElementException();
    }
}
