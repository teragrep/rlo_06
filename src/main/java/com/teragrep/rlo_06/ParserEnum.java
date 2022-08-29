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

/*
public static final String SYSLOG_LINE_ALL = "<14>1 2014-06-20T09:14:07+00:00 loggregator"
            + " d0602076-b14a-4c55-852a-981e7afeed38 DEA MSG-01"
            + " [exampleSDID@32473 iut=\"3\" eventSource=\"Application\" eventID=\"1011\"]"
            + "[exampleSDID@32480 iut=\"4\" eventSource=\"Other Application\" eventID=\"2022\"] Removing instance";
 */

public enum ParserEnum {
    PRIORITY {
        @Override
        public ParserEnum nextState() {
            return VERSION;
        }
    },
    VERSION {
        @Override
        public ParserEnum nextState() {
            return TIMESTAMP;
        }
    },
    TIMESTAMP {
        @Override
        public ParserEnum nextState() {
            return HOSTNAME;
        }
    },
    HOSTNAME {
        @Override
        public ParserEnum nextState() {
            return APPNAME;
        }
    },
    APPNAME {
        @Override
        public ParserEnum nextState() {
            return PROCID;
        }
    },
    PROCID {
        @Override
        public ParserEnum nextState() {
            return MSGID;
        }
    },
    MSGID  {
        @Override
        public ParserEnum nextState() {
            return SD_PARSE;
        }
    },
    SD_PARSE  {
        @Override
        public ParserEnum nextState() {
            return MSG;
        }
    },
    MSG  {
        @Override
        public ParserEnum nextState() {
            return NL;
        }
    },
    NL {
        @Override
        public ParserEnum nextState() {
            return this;
        }
    };

    public abstract ParserEnum nextState();
    }
