package com.teragrep.rlo_06;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class SDVector {

    public final String sdElementId;
    public final String sdParamKey;

    final ByteBuffer sdElementIdBB;
    final ByteBuffer sdParamKeyBB;

    public SDVector(String sdElementId, String sdParamKey) {
        this.sdElementId = sdElementId;
        this.sdParamKey = sdParamKey;

        byte[] sdIDBytes = this.sdElementId.getBytes(StandardCharsets.US_ASCII);
        this.sdElementIdBB = ByteBuffer.allocateDirect(sdIDBytes.length);
        this.sdElementIdBB.put(sdIDBytes);
        this.sdElementIdBB.flip();

        byte[] sdKeyBytes = this.sdParamKey.getBytes(StandardCharsets.US_ASCII);
        this.sdParamKeyBB = ByteBuffer.allocateDirect(sdKeyBytes.length);
        this.sdParamKeyBB.put(sdKeyBytes);
        this.sdParamKeyBB.flip();
    }

    @Override
    public String toString() {
        return "SDVector{" +
                "sdElementId='" + sdElementId + '\'' +
                ", sdParamKey='" + sdParamKey + '\'' +
                '}';
    }
}
