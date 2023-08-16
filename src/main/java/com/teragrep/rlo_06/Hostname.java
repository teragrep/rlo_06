package com.teragrep.rlo_06;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public final class Hostname implements Consumer<Stream>, Clearable {
    /*
                                                      |||||||
                                                      vvvvvvv
                <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

                Actions: ______O
                Payload:'host01 '
                States : ......T
                */
    private final ByteBuffer HOSTNAME;
    private FragmentState fragmentState;

    Hostname() {
        this.HOSTNAME = ByteBuffer.allocateDirect(255);
        this.fragmentState = FragmentState.EMPTY;
    }

    @Override
    public void accept(Stream stream) {
        if (fragmentState != FragmentState.EMPTY) {
            throw new IllegalStateException("fragmentState != FragmentState.EMPTY");
        }

        short hostname_max_left = 255;

        if (!stream.next()) {
            throw new ParseException("Expected HOSTNAME, received nothing");
        }
        byte b = stream.get();
        while (hostname_max_left > 0 && b != 32) {
            HOSTNAME.put(b);
            hostname_max_left--;

            if (!stream.next()) {
                throw new ParseException("HOSTNAME is too short, can't continue");
            }
            b = stream.get();
        }

        if (b != 32) {
            throw new HostnameParseException("SP missing after HOSTNAME or HOSTNAME too long");
        }

        fragmentState = FragmentState.WRITTEN;
    }

    @Override
    public void clear() {
        HOSTNAME.clear();
        fragmentState = FragmentState.EMPTY;
    }

    @Override
    public String toString() {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }
        HOSTNAME.flip();
        return StandardCharsets.US_ASCII.decode(HOSTNAME).toString();
    }
}
