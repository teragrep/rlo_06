package com.teragrep.new_rlo_06;

import java.nio.ByteBuffer;
import java.util.List;

public interface Encodable {
    List<ByteBuffer> toEncoded();
}
