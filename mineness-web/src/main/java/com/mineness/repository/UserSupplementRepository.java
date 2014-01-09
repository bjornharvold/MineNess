/*
 * Copyright (c) 2012. Purple Door Systems, BV.
 */

package com.mineness.repository;

import com.mineness.domain.document.UserSupplement;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


/**
 * Created by Bjorn Harvold
 * Date: 6/13/11
 * Time: 2:56 PM
 * Responsibility:
 */
public interface UserSupplementRepository extends PagingAndSortingRepository<UserSupplement, ObjectId>, UserSupplementRepositoryCustom {

    @Query("{'cd' : ?0}")
    UserSupplement findUserSupplementByCode(String userCode);

    @Query("{'ml' : ?0}")
    UserSupplement findUserSupplementByEmail(String email);

    @Query(value = "{ 'scls.providerId' : ?0, 'scls.providerUserId' : ?1 }", fields = "{ 'cd' : 1 }")
    List<UserSupplement> findUserIdsBySocialNetwork(String providerId, String providerUserId);

    @Query(value = "{ 'scls.providerId' : ?0, 'scls.providerUserId' : { $in : ?1 } }", fields = "{ 'cd' : 1 }")
    List<UserSupplement> findUserIdsConnectedToSocialNetwork(String providerId, Set<String> providerUserIds);

    @Query("{ 'scls.providerId': ?0 }")
    List<UserSupplement> findUsersBySocialNetwork(String providerId);

}
