package com.teragrep.new_rlo_06.clocks;

import com.teragrep.new_rlo_06.Element;
import com.teragrep.new_rlo_06.ElementImpl;
import com.teragrep.new_rlo_06._Message;
import com.teragrep.new_rlo_06._MessageImpl;

import java.nio.ByteBuffer;
import java.util.LinkedList;

public class MessageClock implements TerminalClock<_Message> {
    private final LinkedList<ByteBuffer> bufferSliceList;

    public MessageClock() {
        bufferSliceList = new LinkedList<>();
    }


    @Override
    public void submit(ByteBuffer input) {
        ByteBuffer slice = input.slice();

        // ignore empty slices
        if (slice.capacity() != 0) {
            bufferSliceList.add(slice);
        }

        // TODO new line termination in another type of MessageClock?
    }

    @Override
    public _Message get() {
        Element element = new ElementImpl(bufferSliceList);
        return new _MessageImpl(element);
    }
}
