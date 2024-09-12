package com.teragrep.new_rlo_06.clocks;

import com.teragrep.new_rlo_06.ElementImpl;
import com.teragrep.new_rlo_06.PriorityParseException;
import com.teragrep.new_rlo_06.inputs.StringInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class VersionClockTest {
    @Test
    public void testVersionClock() {
        StringInput input = new StringInput("1 X");
        ByteBuffer[] buffers = input.asBuffers(1);

        VersionClock clock = new VersionClock();

        Assertions.assertFalse(clock.isComplete());

        ByteBuffer out0 = clock.apply(buffers[0]);
        Assertions.assertTrue(out0.hasRemaining());

        Assertions.assertEquals("1", clock.get().toString());

        Assertions.assertTrue(clock.isComplete());

        List<ByteBuffer> bufferList = new ArrayList<>(1);
        bufferList.add(out0.slice());
        Assertions.assertEquals("X", new ElementImpl(bufferList).toString());
    }

    @Test
    public void testVersionClockThreeBuffers() {
        StringInput input = new StringInput("1 X");
        ByteBuffer[] buffers = input.asBuffers(3);

        VersionClock clock = new VersionClock();

        ByteBuffer out0 = clock.apply(buffers[0]);
        Assertions.assertFalse(out0.hasRemaining());
        Assertions.assertFalse(clock.isComplete());

        ByteBuffer out1 = clock.apply(buffers[1]);
        Assertions.assertFalse(out1.hasRemaining());
        Assertions.assertTrue(clock.isComplete());

        Assertions.assertEquals("1", clock.get().toString());

        ByteBuffer out2 = clock.apply(buffers[2]);
        Assertions.assertFalse(out2.hasRemaining());

        ByteBuffer in2 = out2.slice();
        List<ByteBuffer> ins = new ArrayList<>(1);
        ins.add(in2);
        Assertions.assertEquals("X", new ElementImpl(ins).toString());
    }

    @Test
    public void testInvalidThrows() {
        StringInput input = new StringInput("@");
        ByteBuffer[] buffers = input.asBuffers(1);

        VersionClock clock = new VersionClock();

        PriorityParseException exception = Assertions.assertThrows(PriorityParseException.class, () -> {
            clock.apply(buffers[0]);
        });
        Assertions.assertEquals("version version version", exception.getMessage());

    }
}
