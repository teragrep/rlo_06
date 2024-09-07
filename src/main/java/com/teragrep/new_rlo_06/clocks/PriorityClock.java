package com.teragrep.new_rlo_06.clocks;

import com.teragrep.new_rlo_06.*;

import java.nio.ByteBuffer;
import java.util.LinkedList;

public class PriorityClock {

    private final LinkedList<ByteBuffer> bufferSliceList;
    private static final int maximumPriorityLength = 3;
    private static final _PriorityStub priorityStub = new _PriorityStub();
    private boolean hasStart;

    public PriorityClock() {
        this.bufferSliceList = new LinkedList<>();
        this.hasStart = false;
    }

    public _Priority submit(ByteBuffer input) {

        int bytesIncluded = 0;
        boolean isComplete = false;


        // copy buffer
        ByteBuffer slice = input.slice();

        // consume input
        while (input.hasRemaining()) {
            byte b = input.get();

            if (b == '<') {
                if (hasStart) {
                    throw new PriorityParseException("priority must not contain '<' in the content");
                }
                // remove '<' from the content
                slice.position(input.position());
                slice = slice.slice();
                hasStart = true;
                continue;
            }

            if (!hasStart) {
                throw new PriorityParseException("priority must start with a '<'");
            }

            if (b == '>') {
                // mask '>' because it is not part of priority
                slice.limit(slice.limit() - 1);
                if (slice.limit() == 0 && bufferSliceList.isEmpty()) {
                    throw new PriorityParseException("priority must have content");
                }
                isComplete = true;
                break;
            }

            // numbers only
            if (b < '0' || b > '9') {
                throw new PriorityParseException("priority must contain numbers only");
            }

            bytesIncluded++;
            checkOverSize(bytesIncluded);

        }

        // ignore empty slices
        if (slice.capacity() != 0) {
            bufferSliceList.add(slice);
        }

        _Priority priority;
        if (isComplete) {
            Element element = new ElementImpl(bufferSliceList);
            priority = new _PriorityImpl(element);
        } else {
            priority = priorityStub;
        }

        return priority;
    }

    private void checkOverSize(int bytesRead) {
        long currentLength = 0;
        for (ByteBuffer slice : bufferSliceList) {
            currentLength = currentLength + slice.limit();
        }

        currentLength = currentLength + bytesRead;
        if (currentLength > maximumPriorityLength) {
            throw new PriorityParseException("priority too long");
        }
    }
}
