/*
 * Copyright (c) 2012. Purple Door Systems, BV.
 */

package com.mineness.service.impl;

import com.mineness.ApplicationConstants;
import com.mineness.domain.document.Social;
import com.mineness.domain.document.UserSupplement;
import com.mineness.domain.dto.UserSearchQuery;
import com.mineness.repository.UserSupplementRepository;
import com.mineness.service.CacheService;
import com.mineness.service.UserSupplementService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by Bjorn Harvold
 * Date: 8/4/12
 * Time: 7:35 PM
 * Responsibility:
 */
@Service("userSupplementService")
public class UserSupplementServiceImpl extends AbstractCacheableService implements UserSupplementService {
    private final static Logger log = LoggerFactory.getLogger(UserSupplementServiceImpl.class);

    private final UserSupplementRepository userSupplementRepository;
    
    @Autowired
    public UserSupplementServiceImpl(CacheService cacheService,
                                     UserSupplementRepository userSupplementRepository) {
        super(cacheService);
        this.userSupplementRepository = userSupplementRepository;
    }

    @Override
    public UserSupplement saveUserSupplement(UserSupplement us) {
        us = userSupplementRepository.save(us);

        // delete old cache value
        removeFromCache(ApplicationConstants.USER_SUPPLEMENT_CACHE, us.getCd());

        return us;
    }

    @Override
    public List<Social> findSocials(String userCode) {
        UserSupplement us = findUserSupplement(userCode);

        return us.getScls();
    }

    @Override
    public List<Social> saveSocials(String userCode, List<Social> socials) {
        UserSupplement us = findUserSupplement(userCode);

        us.setScls(socials);

        saveUserSupplement(us);

        return socials;
    }

    @Override
    public UserSupplement findUserSupplement(String userCode) {
        UserSupplement result = null;

        if (StringUtils.isBlank(userCode)) {
            throw new IllegalArgumentException("UserCode cannot be null");
        }

        Cache.ValueWrapper wrapper = retrieveFromCache(ApplicationConstants.USER_SUPPLEMENT_CACHE, userCode);

        if (wrapper != null && wrapper.get() != null && wrapper.get() instanceof UserSupplement) {
            result = (UserSupplement) wrapper.get();
        } else {
            result = userSupplementRepository.findUserSupplementByCode(userCode);

            if (result == null) {
                // the user should always have a user supplement object
                // if it doesn't exist, create it
                result = new UserSupplement(userCode);
            }

            putInCache(ApplicationConstants.USER_SUPPLEMENT_CACHE, userCode, result);
        }


        return result;
    }

    @Override
    public UserSupplement findUserSupplement(String userCode, List<String> fields) {
        return userSupplementRepository.findUserSupplement(userCode, fields);
    }

    @Override
    public UserSupplement findUserSupplementByEmail(String email) {
        UserSupplement result = null;

        if (StringUtils.isNotBlank(email)) {
            result = userSupplementRepository.findUserSupplementByEmail(email.trim().toLowerCase());
        }

        return result;
    }

    @Override
    public List<UserSupplement> findUserSupplementsByCodes(List<String> userCodes, List<String> fields) {
        return userSupplementRepository.findByCodes(userCodes, fields);
    }

    @Override
    public List<UserSupplement> findUserSupplementsByQuery(UserSearchQuery query) {
        return userSupplementRepository.findUserSupplementsByQuery(query);
    }

    @Override
    public Long findUserSupplementCountByQuery(UserSearchQuery query) {
        return userSupplementRepository.findUserSupplementCountByQuery(query);
    }

    @Override
    public Long findFacebookUserCount(List<String> userCodes) {
        return userSupplementRepository.findFacebookUserCount(userCodes);
    }

    /**
     * Method description
     *
     * @param providerId     providerId
     * @param providerUserId providerUserId
     * @return Return value
     */
    @Override
    public List<UserSupplement> findUserIdsBySocialNetwork(String providerId, String providerUserId) {
        return userSupplementRepository.findUserIdsBySocialNetwork(providerId, providerUserId);
    }

    /**
     * Method description
     *
     * @param providerId      providerId
     * @param providerUserIds providerUserIds
     * @return Return value
     */
    @Override
    public List<UserSupplement> findUserIdsConnectedToSocialNetwork(String providerId, Set<String> providerUserIds) {
        return userSupplementRepository.findUserIdsConnectedToSocialNetwork(providerId, providerUserIds);
    }

    /**
     * Find ALL users for social network
     *
     * @param providerId Social network provider id
     * @return list of users
     */
    @Override
    public List<UserSupplement> findUsersBySocialNetwork(String providerId) {
        return userSupplementRepository.findUsersBySocialNetwork(providerId);
    }

    @Override
    public void removeUserSupplement(String userCode) {
        UserSupplement us = findUserSupplement(userCode);

        removeUserSupplement(us);
    }

    @Override
    public void removeUserSupplement(UserSupplement us) {
        if (us != null && us.getId() != null) {
            userSupplementRepository.delete(us);

            removeFromCache(ApplicationConstants.USER_SUPPLEMENT_CACHE, us.getCd());
        }
    }

}
