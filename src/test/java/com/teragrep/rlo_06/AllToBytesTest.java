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

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AllToBytesTest {

    @Test
    public void allBytesToStringEqualsTest() throws IOException {
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [sd_one@48577 id_one=\"eno\" id_two=\"owt\"][sd_two@48577 id_three=\"eerht\" id_four=\"ruof\"] msg\n";

        InputStream inputStream = new ByteArrayInputStream(SYSLOG_MESSAGE.getBytes());
        RFC5424Frame rfc5424Frame = new RFC5424Frame(true);
        rfc5424Frame.load(inputStream);
        assertTrue(rfc5424Frame.next());

        Fragment priority = rfc5424Frame.priority;
        Assertions.assertArrayEquals("14".getBytes(), priority.toBytes());
        Assertions.assertEquals("14", priority.toString());

        Fragment version = rfc5424Frame.version;
        Assertions.assertArrayEquals("1".getBytes(), version.toBytes());
        Assertions.assertEquals("1", version.toString());

        Fragment timestamp = rfc5424Frame.timestamp;
        Assertions.assertArrayEquals("2014-06-20T09:14:07.12345+00:00".getBytes(), timestamp.toBytes());
        Assertions.assertEquals("2014-06-20T09:14:07.12345+00:00", timestamp.toString());

        Fragment hostname = rfc5424Frame.hostname;
        Assertions.assertArrayEquals("host01".getBytes(), hostname.toBytes());
        Assertions.assertEquals("host01", hostname.toString());

        Fragment appName = rfc5424Frame.appName;
        Assertions.assertArrayEquals("systemd".getBytes(), appName.toBytes());
        Assertions.assertEquals("systemd", appName.toString());

        Fragment procId = rfc5424Frame.procId;
        Assertions.assertArrayEquals("DEA".getBytes(), procId.toBytes());
        Assertions.assertEquals("DEA", procId.toString());

        Fragment msgId = rfc5424Frame.msgId;
        Assertions.assertArrayEquals("MSG-01".getBytes(), msgId.toBytes());
        Assertions.assertEquals("MSG-01", msgId.toString());

        Fragment id_one = rfc5424Frame.structuredData.getValue(new SDVector("sd_one@48577", "id_one"));
        Assertions.assertArrayEquals("eno".getBytes(), id_one.toBytes());
        Assertions.assertEquals("eno", id_one.toString());

        Fragment id_two = rfc5424Frame.structuredData.getValue(new SDVector("sd_one@48577", "id_two"));
        Assertions.assertArrayEquals("owt".getBytes(), id_two.toBytes());
        Assertions.assertEquals("owt", id_two.toString());

        Fragment id_three = rfc5424Frame.structuredData.getValue(new SDVector("sd_two@48577", "id_three"));
        Assertions.assertArrayEquals("eerht".getBytes(), id_three.toBytes());
        Assertions.assertEquals("eerht", id_three.toString());

        Fragment id_four = rfc5424Frame.structuredData.getValue(new SDVector("sd_two@48577", "id_four"));
        Assertions.assertArrayEquals("ruof".getBytes(), id_four.toBytes());
        Assertions.assertEquals("ruof", id_four.toString());

        Fragment msg = rfc5424Frame.msg;
        Assertions.assertArrayEquals("msg".getBytes(), msg.toBytes());
        Assertions.assertEquals("msg", msg.toString());
    }
}
