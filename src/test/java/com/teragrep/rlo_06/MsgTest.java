package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class MsgTest {
    @Test
    public void parseLeadingSpaceNoLFTest() {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.MSG);
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultSet parserResultSet = new ParserResultSet(
                subscription,
                sdSubscription
        );

        // lf termination off
        Msg msg = new Msg(parserResultSet.MSG, false);

        String input = " msg with preceding space and no newline";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        Assertions.assertTrue(stream.next()); // msg requires stream called with next
        msg.accept(stream);

        ResultSetAsString resultSetAsString = new ResultSetAsString(parserResultSet);
        Assertions.assertEquals("msg with preceding space and no newline", resultSetAsString.getMsg());
    }

    @Test
    public void parseNoLeadingSpaceNoLFTest() {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.MSG);
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultSet parserResultSet = new ParserResultSet(
                subscription,
                sdSubscription
        );

        // lf termination off
        Msg msg = new Msg(parserResultSet.MSG, false);

        String input = "msg without preceding space and no newline";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        Assertions.assertTrue(stream.next()); // msg requires stream called with next
        msg.accept(stream);

        ResultSetAsString resultSetAsString = new ResultSetAsString(parserResultSet);
        Assertions.assertEquals("msg without preceding space and no newline", resultSetAsString.getMsg());
    }

    @Test
    public void parseNewlineTest() {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.MSG);
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultSet parserResultSet = new ParserResultSet(
                subscription,
                sdSubscription
        );

        // lf termination off
        Msg msg = new Msg(parserResultSet.MSG, false);

        String input = " yes\nnewline";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        Assertions.assertTrue(stream.next()); // msg requires stream called with next
        msg.accept(stream);

        ResultSetAsString resultSetAsString = new ResultSetAsString(parserResultSet);
        Assertions.assertEquals("yes\nnewline", resultSetAsString.getMsg());
    }
    @Test
    public void parseLFTerminationWithNextTest() {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.MSG);
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultSet parserResultSet = new ParserResultSet(
                subscription,
                sdSubscription
        );

        // lf termination off
        Msg msg = new Msg(parserResultSet.MSG, true);

        String input = " there is something after newline\nanother";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        Assertions.assertTrue(stream.next()); // msg requires stream called with next
        msg.accept(stream);

        ResultSetAsString resultSetAsString = new ResultSetAsString(parserResultSet);
        Assertions.assertEquals("there is something after newline", resultSetAsString.getMsg());
    }

    @Test
    public void parseLFTerminationWithoutNextTest() {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.MSG);
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultSet parserResultSet = new ParserResultSet(
                subscription,
                sdSubscription
        );

        // lf termination off
        Msg msg = new Msg(parserResultSet.MSG, true);

        String input = " there is nothing after newline\n";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        Assertions.assertTrue(stream.next()); // msg requires stream called with next
        msg.accept(stream);

        ResultSetAsString resultSetAsString = new ResultSetAsString(parserResultSet);
        Assertions.assertEquals("there is nothing after newline", resultSetAsString.getMsg());
    }

    @Test
    public void emptyMessageTest() {
        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.add(ParserEnum.MSG);
        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription();

        ParserResultSet parserResultSet = new ParserResultSet(
                subscription,
                sdSubscription
        );

        // lf termination off
        Msg msg = new Msg(parserResultSet.MSG, true);

        String input = " ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        Assertions.assertTrue(stream.next()); // msg requires stream called with next
        msg.accept(stream);

        ResultSetAsString resultSetAsString = new ResultSetAsString(parserResultSet);
        Assertions.assertEquals("", resultSetAsString.getMsg());
    }
}
