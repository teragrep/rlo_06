/*
 * Teragrep RFC5424 frame library for Java (rlo_06)
 * Copyright (C) 2022-2024 Suomen Kanuuna Oy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 * Additional permission under GNU Affero General Public License version 3
 * section 7
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with other code, such other code is not for that reason alone subject to any
 * of the requirements of the GNU Affero GPL version 3 as long as this Program
 * is the same Program as licensed from Suomen Kanuuna Oy without any additional
 * modifications.
 *
 * Supplemented terms under GNU Affero General Public License version 3
 * section 7
 *
 * Origin of the software must be attributed to Suomen Kanuuna Oy. Any modified
 * versions must be marked as "Modified version of" The Program.
 *
 * Names of the licensors and authors may not be used for publicity purposes.
 *
 * No rights are granted for use of trade names, trademarks, or service marks
 * which are in The Program if any.
 *
 * Licensee must indemnify licensors and authors for any liability that these
 * contractual assumptions impose on licensors and authors.
 *
 * To the extent this program is licensed as part of the Commercial versions of
 * Teragrep, the applicable Commercial License may apply to this file if you as
 * a licensee so wish it.
 */
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

    @Test
    void testFailOversizeOneBuffer() {
        PriorityClock priorityClock = new PriorityClock();
        StringInput input = new StringInput("<1234>");
        ByteBuffer[] buffers = input.asBuffers();

        PriorityParseException exception = Assertions.assertThrows(PriorityParseException.class, () -> {
            priorityClock.submit(buffers[0]);
        });

        Assertions.assertEquals(exception.getMessage(), "priority too long");
    }

    @Test
    void testFailOversizeSixBuffers() {
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

    @Test
    void testFailStart() {
        PriorityClock priorityClock = new PriorityClock();
        StringInput input = new StringInput("123>");
        ByteBuffer[] buffers = input.asBuffers();

        PriorityParseException exception = Assertions.assertThrows(PriorityParseException.class, () -> {
            priorityClock.submit(buffers[0]);
        });

        Assertions.assertEquals(exception.getMessage(), "priority must start with a '<'");
    }

    @Test
    void testFailDoubleStart() {
        PriorityClock priorityClock = new PriorityClock();
        StringInput input = new StringInput("<<3>");
        ByteBuffer[] buffers = input.asBuffers();

        PriorityParseException exception = Assertions.assertThrows(PriorityParseException.class, () -> {
            priorityClock.submit(buffers[0]);
        });

        Assertions.assertEquals(exception.getMessage(), "priority must not contain '<' in the content");
    }

    @Test
    void testFailMultiBuffer() {
        PriorityClock priorityClock = new PriorityClock();
        StringInput input = new StringInput("<<3>");
        ByteBuffer[] buffers = input.asBuffers(4);

        Assertions.assertTrue(priorityClock.submit(buffers[0]).isStub());

        PriorityParseException exception = Assertions.assertThrows(PriorityParseException.class, () -> {
            priorityClock.submit(buffers[1]);
        });

        Assertions.assertEquals(exception.getMessage(), "priority must not contain '<' in the content");
    }

    @Test
    void testTerminationSingleBuffer() {
        PriorityClock priorityClock = new PriorityClock();
        StringInput input = new StringInput("<3>X");
        ByteBuffer[] buffers = input.asBuffers(1);

        _Priority priority = priorityClock.submit(buffers[0]);
        Assertions.assertFalse(priority.isStub());

        Assertions.assertEquals(3, priority.toInt());
    }

    @Test
    void testTerminationFourBuffers() {
        PriorityClock priorityClock = new PriorityClock();
        StringInput input = new StringInput("<3>X");
        ByteBuffer[] buffers = input.asBuffers(4);

        Assertions.assertTrue(priorityClock.submit(buffers[0]).isStub());
        Assertions.assertTrue(priorityClock.submit(buffers[1]).isStub());

        _Priority priority = priorityClock.submit(buffers[2]);
        Assertions.assertFalse(priority.isStub());

        Assertions.assertEquals(3, priority.toInt());
    }
}
