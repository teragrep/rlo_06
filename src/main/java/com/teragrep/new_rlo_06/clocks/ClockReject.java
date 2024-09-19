package com.teragrep.new_rlo_06.clocks;

import com.teragrep.rlo_06.ParseException;

import java.nio.ByteBuffer;

public class ClockReject<T> implements Clock<T>, Rejectable {

    public final Clock<T> clock;

    private boolean isRejected;

    public ClockReject(Clock<T> clock) {
        this.clock = clock;

        this.isRejected = false;
    }

    @Override
    public boolean isComplete() {
        return clock.isComplete();
    }

    @Override
    public boolean isRejected() {
        return isRejected;
    }

    @Override
    public ByteBuffer apply(ByteBuffer input) {
        ByteBuffer copiedInput = input.duplicate();
        if (!isRejected) {
            try {
                input = clock.apply(copiedInput);
            }
            catch (ParseException e) {
                isRejected = true;
            }
        }
        else {
            throw new IllegalStateException("Clock is rejected");
        }
        return input;
    }

    @Override
    public T get() {
        if (isRejected) {
            throw new IllegalStateException("Clock is rejected");
        }
        return clock.get();
    }

}
