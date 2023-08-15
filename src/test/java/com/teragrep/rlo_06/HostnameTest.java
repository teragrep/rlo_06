package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class HostnameTest {
    @Test
    public void parseTest() {
        Hostname hostname = new Hostname();

        String input = "example.com ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        hostname.accept(stream);

        Assertions.assertEquals("example.com", hostname.toString());
    }

    @Test
    public void dashHostnameTest() {
        Hostname hostname = new Hostname();

        String input = "- ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        hostname.accept(stream);

        Assertions.assertEquals("-", hostname.toString());
    }

    @Test
    public void tooLongHostnameTest() {
        Hostname hostname = new Hostname();

        String input = new String(new char[256]).replace('\0', 'x');

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        assertThrows(HostnameParseException.class, () -> {
            Stream stream = new Stream(bais);
            hostname.accept(stream);
            hostname.toString();
        });
    }
}
