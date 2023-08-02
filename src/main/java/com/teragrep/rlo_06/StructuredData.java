package com.teragrep.rlo_06;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

final class StructuredData implements Consumer<Stream> {
/*
                                                                                |||||||||||||||||||||||||||||||||||
                                                                                vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvR
                <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

                Actions: O^^^^^^O^OO_OO^OO_OOO^^^^^^O^OO_OO
                Payload:'[ID_A@1 u="3" e="t"][ID_B@2 n="9"]'
                States : |......%.%%.%%.%%.%%|......%.%%.%T

                NOTE this does not provide any proof what so ever if certain sdId exist or not, we are only interested
                in values if they exist.
                */

    /*
             v
    Payload:'[ID_A@1 u="3" e="t"][ID_B@2 n="9"] ' // sd exists
    Payload:'- ' // no sd
     */
    private final ParserResultSet resultset;
    StructuredData(ParserResultSet resultset) {
        this.resultset = resultset;
    }

    @Override
    public void accept(Stream stream) {
        byte b;


        if (!stream.next()) {
            throw new ParseException("Expected SD, received nothing");
        }
        b = stream.get();

        if (b != 45 && b != 91) { // '-' nor '['
            throw new StructuredDataParseException("SD does not contain '-' or '['");
        }

                /*
                 NOTE: here the SD parser may slip into the message, how dirty of it but
                 as "-xyz" or "- xyz" or "]xyz" or "] xyz" may exist we need to handle them here.
                 */

        if (b == 45) {
            // if '-' then R(ead) and pass to next state

            if (!stream.next()) {
                throw new ParseException("SD is too short, can't continue");
            }
            return;
        }

        while (b == 91) { // '[' sd exists
            // structured data, oh wow the performance hit

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

            if (b != 32 && b != 93) { // ' ' nor ']'
                throw new StructuredDataParseException("SP missing after SD_ID or SD_ID too long");
            } else if (b == 93) { // ']', sdId only here: Payload:'[ID_A@1]' or Payload:'[ID_A@1][ID_B@1]'
                // clean up sdIterator for the next one
                resultset.sdIdIterator.flip();
                resultset.sdIdIterator.clear();

                // MSG may not exist, no \n either, Parsing may be complete. get sets this.returnAfter to false
                // Total payload: '<14>1 2015-06-20T09:14:07.12345+00:00 host02 serverd DEA MSG-01 [ID_A@1]'
            } else { // ' ', sdElement must exist
                // check if we are interested in this sdId at all or skip to next sdId block

                if (resultset.sdSubscription.isSubscribedSDId(resultset.sdIdIterator)) {
                    while (b == 32) { // multiple ' ' separated sdKey="sdValue" pairs may exist
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

                        if (b != 61) { // '='
                            throw new StructuredDataParseException("EQ missing after SD_KEY or SD_KEY too long");
                        }

                        if (!stream.next()) {
                            throw new ParseException("SD is too short, can't continue");
                        }
                        b = stream.get();
                        if (b != 34) { // '"'
                            throw new StructuredDataParseException("\" missing after SD_KEY EQ");
                        }

                        // check if this is for us
                        if (resultset.sdSubscription.isSubscribedSDElement(resultset.sdIdIterator, resultset.sdElementIterator)) {
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
                        } else {
                            // skip through, no subscription for this sdElem

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
                else {
                    // TODO skip through to next block

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
                // clean up sdIterator for the next one
                resultset.sdIdIterator.flip();
                resultset.sdIdIterator.clear();
            }
                    /*
                                                    vv            vv
                        Payload:'[ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n'
                        Payload:            '[ID_A@1] sigsegv\n'
                        */

            if (!stream.next()) {
                throw new ParseException("SD is too short, can't continue");
            }
            b = stream.get(); // will it be '[' or the MSG who knows.
            // let's find out, note if not '[' then R(ead) and pass to next state
        }
    }
}
