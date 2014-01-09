package com.mineness.test.service;

import com.mineness.domain.document.EmailTemplate;
import com.mineness.domain.document.Order;
import com.mineness.domain.document.User;
import com.mineness.domain.dto.EmailTemplates;
import com.mineness.domain.enums.EmailTemplateContentType;
import com.mineness.domain.enums.Vendor;
import com.mineness.repository.OrderRepository;
import com.mineness.repository.PurchasedItemRepository;
import com.mineness.service.AmazonProductApiService;
import com.mineness.service.impl.AmazonHTMLOrderConfirmationEmailServiceImpl;
import com.mineness.service.impl.AmazonHTMLShipmentConfirmationEmailServiceImpl;
import com.mineness.utils.email.impl.MimeMessageUtilityImpl;
import com.mineness.utils.jackson.CustomObjectMapper;
import com.mineness.utils.xml.XMLUtility;
import com.mineness.utils.xml.impl.XMLUtilityImpl;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Bjorn Harvold
 * Date: 4/14/13
 * Time: 8:26 PM
 * Responsibility: Tests our email parsing service
 */
@RunWith(MockitoJUnitRunner.class)
public class AmazonEmailOrderConfirmationParserServiceUnitTest {
    private final static Logger log = LoggerFactory.getLogger(AmazonEmailOrderConfirmationParserServiceUnitTest.class);

    @Spy
    private XMLUtility xmlUtility = new XMLUtilityImpl();

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private AmazonHTMLOrderConfirmationEmailServiceImpl service;

    private final ClassPathResource emailResource = new ClassPathResource("amazon1_order_confirmation.txt");

    private List<EmailTemplate> emailTemplates;

    private final Resource emailTemplatesResource = new ClassPathResource("bootstrap/emailTemplate.json");

    private CustomObjectMapper mapper = new CustomObjectMapper();

    private String username = "bjorn@mineness.com";

    private Part part;

    private User user;

    private Order existingOrder;

    private final String orderId = "002-8092475-6889803";

    @Before
    public void beforeEachTest() throws Exception {
        // create emailTemplates

        EmailTemplates ets = mapper.readValue(emailTemplatesResource.getInputStream(), EmailTemplates.class);

        if (ets != null && ets.getList() != null && !ets.getList().isEmpty()) {
            emailTemplates = ets.getList();
        }

        // create the email part
        MimeMessageUtilityImpl mmm = new MimeMessageUtilityImpl();
        MimeMessage message = mmm.makeMimeMessage(emailResource.getInputStream());
        part = mmm.retrieveMimeMessagePartByContentType(message, EmailTemplateContentType.HTML);

        // create user
        user = new User();
        user.setMl(username);

        // set up existing order object
        Date d = new Date();
        existingOrder = new Order(username, Vendor.AMAZON);
        existingOrder.setId(new ObjectId(d));
        existingOrder.setLdt(d);
        existingOrder.setCdt(d);
        existingOrder.setLbl("--LABEL--");
        existingOrder.setRdrd(orderId);
    }

    @Test
    public void testParserSuccess() throws Exception {
        log.info("Testing " + service.getClass().getSimpleName() + " SUCCESS ...");

        // execute service method
        service.processIncomingEmail(user, part, emailTemplates.get(1));

        // verify results
        verify(orderRepository, times(1)).save(any(Order.class));
        log.info("Testing " + service.getClass().getSimpleName() + " COMPLETE");
    }

    @Test
    public void testParserFailure() throws Exception {
        log.info("Testing " + service.getClass().getSimpleName() + " FAILURE ...");
        // configure test
        when(orderRepository.findByOrderId(orderId)).thenReturn(existingOrder);

        // execute service method
        service.processIncomingEmail(user, part, emailTemplates.get(1));

        // verify results
        verify(orderRepository, times(0)).save(any(Order.class));
        log.info("Testing " + service.getClass().getSimpleName() + " COMPLETE");
    }

}
