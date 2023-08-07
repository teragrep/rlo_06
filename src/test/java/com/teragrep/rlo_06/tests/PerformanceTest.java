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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;

public class PerformanceTest {

    @Test
    @DisabledIfSystemProperty(named = "noPerfTest", matches = "true")
    void testLongPayloadPerformance() throws Exception {
        String payload = new String(new char[128*1000]).replace("\0", "X");
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"][event_id@48577 hostname=\"sc-99-99-14-247\" uuid=\"0FD92E51B37748EB90CD894CCEE63907\" unixtime=\"1612047600.0\" id_source=\"source\"][event_node_source@48577 hostname=\"sc-99-99-14-247\" source=\"f17_ssmis_20210131v7.nc\" source_module=\"imfile\"][event_node_relay@48577 hostname=\"localhost\" source=\"sc-99-99-14-247\" source_module=\"imrelp\"][event_version@48577 major=\"2\" minor=\"2\" hostname=\"localhost\" version_source=\"relay\"][event_node_router@48577 source=\"logrouter.example.com\" source_module=\"imrelp\" hostname=\"localhost\"][teragrep@48577 streamname=\"log:f17:0\" directory=\"com_teragrep_audit\" unixtime=\"1612047600.0\"] " + payload + "\n";
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();

        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        sdSubscription.subscribeElement("ID_A@1","u");

        InputStream inputStream = new ByteArrayInputStream( SYSLOG_MESSAGE.getBytes());
        RFC5424Parser parser = new RFC5424Parser(subscription, sdSubscription, inputStream);



        Instant instant1 = Instant.now();
        long count = 50000;
        for (long i = 0; i < count; i++) {
            Assertions.assertTrue(parser.next());
            inputStream.reset();
        }
        Instant instant2 = Instant.now();
        long msgsize = (count * SYSLOG_MESSAGE.length())/1024/1024;
        long spent = instant2.toEpochMilli()-instant1.toEpochMilli();
        System.out.println("testLongPayloadPerformance: time taken " + spent + " for " + count +
                           ", total RPS: " + (float) count/ ((float) spent/1000) +
                           ", " + (float) msgsize + " megabytes (" + (float) (msgsize/((float)spent/1000)) + " MB/s)");
    }

    @Test
    @DisabledIfSystemProperty(named = "noPerfTest", matches = "true")
    void testShortPayloadPerformance() throws Exception {

        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"][event_id@48577 hostname=\"sc-99-99-14-247\" uuid=\"0FD92E51B37748EB90CD894CCEE63907\" unixtime=\"1612047600.0\" id_source=\"source\"][event_node_source@48577 hostname=\"sc-99-99-14-247\" source=\"f17_ssmis_20210131v7.nc\" source_module=\"imfile\"][event_node_relay@48577 hostname=\"localhost\" source=\"sc-99-99-14-247\" source_module=\"imrelp\"][event_version@48577 major=\"2\" minor=\"2\" hostname=\"localhost\" version_source=\"relay\"][event_node_router@48577 source=\"logrouter.example.com\" source_module=\"imrelp\" hostname=\"localhost\"][teragrep@48577 streamname=\"log:f17:0\" directory=\"com_teragrep_audit\" unixtime=\"1612047600.0\"] sigsegv\n";
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();

        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        sdSubscription.subscribeElement("ID_A@1","u");

        InputStream inputStream = new ByteArrayInputStream( SYSLOG_MESSAGE.getBytes());
        RFC5424Parser parser = new RFC5424Parser(subscription, sdSubscription, inputStream);


        Instant instant1 = Instant.now();
        long count = 10000000;
        for (long i = 0; i < count; i++) {
            Assertions.assertTrue(parser.next());
            inputStream.reset();
        }
        Instant instant2 = Instant.now();

        long msgsize = (count * SYSLOG_MESSAGE.length())/1024/1024;
        long spent = instant2.toEpochMilli()-instant1.toEpochMilli();
        System.out.println("testShortPayloadPerformance: time taken " + spent + " for " + count +
                ", total RPS: " + (float) count/ ((float) spent/1000) +
                ", " + (float) msgsize + " megabytes (" + (float) (msgsize/((float)spent/1000)) + " MB/s)");

    }

    @Test
    @DisabledIfSystemProperty(named = "noPerfTest", matches = "true")
    void testOnlyMsgSub() throws Exception {

        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"][event_id@48577 hostname=\"sc-99-99-14-247\" uuid=\"0FD92E51B37748EB90CD894CCEE63907\" unixtime=\"1612047600.0\" id_source=\"source\"][event_node_source@48577 hostname=\"sc-99-99-14-247\" source=\"f17_ssmis_20210131v7.nc\" source_module=\"imfile\"][event_node_relay@48577 hostname=\"localhost\" source=\"sc-99-99-14-247\" source_module=\"imrelp\"][event_version@48577 major=\"2\" minor=\"2\" hostname=\"localhost\" version_source=\"relay\"][event_node_router@48577 source=\"logrouter.example.com\" source_module=\"imrelp\" hostname=\"localhost\"][teragrep@48577 streamname=\"log:f17:0\" directory=\"com_teragrep_audit\" unixtime=\"1612047600.0\"] sigsegv\n";
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();
        subscription.add(ParserEnum.MSG);

        InputStream inputStream = new ByteArrayInputStream( SYSLOG_MESSAGE.getBytes());
        RFC5424Parser parser = new RFC5424Parser(subscription, sdSubscription, inputStream);


        Instant instant1 = Instant.now();
        long count = 10000000;
        for (long i = 0; i < count; i++) {
            Assertions.assertTrue(parser.next());
            inputStream.reset();
        }
        Instant instant2 = Instant.now();

        long msgsize = (count * SYSLOG_MESSAGE.length())/1024/1024;
        long spent = instant2.toEpochMilli()-instant1.toEpochMilli();
        System.out.println("testOnlyMsgSub: time taken " + spent + " for " + count +
                ", total RPS: " + (float) count/ ((float) spent/1000) +
                ", " + (float) msgsize + " megabytes (" + (float) (msgsize/((float)spent/1000)) + " MB/s)");

    }

    @Test
    @DisabledIfSystemProperty(named = "noPerfTest", matches = "true")
    void testNothingSub() throws Exception {

        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"][event_id@48577 hostname=\"sc-99-99-14-247\" uuid=\"0FD92E51B37748EB90CD894CCEE63907\" unixtime=\"1612047600.0\" id_source=\"source\"][event_node_source@48577 hostname=\"sc-99-99-14-247\" source=\"f17_ssmis_20210131v7.nc\" source_module=\"imfile\"][event_node_relay@48577 hostname=\"localhost\" source=\"sc-99-99-14-247\" source_module=\"imrelp\"][event_version@48577 major=\"2\" minor=\"2\" hostname=\"localhost\" version_source=\"relay\"][event_node_router@48577 source=\"logrouter.example.com\" source_module=\"imrelp\" hostname=\"localhost\"][teragrep@48577 streamname=\"log:f17:0\" directory=\"com_teragrep_audit\" unixtime=\"1612047600.0\"] sigsegv\n";
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();
        InputStream inputStream = new ByteArrayInputStream(SYSLOG_MESSAGE.getBytes());
        RFC5424Parser parser = new RFC5424Parser(subscription, sdSubscription, inputStream);

        Instant instant1 = Instant.now();
        long count = 10000000;
        for (long i = 0; i < count; i++) {
            Assertions.assertTrue(parser.next());
            inputStream.reset();
        }
        Instant instant2 = Instant.now();

        long msgsize = (count * SYSLOG_MESSAGE.length())/1024/1024;
        long spent = instant2.toEpochMilli()-instant1.toEpochMilli();
        System.out.println("testNothingSub: time taken " + spent + " for " + count +
                ", total RPS: " + (float) count/ ((float) spent/1000) +
                ", " + (float) msgsize + " megabytes (" + (float) (msgsize/((float)spent/1000)) + " MB/s)");

    }

    @Test
    @DisabledIfSystemProperty(named = "noPerfTest", matches = "true")
    void testDashesFullSub() throws Exception {

        String SYSLOG_MESSAGE = "<2>1  - - - - - ";
        InputStream inputStream = new ByteArrayInputStream( SYSLOG_MESSAGE.getBytes());
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();
        RFC5424Parser parser = new RFC5424Parser(subscription, sdSubscription, inputStream);

        Instant instant1 = Instant.now();
        long count = 100000000;
        for (long i = 0; i < count; i++) {
            Assertions.assertTrue(parser.next());
            inputStream.reset();
        }
        Instant instant2 = Instant.now();

        long msgsize = (count * SYSLOG_MESSAGE.length())/1024/1024;
        long spent = instant2.toEpochMilli()-instant1.toEpochMilli();
        System.out.println("testDashesFullSub: time taken " + spent + " for " + count +
                ", total RPS: " + (float) count/ ((float) spent/1000) +
                ", " + (float) msgsize + " megabytes (" + (float) (msgsize/((float)spent/1000)) + " MB/s)");

    }

    @Test
    @DisabledIfSystemProperty(named = "noPerfTest", matches = "true")
    void testDashesNoSub() throws Exception {

        String SYSLOG_MESSAGE = "<2>1  - - - - - ";
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        InputStream inputStream = new ByteArrayInputStream( SYSLOG_MESSAGE.getBytes());
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();
        RFC5424Parser parser = new RFC5424Parser(subscription, sdSubscription, inputStream);

        Instant instant1 = Instant.now();
        long count = 100000000;
        for (long i = 0; i < count; i++) {
            Assertions.assertTrue(parser.next());
            inputStream.reset();
        }
        Instant instant2 = Instant.now();

        long msgsize = (count * SYSLOG_MESSAGE.length())/1024/1024;
        long spent = instant2.toEpochMilli()-instant1.toEpochMilli();
        System.out.println("testDashesNoSub: time taken " + spent + " for " + count +
                ", total RPS: " + (float) count/ ((float) spent/1000) +
                ", " + (float) msgsize + " megabytes (" + (float) (msgsize/((float)spent/1000)) + " MB/s)");

    }
}
