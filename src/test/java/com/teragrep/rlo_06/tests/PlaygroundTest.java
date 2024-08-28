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
package com.teragrep.rlo_06.tests;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.Instant;

public class PlaygroundTest {

    //@Test
    void ReadableByteChannelPerformance() throws Exception {
        String payload = new String(new char[128 * 1000]).replace("\0", "X");
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"][event_id@48577 hostname=\"sc-99-99-14-247\" uuid=\"0FD92E51B37748EB90CD894CCEE63907\" unixtime=\"1612047600.0\" id_source=\"source\"][event_node_source@48577 hostname=\"sc-99-99-14-247\" source=\"f17_ssmis_20210131v7.nc\" source_module=\"imfile\"][event_node_relay@48577 hostname=\"localhost\" source=\"sc-99-99-14-247\" source_module=\"imrelp\"][event_version@48577 major=\"2\" minor=\"2\" hostname=\"localhost\" version_source=\"relay\"][event_node_router@48577 source=\"logrouter.example.com\" source_module=\"imrelp\" hostname=\"localhost\"][teragrep@48577 streamname=\"log:f17:0\" directory=\"com_teragrep_audit\" unixtime=\"1612047600.0\"] "
                + payload + "\n";

        Instant instant1 = Instant.now();
        int count = 128102000;
        //int count = 1000000;

        final File initialFile = new File("output.txt");
        final InputStream inputStream = new FileInputStream(initialFile);
        // final InputStream inputStream = new BufferedInputStream(new FileInputStream("output.txt"));
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024 * 128);
        ReadableByteChannel ioChan = Channels.newChannel(inputStream);
        ioChan.read(byteBuffer);

        byteBuffer.flip();
        System.out.println(byteBuffer);
        System.out.println(inputStream);

        byte b = 0;
        int pulls = 0;
        for (int i = 0; i < count; i++) {
            if (!byteBuffer.hasRemaining()) {
                byteBuffer.flip();
                byteBuffer.clear();
                ioChan.read(byteBuffer);
                byteBuffer.flip();

            }
            while (byteBuffer.hasRemaining()) {
                b = byteBuffer.get();
                pulls++;
                //System.out.print(new String(new byte[] {b}));
            }
        }
        Instant instant2 = Instant.now();

        System.out.println(new String(new byte[] {
                b
        }));
        System.out.println(pulls);
        int msgsize = count * SYSLOG_MESSAGE.length();

        long spent = instant2.toEpochMilli() - instant1.toEpochMilli();
        System.out
                .println(
                        "ReadableByteChannelPerformance: time taken " + spent + " for " + count
                                + ", total chars per second: " + (float) count / ((float) spent / 1000) + ", "
                                + (float) msgsize / 1024 / 1024 + " megabytes ("
                                + (float) (msgsize / ((float) spent / 1000)) / 1024 / 1024 + " MB/s)"
                );
    }

    //@Test
    void bufferFast() throws IOException {

        final File initialFile = new File("output.txt");
        final InputStream inputStream = new FileInputStream(initialFile);

        byte b = 0;

        Instant instant1 = Instant.now();
        long count = 0;
        int lines = 0;

        byte[] buffer = new byte[1024 * 128];
        int pointer = 0;
        int read = inputStream.read(buffer, 0, buffer.length);

        while (read > 0) {
            b = buffer[pointer++];
            if (b == 10) {
                lines++;
            }
            count++;

            read--;
            if (read == 0) {
                read = inputStream.read(buffer, 0, buffer.length);
                pointer = 0;
            }
        }
        Instant instant2 = Instant.now();

        System.out.println(new String(new byte[] {
                b
        }));
        long spent = instant2.toEpochMilli() - instant1.toEpochMilli();
        System.out
                .println(
                        "puhHuijaaTsuuh: time taken " + spent + " for " + count + ", total chars per second: "
                                + (float) count / ((float) spent / 1000) + ", "
                                + (float) initialFile.length() / 1024 / 1024 + " megabytes ("
                                + (float) (initialFile.length() / ((float) spent / 1000)) / 1024 / 1024 + " MB/s)"
                );

        System.out
                .println(
                        "puhHuijaaTsuuh: time taken " + spent + " for " + lines + ", total lines per second: "
                                + (float) lines / ((float) spent / 1000)
                );
    }

}
