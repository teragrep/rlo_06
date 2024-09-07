package com.teragrep.new_rlo_06.inputs;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Produces ByteBuffer arrays for testing purposes
 */
public class StringInput {

    private final String string;

    public StringInput(String string) {
        this.string = string;
    }

    public ByteBuffer[] asBuffers() {
        return asBuffers(1);
    }

    /**
     * @param number of buffers to produce
     * @return ByteBuffer[] containing the string as split into each buffer
     */
    public ByteBuffer[] asBuffers(int number) {
        if (number < 1) {
            throw new IllegalArgumentException("Number of buffers must be positive");
        }

        ByteBuffer[] buffers = new ByteBuffer[number];

        int stringLength = string.length();
        int partSize = (int) Math.ceil((double) stringLength / number);



        int bufferIndex = 0;
        for (int i = 0; i < stringLength; i += partSize) {

            String partialString =
                    string.substring(i, Math.min(stringLength, i + partSize));

            byte[] bytes = partialString.getBytes(StandardCharsets.UTF_8);
            ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
            buffer.put(bytes);
            buffer.flip();

            buffers[bufferIndex] = buffer;
            bufferIndex++;
        }

        return buffers;
    }
}
