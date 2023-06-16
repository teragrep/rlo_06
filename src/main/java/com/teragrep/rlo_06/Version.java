package com.teragrep.rlo_06;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

final class Version implements Consumer<Stream> {
    /*
        ||
        vv
    <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n
    
    Actions: _O
    Payload:'1 '
    States : .T
    */
    private final ByteBuffer VERSION;

    Version(ByteBuffer VERSION) {
        this.VERSION = VERSION;
    }

    @Override
    public void accept(Stream stream) {
        byte b;

        if (!stream.next()) {
            throw new ParseException("TOO SHORT");
        }
        b = stream.get();
        if (b == 49) {
            if (VERSION != null)
                VERSION.put(b);

            if (!stream.next()) {
                throw new ParseException("TOO SHORT");
            }
            b = stream.get();
            if (b != 32) { // omit ' '
                throw new VersionParseException("SP missing after VERSION");
            }
        } else {
            throw new VersionParseException("VERSION not 1");
        }
    }
}
