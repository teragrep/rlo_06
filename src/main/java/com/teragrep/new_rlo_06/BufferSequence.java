package com.teragrep.new_rlo_06;

import java.nio.ByteBuffer;

public class BufferSequence {

    private final ByteBuffer[] sequences;

    public BufferSequence(ByteBuffer[] buffers) {
        this.sequences = buffers;
    }

    public _RFC5424Frame rfc5424Frame() {


        return new _RFC5424Frame();
    }
}
