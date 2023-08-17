/*
 * Java RFC524 parser library  RLO-06
 * Copyright (C) 2022  Suomen Kanuuna Oy
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

import java.nio.ByteBuffer;
import java.util.function.BiFunction;

public final class PriorityFunction implements BiFunction<Stream, ByteBuffer, ByteBuffer> {
    /*
    |||
    vvv
    <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

    Actions: O__O
    Payload:'<14>'
    States : |..T
    */

    @Override
    public ByteBuffer apply(Stream stream, ByteBuffer buffer) {
        if (stream.get() != '<') {
            throw new PriorityParseException("PRIORITY < missing");
        }

        if (!stream.next()) {
            throw new PriorityParseException("Expected PRIORITY, received nothing");
        }

        if (stream.get() >= 48 && stream.get() <= 57) { // first is always a number between 0..9
            buffer.put(stream.get());
        } else {
            throw new PriorityParseException("PRIORITY number incorrect");
        }

        if (!stream.next()) {
            throw new PriorityParseException("PRIORITY is too short, can't continue");
        }
        if (stream.get() >= 48 &&stream.get() <= 57) { // second may be a number between 0..9
            buffer.put(stream.get());

            if (!stream.next()) {
                throw new PriorityParseException("PRIORITY is too short, can't continue");
            }

            if (stream.get() >= 48 && stream.get() <= 57) { // third may be a number
                buffer.put(stream.get());

                if (!stream.next()) {
                    throw new PriorityParseException("PRIORITY is too short, can't continue");
                }

                if (stream.get() != 62) { // omit
                    throw new PriorityParseException("PRIORITY > missing");
                }
            } else if (stream.get() == 62) { // third may be a '>'
// omit '>'
            } else {
                throw new PriorityParseException("PRIORITY number incorrect");
            }
        } else if (stream.get() == 62) { // second may be a '>'
            // omit '>'
        } else {
            throw new PriorityParseException("PRIORITY number incorrect");
        }
        buffer.flip();
        return buffer;
    }
}
