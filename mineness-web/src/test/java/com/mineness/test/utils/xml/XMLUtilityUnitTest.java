package com.mineness.test.utils.xml;

import com.mineness.utils.xml.impl.XMLUtilityImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;

import java.io.InputStreamReader;
import java.io.Reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Bjorn Harvold
 * Date: 5/31/13
 * Time: 6:00 PM
 * Responsibility:
 */
@RunWith(MockitoJUnitRunner.class)
public class XMLUtilityUnitTest {
    private final static Logger log = LoggerFactory.getLogger(XMLUtilityUnitTest.class);


    @InjectMocks
    private XMLUtilityImpl impl;

    private final ClassPathResource shipmentResource = new ClassPathResource("amazon1_shipment_confirmation.html");
    private final ClassPathResource orderResource = new ClassPathResource("amazon1_order_confirmation.html");

    private Document doc = null;

    @Test
    public void testAmazonShipmentConfirmation() throws Exception {
        final String asinxPath = "//a[starts-with(@href, 'http://www.amazon.com/dp/') and not(child::img)]";
        final String identifierxPath = "//a[starts-with(@href, 'https://www.amazon.com/gp/css/summary/edit.html/')]";
        final String asinNavigation = "@href|nodevalue|substring('http://www.amazon.com/dp/', '/')";
        final String purchasePriceNavigation = "parent|parent|first_sibling|first_child|first_child|nodevalue";
        final String identifierNavigation = "first_child|nodevalue";

        Reader reader = new InputStreamReader(shipmentResource.getInputStream());
        doc = impl.normalizeHtmlContent(reader);
        assertNotNull("Document is null", doc);

        String value = impl.findValue(doc, asinxPath, asinNavigation);

        assertNotNull("ASIN is null", value);
        assertEquals("ASIN is incorrect", "B000U69B0Y", value);

        value = impl.findValue(doc, asinxPath, purchasePriceNavigation);

        assertNotNull("Purchase Price is null", value);
        assertEquals("Purchase price is incorrect", "$9.00", value);

        value = impl.findValue(doc, identifierxPath, identifierNavigation);
        assertNotNull("Identifier is null", value);
        assertEquals("Identifier is incorrect", "002-8092475-6889803", value);
    }

    @Test
    public void testAmazonOrderConfirmation() throws Exception {
        final String billingAddressxPath = "//b[(text() = 'Billing Address:')]";
        final String billingAddressNavigation = "parent|all_siblings";
        final String expectedBillingAddress = "Fanny Davidson\n247 W 87TH ST APT 17F\nNEW YORK, NY 10024-2850\nUnited States\n";

        Reader reader = new InputStreamReader(orderResource.getInputStream());
        doc = impl.normalizeHtmlContent(reader);
        assertNotNull("Document is null", doc);

        String value = impl.findValue(doc, billingAddressxPath, billingAddressNavigation);

        assertNotNull("Billing address is null", value);


        log.info("Comparing: \n" + expectedBillingAddress + "\nwith\n\n" + value);

        assertEquals("Billing address is incorrect", expectedBillingAddress, value);

    }
}
