package com.mineness.task;

import com.mineness.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Bjorn Harvold
 * Date: 4/2/13
 * Time: 7:43 PM
 * Responsibility: Handles retrieving email from mail server at scheduled intervals
 */
@Component("emailRetrievalTask")
public class EmailRetrievalTask {
    private final static Logger log = LoggerFactory.getLogger(EmailRetrievalTask.class);

    private final EmailService emailService;

    @Autowired
    public EmailRetrievalTask(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Run every 1 minute after the preceding process finishes. Wait 10 seconds before starting the first process.
     */
    @Scheduled(initialDelay = 10000, fixedDelay = 60000)
    public void retrieveEmails() {
        if (log.isDebugEnabled()) {
            log.debug("Kicking off retrieve email task...");
        }

        emailService.processNewEmails();

        if (log.isDebugEnabled()) {
            log.debug("Finishing up retrieve email task...");
        }
    }
}
