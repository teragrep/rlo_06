package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PriorityTest {

    @Test
    public void parseTest() {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.PRIORITY);
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultSet parserResultSet = new ParserResultSet(
                subscription,
                sdSubscription
        );

        Priority priority = new Priority(parserResultSet.PRIORITY);

        String input = "<123>";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        // priority has first byte always loaded
        Assertions.assertTrue(stream.next());
        priority.accept(stream);

        ResultSetAsString resultSetAsString = new ResultSetAsString(parserResultSet);
        Assertions.assertEquals("123", resultSetAsString.getPriority());
    }

    @Test
    public void emptyPriorityIdTest() {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.PRIORITY);
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultSet parserResultSet = new ParserResultSet(
                subscription,
                sdSubscription
        );

        Priority priority = new Priority(parserResultSet.PRIORITY);

        String input = "<>";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        assertThrows(PriorityParseException.class, () -> {
            Stream stream = new Stream(bais);
            // priority has first byte always loaded
            Assertions.assertTrue(stream.next());
            priority.accept(stream);
            new ResultSetAsString(parserResultSet);
        });
    }

    @Test
    public void tooLongPriorityIdTest() {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.PRIORITY);
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultSet parserResultSet = new ParserResultSet(
                subscription,
                sdSubscription
        );

        Priority priority = new Priority(parserResultSet.PRIORITY);

        String input = "<12345>";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        assertThrows(PriorityParseException.class, () -> {
            Stream stream = new Stream(bais);
            // priority has first byte always loaded
            Assertions.assertTrue(stream.next());
            priority.accept(stream);
            new ResultSetAsString(parserResultSet);
        });
    }
}