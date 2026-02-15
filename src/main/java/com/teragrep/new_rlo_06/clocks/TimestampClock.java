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

import com.teragrep.new_rlo_06.Timestamp;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class TimestampClock implements Clock<Timestamp> {

    // TODO tzinfo
    private final TimestampPrecisionClock timestampPrecisionClock;

    private final NumberSequenceClock secondClock;
    private final CharClock minuteColonClock;
    private final NumberSequenceClock minuteClock;
    private final CharClock hourColonClock;
    private final NumberSequenceClock hourClock;
    private final CharClock TClock;
    private final NumberSequenceClock dayClock;
    private final CharClock monthDashClock;
    private final NumberSequenceClock monthClock;
    private final CharClock yearDashClock;
    private final NumberSequenceClock yearClock;

    public TimestampClock(Consumer<ByteBuffer> nextClock) {
        this.timestampPrecisionClock = new TimestampPrecisionClock(nextClock);

        this.secondClock = new NumberSequenceClock(this.timestampPrecisionClock, 2, 2);
        this.minuteColonClock = new CharClock(this.secondClock, ':');
        this.minuteClock = new NumberSequenceClock(this.minuteColonClock, 2, 2);
        this.hourColonClock = new CharClock(this.minuteClock, ':');
        this.hourClock = new NumberSequenceClock(this.hourColonClock, 2, 2);
        this.TClock = new CharClock(this.hourClock, 'T');
        this.dayClock = new NumberSequenceClock(this.TClock, 2, 2);
        this.monthDashClock = new CharClock(this.dayClock, '-');
        this.monthClock = new NumberSequenceClock(this.monthDashClock, 2, 2);
        this.yearDashClock = new CharClock(monthClock, '-');
        this.yearClock = new NumberSequenceClock(this.yearDashClock, 4, 4);

    }

    @Override
    public void accept(ByteBuffer input) {
        yearClock.accept(input);
    }

    @Override
    public Timestamp get() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
