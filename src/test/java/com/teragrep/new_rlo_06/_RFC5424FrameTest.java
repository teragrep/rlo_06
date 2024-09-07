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

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class _RFC5424FrameTest {

    @Test
    public void testInterpretation() {
        String payloadFirstFragment = "<14>1 2014-06-20T09:14:07.123456";
        byte[] firstFragmentBytes = payloadFirstFragment.getBytes(StandardCharsets.UTF_8);
        ByteBuffer firstFragment = ByteBuffer.allocateDirect(firstFragmentBytes.length);
        firstFragment.put(firstFragmentBytes);
        firstFragment.flip();

        String payloadSecondFragment = "+00:00 host01 systemd DEA MSG-01 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"] sigsegv\n";
        byte[] secondFragmentBytes = payloadSecondFragment.getBytes(StandardCharsets.UTF_8);
        ByteBuffer secondFragment = ByteBuffer.allocateDirect(secondFragmentBytes.length);
        secondFragment.put(secondFragmentBytes);
        secondFragment.flip();

        ByteBuffer[] fragments = new ByteBuffer[2];
        fragments[0] = firstFragment;
        fragments[1] = secondFragment;

        _RFC5424Frame rfc5424Frame = new _RFC5424Frame();

    }
}
