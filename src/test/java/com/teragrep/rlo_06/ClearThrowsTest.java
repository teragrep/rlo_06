package com.teragrep.rlo_06;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ClearThrowsTest {
    @Test
    public void allBytesToStringEqualsTest() throws IOException {
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [sd_one@48577 id_one=\"eno\" id_two=\"owt\"][sd_two@48577 id_three=\"eerht\" id_four=\"ruof\"] msg\n";
        InputStream inputStream = new ByteArrayInputStream(SYSLOG_MESSAGE.getBytes());
        RFC5424Frame rfc5424Frame = new RFC5424Frame(true);
        rfc5424Frame.load(inputStream);
        Assertions.assertTrue(rfc5424Frame.next());

        rfc5424Frame.priority.clear();
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.priority::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.priority::toBytes);

        rfc5424Frame.version.clear();
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.version::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.version::toBytes);

        rfc5424Frame.timestamp.clear();
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.timestamp::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.timestamp::toBytes);

        rfc5424Frame.hostname.clear();
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.hostname::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.hostname::toBytes);

        rfc5424Frame.appName.clear();
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.appName::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.appName::toBytes);

        rfc5424Frame.procId.clear();
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.procId::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.procId::toBytes);

        rfc5424Frame.msgId.clear();
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.msgId::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.msgId::toBytes);

        SDParamValue sdParamValue = rfc5424Frame.structuredData.getValue(new SDVector("sd_one@48577", "id_one"));
        sdParamValue.clear();
        Assertions.assertThrows(IllegalStateException.class, sdParamValue::toString);
        Assertions.assertThrows(IllegalStateException.class, sdParamValue::toString);

        rfc5424Frame.structuredData.clear();
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.structuredData::toString);

        rfc5424Frame.msg.clear();
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.msg::toString);
        Assertions.assertThrows(IllegalStateException.class, rfc5424Frame.msg::toBytes);
    }
}
