/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.mineness.bootstrap.impl;

import com.mineness.bootstrap.Bootstrapper;
import com.mineness.bootstrap.BootstrapperException;
import com.mineness.domain.document.EmailTemplate;
import com.mineness.domain.dto.EmailTemplates;
import com.mineness.repository.EmailTemplateRepository;
import com.mineness.utils.jackson.CustomObjectMapper;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: bjorn
 * Date: Nov 4, 2007
 * Time: 11:19:22 AM
 * Inserts required roles into the system
 */
@SuppressWarnings("unchecked")
public class EmailTemplateBootstrapper extends AbstractBootstrapper implements Bootstrapper {
    private final static Logger log = LoggerFactory.getLogger(EmailTemplateBootstrapper.class);
    private final Resource file = new ClassPathResource("bootstrap/emailTemplate.json");
    private CustomObjectMapper mapper = new CustomObjectMapper();

    @Value("${bootstrapper.email.template.enabled:true}")
    private Boolean enabled;

    private final EmailTemplateRepository emailTemplateRepository;

    public EmailTemplateBootstrapper(EmailTemplateRepository emailTemplateRepository) {
        this.emailTemplateRepository = emailTemplateRepository;
    }

    @Override
    public void create() throws BootstrapperException {

        try {
            if (file.exists()) {
                EmailTemplates ets = mapper.readValue(file.getInputStream(), EmailTemplates.class);

                emailTemplateRepository.save(ets.getList());

                log.info("Populated " + ets.getList().size() + " email templates in db");
            } else {
                throw new BootstrapperException("JSON file could not be found");
            }
        } catch (JsonMappingException e) {
            throw new BootstrapperException(e.getMessage(), e);
        } catch (JsonParseException e) {
            throw new BootstrapperException(e.getMessage(), e);
        } catch (IOException e) {
            throw new BootstrapperException(e.getMessage(), e);
        }

    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Boolean getEnabled() {
        return enabled;
    }
}
