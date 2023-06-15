package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SyntaxTestIssue8Test {

    @Test
    @Disabled // fails
    void testBrokenFail() throws Exception {
        String SYSLOG_MESSAGE = "<134>1  - [ ] ] [";

        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();

        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultset res = new ParserResultset(subscription, sdSubscription);

        RFC5424Parser parser = new RFC5424Parser(null, false);

        int count = 1;
        for (int i = 0; i < count; i++) {
            parser.setInputStream(new ByteArrayInputStream( (SYSLOG_MESSAGE).getBytes()));

            Assertions.assertThrows(ParseException.class, () -> {
                parser.next(res);
            });

            res.clear();
        }
    }

    @Test
    void testNilTimestamp() throws Exception {
        String SYSLOG_MESSAGE = "<14>1 - host01 systemd DEA MSG-01 - sigsegv\n";
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();

        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultset res = new ParserResultset(subscription, sdSubscription);

        RFC5424Parser parser = new RFC5424Parser(null, false);

        int count = 1;
        for (int i = 0; i < count; i++) {
            parser.setInputStream(new ByteArrayInputStream( (SYSLOG_MESSAGE).getBytes()));

            assertTrue(parser.next(res));
            ResultsetAsString strings1 = new ResultsetAsString(res);

            // Message 1
            Assertions.assertEquals("14", strings1.getPriority());
            Assertions.assertEquals("1", strings1.getVersion());
            Assertions.assertEquals("-", strings1.getTimestamp());
            Assertions.assertEquals("host01", strings1.getHostname());
            Assertions.assertEquals("systemd", strings1.getAppname());
            Assertions.assertEquals("DEA", strings1.getProcid());
            Assertions.assertEquals("MSG-01", strings1.getMsgid());
            Assertions.assertEquals("sigsegv\n", strings1.getMsg());

            res.clear();
        }
    }

    @Test
    @Disabled // fails
    void testMissingTimestamp() throws Exception {
        String SYSLOG_MESSAGE = "<14>1  host01 systemd DEA MSG-01 - sigsegv\n";
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();

        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultset res = new ParserResultset(subscription, sdSubscription);

        RFC5424Parser parser = new RFC5424Parser(null, false);

        int count = 1;
        for (int i = 0; i < count; i++) {
            parser.setInputStream(new ByteArrayInputStream( (SYSLOG_MESSAGE).getBytes()));


            Assertions.assertThrows(TimestampParseException.class, () -> {
                parser.next(res);
            });

            res.clear();
        }
    }

    @Test
    @Disabled // fails
    void testOpenSD() throws Exception {
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.123456+00:00 host01 systemd DEA MSG-01 [";
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();

        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultset res = new ParserResultset(subscription, sdSubscription);

        RFC5424Parser parser = new RFC5424Parser(null, false);

        parser.setInputStream(new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes()));

        Assertions.assertThrows(StructuredDataParseException.class, () -> {
            parser.next(res);
        });

        res.clear();
    }


    @Test
    void testAllNil() throws Exception {
        String SYSLOG_MESSAGE = "<2>1 - - - - - -";
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();

        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultset res = new ParserResultset(subscription, sdSubscription);

        RFC5424Parser parser = new RFC5424Parser(null, false);

        parser.setInputStream(new ByteArrayInputStream((SYSLOG_MESSAGE).getBytes()));

        assertTrue(parser.next(res));
        ResultsetAsString strings1 = new ResultsetAsString(res);

        // Message 1
        Assertions.assertEquals("2", strings1.getPriority());
        Assertions.assertEquals("1", strings1.getVersion());
        Assertions.assertEquals("-", strings1.getTimestamp());
        Assertions.assertEquals("-", strings1.getHostname());
        Assertions.assertEquals("-", strings1.getAppname());
        Assertions.assertEquals("-", strings1.getProcid());
        Assertions.assertEquals("-", strings1.getMsgid());
        Assertions.assertEquals("", strings1.getMsg());

        res.clear();

        res.clear();
    }
}
