package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class VersionTest {
    @Test
    public void parseTest() {
        Version version = new Version();

        String input = "1 ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        version.accept(stream);

        Assertions.assertEquals("1", version.toString());
    }

    @Test
    public void testEmptyVersion() {
        Version version = new Version();

        String input = " ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        assertThrows(VersionParseException.class, () -> {
            Stream stream = new Stream(bais);
            version.accept(stream);
            version.toString();
        });
    }


    @Test
    public void testNonOneVersion() {
        Version version = new Version();

        String input = "2 ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        assertThrows(VersionParseException.class, () -> {
            Stream stream = new Stream(bais);
            version.accept(stream);
            version.toString();
        });
    }
}
