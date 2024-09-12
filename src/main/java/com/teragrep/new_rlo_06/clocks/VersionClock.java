package com.teragrep.new_rlo_06.clocks;

import com.teragrep.new_rlo_06.Version;
import com.teragrep.new_rlo_06.VersionBufferedImpl;
import com.teragrep.new_rlo_06.VersionStub;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Function;

public class VersionClock implements Clock<Version>{
    private static final VersionStub versionStub = new VersionStub();

    private final NumberSequenceClock numberSequenceClock;
    private final CharClock spaceClock;

    private final Function<ByteBuffer, ByteBuffer> clockChain;
    public VersionClock() {
        this.numberSequenceClock = new NumberSequenceClock(1);
        this.spaceClock = new CharClock(' ');

        this.clockChain = numberSequenceClock.andThen(spaceClock);

    }
    @Override
    public boolean isComplete() {
        return spaceClock.isComplete();
    }

    @Override
    public ByteBuffer apply(ByteBuffer input) {
        if (!isComplete()) {
            input = clockChain.apply(input);
        }
        return input;
    }

    @Override
    public Version get() {
        final Version version;
        if (isComplete()) {
            List<ByteBuffer> numbers = numberSequenceClock.get();
            List<ByteBuffer> spaces = spaceClock.get();
            version = new VersionBufferedImpl(numbers, spaces);
        }
        else {
            version = versionStub;
        }
        return version;
    }
}
