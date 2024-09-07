package com.teragrep.new_rlo_06.clocks;

import com.teragrep.new_rlo_06._Priority;
import com.teragrep.new_rlo_06.inputs.StringInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.LinkedList;

public class PriorityClockTest {

    @Test
    public void testClock() {
        PriorityClock priorityClock = new PriorityClock();

        LinkedList<_Priority> priorities = new LinkedList<>();
        StringInput input = new StringInput("<123>");

        ByteBuffer[] buffers = input.asBuffers(2);
        for (ByteBuffer buffer : buffers) {
            _Priority priority = priorityClock.submit(buffer);

            if (!priority.isStub()) {
                priorities.add(priority);
            }
        }

        Assertions.assertEquals(1, priorities.size());

        _Priority priority = priorities.get(0);

        Assertions.assertFalse(priority.isStub());

        Assertions.assertEquals(123, priority.toInt());
    }

    @Test void testFailOversizeOneBuffer() {
        PriorityClock priorityClock = new PriorityClock();
        StringInput input = new StringInput("<1234>");
        ByteBuffer[] buffers = input.asBuffers();

        PriorityParseException exception = Assertions.assertThrows(PriorityParseException.class, () -> {
            priorityClock.submit(buffers[0]);
        });

        Assertions.assertEquals(exception.getMessage(), "priority too long");
    }

    @Test void testFailOversizeSixBuffers() {
        PriorityClock priorityClock = new PriorityClock();
        StringInput input = new StringInput("<1234>");
        ByteBuffer[] buffers = input.asBuffers(6);

        Assertions.assertEquals(6, buffers.length);

        _Priority priority0 = priorityClock.submit(buffers[0]);
        Assertions.assertTrue(priority0.isStub());

        _Priority priority1 = priorityClock.submit(buffers[1]);
        Assertions.assertTrue(priority1.isStub());

        _Priority priority2 = priorityClock.submit(buffers[2]);
        Assertions.assertTrue(priority2.isStub());

        _Priority priority3 = priorityClock.submit(buffers[3]);
        Assertions.assertTrue(priority3.isStub());

        PriorityParseException exception = Assertions.assertThrows(PriorityParseException.class, () -> {
            priorityClock.submit(buffers[4]);
        });

        Assertions.assertEquals(exception.getMessage(), "priority too long");
    }

    @Test
    public void testFailNoContentOneBuffer() {
        PriorityClock priorityClock = new PriorityClock();
        StringInput input = new StringInput("<>");
        ByteBuffer[] buffers = input.asBuffers();


        PriorityParseException exception = Assertions.assertThrows(PriorityParseException.class, () -> {
            priorityClock.submit(buffers[0]);
        });

        Assertions.assertEquals(exception.getMessage(), "priority must have content");
    }

    @Test
    public void testFailNoContentTwoBuffers() {
        PriorityClock priorityClock = new PriorityClock();
        StringInput input = new StringInput("<>");
        ByteBuffer[] buffers = input.asBuffers(2);

        _Priority priority = priorityClock.submit(buffers[0]);

        Assertions.assertTrue(priority.isStub());

        PriorityParseException exception = Assertions.assertThrows(PriorityParseException.class, () -> {
            priorityClock.submit(buffers[1]);
        });

        Assertions.assertEquals(exception.getMessage(), "priority must have content");
    }

    @Test void testFailStart() {
        PriorityClock priorityClock = new PriorityClock();
        StringInput input = new StringInput("123>");
        ByteBuffer[] buffers = input.asBuffers();

        PriorityParseException exception = Assertions.assertThrows(PriorityParseException.class, () -> {
            priorityClock.submit(buffers[0]);
        });

        Assertions.assertEquals(exception.getMessage(), "priority must start with a '<'");
    }

    @Test void testFailDoubleStart() {
        PriorityClock priorityClock = new PriorityClock();
        StringInput input = new StringInput("<<3>");
        ByteBuffer[] buffers = input.asBuffers();

        PriorityParseException exception = Assertions.assertThrows(PriorityParseException.class, () -> {
            priorityClock.submit(buffers[0]);
        });

        Assertions.assertEquals(exception.getMessage(), "priority must not contain '<' in the content");
    }

    @Test void testFailMultiBuffer() {
        PriorityClock priorityClock = new PriorityClock();
        StringInput input = new StringInput("<<3>");
        ByteBuffer[] buffers = input.asBuffers(4);

        Assertions.assertTrue(priorityClock.submit(buffers[0]).isStub());

        PriorityParseException exception = Assertions.assertThrows(PriorityParseException.class, () -> {
            priorityClock.submit(buffers[1]);
        });

        Assertions.assertEquals(exception.getMessage(), "priority must not contain '<' in the content");
    }
}
