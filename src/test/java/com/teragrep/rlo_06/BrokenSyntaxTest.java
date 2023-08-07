package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
public class BrokenSyntaxTest {

    @Test
    void testBrokenFail() throws Exception {
        String SYSLOG_MESSAGE = "<134>1  - [ ] ] [";
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();
        RFC5424Parser parser = new RFC5424Parser(subscription, sdSubscription);
        parser.setInputStream(new ByteArrayInputStream( (SYSLOG_MESSAGE).getBytes()));
        Assertions.assertThrows(ParseException.class, parser::next);
    }

    @Test
    void testNilTimestamp() throws Exception {
        String input = "<14>1  host01 systemd DEA MSG-01 - sigsegv\n";
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription(true);
        RFC5424Parser parser = new RFC5424Parser(subscription, sdSubscription);
        ResultSetAsString resultsetAsString = new ResultSetAsString(parser.get());
        parser.setInputStream(new ByteArrayInputStream( (input).getBytes()));
        parser.next();
        Assertions.assertEquals("14", resultsetAsString.getPriority(), "Priority");
        Assertions.assertEquals("1", resultsetAsString.getVersion(), "Version");
        Assertions.assertEquals("", resultsetAsString.getTimestamp(), "Timestamp");
        Assertions.assertEquals("host01", resultsetAsString.getHostname(), "Hostname");
        Assertions.assertEquals("systemd", resultsetAsString.getAppname(), "Appname");
        Assertions.assertEquals("DEA", resultsetAsString.getProcid(), "Procid");
        Assertions.assertEquals("MSG-01", resultsetAsString.getMsgid(), "msgid");
        Assertions.assertEquals("sigsegv", resultsetAsString.getMsg(), "msg");
    }

    @Test
    void testOpenSD() throws Exception {
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.123456+00:00 host01 systemd DEA MSG-01 [";
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();
        RFC5424Parser parser = new RFC5424Parser(subscription, sdSubscription);
        parser.setInputStream(new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes()));
        Assertions.assertThrows(ParseException.class, parser::next);
    }


    @Test
    void testAllNil() throws Exception {
        String input = "<2>1  - - - - - ";
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription(true);
        InputStream inputStream = new ByteArrayInputStream( (input).getBytes());
        RFC5424Parser parser = new RFC5424Parser(subscription, sdSubscription, inputStream);
        ResultSetAsString resultsetAsString = new ResultSetAsString(parser.get());
        parser.next();
        Assertions.assertEquals("2", resultsetAsString.getPriority(), "Priority");
        Assertions.assertEquals("1", resultsetAsString.getVersion(), "Version");
        Assertions.assertEquals("", resultsetAsString.getTimestamp(), "Timestamp");
        Assertions.assertEquals("-", resultsetAsString.getHostname(), "Hostname");
        Assertions.assertEquals("-", resultsetAsString.getAppname(), "Appname");
        Assertions.assertEquals("-", resultsetAsString.getProcid(), "ProcId");
        Assertions.assertEquals("-", resultsetAsString.getMsgid(), "MsgId");
        Assertions.assertEquals("", resultsetAsString.getMsg(), "Msg");
    }
}
