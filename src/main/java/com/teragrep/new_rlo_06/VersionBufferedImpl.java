package com.teragrep.new_rlo_06;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class VersionBufferedImpl implements Version {

    private final List<ByteBuffer> numbers;
    private final List<ByteBuffer> spaces;
    private final Element element;

    public VersionBufferedImpl(List<ByteBuffer> numbers, List<ByteBuffer> spaces) {
        this(numbers, spaces, new ElementImpl(numbers));
    }

    public VersionBufferedImpl(List<ByteBuffer> numbers, List<ByteBuffer> spaces, Element element) {
        this.numbers = numbers;
        this.spaces = spaces;
        this.element = element;
    }

    @Override
    public byte[] toBytes() {
        return element.toBytes();
    }

    @Override
    public int toInt() {
        return element.toInt();
    }

    @Override
    public long size() {
        return element.size();
    }

    @Override
    public List<ByteBuffer> toEncoded() {
        List<ByteBuffer> readOnlyBuffers = new ArrayList<>(numbers.size() + spaces.size());

        for (ByteBuffer number : numbers) {
            readOnlyBuffers.add(number.asReadOnlyBuffer());
        }

        for (ByteBuffer space : spaces) {
            readOnlyBuffers.add(space.asReadOnlyBuffer());
        }
        return readOnlyBuffers;
    }

    @Override
    public boolean isStub() {
        return false;
    }

    @Override
    public String toString() {
        return element.toString();
    }
}
