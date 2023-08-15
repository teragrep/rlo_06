package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProcIdTest {
    @Test
    public void parseTest() {
        ProcId procId = new ProcId();

        String input = "cade00f0-3260-4b88-ab61-d644a75dfbbb ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        procId.accept(stream);

        Assertions.assertEquals("cade00f0-3260-4b88-ab61-d644a75dfbbb", procId.toString());
    }

    @Test
    public void emptyProcIdTest() {
        ProcId procId = new ProcId();

        String input = "";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        assertThrows(ParseException.class, () -> {
            Stream stream = new Stream(bais);
            procId.accept(stream);
            procId.toString();
        });
    }

    @Test
    public void tooLongProcIdTest() {
        ProcId procId = new ProcId();

        String input = new String(new char[256]).replace('\0', 'x');

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        assertThrows(ProcIdParseException.class, () -> {
            Stream stream = new Stream(bais);
            procId.accept(stream);
            procId.toString();
        });
    }
}
