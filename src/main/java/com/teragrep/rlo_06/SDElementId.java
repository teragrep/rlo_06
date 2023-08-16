package com.teragrep.rlo_06;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public final class SDElementId implements Consumer<Stream>, Clearable, Matchable, Byteable {

    private final ByteBuffer sdId;
    private FragmentState fragmentState;


    SDElementId() {
        this.sdId = ByteBuffer.allocateDirect(32);
        this.fragmentState = FragmentState.EMPTY;
    }

    @Override
    public void accept(Stream stream) {
        if (fragmentState != FragmentState.EMPTY) {
            throw new IllegalStateException("fragmentState != FragmentState.EMPTY");
        }
        byte b;

        // parse the sdId
        short sdId_max_left = 32;
                    /*
                              vvvvvv
                    Payload:'[ID_A@1 u="3" e="t"][ID_B@2 n="9"] '
                    Payload:'[ID_A@1]'
                    */

        if (!stream.next()) {
            throw new ParseException("SD is too short, can't continue");
        }
        b = stream.get();
        while (sdId_max_left > 0 && b != 32 && b != 93) { // ' ' nor ']'
            sdId.put(b);
            sdId_max_left--;

            if (!stream.next()) {
                throw new ParseException("SD is too short, can't continue");
            }
            b = stream.get();
        }
        sdId.flip();
        fragmentState = FragmentState.WRITTEN;
    }

    @Override
    public void clear() {
        sdId.clear();
        fragmentState = FragmentState.EMPTY;
    }

    @Override
    public String toString() {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }
        String string = StandardCharsets.UTF_8.decode(sdId).toString();
        sdId.rewind();
        return string;
    }

    @Override
    public boolean matches(ByteBuffer buffer) {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }
        return sdId.equals(buffer);
    }

    @Override
    public byte[] toBytes() {
        final byte[] bytes = new byte[sdId.remaining()];
        sdId.get(bytes);
        sdId.rewind();
        return bytes;
    }
}
