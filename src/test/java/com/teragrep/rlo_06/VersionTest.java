package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class VersionTest {
    @Test
    public void parseTest() {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.VERSION);
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultSet parserResultSet = new ParserResultSet(
                subscription,
                sdSubscription
        );

        Version version = new Version(parserResultSet.VERSION);

        String input = "1 ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        version.accept(stream);

        ResultSetAsString resultSetAsString = new ResultSetAsString(parserResultSet);
        Assertions.assertEquals("1", resultSetAsString.getVersion());
    }

    @Test
    public void testEmptyVersion() {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.VERSION);
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultSet parserResultSet = new ParserResultSet(
                subscription,
                sdSubscription
        );

        Version version = new Version(parserResultSet.VERSION);

        String input = " ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        assertThrows(VersionParseException.class, () -> {
            Stream stream = new Stream(bais);
            version.accept(stream);
            new ResultSetAsString(parserResultSet);
        });
    }


    @Test
    public void testNonOneVersion() {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.VERSION);
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultSet parserResultSet = new ParserResultSet(
                subscription,
                sdSubscription
        );

        Version version = new Version(parserResultSet.VERSION);

        String input = "2 ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        assertThrows(VersionParseException.class, () -> {
            Stream stream = new Stream(bais);
            version.accept(stream);
            new ResultSetAsString(parserResultSet);
        });
    }
}
