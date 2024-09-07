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

import java.nio.ByteBuffer;
import java.util.LinkedList;

public class PriorityClock implements Clock<_Priority> {

    private final LinkedList<ByteBuffer> bufferSliceList;
    private static final int maximumPriorityLength = 3;
    private static final _PriorityStub priorityStub = new _PriorityStub();
    private boolean hasStart;

    public PriorityClock() {
        this.bufferSliceList = new LinkedList<>();
        this.hasStart = false;
    }

    @Override
    public _Priority submit(ByteBuffer input) {

        int bytesIncluded = 0;
        boolean isComplete = false;

        // copy buffer
        ByteBuffer slice = input.slice();

        // consume input
        while (input.hasRemaining()) {
            byte b = input.get();

            if (b == '<') {
                if (hasStart) {
                    throw new PriorityParseException("priority must not contain '<' in the content");
                }
                // remove '<' from the content
                slice.position(input.position());
                slice = slice.slice();
                hasStart = true;
                continue;
            }

            if (!hasStart) {
                throw new PriorityParseException("priority must start with a '<'");
            }

            if (b == '>') {
                // mask '>' because it is not part of priority
                slice.limit(slice.limit() - 1);
                if (slice.limit() == 0 && bufferSliceList.isEmpty()) {
                    throw new PriorityParseException("priority must have content");
                }
                isComplete = true;
                break;
            }

            // numbers only
            if (b < '0' || b > '9') {
                throw new PriorityParseException("priority must contain numbers only");
            }

            bytesIncluded++;
            checkOverSize(bytesIncluded);
        }

        slice.limit(bytesIncluded);

        // ignore empty slices
        if (slice.capacity() != 0) {
            bufferSliceList.add(slice);
        }

        _Priority priority;
        if (isComplete) {
            Element element = new ElementImpl(bufferSliceList);
            priority = new _PriorityImpl(element);
        }
        else {
            priority = priorityStub;
        }

        return priority;
    }

    private void checkOverSize(int bytesRead) {
        long currentLength = 0;
        for (ByteBuffer slice : bufferSliceList) {
            currentLength = currentLength + slice.limit();
        }

        currentLength = currentLength + bytesRead;
        if (currentLength > maximumPriorityLength) {
            throw new PriorityParseException("priority too long");
        }
    }
}
