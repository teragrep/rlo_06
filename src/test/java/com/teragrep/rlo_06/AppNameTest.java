package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AppNameTest {
    @Test
    public void parseTest() {
        AppName appName = new AppName();

        String input = "anAppNameTag ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        appName.accept(stream);
        Assertions.assertEquals("anAppNameTag", appName.toString());
    }

    @Test
    public void dashAppnameTest() {
        AppName appName = new AppName();

        String input = "- ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        appName.accept(stream);

        Assertions.assertEquals("-", appName.toString());
    }

    @Test
    public void tooLongAppNameTest() {
        AppName appName = new AppName();

        String input = "ThisIsVeryLongAppNameThatShouldNotExistAndWillBeOverThe48CharLimit ";

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        assertThrows(AppNameParseException.class, () -> {
            Stream stream = new Stream(bais);
            appName.accept(stream);
            appName.toString();
        });
    }
}
