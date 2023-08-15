package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MsgIdTest {
    @Test
    public void parseTest() {
        MsgId msgId = new MsgId();

        String input = "987654 ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        msgId.accept(stream);

        Assertions.assertEquals("987654", msgId.toString());
    }

    @Test
    public void dashMsgIdTest() {
        MsgId msgId = new MsgId();

        String input = "- ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        msgId.accept(stream);

        Assertions.assertEquals("-", msgId.toString());
    }

    @Test
    public void tooLongMsgIdTest() {
        MsgId msgId = new MsgId();

        String input = "9876543210987654321098765432109876543210 ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );
        assertThrows(MsgIdParseException.class, () -> {
            Stream stream = new Stream(bais);
            msgId.accept(stream);
            msgId.toString();
        });
    }
}
