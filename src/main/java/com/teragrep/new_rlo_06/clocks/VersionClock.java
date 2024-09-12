package com.teragrep.new_rlo_06.clocks;

import com.teragrep.new_rlo_06.Version;

import java.nio.ByteBuffer;

public class VersionClock implements Clock<Version>{
    @Override
    public boolean isComplete() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ByteBuffer apply(ByteBuffer byteBuffer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Version get() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
