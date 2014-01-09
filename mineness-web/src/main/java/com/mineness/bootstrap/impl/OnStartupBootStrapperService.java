/*
 * Copyright (c) 2009. This beautifully written piece of code has been created by Bjorn Harvold. Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

package com.mineness.bootstrap.impl;

import com.mineness.bootstrap.BootStrapperService;
import com.mineness.bootstrap.Bootstrapper;
import com.mineness.bootstrap.BootstrapperException;
import com.mineness.spring.security.SpringSecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * User: bjorn
 * Date: Nov 4, 2007
 * Time: 11:17:07 AM
 */
public class OnStartupBootStrapperService implements BootStrapperService {
    private final static Logger log = LoggerFactory.getLogger(OnStartupBootStrapperService.class);

    @Value("${data.creation.enabled:true}")
    private Boolean enabled = null;

    private Boolean complete = false;
    private List<Bootstrapper> bootstrappers = null;

    @Override
    public void init() {

        if (enabled) {
            SpringSecurityHelper.secureChannel();

            // bind entityManager to current thread
            if (bootstrappers != null && bootstrappers.size() > 0) {

                try {

                    for (Bootstrapper dc : bootstrappers) {

                        log.info("Creating data with: " + dc.toString());
                        dc.create();
                        log.info("Success: " + dc.toString());

                    }

                    complete = true;

                } catch (BootstrapperException e) {
                    log.error("Error creating data! " + e.getMessage(), e);
                } finally {
                    SpringSecurityHelper.unsecureChannel();
                }

            }


        } else {
            log.info("OnStartupBootStrapperService is currently disabled. Check application.properties file for property: 'data.creation.enabled'.");
        }
    }

    @Override
    public Boolean isComplete() {
        return complete;
    }

    public void setBootstrappers(List<Bootstrapper> bootstrappers) {
        this.bootstrappers = bootstrappers;
    }
}
