package com.mineness.utils.email;

import com.mineness.domain.enums.EmailTemplateContentType;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by Bjorn Harvold
 * Date: 5/18/13
 * Time: 7:36 PM
 * Responsibility:
 */
public interface MimeMessageUtility {
    MimeMessage makeMimeMessage(InputStream is) throws MessagingException;

    Part retrieveMimeMessagePartByContentType(MimeMessage message, EmailTemplateContentType type);

    Map<String, Part> findMimeTypes(Part p, String... mimeTypes);
}
