package com.mineness.service.impl;

import com.mineness.domain.entity.JamesMail;
import com.mineness.domain.entity.JamesMailbox;
import com.mineness.service.EmailParserService;
import com.mineness.service.EmailService;
import com.mineness.service.ParserManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by Bjorn Harvold
 * Date: 4/2/13
 * Time: 11:01 PM
 * Responsibility: Interface to all things Email Server related
 */
@Service("scheduledEmailFetchService")
public class ScheduledEmailFetchServiceImpl implements EmailService {
    private final static Logger log = LoggerFactory.getLogger(ScheduledEmailFetchServiceImpl.class);

    /** Inject the service that controls all different email parsers */
    private final ParserManagerService parserManagerService;

    @Autowired
    public ScheduledEmailFetchServiceImpl(ParserManagerService parserManagerService) {
        this.parserManagerService = parserManagerService;
    }

    @Override
    public void processNewEmails() {
        // retrieve all mail
        List<JamesMail> list = JamesMail.findAllJamesMails();

        // process each email and save as user
        if (list != null && !list.isEmpty()) {
            if (log.isInfoEnabled()) {
                log.info("Found mail " + list.size() + " unprocessed mails with mail server");
            }
            for (JamesMail mail : list) {
                // process email
                processEmail(mail);
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info("No new mail found");
            }
        }
    }

    /**
     * Processing an email has several steps to it
     * 1. Determine whether we support this email / vendor
     * 2. Ensure user exists
     * 3. Determine type of email e.g. order confirmation / shipment confirmation
     * 4. Determine content type and that we have the template for that content type / vendor combination
     *
     * Note: This method is a good candidate for asynchronous processing
     * @param mail mail
     */
//    @Async
    private void processEmail(JamesMail mail) {
        byte[] headerBytes = mail.getHeaderBytes();
        byte[] mailBytes = mail.getMailBytes();

        try {
            if (log.isDebugEnabled()) {
                log.debug("Finding user mailbox: " + mail.getId().getMailboxId());
            }

            JamesMailbox mbox = JamesMailbox.findJamesMailbox(mail.getId().getMailboxId());

            if (log.isDebugEnabled()) {
                log.debug("Mailbox is for user: " + mbox.getUsername());
            }

            // merge the email header and content into one byte array
            byte[] combined = new byte[headerBytes.length + mailBytes.length];

            for (int i = 0; i < combined.length; ++i) {
                combined[i] = i < headerBytes.length ? headerBytes[i] : mailBytes[i - headerBytes.length];
            }

            // create a combined input stream of both the header and the body

            ByteArrayInputStream bis = new ByteArrayInputStream(combined);
            parserManagerService.processIncomingEmail(mbox.getUsername(), bis);

            // we can now safely remove the email from James
            if (log.isDebugEnabled()) {
                log.debug(String.format("Removing mail with %s from mail server", mail.getId()));
            }

//          remove mail from db
            // TODO uncomment when we are done with testing
//            mail.remove();

        } catch (EmailParserException ex) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Could not successfully process email with composite ID: %d / %d", mail.getId().getMailboxId(), mail.getId().getMailUid()));
                log.debug("Cause: " + ex.getMessage(), ex);
            }
        }
    }
}
