package com.teragrep.new_rlo_06.clocks;

import com.teragrep.new_rlo_06.Timestamp;

import java.nio.ByteBuffer;
import java.util.function.Function;

public class TimestampClock implements Clock<Timestamp> {
    private final NumberSequenceClock yearClock;
    private final CharClock yearDashClock;
    private final NumberSequenceClock monthClock;
    private final CharClock monthDashClock;
    private final NumberSequenceClock dayClock;
    private final CharClock dayTClock;
    private final NumberSequenceClock hourClock;
    private final CharClock hourColonClock;
    private final NumberSequenceClock minuteClock;
    private final CharClock minuteColonClock;
    private final NumberSequenceClock secondClock;

    private final TimestampPrecisionClock timestampPrecisionClock;

    private final Function<ByteBuffer, ByteBuffer> clockChain;
    // TODO

    public TimestampClock() {
        this.yearClock = new NumberSequenceClock(4, 4);
        this.yearDashClock = new CharClock('-');
        this.monthClock = new NumberSequenceClock(2, 2);
        this.monthDashClock = new CharClock('-');
        this.dayClock = new NumberSequenceClock(2, 2);
        this.dayTClock = new CharClock('T');
        this.hourClock = new NumberSequenceClock(2, 2);
        this.hourColonClock = new CharClock(':');
        this.minuteClock = new NumberSequenceClock(2, 2);
        this.minuteColonClock = new CharClock(':');
        this.secondClock = new NumberSequenceClock(2, 2);

        this.timestampPrecisionClock = new TimestampPrecisionClock();

        this.clockChain = yearClock.andThen(yearDashClock.andThen(monthClock.andThen(monthDashClock.andThen(dayClock.andThen(dayTClock.andThen(hourClock.andThen(hourColonClock.andThen(minuteClock.andThen(minuteColonClock.andThen(secondClock.andThen(timestampPrecisionClock)
        ))))))))));
    }

    @Override
    public boolean isComplete() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ByteBuffer apply(ByteBuffer input) {
        if (!secondClock.isComplete()) {
            input = clockChain.apply(input);
        }
        return input;
    }

    @Override
    public Timestamp get() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
