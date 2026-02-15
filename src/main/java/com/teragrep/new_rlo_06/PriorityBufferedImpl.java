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
package com.teragrep.new_rlo_06;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PriorityBufferedImpl implements Priority {

    private final List<ByteBuffer> openBuffers;
    private final List<ByteBuffer> numbersBuffers;
    private final List<ByteBuffer> closeBuffers;

    public PriorityBufferedImpl(
            List<ByteBuffer> openBuffers,
            List<ByteBuffer> numbersBuffers,
            List<ByteBuffer> closeBuffers
    ) {
        this.openBuffers = openBuffers;
        this.numbersBuffers = numbersBuffers;
        this.closeBuffers = closeBuffers;
    }

    @Override
    public boolean isStub() {
        return false;
    }

    @Override
    public List<ByteBuffer> toEncoded() {
        List<ByteBuffer> readOnlyBuffers = new ArrayList<>(
                openBuffers.size() + numbersBuffers.size() + closeBuffers.size()
        );

        for (ByteBuffer buffer : openBuffers) {
            readOnlyBuffers.add(buffer.asReadOnlyBuffer());
        }

        for (ByteBuffer buffer : numbersBuffers) {
            readOnlyBuffers.add(buffer.asReadOnlyBuffer());
        }

        for (ByteBuffer buffer : closeBuffers) {
            readOnlyBuffers.add(buffer.asReadOnlyBuffer());
        }

        return readOnlyBuffers;
    }

    @Override
    public byte[] toBytes() {
        return new ElementImpl(numbersBuffers).toBytes();
    }

    @Override
    public int toInt() {
        return new ElementImpl(numbersBuffers).toInt();
    }

    @Override
    public long size() {
        return new ElementImpl(numbersBuffers).size();
    }

    @Override
    public String toString() {
        return new ElementImpl(numbersBuffers).toString();
    }
}
