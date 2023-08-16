package com.teragrep.rlo_06;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AllToBytesTest {
    @Test
    public void allBytesEquals() throws IOException {
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"][event_id@48577 hostname=\"sc-99-99-14-247\" uuid=\"0FD92E51B37748EB90CD894CCEE63907\" unixtime=\"1612047600.0\" id_source=\"source\"][event_node_source@48577 hostname=\"sc-99-99-14-247\" source=\"f17_ssmis_20210131v7.nc\" source_module=\"imfile\"][event_node_relay@48577 hostname=\"localhost\" source=\"sc-99-99-14-247\" source_module=\"imrelp\"][event_version@48577 major=\"2\" minor=\"2\" hostname=\"localhost\" version_source=\"relay\"][event_node_router@48577 source=\"logrouter.example.com\" source_module=\"imrelp\" hostname=\"localhost\"][teragrep@48577 streamname=\"log:f17:0\" directory=\"com_teragrep_audit\" unixtime=\"1612047600.0\"] msg\n";

        InputStream inputStream = new ByteArrayInputStream( SYSLOG_MESSAGE.getBytes());
        RFC5424Frame rfc5424Frame = new RFC5424Frame(true);
        rfc5424Frame.load(inputStream);
        assertTrue(rfc5424Frame.next());

        Assertions.assertArrayEquals("\\\"3".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("ID_A@1", "u")).toBytes());
        Assertions.assertArrayEquals("t".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("ID_A@1", "e")).toBytes());

        Assertions.assertArrayEquals("9".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("ID_B@2", "n")).toBytes());

        Assertions.assertArrayEquals("sc-99-99-14-247".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "hostname")).toBytes());
        Assertions.assertArrayEquals("0FD92E51B37748EB90CD894CCEE63907".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "uuid")).toBytes());
        Assertions.assertArrayEquals("sc-99-99-14-247".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "hostname")).toBytes());
        Assertions.assertArrayEquals("1612047600.0".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "unixtime")).toBytes());
        Assertions.assertArrayEquals("source".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "id_source")).toBytes());

        Assertions.assertArrayEquals("sc-99-99-14-247".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_source@48577", "hostname")).toBytes());
        Assertions.assertArrayEquals("f17_ssmis_20210131v7.nc".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_source@48577", "source")).toBytes());
        Assertions.assertArrayEquals("imfile".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_source@48577", "source_module")).toBytes());

        Assertions.assertArrayEquals("localhost".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_relay@48577", "hostname")).toBytes());
        Assertions.assertArrayEquals("sc-99-99-14-247".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_relay@48577", "source")).toBytes());
        Assertions.assertArrayEquals("imrelp".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_relay@48577", "source_module")).toBytes());

        Assertions.assertArrayEquals("2".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_version@48577", "major")).toBytes());
        Assertions.assertArrayEquals("2".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_version@48577", "minor")).toBytes());
        Assertions.assertArrayEquals("localhost".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_version@48577", "hostname")).toBytes());
        Assertions.assertArrayEquals("relay".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_version@48577", "version_source")).toBytes());

        Assertions.assertArrayEquals("logrouter.example.com".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_router@48577", "source")).toBytes());
        Assertions.assertArrayEquals("imrelp".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_router@48577", "source_module")).toBytes());
        Assertions.assertArrayEquals("localhost".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_router@48577", "hostname")).toBytes());

        Assertions.assertArrayEquals("log:f17:0".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("teragrep@48577", "streamname")).toBytes());
        Assertions.assertArrayEquals("com_teragrep_audit".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("teragrep@48577", "directory")).toBytes());
        Assertions.assertArrayEquals("1612047600.0".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("teragrep@48577", "unixtime")).toBytes());
    }

    @Test
    public void allBytesToStringEquals() throws IOException {
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [sd_one@48577 id_one=\"eno\" id_two=\"owt\"][sd_two@48577 id_three=\"eerht\" id_four=\"ruof\"] msg\n";

        InputStream inputStream = new ByteArrayInputStream( SYSLOG_MESSAGE.getBytes());
        RFC5424Frame rfc5424Frame = new RFC5424Frame(true);
        rfc5424Frame.load(inputStream);
        assertTrue(rfc5424Frame.next());

        Priority priority = rfc5424Frame.priority;
        Assertions.assertArrayEquals("14".getBytes(), priority.toBytes());
        Assertions.assertEquals("14", priority.toString());

        Version version = rfc5424Frame.version;
        Assertions.assertArrayEquals("1".getBytes(), version.toBytes());
        Assertions.assertEquals("1", version.toString());

        Timestamp timestamp = rfc5424Frame.timestamp;
        Assertions.assertArrayEquals("2014-06-20T09:14:07.12345+00:00".getBytes(), timestamp.toBytes());
        Assertions.assertEquals("2014-06-20T09:14:07.12345+00:00", timestamp.toString());

        Hostname hostname = rfc5424Frame.hostname;
        Assertions.assertArrayEquals("host01".getBytes(), hostname.toBytes());
        Assertions.assertEquals("host01", hostname.toString());

        AppName appName = rfc5424Frame.appName;
        Assertions.assertArrayEquals("systemd".getBytes(), appName.toBytes());
        Assertions.assertEquals("systemd", appName.toString());

        ProcId procId = rfc5424Frame.procId;
        Assertions.assertArrayEquals("DEA".getBytes(), procId.toBytes());
        Assertions.assertEquals("DEA", procId.toString());

        MsgId msgId = rfc5424Frame.msgId;
        Assertions.assertArrayEquals("MSG-01".getBytes(), msgId.toBytes());
        Assertions.assertEquals("MSG-01", msgId.toString());

        SDParamValue id_one = rfc5424Frame.structuredData.getValue(new SDVector("sd_one@48577", "id_one"));
        Assertions.assertArrayEquals("eno".getBytes(), id_one.toBytes());
        Assertions.assertEquals("eno", id_one.toString());

        SDParamValue id_two = rfc5424Frame.structuredData.getValue(new SDVector("sd_one@48577", "id_two"));
        Assertions.assertArrayEquals("owt".getBytes(), id_two.toBytes());
        Assertions.assertEquals("owt", id_two.toString());

        SDParamValue id_three = rfc5424Frame.structuredData.getValue(new SDVector("sd_two@48577", "id_three"));
        Assertions.assertArrayEquals("eerht".getBytes(), id_three.toBytes());
        Assertions.assertEquals("eerht", id_three.toString());

        SDParamValue id_four = rfc5424Frame.structuredData.getValue(new SDVector("sd_two@48577", "id_four"));
        Assertions.assertArrayEquals("ruof".getBytes(), id_four.toBytes());
        Assertions.assertEquals("ruof", id_four.toString());

        Msg msg = rfc5424Frame.msg;
        Assertions.assertArrayEquals("msg".getBytes(), msg.toBytes());
        Assertions.assertEquals("msg", msg.toString());
    }
}
