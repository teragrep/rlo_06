package com.teragrep.new_rlo_06.clocks;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TimestampPrecisionClock implements Clock<List<ByteBuffer>> {

    private final Clock<List<ByteBuffer>> dotRejectableClock;
    private final NumberSequenceClock numbersClock;
    private final Function<ByteBuffer, ByteBuffer> clockChain;

    private boolean isPresent;
    private boolean isAbsent;

    public TimestampPrecisionClock() {
        this.dotRejectableClock = new ClockReject<>(new CharClock('.'));
        this.numbersClock = new NumberSequenceClock(6, 1);
        this.clockChain = dotRejectableClock.andThen(numbersClock);

        this.isPresent = false;
        this.isAbsent = false;
    }

    @Override
    public boolean isComplete() {
        boolean rv;
        if (isPresent) {
            rv = numbersClock.isComplete();
        }
        else {
            rv = isAbsent;
        }
        return rv;
    }

    @Override
    public ByteBuffer apply(ByteBuffer input) {
        if (!isAbsent) {

            // detect if present
            if (!isPresent && input.hasRemaining()) {
                byte b = input.get();
                if (b == '.') {
                    isPresent = true;
                }
                else {
                    isAbsent = true;
                }
                input.position(input.position() - 1);
            }

            if (isPresent) {
                input = clockChain.apply(input);
            }
        }
        return input;
    }

    @Override
    public List<ByteBuffer> get() {
        List<ByteBuffer> dots = dotRejectableClock.get();
        List<ByteBuffer> numbers = numbersClock.get();
        List<ByteBuffer> buffers = new ArrayList<>(dots.size() + numbers.size());
        buffers.addAll(dots);
        buffers.addAll(numbers);

        return buffers;
    }
}
