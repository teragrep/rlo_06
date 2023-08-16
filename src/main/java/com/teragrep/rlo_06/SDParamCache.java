package com.teragrep.rlo_06;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Stack;

public class SDParamCache implements Cache<SDParam> {


    private final Stack<SDParam> cachedSDParams;
    final int numElements;
    
    SDParamCache(int numElements) {
        this.numElements = numElements;
        this.cachedSDParams = new Stack<>();
    }

    public SDParam take() {
        SDParam sdParam;

        if (cachedSDParams.isEmpty()) {
            sdParam = new SDParam();
        }
        else {
            sdParam = cachedSDParams.pop();
        }

        return sdParam;
    }

    public void put(SDParam sdParam) {
        if (cachedSDParams.size() < numElements) {
            sdParam.clear();
            cachedSDParams.push(sdParam);
        }
    }
}
