package com.teragrep.rlo_06;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AllToBytesTest {
    @Test
    public void allBytesToStringEqualsTest() throws IOException {
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
