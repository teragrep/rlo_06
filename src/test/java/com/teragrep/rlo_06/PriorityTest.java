package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PriorityTest {

    @Test
    public void parseTest() {
        Priority priority = new Priority();

        String input = "<123>";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        // priority has first byte always loaded
        Assertions.assertTrue(stream.next());
        priority.accept(stream);

        Assertions.assertEquals("123", priority.toString());
    }

    @Test
    public void emptyPriorityIdTest() {
        Priority priority = new Priority();

        String input = "<>";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        assertThrows(PriorityParseException.class, () -> {
            Stream stream = new Stream(bais);
            // priority has first byte always loaded
            Assertions.assertTrue(stream.next());
            priority.accept(stream);
            priority.toString();
        });
    }

    @Test
    public void tooLongPriorityIdTest() {
        Priority priority = new Priority();

        String input = "<12345>";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        assertThrows(PriorityParseException.class, () -> {
            Stream stream = new Stream(bais);
            // priority has first byte always loaded
            Assertions.assertTrue(stream.next());
            priority.accept(stream);
            priority.toString();
        });
    }
}
