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

import java.io.*;


import static org.junit.jupiter.api.Assertions.*;

public class SyntaxTest {

    @Test
    void testMultipleSyntax() throws Exception {
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.123456+00:00 host01 systemd DEA MSG-01 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"] sigsegv\n";
        String SYSLOG_MESSAGE2 = "<31>1 2021-12-24T09:14:07.12345+00:00 host02 journald MOI ASD-05 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"] normal\n";

        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();

        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        sdSubscription.subscribeElement("ID_A@1", "u");


        InputStream inputStream = new ByteArrayInputStream((SYSLOG_MESSAGE + SYSLOG_MESSAGE2).getBytes());
        RFC5424Frame parser = new RFC5424Frame(subscription, sdSubscription, inputStream);

        int count = 1;
        for (int i = 0; i < count; i++) {
            Assertions.assertTrue(parser.next());
            ResultSetAsString strings1 = new ResultSetAsString(parser.get());

            Assertions.assertEquals("14", strings1.getPriority());
            Assertions.assertEquals("1", strings1.getVersion());
            Assertions.assertEquals("2014-06-20T09:14:07.123456+00:00", strings1.getTimestamp());
            Assertions.assertEquals("host01", strings1.getHostname());
            Assertions.assertEquals("systemd", strings1.getAppname());
            Assertions.assertEquals("DEA", strings1.getProcid());
            Assertions.assertEquals("MSG-01", strings1.getMsgid());
            Assertions.assertEquals("sigsegv", strings1.getMsg());


            // Structured Data 1
            Assertions.assertEquals("\\\"3", strings1.getSdValue("ID_A@1", "u"));

            Assertions.assertTrue(parser.next());
           strings1 = new ResultSetAsString(parser.get());

            // Message 2
            Assertions.assertEquals("31", strings1.getPriority());
            Assertions.assertEquals("1", strings1.getVersion());
            Assertions.assertEquals("2021-12-24T09:14:07.12345+00:00", strings1.getTimestamp());
            //Instant instant = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(strings1.getTimestamp(), Instant::from);
            //System.out.println("TIMESTAMP INSTANT: >" + instant + "<");
            //System.out.println("TIMESTAMP CONVERTED: >" + Timestamp.from(instant) + "<");
            Assertions.assertEquals("host02", strings1.getHostname());
            Assertions.assertEquals("journald", strings1.getAppname());
            Assertions.assertEquals("MOI", strings1.getProcid());
            Assertions.assertEquals("ASD-05", strings1.getMsgid());
            Assertions.assertEquals("normal", strings1.getMsg());


            // Structured Data 2
            Assertions.assertEquals("\\\"3", strings1.getSdValue("ID_A@1", "u"));

            assertFalse(parser.next());
           strings1 = new ResultSetAsString(parser.get());

            // Finished
            Assertions.assertEquals("", strings1.getPriority());
            Assertions.assertEquals("", strings1.getVersion());
            Assertions.assertEquals("", strings1.getTimestamp());
            Assertions.assertEquals("", strings1.getHostname());
            Assertions.assertEquals("", strings1.getAppname());
            Assertions.assertEquals("", strings1.getProcid());
            Assertions.assertEquals("", strings1.getMsgid());
            Assertions.assertEquals("", strings1.getMsg());

            // Structured Data Finished
            Assertions.assertEquals("", strings1.getSdValue("ID_A@1", "u"));

            inputStream.reset();
        }
    }

    @Test
    void testNoNewLineEOF() throws Exception {
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"] sigsegv";

        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();

        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        sdSubscription.subscribeElement("ID_A@1", "u");

        InputStream inputStream = new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes());
        RFC5424Frame parser = new RFC5424Frame(subscription, sdSubscription, inputStream);

        int count = 1;
        for (int i = 0; i < count; i++) {
            Assertions.assertTrue(parser.next());
            ResultSetAsString strings1 = new ResultSetAsString(parser.get());

            // Message 1
            Assertions.assertEquals("14", strings1.getPriority());
            Assertions.assertEquals("1", strings1.getVersion());
            Assertions.assertEquals("2014-06-20T09:14:07.12345+00:00", strings1.getTimestamp());
            Assertions.assertEquals("host01", strings1.getHostname());
            Assertions.assertEquals("systemd", strings1.getAppname());
            Assertions.assertEquals("DEA", strings1.getProcid());
            Assertions.assertEquals("MSG-01", strings1.getMsgid());
            Assertions.assertEquals("sigsegv", strings1.getMsg());

            // Structured Data 1
            Assertions.assertEquals("\\\"3", strings1.getSdValue("ID_A@1", "u"));

            assertFalse(parser.next());
           strings1 = new ResultSetAsString(parser.get());

            // Message Finished
            Assertions.assertEquals("", strings1.getPriority());
            Assertions.assertEquals("", strings1.getVersion());
            Assertions.assertEquals("", strings1.getTimestamp());
            Assertions.assertEquals("", strings1.getHostname());
            Assertions.assertEquals("", strings1.getAppname());
            Assertions.assertEquals("", strings1.getProcid());
            Assertions.assertEquals("", strings1.getMsgid());
            Assertions.assertEquals("", strings1.getMsg());

            // Structured Data Finished
            Assertions.assertEquals("", strings1.getSdValue("ID_A@1", "u"));

            inputStream.reset();
        }
    }

    @Test
    void testTeragrepStructuredElement() throws Exception {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.TIMESTAMP);
        subscription.add(ParserEnum.HOSTNAME);
        subscription.add(ParserEnum.PROCID);
        subscription.add(ParserEnum.MSGID);
        subscription.add(ParserEnum.MSG);

        // Structured
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();
        sdSubscription.subscribeElement("event_node_source@48577", "source");
        sdSubscription.subscribeElement("event_node_source@48577", "source_module");
        sdSubscription.subscribeElement("event_node_source@48577", "hostname");
        sdSubscription.subscribeElement("event_node_relay@48577", "source");
        sdSubscription.subscribeElement("event_node_relay@48577", "source_module");
        sdSubscription.subscribeElement("event_node_relay@48577", "hostname");
        // [teragrep@48577 streamname="log:f17:0" directory="com_teragrep_audit" unixtime="1584572400.0"]
        sdSubscription.subscribeElement("teragrep@48577", "streamname");
        sdSubscription.subscribeElement("teragrep@48577", "directory");
        sdSubscription.subscribeElement("teragrep@48577", "unixtime");

        final File logFile = new File("src/test/resources/event.log");
        final InputStream inputStream = new BufferedInputStream(new FileInputStream(logFile), 32 * 1024 * 1024);
        final InputStream inputStream2 = new BufferedInputStream(new FileInputStream(logFile), 32 * 1024 * 1024);
        RFC5424Frame parser = new RFC5424Frame(subscription, sdSubscription, inputStream);

        Assertions.assertTrue(parser.next());
        ResultSetAsString strings1 = new ResultSetAsString(parser.get());

        // Teragrep structured
        Assertions.assertEquals("2020-03-19T01:00:00+00:00", strings1.getTimestamp());
        Assertions.assertEquals("sc-99-99-14-25", strings1.getHostname());
        Assertions.assertEquals("-", strings1.getProcid());
        Assertions.assertEquals("-", strings1.getMsgid());
        Assertions.assertEquals("{\"rainfall_rate\": 0.0, \"wind_speed\": 8.0, \"atmosphere_water_vapor_content\": 4.800000190734863, \"atmosphere_cloud_liquid_water_content\": 0.029999997466802597, \"latitude\": -89.875, \"longitude\": 0.125}", strings1.getMsg());

        // event_node_source@48577
        Assertions.assertEquals("f17_ssmis_20200319v7.nc", strings1.getSdValue("event_node_source@48577", "source"));
        Assertions.assertEquals("imfile", strings1.getSdValue("event_node_source@48577", "source_module"));
        Assertions.assertEquals("sc-99-99-14-25", strings1.getSdValue("event_node_source@48577", "hostname"));

        // event_node_relay@48577
        Assertions.assertEquals("sc-99-99-14-25", strings1.getSdValue("event_node_relay@48577", "source"));
        Assertions.assertEquals("imrelp", strings1.getSdValue("event_node_relay@48577", "source_module"));
        Assertions.assertEquals("localhost", strings1.getSdValue("event_node_relay@48577", "hostname"));
        // teragrep@48577
        Assertions.assertEquals("log:f17:0", strings1.getSdValue("teragrep@48577", "streamname"));
        Assertions.assertEquals("com_teragrep_audit", strings1.getSdValue("teragrep@48577", "directory"));
        Assertions.assertEquals("1584572400.0", strings1.getSdValue("teragrep@48577", "unixtime"));
        // Message Finished
    }

    @Test
    void consecutiveNoNewLine() throws Exception {
        String SYSLOG_MESSAGE = "<46>1 2021-03-18T12:29:36.842898+02:00 logsource.example.com rsyslogd-pstats - - [event_id@48577 hostname=\"logsource.example.com\" uuid=\"80AA765156F34854B9806BC69FF68659\" unixtime=\"1616063376\" id_source=\"source\"][event_format@48577 original_format=\"rfc5424\"][event_node_relay@48577 hostname=\"logrelay.example.com\" source=\"172.17.254.29\" source_module=\"imudp\"][event_version@48577 major=\"2\" minor=\"2\" hostname=\"logrelay.example.com\" version_source=\"relay\"][event_node_router@48577 source=\"172.17.254.16\" source_module=\"imrelp\" hostname=\"logrouter.example.com\"][teragrep@48577 streamname=\"stats:impstats:0\" directory=\"rsyslogd-pstats\" unixtime=\"1616070576\"] {\"@timestamp\":\"2021-03-18T12:29:36.842898+02:00\",\"host\":\"logsource.example.com\",\"source-module\":\"impstats\", \"name\": \"tags-out\", \"origin\": \"dynstats.bucket\", \"values\": { } }";

        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();

        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        sdSubscription.subscribeElement("event_id@48577", "hostname");


        RFC5424Frame parser = new RFC5424Frame(subscription, sdSubscription);

        int count = 2;
        for (int i = 0; i < count; i++) {
            parser.setInputStream(new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes()));

            Assertions.assertTrue(parser.next());
            ResultSetAsString strings1 = new ResultSetAsString(parser.get());

            // Message 1
            Assertions.assertEquals("46", strings1.getPriority());
            Assertions.assertEquals("1", strings1.getVersion());
            Assertions.assertEquals("2021-03-18T12:29:36.842898+02:00", strings1.getTimestamp());
            Assertions.assertEquals("logsource.example.com", strings1.getHostname());
            Assertions.assertEquals("rsyslogd-pstats", strings1.getAppname());
            Assertions.assertEquals("-", strings1.getProcid());
            Assertions.assertEquals("-", strings1.getMsgid());
            Assertions.assertEquals("{\"@timestamp\":\"2021-03-18T12:29:36.842898+02:00\",\"host\":\"logsource.example.com\",\"source-module\":\"impstats\", \"name\": \"tags-out\", \"origin\": \"dynstats.bucket\", \"values\": { } }", strings1.getMsg());

            // Structured Data 1
            Assertions.assertEquals("logsource.example.com", strings1.getSdValue("event_id@48577", "hostname"));
        }

        // finally empty
        assertFalse(parser.next());
        ResultSetAsString strings1 = new ResultSetAsString(parser.get());

        // Message Finished
        Assertions.assertEquals("", strings1.getPriority());
        Assertions.assertEquals("", strings1.getVersion());
        Assertions.assertEquals("", strings1.getTimestamp());
        Assertions.assertEquals("", strings1.getHostname());
        Assertions.assertEquals("", strings1.getAppname());
        Assertions.assertEquals("", strings1.getProcid());
        Assertions.assertEquals("", strings1.getMsgid());
        Assertions.assertEquals("", strings1.getMsg());

        // Structured Data Finished
        Assertions.assertEquals("", strings1.getSdValue("event_id@48577", "hostname"));

    }

    @Test
    void consecutiveWithNewLine() throws Exception {
        String SYSLOG_MESSAGE = "<46>1 2021-03-25T15:14:09.449777+02:00 logsource.example.com rsyslogd-pstats - - [event_id@48577 hostname=\"logsource.example.com\" uuid=\"30AF2CD3C24F47C8BA687D56E0300246\" unixtime=\"1616678049\" id_source=\"source\"][event_format@48577 original_format=\"rfc5424\"][event_node_relay@48577 hostname=\"logrelay.example.com\" source=\"172.17.254.29\" source_module=\"imudp\"][event_version@48577 major=\"2\" minor=\"2\" hostname=\"logrelay.example.com\" version_source=\"relay\"][event_node_router@48577 source=\"172.17.254.16\" source_module=\"imrelp\" hostname=\"logrouter.example.com\"][teragrep@48577 streamname=\"stats:impstats:0\" directory=\"rsyslogd-pstats\" unixtime=\"1616685249\"] {\"@timestamp\":\"2021-03-25T15:14:09.449777+02:00\",\"host\":\"logsource.example.com\",\"source-module\":\"impstats\", \"name\": \"resource-usage\", \"origin\": \"impstats\", \"utime\": 693053726, \"stime\": 133593735, \"maxrss\": 4690828, \"minflt\": 46694808, \"majflt\": 0, \"inblock\": 122077416, \"oublock\": 123878288, \"nvcsw\": 7199, \"nivcsw\": 9287, \"openfiles\": 20 }\n";

        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();

        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        sdSubscription.subscribeElement("event_id@48577", "hostname");

        RFC5424Frame parser = new RFC5424Frame(subscription, sdSubscription);

        int count = 2;
        for (int i = 0; i < count; i++) {
            parser.setInputStream(new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes()));

            Assertions.assertTrue(parser.next());
            ResultSetAsString strings1 = new ResultSetAsString(parser.get());

            // Message 1
            Assertions.assertEquals("46", strings1.getPriority());
            Assertions.assertEquals("1", strings1.getVersion());
            Assertions.assertEquals("2021-03-25T15:14:09.449777+02:00", strings1.getTimestamp());
            Assertions.assertEquals("logsource.example.com", strings1.getHostname());
            Assertions.assertEquals("rsyslogd-pstats", strings1.getAppname());
            Assertions.assertEquals("-", strings1.getProcid());
            Assertions.assertEquals("-", strings1.getMsgid());
            Assertions.assertEquals("{\"@timestamp\":\"2021-03-25T15:14:09.449777+02:00\",\"host\":\"logsource.example.com\",\"source-module\":\"impstats\", \"name\": \"resource-usage\", \"origin\": \"impstats\", \"utime\": 693053726, \"stime\": 133593735, \"maxrss\": 4690828, \"minflt\": 46694808, \"majflt\": 0, \"inblock\": 122077416, \"oublock\": 123878288, \"nvcsw\": 7199, \"nivcsw\": 9287, \"openfiles\": 20 }", strings1.getMsg());

            // Structured Data 1
            Assertions.assertEquals("logsource.example.com", strings1.getSdValue("event_id@48577", "hostname"));
        }

        // finally empty
        assertFalse(parser.next());
        ResultSetAsString strings1 = new ResultSetAsString(parser.get());

        // Message Finished
        Assertions.assertEquals("", strings1.getPriority());
        Assertions.assertEquals("", strings1.getVersion());
        Assertions.assertEquals("", strings1.getTimestamp());
        Assertions.assertEquals("", strings1.getHostname());
        Assertions.assertEquals("", strings1.getAppname());
        Assertions.assertEquals("", strings1.getProcid());
        Assertions.assertEquals("", strings1.getMsgid());
        Assertions.assertEquals("", strings1.getMsg());


        // Structured Data Finished
        Assertions.assertEquals("", strings1.getSdValue("event_id@48577", "hostname"));

    }

    @Test
    public void testNoSd() throws IOException {
        String SYSLOG_MESSAGE = "<134>1 2019-03-08T14:00:00+02:00 host-1-2-3-4 app-tag - - -  1.2.3.4 - - [08/Mar/2019:14:00:00 +0200] \"POST /idt/device/";

        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();

        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        //sdSubscription.subscribeElement("ID_A@1","u");

        InputStream inputStream = new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes());
        RFC5424Frame parser = new RFC5424Frame(subscription, sdSubscription, inputStream);

        int count = 1;
        for (int i = 0; i < count; i++) {
            Assertions.assertTrue(parser.next());
            ResultSetAsString strings1 = new ResultSetAsString(parser.get());

            Assertions.assertEquals("134", strings1.getPriority());
            Assertions.assertEquals("1", strings1.getVersion());
            Assertions.assertEquals("2019-03-08T14:00:00+02:00", strings1.getTimestamp());
            Assertions.assertEquals("host-1-2-3-4", strings1.getHostname());
            Assertions.assertEquals("app-tag", strings1.getAppname());
            Assertions.assertEquals("-", strings1.getProcid());
            Assertions.assertEquals("-", strings1.getMsgid());
            Assertions.assertEquals(" 1.2.3.4 - - [08/Mar/2019:14:00:00 +0200] \"POST /idt/device/", strings1.getMsg());

            assertFalse(parser.next());

            inputStream.reset();
        }
    }

    @Test
    public void brokenSDElemWorkaroundTest() throws IOException {
        String SYSLOG_MESSAGE = "<15>1 2021-11-10T12:46:33+02:00 HOST01A  PROD01A - [event_id@48577 hostname=\"somehostname.tld\" uuid=\"4849E84B6C1C42C09551DC06F4D7F4AE\" unixtime=\"1636548393\" id_source=\"relay\"][rfc3164@48577 syslogtag=\"[i][be][broken][sdelem]\"][event_format@48577 original_format=\"rfc3164\"][event_node_relay@48577 hostname=\"relay.somedomain.tld\" source=\"gateway\" source_module=\"imptcp\"][event_version@48577 major=\"2\" minor=\"2\" hostname=\"relay.somedomain.tld\" version_source=\"relay\"][event_node_router@48577 source=\"127.1.2.3\" source_module=\"imrelp\" hostname=\"route.somedomain.tld\"][teragrep@48577 streamname=\"on:two:messages:0\" directory=\"host_log_data\" unixtime=\"1636548394\"]  source-http <snip>";
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();

        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        /*
         there is an illegal field in syslogtag containing brackets
         it is fixed by subscribing to it.
         */
        sdSubscription.subscribeElement("rfc3164@48577", "syslogtag");


        InputStream inputStream = new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes());
        RFC5424Frame parser = new RFC5424Frame(subscription, sdSubscription, inputStream, false);

        int count = 1;
        for (int i = 0; i < count; i++) {
            Assertions.assertTrue(parser.next());
            ResultSetAsString strings1 = new ResultSetAsString(parser.get());

            Assertions.assertEquals("15", strings1.getPriority());
            Assertions.assertEquals("1", strings1.getVersion());
            Assertions.assertEquals("2021-11-10T12:46:33+02:00", strings1.getTimestamp());
            Assertions.assertEquals("HOST01A", strings1.getHostname());
            Assertions.assertEquals("", strings1.getAppname());
            Assertions.assertEquals("PROD01A", strings1.getProcid());
            Assertions.assertEquals("-", strings1.getMsgid());
            Assertions.assertEquals(" source-http <snip>", strings1.getMsg());

            assertFalse(parser.next());
            

            inputStream.reset();
        }
    }

    @Test
    public void noSDTest() throws IOException {
        String SYSLOG_MESSAGE = "<15>1 2019-05-29T15:00:00+03:00 PROD03A  PRODA - -  http(Worker1\n";

        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();

        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();


        InputStream inputStream = new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes());
        RFC5424Frame parser = new RFC5424Frame(subscription, sdSubscription, inputStream);

        int count = 1;
        for (int i = 0; i < count; i++) {
            Assertions.assertTrue(parser.next());
            ResultSetAsString strings1 = new ResultSetAsString(parser.get());

            Assertions.assertEquals("15", strings1.getPriority());
            Assertions.assertEquals("1", strings1.getVersion());
            Assertions.assertEquals("2019-05-29T15:00:00+03:00", strings1.getTimestamp());
            Assertions.assertEquals("PROD03A", strings1.getHostname());
            Assertions.assertEquals("", strings1.getAppname());
            Assertions.assertEquals("PRODA", strings1.getProcid());
            Assertions.assertEquals("-", strings1.getMsgid());
            Assertions.assertEquals(" http(Worker1", strings1.getMsg());

            assertFalse(parser.next());

            inputStream.reset();
        }
    }

    @Test
    void consecutiveMoSDTest() throws Exception {
        String SYSLOG_MESSAGE = "<15>1 2019-05-29T15:00:00+03:00 PROD03A  PRODA - -  http(Worker1\n";

        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();

        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        sdSubscription.subscribeElement("event_id@48577", "hostname");

        RFC5424Frame parser = new RFC5424Frame(subscription, sdSubscription);

        int count = 2;
        for (int i = 0; i < count; i++) {
            parser.setInputStream(new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes()));

            Assertions.assertTrue(parser.next());
            ResultSetAsString strings1 = new ResultSetAsString(parser.get());

            // Message 1
            Assertions.assertEquals("15", strings1.getPriority());
            Assertions.assertEquals("1", strings1.getVersion());
            Assertions.assertEquals("2019-05-29T15:00:00+03:00", strings1.getTimestamp());
            Assertions.assertEquals("PROD03A", strings1.getHostname());
            Assertions.assertEquals("", strings1.getAppname());
            Assertions.assertEquals("PRODA", strings1.getProcid());
            Assertions.assertEquals("-", strings1.getMsgid());
            Assertions.assertEquals(" http(Worker1", strings1.getMsg());
        }

        // finally empty
        assertFalse(parser.next());
        ResultSetAsString strings1 = new ResultSetAsString(parser.get());

        // Message Finished
        Assertions.assertEquals("", strings1.getPriority());
        Assertions.assertEquals("", strings1.getVersion());
        Assertions.assertEquals("", strings1.getTimestamp());
        Assertions.assertEquals("", strings1.getHostname());
        Assertions.assertEquals("", strings1.getAppname());
        Assertions.assertEquals("", strings1.getProcid());
        Assertions.assertEquals("", strings1.getMsgid());
        Assertions.assertEquals("", strings1.getMsg());
    }

    @Test
    void multipleNewlinesInMsg() throws Exception {
        String SYSLOG_MESSAGE = "<14>1 2022-12-13T14:41:29.715Z test-stream 9627df7a-testi - - - Testing text.\ntest\ning.\n";

        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();

        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();
        sdSubscription.subscribeElement("event_id@48577", "hostname");

        RFC5424Frame parser = new RFC5424Frame(subscription, sdSubscription, false);

        int count = 1;
        for (int i = 0; i < count; i++) {
            parser.setInputStream(new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes()));

            Assertions.assertTrue(parser.next());
            ResultSetAsString strings1 = new ResultSetAsString(parser.get());

            // Message 1
            Assertions.assertEquals("14", strings1.getPriority());
            Assertions.assertEquals("1", strings1.getVersion());
            Assertions.assertEquals("2022-12-13T14:41:29.715Z", strings1.getTimestamp());
            Assertions.assertEquals("test-stream", strings1.getHostname());
            Assertions.assertEquals("9627df7a-testi", strings1.getAppname());
            Assertions.assertEquals("-", strings1.getProcid());
            Assertions.assertEquals("-", strings1.getMsgid());
            Assertions.assertEquals("Testing text.\ntest\ning.\n", strings1.getMsg());
        }

        // finally empty
        assertFalse(parser.next());
        ResultSetAsString strings1 = new ResultSetAsString(parser.get());

        // Message Finished
        Assertions.assertEquals("", strings1.getPriority());
        Assertions.assertEquals("", strings1.getVersion());
        Assertions.assertEquals("", strings1.getTimestamp());
        Assertions.assertEquals("", strings1.getHostname());
        Assertions.assertEquals("", strings1.getAppname());
        Assertions.assertEquals("", strings1.getProcid());
        Assertions.assertEquals("", strings1.getMsgid());
        Assertions.assertEquals("", strings1.getMsg());
    }
}
