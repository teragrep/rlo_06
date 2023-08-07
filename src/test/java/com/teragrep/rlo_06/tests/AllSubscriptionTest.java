package com.teragrep.rlo_06.tests;

import com.teragrep.rlo_06.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class AllSubscriptionTest {

    @Test
    public void testAllSubscription() throws IOException {
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"][event_id@48577 hostname=\"sc-99-99-14-247\" uuid=\"0FD92E51B37748EB90CD894CCEE63907\" unixtime=\"1612047600.0\" id_source=\"source\"][event_node_source@48577 hostname=\"sc-99-99-14-247\" source=\"f17_ssmis_20210131v7.nc\" source_module=\"imfile\"][event_node_relay@48577 hostname=\"localhost\" source=\"sc-99-99-14-247\" source_module=\"imrelp\"][event_version@48577 major=\"2\" minor=\"2\" hostname=\"localhost\" version_source=\"relay\"][event_node_router@48577 source=\"logrouter.example.com\" source_module=\"imrelp\" hostname=\"localhost\"][teragrep@48577 streamname=\"log:f17:0\" directory=\"com_teragrep_audit\" unixtime=\"1612047600.0\"] msg\n";

        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();

        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription(true);

        InputStream inputStream = new ByteArrayInputStream( (SYSLOG_MESSAGE).getBytes());

        RFC5424Parser parser = new RFC5424Parser(subscription, sdSubscription, inputStream);

        Assertions.assertTrue(parser.next());

        ResultSetAsString resultsetAsString = new ResultSetAsString(parser.get());

        Assertions.assertEquals("0FD92E51B37748EB90CD894CCEE63907", resultsetAsString.getSdValue("event_id@48577", "uuid"));
    }


    @Test
    public void testAllSubscriptionAsMap() throws IOException {
        String SYSLOG_MESSAGE = "<14>1 2014-06-20T09:14:07.12345+00:00 host01 systemd DEA MSG-01 [ID_A@1 u=\"\\\"3\" e=\"t\"][ID_B@2 n=\"9\"][event_id@48577 hostname=\"sc-99-99-14-247\" uuid=\"0FD92E51B37748EB90CD894CCEE63907\" unixtime=\"1612047600.0\" id_source=\"source\"][event_node_source@48577 hostname=\"sc-99-99-14-247\" source=\"f17_ssmis_20210131v7.nc\" source_module=\"imfile\"][event_node_relay@48577 hostname=\"localhost\" source=\"sc-99-99-14-247\" source_module=\"imrelp\"][event_version@48577 major=\"2\" minor=\"2\" hostname=\"localhost\" version_source=\"relay\"][event_node_router@48577 source=\"logrouter.example.com\" source_module=\"imrelp\" hostname=\"localhost\"][teragrep@48577 streamname=\"log:f17:0\" directory=\"com_teragrep_audit\" unixtime=\"1612047600.0\"] msg\n";

        RFC5424ParserSubscription subscription = new RFC5424ParserSubscription();
        subscription.subscribeAll();

        RFC5424ParserSDSubscription sdSubscription = new RFC5424ParserSDSubscription(true);


        InputStream inputStream = new ByteArrayInputStream( (SYSLOG_MESSAGE).getBytes());

        RFC5424Parser parser = new RFC5424Parser(subscription, sdSubscription, inputStream);

        Assertions.assertTrue(parser.next());

        ResultSetAsString resultsetAsString = new ResultSetAsString(parser.get());

        HashMap<String, HashMap<String, String>> resultsetMap = resultsetAsString.getAsMap();

        Assertions.assertEquals("\\\"3", resultsetMap.get("ID_A@1").get("u"));
        Assertions.assertEquals("t", resultsetMap.get("ID_A@1").get("e"));

        Assertions.assertEquals("9", resultsetMap.get("ID_B@2").get("n"));

        Assertions.assertEquals("sc-99-99-14-247", resultsetMap.get("event_id@48577").get("hostname"));
        Assertions.assertEquals("0FD92E51B37748EB90CD894CCEE63907", resultsetMap.get("event_id@48577").get("uuid"));
        Assertions.assertEquals("sc-99-99-14-247", resultsetMap.get("event_id@48577").get("hostname"));
        Assertions.assertEquals("1612047600.0", resultsetMap.get("event_id@48577").get("unixtime"));
        Assertions.assertEquals("source", resultsetMap.get("event_id@48577").get("id_source"));

        Assertions.assertEquals("sc-99-99-14-247", resultsetMap.get("event_node_source@48577").get("hostname"));
        Assertions.assertEquals("f17_ssmis_20210131v7.nc", resultsetMap.get("event_node_source@48577").get("source"));
        Assertions.assertEquals("imfile", resultsetMap.get("event_node_source@48577").get("source_module"));

        Assertions.assertEquals("localhost", resultsetMap.get("event_node_relay@48577").get("hostname"));
        Assertions.assertEquals("sc-99-99-14-247", resultsetMap.get("event_node_relay@48577").get("source"));
        Assertions.assertEquals("imrelp", resultsetMap.get("event_node_relay@48577").get("source_module"));

        Assertions.assertEquals("2", resultsetMap.get("event_version@48577").get("major"));
        Assertions.assertEquals("2", resultsetMap.get("event_version@48577").get("minor"));
        Assertions.assertEquals("localhost", resultsetMap.get("event_version@48577").get("hostname"));
        Assertions.assertEquals("relay", resultsetMap.get("event_version@48577").get("version_source"));

        Assertions.assertEquals("logrouter.example.com", resultsetMap.get("event_node_router@48577").get("source"));
        Assertions.assertEquals("imrelp", resultsetMap.get("event_node_router@48577").get("source_module"));
        Assertions.assertEquals("localhost", resultsetMap.get("event_node_router@48577").get("hostname"));

        Assertions.assertEquals("log:f17:0", resultsetMap.get("teragrep@48577").get("streamname"));
        Assertions.assertEquals("com_teragrep_audit", resultsetMap.get("teragrep@48577").get("directory"));
        Assertions.assertEquals("1612047600.0", resultsetMap.get("teragrep@48577").get("unixtime"));
    }
}
