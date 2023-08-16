package com.teragrep.rlo_06;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public final class SDElement implements Consumer<Stream>, Clearable {

    public final SDElementId sdElementId;
    public final List<SDParam> sdParams;

    private final SDParamCache sdParamCache;

    private FragmentState fragmentState;

    SDElement() {
        int numElements = 16;
        this.sdElementId = new SDElementId();
        this.sdParams = new ArrayList<>(numElements);
        this.sdParamCache = new SDParamCache(numElements);
        this.fragmentState = FragmentState.EMPTY;
    }
    // structured data, oh wow the performance hit
    @Override
    public void accept(Stream stream) {
        if (fragmentState != FragmentState.EMPTY) {
            throw new IllegalStateException("fragmentState != FragmentState.EMPTY");
        }

        byte b;

        // parse the sdId
        sdElementId.accept(stream);
        b = stream.get();

        while (b == 32) { // multiple ' ' separated sdKey="sdValue" pairs may exist
            SDParam sdParam = sdParamCache.take();
            sdParam.accept(stream);
            sdParams.add(sdParam);
            b = stream.get();
        }

        if (b == 93) { // ']', sdId only here: Payload:'[ID_A@1]' or Payload:'[ID_A@1][ID_B@1]'
            // MSG may not exist, no \n either, Parsing may be complete. get sets this.returnAfter to false
            // Total payload: '<14>1 2015-06-20T09:14:07.12345+00:00 host02 serverd DEA MSG-01 [ID_A@1]'
        }
        else {
            throw new StructuredDataParseException("SP missing after SD_ID or SD_ID too long");
        }
        fragmentState = FragmentState.WRITTEN;
    }

    @Override
    public void clear() {
        sdElementId.clear();
        for (SDParam sdParam : sdParams) {
            // cache clears
            sdParamCache.put(sdParam);
        }
        sdParams.clear();
        fragmentState = FragmentState.EMPTY;
    }

    public SDParamValue getSDParamValue(SDVector sdVector) {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }
        if (sdElementId.matches(sdVector.sdElementIdBB)) {
            ListIterator<SDParam> listIterator = sdParams.listIterator(sdParams.size());
            while (listIterator.hasPrevious()) {
                SDParam sdParam = listIterator.previous();
                try {
                    return sdParam.getSDParamValue(sdVector);
                } catch (NoSuchElementException nsee) {
                    continue;
                }
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public String toString() {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }
        return "SDElement{" +
                "sdElementId=" + sdElementId +
                ", sdParams=" + sdParams +
                '}';
    }
}
