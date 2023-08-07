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

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public final class RFC5424Parser {
    private Stream stream;
    private final ParserResultSet resultset;
    private final Consumer<Stream> streamConsumer;

    public RFC5424Parser(RFC5424ParserSubscription subscription, RFC5424ParserSDSubscription sdSubscription) {
        this(subscription, sdSubscription, true);
    }

    public RFC5424Parser(RFC5424ParserSubscription subscription, RFC5424ParserSDSubscription sdSubscription, boolean lineFeedTermination) {
        this.resultset = new ParserResultSet(subscription, sdSubscription);
        this.streamConsumer = new Priority(this.resultset.PRIORITY)
                .andThen(new Version(this.resultset.VERSION)
                        .andThen(new Timestamp(this.resultset.TIMESTAMP)
                                .andThen(new Hostname(this.resultset.HOSTNAME)
                                        .andThen(new AppName(this.resultset.APPNAME)
                                                .andThen(new ProcId(this.resultset.PROCID)
                                                        .andThen(new MsgId(this.resultset.MSGID)
                                                                .andThen(new StructuredData(this.resultset)
                                                                        .andThen(new Msg(this.resultset.MSG, lineFeedTermination))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                );
    }

    public RFC5424Parser(RFC5424ParserSubscription subscription, RFC5424ParserSDSubscription sdSubscription, InputStream inputStream) {
        this(subscription, sdSubscription, inputStream, true);
    }

    public RFC5424Parser(RFC5424ParserSubscription subscription, RFC5424ParserSDSubscription sdSubscription, InputStream inputStream, boolean lineFeedTermination) {
        this.stream = new Stream(inputStream);
        this.resultset = new ParserResultSet(subscription, sdSubscription);
        this.streamConsumer = new Priority(this.resultset.PRIORITY)
                .andThen(new Version(this.resultset.VERSION)
                        .andThen(new Timestamp(this.resultset.TIMESTAMP)
                                .andThen(new Hostname(this.resultset.HOSTNAME)
                                        .andThen(new AppName(this.resultset.APPNAME)
                                                .andThen(new ProcId(this.resultset.PROCID)
                                                        .andThen(new MsgId(this.resultset.MSGID)
                                                                .andThen(new StructuredData(this.resultset)
                                                                        .andThen(new Msg(this.resultset.MSG, lineFeedTermination))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                );
    }

    public void setInputStream(InputStream inputStream) {
        stream = new Stream(inputStream);
    }

    public boolean next() throws IOException {
        /*
        Following abbreviations are used to indicate parsing in the comments.

        Actions done to characters: _=Store, ^=Parser variable, O=Omit

        O__O_O_______________________________O______O_______O___O______OO^^^^^^O^OO_OO^OO_OOO^^^^^^O^OO_OO________OO
        <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n
        |..T.T...............................T......T.......T...T......T|......%.%%.%%.%%.%%|......%.%%.%T
        Parsing flow states:
        |=State indicator, .= Token, T=State termination, %=State internal change
         */

        resultset.clear();

        // everything starts here, loads the '<' if it exists
        if (!stream.next()) {
            return false;
        }

        streamConsumer.accept(stream);

        return true;
    }

    public ParserResultSet get() throws IOException {
        return resultset;
    }
}
