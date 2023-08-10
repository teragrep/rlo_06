package com.teragrep.rlo_06;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public final class Priority implements Consumer<Stream>, Clearable {
    /*
    |||
    vvv
    <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

    Actions: O__O
    Payload:'<14>'
    States : |..T
    */
    private final ByteBuffer PRIORITY;

    Priority() {
        this.PRIORITY = ByteBuffer.allocateDirect(3);
    }


    @Override
    public void accept(Stream stream) {
        if (stream.get() != '<') {
            throw new PriorityParseException("PRIORITY < missing");
        }

        if (!stream.next()) {
            throw new ParseException("Expected PRIORITY, received nothing");
        }

        if (stream.get() >= 48 && stream.get() <= 57) { // first is always a number between 0..9
            PRIORITY.put(stream.get());
        } else {
            throw new PriorityParseException("PRIORITY number incorrect");
        }

        if (!stream.next()) {
            throw new ParseException("PRIORITY is too short, can't continue");
        }
        if (stream.get() >= 48 &&stream.get() <= 57) { // second may be a number between 0..9
            PRIORITY.put(stream.get());

            if (!stream.next()) {
                throw new ParseException("PRIORITY is too short, can't continue");
            }

            if (stream.get() >= 48 && stream.get() <= 57) { // third may be a number
                PRIORITY.put(stream.get());

                if (!stream.next()) {
                    throw new ParseException("PRIORITY is too short, can't continue");
                }

                if (stream.get() != 62) { // omit
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

    @Override
    public void clear() {
        PRIORITY.clear();
    }

    @Override
    public String toString() {
        PRIORITY.flip();
        return StandardCharsets.US_ASCII.decode(PRIORITY).toString();
    }
}
