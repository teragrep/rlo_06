package com.teragrep.new_rlo_06;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * FIXME this is a copy from FragmentImpl in rlp_03 in many ways
 */
public class ElementImpl implements Element {

    private final List<ByteBuffer> bufferSliceList;

    public ElementImpl(List<ByteBuffer> byteBufferList) {
        this.bufferSliceList = byteBufferList;
    }

    @Override
    public byte[] toBytes() {
        int totalBytes = 0;
        for (ByteBuffer slice : bufferSliceList) {
            totalBytes = totalBytes + slice.remaining();
        }
        byte[] bytes = new byte[totalBytes];

        int copiedBytes = 0;
        for (ByteBuffer slice : bufferSliceList) {
            int remainingBytes = slice.remaining();
            slice.asReadOnlyBuffer().get(bytes, copiedBytes, remainingBytes);
            copiedBytes = copiedBytes + remainingBytes;
        }

        return bytes;
    }

    @Override
    public String toString() {
        byte[] bytes = toBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public int toInt() {
        String integerString = toString();
        return Integer.parseInt(integerString);
    }

    @Override
    public long size() {
        long currentLength = 0;
        for (ByteBuffer slice : bufferSliceList) {
            currentLength = currentLength + slice.limit();
        }
        return currentLength;
    }

    @Override
    public boolean isStub() {
        return false;
    }
}
