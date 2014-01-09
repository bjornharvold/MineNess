package com.mineness.service.impl;

import com.mineness.domain.document.Attribute;
import com.mineness.domain.document.EmailTemplate;
import com.mineness.domain.document.Order;
import com.mineness.domain.document.PurchasedItem;
import com.mineness.domain.document.User;
import com.mineness.repository.OrderRepository;
import com.mineness.repository.PurchasedItemRepository;
import com.mineness.service.AmazonProductApiService;
import com.mineness.service.EmailParserService;
import com.mineness.utils.xml.XMLUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;

/**
 * Created by Bjorn Harvold
 * Date: 3/29/13
 * Time: 3:02 PM
 * Responsibility: Responsible for handling amazon generated emails
 */
@Service("amazonHTMLEmailOrderConfirmationParserService")
public class AmazonHTMLOrderConfirmationEmailServiceImpl extends AbstractEmailService implements EmailParserService {
    private final static Logger log = LoggerFactory.getLogger(AmazonHTMLOrderConfirmationEmailServiceImpl.class);

    private final OrderRepository orderRepository;

    @Autowired
    public AmazonHTMLOrderConfirmationEmailServiceImpl(XMLUtility xmlUtility, OrderRepository orderRepository) {
        super(xmlUtility);
        this.orderRepository = orderRepository;
    }

    @Override
    public void processIncomingEmail(User user, Part htmlEmail, EmailTemplate template) throws EmailParserException {

        // process incoming email to retrieve interesting information
        Order order = createOrderFromEmail(user, htmlEmail, template);

        // see if there is an existing order with that identifier
        Order existingOrder = orderRepository.findByOrderId(order.getRdrd());

        // todo we might want to do additional checking of the existing order
        if (existingOrder == null) {
            // save the order
            orderRepository.save(order);
        } else {
            if (log.isErrorEnabled()) {
                log.error("Order with identifier already exists in db: " + order.getRdrd());
                log.error("Confirmation email should have been removed");
            }
        }

    }

    /**
     * The end result of this parsing will be a new product with the user
     *
     * @param user  Username is the user's email with us e.g. bjorn@mineness.com
     * @param htmlEmail This is the forwarded email
     */
    private Order createOrderFromEmail(User user, Part htmlEmail, EmailTemplate template) throws EmailParserException {
        Order result;

        try {

            // retrieve the email body of this content type
            String mailBody = (String) htmlEmail.getContent();

            // this body is, for now, only supported if it's html
            Document doc = xmlUtility.normalizeHtmlContent(mailBody);

            // grab all the data from the email as outlined in the EmailTemplate
            result = parsePropertyValuesFromEmailAndCreateOrder(user, doc, template);

        } catch (MessagingException e) {
            throw new EmailParserException(e.getMessage(), e);
        } catch (IOException e) {
            throw new EmailParserException(e.getMessage(), e);
        }

        return result;
    }

}
