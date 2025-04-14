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

import com.teragrep.new_rlo_06.*;
import com.teragrep.new_rlo_06.inputs.StringInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

public class PriorityClockTest {

    @Test
    public void testClock() {
        PriorityClock priorityClock = new PriorityClock(new FakeClock());

        StringInput input = new StringInput("<123>");

        ByteBuffer[] buffers = input.asBuffers(2);

        priorityClock.accept(buffers[0]);
        Assertions.assertFalse(buffers[0].hasRemaining());

        priorityClock.accept(buffers[1]);
        Assertions.assertFalse(buffers[1].hasRemaining());

        Priority priority = priorityClock.get();
        Assertions.assertEquals(123, priority.toInt());
    }

    @Test
    void testFailOversizeOneBuffer() {
        PriorityClock priorityClock = new PriorityClock(new FakeClock());
        StringInput input = new StringInput("<1234>");
        ByteBuffer[] buffers = input.asBuffers();

        NumberSequenceParseException exception = Assertions.assertThrows(NumberSequenceParseException.class, () -> {
            priorityClock.accept(buffers[0]);
        });

        Assertions.assertEquals(exception.getMessage(), "too many numbers");
    }

    @Test
    void testFailOversizeSixBuffers() {
        PriorityClock priorityClock = new PriorityClock(new FakeClock());
        StringInput input = new StringInput("<1234>");
        ByteBuffer[] buffers = input.asBuffers(6);

        Assertions.assertEquals(6, buffers.length);

        priorityClock.accept(buffers[0]);
        Assertions.assertFalse(buffers[0].hasRemaining());

        priorityClock.accept(buffers[1]);
        Assertions.assertFalse(buffers[1].hasRemaining());

        priorityClock.accept(buffers[2]);
        Assertions.assertFalse(buffers[2].hasRemaining());

        priorityClock.accept(buffers[3]);
        Assertions.assertFalse(buffers[3].hasRemaining());

        NumberSequenceParseException exception = Assertions.assertThrows(NumberSequenceParseException.class, () -> {
            priorityClock.accept(buffers[4]);
        });

        Assertions.assertEquals(exception.getMessage(), "too many numbers");
    }

    @Test
    public void testFailNoContentOneBuffer() {
        PriorityClock priorityClock = new PriorityClock(new FakeClock());
        StringInput input = new StringInput("<>");
        ByteBuffer[] buffers = input.asBuffers();

        NumberSequenceParseException exception = Assertions.assertThrows(NumberSequenceParseException.class, () -> {
            priorityClock.accept(buffers[0]);
        });

        Assertions.assertEquals(exception.getMessage(), "too few numbers");
    }

    @Test
    public void testFailNoContentTwoBuffers() {
        PriorityClock priorityClock = new PriorityClock(new FakeClock());
        StringInput input = new StringInput("<>");
        ByteBuffer[] buffers = input.asBuffers(2);

        priorityClock.accept(buffers[0]);
        Assertions.assertFalse(buffers[0].hasRemaining());

        NumberSequenceParseException exception = Assertions.assertThrows(NumberSequenceParseException.class, () -> {
            priorityClock.accept(buffers[1]);
        });

        Assertions.assertEquals(exception.getMessage(), "too few numbers");
    }

    @Test
    void testFailStart() {
        PriorityClock priorityClock = new PriorityClock(new FakeClock());
        StringInput input = new StringInput("123>");
        ByteBuffer[] buffers = input.asBuffers();

        CharacterParseException exception = Assertions.assertThrows(CharacterParseException.class, () -> {
            priorityClock.accept(buffers[0]);
        });

        Assertions.assertEquals(exception.getMessage(), "expected '<'");
    }

    @Test
    void testFailDoubleStart() {
        PriorityClock priorityClock = new PriorityClock(new FakeClock());
        StringInput input = new StringInput("<<3>");
        ByteBuffer[] buffers = input.asBuffers();

        NumberSequenceParseException exception = Assertions.assertThrows(NumberSequenceParseException.class, () -> {
            priorityClock.accept(buffers[0]);
        });

        Assertions.assertEquals(exception.getMessage(), "too few numbers");
    }

    @Test
    void testFailMultiBuffer() {
        PriorityClock priorityClock = new PriorityClock(new FakeClock());
        StringInput input = new StringInput("<<3>");
        ByteBuffer[] buffers = input.asBuffers(4);

        priorityClock.accept(buffers[0]);
        Assertions.assertFalse(buffers[0].hasRemaining());

        NumberSequenceParseException exception = Assertions.assertThrows(NumberSequenceParseException.class, () -> {
            priorityClock.accept(buffers[1]);
        });

        Assertions.assertEquals(exception.getMessage(), "too few numbers");
    }

    @Test
    void testTerminationSingleBuffer() {
        PriorityClock priorityClock = new PriorityClock(new FakeClock());
        StringInput input = new StringInput("<3>X");
        ByteBuffer[] buffers = input.asBuffers(1);

        priorityClock.accept(buffers[0]);
        Assertions.assertTrue(buffers[0].hasRemaining());

        Priority priority = priorityClock.get();

        Assertions.assertEquals(3, priority.toInt());
    }

    @Test
    void testTerminationFourBuffers() {
        PriorityClock priorityClock = new PriorityClock(new FakeClock());
        StringInput input = new StringInput("<3>X");
        ByteBuffer[] buffers = input.asBuffers(4);

        priorityClock.accept(buffers[0]);
        Assertions.assertFalse(buffers[0].hasRemaining());

        priorityClock.accept(buffers[1]);
        Assertions.assertFalse(buffers[1].hasRemaining());

        priorityClock.accept(buffers[2]);
        Priority priority = priorityClock.get();

        Assertions.assertFalse(buffers[2].hasRemaining());
        Assertions.assertEquals(3, priority.toInt());

        priorityClock.accept(buffers[3]);
        Assertions.assertTrue(buffers[3].hasRemaining()); // must not consume X

    }
}
