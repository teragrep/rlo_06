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
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimestampConversionTest {

    @Test
    public void testTimestampConversion() throws IOException {
        Assertions.assertEquals("2003-08-24T05:14:15.000003-07:00", getTimestamp("2003-08-24T05:14:15.000003-07:00"));
        Assertions.assertEquals("1985-04-12T23:20:50.520Z", getTimestamp("1985-04-12T23:20:50.52Z"));
        Assertions.assertEquals("1985-04-12T19:20:50.520-04:00", getTimestamp("1985-04-12T19:20:50.52-04:00"));
        Assertions.assertEquals("2003-10-11T22:14:15.003Z", getTimestamp("2003-10-11T22:14:15.003Z"));
        Assertions.assertEquals("2023-08-16T17:09:00.123456+03:00", getTimestamp("2023-08-16T17:09:00.123456+03:00"));
        // Missing seconds is a feature:
        // "The format used will be the shortest that outputs the full value of the time where the omitted parts are implied to be zero."
        // Source: https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html#toString--
        Assertions.assertEquals("2023-01-01T00:00Z", getTimestamp("2023-01-01T00:00:00.000000+00:00"));
        Assertions.assertEquals("2023-01-01T00:00Z", getTimestamp("2023-01-01T00:00:00.000000-00:00"));
        Assertions.assertEquals("2023-01-01T00:00+02:00", getTimestamp("2023-01-01T00:00:00+02:00"));

    }

    private String getTimestamp(String timestamp) throws IOException {
        String SYSLOG_MESSAGE = "<14>1 " + timestamp + " hostname appname - - - msg\n";
        InputStream inputStream = new ByteArrayInputStream(SYSLOG_MESSAGE.getBytes());
        RFC5424Frame rfc5424Frame = new RFC5424Frame(true);
        rfc5424Frame.load(inputStream);
        assertTrue(rfc5424Frame.next());
        return new RFC5424Timestamp(rfc5424Frame.timestamp).toZonedDateTime().toString();
    }
}
