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

import com.teragrep.new_rlo_06.inputs.StringInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class PriorityBufferedImplTest {

    @Test
    public void test() {
        StringInput openInput = new StringInput("<");
        StringInput numbersInput = new StringInput("012");
        StringInput closeInput = new StringInput(">");

        Priority priority = new PriorityBufferedImpl(
                Arrays.asList(openInput.asBuffers()),
                Arrays.asList(numbersInput.asBuffers()),
                Arrays.asList(closeInput.asBuffers())
        );

        Assertions.assertEquals(12, priority.toInt());

        Assertions.assertEquals("012", priority.toString());
    }

    /*
    @Test
    public void testEncoded() {
        StringInput stringInput = new StringInput("012");
        LinkedList<ByteBuffer> buffers = new LinkedList<>(Arrays.asList(stringInput.asBuffers()));
        Priority priority = new PriorityBufferedImpl(new ElementImpl(buffers));
        Element element = new ElementImpl(priority.encode().buffers());
        Assertions.assertEquals("<012>", element.toString());
    }
    
    @Test
    public void testDecodeEncodeDecode() {
        StringInput stringInput = new StringInput("012");
        LinkedList<ByteBuffer> buffers = new LinkedList<>(Arrays.asList(stringInput.asBuffers()));
        Priority priority = new PriorityBufferedImpl(new ElementImpl(buffers));
        Priority priority1Decoded = priority.encode().decode();
    
        Assertions.assertEquals("012", priority1Decoded.toString());
        // Assertions.assertEquals(priority, priority1Decoded); // TODO once testEquals passes
    
    }
    
    @Disabled
    @Test
    public void testEquals() {
        StringInput stringInput = new StringInput("012");
        LinkedList<ByteBuffer> buffers = new LinkedList<>(Arrays.asList(stringInput.asBuffers()));
        Priority priority = new PriorityBufferedImpl(new ElementImpl(buffers));
    
        LinkedList<ByteBuffer> buffers1 = new LinkedList<>(Arrays.asList(stringInput.asBuffers()));
        Priority priority1 = new PriorityBufferedImpl(new ElementImpl(buffers1));
    
        Assertions.assertEquals(priority, priority1);
    }
    
     */
}
