package com.teragrep.rlo_06;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;

/**
 * Cache object to contain multiple pre-allocated SDElements for use to avoid memory allocations for new ones
 */
final class SDElementCache implements Cache<SDElement> {

    private final Stack<SDElement> cachedSDElements;

    final int numElements;

    SDElementCache(int numElements) {
        this.numElements = numElements;
        cachedSDElements = new Stack<>();
    }


    public SDElement take() {
        SDElement sdElement;

        if (cachedSDElements.isEmpty()) {
            sdElement = new SDElement();
        }
        else {
            sdElement = cachedSDElements.pop();
        }

        return sdElement;
    }

    public void put(SDElement sdElement) {
        if (cachedSDElements.size() < numElements) {
            sdElement.clear();
            cachedSDElements.push(sdElement);
        }
    }
}
