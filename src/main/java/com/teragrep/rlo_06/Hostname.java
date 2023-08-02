package com.teragrep.rlo_06;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

final class Hostname implements Consumer<Stream> {
    /*
                                                      |||||||
                                                      vvvvvvv
                <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

                Actions: ______O
                Payload:'host01 '
                States : ......T
                */
    private final ByteBuffer HOSTNAME;

    Hostname(ByteBuffer HOSTNAME) {
        this.HOSTNAME = HOSTNAME;
    }

    @Override
    public void accept(Stream stream) {
        short hostname_max_left = 255;

        if (!stream.next()) {
            throw new ParseException("Expected HOSTNAME, received nothing");
        }
        byte b = stream.get();
        while (hostname_max_left > 0 && b != 32) {
            if (HOSTNAME != null) {
                HOSTNAME.put(b);
            }
            hostname_max_left--;

            if (!stream.next()) {
                throw new ParseException("HOSTNAME is too short, can't continue");
            }
            b = stream.get();
        }

        if (b != 32) {
            throw new HostnameParseException("SP missing after HOSTNAME or HOSTNAME too long");
        }
    }
}
