/*
 * Copyright (c) 2012. Purple Door Systems, BV.
 */

package com.mineness.service;

import com.mineness.domain.document.Social;
import com.mineness.domain.document.UserSupplement;
import com.mineness.domain.dto.UserSearchQuery;

import java.util.List;
import java.util.Set;

/**
 * Created by Bjorn Harvold
 * Date: 8/4/12
 * Time: 10:19 PM
 * Responsibility:
 */
public interface UserSupplementService {

    Long findFacebookUserCount(List<String> userCodes);

    List<UserSupplement> findUserIdsBySocialNetwork(String providerId, String providerUserId);

    List<UserSupplement> findUserIdsConnectedToSocialNetwork(String providerId, Set<String> providerUserIds);

    List<UserSupplement> findUsersBySocialNetwork(String providerId);

    void removeUserSupplement(String userCode);

    void removeUserSupplement(UserSupplement us);

    UserSupplement findUserSupplement(String userCode);

    UserSupplement saveUserSupplement(UserSupplement us);

    List<Social> findSocials(String userCode);

    List<Social> saveSocials(String userCode, List<Social> socials);

    UserSupplement findUserSupplementByEmail(String email);

    UserSupplement findUserSupplement(String userCode, List<String> fields);

    List<UserSupplement> findUserSupplementsByCodes(List<String> userCodes, List<String> fields);

    List<UserSupplement> findUserSupplementsByQuery(UserSearchQuery query);

    Long findUserSupplementCountByQuery(UserSearchQuery query);
}
