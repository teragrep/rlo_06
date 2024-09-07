package com.teragrep.new_rlo_06.inputs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class StringInputTest {

    @Test
    public void sliceHelloIntoBuffers() {
        StringInput stringInput = new StringInput("Hello");

        ByteBuffer[] buffers = stringInput.asBuffers(3);

        String part1 = StandardCharsets.UTF_8.decode(buffers[0]).toString();
        Assertions.assertEquals("He", part1);
        String part2 = StandardCharsets.UTF_8.decode(buffers[1]).toString();
        Assertions.assertEquals("ll", part2);
        String part3 = StandardCharsets.UTF_8.decode(buffers[2]).toString();
        Assertions.assertEquals("o", part3);

    }
    
    @Test
    public void helloAsBuffers() {
        StringInput stringInput = new StringInput("Hello");
        
        ByteBuffer[] buffers = stringInput.asBuffers();
        
        String hello = StandardCharsets.UTF_8.decode(buffers[0]).toString();
        
        Assertions.assertEquals("Hello", hello);
    }
}
