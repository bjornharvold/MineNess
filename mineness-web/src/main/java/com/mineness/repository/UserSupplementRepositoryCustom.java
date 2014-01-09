/*
 * Copyright (c) 2012. Purple Door Systems, BV.
 */

package com.mineness.repository;


import com.mineness.domain.document.UserSupplement;
import com.mineness.domain.dto.UserSearchQuery;

import java.util.List;

/**
 * Created by Bjorn Harvold
 * Date: 8/8/12
 * Time: 2:39 PM
 * Responsibility:
 */
public interface UserSupplementRepositoryCustom {

    Long findFacebookUserCount(List<String> userCodes);

    UserSupplement findUserSupplement(String userCode, List<String> fields);

    List<UserSupplement> findByCodes(List<String> userCodes, List<String> fields);

    Long findUserSupplementCountByQuery(UserSearchQuery query);

    List<UserSupplement> findUserSupplementsByQuery(UserSearchQuery query);

}
