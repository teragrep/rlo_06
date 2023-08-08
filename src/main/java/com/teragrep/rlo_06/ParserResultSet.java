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

/**
 * NOTE: Each ByteBuffer here is always kept in 'put' mode.
 */

public class ParserResultSet {
    // subscriptions
    final RFC5424ParserSubscription subscription;
    final RFC5424ParserSDSubscription sdSubscription;

    // results
    final ByteBuffer PRIORITY;
    final ByteBuffer VERSION;
    final ByteBuffer TIMESTAMP;
    final ByteBuffer HOSTNAME;
    final ByteBuffer APPNAME;
    final ByteBuffer PROCID;
    final ByteBuffer MSGID;
    final ByteBuffer MSG;

    // sdId and sdElement iterators, used by the parser but kept here for the consistency
    final ByteBuffer sdIdIterator;
    final ByteBuffer sdElementIterator;



    ParserResultSet(RFC5424ParserSubscription subscription, RFC5424ParserSDSubscription sdSubscription) {
        this.subscription = subscription;
        this.sdSubscription = sdSubscription;

        if (this.subscription.subscription.contains(ParserEnum.PRIORITY)) {
            this.PRIORITY = ByteBuffer.allocateDirect(3);
        }
        else {
            this.PRIORITY = null;
        }

        if (this.subscription.subscription.contains(ParserEnum.VERSION)) {
            this.VERSION = ByteBuffer.allocateDirect(1);
        }
        else {
            this.VERSION = null;
        }

        if (this.subscription.subscription.contains(ParserEnum.TIMESTAMP)) {
            this.TIMESTAMP = ByteBuffer.allocateDirect(32);
        }
        else {
            this.TIMESTAMP = null;
        }

        if (this.subscription.subscription.contains(ParserEnum.HOSTNAME)) {
            this.HOSTNAME = ByteBuffer.allocateDirect(255);
        }
        else {
            this.HOSTNAME = null;
        }

        if (this.subscription.subscription.contains(ParserEnum.APPNAME)) {
            this.APPNAME = ByteBuffer.allocateDirect(48);
        }
        else {
            this.APPNAME = null;
        }

        if (this.subscription.subscription.contains(ParserEnum.PROCID)) {
            this.PROCID = ByteBuffer.allocateDirect(128);
        }
        else {
            this.PROCID = null;
        }

        if (this.subscription.subscription.contains(ParserEnum.MSGID)) {
            this.MSGID = ByteBuffer.allocateDirect(32);
        }
        else {
            this.MSGID = null;
        }

        if (this.subscription.subscription.contains(ParserEnum.MSG)) {
            this.MSG = ByteBuffer.allocateDirect(256 * 1024);
        }
        else {
            this.MSG = null;
        }

        // sdIterator allocations
        this.sdIdIterator = ByteBuffer.allocateDirect(32);
        this.sdElementIterator = ByteBuffer.allocateDirect(32);
    }

    public void clear() {
        if(this.PRIORITY != null)
            this.PRIORITY.clear();
        if(this.VERSION != null)
            this.VERSION.clear();
        if(this.TIMESTAMP != null)
            this.TIMESTAMP.clear();
        if(this.HOSTNAME != null)
            this.HOSTNAME.clear();
        if(this.APPNAME != null)
            this.APPNAME.clear();
        if(this.PROCID != null)
            this.PROCID.clear();
        if(this.MSGID != null)
            this.MSGID.clear();
        if(this.MSG != null)
            this.MSG.clear();

        this.sdSubscription.clear();

        this.sdIdIterator.clear();
        this.sdElementIterator.clear();
    }
}
