package com.teragrep.rlo_06;

import java.io.IOException;
import java.nio.ByteBuffer;

final class Priority {
    /*
    |||
    vvv
    <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

    Actions: O__O
    Payload:'<14>'
    States : |..T
    */

    private final Stream stream;
    private final ByteBuffer PRIORITY;

    Priority(Stream stream, ByteBuffer PRIORITY) {
        this.stream = stream;
        this.PRIORITY = PRIORITY;
    }

    void parsePriority() throws IOException {
        if (stream.get() != '<') {
            throw new PriorityParseException("PRIORITY < missing");
        }

        if (!stream.next()) {
            throw new ParseException("TOO SHORT");
        }

        if (stream.get() >= 48 && stream.get() <= 57) { // first is always a number between 0..9
            if (PRIORITY != null)
                PRIORITY.put(stream.get());
        } else {
            throw new PriorityParseException("PRIORITY number incorrect");
        }

        if (!stream.next()) {
            throw new ParseException("TOO SHORT");
        }
        if (stream.get() >= 48 &&stream.get() <= 57) { // second may be a number between 0..9
            if (PRIORITY != null)
                PRIORITY.put(stream.get());

            if (!stream.next()) {
                throw new ParseException("TOO SHORT");
            }

            if (stream.get() >= 48 && stream.get() <= 57) { // third may be a number
                if (PRIORITY != null)
                    PRIORITY.put(stream.get());

                if (!stream.next()) {
                    throw new ParseException("TOO SHORT");
                }

                if (stream.get() != '>') { // omit
                    throw new PriorityParseException("PRIORITY > missing");
                }
            } else if (stream.get() == 62) { // third may be a '>'
// omit '>'
            } else {
                throw new PriorityParseException("PRIORITY number incorrect");
            }
        } else if (stream.get() == 62) { // second may be a '>'
            // omit '>'
        } else {
            throw new PriorityParseException("PRIORITY number incorrect");
        }
    }
}
