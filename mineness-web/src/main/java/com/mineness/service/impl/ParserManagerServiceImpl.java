package com.mineness.service.impl;

import com.mineness.domain.document.EmailTemplate;
import com.mineness.domain.document.User;
import com.mineness.repository.EmailTemplateRepository;
import com.mineness.service.EmailParserService;
import com.mineness.service.ParserManagerService;
import com.mineness.service.UserService;
import com.mineness.utils.email.MimeMessageUtility;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Bjorn Harvold
 * Date: 5/8/13
 * Time: 3:22 PM
 * Responsibility:
 */
@Service("parserManagerService")
public class ParserManagerServiceImpl implements ParserManagerService, ApplicationContextAware {
    private final static Logger log = LoggerFactory.getLogger(ParserManagerServiceImpl.class);

    private final UserService userService;
    private final EmailTemplateRepository emailTemplateRepository;
    private final MimeMessageUtility mimeMessageUtility;

    private ApplicationContext ctx;

    @Autowired
    public ParserManagerServiceImpl(UserService userService,
                                    EmailTemplateRepository emailTemplateRepository,
                                    Collection<EmailParserService> emailParserServices,
                                    MimeMessageUtility mimeMessageUtility) {
        this.userService = userService;
        this.emailTemplateRepository = emailTemplateRepository;
        this.mimeMessageUtility = mimeMessageUtility;
    }

    /**
     * Determines which parser should handle the specific email
     *
     * @param username username
     * @param email    email input stream
     * @throws EmailParserException
     */
    @Override
    public void processIncomingEmail(String username, InputStream email) throws EmailParserException {

        try {
            // validate that the user exists
            User user = userService.findUserByEmail(username);

            if (user != null) {
                MimeMessage message = mimeMessageUtility.makeMimeMessage(email);

                // determine the right email template to use for this email
                EmailTemplate template = determineEmailTemplate(message);

                // determine the right email parser based on the email template
                EmailParserService service = determineEmailParser(template);

                // determine which part of the email should be passed to the parser
                Part part = mimeMessageUtility.retrieveMimeMessagePartByContentType(message, template.getCntntTp());

                // let the parser handle the email
                service.processIncomingEmail(user, part, template);

            } else {
                throw new EmailParserException(String.format("User: %s doesn't exist. Cannot process email.", username));
            }

        } catch (MessagingException e) {
            throw new EmailParserException(e.getMessage(), e);
        }

    }

    /**
     * Retrieve the service from the email template
     */
    private EmailParserService determineEmailParser(EmailTemplate template) throws EmailParserException {
        EmailParserService result = null;

        try {
            result = ctx.getBean(template.getSrvc(), EmailParserService.class);
        } catch (BeansException ex) {
            throw new EmailParserException(String.format("Couldn't find bean with name: %s", template.getSrvc()));
        }

        return result;
    }

    /**
     * Will look at content of email to determine whether this parser understands how to handle it
     *
     * @param message message
     * @return Whether there was a match or not
     */
    private EmailTemplate determineEmailTemplate(MimeMessage message) throws EmailParserException {
        EmailTemplate result = null;

        try {
            if (log.isDebugEnabled()) {
                log.debug("Determining which mail template should handle incoming email...");
            }

            // find all email templates
            Iterable<EmailTemplate> templates = emailTemplateRepository.findAll();

            Map<String, Part> types = mimeMessageUtility.findMimeTypes(message, "text/html", "text/plain");

            Part plain = types.get("text/plain");
            Part html = types.get("text/html");
            String mailBodyPlain = "";
            String mailBodyHtml = "";

            if (plain != null && plain.getContent() != null) {
                mailBodyPlain = (String) plain.getContent();
            }

            if (html != null && html.getContent() != null) {
                mailBodyHtml = (String) html.getContent();
            }

            if (log.isTraceEnabled()) {
                log.trace("Matching strings against mail body plain: \n" + mailBodyPlain);
                log.trace("Matching strings against mail body html: \n" + mailBodyHtml);
            }

            Iterator<EmailTemplate> iter = templates.iterator();
            boolean found = false;

            while (iter.hasNext() && !found) {
                EmailTemplate template = iter.next();

                switch (template.getCntntTp()) {
                    case TEXT:
                        if (StringUtils.isNotBlank(mailBodyPlain)) {
                            found = matchOnEmailContent(template, mailBodyPlain);
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("This template needs the plain text version of the email but it's not available.");
                            }
                        }
                        break;
                    case HTML:
                        if (StringUtils.isNotBlank(mailBodyHtml)) {
                            found = matchOnEmailContent(template, mailBodyHtml);
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("This template needs the HTML version of the email but it's not available.");
                            }
                        }
                        break;
                    default:
                        if (log.isInfoEnabled()) {
                            log.info("Unsupported content type: " + template.getCntntTp());
                        }
                }

                if (found) {
                    result = template;

                    if (log.isDebugEnabled()) {
                        log.debug("Found template to use for email: " + template.getLbl());
                    }
                }
            }

        } catch (MessagingException e) {
            throw new EmailParserException(e.getMessage(), e);
        } catch (IOException e) {
            throw new EmailParserException(e.getMessage(), e);
        }

        if (result == null) {
            throw new EmailParserException("Couldn't find a valid mail template to handle message");
        }

        return result;
    }

    /**
     * Will loop through all match properties that are required for this template to match the email content
     * @param template template
     * @param emailContent emailContent
     * @return True if it is a match
     */
    private boolean matchOnEmailContent(EmailTemplate template, String emailContent) {
        boolean found = true;

        // loop through all required match properties for this template to determine if this is the one
        // if more than one template matches the message, the first match is selected
        if (template.getMtch() != null && !template.getMtch().isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("Checking if email content is a match for template: " + template.getLbl());
            }

            for (String requiredStringMatch : template.getMtch()) {
                if (log.isDebugEnabled()) {
                    log.debug("Matching mail body against: " + requiredStringMatch);
                }

                // check in both plain and html version of email
                if (!emailContent.contains(requiredStringMatch)) {
                    found = false;

                    if (log.isDebugEnabled()) {
                        log.debug("Could not find: " + requiredStringMatch);
                    }
                }
            }
        }

        return found;
    }



    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
