package com.teragrep.new_rlo_06.clocks;

import com.teragrep.new_rlo_06.PriorityParseException;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class PriorityNumbersClock implements Clock<List<ByteBuffer>> {

    private boolean isComplete;
    private final List<ByteBuffer> buffers;
    private int numberCount = 0;

    public PriorityNumbersClock() {
        this.isComplete = false;
        this.buffers = new LinkedList<>();
    }
    @Override
    public ByteBuffer apply(ByteBuffer input) {
        if (!isComplete) {
            ByteBuffer slice = input.slice();
            int sliceLimit = 0;
            while (input.hasRemaining()) {
                byte b = input.get();

                if (b >= '0' && b <= '9') {
                    numberCount++;
                    if (numberCount > 3) {
                        throw new PriorityParseException("too many numbers");
                    }
                    sliceLimit++;
                }
                else {
                    if (numberCount < 1) {
                        throw new PriorityParseException("too few numbers");
                    }
                    // un-get
                    input.position(input.position() - 1);
                    isComplete = true;
                    break;
                }
            }
            slice.limit(sliceLimit);

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
