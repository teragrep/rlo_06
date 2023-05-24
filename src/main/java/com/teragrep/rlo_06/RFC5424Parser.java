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
import java.nio.ByteBuffer;

public final class RFC5424Parser {
    private InputStream inputStream;
    private final boolean lineFeedTermination;
    private Boolean EOF = false;
    private final byte[] buffer = new byte[256 * 1024];
    private int pointer = -1;
    private int read = -1;

    public RFC5424Parser(InputStream inputStream) {
        this.inputStream = inputStream;
        this.lineFeedTermination = true;
    }

    public RFC5424Parser(InputStream inputStream, boolean lineFeedTermination) {
        this.inputStream = inputStream;
        this.lineFeedTermination = lineFeedTermination;
    }

    public void setInputStream(InputStream inputStream) {
        this.EOF = false;
        this.pointer = -1;
        this.read = -1;
        this.inputStream = inputStream;
    }

    private byte readBuffer() throws IOException {
        byte b;

        if (pointer == read) {
            read = inputStream.read(buffer, 0, buffer.length);
            if (read == -1 && !this.EOF) {
                // EOF met
                this.EOF = true;
            }
            pointer = 0;
        }

        b = buffer[pointer++];

        return b;
    }

    private void parsePriority(ParserResultset resultset) throws IOException {
        byte b;
        b = this.readBuffer(); // omit first <

        if (this.EOF) // checks if next actually even exists
            return;

        if (b != 60) { // '<'
            throw new PriorityParseException("PRIORITY < missing" + " read: " + read + " pointer: " + pointer);
        }

        b = this.readBuffer();
        if (b >= 48 && b <= 57) { // first is always a number between 0..9
            if (resultset.PRIORITY != null)
                resultset.PRIORITY.put(b);
        } else {
            throw new PriorityParseException("PRIORITY number incorrect");
        }

        b = this.readBuffer();
        if (b >= 48 && b <= 57) { // second may be a number between 0..9
            if (resultset.PRIORITY != null)
                resultset.PRIORITY.put(b);

            b = this.readBuffer();
            if (b >= 48 && b <= 57) { // third may be a number
                if (resultset.PRIORITY != null)
                    resultset.PRIORITY.put(b);

                b = this.readBuffer();
                if (b != 62) { // '>' must be after three numbers, omit
                    throw new PriorityParseException("PRIORITY > missing");
                }
            } else if (b == 62) { // third may be a '>'
                // omit '>'
            } else {
                throw new PriorityParseException("PRIORITY number incorrect");
            }
        } else if (b == 62) { // second may be a '>'
            // omit '>'
        } else {
            throw new PriorityParseException("PRIORITY number incorrect");
        }
    }


    private byte parseSD(ParserResultset resultset) throws IOException {
        byte b;
        /*
                                                                                |||||||||||||||||||||||||||||||||||
                                                                                vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvR
                <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

                Actions: O^^^^^^O^OO_OO^OO_OOO^^^^^^O^OO_OO
                Payload:'[ID_A@1 u="3" e="t"][ID_B@2 n="9"]'
                States : |......%.%%.%%.%%.%%|......%.%%.%T

                NOTE this does not provide any proof what so ever if certain sdId exist or not, we are only interested
                in values if they exist.
                */

                /*
                         v
                Payload:'[ID_A@1 u="3" e="t"][ID_B@2 n="9"] ' // sd exists
                Payload:'- ' // no sd
                 */
        b = this.readBuffer();

        if (b != 45 && b != 91) { // '-' nor '['
            throw new StructuredDataParseException("SD does not contain '-' or '['");
        }

                /*
                 NOTE: here the SD parser may slip into the message, how dirty of it but
                 as "-xyz" or "- xyz" or "]xyz" or "] xyz" may exist we need to handle them here.
                 */

        if (b == 45) {
            // if '-' then R(ead) and pass to next state
            b = this.readBuffer();
            return b;
        }

        while (b == 91) { // '[' sd exists
            // structured data, oh wow the performance hit

            // parse the sdId
            short sdId_max_left = 32;
                    /*
                              vvvvvv
                    Payload:'[ID_A@1 u="3" e="t"][ID_B@2 n="9"] '
                    Payload:'[ID_A@1]'
                    */
            b = this.readBuffer();
            while (sdId_max_left > 0 && b != 32 && b != 93) { // ' ' nor ']'
                resultset.sdIdIterator.put(b);
                sdId_max_left--;
                b = this.readBuffer();
            }
            resultset.sdIdIterator.flip(); // flip to READ so the compare works

            if (b != 32 && b != 93) { // ' ' nor ']'
                throw new StructuredDataParseException("SP missing after SD_ID or SD_ID too long");
            } else if (b == 93) { // ']', sdId only here: Payload:'[ID_A@1]' or Payload:'[ID_A@1][ID_B@1]'
                // clean up sdIterator for the next one
                resultset.sdIdIterator.flip();
                resultset.sdIdIterator.clear();

                // MSG may not exist, no \n either, Parsing may be complete. readBuffer sets this.returnAfter to false
                // Total payload: '<14>1 2015-06-20T09:14:07.12345+00:00 host02 serverd DEA MSG-01 [ID_A@1]'
            } else { // ' ', sdElement must exist
                // check if we are interested in this sdId at all or skip to next sdId block

                if (resultset.sdSubscription.isSubscribedSDId(resultset.sdIdIterator)) {
                    while (b == 32) { // multiple ' ' separated sdKey="sdValue" pairs may exist
                        short sdElemKey_max_left = 32;
                        b = this.readBuffer();
                        while (sdElemKey_max_left > 0 && b != 61) { // '='
                            resultset.sdElementIterator.put(b);
                            sdElemKey_max_left--;
                            b = this.readBuffer();
                        }
                        resultset.sdElementIterator.flip(); // flip to READ so the compare works

                        if (b != 61) { // '='
                            throw new StructuredDataParseException("EQ missing after SD_KEY or SD_KEY too long");
                        }

                        b = this.readBuffer();
                        if (b != 34) { // '"'
                            throw new StructuredDataParseException("\" missing after SD_KEY EQ");
                        }

                        // check if this is for us
                        if (resultset.sdSubscription.isSubscribedSDElement(resultset.sdIdIterator, resultset.sdElementIterator)) {
                            ByteBuffer elementValue = resultset.sdSubscription.getSubscribedSDElementBuffer(resultset.sdIdIterator, resultset.sdElementIterator);
                            short sdElemVal_max_left = 8 * 1024;
                            b = this.readBuffer();

                            while (sdElemVal_max_left > 0 && b != 34) { // '"'
                                // escaped are special: \" \\ \] ...
                                if (b == 92) { // \
                                    // insert
                                    elementValue.put(b);
                                    sdElemVal_max_left--;
                                    // read next
                                    b = this.readBuffer();

                                    // if it is a '"' then it must be taken care of, loop can do the rest
                                    if (b == 34) {
                                        if (sdElemVal_max_left > 0) {
                                            elementValue.put(b);
                                            sdElemVal_max_left--;
                                            b = this.readBuffer();
                                        }
                                    }
                                } else {
                                    elementValue.put(b);
                                    sdElemVal_max_left--;
                                    b = this.readBuffer();
                                }
                            }
                        } else {
                            // skip through, no subscription for this sdElem
                            b = this.readBuffer();
                            while (b != 34) { // '"'
                                // escaped are special: \" \\ \] ...
                                if (b == 92) { // \
                                    // read next
                                    b = this.readBuffer();
                                    // if it is a '"' then it must be taken care of, loop can do the rest
                                    if (b == 34) {
                                        b = this.readBuffer();
                                    }
                                } else {
                                    b = this.readBuffer();
                                }
                            }
                        }

                        // clean up sdElementIterator for the next one
                        resultset.sdElementIterator.flip();
                        resultset.sdElementIterator.clear();

                        // take next one for the while to check if ' ' or if to break it
                        b = this.readBuffer();
                    } // while (b == 32) { // multiple ' ' separated sdKey="sdValue" pairs may exist
                } // if (this.resultset.sdSubscription.containsKey(this.resultset.sdIdIterator))
                else {
                    // TODO skip through to next block
                    b = this.readBuffer();
                    while (b != 93) { // ']'
                        // escaped '\]' are special:
                        if (b == 92) { // \
                            // read next
                            b = this.readBuffer();
                            // if it is a ']' then it must be taken care of, loop can do the rest
                            if (b == 93) {
                                b = this.readBuffer();
                            }
                        } else {
                            b = this.readBuffer();
                        }
                    }
                }
                // clean up sdIterator for the next one
                resultset.sdIdIterator.flip();
                resultset.sdIdIterator.clear();
            }
                    /*
                                                    vv            vv
                        Payload:'[ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n'
                        Payload:            '[ID_A@1] sigsegv\n'
                        */
            b = this.readBuffer(); // will it be '[' or the MSG who knows.
            // let's find out, note if not '[' then R(ead) and pass to next state
        } // while(b == 91) { // '[' sd exists
        return b;
    }

    private byte parseMSG(ParserResultset resultset, byte lastByte) throws IOException {
        byte b;
        b = lastByte;
        /*
                                                                                                                  ||||||||||
                                                                                                                  vvvvvvvvvv
                <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

                Actions: x_______OO
                Actions: _           // if not space
                Actions: O           // if space
                Payload:' sigsegv\n'
                States : %.......TT

                */
        int msg_current_left = 256 * 1024;

        // first of anything is ' '
        // first
        if (b != 32) { // space is skipped as "-xyz" or "- xyz" or "]xyz" or "] xyz" may exist
            resultset.MSG.put(b);
            msg_current_left--;
        } else { // read next byte because this one is a space
            b = this.readBuffer();
        }

        // this little while here is the steamroller of this parser
        if (this.lineFeedTermination) { // Line-feed termination active
            while (b != 10 && !this.EOF) {
                if (msg_current_left > 0) {
                    if (resultset.MSG != null) {
                        resultset.MSG.put(b);
                    }
                    msg_current_left--;
                }
                b = this.readBuffer();
            }
        } else { // Line-feed termination inactive, reading until EOF
            while (!this.EOF) {
                if (msg_current_left > 0) {
                    if (resultset.MSG != null) {
                        resultset.MSG.put(b);
                    }
                    msg_current_left--;
                }
                b = this.readBuffer();
            }
        }
        return b;
    }

    private void parseVERSION(ParserResultset resultset) throws IOException {
        byte b;
        b = this.readBuffer();
        if (b == 49) {
            if (resultset.VERSION != null)
                resultset.VERSION.put(b);

            b = this.readBuffer();
            if (b != 32) { // omit ' '
                throw new VersionParseException("SP missing after VERSION");
            }
        } else {
            throw new VersionParseException("VERSION not 1");
        }
    }

    private void parseTIMESTAMP(ParserResultset resultset) throws IOException {
        byte b;
        short ts_max_left = 32;
        b = this.readBuffer();
        while (ts_max_left > 0 && b != 32) {
            if (resultset.TIMESTAMP != null)
                resultset.TIMESTAMP.put(b);
            ts_max_left--;
            b = this.readBuffer();
        }

        if (b != 32) {
            throw new TimestampParseException("SP missing after TIMESTAMP or TIMESTAMP too long");
        }
    }

    private void parseHOSTNAME(ParserResultset resultset) throws IOException {
        byte b;
        short hostname_max_left = 255;
        b = this.readBuffer();
        while (hostname_max_left > 0 && b != 32) {
            if (resultset.HOSTNAME != null)
                resultset.HOSTNAME.put(b);
            hostname_max_left--;
            b = this.readBuffer();
        }

        if (b != 32) {
            throw new HostnameParseException("SP missing after HOSTNAME or HOSTNAME too long");
        }
    }

    private void parseAPPNAME(ParserResultset resultset) throws IOException {
        byte b;
        short appname_max_left = 48;
        b = this.readBuffer();
        while (appname_max_left > 0 && b != 32) {
            if (resultset.APPNAME != null)
                resultset.APPNAME.put(b);
            appname_max_left--;
            b = this.readBuffer();
        }

        if (b != 32) {
            throw new AppNameParseException("SP missing after APPNAME or APPNAME too long");
        }
    }

    private void parsePROCID(ParserResultset resultset) throws IOException {
        byte b;
        short procid_max_left = 128;
        b = this.readBuffer();
        while (procid_max_left > 0 && b != 32) {
            if (resultset.PROCID != null)
                resultset.PROCID.put(b);
            procid_max_left--;
            b = this.readBuffer();
        }

        if (b != 32) {
            throw new ProcIdParseException("SP missing after PROCID or PROCID too long");
        }
    }

    private void parseMSGID(ParserResultset resultset) throws IOException {
        byte b;
        short msgid_max_left = 32;
        b = this.readBuffer();
        while (msgid_max_left > 0 && b != 32) {
            if (resultset.MSGID != null)
                resultset.MSGID.put(b);
            msgid_max_left--;
            b = this.readBuffer();
        }

        if (b != 32) {
            throw new MsgIdParseException("SP missing after MSGID or MSGID too long");
        }
    }

    public boolean next(ParserResultset resultset) throws IOException {
        byte b = 0;

        ParserEnum parserState = ParserEnum.PRIORITY;

        // TODO use the subscription toggling
        //System.out.println(new String(b));
        // flow through switch case, just to structure it out

        /*
        Following abbreviations are used to indicate parsing in the comments.

        Actions done to characters: _=Store, ^=Parser variable, O=Omit

        O__O_O_______________________________O______O_______O___O______OO^^^^^^O^OO_OO^OO_OOO^^^^^^O^OO_OO________OO
        <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n
        |..T.T...............................T......T.......T...T......T|......%.%%.%%.%%.%%|......%.%%.%T
        Parsing flow states:
        |=State indicator, .= Token, T=State termination, %=State internal change
         */

        // switch (parserState) {

            /*
                |||
                vvv
                <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

                Actions: O__O
                Payload:'<14>'
                States : |..T
                 */
        this.parsePriority(resultset);

        if (this.EOF)
            return false; // there was no data, returning false

        parserState = parserState.nextState();
        // fall through
            /*
                    ||
                    vv
                <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

                Actions: _O
                Payload:'1 '
                States : .T
                */
        this.parseVERSION(resultset);
        parserState.nextState();
        // fall through

            /*
                      ||||||||||||||||||||||||||||||||
                      vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
                <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

                Actions: _______________________________O
                Payload:'2014-06-20T09:14:07.12345+00:00 '
                States : ...............................T
                */
        this.parseTIMESTAMP(resultset);
        parserState.nextState();
        // fall through

            /*
                                                      |||||||
                                                      vvvvvvv
                <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

                Actions: ______O
                Payload:'host01 '
                States : ......T
                */
        this.parseHOSTNAME(resultset);
        parserState.nextState();
        // fall through

            /*
                                                             ||||||||
                                                             vvvvvvvv
                <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

                Actions: _______O
                Payload:'systemd '
                States : .......T
                */
        this.parseAPPNAME(resultset);
        parserState.nextState();
        // fall through

            /*
                                                                     ||||
                                                                     vvvv
                <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

                Actions: ___O
                Payload:'DEA '
                States : ...T
                */
        this.parsePROCID(resultset);
        parserState.nextState();
        // fall through

            /*
                                                                         |||||||
                                                                         vvvvvvv
                <14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u="3" e="t"][ID_B@2 n="9"] sigsegv\n

                Actions: ______O
                Payload:'MSG-01 '
                States : ......T
                */
        this.parseMSGID(resultset);
        parserState.nextState();
        // fall through

        b = this.parseSD(resultset);
        if (this.EOF)
            return true; // there was data, returning true

        parserState.nextState();
        // fall through

        b = this.parseMSG(resultset, b);
        if (this.EOF) {
            return true; // there was data, returning true
        }
        parserState.nextState();
        // fall through

        if (b != 10) {
            throw new MsgParseException("NL missing after MSG or MSG too long");
        }
        return true; // there was data, returning true
    } // public void parseStream(InputStream inputStream) throws IOException {
}
