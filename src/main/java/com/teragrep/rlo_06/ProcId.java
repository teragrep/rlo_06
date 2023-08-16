package com.teragrep.rlo_06;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public final class ProcId implements Consumer<Stream>, Clearable {
    /*
                                                             ||||
                                                             vvvv
        <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

        Actions: ___O
        Payload:'DEA '
        States : ...T
        */

    private final ByteBuffer PROCID;

    private FragmentState fragmentState;

    ProcId() {
        this.PROCID = ByteBuffer.allocateDirect(128);
        this.fragmentState = FragmentState.EMPTY;
    }

    public void accept(Stream stream) {
        if (fragmentState != FragmentState.EMPTY) {
            throw new IllegalStateException("fragmentState != FragmentState.EMPTY");
        }

        byte b;
        short procid_max_left = 128;

        if (!stream.next()) {
            throw new ParseException("Expected PROCID, received nothing");
        }
        b = stream.get();
        while (procid_max_left > 0 && b != 32) {
            PROCID.put(b);
            procid_max_left--;

            if (!stream.next()) {
                throw new ParseException("PROCID is too short, can't continue");
            }
            b = stream.get();
        }

        if (b != 32) {
            throw new ProcIdParseException("SP missing after PROCID or PROCID too long");
        }
        PROCID.flip();
        fragmentState = FragmentState.WRITTEN;
    }

    @Override
    public void clear() {
        PROCID.clear();
        fragmentState = FragmentState.EMPTY;
    }

    @Override
    public String toString() {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }

        String string = StandardCharsets.US_ASCII.decode(PROCID).toString();
        PROCID.rewind();
        return string;
    }
}
