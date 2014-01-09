package com.mineness.service;

import com.mineness.domain.document.PurchasedItem;
import com.mineness.service.impl.AmazonProductApiException;
import org.w3c.dom.Node;

/**
 * Created by Bjorn Harvold
 * Date: 4/27/13
 * Time: 2:36 PM
 * Responsibility:
 */
public interface AmazonProductApiService {
    Node findProductById(String itemId) throws AmazonProductApiException;
}
