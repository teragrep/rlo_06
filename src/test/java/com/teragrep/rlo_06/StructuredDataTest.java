package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class StructuredDataTest {
    @Test
    public void parseTest() {
        StructuredData structuredData = new StructuredData();

        String input = "[id@0 keyHere=\"valueThere\"] "; // structured data terminates only to non [ character

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        structuredData.accept(stream);

        Assertions.assertEquals("valueThere", structuredData.getValue(new SDVector("id@0","keyHere")).toString());
    }
}
