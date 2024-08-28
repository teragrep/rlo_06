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

public class ClearThrowsTest {

    @Test
    public void allBytesToStringEqualsTest() throws IOException {
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [sd_one@48577 id_one=\"eno\" id_two=\"owt\"][sd_two@48577 id_three=\"eerht\" id_four=\"ruof\"] msg\n";
        InputStream inputStream = new ByteArrayInputStream(SYSLOG_MESSAGE.getBytes());
        RFC5424Frame rfc5424Frame = new RFC5424Frame(true);
        rfc5424Frame.load(inputStream);
        Assertions.assertTrue(rfc5424Frame.next());

        rfc5424Frame.priority.clear();
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.priority::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.priority::toBytes);

        rfc5424Frame.version.clear();
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.version::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.version::toBytes);

        rfc5424Frame.timestamp.clear();
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.timestamp::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.timestamp::toBytes);

        rfc5424Frame.hostname.clear();
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.hostname::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.hostname::toBytes);

        rfc5424Frame.appName.clear();
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.appName::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.appName::toBytes);

        rfc5424Frame.procId.clear();
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.procId::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.procId::toBytes);

        rfc5424Frame.msgId.clear();
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.msgId::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.msgId::toBytes);

        Fragment sdParamValue = rfc5424Frame.structuredData.getValue(new SDVector("sd_one@48577", "id_one"));
        sdParamValue.clear();
        Assertions.assertThrows(IllegalStateException.class, sdParamValue::toString);
        Assertions.assertThrows(IllegalStateException.class, sdParamValue::toBytes);

        rfc5424Frame.structuredData.clear();
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.structuredData::toString);

        rfc5424Frame.msg.clear();
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.msg::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.msg::toBytes);
    }
}
