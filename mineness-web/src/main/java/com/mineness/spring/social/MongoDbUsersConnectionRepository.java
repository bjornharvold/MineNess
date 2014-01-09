/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.spring.social;

import com.mineness.domain.document.User;
import com.mineness.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Bjorn Harvold
 * Date: 7/29/11
 * Time: 11:36 AM
 * Responsibility:
 */
@Component("usersConnectionRepository")
public class MongoDbUsersConnectionRepository implements UsersConnectionRepository {
    private final static Logger log = LoggerFactory.getLogger(MongoDbUsersConnectionRepository.class);

    private final ConnectionFactoryLocator connectionFactoryLocator;
    private final UserService userService;
    private final ConnectionSignUp connectionSignUp;

    @Autowired
    public MongoDbUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator,
                                            UserService userService,
                                            ConnectionSignUp connectionSignUp) {
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.userService = userService;
        this.connectionSignUp = connectionSignUp;
    }

    @Override
    public List<String> findUserIdsWithConnection(Connection<?> connection) {
        ConnectionKey key = connection.getKey();
        List<String> result = null;

        List<User> users = userService.findUserIdsBySocialNetwork(key.getProviderId(), key.getProviderUserId());

        if (users != null && !users.isEmpty()) {
            result = new ArrayList<String>();

            for (User user : users) {
                result.add(user.getId().toString());
            }
        } else {
            if (connectionSignUp != null) {
				String newUserId = connectionSignUp.execute(connection);

                if (newUserId != null) {
				    createConnectionRepository(newUserId).addConnection(connection);
				    result = Arrays.asList(newUserId);
                }
			}
        }

        return result;
    }

    @Override
    public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
        Set<String> result = null;

        List<User> users = userService.findUserIdsConnectedToSocialNetwork(providerId, providerUserIds);

        if (users != null) {
            result = new HashSet<String>();

            for (User user : users) {
                result.add(user.getId().toString());
            }
        }

        return result;
    }

    @Override
    public ConnectionRepository createConnectionRepository(String userId) {
        if (userId == null) {
			throw new IllegalArgumentException("userId cannot be null");
		}

        if (log.isDebugEnabled()) {
            log.debug("Creating a new ConnectionRepository for userId: " + userId);
        }

        return new MongoDbConnectionRepository(userId, userService, connectionFactoryLocator);
    }
}
