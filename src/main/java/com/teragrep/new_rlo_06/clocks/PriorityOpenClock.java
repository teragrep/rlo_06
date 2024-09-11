package com.teragrep.new_rlo_06.clocks;

import com.teragrep.new_rlo_06.PriorityParseException;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class PriorityOpenClock implements Clock<List<ByteBuffer>> {

    private boolean isComplete;
    private final List<ByteBuffer> buffers;

    public PriorityOpenClock() {
        this.isComplete = false;
        this.buffers = new LinkedList<>();
    }

    @Override
    public ByteBuffer apply(ByteBuffer input) {
        if (!isComplete) {
            ByteBuffer slice = input.slice();

            if (input.hasRemaining()) {
                byte b = input.get();
                if (b == '<') {
                    slice.limit(1);
                    isComplete = true;
                }
                else {
                    throw new PriorityParseException("priority must start with a '<'");
                }
            }

            // ignore empty slices
            if (slice.limit() > 0) {
                buffers.add(slice);
            }
        }
        return input;
    }

    @Override
    public List<ByteBuffer> get() {
        return buffers;
    }

    @Override
    public boolean isComplete() {
        return isComplete;
    }
}
