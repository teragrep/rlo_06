package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProcIdTest {
    @Test
    public void parseTest() {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.PROCID);
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultSet parserResultSet = new ParserResultSet(
                subscription,
                sdSubscription
        );

        ProcId procId = new ProcId(parserResultSet.PROCID);

        String input = "cade00f0-3260-4b88-ab61-d644a75dfbbb ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        procId.accept(stream);

        ResultSetAsString resultSetAsString = new ResultSetAsString(parserResultSet);
        Assertions.assertEquals("cade00f0-3260-4b88-ab61-d644a75dfbbb", resultSetAsString.getProcid());
    }

    @Test
    public void emptyProcIdTest() {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.PROCID);
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultSet parserResultSet = new ParserResultSet(
                subscription,
                sdSubscription
        );

        ProcId procId = new ProcId(parserResultSet.PROCID);

        String input = "";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        assertThrows(ParseException.class, () -> {
            Stream stream = new Stream(bais);
            procId.accept(stream);
            new ResultSetAsString(parserResultSet);
        });
    }

    @Test
    public void tooLongProcIdTest() {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.PROCID);
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultSet parserResultSet = new ParserResultSet(
                subscription,
                sdSubscription
        );

        ProcId procId = new ProcId(parserResultSet.PROCID);

        String input = new String(new char[256]).replace('\0', 'x');

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        assertThrows(ProcIdParseException.class, () -> {
            Stream stream = new Stream(bais);
            procId.accept(stream);
            new ResultSetAsString(parserResultSet);
        });
    }
}
