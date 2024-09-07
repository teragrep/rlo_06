package com.teragrep.new_rlo_06.clocks;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

public interface TerminalClock<T> extends Supplier<T> {
    void submit(ByteBuffer input);
}
