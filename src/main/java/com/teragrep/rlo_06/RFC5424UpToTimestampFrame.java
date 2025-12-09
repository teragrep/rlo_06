/*
 * Teragrep RFC5424 frame library for Java (rlo_06)
 * Copyright (C) 2022-2024 Suomen Kanuuna Oy
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.function.Consumer;

/** Reads stream only until timestamp is filled and then stops */
public final class RFC5424UpToTimestampFrame implements Frame {

    private final Stream stream;
    private final Consumer<Stream> streamConsumer;

    private final Fragment priority;
    private final Fragment version;
    private final Fragment timestamp;

    public RFC5424UpToTimestampFrame() {
        this(false);
    }

    public RFC5424UpToTimestampFrame(boolean lineFeedTermination) {
        this.priority = new Fragment(3, new PriorityFunction());
        this.version = new Fragment(1, new VersionFunction());
        this.timestamp = new Fragment(32, new TimestampFunction());
        this.stream = new Stream();

        this.streamConsumer = priority.andThen(version.andThen(timestamp));
    }

    public boolean next() throws IOException {
        /*
        Following abbreviations are used to indicate parsing in the comments.
        
        Actions done to characters: _=Store, ^=Parser variable, O=Omit
        
        O__O_O_______________________________O
        <14>1 2014-06-20T09:14:07.12345+00:00
        |..T.T...............................T
        Parsing flow states:
        |=State indicator, .= Token, T=State termination, %=State internal change
         */

        clear();

        // everything starts here, loads the '<' if it exists
        if (!stream.next()) {
            return false;
        }

        streamConsumer.accept(stream);

        return true;
    }

    private void clear() {
        priority.clear();
        version.clear();
        timestamp.clear();
    }

    public void load(final InputStream inputStream) {
        // PRI + VER + SP + TIMESTAMP
        final byte[] buffer = new byte[5 + 1 + 1 + 32];
        final int read;
        try {
            read = inputStream.read(buffer);
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        final ByteArrayInputStream streamHead = new ByteArrayInputStream(buffer, 0, read);
        stream.setInputStream(streamHead);
    }

    @Override
    public Fragment priority() {
        return priority;
    }

    @Override
    public Fragment version() {
        return version;
    }

    @Override
    public Fragment timestamp() {
        return timestamp;
    }

    @Override
    public Fragment hostname() {
        return new Fragment(true);
    }

    @Override
    public Fragment appName() {
        return new Fragment(true);
    }

    @Override
    public Fragment procId() {
        return new Fragment(true);
    }

    @Override
    public Fragment msgId() {
        return new Fragment(true);
    }

    @Override
    public StructuredData structuredData() {
        return new StructuredData(true);
    }

    @Override
    public Fragment msg() {
        return new Fragment(true);
    }
}
