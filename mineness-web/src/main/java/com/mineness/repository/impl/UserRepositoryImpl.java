package com.mineness.repository.impl;

import com.mineness.domain.document.User;
import com.mineness.repository.UserRepositoryCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Custom user repository
 */
public class UserRepositoryImpl implements UserRepositoryCustom {
    private final static Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<User> findByCodes(List<String> userCodes, List<String> fields) {
        List<User> result = null;

        if (userCodes != null && !userCodes.isEmpty()) {
            Query q = query(where("cd").in(userCodes));

            if (fields != null && !fields.isEmpty()) {
                for (String field : fields) {
                    q.fields().include(field);
                }
            }
            result = mongoTemplate.find(q, User.class);
        }

        return result;
    }

    @Override
    public List<User> findUsers(Integer page, Integer maxResults, List<String> fields) {
        Query query = query(where("id").exists(true));

        query.skip(page * maxResults);
        query.limit(maxResults);

        if (fields != null && !fields.isEmpty()) {
            for (String field : fields) {
                query.fields().include(field);
            }
        }

        // sort on login date
        query.sort().on("lgndt", Order.DESCENDING);

        return mongoTemplate.find(query, User.class);
    }

    @Override
    public List<User> findUsersCreatedBetween(Date beginDate, Date endDate, Integer page, Integer maxResults, List<String> fields) {
        Criteria where = where("id").exists(true);
        if (beginDate != null) {
            where = where.and("cdt").gte(beginDate);
        }
        if (endDate != null) {
            where = where.and("cdt").lte(endDate);
        }

        Query query = query(where);

        query.skip(page * maxResults);
        query.limit(maxResults);

        if (fields != null && !fields.isEmpty()) {
            for (String field : fields) {
                query.fields().include(field);
            }
        }

        // sort on create date
        query.sort().on("cdt", Order.DESCENDING);

        return mongoTemplate.find(query, User.class);
    }

}
