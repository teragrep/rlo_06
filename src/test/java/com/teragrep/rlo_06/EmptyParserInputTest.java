package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class EmptyParserInputTest {
    @Test
    void testEmptyParserInput() throws IOException {
        // Should not break but also should not return any data
        RFC5424Parser parser = new RFC5424Parser();
        Assertions.assertFalse(parser.next());
        ResultSetAsString resultsetAsString = new ResultSetAsString(parser.get());
    }
}
