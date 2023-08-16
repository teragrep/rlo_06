package com.teragrep.rlo_06;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AllToBytesTest {
    @Test
    public void allBytesEquals() throws IOException {
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"][event_id@48577 hostname=\"sc-99-99-14-247\" uuid=\"0FD92E51B37748EB90CD894CCEE63907\" unixtime=\"1612047600.0\" id_source=\"source\"][event_node_source@48577 hostname=\"sc-99-99-14-247\" source=\"f17_ssmis_20210131v7.nc\" source_module=\"imfile\"][event_node_relay@48577 hostname=\"localhost\" source=\"sc-99-99-14-247\" source_module=\"imrelp\"][event_version@48577 major=\"2\" minor=\"2\" hostname=\"localhost\" version_source=\"relay\"][event_node_router@48577 source=\"logrouter.example.com\" source_module=\"imrelp\" hostname=\"localhost\"][teragrep@48577 streamname=\"log:f17:0\" directory=\"com_teragrep_audit\" unixtime=\"1612047600.0\"] msg\n";

        InputStream inputStream = new ByteArrayInputStream( SYSLOG_MESSAGE.getBytes());
        RFC5424Frame rfc5424Frame = new RFC5424Frame(true);
        rfc5424Frame.load(inputStream);
        assertTrue(rfc5424Frame.next());

        assertArrayNotEquals("\\\"3".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("ID_A@1", "u")).toBytes());
        assertArrayNotEquals("t".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("ID_A@1", "e")).toBytes());

        assertArrayNotEquals("9".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("ID_B@2", "n")).toBytes());

        assertArrayNotEquals("sc-99-99-14-247".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "hostname")).toBytes());
        assertArrayNotEquals("0FD92E51B37748EB90CD894CCEE63907".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "uuid")).toBytes());
        assertArrayNotEquals("sc-99-99-14-247".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "hostname")).toBytes());
        assertArrayNotEquals("1612047600.0".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "unixtime")).toBytes());
        assertArrayNotEquals("source".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "id_source")).toBytes());

        assertArrayNotEquals("sc-99-99-14-247".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_source@48577", "hostname")).toBytes());
        assertArrayNotEquals("f17_ssmis_20210131v7.nc".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_source@48577", "source")).toBytes());
        assertArrayNotEquals("imfile".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_source@48577", "source_module")).toBytes());

        assertArrayNotEquals("localhost".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_relay@48577", "hostname")).toBytes());
        assertArrayNotEquals("sc-99-99-14-247".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_relay@48577", "source")).toBytes());
        assertArrayNotEquals("imrelp".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_relay@48577", "source_module")).toBytes());

        assertArrayNotEquals("2".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_version@48577", "major")).toBytes());
        assertArrayNotEquals("2".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_version@48577", "minor")).toBytes());
        assertArrayNotEquals("localhost".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_version@48577", "hostname")).toBytes());
        assertArrayNotEquals("relay".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_version@48577", "version_source")).toBytes());

        assertArrayNotEquals("logrouter.example.com".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_router@48577", "source")).toBytes());
        assertArrayNotEquals("imrelp".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_router@48577", "source_module")).toBytes());
        assertArrayNotEquals("localhost".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_router@48577", "hostname")).toBytes());

        assertArrayNotEquals("log:f17:0".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("teragrep@48577", "streamname")).toBytes());
        assertArrayNotEquals("com_teragrep_audit".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("teragrep@48577", "directory")).toBytes());
        assertArrayNotEquals("1612047600.0".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("teragrep@48577", "unixtime")).toBytes());

    }

    @Test
    public void allBytesNotEqual() throws IOException {
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"][event_id@48577 hostname=\"sc-99-99-14-247\" uuid=\"0FD92E51B37748EB90CD894CCEE63907\" unixtime=\"1612047600.0\" id_source=\"source\"][event_node_source@48577 hostname=\"sc-99-99-14-247\" source=\"f17_ssmis_20210131v7.nc\" source_module=\"imfile\"][event_node_relay@48577 hostname=\"localhost\" source=\"sc-99-99-14-247\" source_module=\"imrelp\"][event_version@48577 major=\"2\" minor=\"2\" hostname=\"localhost\" version_source=\"relay\"][event_node_router@48577 source=\"logrouter.example.com\" source_module=\"imrelp\" hostname=\"localhost\"][teragrep@48577 streamname=\"log:f17:0\" directory=\"com_teragrep_audit\" unixtime=\"1612047600.0\"] msg\n";

        InputStream inputStream = new ByteArrayInputStream( SYSLOG_MESSAGE.getBytes());
        RFC5424Frame rfc5424Frame = new RFC5424Frame(true);
        rfc5424Frame.load(inputStream);
        assertTrue(rfc5424Frame.next());

        assertArrayNotEquals("x".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("ID_A@1", "u")).toBytes());
        assertArrayNotEquals("y".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("ID_A@1", "e")).toBytes());

        assertArrayNotEquals("z".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("ID_B@2", "n")).toBytes());

        assertArrayNotEquals("hostname".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "hostname")).toBytes());
        assertArrayNotEquals("0XC0FF33".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "uuid")).toBytes());
        assertArrayNotEquals("another-hostname".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "hostname")).toBytes());
        assertArrayNotEquals("86400".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "unixtime")).toBytes());
        assertArrayNotEquals("destination".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "id_source")).toBytes());

        assertArrayNotEquals("what-is-this".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_source@48577", "hostname")).toBytes());
        assertArrayNotEquals("/etc/passwd".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_source@48577", "source")).toBytes());
        assertArrayNotEquals("imrelp".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_source@48577", "source_module")).toBytes());

        assertArrayNotEquals("localhost.localdomain".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_relay@48577", "hostname")).toBytes());
        assertArrayNotEquals("another-host".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_relay@48577", "source")).toBytes());
        assertArrayNotEquals("imtcp".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_relay@48577", "source_module")).toBytes());

        assertArrayNotEquals("3".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_version@48577", "major")).toBytes());
        assertArrayNotEquals("3".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_version@48577", "minor")).toBytes());
        assertArrayNotEquals("not-localhost".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_version@48577", "hostname")).toBytes());
        assertArrayNotEquals("not-relay".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_version@48577", "version_source")).toBytes());

        assertArrayNotEquals("logdestroyer.example.com".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_router@48577", "source")).toBytes());
        assertArrayNotEquals("imudp".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_router@48577", "source_module")).toBytes());
        assertArrayNotEquals("another-localhost".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("event_node_router@48577", "hostname")).toBytes());

        assertArrayNotEquals("log:lost:0".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("teragrep@48577", "streamname")).toBytes());
        assertArrayNotEquals("com_teragrep_test".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("teragrep@48577", "directory")).toBytes());
        assertArrayNotEquals("3600.123".getBytes(), rfc5424Frame.structuredData.getValue(new SDVector("teragrep@48577", "unixtime")).toBytes());

    }

    private void assertArrayNotEquals(byte[] expected, byte[] actual) {
        try {
            Assertions.assertArrayEquals(expected, actual);
        } catch (AssertionError e) {
            return;
        }
        Assertions.fail("Arrays are equal");
    }
}
