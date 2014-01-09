/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.bootstrap.impl;

import com.mineness.bootstrap.Bootstrapper;
import com.mineness.bootstrap.BootstrapperException;
import com.mineness.domain.document.User;
import com.mineness.domain.dto.Users;
import com.mineness.service.UserService;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: bjorn
 * Date: Nov 4, 2007
 * Time: 11:19:22 AM
 * Inserts required categories into the system
 */
@SuppressWarnings("unchecked")
@Component("userBootstrapper")
public class UserBootstrapper extends AbstractBootstrapper implements Bootstrapper {
    private final static Logger log = LoggerFactory.getLogger(UserBootstrapper.class);
    private final Resource file = new ClassPathResource("bootstrap/users.json");

    @Value("${bootstrapper.user.enabled:true}")
    private Boolean enabled;

    private final UserService userService;

    @Autowired
    public UserBootstrapper(UserService userService) {
        this.userService = userService;
    }


    @Override
    public void create() throws BootstrapperException {

        try {
            if (file.exists()) {
                Users users = mapper.readValue(file.getInputStream(), Users.class);

                log.info("Populated " + users.getList().size() + " users in db");

                userService.registerBootstrappedUsers(users.getList());
            } else {
                throw new BootstrapperException("JSON file could not be found");
            }

        } catch (JsonMappingException e) {
            throw new BootstrapperException(e.getMessage(), e);
        } catch (JsonParseException e) {
            throw new BootstrapperException(e.getMessage(), e);
        } catch (IOException e) {
            throw new BootstrapperException(e.getMessage(), e);
        }

    }

    @Override
    public Boolean getEnabled() {
        return enabled;
    }
}