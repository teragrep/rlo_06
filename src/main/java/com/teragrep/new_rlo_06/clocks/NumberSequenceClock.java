package com.teragrep.new_rlo_06.clocks;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class NumberSequenceClock implements Clock<List<ByteBuffer>> {

    private final int maximumLength;
    private final int minimumLength;
    private boolean isComplete;
    private final List<ByteBuffer> buffers;
    private int numberCount = 0;

    public NumberSequenceClock(int maximumLength) {
        this(maximumLength, 1);
    }

    public NumberSequenceClock(int maximumLength, int minimumLength) {
        this.maximumLength = maximumLength;
        this.minimumLength = minimumLength;
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
                    if (numberCount > maximumLength) {
                        throw new NumberSequenceParseException("too many numbers");
                    }
                    sliceLimit++;
                }
                else {
                    if (numberCount < minimumLength) {
                        throw new NumberSequenceParseException("too few numbers");
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
