package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

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

        Assertions.assertEquals("valueThere", structuredData.getValue(new SDVector("id@0", "keyHere")).toString());

        Assertions.assertEquals("valueThere", structuredData.getValue(new SDVector("id@0", "keyHere")).toString());

        structuredData.clear();

        Assertions.assertThrows(NoSuchElementException.class, () -> {
            structuredData.getValue(new SDVector("id@0", "keyHere")).toString();
        });

    }

    @Test
    public void clearSDElementTest() {
        StructuredData structuredData = new StructuredData();

        String input = "[id@0 keyHere=\"valueThere\"] "; // structured data terminates only to non [ character

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        structuredData.accept(stream);

        Assertions.assertEquals("id@0", structuredData.sdElements.get(0).sdElementId.toString());

        Assertions.assertEquals("id@0", structuredData.sdElements.get(0).sdElementId.toString());

        // clear
        structuredData.sdElements.get(0).sdElementId.clear();
        Assertions.assertThrows(IllegalStateException.class, () -> {
                    structuredData.sdElements.get(0).sdElementId.toString();
                },
                "direction != Direction.READ");


        // double clear
        structuredData.sdElements.get(0).sdElementId.clear();
        Assertions.assertThrows(IllegalStateException.class, () -> {
                    structuredData.sdElements.get(0).sdElementId.toString();
                },
                "direction != Direction.READ");
    }

    @Test
    public void clearSDParamKeyTest() {
        StructuredData structuredData = new StructuredData();

        String input = "[id@0 keyHere=\"valueThere\"] "; // structured data terminates only to non [ character

        ByteArrayInputStream bais = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.US_ASCII)
        );

        Stream stream = new Stream(bais);

        structuredData.accept(stream);

        Assertions.assertEquals("keyHere", structuredData.sdElements.get(0).sdParams.get(0).sdParamKey.toString());

        Assertions.assertEquals("keyHere", structuredData.sdElements.get(0).sdParams.get(0).sdParamKey.toString());

        // clear
        structuredData.sdElements.get(0).sdParams.get(0).sdParamKey.clear();
        Assertions.assertEquals("", structuredData.sdElements.get(0).sdParams.get(0).sdParamKey.toString());

        // double clear
        structuredData.sdElements.get(0).sdParams.get(0).sdParamKey.clear();
        Assertions.assertEquals("", structuredData.sdElements.get(0).sdParams.get(0).sdParamKey.toString());
    }


}
