/*
 * Java RFC524 parser library  RLO-06
 * Copyright (C) 2022  Suomen Kanuuna Oy
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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * subscription that controls which sdId's are parsed and which elements are captured during the parse
 * NOTE: creating empty one will capture all.
 */

public class RFC5424ParserSDSubscription {

    private final boolean subscribeAll;
    private final HashMap<ByteBuffer, HashMap<ByteBuffer, ByteBuffer>> mapHashMap = new HashMap<>();
    public RFC5424ParserSDSubscription() {
        this.subscribeAll = false;

    }

    public RFC5424ParserSDSubscription(boolean subscribeAll) {
        this.subscribeAll = subscribeAll;
    }

    public void subscribeElement(String sdId, String sdElement) {
        if (subscribeAll) {
            throw new IllegalArgumentException("subscribeElement can not be used together with subscribeAll");
        }
        // sdId
        byte[] sdIdBytes = sdId.getBytes(StandardCharsets.US_ASCII);
        ByteBuffer sdIdByteBuffer = ByteBuffer.allocateDirect(sdIdBytes.length);
        sdIdByteBuffer.put(sdIdBytes, 0 ,sdIdBytes.length);
        sdIdByteBuffer.flip();

        if (!mapHashMap.containsKey(sdIdByteBuffer)) {
            mapHashMap.put(sdIdByteBuffer, new HashMap<>());
        }

        // sdElement
        byte[] sdElemBytes = sdElement.getBytes(StandardCharsets.US_ASCII);
        ByteBuffer sdElemByteBuffer = ByteBuffer.allocateDirect(sdElemBytes.length);
        sdElemByteBuffer.put(sdElemBytes, 0 , sdElemBytes.length);
        sdElemByteBuffer.flip();

        mapHashMap.get(sdIdByteBuffer).put(sdElemByteBuffer, ByteBuffer.allocateDirect(8*1024));
    }

    public void clear() {
        if (subscribeAll) {
            mapHashMap.clear();
        }
        else {
            for (HashMap<ByteBuffer, ByteBuffer> sdElem : mapHashMap.values()) {
                for (ByteBuffer value : sdElem.values()) {
                    value.clear();
                }
            }
        }
    }

    public boolean isSubscribedSDId(ByteBuffer sdIdIterator) {
        boolean containsKey = mapHashMap.containsKey(sdIdIterator);

        if (subscribeAll && !containsKey) {
            mapHashMap.put(cloneByteBuffer(sdIdIterator), new HashMap<>());
            containsKey = true;
        }

        return containsKey;

    }

    public boolean isSubscribedSDElement(ByteBuffer sdIdIterator, ByteBuffer sdElementIterator) {
        boolean containsKey = mapHashMap.get(sdIdIterator).containsKey(sdElementIterator);

        if (subscribeAll && !containsKey) {
            HashMap<ByteBuffer, ByteBuffer> hashMap = mapHashMap.get(sdIdIterator);
            hashMap.put(cloneByteBuffer(sdElementIterator), ByteBuffer.allocateDirect(8 * 1024));
            containsKey = true;

        }

        return containsKey;
    }

    public ByteBuffer getSubscribedSDElementBuffer(ByteBuffer sdIdIterator, ByteBuffer sdElementIterator) {
        return mapHashMap.get(sdIdIterator).get(sdElementIterator);
    }

    private static ByteBuffer cloneByteBuffer(ByteBuffer source) {
        ByteBuffer clone = ByteBuffer.allocateDirect(source.capacity());
        source.rewind();
        clone.put(source);
        source.rewind();
        clone.flip();
        return clone;
    }

    HashMap<ByteBuffer, HashMap<ByteBuffer, ByteBuffer>> getMap() {
        return mapHashMap;
    }
}
