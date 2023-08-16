package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimestampConversionTest {
    @Test
    public void testTimestampConversion() throws IOException {
        Assertions.assertEquals("2003-08-24T05:14:15.000003-07:00", getTimestamp("2003-08-24T05:14:15.000003-07:00"));
        Assertions.assertEquals("1985-04-12T23:20:50.520Z", getTimestamp("1985-04-12T23:20:50.52Z"));
        Assertions.assertEquals("1985-04-12T19:20:50.520-04:00", getTimestamp("1985-04-12T19:20:50.52-04:00"));
        Assertions.assertEquals("2003-10-11T22:14:15.003Z", getTimestamp("2003-10-11T22:14:15.003Z"));
        Assertions.assertEquals("2023-08-16T17:09:00.123456+03:00", getTimestamp("2023-08-16T17:09:00.123456+03:00"));
        // Missing seconds is a feature:
        // "The format used will be the shortest that outputs the full value of the time where the omitted parts are implied to be zero."
        // Source: https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html#toString--
        Assertions.assertEquals("2023-01-01T00:00Z", getTimestamp("2023-01-01T00:00:00.000000+00:00"));
        Assertions.assertEquals("2023-01-01T00:00Z", getTimestamp("2023-01-01T00:00:00.000000-00:00"));
        Assertions.assertEquals("2023-01-01T00:00+02:00", getTimestamp("2023-01-01T00:00:00+02:00"));
    }

    private String getTimestamp(String timestamp) throws IOException {
        String SYSLOG_MESSAGE = "<14>1 " + timestamp + " hostname appname - - - msg\n";
        InputStream inputStream = new ByteArrayInputStream(SYSLOG_MESSAGE.getBytes());
        RFC5424Frame rfc5424Frame = new RFC5424Frame(true);
        rfc5424Frame.load(inputStream);
        assertTrue(rfc5424Frame.next());
        return rfc5424Frame.timestamp.toZonedDateTime().toString();
    }
}
