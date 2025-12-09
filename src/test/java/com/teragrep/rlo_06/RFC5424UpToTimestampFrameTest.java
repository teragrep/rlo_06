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

import static org.junit.jupiter.api.Assertions.*;

public class RFC5424UpToTimestampFrameTest {

    @Test
    public void testValues() {
        String SYSLOG_MESSAGE = "<191>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [sd_one@48577 id_one=\"eno\" id_two=\"owt\"][sd_two@48577 id_three=\"eerht\" id_four=\"ruof\"] msg\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(SYSLOG_MESSAGE.getBytes());
        Frame timestampFrame = new RFC5424UpToTimestampFrame(false);
        timestampFrame.load(inputStream);
        Assertions.assertDoesNotThrow(() -> assertTrue(timestampFrame.next()));

        Assertions.assertEquals("191", timestampFrame.priority().toString());
        Assertions.assertEquals("1", timestampFrame.version().toString());
        Assertions.assertEquals("2014-06-20T09:14:07.12345+00:00", timestampFrame.timestamp().toString());
        Assertions.assertEquals("", timestampFrame.hostname().toString());
        Assertions.assertEquals("", timestampFrame.appName().toString());
        Assertions.assertEquals("", timestampFrame.procId().toString());
        Assertions.assertEquals("", timestampFrame.msgId().toString());
        Assertions.assertEquals("", timestampFrame.msg().toString());
        Assertions.assertEquals("", timestampFrame.structuredData().toString());

    }
}
