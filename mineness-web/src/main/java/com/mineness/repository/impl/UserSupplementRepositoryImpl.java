/*
 * Copyright (c) 2012. Purple Door Systems, BV.
 */

package com.mineness.repository.impl;

import com.mineness.ApplicationConstants;
import com.mineness.domain.document.UserSupplement;
import com.mineness.domain.dto.UserSearchQuery;
import com.mineness.repository.UserSupplementRepositoryCustom;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Custom user repository
 */
public class UserSupplementRepositoryImpl implements UserSupplementRepositoryCustom {
    private final static Logger log = LoggerFactory.getLogger(UserSupplementRepositoryImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<UserSupplement> findByCodes(List<String> userCodes, List<String> fields) {
        List<UserSupplement> result = null;

        if (userCodes != null && !userCodes.isEmpty()) {
            Query q = query(where("cd").in(userCodes));

            if (fields != null && !fields.isEmpty()) {
                for (String field : fields) {
                    q.fields().include(field);
                }
            }
            result = mongoTemplate.find(q, UserSupplement.class);
        }

        return result;
    }

    @Override
    public Long findFacebookUserCount(List<String> userCodes) {
        Query q = query(where("scls").elemMatch(where("providerId").is(ApplicationConstants.FACEBOOK)));

        if (userCodes != null && !userCodes.isEmpty()) {
            q.addCriteria(where("cd").in(userCodes));
        }

        return mongoTemplate.count(q, UserSupplement.class);
    }

    @Override
    public UserSupplement findUserSupplement(String userCode, List<String> fields) {
        Query query = query(where("cd").is(userCode));

        if (fields != null && !fields.isEmpty()) {
            for (String field : fields) {
                query.fields().include(field);
            }
        }

        return mongoTemplate.findOne(query, UserSupplement.class);
    }

    @Override
    public List<UserSupplement> findUserSupplementsByQuery(UserSearchQuery query) {
        List<UserSupplement> result = null;

        Query q = createUserSearchQuery(query);

        if (q != null) {
            result = mongoTemplate.find(q, UserSupplement.class);
        }

        return result;
    }

    @Override
    public Long findUserSupplementCountByQuery(UserSearchQuery query) {
        Long result = null;

        Query q = createUserSearchQuery(query);

        if (q != null) {
            result = mongoTemplate.count(q, UserSupplement.class);
        }

        return result;
    }

    private Query createUserSearchQuery(UserSearchQuery query) {
        Query q = null;

        if (query != null) {
            q = new Query();

            if (StringUtils.isNotBlank(query.getFnm())) {
                q.addCriteria(where("fnm").is(query.getFnm()));
            }
            if (StringUtils.isNotBlank(query.getLnm())) {
                q.addCriteria(where("lnm").is(query.getLnm()));
            }
            if (StringUtils.isNotBlank(query.getMl())) {
                q.addCriteria(where("ml").is(query.getMl()));
            }
            if (query.getCdt() != null) {
                q.addCriteria(where("cdt").gte(query.getCdt()));
            }
            if (query.getLdt() != null) {
                q.addCriteria(where("ldt").lte(query.getLdt()));
            }

            if (query.getFields() != null && !query.getFields().isEmpty()) {
                for (String field : query.getFields()) {
                    q.fields().include(field);
                }
            }
        }
        return q;
    }

}
