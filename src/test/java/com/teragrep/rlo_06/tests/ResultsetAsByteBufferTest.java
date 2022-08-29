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
package com.teragrep.rlo_06.tests;

import com.teragrep.rlo_06.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResultsetAsByteBufferTest {

    @Test
    public void read() throws IOException {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.TIMESTAMP);
        subscription.add(ParserEnum.MSG);

        // Structured
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();
        sdSubscription.subscribeElement("event_node_source@48577","source");
        sdSubscription.subscribeElement("event_node_relay@48577","source");
        sdSubscription.subscribeElement("event_node_source@48577","source_module");
        sdSubscription.subscribeElement("event_node_relay@48577","source_module");
        sdSubscription.subscribeElement("event_node_source@48577","hostname");
        sdSubscription.subscribeElement("event_node_relay@48577","hostname");

        // match string initializers, here to make them finals as optimized for instantiation
        byte[] ens = "event_node_source@48577".getBytes(StandardCharsets.US_ASCII);
        // subscribed fields as bytebuffer
        ByteBuffer eventNodeSourceBB = ByteBuffer.allocateDirect(ens.length);
        eventNodeSourceBB.put(ens, 0, ens.length);
        eventNodeSourceBB.flip();

        byte[] enr = "event_node_relay@48577".getBytes(StandardCharsets.US_ASCII);
        ByteBuffer eventNodeRelayBB = ByteBuffer.allocateDirect(enr.length);
        eventNodeRelayBB.put(enr, 0, enr.length);
        eventNodeRelayBB.flip();

        byte[] sm = "source_module".getBytes(StandardCharsets.US_ASCII);
        ByteBuffer sourceModuleBB = ByteBuffer.allocateDirect(sm.length);
        sourceModuleBB.put(sm, 0, sm.length);
        sourceModuleBB.flip();

        byte[] hn = "hostname".getBytes(StandardCharsets.US_ASCII);
        ByteBuffer hostnameBB = ByteBuffer.allocateDirect(hn.length);
        hostnameBB.put(hn, 0, hn.length);
        hostnameBB.flip();

        byte[] sourceBytes = "source".getBytes(StandardCharsets.US_ASCII);
        ByteBuffer sourceBB = ByteBuffer.allocateDirect(sourceBytes.length);
        sourceBB.put(sourceBytes, 0, sourceBytes.length);
        sourceBB.flip();

        ParserResultset currentResultSet = new ParserResultset(subscription, sdSubscription);

        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"][event_id@48577 hostname=\"sc-99-99-14-247\" uuid=\"0FD92E51B37748EB90CD894CCEE63907\" unixtime=\"1612047600.0\" id_source=\"source\"][event_node_source@48577 hostname=\"sc-99-99-14-247\" source=\"f17_ssmis_20210131v7.nc\" source_module=\"imfile\"][event_node_relay@48577 hostname=\"localhost\" source=\"sc-99-99-14-247\" source_module=\"imrelp\"][event_version@48577 major=\"2\" minor=\"2\" hostname=\"localhost\" version_source=\"relay\"][event_node_router@48577 source=\"logrouter.example.com\" source_module=\"imrelp\" hostname=\"localhost\"][teragrep@48577 streamname=\"log:f17:0\" directory=\"com_teragrep_audit\" unixtime=\"1612047600.0\"] msg\n";
        InputStream inputStream = new ByteArrayInputStream( (SYSLOG_MESSAGE).getBytes());

        RFC5424Parser parser = new RFC5424Parser(inputStream);

        parser.next(currentResultSet);

        ByteBuffer sourceStringBB = ByteBuffer.allocateDirect(8*1024 + 1 + 8*1024 + 1 + 8*1024);

        ResultsetAsByteBuffer resultsetAsByteBuffer = new ResultsetAsByteBuffer(null);

        resultsetAsByteBuffer.setResultset(currentResultSet);

        ByteBuffer source_module = resultsetAsByteBuffer.getSdValue(eventNodeSourceBB, sourceModuleBB);
        if(source_module == null || !source_module.hasRemaining()){
            source_module = resultsetAsByteBuffer.getSdValue(eventNodeRelayBB, sourceModuleBB);
        }
        ByteBuffer hostname = resultsetAsByteBuffer.getSdValue(eventNodeSourceBB, hostnameBB);
        if(hostname == null || !hostname.hasRemaining()){
            hostname = resultsetAsByteBuffer.getSdValue(eventNodeRelayBB, hostnameBB);
        }
        ByteBuffer source = resultsetAsByteBuffer.getSdValue(eventNodeSourceBB, sourceBB);
        if(source == null || !source.hasRemaining()){
            source = resultsetAsByteBuffer.getSdValue(eventNodeRelayBB, sourceBB);
        }


        // sm:hn:s
        sourceStringBB.clear();
        // source_module:hostname:source"
        if(source_module != null) {
            sourceStringBB.put(source_module);
        }
        sourceStringBB.put((byte) ':');
        if (hostname != null) {
            sourceStringBB.put(hostname);
        }
        sourceStringBB.put((byte)':');
        if (source != null) {
            sourceStringBB.put(source);
        }

        sourceStringBB.flip();
        assertEquals(
                "imfile:sc-99-99-14-247:f17_ssmis_20210131v7.nc",
                StandardCharsets.US_ASCII.decode(sourceStringBB).toString()
        );
    }
}
