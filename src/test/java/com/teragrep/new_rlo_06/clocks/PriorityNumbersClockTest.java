package com.teragrep.new_rlo_06.clocks;

import com.teragrep.new_rlo_06.ElementImpl;
import com.teragrep.new_rlo_06.inputs.StringInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

public class PriorityNumbersClockTest {
    @Test
    public void testNumbersClock() {
        StringInput input = new StringInput("059>");
        ByteBuffer[] buffers = input.asBuffers(1);

        PriorityNumbersClock clock = new PriorityNumbersClock();

        Assertions.assertFalse(clock.isComplete());

        ByteBuffer out0 = clock.apply(buffers[0]);
        Assertions.assertTrue(out0.hasRemaining());

        Assertions.assertEquals("059", new ElementImpl(clock.get()).toString());

        Assertions.assertEquals(59, new ElementImpl(clock.get()).toInt());

        Assertions.assertTrue(clock.isComplete());
    }

    @Test
    public void testNumbersClockMultipleBuffers() {
        StringInput input = new StringInput("059>");
        ByteBuffer[] buffers = input.asBuffers(4);

        PriorityNumbersClock clock = new PriorityNumbersClock();

        Assertions.assertFalse(clock.isComplete());

        ByteBuffer out0 = clock.apply(buffers[0]);
        Assertions.assertFalse(out0.hasRemaining());
        Assertions.assertFalse(clock.isComplete());

        ByteBuffer out1 = clock.apply(buffers[1]);
        Assertions.assertFalse(out1.hasRemaining());
        Assertions.assertFalse(clock.isComplete());

        ByteBuffer out2 = clock.apply(buffers[2]);
        Assertions.assertFalse(out2.hasRemaining());
        Assertions.assertFalse(clock.isComplete());

        // must not consume '>'
        ByteBuffer out3 = clock.apply(buffers[3]);
        Assertions.assertTrue(out3.hasRemaining());
        Assertions.assertTrue(clock.isComplete());


        Assertions.assertEquals("059", new ElementImpl(clock.get()).toString());

        Assertions.assertEquals(59, new ElementImpl(clock.get()).toInt());

        Assertions.assertTrue(clock.isComplete());
    }
}
