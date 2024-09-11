package com.teragrep.new_rlo_06.clocks;

import java.nio.ByteBuffer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface Clock<T> extends UnaryOperator<ByteBuffer>, Supplier<T> {
    boolean isComplete();
}
