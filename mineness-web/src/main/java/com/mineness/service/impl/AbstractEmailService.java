package com.mineness.service.impl;

import com.mineness.domain.document.EmailProperty;
import com.mineness.domain.document.EmailPropertyProductGroup;
import com.mineness.domain.document.EmailTemplate;
import com.mineness.domain.document.Order;
import com.mineness.domain.document.PurchasedItem;
import com.mineness.domain.document.User;
import com.mineness.utils.xml.XMLUtility;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bjorn Harvold
 * Date: 5/27/13
 * Time: 8:48 PM
 * Responsibility:
 */
public abstract class AbstractEmailService {
    protected final XMLUtility xmlUtility;

    public AbstractEmailService(XMLUtility xmlUtility) {
        this.xmlUtility = xmlUtility;
    }

    /**
     * Generic method used for scraping retrieving value from xml DOM objects.
     *
     * @param doc      doc
     * @param template template
     * @return
     */
    protected Order parsePropertyValuesFromEmailAndCreateOrder(User user, Document doc, EmailTemplate template) throws EmailParserException {
        Order result = null;

        try {
            if (template != null) {
                // init order with user's email and template vendor
                result = new Order(user.getMl(), template.getVndr());

                // retrieve the identifier
                if (template.getRdrd() != null) {
                    EmailProperty property = template.getRdrd();
                    String identifier = xmlUtility.findValue(doc, property.getXpth(), property.getNvgtn());
                    result.setRdrd(identifier);
                }

                if (template.getGrps() != null && !template.getGrps().isEmpty()) {
                    List<PurchasedItem> items = new ArrayList<>();

                    // a property group signifies properties that might occur several times
                    // in a DOM because there are more than 1 purchased product listed
                    for (EmailPropertyProductGroup group : template.getGrps()) {
                        PurchasedItem item = new PurchasedItem(result);

                        if (group != null && group.getPrprts() != null && !group.getPrprts().isEmpty()) {

                            for (EmailProperty property : group.getPrprts()) {
                                String value = xmlUtility.findValue(doc, property.getXpth(), property.getNvgtn());

                                item.addAttribute(property.getKy(), property.getLbl(), value);
                            }
                        }

                        items.add(item);

                    }

                    // temporarily set the items on the order
                    result.setItems(items);
                }

                if (template.getPrps() != null && !template.getPrps().isEmpty()) {

                    // a property is a global property for the order where the products where purchased
                    for (EmailProperty property : template.getPrps()) {
                        String value = xmlUtility.findValue(doc, property.getXpth(), property.getNvgtn());

                        result.addAttribute(property.getKy(), property.getLbl(), value);
                    }
                }
            }
        } catch (XPathExpressionException e) {
            throw new EmailParserException(e.getMessage(), e);
        }

        return result;
    }
}
