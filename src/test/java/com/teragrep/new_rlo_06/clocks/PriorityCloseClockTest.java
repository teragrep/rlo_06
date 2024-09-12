package com.teragrep.new_rlo_06.clocks;

import com.teragrep.new_rlo_06.ElementImpl;
import com.teragrep.new_rlo_06.PriorityParseException;
import com.teragrep.new_rlo_06.inputs.StringInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PriorityCloseClockTest {
    @Test
    public void testCloseClock() {
        StringInput input = new StringInput(">");
        ByteBuffer[] buffers = input.asBuffers(1);

        PriorityCloseClock clock = new PriorityCloseClock();

        Assertions.assertFalse(clock.isComplete());

        ByteBuffer out0 = clock.apply(buffers[0]);
        Assertions.assertFalse(out0.hasRemaining());

        Assertions.assertEquals(">", new ElementImpl(clock.get()).toString());

        Assertions.assertTrue(clock.isComplete());
    }

    @Test
    public void testCloseClockNoConsumeMore() {
        StringInput input = new StringInput(">1");
        ByteBuffer[] buffers = input.asBuffers(1);

        PriorityCloseClock clock = new PriorityCloseClock();
        ByteBuffer out0 = clock.apply(buffers[0]);
        Assertions.assertTrue(out0.hasRemaining());

        Assertions.assertTrue(clock.isComplete());

        Assertions.assertEquals(">", new ElementImpl(clock.get()).toString());

        ByteBuffer in1 = out0.slice();
        List<ByteBuffer> ins = new ArrayList<>();
        ins.add(in1);
        Assertions.assertEquals("1", new ElementImpl(ins).toString());
    }

    @Test
    public void testCloseClockNoConsumeMoreTwoBuffers() {
        StringInput input = new StringInput(">1");
        ByteBuffer[] buffers = input.asBuffers(2);

        PriorityCloseClock clock = new PriorityCloseClock();

        ByteBuffer out0 = clock.apply(buffers[0]);
        Assertions.assertFalse(out0.hasRemaining());
        Assertions.assertTrue(clock.isComplete());

        Assertions.assertEquals(">", new ElementImpl(clock.get()).toString());

        ByteBuffer out1 = clock.apply(buffers[1]);
        Assertions.assertTrue(out1.hasRemaining());

        ByteBuffer in1 = out1.slice();
        List<ByteBuffer> ins = new ArrayList<>();
        ins.add(in1);
        Assertions.assertEquals("1", new ElementImpl(ins).toString());
    }

    @Test
    public void testInvalidThrows() {
        StringInput input = new StringInput("1");
        ByteBuffer[] buffers = input.asBuffers(1);

        PriorityCloseClock clock = new PriorityCloseClock();

        PriorityParseException exception = Assertions.assertThrows(PriorityParseException.class, () -> {
            clock.apply(buffers[0]);
        });
        Assertions.assertEquals("priority must end with a '>'", exception.getMessage());

    }
}
