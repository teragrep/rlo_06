package com.teragrep.rlo_06;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public final class Version implements Consumer<Stream>, Clearable {
    /*
        ||
        vv
    <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n
    
    Actions: _O
    Payload:'1 '
    States : .T
    */
    private final ByteBuffer VERSION;

    private FragmentState fragmentState;


    Version() {
        this.VERSION = ByteBuffer.allocateDirect(1);
        this.fragmentState = FragmentState.EMPTY;
    }

    @Override
    public void accept(Stream stream) {
        if (fragmentState != FragmentState.EMPTY) {
            throw new IllegalStateException("fragmentState != FragmentState.EMPTY");
        }

        byte b;

        if (!stream.next()) {
            throw new ParseException("Expected VERSION, received nothing");
        }
        b = stream.get();
        if (b == 49) {
            VERSION.put(b);

            if (!stream.next()) {
                throw new ParseException("VERSION is too short, can't continue");
            }
            b = stream.get();
            if (b != 32) { // omit ' '
                throw new VersionParseException("SP missing after VERSION");
            }
        } else {
            throw new VersionParseException("VERSION not 1");
        }
        VERSION.flip();
        fragmentState = FragmentState.WRITTEN;
    }

    @Override
    public void clear() {
        VERSION.clear();
        fragmentState = FragmentState.EMPTY;
    }

    @Override
    public String toString() {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }

        String string = StandardCharsets.US_ASCII.decode(VERSION).toString();
        VERSION.rewind();
        return string;
    }
}
