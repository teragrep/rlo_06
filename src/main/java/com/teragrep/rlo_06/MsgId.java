package com.teragrep.rlo_06;

import java.io.IOException;
import java.nio.ByteBuffer;

final class MsgId {
    /*
                                                                             |||||||
                                                                             vvvvvvv
                    <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

                    Actions: ______O
                    Payload:'MSG-01 '
                    States : ......T
                    */
    private final Stream stream;
    private final ByteBuffer MSGID;
    MsgId(Stream stream, ByteBuffer MSGID) {
        this.stream = stream;
        this.MSGID = MSGID;
    }

    void parseMsgId() throws IOException {
        byte b;
        short msgid_max_left = 32;

        if (!stream.next()) {
            throw new ParseException("TOO SHORT");
        }
        b = stream.get();
        while (msgid_max_left > 0 && b != 32) {
            if (MSGID != null)
                MSGID.put(b);
            msgid_max_left--;

            if (!stream.next()) {
                throw new ParseException("TOO SHORT");
            }
            b = stream.get();
        }

        if (b != 32) {
            throw new MsgIdParseException("SP missing after MSGID or MSGID too long");
        }
    }
}
