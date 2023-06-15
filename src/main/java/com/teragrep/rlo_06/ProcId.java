package com.teragrep.rlo_06;

import java.io.IOException;
import java.nio.ByteBuffer;

final class ProcId {
    /*
                                                             ||||
                                                             vvvv
        <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

        Actions: ___O
        Payload:'DEA '
        States : ...T
        */

    private final Stream stream;
    private final ByteBuffer PROCID;
    ProcId(Stream stream, ByteBuffer PROCID) {
        this.stream = stream;
        this.PROCID = PROCID;
    }

    void praseProcId() throws IOException {
        byte b;
        short procid_max_left = 128;

        if (!stream.next()) {
            throw new ParseException("TOO SHORT");
        }
        b = stream.get();
        while (procid_max_left > 0 && b != 32) {
            if (PROCID != null)
                PROCID.put(b);
            procid_max_left--;

            if (!stream.next()) {
                throw new ParseException("TOO SHORT");
            }
            b = stream.get();
        }

        if (b != 32) {
            throw new ProcIdParseException("SP missing after PROCID or PROCID too long");
        }
    }
}
