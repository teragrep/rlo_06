package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class MsgTest {
    @Test
    public void parseLeadingSpaceNoLFTest() {
        // lf termination off
        Msg msg = new Msg(false);

        String input = " msg with preceding space and no newline";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        Assertions.assertTrue(stream.next()); // msg requires stream called with next
        msg.accept(stream);

        Assertions.assertEquals("msg with preceding space and no newline", msg.toString());
    }

    @Test
    public void parseNoLeadingSpaceNoLFTest() {
        // lf termination off
        Msg msg = new Msg(false);

        String input = "msg without preceding space and no newline";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        Assertions.assertTrue(stream.next()); // msg requires stream called with next
        msg.accept(stream);

        Assertions.assertEquals("msg without preceding space and no newline", msg.toString());
    }

    @Test
    public void parseNewlineTest() {
        // lf termination off
        Msg msg = new Msg(false);

        String input = " yes\nnewline";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        Assertions.assertTrue(stream.next()); // msg requires stream called with next
        msg.accept(stream);

        Assertions.assertEquals("yes\nnewline", msg.toString());
    }
    @Test
    public void parseLFTerminationWithNextTest() {
        // lf termination off
        Msg msg = new Msg(true);

        String input = " there is something after newline\nanother";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        Assertions.assertTrue(stream.next()); // msg requires stream called with next
        msg.accept(stream);

        Assertions.assertEquals("there is something after newline", msg.toString());
    }

    @Test
    public void parseLFTerminationWithoutNextTest() {
        // lf termination off
        Msg msg = new Msg(true);

        String input = " there is nothing after newline\n";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        Assertions.assertTrue(stream.next()); // msg requires stream called with next
        msg.accept(stream);

        Assertions.assertEquals("there is nothing after newline", msg.toString());
    }

    @Test
    public void emptyMessageTest() {
        // lf termination off
        Msg msg = new Msg(true);

        String input = " ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        Assertions.assertTrue(stream.next()); // msg requires stream called with next
        msg.accept(stream);

        Assertions.assertEquals("", msg.toString());
    }
}
