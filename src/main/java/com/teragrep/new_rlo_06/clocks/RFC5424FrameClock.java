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

public class RFC5424FrameClock implements TerminalClock<_RFC5424Frame> {

    private static final _RFC5424FrameStub rfc5424FrameStub = new _RFC5424FrameStub();

    private static final _PriorityStub priorityStub = new _PriorityStub();

    private final PriorityClock priorityClock;
    private final MessageClock messageClock;

    private _Priority priority;

    private _RFC5424Frame rfc5424Frame;

    public RFC5424FrameClock() {

        this.priorityClock = new PriorityClock();
        this.messageClock = new MessageClock();

        this.priority = priorityStub;
        this.rfc5424Frame = rfc5424FrameStub;
    }

    public synchronized void submit(ByteBuffer input) {
        while (input.hasRemaining()) {
            if (priority.isStub()) {
                priority = priorityClock.submit(input);
            }
            // TODO rest
            else {
                // messages are never stub
                messageClock.submit(input);
                break;
            }
        }
    }

    public _RFC5424Frame get() {
        if (rfc5424FrameStub.isStub()) {
            // TODO add if (!structuredData.isStub())
            rfc5424Frame = new _RFC5424FrameImpl(priority, messageClock.get());
        }
        return rfc5424Frame;
    }
}
