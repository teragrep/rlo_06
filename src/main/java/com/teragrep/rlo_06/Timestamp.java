package com.teragrep.rlo_06;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public final class Timestamp implements Consumer<Stream>, Clearable, Byteable {
    /*
                          ||||||||||||||||||||||||||||||||
                          vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
                    <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

                    Actions: _______________________________O
                    Payload:'2014-06-20T09:14:07.12345+00:00 '
                    States : ...............................T
                    */
    private final ByteBuffer TIMESTAMP;

    private FragmentState fragmentState;

    Timestamp() {
        this.TIMESTAMP = ByteBuffer.allocateDirect(32);
        this.fragmentState = FragmentState.EMPTY;
    }

    public void accept(Stream stream) {
        if (fragmentState != FragmentState.EMPTY) {
            throw new IllegalStateException("fragmentState != FragmentState.EMPTY");
        }

        byte b;
        short ts_max_left = 32;

        if (!stream.next()) {
            throw new ParseException("Expected TIMESTAMP, received nothing");
        }
        b = stream.get();
        while (ts_max_left > 0 && b != 32) {
            TIMESTAMP.put(b);
            ts_max_left--;

            if (!stream.next()) {
                throw new ParseException("TIMESTAMP is too short, can't continue");
            }
            b = stream.get();
        }

        if (b != 32) {
            throw new TimestampParseException("SP missing after TIMESTAMP or TIMESTAMP too long");
        }
        TIMESTAMP.flip();
        fragmentState = FragmentState.WRITTEN;
    }

    @Override
    public void clear() {
        TIMESTAMP.clear();
        fragmentState = FragmentState.EMPTY;
    }

    @Override
    public String toString() {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }

        String string = StandardCharsets.US_ASCII.decode(TIMESTAMP).toString();
        TIMESTAMP.rewind();
        return string;
    }

    @Override
    public byte[] toBytes() {
        final byte[] bytes = new byte[TIMESTAMP.remaining()];
        TIMESTAMP.get(bytes);
        TIMESTAMP.rewind();
        return bytes;
    }
}
