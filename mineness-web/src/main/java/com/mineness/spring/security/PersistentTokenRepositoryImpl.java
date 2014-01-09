/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.spring.security;

import com.mineness.domain.document.RememberMe;
import com.mineness.repository.RememberMeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import java.util.Date;

/**
 * Created by Bjorn Harvold
 * Date: 8/15/11
 * Time: 4:31 PM
 * Responsibility:
 */
public class PersistentTokenRepositoryImpl implements PersistentTokenRepository {
    private final static Logger log = LoggerFactory.getLogger(PersistentTokenRepositoryImpl.class);
    private final RememberMeRepository rememberMeRepository;

    public PersistentTokenRepositoryImpl(RememberMeRepository rememberMeRepository) {
        this.rememberMeRepository = rememberMeRepository;
    }

    @Override
    public synchronized void createNewToken(PersistentRememberMeToken token) {
        RememberMe rm = rememberMeRepository.findBySrs(token.getSeries());

        if (rm != null) {
            String error = "Series Id '" + token.getSeries() + "' already exists!";
            if (log.isErrorEnabled()) {
                log.error(error);
            }
            throw new DataIntegrityViolationException(error);
        }

        rm = new RememberMe(token);
        rememberMeRepository.save(rm);

        /*
        if (log.isErrorEnabled()) {
            log.error(String.format("RememberMe token created. Series: %s, Token: %s", rm.getSrs(), rm.getTkn()));
        }
        */
    }

    @Override
    public synchronized void updateToken(String series, String tokenValue, Date lastUsed) {
        RememberMe rm = rememberMeRepository.findBySrs(series);

        if (rm != null) {
            // update values
            rm.setTkn(tokenValue);
            rm.setDt(new Date());

            // Store it, overwriting the existing one.
            rememberMeRepository.save(rm);

            /*
            if (log.isErrorEnabled()) {
                log.error(String.format("RememberMe token updated. Series: %s, Token: %s", series, tokenValue));
            }
            */
        } else {
            String error = "Could not find RememberMe for series: " + series;
            if (log.isErrorEnabled()) {
                log.error(error);
            }
            throw new DataIntegrityViolationException(error);
        }
    }

    @Override
    public synchronized PersistentRememberMeToken getTokenForSeries(String seriesId) {
        PersistentRememberMeToken result = null;
        RememberMe rm = rememberMeRepository.findBySrs(seriesId);

        if (rm != null) {
            result = new PersistentRememberMeToken(rm.getMl(), rm.getSrs(), rm.getTkn(), rm.getDt());
        }

        return result;
    }

    @Override
    public synchronized void removeUserTokens(String username) {
        RememberMe rm = rememberMeRepository.findByMl(username);

        if (rm != null) {
            rememberMeRepository.delete(rm);
        }
    }

    /**
    private void trackLogin(String email) {
        analyticsService.trackLogin(email);
    }
     */
}
