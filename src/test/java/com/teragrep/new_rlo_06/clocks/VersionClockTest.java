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

import com.teragrep.new_rlo_06.ElementImpl;
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

        VersionClock clock = new VersionClock(timestampClock);

        Assertions.assertFalse(clock.isComplete());

        ByteBuffer out0 = clock.accept(buffers[0]);
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

        VersionClock clock = new VersionClock(timestampClock);

        ByteBuffer out0 = clock.accept(buffers[0]);
        Assertions.assertFalse(out0.hasRemaining());
        Assertions.assertFalse(clock.isComplete());

        ByteBuffer out1 = clock.accept(buffers[1]);
        Assertions.assertFalse(out1.hasRemaining());
        Assertions.assertTrue(clock.isComplete());

        Assertions.assertEquals("1", clock.get().toString());

        ByteBuffer out2 = clock.accept(buffers[2]);
        Assertions.assertTrue(out2.hasRemaining());

        ByteBuffer in2 = out2.slice();
        List<ByteBuffer> ins = new ArrayList<>(1);
        ins.add(in2);
        Assertions.assertEquals("X", new ElementImpl(ins).toString());
    }

    @Test
    public void testInvalidThrows() {
        StringInput input = new StringInput("@");
        ByteBuffer[] buffers = input.asBuffers(1);

        VersionClock clock = new VersionClock(timestampClock);

        NumberSequenceParseException exception = Assertions.assertThrows(NumberSequenceParseException.class, () -> {
            clock.accept(buffers[0]);
        });
        Assertions.assertEquals("too few numbers", exception.getMessage());

    }

    @Test
    public void testMissingSpaceThrows() {
        StringInput input = new StringInput("1@");
        ByteBuffer[] buffers = input.asBuffers(1);

        VersionClock clock = new VersionClock(timestampClock);

        CharacterParseException exception = Assertions.assertThrows(CharacterParseException.class, () -> {
            clock.accept(buffers[0]);
        });
        Assertions.assertEquals("expected ' '", exception.getMessage());

    }
}
