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

public final class SDElement implements Consumer<Stream>, Clearable {

    public final Fragment sdElementId;
    public final List<SDParam> sdParams;

    private final SDParamCache sdParamCache;

    private FragmentState fragmentState;

    private final Fragment stubFragment;

    SDElement() {
        int numElements = 16;
        this.sdElementId = new Fragment(32, new SDElementIdFunction());
        this.sdParams = new ArrayList<>(numElements);
        this.sdParamCache = new SDParamCache(numElements);
        this.fragmentState = FragmentState.EMPTY;
        this.stubFragment = new Fragment();
    }

    // structured data, oh wow the performance hit
    @Override
    public void accept(Stream stream) {
        if (fragmentState != FragmentState.EMPTY) {
            throw new IllegalStateException("fragmentState != FragmentState.EMPTY");
        }

        byte b;

        // parse the sdId
        sdElementId.accept(stream);
        b = stream.get();

        while (b == 32) { // multiple ' ' separated sdKey="sdValue" pairs may exist
            SDParam sdParam = sdParamCache.take();
            sdParam.accept(stream);
            sdParams.add(sdParam);
            b = stream.get();
        }

        if (b == 93) { // ']', sdId only here: Payload:'[ID_A@1]' or Payload:'[ID_A@1][ID_B@1]'
            // MSG may not exist, no \n either, Parsing may be complete. get sets this.returnAfter to false
            // Total payload: '<14>1 2015-06-20T09:14:07.12345+00:00 host02 serverd DEA MSG-01 [ID_A@1]'
        }
        else {
            throw new StructuredDataParseException("SP missing after SD_ID or SD_ID too long");
        }
        fragmentState = FragmentState.WRITTEN;
    }

    @Override
    public void clear() {
        sdElementId.clear();
        for (SDParam sdParam : sdParams) {
            // cache clears
            sdParamCache.put(sdParam);
        }
        sdParams.clear();
        fragmentState = FragmentState.EMPTY;
    }

    public Fragment getSDParamValue(SDVector sdVector) {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }
        Fragment rv = stubFragment;
        if (sdElementId.matches(sdVector.sdElementIdBB)) {
            ListIterator<SDParam> listIterator = sdParams.listIterator(sdParams.size());
            while (listIterator.hasPrevious()) {
                SDParam sdParam = listIterator.previous();
                rv = sdParam.getSDParamValue(sdVector);
                if (!rv.isStub) {
                    break;
                }
            }
        }
        return rv;
    }

    @Override
    public String toString() {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }
        return "SDElement{" + "sdElementId=" + sdElementId + ", sdParams=" + sdParams + '}';
    }
}
