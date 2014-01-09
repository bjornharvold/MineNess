/*
 * Copyright (c) 2012. Purple Door Systems, BV.
 */

package com.mineness.service;

import com.mineness.domain.document.Role;

import java.util.List;

/**
 * Created by Bjorn Harvold
 * Date: 6/4/12
 * Time: 8:31 PM
 * Responsibility:
 */
public interface RoleService {
    List<Role> findRoles();
    Role findRoleByUrlName(String name);
    Role saveRole(Role role);
    Role removeRole(String rlnm);
    List<Role> saveRoles(List<Role> list);
}
