package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class EmptyParserInputTest {
    @Test
    void testEmptyParserInput() throws IOException {
        // Should not break but also should not return any data
        RFC5424Parser parser = new RFC5424Parser();
        Assertions.assertFalse(parser.next());
        ResultSetAsString resultsetAsString = new ResultSetAsString(parser.get());
    }

    @Test
    void testDefaultSubscriptions() throws IOException {
        String input = "<134>1 2018-01-01T10:12:00+01:00 hostname appname ProcessID MsgID - message";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        RFC5424Parser parser = new RFC5424Parser(inputStream);
        Assertions.assertTrue(parser.next());
        ResultSetAsString resultsetAsString = new ResultSetAsString(parser.get());
        Assertions.assertEquals("134", resultsetAsString.getPriority(), "Priority");
        Assertions.assertEquals("1", resultsetAsString.getVersion(), "Version");
        Assertions.assertEquals("2018-01-01T10:12:00+01:00", resultsetAsString.getTimestamp(), "Timestamp");
        Assertions.assertEquals("hostname", resultsetAsString.getHostname(), "Hostname");
        Assertions.assertEquals("appname", resultsetAsString.getAppname(), "Appname");
        Assertions.assertEquals("ProcessID", resultsetAsString.getProcid(), "ProcessID");
        Assertions.assertEquals("MsgID", resultsetAsString.getMsgid(), "MsgID");
        Assertions.assertEquals("message", resultsetAsString.getMsg(), "MSG");
    }
}
