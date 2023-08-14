package com.teragrep.rlo_06;


import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public final class StructuredData implements Consumer<Stream>, Clearable {
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
    public final List<SDElement> sdElements;
    private final SDElementCache sdElementCache;

    StructuredData() {
        int numElements = 16;
        this.sdElementCache = new SDElementCache(numElements);
        this.sdElements = new ArrayList<>(numElements);
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
            SDElement sdElement = sdElementCache.take();
            sdElement.accept(stream);
            sdElements.add(sdElement);
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


    @Override
    public void clear() {
        for (SDElement sdElement : sdElements) {
            // cache clears and deallocates
            sdElementCache.put(sdElement);
        }
        sdElements.clear();
    }

    public SDParamValue getValue(SDVector sdVector) {
        // reverse search as last value is only that matters
        ListIterator<SDElement> listIterator = sdElements.listIterator(sdElements.size());
        while(listIterator.hasPrevious()) {
            SDElement sdElement = listIterator.previous();
            try {
                return sdElement.getSDParamValue(sdVector);
            }
            catch (NoSuchElementException nsee) {
                continue;
            }
        }
        throw new NoSuchElementException(sdVector.toString());
    }

    @Override
    public String toString() {
        return "StructuredData{" +
                "sdElements=" + sdElements +
                '}';
    }
}
