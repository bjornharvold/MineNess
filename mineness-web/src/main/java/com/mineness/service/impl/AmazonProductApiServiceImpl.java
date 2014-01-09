package com.mineness.service.impl;

import com.mineness.service.AmazonProductApiService;
import com.mineness.utils.amazon.SignedRequestsHelper;
import com.mineness.utils.xml.XMLUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bjorn Harvold
 * Date: 4/15/13
 * Time: 5:46 PM
 * Responsibility:
 */
@Service("amazonProductApiService")
public class AmazonProductApiServiceImpl implements AmazonProductApiService {
    private final static Logger log = LoggerFactory.getLogger(AmazonProductApiServiceImpl.class);

    @Value("${amazon.associate.id}")
    private String awsAssociateId;

    private final XMLUtility xmlUtility;

    private final SignedRequestsHelper helper;

    @Autowired
    public AmazonProductApiServiceImpl(XMLUtility xmlUtility,
                                       SignedRequestsHelper helper) {
        this.xmlUtility = xmlUtility;
        this.helper = helper;
    }

    @Override
    public Node findProductById(String itemId) throws AmazonProductApiException {
        Node result = null;

        try {
            Assert.hasText(itemId, "itemId is null");

            Map<String, String> params = new HashMap<String, String>();
            params.put("Service", "AWSECommerceService");
            params.put("Operation", "ItemLookup");
            params.put("ItemId", itemId);
            params.put("ResponseGroup", "ItemAttributes,Images");
            params.put("AssociateTag", awsAssociateId);
//            params.put("SearchIndex", "All");

            String requestUrl = helper.sign(params);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);

            if (doc != null) {

                if (log.isDebugEnabled()) {
                    try {
                        xmlUtility.printXMLDocument(doc, System.out);
                    } catch (TransformerException e) {
                        log.error(e.getMessage(), e);
                    }
                }

                // this will return all items from amazon's search result
                NodeList list = doc.getElementsByTagName("Item");

                // we only want there to be one result - we only know how to handle one anyway
                // the itemId should be unique
                result = list.item(0);
            }
        } catch (ParserConfigurationException e) {
            throw new AmazonProductApiException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new AmazonProductApiException(e.getMessage(), e);
        } catch (IOException e) {
            throw new AmazonProductApiException(e.getMessage(), e);
        }

        return result;
    }

    private String fetchElementValue(Document doc, String element) {
        return doc.getElementsByTagName(element).item(0).getTextContent();
    }


}
