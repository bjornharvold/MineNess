/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.bootstrap.impl;

import com.mineness.bootstrap.Bootstrapper;
import com.mineness.bootstrap.BootstrapperException;
import com.mineness.domain.document.Role;
import com.mineness.domain.dto.Roles;
import com.mineness.domain.dto.Users;
import com.mineness.service.RoleService;
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
 * Role: bjorn
 * Date: Nov 4, 2007
 * Time: 11:19:22 AM
 * Inserts required roles into the system
 */
@SuppressWarnings("unchecked")
@Component("roleBootstrapper")
public class RoleBootstrapper extends AbstractBootstrapper implements Bootstrapper {
    private final static Logger log = LoggerFactory.getLogger(RoleBootstrapper.class);
    private final Resource file = new ClassPathResource("bootstrap/roles.json");

    @Value("${bootstrapper.role.enabled:true}")
    private Boolean enabled;

    private final RoleService roleService;

    @Autowired
    public RoleBootstrapper(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public void create() throws BootstrapperException {

        try {
            if (file.exists()) {
                Roles roles = mapper.readValue(file.getInputStream(), Roles.class);

                log.info("Populated " + roles.getList().size() + " roles in db");

                roleService.saveRoles(roles.getList());
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