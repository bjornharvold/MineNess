/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.spring.social;

import com.mineness.domain.document.User;
import com.mineness.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Bjorn Harvold
 * Date: 8/26/11
 * Time: 3:41 PM
 * Responsibility:
 *
 * The UserConnectionSignUp is Session scoped so that the new User can be given the anonymous ID in the Session
 */
@Component("userConnectionSignUp")
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserConnectionSignUp implements ConnectionSignUp {

    @Autowired
    private UserService userService;

    @Override
    public String execute(Connection<?> connection) {
        UserProfile profile = connection.fetchUserProfile();

        User user = findOrCreateUser(profile);

        return user != null ? user.getId().toString() : null;
    }

    private User findOrCreateUser(UserProfile profile) {
        if(profile == null || profile.getEmail() == null) {
            return null;
        }
        
        // Does this user already exist?
        User user = userService.findUserByEmail(profile.getEmail());

        // User did not already exist, register it now
        if (user == null) {
            user = userService.registerFacebookUser(profile);
        }

        // Return existing or new user
        return user;
    }
}
