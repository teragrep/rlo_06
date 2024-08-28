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
package com.teragrep.rlo_06.tests;

import com.teragrep.rlo_06.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class SyntaxTest {

    @Test
    void testMultipleSyntax() throws Exception {
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.123456+00:00 host01 systemd DEA MSG-01 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"] sigsegv\n";
        String SYSLOG_MESSAGE2 = "<31>1 2021-12-24T09:14:07.12345+00:00 host02 journald MOI ASD-05 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"] normal\n";

        InputStream inputStream = new ByteArrayInputStream((SYSLOG_MESSAGE + SYSLOG_MESSAGE2).getBytes());
        RFC5424Frame rfc5424Frame = new RFC5424Frame(true);
        rfc5424Frame.load(inputStream);

        int count = 1;
        for (int i = 0; i < count; i++) {
            Assertions.assertTrue(rfc5424Frame.next());

            Assertions.assertEquals("14", rfc5424Frame.priority.toString());
            Assertions.assertEquals("1", rfc5424Frame.version.toString());
            Assertions.assertEquals("2014-06-20T09:14:07.123456+00:00", rfc5424Frame.timestamp.toString());
            Assertions.assertEquals("host01", rfc5424Frame.hostname.toString());
            Assertions.assertEquals("systemd", rfc5424Frame.appName.toString());
            Assertions.assertEquals("DEA", rfc5424Frame.procId.toString());
            Assertions.assertEquals("MSG-01", rfc5424Frame.msgId.toString());
            Assertions.assertEquals("sigsegv", rfc5424Frame.msg.toString());

            // Structured Data 1
            SDVector sdVector = new SDVector("ID_A@1", "u");
            Assertions.assertEquals("\\\"3", rfc5424Frame.structuredData.getValue(sdVector).toString());

            Assertions.assertTrue(rfc5424Frame.next());

            // Message 2
            Assertions.assertEquals("31", rfc5424Frame.priority.toString());
            Assertions.assertEquals("1", rfc5424Frame.version.toString());
            Assertions.assertEquals("2021-12-24T09:14:07.12345+00:00", rfc5424Frame.timestamp.toString());
            //Instant instant = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(rfc5424Frame.timestamp.toString(), Instant::from);
            //System.out.println("TIMESTAMP INSTANT: >" + instant + "<");
            //System.out.println("TIMESTAMP CONVERTED: >" + Timestamp.from(instant) + "<");
            Assertions.assertEquals("host02", rfc5424Frame.hostname.toString());
            Assertions.assertEquals("journald", rfc5424Frame.appName.toString());
            Assertions.assertEquals("MOI", rfc5424Frame.procId.toString());
            Assertions.assertEquals("ASD-05", rfc5424Frame.msgId.toString());
            Assertions.assertEquals("normal", rfc5424Frame.msg.toString());

            // Structured Data 2
            Assertions
                    .assertEquals("\\\"3", rfc5424Frame.structuredData.getValue(new SDVector("ID_A@1", "u")).toString());

            assertFalse(rfc5424Frame.next());

            // Finished
            Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.priority::toString);
            Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.version::toString);
            Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.timestamp::toString);
            Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.hostname::toString);
            Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.appName::toString);
            Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.procId::toString);
            Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.msgId::toString);
            Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.msg::toString);

            // Structured Data Finished
            Assertions.assertThrows(IllegalStateException.class, () -> {
                rfc5424Frame.structuredData.getValue(new SDVector("ID_A@1", "u")).toString();
            });

            inputStream.reset();
        }
    }

    @Test
    void testNoNewLineEOF() throws Exception {
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"] sigsegv";
        InputStream inputStream = new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes());
        RFC5424Frame rfc5424Frame = new RFC5424Frame();
        rfc5424Frame.load(inputStream);

        int count = 1;
        for (int i = 0; i < count; i++) {
            Assertions.assertTrue(rfc5424Frame.next());

            // Message 1
            Assertions.assertEquals("14", rfc5424Frame.priority.toString());
            Assertions.assertEquals("1", rfc5424Frame.version.toString());
            Assertions.assertEquals("2014-06-20T09:14:07.12345+00:00", rfc5424Frame.timestamp.toString());
            Assertions.assertEquals("host01", rfc5424Frame.hostname.toString());
            Assertions.assertEquals("systemd", rfc5424Frame.appName.toString());
            Assertions.assertEquals("DEA", rfc5424Frame.procId.toString());
            Assertions.assertEquals("MSG-01", rfc5424Frame.msgId.toString());
            Assertions.assertEquals("sigsegv", rfc5424Frame.msg.toString());

            // Structured Data 1
            Assertions
                    .assertEquals("\\\"3", rfc5424Frame.structuredData.getValue(new SDVector("ID_A@1", "u")).toString());

            assertFalse(rfc5424Frame.next());

            // Message Finished
            Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.priority::toString);
            Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.version::toString);
            Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.timestamp::toString);
            Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.hostname::toString);
            Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.appName::toString);
            Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.procId::toString);
            Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.msgId::toString);
            Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.msg::toString);

            // Structured Data Finished
            assertThrows(IllegalStateException.class, () -> {
                rfc5424Frame.structuredData.getValue(new SDVector("ID_A@1", "u")).toString();
            });
            inputStream.reset();
        }
    }

    @Test
    void testTeragrepStructuredElement() throws Exception {
        final File logFile = new File("src/test/resources/event.log");
        final InputStream inputStream = new BufferedInputStream(
                Files.newInputStream(logFile.toPath()),
                32 * 1024 * 1024
        );
        RFC5424Frame rfc5424Frame = new RFC5424Frame(true);
        rfc5424Frame.load(inputStream);

        Assertions.assertTrue(rfc5424Frame.next());

        // Teragrep structured
        Assertions.assertEquals("2020-03-19T01:00:00+00:00", rfc5424Frame.timestamp.toString());
        Assertions.assertEquals("sc-99-99-14-25", rfc5424Frame.hostname.toString());
        Assertions.assertEquals("-", rfc5424Frame.procId.toString());
        Assertions.assertEquals("-", rfc5424Frame.msgId.toString());
        Assertions
                .assertEquals(
                        "{\"rainfall_rate\": 0.0, \"wind_speed\": 8.0, \"atmosphere_water_vapor_content\": 4.800000190734863, \"atmosphere_cloud_liquid_water_content\": 0.029999997466802597, \"latitude\": -89.875, \"longitude\": 0.125}",
                        rfc5424Frame.msg.toString()
                );

        // event_node_source@48577
        Assertions
                .assertEquals(
                        "f17_ssmis_20200319v7.nc", rfc5424Frame.structuredData
                                .getValue(new SDVector("event_node_source@48577", "source"))
                                .toString()
                );
        Assertions
                .assertEquals(
                        "imfile", rfc5424Frame.structuredData
                                .getValue(new SDVector("event_node_source@48577", "source_module"))
                                .toString()
                );
        Assertions
                .assertEquals(
                        "sc-99-99-14-25", rfc5424Frame.structuredData
                                .getValue(new SDVector("event_node_source@48577", "hostname"))
                                .toString()
                );

        // event_node_relay@48577
        Assertions
                .assertEquals(
                        "sc-99-99-14-25", rfc5424Frame.structuredData
                                .getValue(new SDVector("event_node_relay@48577", "source"))
                                .toString()
                );
        Assertions
                .assertEquals(
                        "imrelp", rfc5424Frame.structuredData
                                .getValue(new SDVector("event_node_relay@48577", "source_module"))
                                .toString()
                );
        Assertions
                .assertEquals(
                        "localhost", rfc5424Frame.structuredData
                                .getValue(new SDVector("event_node_relay@48577", "hostname"))
                                .toString()
                );
        // teragrep@48577
        Assertions
                .assertEquals(
                        "log:f17:0", rfc5424Frame.structuredData.getValue(new SDVector("teragrep@48577", "streamname")).toString()
                );
        Assertions
                .assertEquals(
                        "com_teragrep_audit",
                        rfc5424Frame.structuredData.getValue(new SDVector("teragrep@48577", "directory")).toString()
                );
        Assertions
                .assertEquals(
                        "1584572400.0", rfc5424Frame.structuredData.getValue(new SDVector("teragrep@48577", "unixtime")).toString()
                );
        // Message Finished
    }

    @Test
    void consecutiveNoNewLine() throws Exception {
        String SYSLOG_MESSAGE = "<46>1 2021-03-18T12:29:36.842898+02:00 logsource.example.com rsyslogd-pstats - - [event_id@48577 hostname=\"logsource.example.com\" uuid=\"80AA765156F34854B9806BC69FF68659\" unixtime=\"1616063376\" id_source=\"source\"][event_format@48577 original_format=\"rfc5424\"][event_node_relay@48577 hostname=\"logrelay.example.com\" source=\"172.17.254.29\" source_module=\"imudp\"][event_version@48577 major=\"2\" minor=\"2\" hostname=\"logrelay.example.com\" version_source=\"relay\"][event_node_router@48577 source=\"172.17.254.16\" source_module=\"imrelp\" hostname=\"logrouter.example.com\"][teragrep@48577 streamname=\"stats:impstats:0\" directory=\"rsyslogd-pstats\" unixtime=\"1616070576\"] {\"@timestamp\":\"2021-03-18T12:29:36.842898+02:00\",\"host\":\"logsource.example.com\",\"source-module\":\"impstats\", \"name\": \"tags-out\", \"origin\": \"dynstats.bucket\", \"values\": { } }";
        RFC5424Frame rfc5424Frame = new RFC5424Frame();

        int count = 2;
        for (int i = 0; i < count; i++) {
            rfc5424Frame.load(new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes()));

            Assertions.assertTrue(rfc5424Frame.next());

            // Message 1
            Assertions.assertEquals("46", rfc5424Frame.priority.toString());
            Assertions.assertEquals("1", rfc5424Frame.version.toString());
            Assertions.assertEquals("2021-03-18T12:29:36.842898+02:00", rfc5424Frame.timestamp.toString());
            Assertions.assertEquals("logsource.example.com", rfc5424Frame.hostname.toString());
            Assertions.assertEquals("rsyslogd-pstats", rfc5424Frame.appName.toString());
            Assertions.assertEquals("-", rfc5424Frame.procId.toString());
            Assertions.assertEquals("-", rfc5424Frame.msgId.toString());
            Assertions
                    .assertEquals(
                            "{\"@timestamp\":\"2021-03-18T12:29:36.842898+02:00\",\"host\":\"logsource.example.com\",\"source-module\":\"impstats\", \"name\": \"tags-out\", \"origin\": \"dynstats.bucket\", \"values\": { } }",
                            rfc5424Frame.msg.toString()
                    );

            // Structured Data 1
            Assertions
                    .assertEquals(
                            "logsource.example.com",
                            rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "hostname")).toString()
                    );
        }

        // finally empty
        assertFalse(rfc5424Frame.next());

        // Message Finished
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.priority::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.version::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.timestamp::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.hostname::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.appName::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.procId::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.msgId::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.msg::toString);

        // Structured Data Finished
        assertThrows(IllegalStateException.class, () -> {
            rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "hostname")).toString();
        });
    }

    @Test
    void consecutiveWithNewLine() throws Exception {
        String SYSLOG_MESSAGE = "<46>1 2021-03-25T15:14:09.449777+02:00 logsource.example.com rsyslogd-pstats - - [event_id@48577 hostname=\"logsource.example.com\" uuid=\"30AF2CD3C24F47C8BA687D56E0300246\" unixtime=\"1616678049\" id_source=\"source\"][event_format@48577 original_format=\"rfc5424\"][event_node_relay@48577 hostname=\"logrelay.example.com\" source=\"172.17.254.29\" source_module=\"imudp\"][event_version@48577 major=\"2\" minor=\"2\" hostname=\"logrelay.example.com\" version_source=\"relay\"][event_node_router@48577 source=\"172.17.254.16\" source_module=\"imrelp\" hostname=\"logrouter.example.com\"][teragrep@48577 streamname=\"stats:impstats:0\" directory=\"rsyslogd-pstats\" unixtime=\"1616685249\"] {\"@timestamp\":\"2021-03-25T15:14:09.449777+02:00\",\"host\":\"logsource.example.com\",\"source-module\":\"impstats\", \"name\": \"resource-usage\", \"origin\": \"impstats\", \"utime\": 693053726, \"stime\": 133593735, \"maxrss\": 4690828, \"minflt\": 46694808, \"majflt\": 0, \"inblock\": 122077416, \"oublock\": 123878288, \"nvcsw\": 7199, \"nivcsw\": 9287, \"openfiles\": 20 }\n";
        RFC5424Frame rfc5424Frame = new RFC5424Frame(true);

        int count = 2;
        for (int i = 0; i < count; i++) {
            rfc5424Frame.load(new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes()));

            Assertions.assertTrue(rfc5424Frame.next());

            // Message 1
            Assertions.assertEquals("46", rfc5424Frame.priority.toString());
            Assertions.assertEquals("1", rfc5424Frame.version.toString());
            Assertions.assertEquals("2021-03-25T15:14:09.449777+02:00", rfc5424Frame.timestamp.toString());
            Assertions.assertEquals("logsource.example.com", rfc5424Frame.hostname.toString());
            Assertions.assertEquals("rsyslogd-pstats", rfc5424Frame.appName.toString());
            Assertions.assertEquals("-", rfc5424Frame.procId.toString());
            Assertions.assertEquals("-", rfc5424Frame.msgId.toString());
            Assertions
                    .assertEquals(
                            "{\"@timestamp\":\"2021-03-25T15:14:09.449777+02:00\",\"host\":\"logsource.example.com\",\"source-module\":\"impstats\", \"name\": \"resource-usage\", \"origin\": \"impstats\", \"utime\": 693053726, \"stime\": 133593735, \"maxrss\": 4690828, \"minflt\": 46694808, \"majflt\": 0, \"inblock\": 122077416, \"oublock\": 123878288, \"nvcsw\": 7199, \"nivcsw\": 9287, \"openfiles\": 20 }",
                            rfc5424Frame.msg.toString()
                    );

            // Structured Data 1
            Assertions
                    .assertEquals(
                            "logsource.example.com",
                            rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "hostname")).toString()
                    );
        }

        // finally empty
        assertFalse(rfc5424Frame.next());

        // Message Finished
        assertThrows(IllegalStateException.class, rfc5424Frame.priority::toString);
        assertThrows(IllegalStateException.class, rfc5424Frame.version::toString);
        assertThrows(IllegalStateException.class, rfc5424Frame.timestamp::toString);
        assertThrows(IllegalStateException.class, rfc5424Frame.hostname::toString);
        assertThrows(IllegalStateException.class, rfc5424Frame.appName::toString);
        assertThrows(IllegalStateException.class, rfc5424Frame.procId::toString);
        assertThrows(IllegalStateException.class, rfc5424Frame.msgId::toString);
        assertThrows(IllegalStateException.class, rfc5424Frame.msg::toString);

        // Structured Data Finished
        Assertions.assertThrows(IllegalStateException.class, () -> {
            rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "hostname")).toString();
        });

    }

    @Test
    public void testNoSd() throws IOException {
        String SYSLOG_MESSAGE = "<134>1 2019-03-08T14:00:00+02:00 host-1-2-3-4 app-tag - - -  1.2.3.4 - - [08/Mar/2019:14:00:00 +0200] \"POST /idt/device/";

        InputStream inputStream = new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes());
        RFC5424Frame rfc5424Frame = new RFC5424Frame();
        rfc5424Frame.load(inputStream);

        int count = 1;
        for (int i = 0; i < count; i++) {
            Assertions.assertTrue(rfc5424Frame.next());
            Assertions.assertEquals("134", rfc5424Frame.priority.toString());
            Assertions.assertEquals("1", rfc5424Frame.version.toString());
            Assertions.assertEquals("2019-03-08T14:00:00+02:00", rfc5424Frame.timestamp.toString());
            Assertions.assertEquals("host-1-2-3-4", rfc5424Frame.hostname.toString());
            Assertions.assertEquals("app-tag", rfc5424Frame.appName.toString());
            Assertions.assertEquals("-", rfc5424Frame.procId.toString());
            Assertions.assertEquals("-", rfc5424Frame.msgId.toString());
            Assertions
                    .assertEquals(
                            " 1.2.3.4 - - [08/Mar/2019:14:00:00 +0200] \"POST /idt/device/", rfc5424Frame.msg.toString()
                    );

            assertFalse(rfc5424Frame.next());

            inputStream.reset();
        }
    }

    @Test
    public void brokenSDElemWorkaroundTest() throws IOException {
        String SYSLOG_MESSAGE = "<15>1 2021-11-10T12:46:33+02:00 HOST01A  PROD01A - [event_id@48577 hostname=\"somehostname.tld\" uuid=\"4849E84B6C1C42C09551DC06F4D7F4AE\" unixtime=\"1636548393\" id_source=\"relay\"][rfc3164@48577 syslogtag=\"[i][be][broken][sdelem]\"][event_format@48577 original_format=\"rfc3164\"][event_node_relay@48577 hostname=\"relay.somedomain.tld\" source=\"gateway\" source_module=\"imptcp\"][event_version@48577 major=\"2\" minor=\"2\" hostname=\"relay.somedomain.tld\" version_source=\"relay\"][event_node_router@48577 source=\"127.1.2.3\" source_module=\"imrelp\" hostname=\"route.somedomain.tld\"][teragrep@48577 streamname=\"on:two:messages:0\" directory=\"host_log_data\" unixtime=\"1636548394\"]  source-http <snip>";

        InputStream inputStream = new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes());
        RFC5424Frame rfc5424Frame = new RFC5424Frame(false);
        rfc5424Frame.load(inputStream);

        int count = 1;
        for (int i = 0; i < count; i++) {
            Assertions.assertTrue(rfc5424Frame.next());
            Assertions.assertEquals("15", rfc5424Frame.priority.toString());
            Assertions.assertEquals("1", rfc5424Frame.version.toString());
            Assertions.assertEquals("2021-11-10T12:46:33+02:00", rfc5424Frame.timestamp.toString());
            Assertions.assertEquals("HOST01A", rfc5424Frame.hostname.toString());
            Assertions.assertEquals("", rfc5424Frame.appName.toString());
            Assertions.assertEquals("PROD01A", rfc5424Frame.procId.toString());
            Assertions.assertEquals("-", rfc5424Frame.msgId.toString());
            Assertions.assertEquals(" source-http <snip>", rfc5424Frame.msg.toString());

            assertFalse(rfc5424Frame.next());

            inputStream.reset();
        }
    }

    @Test
    public void noSDTest() throws IOException {
        String SYSLOG_MESSAGE = "<15>1 2019-05-29T15:00:00+03:00 PROD03A  PRODA - -  http(Worker1";
        InputStream inputStream = new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes());
        RFC5424Frame rfc5424Frame = new RFC5424Frame();
        rfc5424Frame.load(inputStream);

        int count = 1;
        for (int i = 0; i < count; i++) {
            Assertions.assertTrue(rfc5424Frame.next());

            Assertions.assertEquals("15", rfc5424Frame.priority.toString());
            Assertions.assertEquals("1", rfc5424Frame.version.toString());
            Assertions.assertEquals("2019-05-29T15:00:00+03:00", rfc5424Frame.timestamp.toString());
            Assertions.assertEquals("PROD03A", rfc5424Frame.hostname.toString());
            Assertions.assertEquals("", rfc5424Frame.appName.toString());
            Assertions.assertEquals("PRODA", rfc5424Frame.procId.toString());
            Assertions.assertEquals("-", rfc5424Frame.msgId.toString());
            Assertions.assertEquals(" http(Worker1", rfc5424Frame.msg.toString());

            assertFalse(rfc5424Frame.next());

            inputStream.reset();
        }
    }

    @Test
    void consecutiveMoSDTest() throws Exception {
        String SYSLOG_MESSAGE = "<15>1 2019-05-29T15:00:00+03:00 PROD03A  PRODA - -  http(Worker1";
        RFC5424Frame rfc5424Frame = new RFC5424Frame();

        int count = 2;
        for (int i = 0; i < count; i++) {
            rfc5424Frame.load(new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes()));

            Assertions.assertTrue(rfc5424Frame.next());
            // Message 1
            Assertions.assertEquals("15", rfc5424Frame.priority.toString());
            Assertions.assertEquals("1", rfc5424Frame.version.toString());
            Assertions.assertEquals("2019-05-29T15:00:00+03:00", rfc5424Frame.timestamp.toString());
            Assertions.assertEquals("PROD03A", rfc5424Frame.hostname.toString());
            Assertions.assertEquals("", rfc5424Frame.appName.toString());
            Assertions.assertEquals("PRODA", rfc5424Frame.procId.toString());
            Assertions.assertEquals("-", rfc5424Frame.msgId.toString());
            Assertions.assertEquals(" http(Worker1", rfc5424Frame.msg.toString());
        }

        // finally empty
        assertFalse(rfc5424Frame.next());

        // Message Finished
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.priority::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.version::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.timestamp::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.hostname::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.appName::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.procId::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.msgId::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.msg::toString);
    }

    @Test
    void multipleNewlinesInMsg() throws Exception {
        String SYSLOG_MESSAGE = "<14>1 2022-12-13T14:41:29.715Z test-stream 9627df7a-testi - - - Testing text.\ntest\ning.\n";

        RFC5424Frame rfc5424Frame = new RFC5424Frame(false);

        int count = 1;
        for (int i = 0; i < count; i++) {
            rfc5424Frame.load(new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes()));

            Assertions.assertTrue(rfc5424Frame.next());

            // Message 1
            Assertions.assertEquals("14", rfc5424Frame.priority.toString());
            Assertions.assertEquals("1", rfc5424Frame.version.toString());
            Assertions.assertEquals("2022-12-13T14:41:29.715Z", rfc5424Frame.timestamp.toString());
            Assertions.assertEquals("test-stream", rfc5424Frame.hostname.toString());
            Assertions.assertEquals("9627df7a-testi", rfc5424Frame.appName.toString());
            Assertions.assertEquals("-", rfc5424Frame.procId.toString());
            Assertions.assertEquals("-", rfc5424Frame.msgId.toString());
            Assertions.assertEquals("Testing text.\ntest\ning.\n", rfc5424Frame.msg.toString());
        }

        // finally empty
        assertFalse(rfc5424Frame.next());

        // Message Finished
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.priority::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.version::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.timestamp::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.hostname::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.appName::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.procId::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.msgId::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.msg::toString);
    }
}
