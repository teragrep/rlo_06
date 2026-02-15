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

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class NumberSequenceClock implements Clock<List<ByteBuffer>> {

    private final Consumer<ByteBuffer> nextClock;

    private final int maximumLength;
    private final int minimumLength;
    private boolean isComplete;
    private final List<ByteBuffer> buffers;
    private int numberCount = 0;

    public NumberSequenceClock(final Consumer<ByteBuffer> nextClock, int maximumLength) {
        this(nextClock, maximumLength, 1);
    }

    public NumberSequenceClock(final Consumer<ByteBuffer> nextClock, int maximumLength, int minimumLength) {
        this.nextClock = nextClock;
        this.maximumLength = maximumLength;
        this.minimumLength = minimumLength;
        this.isComplete = false;
        this.buffers = new LinkedList<>();
    }

    @Override
    public void accept(ByteBuffer input) {
        if (!isComplete) {
            ByteBuffer slice = input.slice();
            int sliceLimit = 0;
            while (input.hasRemaining()) {
                byte b = input.get();

                if (b >= '0' && b <= '9') {
                    numberCount++;
                    if (numberCount > maximumLength) {
                        throw new NumberSequenceParseException("too many numbers");
                    }
                    sliceLimit++;
                }
                else {
                    if (numberCount < minimumLength) {
                        throw new NumberSequenceParseException("too few numbers");
                    }
                    // un-get
                    input.position(input.position() - 1);
                    isComplete = true;
                    break;
                }
            }
            slice.limit(sliceLimit);

            // ignore empty slices
            if (slice.limit() > 0) {
                buffers.add(slice);
            }
        }

        if (isComplete) {
            nextClock.accept(input);
        }
    }

    @Override
    public List<ByteBuffer> get() {
        return buffers;
    }
}
