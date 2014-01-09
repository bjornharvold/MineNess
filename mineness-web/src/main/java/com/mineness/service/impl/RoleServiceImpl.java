/*
 * Copyright (c) 2012. Purple Door Systems, BV.
 */

package com.mineness.service.impl;

import com.mineness.ApplicationConstants;
import com.mineness.domain.document.Role;
import com.mineness.repository.RoleRepository;
import com.mineness.service.CacheService;
import com.mineness.service.RoleService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bjorn Harvold
 * Date: 6/4/12
 * Time: 8:28 PM
 * Responsibility:
 */
@Service("roleService")
public class RoleServiceImpl extends AbstractCacheableService implements RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(CacheService cacheService, RoleRepository roleRepository) {
        super(cacheService);
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> findRoles() {
        return (List<Role>) roleRepository.findAll();
    }

    /**
     * Method description
     *
     * @param urlName name
     * @return Return value
     */
    @Override
    public Role findRoleByUrlName(String urlName) {
        Role result = null;

        Cache.ValueWrapper wrapper = retrieveFromCache(ApplicationConstants.ROLE_CACHE, urlName);

        if (wrapper != null && wrapper.get() != null && wrapper.get() instanceof Role) {
            result = (Role) wrapper.get();
        } else {
            result = roleRepository.findByUrlName(urlName);

            if (result != null) {
                putInCache(ApplicationConstants.ROLE_CACHE, urlName, result);
            }
        }

        return result;
    }

    /**
     * Method description
     *
     * @param rlnm role
     */
    @PreAuthorize("hasAnyRole('RIGHT_DELETE_ROLE_AS_ADMIN', 'RIGHT_CONTENT_INGEST')")
    @Override
    public Role removeRole(String rlnm) {
        Role role = roleRepository.findByUrlName(rlnm);
        if (role != null) {
            roleRepository.delete(role);

            // Remove from cache
            removeFromCache(ApplicationConstants.ROLE_CACHE, role.getRlnm());
        }

        return role;
    }

    /**
     * Method description
     *
     * @param role role
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('RIGHT_INSERT_ROLE_AS_ADMIN')")
    @Override
    public Role saveRole(Role role) {
        Role result = roleRepository.save(role);

        // Clear cache
        removeFromCache(ApplicationConstants.ROLE_CACHE, role.getRlnm());

        return result;
    }

    /**
     * Method description
     *
     * @param list list
     * @return Return value
     */
    @PreAuthorize("hasAnyRole('INSERT_ROLE')")
    @Override
    public List<Role> saveRoles(List<Role> list) {
        List<Role> result = (List<Role>) roleRepository.save(list);

        // Remove from cache
        if (list != null && !list.isEmpty()) {
            List<String> cacheKeys = new ArrayList<String>(list.size());

            for (Role item : list) {
                cacheKeys.add(item.getRlnm());
            }

            removeFromCache(ApplicationConstants.ROLE_CACHE, cacheKeys);
        }

        return result;
    }

    /**
     * Method description
     *
     *
     * @param id id
     *
     * @return Return value
     */
    private Role findRoleById(ObjectId id) {
        return roleRepository.findOne(id);
    }

}
