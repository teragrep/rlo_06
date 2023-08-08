package com.teragrep.rlo_06;

import java.util.function.Consumer;

final class SDElement implements Consumer<Stream> {

    private final ParserResultSet resultset;
    private final SDElementId sdElementId;
    private final SDParam sdParam;
    private final SDSkip sdSkip;

    SDElement(ParserResultSet resultset) {
        this.resultset = resultset;
        this.sdElementId = new SDElementId(resultset);
        this.sdParam = new SDParam(resultset);
        this.sdSkip = new SDSkip();
    }

    // structured data, oh wow the performance hit
    @Override
    public void accept(Stream stream) {
        byte b;

        // parse the sdId
        sdElementId.accept(stream);
        b = stream.get();

        if (b == 32) { // ' ', sdElement must exist
            if (resultset.sdSubscription.isSubscribedSDId(resultset.sdIdIterator)) {
                sdParam.accept(stream);
            }
            else {
                sdSkip.accept(stream);
            }
        }
        else if (b == 93) { // ']', sdId only here: Payload:'[ID_A@1]' or Payload:'[ID_A@1][ID_B@1]'


            // MSG may not exist, no \n either, Parsing may be complete. get sets this.returnAfter to false
            // Total payload: '<14>1 2015-06-20T09:14:07.12345+00:00 host02 serverd DEA MSG-01 [ID_A@1]'
        }
        else {
            throw new StructuredDataParseException("SP missing after SD_ID or SD_ID too long");
        }

        // clean up sdIterator for the next one
        resultset.sdIdIterator.flip();
        resultset.sdIdIterator.clear();
    }
}
