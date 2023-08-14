package com.teragrep.rlo_06;

import java.nio.ByteBuffer;

public interface Matchable {
    boolean matches(ByteBuffer buffer);
}
