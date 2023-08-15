package com.teragrep.rlo_06.tests;

import com.teragrep.rlo_06.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AllSubscriptionTest {

    @Test
    public void testAllSubscription() throws IOException {
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"][event_id@48577 hostname=\"sc-99-99-14-247\" uuid=\"0FD92E51B37748EB90CD894CCEE63907\" unixtime=\"1612047600.0\" id_source=\"source\"][event_node_source@48577 hostname=\"sc-99-99-14-247\" source=\"f17_ssmis_20210131v7.nc\" source_module=\"imfile\"][event_node_relay@48577 hostname=\"localhost\" source=\"sc-99-99-14-247\" source_module=\"imrelp\"][event_version@48577 major=\"2\" minor=\"2\" hostname=\"localhost\" version_source=\"relay\"][event_node_router@48577 source=\"logrouter.example.com\" source_module=\"imrelp\" hostname=\"localhost\"][teragrep@48577 streamname=\"log:f17:0\" directory=\"com_teragrep_audit\" unixtime=\"1612047600.0\"] msg\n";

        InputStream inputStream = new ByteArrayInputStream( SYSLOG_MESSAGE.getBytes());
        RFC5424Frame rfc5424Frame = new RFC5424Frame(true);
        rfc5424Frame.load(inputStream);
        assertTrue(rfc5424Frame.next());


        SDVector sdVector = new SDVector("event_id@48577", "uuid");
        Assertions.assertEquals("0FD92E51B37748EB90CD894CCEE63907", rfc5424Frame.structuredData.getValue(sdVector).toString());
    }


    @Test
    public void testAllGet() throws IOException {
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"][event_id@48577 hostname=\"sc-99-99-14-247\" uuid=\"0FD92E51B37748EB90CD894CCEE63907\" unixtime=\"1612047600.0\" id_source=\"source\"][event_node_source@48577 hostname=\"sc-99-99-14-247\" source=\"f17_ssmis_20210131v7.nc\" source_module=\"imfile\"][event_node_relay@48577 hostname=\"localhost\" source=\"sc-99-99-14-247\" source_module=\"imrelp\"][event_version@48577 major=\"2\" minor=\"2\" hostname=\"localhost\" version_source=\"relay\"][event_node_router@48577 source=\"logrouter.example.com\" source_module=\"imrelp\" hostname=\"localhost\"][teragrep@48577 streamname=\"log:f17:0\" directory=\"com_teragrep_audit\" unixtime=\"1612047600.0\"] msg\n";

        InputStream inputStream = new ByteArrayInputStream( SYSLOG_MESSAGE.getBytes());
        RFC5424Frame rfc5424Frame = new RFC5424Frame(true);
        rfc5424Frame.load(inputStream);
        assertTrue(rfc5424Frame.next());

        Assertions.assertEquals("\\\"3", rfc5424Frame.structuredData.getValue(new SDVector("ID_A@1", "u")).toString());
        Assertions.assertEquals("t", rfc5424Frame.structuredData.getValue(new SDVector("ID_A@1", "e")).toString());

        Assertions.assertEquals("9", rfc5424Frame.structuredData.getValue(new SDVector("ID_B@2", "n")).toString());

        Assertions.assertEquals("sc-99-99-14-247", rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "hostname")).toString());
        Assertions.assertEquals("0FD92E51B37748EB90CD894CCEE63907", rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "uuid")).toString());
        Assertions.assertEquals("sc-99-99-14-247", rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "hostname")).toString());
        Assertions.assertEquals("1612047600.0", rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "unixtime")).toString());
        Assertions.assertEquals("source", rfc5424Frame.structuredData.getValue(new SDVector("event_id@48577", "id_source")).toString());

        Assertions.assertEquals("sc-99-99-14-247", rfc5424Frame.structuredData.getValue(new SDVector("event_node_source@48577", "hostname")).toString());
        Assertions.assertEquals("f17_ssmis_20210131v7.nc", rfc5424Frame.structuredData.getValue(new SDVector("event_node_source@48577", "source")).toString());
        Assertions.assertEquals("imfile", rfc5424Frame.structuredData.getValue(new SDVector("event_node_source@48577", "source_module")).toString());

        Assertions.assertEquals("localhost", rfc5424Frame.structuredData.getValue(new SDVector("event_node_relay@48577", "hostname")).toString());
        Assertions.assertEquals("sc-99-99-14-247", rfc5424Frame.structuredData.getValue(new SDVector("event_node_relay@48577", "source")).toString());
        Assertions.assertEquals("imrelp", rfc5424Frame.structuredData.getValue(new SDVector("event_node_relay@48577", "source_module")).toString());

        Assertions.assertEquals("2", rfc5424Frame.structuredData.getValue(new SDVector("event_version@48577", "major")).toString());
        Assertions.assertEquals("2", rfc5424Frame.structuredData.getValue(new SDVector("event_version@48577", "minor")).toString());
        Assertions.assertEquals("localhost", rfc5424Frame.structuredData.getValue(new SDVector("event_version@48577", "hostname")).toString());
        Assertions.assertEquals("relay", rfc5424Frame.structuredData.getValue(new SDVector("event_version@48577", "version_source")).toString());

        Assertions.assertEquals("logrouter.example.com", rfc5424Frame.structuredData.getValue(new SDVector("event_node_router@48577", "source")).toString());
        Assertions.assertEquals("imrelp", rfc5424Frame.structuredData.getValue(new SDVector("event_node_router@48577", "source_module")).toString());
        Assertions.assertEquals("localhost", rfc5424Frame.structuredData.getValue(new SDVector("event_node_router@48577", "hostname")).toString());

        Assertions.assertEquals("log:f17:0", rfc5424Frame.structuredData.getValue(new SDVector("teragrep@48577", "streamname")).toString());
        Assertions.assertEquals("com_teragrep_audit", rfc5424Frame.structuredData.getValue(new SDVector("teragrep@48577", "directory")).toString());
        Assertions.assertEquals("1612047600.0", rfc5424Frame.structuredData.getValue(new SDVector("teragrep@48577", "unixtime")).toString());
    }


}
