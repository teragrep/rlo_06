package com.teragrep.new_rlo_06;

import com.teragrep.new_rlo_06.inputs.StringInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;

public class _PriorityTest {


    @Test
    public void test() {
        StringInput stringInput = new StringInput("012");

        LinkedList<ByteBuffer> buffers = new LinkedList<>(Arrays.asList(stringInput.asBuffers()));

        _Priority priority = new _PriorityImpl(new ElementImpl(buffers));

        Assertions.assertEquals(12, priority.toInt());

        Assertions.assertEquals("012", priority.toString());
    }
}
