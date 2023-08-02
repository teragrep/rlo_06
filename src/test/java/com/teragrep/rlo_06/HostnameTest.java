package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class HostnameTest {
    @Test
    public void parseTest() {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.HOSTNAME);
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultSet parserResultSet = new ParserResultSet(
                subscription,
                sdSubscription
        );

        Hostname hostname = new Hostname(parserResultSet.HOSTNAME);

        String input = "example.com ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        hostname.accept(stream);

        ResultSetAsString resultSetAsString = new ResultSetAsString(parserResultSet);
        Assertions.assertEquals("example.com", resultSetAsString.getHostname());
    }

    @Test
    public void dashHostnameTest() {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.HOSTNAME);
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultSet parserResultSet = new ParserResultSet(
                subscription,
                sdSubscription
        );

        Hostname hostname = new Hostname(parserResultSet.HOSTNAME);

        String input = "- ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        hostname.accept(stream);

        ResultSetAsString resultSetAsString = new ResultSetAsString(parserResultSet);
        Assertions.assertEquals("-", resultSetAsString.getHostname());
    }

    @Test
    public void tooLongHostnameTest() {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.HOSTNAME);
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultSet parserResultSet = new ParserResultSet(
                subscription,
                sdSubscription
        );

        Hostname hostname = new Hostname(parserResultSet.HOSTNAME);

        String input = new String(new char[256]).replace('\0', 'x');

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        assertThrows(HostnameParseException.class, () -> {
            Stream stream = new Stream(bais);
            hostname.accept(stream);
            new ResultSetAsString(parserResultSet);
        });
    }
}
