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
import java.nio.charset.StandardCharsets;

public class StructuredDataTest {

    @Test
    public void parseTest() {
        StructuredData structuredData = new StructuredData();

        String input = "[id@0 keyHere=\"valueThere\"] "; // structured data terminates only to non [ character

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes(StandardCharsets.US_ASCII));

        Stream stream = new Stream();
        stream.setInputStream(bais);

        structuredData.accept(stream);

        Assertions.assertEquals("valueThere", structuredData.getValue(new SDVector("id@0", "keyHere")).toString());

        Assertions.assertEquals("valueThere", structuredData.getValue(new SDVector("id@0", "keyHere")).toString());

        structuredData.clear();

        Assertions.assertThrows(IllegalStateException.class, () -> {
            structuredData.getValue(new SDVector("id@0", "keyHere")).toString();
        });

    }

    @Test
    public void clearSDElementTest() {
        StructuredData structuredData = new StructuredData();

        String input = "[id@0 keyHere=\"valueThere\"] "; // structured data terminates only to non [ character

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes(StandardCharsets.US_ASCII));

        Stream stream = new Stream();
        stream.setInputStream(bais);

        structuredData.accept(stream);

        Assertions.assertEquals("id@0", structuredData.sdElements.get(0).sdElementId.toString());

        Assertions.assertEquals("id@0", structuredData.sdElements.get(0).sdElementId.toString());

        // clear
        structuredData.sdElements.get(0).sdElementId.clear();
        Assertions.assertThrows(IllegalStateException.class, () -> {
            structuredData.sdElements.get(0).sdElementId.toString();
        }, "direction != Direction.READ");

        // double clear
        structuredData.sdElements.get(0).sdElementId.clear();
        Assertions.assertThrows(IllegalStateException.class, () -> {
            structuredData.sdElements.get(0).sdElementId.toString();
        }, "direction != Direction.READ");
    }

    @Test
    public void clearSDParamKeyTest() {
        StructuredData structuredData = new StructuredData();

        String input = "[id@0 keyHere=\"valueThere\"] "; // structured data terminates only to non [ character

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes(StandardCharsets.US_ASCII));

        Stream stream = new Stream();
        stream.setInputStream(bais);

        structuredData.accept(stream);

        Assertions.assertEquals("keyHere", structuredData.sdElements.get(0).sdParams.get(0).sdParamKey.toString());

        Assertions.assertEquals("keyHere", structuredData.sdElements.get(0).sdParams.get(0).sdParamKey.toString());

        // clear
        structuredData.sdElements.get(0).sdParams.get(0).sdParamKey.clear();
        Assertions
                .assertThrows(IllegalStateException.class, () -> structuredData.sdElements.get(0).sdParams.get(0).sdParamKey.toString());

        // double clear
        structuredData.sdElements.get(0).sdParams.get(0).sdParamKey.clear();
        Assertions
                .assertThrows(IllegalStateException.class, () -> structuredData.sdElements.get(0).sdParams.get(0).sdParamKey.toString());
    }

    @Test
    public void parseDashTest() {
        StructuredData structuredData = new StructuredData();

        String input = "- "; // structured data terminates after the dash character

        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes(StandardCharsets.US_ASCII));

        Stream stream = new Stream();
        stream.setInputStream(bais);

        structuredData.accept(stream);

        Assertions.assertTrue(structuredData.getValue(new SDVector("id@0", "keyHere")).isStub);
        structuredData.clear();

        Assertions.assertThrows(IllegalStateException.class, () -> {
            structuredData.getValue(new SDVector("id@0", "keyHere")).toString();
        });
    }
}
