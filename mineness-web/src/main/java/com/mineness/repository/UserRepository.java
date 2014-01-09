/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.repository;

import com.mineness.domain.document.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Bjorn Harvold
 * Date: 6/13/11
 * Time: 2:56 PM
 * Responsibility:
 */
public interface UserRepository extends PagingAndSortingRepository<User, ObjectId>, UserRepositoryCustom {
    @Query("{ 'ml' : ?0 }")
    User findByEmail(String email);

    @Query("{ 'cd' : ?0 }")
    User findByCode(String checksum);
}
