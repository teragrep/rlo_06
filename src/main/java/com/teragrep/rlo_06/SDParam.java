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

import java.util.function.Consumer;

public final class SDParam implements Consumer<Stream>, Clearable {

    public final Fragment sdParamKey;
    public final Fragment sdParamValue;

    private FragmentState fragmentState;
    private final Fragment stubFragment;

    SDParam() {
        this.sdParamKey = new Fragment(32, new SDParamKeyFunction());
        this.sdParamValue = new Fragment(8 * 1024, new SDParamValueFunction());
        this.fragmentState = FragmentState.EMPTY;
        this.stubFragment = new Fragment();
    }

    @Override
    public void accept(Stream stream) {
        if (fragmentState != FragmentState.EMPTY) {
            throw new IllegalStateException("fragmentState != FragmentState.EMPTY");
        }
        byte b;
        // check if we are interested in this sdId at all or skip to next sdId block
        sdParamKey.accept(stream);

        b = stream.get();
        if (b != 61) { // '='
            throw new StructuredDataParseException("EQ missing after SD_KEY or SD_KEY too long");
        }
        sdParamValue.accept(stream);

        // take next one for the while to check if ' ' or if to break it
        if (!stream.next()) {
            throw new StructuredDataParseException("SD is too short, can't continue");
        }
        fragmentState = FragmentState.WRITTEN;
    }

    @Override
    public void clear() {
        sdParamKey.clear();
        sdParamValue.clear();
        fragmentState = FragmentState.EMPTY;
    }

    public Fragment getSDParamValue(SDVector sdVector) {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }
        Fragment rv = stubFragment;
        if (sdParamKey.matches(sdVector.sdParamKeyBB)) {
            rv = sdParamValue;
        }
        return rv;
    }

    @Override
    public String toString() {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }
        return "SDParam{" + "sdParamKey=" + sdParamKey + ", sdParamValue=" + sdParamValue + '}';
    }
}
