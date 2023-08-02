package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class StructuredDataTest {
    @Test
    public void parseTest() {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();
        sdSubscription.subscribeElement("id@0","keyHere");
        ParserResultSet parserResultSet = new ParserResultSet(
                subscription,
                sdSubscription
        );

        StructuredData structuredData = new StructuredData(parserResultSet);

        String input = "[id@0 keyHere=\"valueThere\"] "; // structured data terminates only to non [ character

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        structuredData.accept(stream);

        ResultSetAsString resultSetAsString = new ResultSetAsString(parserResultSet);
        Assertions.assertEquals("valueThere", resultSetAsString.getSdValue("id@0","keyHere"));
    }
}
