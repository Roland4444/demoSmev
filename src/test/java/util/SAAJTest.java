package util;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;

public class SAAJTest {

    @Test
    public void send() throws Exception {
        SAAJ saa= new SAAJ("http://smev3-n0.test.gosuslugi.ru:7500/ws?wsdl");
        assertNotEquals(null, saa.send("12.xml", "results.xml"));
    }
}