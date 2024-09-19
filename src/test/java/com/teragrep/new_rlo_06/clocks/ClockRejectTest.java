package com.teragrep.new_rlo_06.clocks;

import com.teragrep.new_rlo_06.inputs.StringInput;
import com.teragrep.rlo_06.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.List;

public class ClockRejectTest {

    @Test
    public void testReject() {
        ClockReject<List<ByteBuffer>> clockReject = new ClockReject<>(new CharClock(' '));

        StringInput stringInput = new StringInput("#####");

        ByteBuffer output = clockReject.apply(stringInput.asBuffers()[0]);

        Assertions.assertTrue(clockReject.isRejected());

        Assertions.assertEquals(stringInput.asBuffers()[0], output);
    }

    @Test
    public void testRejectedThrows() {
        ClockReject<List<ByteBuffer>> clockReject = new ClockReject<>(new CharClock('c'));
        StringInput stringInput = new StringInput("v");

        ByteBuffer output = clockReject.apply(stringInput.asBuffers()[0]);

        Assertions.assertTrue(clockReject.isRejected());

        ParseException parseException = Assertions.assertThrows(ParseException.class,() -> {
            // retry
            clockReject.apply(stringInput.asBuffers()[0]);
        });

        Assertions.assertEquals("Rejected", parseException.getMessage());

        Assertions.assertEquals(stringInput.asBuffers()[0], output);
    }

    @Test
    public void testRejectedPartialMatch() {
        ClockReject<List<ByteBuffer>> clockReject = new ClockReject<>(new NumberSequenceClock(10));

        StringInput stringInput = new StringInput("12345");

        ByteBuffer output = clockReject.apply(stringInput.asBuffers()[0]);

        Assertions.assertFalse(clockReject.isRejected());
    }
}
