package com.teragrep.new_rlo_06;

public interface Element extends Stubable {

    byte[] toBytes();

    String toString();

    int toInt();

    long size();
}
