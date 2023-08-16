/*
 * Java RFC524 parser library  RLO-06
 * Copyright (C) 2022  Suomen Kanuuna Oy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 * Additional permission under GNU Affero General Public License version 3
 * section 7
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with other code, such other code is not for that reason alone subject to any
 * of the requirements of the GNU Affero GPL version 3 as long as this Program
 * is the same Program as licensed from Suomen Kanuuna Oy without any additional
 * modifications.
 *
 * Supplemented terms under GNU Affero General Public License version 3
 * section 7
 *
 * Origin of the software must be attributed to Suomen Kanuuna Oy. Any modified
 * versions must be marked as "Modified version of" The Program.
 *
 * Names of the licensors and authors may not be used for publicity purposes.
 *
 * No rights are granted for use of trade names, trademarks, or service marks
 * which are in The Program if any.
 *
 * Licensee must indemnify licensors and authors for any liability that these
 * contractual assumptions impose on licensors and authors.
 *
 * To the extent this program is licensed as part of the Commercial versions of
 * Teragrep, the applicable Commercial License may apply to this file if you as
 * a licensee so wish it.
 */
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
        if (fragmentState != FragmentState.WRITTEN) {
            throw new IllegalStateException("fragmentState != FragmentState.WRITTEN");
        }

        final byte[] bytes = new byte[MSG.remaining()];
        MSG.get(bytes);
        MSG.rewind();
        return bytes;
    }
}
