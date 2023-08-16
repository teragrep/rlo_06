package com.teragrep.rlo_06;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public final class Msg implements Consumer<Stream>, Clearable, Byteable {
    /*
                                                                                               vvvvvvvvvv
            <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

            Actions: x_______OO
            Actions: _           // if not space
            Actions: O           // if space
            Payload:' sigsegv\n'
            States : %.......TT

            */

    private final ByteBuffer MSG;

    private final boolean lineFeedTermination;

    private FragmentState fragmentState;
    Msg(boolean lineFeedTermination) {
        this.MSG = ByteBuffer.allocateDirect(256 * 1024);
        this.lineFeedTermination = lineFeedTermination;
        this.fragmentState = FragmentState.EMPTY;
    }

    public void accept(Stream stream) {
        if (fragmentState != FragmentState.EMPTY) {
            throw new IllegalStateException("fragmentState != FragmentState.EMPTY");
        }

        int msg_current_left = 256 * 1024;

        byte oldByte = stream.get();

        if (oldByte != ' ') {
            MSG.put(oldByte);
        }
        msg_current_left--;


        // this little while here is the steamroller of this parser
        if (this.lineFeedTermination) { // Line-feed termination active
            while (stream.next()) {
                final byte b = stream.get();

                if (b == '\n') {
                    // new line is not added to the payload
                    break;
                }
                else if (msg_current_left < 1) {
                    throw new MsgParseException("MSG too long, no new line in 256K range");
                }

                MSG.put(b);
                msg_current_left--;



            }
        } else { // Line-feed termination inactive, reading until EOF
            while (stream.next()) {
                MSG.put(stream.get());
                msg_current_left--;

                if (msg_current_left < 1) {
                    throw new MsgParseException("MSG too long");
                }
            }
        }
        MSG.flip();
        fragmentState = FragmentState.WRITTEN;
    }

    @Override
    public void clear() {
        MSG.clear();
        fragmentState = FragmentState.EMPTY;
    }

    @Override
    public String toString() {
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }

        String string = StandardCharsets.UTF_8.decode(MSG).toString();
        MSG.rewind();
        return string;
    }

    @Override
    public byte[] toBytes() {
        final byte[] bytes = new byte[MSG.remaining()];
        MSG.get(bytes);
        MSG.rewind();
        return bytes;
    }
}
