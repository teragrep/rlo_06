package com.teragrep.new_rlo_06;

public class _PriorityImpl implements _Priority {

    private final Element element;
    public _PriorityImpl(Element element) {
        this.element = element;
    }

    @Override
    public boolean isStub() {
        return element.isStub();
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
    public String toString() {
        return element.toString();
    }
}
