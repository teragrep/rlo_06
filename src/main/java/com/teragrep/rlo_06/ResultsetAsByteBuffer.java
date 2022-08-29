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

public class ResultsetAsByteBuffer {
    ParserResultset resultset;

    public ResultsetAsByteBuffer(ParserResultset resultset) {
        this.resultset = resultset;
    }

    public void setResultset(ParserResultset resultset) {
        this.resultset = resultset;
    }

    public ByteBuffer getPRIORITY() {
        return (ByteBuffer) this.resultset.PRIORITY.flip();
    }

    public ByteBuffer getVERSION() {
        return (ByteBuffer) this.resultset.VERSION.flip();
    }

    public ByteBuffer getTIMESTAMP() {
        return (ByteBuffer) this.resultset.TIMESTAMP.flip();
    }

    public ByteBuffer getHOSTNAME() {
        return (ByteBuffer) this.resultset.HOSTNAME.flip();
    }

    public ByteBuffer getAPPNAME() {
        return (ByteBuffer) this.resultset.APPNAME.flip();
    }

    public ByteBuffer getPROCID() {
        return (ByteBuffer) this.resultset.PROCID.flip();
    }

    public ByteBuffer getMSGID() {
        return (ByteBuffer) this.resultset.MSGID.flip();
    }

    public ByteBuffer getMSG() {
        return (ByteBuffer) this.resultset.MSG.flip();
    }

    public ByteBuffer getSdValue(ByteBuffer sdIdByteBuffer, ByteBuffer sdElemByteBuffer) {
        // NOTE sdIdByteBuffer and sdElemByteBuffer needs to be flipped to read when calling this
        if (this.resultset.sdSubscription.containsKey(sdIdByteBuffer)) {
            if (this.resultset.sdSubscription.get(sdIdByteBuffer).containsKey(sdElemByteBuffer)) {
                return (ByteBuffer) this.resultset.sdSubscription.get(sdIdByteBuffer).get(sdElemByteBuffer).flip();
            }
        }
        return null;
    }
}
