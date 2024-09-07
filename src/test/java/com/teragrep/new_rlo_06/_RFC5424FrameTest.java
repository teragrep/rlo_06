package com.teragrep.new_rlo_06;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class _RFC5424FrameTest {

    @Test
    public void testInterpretation() {
        String payloadFirstFragment = "<14>1 2014-06-20T09:14:07.123456";
        byte[] firstFragmentBytes = payloadFirstFragment.getBytes(StandardCharsets.UTF_8);
        ByteBuffer firstFragment = ByteBuffer.allocateDirect(firstFragmentBytes.length);
        firstFragment.put(firstFragmentBytes);
        firstFragment.flip();

        String payloadSecondFragment = "+00:00 host01 systemd DEA MSG-01 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"] sigsegv\n";
        byte[] secondFragmentBytes = payloadSecondFragment.getBytes(StandardCharsets.UTF_8);
        ByteBuffer secondFragment = ByteBuffer.allocateDirect(secondFragmentBytes.length);
        secondFragment.put(secondFragmentBytes);
        secondFragment.flip();


        ByteBuffer[] fragments = new ByteBuffer[2];
        fragments[0] = firstFragment;
        fragments[1] = secondFragment;

        _RFC5424Frame rfc5424Frame = new _RFC5424Frame();

    }
}
