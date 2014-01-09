/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.spring.social;

//~--- non-JDK imports --------------------------------------------------------

import com.mineness.ApplicationConstants;
import com.mineness.domain.document.Social;
import com.mineness.domain.document.User;
import com.mineness.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 7/29/11
 * Time: 12:47 PM
 * Responsibility: This is an implementation of ConnectionRepository that supports MongoDb and the
 * Lela domain model.
 */
public class MongoDbConnectionRepository implements ConnectionRepository {

    /** Field description */
    private final ConnectionFactoryLocator connectionFactoryLocator;

    /** Field description */
    private final String userId;

    /** Field description */
    private final UserService userService;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     *
     * @param userId userId
     * @param userService userService
     * @param connectionFactoryLocator connectionFactoryLocator
     */
    public MongoDbConnectionRepository(String userId, UserService userService,
                                       ConnectionFactoryLocator connectionFactoryLocator) {
        this.userId                   = userId;
        this.userService           = userService;
        this.connectionFactoryLocator = connectionFactoryLocator;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param connection connection
     */
    @Override
    public void addConnection(Connection<?> connection) {
        User user = userService.findUser(new ObjectId(userId));

        if (user != null) {
            int rank = 0;

            List<Social> socials = userService.findSocials(user.getCd());

            if (socials == null) {
                socials = new ArrayList<Social>();
            }

            // find the highest current rank for the providerId (in case of multiple accounts)
            for (Social social : socials) {
                if (StringUtils.equals(social.getProviderId(), connection.getKey().getProviderId())) {
                    if (social.getRank() > rank) {
                        rank = social.getRank();
                    }
                }
            }

            rank += 1;
            Social social = createSocial(connection, rank);

            socials.add(social);

            // save socials
            userService.saveSocials(user.getCd(), socials);
        }
    }

    /**
     * Method description
     *
     *
     * @return Return value
     */
    @Override
    public MultiValueMap<String, Connection<?>> findAllConnections() {
        MultiValueMap<String, Connection<?>> result = new LinkedMultiValueMap<String, Connection<?>>();

        // retrieve the user
        User user = userService.findUser(new ObjectId(userId));

        // add all registered social networks
        Set<String> registeredProviderIds = connectionFactoryLocator.registeredProviderIds();

        for (String registeredProviderId : registeredProviderIds) {
            result.put(registeredProviderId, Collections.<Connection<?>>emptyList());
        }

        if (user != null) {
            List<Social> socials = userService.findSocials(user.getCd());

            // match on users own social networks
            if (socials != null && !socials.isEmpty()) {
                for (Social social : socials) {
                    String providerId = social.getProviderId();

                    if (result.get(providerId).size() == 0) {
                        result.put(providerId, new LinkedList<Connection<?>>());
                    }

                    // associate the user social object with the specific social network
                    result.add(providerId, createConnection(social));
                }
            }
        }

        return result;
    }

    /**
     * Method description
     *
     *
     * @param providerId providerId
     *
     * @return Return value
     */
    @Override
    public List<Connection<?>> findConnections(String providerId) {
        List<Connection<?>> result = new ArrayList<Connection<?>>();

        // retrieve the user
        User user = userService.findUser(new ObjectId(userId));

        if (user != null) {
            List<Social> socials = userService.findSocials(user.getCd());
            // see if the user has the providerId registered
            if (socials != null && !socials.isEmpty()) {
                for (Social social : socials) {
                    if (StringUtils.equals(social.getProviderId(), providerId)) {
                        result.add(createConnection(social));
                    }
                }
            }
        }

        return result;
    }

    /**
     * Method description
     *
     *
     * @param apiType apiType
     * @param <A> <A>
     *
     * @return Return value
     */
    @SuppressWarnings("unchecked")
    @Override
    public <A> List<Connection<A>> findConnections(Class<A> apiType) {
        List<?> connections = findConnections(getProviderId(apiType));

        return (List<Connection<A>>) connections;
    }

    /**
     * Method description
     *
     *
     * @param providerUsers providerUsers
     *
     * @return Return value
     */
    @Override
    public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUsers) {
        if (providerUsers.isEmpty()) {
            throw new IllegalArgumentException("Unable to execute find: no providerUsers provided");
        }

        MultiValueMap<String, Connection<?>> connectionsForUsers = new LinkedMultiValueMap<String, Connection<?>>();

        // retrieve the user
        User user = userService.findUser(new ObjectId(userId));

        if (user != null) {
            List<Social> socials = userService.findSocials(user.getCd());

            // match the existing social networks on this user with the incoming MultiValueMap
            if (socials != null && !socials.isEmpty()) {
                List<Connection<?>> list = new ArrayList<Connection<?>>();

                for (Social social : socials) {
                    for (Map.Entry<String, List<String>> entry : providerUsers.entrySet()) {
                        String providerId      = entry.getKey();
                        List<String> providerUserIds = entry.getValue();

                        if (StringUtils.equals(social.getProviderId(), providerId)) {
                            for (String providerUserId : providerUserIds) {
                                if (StringUtils.equals(social.getProviderUserId(), providerUserId)) {
                                    list.add(createConnection(social));
                                }
                            }
                        }
                    }
                }

                // add all the connections that we found to the MultiValueMap
                if (!list.isEmpty()) {
                    for (Connection<?> connection : list) {
                        String providerId      = connection.getKey().getProviderId();
                        List<String> providerUserIds = providerUsers.get(providerId);
                        List<Connection<?>> connections     = connectionsForUsers.get(providerId);

                        if (connections == null) {
                            connections = new ArrayList<Connection<?>>(providerUserIds.size());

                            for (String providerUserId : providerUserIds) {
                                connections.add(null);
                            }

                            connectionsForUsers.put(providerId, connections);
                        }

                        String providerUserId  = connection.getKey().getProviderUserId();
                        int    connectionIndex = providerUserIds.indexOf(providerUserId);

                        connections.set(connectionIndex, connection);
                    }
                }
            }
        }

        return connectionsForUsers;
    }

    /**
     * Method description
     *
     *
     * @param apiType apiType
     * @param <A> <A>
     *
     * @return Return value
     */
    @SuppressWarnings("unchecked")
    @Override
    public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
        String providerId = getProviderId(apiType);

        return (Connection<A>) findPrimaryConnection(providerId);
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param connectionKey connectionKey
     *
     * @return Return value
     */
    @Override
    public Connection<?> getConnection(ConnectionKey connectionKey) {

        // retrieve the user
        User user = userService.findUser(new ObjectId(userId));

        if (user != null) {
            List<Social> socials = userService.findSocials(user.getCd());
            if (socials != null && !socials.isEmpty()) {
                for (Social social : socials) {

                    // match on both providerId and providerUserId
                    if (StringUtils.equals(social.getProviderId(), connectionKey.getProviderId())
                            && StringUtils.equals(social.getProviderUserId(), connectionKey.getProviderUserId())) {

                        // found what we were looking for
                        return createConnection(social);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Method description
     *
     *
     * @param apiType apiType
     * @param providerUserId providerUserId
     * @param <A> <A>
     *
     * @return Return value
     */
    @SuppressWarnings("unchecked")
    @Override
    public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
        String providerId = getProviderId(apiType);

        return (Connection<A>) getConnection(new ConnectionKey(providerId, providerUserId));
    }

    /**
     * Method description
     *
     *
     * @param apiType apiType
     * @param <A> <A>
     *
     * @return Return value
     */
    @SuppressWarnings("unchecked")
    @Override
    public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
        String providerId = getProviderId(apiType);
        Connection<A> connection = (Connection<A>) findPrimaryConnection(providerId);

        if (connection == null) {
            throw new NotConnectedException(providerId);
        }

        return connection;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param connectionKey connectionKey
     */
    @Override
    public void removeConnection(ConnectionKey connectionKey) {
        User user = userService.findUser(new ObjectId(userId));

        if (user != null) {
            Social remove = null;
            List<Social> socials = userService.findSocials(user.getCd());

            if (socials != null && !socials.isEmpty()) {
                for (Social social : socials) {
                    if (StringUtils.equals(social.getProviderId(), connectionKey.getProviderId())
                            && StringUtils.equals(social.getProviderUserId(), connectionKey.getProviderUserId())) {
                        remove = social;
                    }
                }

                if (remove != null) {
                    socials.remove(remove);
                    userService.saveSocials(user.getCd(), socials);
                }
            }
        }
    }

    /**
     * Method description
     *
     *
     * @param providerId providerId
     */
    @Override
    public void removeConnections(String providerId) {
        User user = userService.findUser(new ObjectId(userId));

        if (user != null) {
            List<Social> remove = new ArrayList<Social>();
            List<Social> socials = userService.findSocials(user.getCd());

            if (socials != null && !socials.isEmpty()) {
                for (Social social : socials) {
                    if (StringUtils.equals(social.getProviderId(), providerId)) {
                        remove.add(social);
                    }
                }

                if (!remove.isEmpty()) {
                    socials.removeAll(remove);
                    userService.saveSocials(user.getCd(), socials);
                }
            }

        }
    }

    /**
     * Method description
     *
     *
     * @param connection connection
     */
    @Override
    public void updateConnection(Connection<?> connection) {
        User user = userService.findUser(new ObjectId(userId));

        if (user != null) {
            Social update = null;
            List<Social> socials = userService.findSocials(user.getCd());

            if (socials != null && !socials.isEmpty()) {
                for (Social social : socials) {
                    if (StringUtils.equals(social.getProviderId(), connection.getKey().getProviderId())
                            && StringUtils.equals(social.getProviderUserId(), connection.getKey().getProviderUserId())) {
                        update = social;
                    }
                }

                if (update != null) {
                    socials.remove(update);
                    update = createSocial(connection, null);
                    socials.add(update);
                    userService.saveSocials(user.getCd(), socials);
                }
            }
        }
    }

    /**
     * Method description
     *
     *
     * @param social social
     *
     * @return Return value
     */
    private Connection<?> createConnection(Social social) {
        ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(social.getProviderId());

        return connectionFactory.createConnection(new ConnectionData(social.getProviderId(),
                social.getProviderUserId(), social.getDisplayName(), social.getProfileUrl(), social.getImageUrl(),
                social.getAccessToken(), social.getSecret(), social.getRefreshToken(), social.getExpireTime()));
    }

    /**
     * Method description
     *
     *
     * @param connection connection
     * @param rank rank
     *
     * @return Return value
     */
    private Social createSocial(Connection<?> connection, Integer rank) {

        // get ready to create a new Social object
        ConnectionData data = connection.createData();

        return new Social(data.getProviderUserId(), data.getProviderId(), rank, data.getDisplayName(),
                          data.getProfileUrl(), data.getImageUrl(), data.getAccessToken(), data.getSecret(),
                          data.getRefreshToken(), data.getExpireTime());
    }

    /**
     * Method description
     *
     *
     * @param providerId providerId
     *
     * @return Return value
     */
    private Connection<?> findPrimaryConnection(String providerId) {

        // retrieve the user
        User user = userService.findUser(new ObjectId(userId));

        if (user != null) {
            List<Social> socials = userService.findSocials(user.getCd());

            if (socials != null && !socials.isEmpty()) {
                List<Connection<?>> connections = new ArrayList<Connection<?>>();

                // here we are trying to find the highest ranked social network connection if you have multiple accounts
                for (Social social : socials) {
                    if (StringUtils.equals(social.getProviderId(), providerId) &&
                            ((social.getRank() != null) && (social.getRank() == 1)) ||
                            (StringUtils.equals(social.getProviderId(), ApplicationConstants.FACEBOOK))) {
                        connections.add(createConnection(social));
                    }
                }

                if (connections.size() > 0) {
                    return connections.get(0);
                } else {
                    return null;
                }
            }
        }

        return null;
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param apiType apiType
     * @param <A> <A>
     *
     * @return Return value
     */
    private <A> String getProviderId(Class<A> apiType) {
        return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
    }
}
