package com.mineness.service;

import com.mineness.domain.document.EmailTemplate;
import com.mineness.domain.document.PurchasedItem;
import com.mineness.domain.document.User;
import com.mineness.service.impl.EmailParserException;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Bjorn Harvold
 * Date: 4/7/13
 * Time: 8:06 PM
 * Responsibility:
 */
public interface EmailParserService {
    void processIncomingEmail(User user, Part part, EmailTemplate template) throws EmailParserException;
}
