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
import java.io.InputStream;

public class BrokenSyntaxTest {

    @Test
    void testBrokenFail() throws Exception {
        String SYSLOG_MESSAGE = "<134>1  - [ ] ] [";
        RFC5424Frame rfc5424Frame = new RFC5424Frame();
        rfc5424Frame.load(new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes()));
        Assertions.assertThrows(ParseException.class, rfc5424Frame::next);
    }

    @Test
    void testNilTimestamp() throws Exception {
        String input = "<14>1  host01 systemd DEA MSG-01 - sigsegv";
        RFC5424Frame rfc5424Frame = new RFC5424Frame();
        rfc5424Frame.load(new ByteArrayInputStream((input).getBytes()));
        rfc5424Frame.next();
        Assertions.assertEquals("14", rfc5424Frame.priority.toString(), "Priority");
        Assertions.assertEquals("1", rfc5424Frame.version.toString(), "Version");
        Assertions.assertEquals("", rfc5424Frame.timestamp.toString(), "Timestamp");
        Assertions.assertEquals("host01", rfc5424Frame.hostname.toString(), "Hostname");
        Assertions.assertEquals("systemd", rfc5424Frame.appName.toString(), "Appname");
        Assertions.assertEquals("DEA", rfc5424Frame.procId.toString(), "Procid");
        Assertions.assertEquals("MSG-01", rfc5424Frame.msgId.toString(), "msgid");
        Assertions.assertEquals("sigsegv", rfc5424Frame.msg.toString(), "msg");
    }

    @Test
    void testOpenSD() throws Exception {
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.123456+00:00 host01 systemd DEA MSG-01 [";
        RFC5424Frame rfc5424Frame = new RFC5424Frame();
        rfc5424Frame.load(new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes()));
        Assertions.assertThrows(ParseException.class, rfc5424Frame::next);
    }

    @Test
    void testAllNil() throws Exception {
        String input = "<2>1  - - - - - ";
        InputStream inputStream = new ByteArrayInputStream((input).getBytes());
        RFC5424Frame rfc5424Frame = new RFC5424Frame();
        rfc5424Frame.load(inputStream);
        rfc5424Frame.next();
        Assertions.assertEquals("2", rfc5424Frame.priority.toString(), "Priority");
        Assertions.assertEquals("1", rfc5424Frame.version.toString(), "Version");
        Assertions.assertEquals("", rfc5424Frame.timestamp.toString(), "Timestamp");
        Assertions.assertEquals("-", rfc5424Frame.hostname.toString(), "Hostname");
        Assertions.assertEquals("-", rfc5424Frame.appName.toString(), "Appname");
        Assertions.assertEquals("-", rfc5424Frame.procId.toString(), "ProcId");
        Assertions.assertEquals("-", rfc5424Frame.msgId.toString(), "MsgId");
        Assertions.assertEquals("", rfc5424Frame.msg.toString(), "Msg");
    }
}
