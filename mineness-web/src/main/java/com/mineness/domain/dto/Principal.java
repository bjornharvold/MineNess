/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.domain.dto;

import com.mineness.domain.document.Role;
import com.mineness.domain.document.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Bjorn Harvold
 * Date: 6/19/11
 * Time: 1:55 PM
 * Responsibility:
 */
public final class Principal implements UserDetails {

    private final User user;

    public Principal(String username) {
        user = new User();
        user.setMl(username);
    }
    
    public Principal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> result = null;

        if (user != null) {
            if (user.getRoles() != null) {
                result = new ArrayList<GrantedAuthority>();

                for (Role ur : user.getRoles()) {
                    for (String right : ur.getRghts()) {
                        result.add(new SimpleGrantedAuthority(right));
                    }
                }
            }
        }

        return result;
    }

    @Override
    public String getPassword() {
        return user != null ? user.getPsswrd() : null;
    }

    @Override
    public String getUsername() {
        return user != null ? user.getMl() : null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return user != null ? user.getNxprd() : false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user != null ? user.getNlckd() : false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user != null ? user.getCrdnxprd() : false;
    }

    @Override
    public boolean isEnabled() {
        return user != null ? user.getNbld() : false;
    }

    public User getUser() {
        return user;
    }
}
