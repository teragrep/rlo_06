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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

public final class StructuredData implements Consumer<Stream>, Clearable {
    /*
                                                                    |||||||||||||||||||||||||||||||||||
                                                                    vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvR
    <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n
    
    Actions: O^^^^^^O^OO_OO^OO_OOO^^^^^^O^OO_OO
    Payload:'[ID_A@1 u="3" e="t"][ID_B@2 n="9"]'
    States : |......%.%%.%%.%%.%%|......%.%%.%T
    
    NOTE this does not provide any proof what so ever if certain sdId exist or not, we are only interested
    in values if they exist.
    */

    /*
             v
    Payload:'[ID_A@1 u="3" e="t"][ID_B@2 n="9"] ' // sd exists
    Payload:'- ' // no sd
     */
    public final List<SDElement> sdElements;
    private final SDElementCache sdElementCache;

    private FragmentState fragmentState;

    private final Fragment stubFragment;

    StructuredData() {
        int numElements = 16;
        this.sdElementCache = new SDElementCache(numElements);
        this.sdElements = new ArrayList<>(numElements);
        this.fragmentState = FragmentState.EMPTY;
        this.stubFragment = new Fragment();
    }

    @Override
    public void accept(Stream stream) {
        if (fragmentState != FragmentState.EMPTY) {
            throw new IllegalStateException("fragmentState != FragmentState.EMPTY");
        }

        byte b;

        if (!stream.next()) {
            throw new StructuredDataParseException("Expected SD, received nothing");
        }
        b = stream.get();

        /*
         NOTE: here the SD parser may slip into the message, how dirty of it but
         as "-xyz" or "- xyz" or "]xyz" or "] xyz" may exist we need to handle them here.
         */

        if (b == 45) {
            // if '-' then R(ead) and pass to next state

            if (!stream.next()) {
                throw new StructuredDataParseException("SD is too short, can't continue");
            }
        }
        else if (b == 91) {
            while (b == 91) { // '[' sd exists
                SDElement sdElement = sdElementCache.take();
                sdElement.accept(stream);
                sdElements.add(sdElement);
                /*
                                        vv            vv
                Payload:'[ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n'
                Payload:            '[ID_A@1] sigsegv\n'
                */

                if (!stream.next()) {
                    throw new StructuredDataParseException("SD is too short, can't continue");
                }
                b = stream.get(); // will it be '[' or the MSG who knows.
                // let's find out, note if not '[' then R(ead) and pass to next state
            }
        }
        else {
            throw new StructuredDataParseException("SD does not contain '-' or '['");
        }
        fragmentState = FragmentState.WRITTEN;
    }

    @Override
    public void clear() {
        for (SDElement sdElement : sdElements) {
            // cache clears and deallocates
            sdElementCache.put(sdElement);
        }
        sdElements.clear();
        fragmentState = FragmentState.EMPTY;
    }

    public Fragment getValue(SDVector sdVector) {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }

        // reverse search as last value is only that matters
        ListIterator<SDElement> listIterator = sdElements.listIterator(sdElements.size());
        Fragment rv = stubFragment;
        while (listIterator.hasPrevious()) {
            SDElement sdElement = listIterator.previous();
            rv = sdElement.getSDParamValue(sdVector);
            if (!rv.isStub) {
                break;
            }
        }
        return rv;
    }

    @Override
    public String toString() {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }
        return "StructuredData{" + "sdElements=" + sdElements + '}';
    }
}
