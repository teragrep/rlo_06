package com.teragrep.new_rlo_06;

public class _MessageImpl implements _Message {

    private final Element element;
    public _MessageImpl(Element element) {
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
    public boolean isStub() {
        return element.isStub();
    }

    @Override
    public String toString() {
        return element.toString();
    }
}
