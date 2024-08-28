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
package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class MsgTest {

    @Test
    public void parseLeadingSpaceNoLFTest() {
        // lf termination off
        Fragment msg = new Fragment(256 * 1024, new MsgFunction(false));

        String input = " msg with preceding space and no newline";

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes(StandardCharsets.US_ASCII));

        Stream stream = new Stream();
        stream.setInputStream(bais);

        Assertions.assertTrue(stream.next()); // msg requires stream called with next
        msg.accept(stream);

        Assertions.assertEquals("msg with preceding space and no newline", msg.toString());
    }

    @Test
    public void parseNoLeadingSpaceNoLFTest() {
        // lf termination off
        Fragment msg = new Fragment(256 * 1024, new MsgFunction(false));

        String input = "msg without preceding space and no newline";

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes(StandardCharsets.US_ASCII));

        Stream stream = new Stream();
        stream.setInputStream(bais);

        Assertions.assertTrue(stream.next()); // msg requires stream called with next
        msg.accept(stream);

        Assertions.assertEquals("msg without preceding space and no newline", msg.toString());
    }

    @Test
    public void parseNewlineTest() {
        // lf termination off
        Fragment msg = new Fragment(256 * 1024, new MsgFunction(false));

        String input = " yes\nnewline";

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes(StandardCharsets.US_ASCII));

        Stream stream = new Stream();
        stream.setInputStream(bais);

        Assertions.assertTrue(stream.next()); // msg requires stream called with next
        msg.accept(stream);

        Assertions.assertEquals("yes\nnewline", msg.toString());
    }

    @Test
    public void parseLFTerminationWithNextTest() {
        // lf termination off
        Fragment msg = new Fragment(256 * 1024, new MsgFunction(true));

        String input = " there is something after newline\nanother";

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes(StandardCharsets.US_ASCII));

        Stream stream = new Stream();
        stream.setInputStream(bais);

        Assertions.assertTrue(stream.next()); // msg requires stream called with next
        msg.accept(stream);

        Assertions.assertEquals("there is something after newline", msg.toString());
    }

    @Test
    public void parseLFTerminationWithoutNextTest() {
        // lf termination off
        Fragment msg = new Fragment(256 * 1024, new MsgFunction(true));

        String input = " there is nothing after newline\n";

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes(StandardCharsets.US_ASCII));

        Stream stream = new Stream();
        stream.setInputStream(bais);

        Assertions.assertTrue(stream.next()); // msg requires stream called with next
        msg.accept(stream);

        Assertions.assertEquals("there is nothing after newline", msg.toString());
    }

    @Test
    public void emptyMessageTest() {
        // lf termination off
        Fragment msg = new Fragment(256 * 1024, new MsgFunction(true));

        String input = " ";

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes(StandardCharsets.US_ASCII));

        Stream stream = new Stream();
        stream.setInputStream(bais);

        Assertions.assertTrue(stream.next()); // msg requires stream called with next
        msg.accept(stream);

        Assertions.assertEquals("", msg.toString());
    }
}
