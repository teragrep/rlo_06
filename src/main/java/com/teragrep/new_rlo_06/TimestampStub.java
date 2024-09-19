package com.teragrep.new_rlo_06;

import java.nio.ByteBuffer;
import java.util.List;

public class TimestampStub implements Timestamp {
    @Override
    public byte[] toBytes() {
        throw new UnsupportedOperationException("Stub does not allow this.");
    }

    @Override
    public int toInt() {
        throw new UnsupportedOperationException("Stub does not allow this.");
    }

    @Override
    public long size() {
        throw new UnsupportedOperationException("Stub does not allow this.");
    }

    @Override
    public List<ByteBuffer> toEncoded() {
        throw new UnsupportedOperationException("Stub does not allow this.");
    }

    @Override
    public boolean isStub() {
        throw new UnsupportedOperationException("Stub does not allow this.");
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Stub does not allow this.");
    }
}
