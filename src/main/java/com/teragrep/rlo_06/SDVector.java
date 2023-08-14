package com.teragrep.rlo_06;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class SDVector {

    public final String sdId;
    public final String sdKey;

    final ByteBuffer sdIdBB;
    final ByteBuffer sdKeyBB;

    public SDVector(String sdId, String sdKey) {
        this.sdId = sdId;
        this.sdKey = sdKey;

        byte[] sdIDBytes = this.sdId.getBytes(StandardCharsets.US_ASCII);
        this.sdIdBB = ByteBuffer.allocateDirect(sdIDBytes.length);
        this.sdIdBB.put(sdIDBytes);
        this.sdIdBB.flip();

        byte[] sdKeyBytes = this.sdKey.getBytes(StandardCharsets.US_ASCII);
        this.sdKeyBB = ByteBuffer.allocateDirect(sdKeyBytes.length);
        this.sdKeyBB.put(sdKeyBytes);
        this.sdKeyBB.flip();
    }

    @Override
    public String toString() {
        return "SDVector{" +
                "sdId='" + sdId + '\'' +
                ", sdKey='" + sdKey + '\'' +
                '}';
    }
}
