package com.mineness.utils.email.impl;

import com.mineness.domain.enums.EmailTemplateContentType;
import com.mineness.utils.email.MimeMessageUtility;
import com.mineness.utils.xml.impl.XMLUtilityImpl;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Bjorn Harvold
 * Date: 5/17/13
 * Time: 12:54 PM
 * Responsibility:
 */
@Component("mimeMessageUtility")
public class MimeMessageUtilityImpl implements MimeMessageUtility {

    public static void main(String[] args) {
        try {
            FileSystemResource resource = new FileSystemResource("sample_emails/" + args[0]);

            MimeMessageUtilityImpl impl = new MimeMessageUtilityImpl();

            MimeMessage message = impl.makeMimeMessage(resource.getInputStream());

            Part part = impl.retrieveMimeMessagePartByContentType(message, EmailTemplateContentType.valueOf(args[1]));

            if (part.getContentType().contains("text/plain")) {
                System.out.println(part.getContent());
            } else if (part.getContentType().contains("text/html")) {
                XMLUtilityImpl xmlUtility = new XMLUtilityImpl();
                Document doc = xmlUtility.normalizeHtmlContent((String) part.getContent());
                xmlUtility.printXHTMLDocument(doc, System.out);
            }
        } catch (MessagingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Takes the InputStream of the incoming email and makes a MimeMessage object out of it
     *
     * @param is email
     * @return
     * @throws MessagingException
     */
    @Override
    public MimeMessage makeMimeMessage(InputStream is) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props);
        return new MimeMessage(session, is);
    }


    @Override
    public Part retrieveMimeMessagePartByContentType(MimeMessage message, EmailTemplateContentType type) {
        Part result = null;

        Map<String, Part> types = findMimeTypes(message, "text/html", "text/plain");

        switch (type) {
            case HTML:
                result = types.get("text/html");
                break;
            case TEXT:
                result = types.get("text/plain");
        }

        return result;
    }

    @Override
    public Map<String, Part> findMimeTypes(Part p, String... mimeTypes) {
        Map<String, Part> parts = new HashMap<String, Part>();
        findMimeTypesHelper(p, parts, mimeTypes);
        return parts;
    }

    // a little recursive helper function that actually does all the work.
    private void findMimeTypesHelper(Part p, Map parts, String... mimeTypes) {
        try {
            if (p.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) p.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    findContentTypesHelper(mp.getBodyPart(i), parts, mimeTypes);
                }
            } else {
                for (String mimeType : mimeTypes) {
                    if (p.isMimeType(mimeType) && !parts.containsKey(mimeType)) {
                        parts.put(mimeType, p);
                    }
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void findContentTypesHelper(Part p, Map<String, Part> contentTypes, String... mimeTypes) throws MessagingException, IOException {
        try {
            if (p.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) p.getContent();
                for (int i = 0; mp != null && i < mp.getCount(); i++) {
                    findContentTypesHelper(mp.getBodyPart(i), contentTypes, mimeTypes);
                }
            } else {
                for (String mimeType : mimeTypes) {
                    if (p.isMimeType(mimeType)) {
                        contentTypes.put(mimeType, p);
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
