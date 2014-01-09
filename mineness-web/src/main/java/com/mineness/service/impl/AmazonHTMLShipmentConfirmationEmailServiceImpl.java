package com.mineness.service.impl;

import com.mineness.domain.document.Attribute;
import com.mineness.domain.document.EmailTemplate;
import com.mineness.domain.document.Order;
import com.mineness.domain.document.PurchasedItem;
import com.mineness.domain.document.User;
import com.mineness.domain.enums.PurchaseStatus;
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
@Service("amazonHTMLEmailShipmentConfirmationParserService")
public class AmazonHTMLShipmentConfirmationEmailServiceImpl extends AbstractEmailService implements EmailParserService {
    private final static Logger log = LoggerFactory.getLogger(AmazonHTMLShipmentConfirmationEmailServiceImpl.class);

    private final OrderRepository orderRepository;
    private final PurchasedItemRepository purchasedItemRepository;
    private final AmazonProductApiService amazonProductApiService;

    @Autowired
    public AmazonHTMLShipmentConfirmationEmailServiceImpl(PurchasedItemRepository purchasedItemRepository,
                                                          AmazonProductApiService amazonProductApiService,
                                                          XMLUtility xmlUtility, OrderRepository orderRepository) {
        super(xmlUtility);
        this.purchasedItemRepository = purchasedItemRepository;
        this.amazonProductApiService = amazonProductApiService;
        this.orderRepository = orderRepository;
    }

    @Override
    public void processIncomingEmail(User user, Part htmlEmail, EmailTemplate template) throws EmailParserException {

        // process incoming email to retrieve interesting information
        Order order = createOrderFromEmail(user, htmlEmail, template);

        // there should already been an order with this order id that came in on the order confirmation email
        // it's not required but it has added information on it
        Order existingOrder = orderRepository.findByOrderId(order.getRdrd());

        if (existingOrder != null) {
            // merge the data
            order.mergeExistingOrder(existingOrder);
        }

        // save the order (this is 99% certainty of an update)
        orderRepository.save(order);

        // save purchased items
        purchasedItemRepository.save(order.getItems());

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

            // fetch product info from amazon
            enrichWithProductInfoFromAmazonAPI(result);

        } catch (MessagingException e) {
            throw new EmailParserException(e.getMessage(), e);
        } catch (IOException e) {
            throw new EmailParserException(e.getMessage(), e);
        }

        return result;
    }

    /**
     * Grab amazon data based on the product ids we parsed from the email
     *
     * @param order order
     * @return
     * @throws EmailParserException
     */
    private void enrichWithProductInfoFromAmazonAPI(Order order) throws EmailParserException {

        try {

            for (PurchasedItem item : order.getItems()) {
                // first thing we want to do is set the status to shipped
                item.setSts(PurchaseStatus.SHIPPED);

                Attribute asin = item.getTtrbts().get("asin");

                if (log.isDebugEnabled()) {
                    log.debug("Retrieving information for ASIN: " + asin.getVl());
                }

                Node node = amazonProductApiService.findProductById(asin.getVl());

                extractProductFromNode(item, node);

            }
        } catch (AmazonProductApiException e) {
            throw new EmailParserException(e.getMessage(), e);
        }

    }

    /**
     * Traverses xml and records all product attributes on the Project object
     * TODO these strings can't be hard coded here
     * @param itemNode node
     * @return Product
     */
    private PurchasedItem extractProductFromNode(PurchasedItem item, Node itemNode) throws EmailParserException {

        try {
            XPath xPath = XPathFactory.newInstance().newXPath();

            // DetailPageURL
            Node n = (Node) xPath.evaluate("//DetailPageURL", itemNode, XPathConstants.NODE);
            item.addAttribute("DetailPageURL", "DetailPageURL", n.getFirstChild().getNodeValue());

            // Brand
            n = (Node) xPath.evaluate("//ItemAttributes/Brand", itemNode, XPathConstants.NODE);
            item.addAttribute("Brand", "Brand", n.getFirstChild().getNodeValue());

            // Title
            n = (Node) xPath.evaluate("//ItemAttributes/Title", itemNode, XPathConstants.NODE);
            item.addAttribute("Title", "Title", n.getFirstChild().getNodeValue());

            // Feature
            NodeList nodes = (NodeList) xPath.evaluate("//ItemAttributes/Feature", itemNode, XPathConstants.NODESET);
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < nodes.getLength(); i++) {
                Node fNode = nodes.item(i);
                sb.append(fNode.getFirstChild().getNodeValue()).append("\n");
            }
            item.addAttribute("Feature", "Feature", sb.toString());

            // Color
            n = (Node) xPath.evaluate("//ItemAttributes/Color", itemNode, XPathConstants.NODE);
            item.addAttribute("Color", "Color", n.getFirstChild().getNodeValue());

            // Binding
            n = (Node) xPath.evaluate("//ItemAttributes/Binding", itemNode, XPathConstants.NODE);
            item.addAttribute("Binding", "Binding", n.getFirstChild().getNodeValue());

            // Manufacturer
            n = (Node) xPath.evaluate("//ItemAttributes/Manufacturer", itemNode, XPathConstants.NODE);
            item.addAttribute("Manufacturer", "Manufacturer", n.getFirstChild().getNodeValue());

            // Model
            n = (Node) xPath.evaluate("//ItemAttributes/Model", itemNode, XPathConstants.NODE);
            item.addAttribute("Model", "Model", n.getFirstChild().getNodeValue());

            // SKU
            n = (Node) xPath.evaluate("//ItemAttributes/SKU", itemNode, XPathConstants.NODE);
            item.addAttribute("SKU", "SKU", n.getFirstChild().getNodeValue());

            // UPC
            n = (Node) xPath.evaluate("//ItemAttributes/UPC", itemNode, XPathConstants.NODE);
            item.addAttribute("UPC", "UPC", n.getFirstChild().getNodeValue());

            // Warranty
            n = (Node) xPath.evaluate("//ItemAttributes/Warranty", itemNode, XPathConstants.NODE);
            item.addAttribute("Warranty", "Warranty", n.getFirstChild().getNodeValue());

            // Large image
            n = (Node) xPath.evaluate("//LargeImage/URL", itemNode, XPathConstants.NODE);
            item.addAttribute("LargeImage", "LargeImage", n.getFirstChild().getNodeValue());
        } catch (XPathExpressionException e) {
            throw new EmailParserException(e.getMessage(), e);
        }

        return item;
    }


}
