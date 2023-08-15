package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
public class BrokenSyntaxTest {

    @Test
    void testBrokenFail() throws Exception {
        String SYSLOG_MESSAGE = "<134>1  - [ ] ] [";
        RFC5424Frame rfc5424Frame = new RFC5424Frame();
        rfc5424Frame.load(new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes()));
        Assertions.assertThrows(ParseException.class, rfc5424Frame::next);
    }

    @Test
    void testNilTimestamp() throws Exception {
        String input = "<14>1  host01 systemd DEA MSG-01 - sigsegv\n";
        RFC5424Frame rfc5424Frame = new RFC5424Frame();
        rfc5424Frame.load(new ByteArrayInputStream((input).getBytes()));
        rfc5424Frame.next();
        Assertions.assertEquals("14", rfc5424Frame.priority.toString(), "Priority");
        Assertions.assertEquals("1", rfc5424Frame.version.toString(), "Version");
        Assertions.assertEquals("", rfc5424Frame.timestamp.toString(), "Timestamp");
        Assertions.assertEquals("host01", rfc5424Frame.hostname.toString(), "Hostname");
        Assertions.assertEquals("systemd", rfc5424Frame.appName.toString(), "Appname");
        Assertions.assertEquals("DEA", rfc5424Frame.procId.toString(), "Procid");
        Assertions.assertEquals("MSG-01", rfc5424Frame.msgId.toString(), "msgid");
        Assertions.assertEquals("sigsegv", rfc5424Frame.msg.toString(), "msg");
    }

    @Test
    void testOpenSD() throws Exception {
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.123456+00:00 host01 systemd DEA MSG-01 [";
        RFC5424Frame rfc5424Frame = new RFC5424Frame();
        rfc5424Frame.load(new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes()));
        Assertions.assertThrows(ParseException.class, rfc5424Frame::next);
    }


    @Test
    void testAllNil() throws Exception {
        String input = "<2>1  - - - - - ";
        InputStream inputStream = new ByteArrayInputStream((input).getBytes());
        RFC5424Frame rfc5424Frame = new RFC5424Frame();
        rfc5424Frame.load(inputStream);
        rfc5424Frame.next();
        Assertions.assertEquals("2", rfc5424Frame.priority.toString(), "Priority");
        Assertions.assertEquals("1", rfc5424Frame.version.toString(), "Version");
        Assertions.assertEquals("", rfc5424Frame.timestamp.toString(), "Timestamp");
        Assertions.assertEquals("-", rfc5424Frame.hostname.toString(), "Hostname");
        Assertions.assertEquals("-", rfc5424Frame.appName.toString(), "Appname");
        Assertions.assertEquals("-", rfc5424Frame.procId.toString(), "ProcId");
        Assertions.assertEquals("-", rfc5424Frame.msgId.toString(), "MsgId");
        Assertions.assertEquals("", rfc5424Frame.msg.toString(), "Msg");
    }
}
