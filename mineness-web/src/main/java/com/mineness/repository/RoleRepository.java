/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.repository;

import com.mineness.domain.document.Role;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Bjorn Harvold
 * Date: 6/13/11
 * Time: 2:56 PM
 * Responsibility:
 */
public interface RoleRepository extends PagingAndSortingRepository<Role, ObjectId> {
    @Query("{ 'nm' : ?0 }")
    Role findByName(String name);

    @Query("{ 'rlnm' : ?0 }")
    Role findByUrlName(String urlName);
}
