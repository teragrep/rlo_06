package com.teragrep.rlo_06;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;

public class SDParamCache implements Cache<SDParam> {


    private final Queue<SDParam> cachedSDParams;
    final int numElements;
    
    SDParamCache(int numElements) {
        this.numElements = numElements;
        this.cachedSDParams = new ArrayDeque<>(numElements);
    }

    public SDParam take() {
        SDParam sdParam;
        try {
            sdParam = cachedSDParams.remove();
        }
        catch (NoSuchElementException nsee) {
            sdParam = new SDParam();
        }
        return sdParam;
    }

    public void put(SDParam sdParam) {
        sdParam.clear();
        if (cachedSDParams.size() < numElements) {
            cachedSDParams.add(sdParam);
        }
    }
}
