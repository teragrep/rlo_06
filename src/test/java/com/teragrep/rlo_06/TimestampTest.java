package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class TimestampTest {
    @Test
    public void parseTest() {
        Timestamp timestamp = new Timestamp();

        String input = "2023-06-16T12:08:12.123456+03:00 ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        timestamp.accept(stream);

        Assertions.assertEquals("2023-06-16T12:08:12.123456+03:00", timestamp.toString());
    }
}
