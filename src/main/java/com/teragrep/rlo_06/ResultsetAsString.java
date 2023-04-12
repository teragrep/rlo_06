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

public class ResultsetAsString {

    ParserResultset resultset;

    public ResultsetAsString(ParserResultset resultset) {
        this.resultset = resultset;
    }

    public void setResultset(ParserResultset resultset) {
        this.resultset = resultset;
    }

    public String getPriority() {
        this.resultset.PRIORITY.flip();
        String priority = StandardCharsets.US_ASCII.decode(this.resultset.PRIORITY).toString();
        return priority;
    }

    public String getVersion() {
        this.resultset.VERSION.flip();
        String version = StandardCharsets.US_ASCII.decode(this.resultset.VERSION).toString();
        return version;
    }

    public String getTimestamp() {
        this.resultset.TIMESTAMP.flip();
        String ts = StandardCharsets.US_ASCII.decode(this.resultset.TIMESTAMP).toString();
        return ts;
    }

    public String getHostname() {
        this.resultset.HOSTNAME.flip();
        String hostname = StandardCharsets.US_ASCII.decode(this.resultset.HOSTNAME).toString();
        return hostname;
    }

    public String getAppname() {
        this.resultset.APPNAME.flip();
        String appname = StandardCharsets.US_ASCII.decode(this.resultset.APPNAME).toString();
        return appname;
    }

    public String getProcid() {
        this.resultset.PROCID.flip();
        String procid = StandardCharsets.US_ASCII.decode(this.resultset.PROCID).toString();
        return procid;
    }
    public String getMsgid() {
        this.resultset.MSGID.flip();
        String msgid = StandardCharsets.US_ASCII.decode(this.resultset.MSGID).toString();
        return msgid;
    }

    public String getMsg() {
        this.resultset.MSG.flip();
        String msg = StandardCharsets.UTF_8.decode(this.resultset.MSG).toString();
        return msg;
    }

    public String getSdValue(String sdId, String sdElement) {
        byte[] sdIdBytes = sdId.getBytes(StandardCharsets.US_ASCII);
        ByteBuffer sdIdByteBuffer = ByteBuffer.allocateDirect(sdIdBytes.length);
        sdIdByteBuffer.put(sdIdBytes, 0, sdIdBytes.length);
        sdIdByteBuffer.flip();

        if (this.resultset.sdSubscription.isSubscribedSDId(sdIdByteBuffer)) {
            byte[] sdElemBytes = sdElement.getBytes(StandardCharsets.US_ASCII);
            ByteBuffer sdElemByteBuffer = ByteBuffer.allocateDirect(sdElemBytes.length);
            sdElemByteBuffer.put(sdElemBytes, 0, sdElemBytes.length);
            sdElemByteBuffer.flip();

            if (this.resultset.sdSubscription.isSubscribedSDElement(sdIdByteBuffer, sdElemByteBuffer)) {
                ByteBuffer outBuffer = this.resultset.sdSubscription.getSubscribedSDElementBuffer(sdIdByteBuffer, sdElemByteBuffer);
                outBuffer.flip();
                return StandardCharsets.UTF_8.decode(outBuffer).toString();
            }
        }
        return null;
    }

    public HashMap<String, HashMap<String, String>> getAsMap() {
        // there might be much more optimal way of translating the map
        HashMap<String, HashMap<String, String>> sdSubscriptionStringMap = new HashMap<>();

        HashMap<ByteBuffer, HashMap<ByteBuffer, ByteBuffer>> sdSubscriptionMap = this.resultset.sdSubscription.getMap();

        for (ByteBuffer sdIdBB : sdSubscriptionMap.keySet()) {
            String sdIdString = StandardCharsets.US_ASCII.decode(sdIdBB).toString();
            sdIdBB.flip();
            HashMap<String, String> sdElemHashMap = new HashMap<>();
            sdSubscriptionStringMap.put(sdIdString, sdElemHashMap);

            for (ByteBuffer sdElemBB : sdSubscriptionMap.get(sdIdBB).keySet()) {
                String sdElemKeyString = StandardCharsets.US_ASCII.decode(sdElemBB).toString();
                sdElemBB.flip();
                ByteBuffer sdValBB = (ByteBuffer) sdSubscriptionMap.get(sdIdBB).get(sdElemBB).flip();
                String sdElemValString = StandardCharsets.UTF_8.decode(sdValBB).toString();
                sdElemHashMap.put(sdElemKeyString, sdElemValString);
            }
        }

        return sdSubscriptionStringMap;
    }
}
