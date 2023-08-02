package com.teragrep.rlo_06;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

final class AppName implements Consumer<Stream> {
    /*
                                                     ||||||||
                                                     vvvvvvvv
        <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

        Actions: _______O
        Payload:'systemd '
        States : .......T
        */
    private final ByteBuffer APPNAME;
    AppName(ByteBuffer APPNAME) {
        this.APPNAME = APPNAME;
    }

    @Override
    public void accept(Stream stream) {
        byte b;
        short appname_max_left = 48;

        if (!stream.next()) {
            throw new ParseException("Expected APPNAME, received nothing");
        }
        b = stream.get();
        while (appname_max_left > 0 && b != 32) {
            if (APPNAME != null) {
                APPNAME.put(b);
            }
            appname_max_left--;

            if (!stream.next()) {
                throw new ParseException("APPNAME is too short, can't continue");
            }
            b = stream.get();
        }

        if (b != 32) {
            throw new AppNameParseException("SP missing after APPNAME or APPNAME too long");
        }
    }
}