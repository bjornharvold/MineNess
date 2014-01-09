/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.service;

import com.mineness.domain.document.Social;
import com.mineness.domain.document.User;
import com.mineness.domain.document.UserSupplement;
import com.mineness.domain.dto.UserDto;
import com.mineness.domain.dto.UserSearchQuery;
import org.bson.types.ObjectId;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.social.connect.UserProfile;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Bjorn Harvold
 * Date: 6/19/11
 * Time: 1:14 PM
 * Responsibility:
 */
public interface UserService extends UserDetailsService {
    User findUser(ObjectId id);

    User saveUser(User user);

    User findUserByEmail(String email);

    Boolean isValid(String encryptedPassword, String rawPassword);

    String encryptPassword(String password);

    List<User> findUserIdsBySocialNetwork(String providerId, String providerUserId);

    List<User> findUserIdsConnectedToSocialNetwork(String providerId, Set<String> providerUserIds);

    List<User> saveUsers(List<User> list);

    void removeUser(User user);

    void removeUser(String userCode);

    void autoLogin(ObjectId userId, String remoteAddress);

    User updateUserAndRefreshSecurityContext(User user);

    List<User> findUsersBySocialNetwork(String providerId);

    Long findUserCount();

    Long findFacebookUserCount();

    UserSupplement findUserSupplement(String userCode);

    UserSupplement saveUserSupplement(UserSupplement us);

    User findUserByCode(String userCode);

    UserSupplement findUserSupplementByEmail(String email);

    Long findFacebookUserCount(UserSearchQuery query);

    List<Social> saveSocials(String userCode, List<Social> socials);

    List<Social> findSocials(String userCode);

    UserSupplement findUserSupplement(String userCode, List<String> fields);

    Long findUserCount(UserSearchQuery query);

    List<User> findUsersCreatedBetween(Date startDate, Date endDate);

    void removeUserSupplement(String userCode);

    User registerFacebookUser(UserProfile dto);

    User registerWebUser(UserDto dto);

    List<User> registerBootstrappedUsers(List<User> users);
}
