/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */


package com.mineness.spring.security;

//~--- non-JDK imports --------------------------------------------------------

import com.mineness.domain.dto.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 12/20/10
 * Time: 1:26 AM
 * Responsibility:
 */
public class SpringSecurityHelper {
    private static final Logger log = LoggerFactory.getLogger(SpringSecurityHelper.class);
    private static String[] rights = {
            "UPDATE_USER",
            "INSERT_USER",
            "READ_USER",
            "DELETE_USER",

            "READ_ROLE",
            "INSERT_ROLE",
            "DELETE_ROLE"
    };

    public static void secureChannel() {
        log.info("Securing channel...");
        List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();

        for (String right : rights) {
            auths.add(new SimpleGrantedAuthority(right));
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("BOOTSTRAPPER", "BOOTSTRAPPER", auths);
        SecurityContextHolder.getContext().setAuthentication(token);

        log.info("Channel administration");
    }

    public static void secureChannel(String username, String password, Collection<? extends GrantedAuthority> auths) {
        log.info("Securing channel...");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password, auths);
        SecurityContextHolder.getContext().setAuthentication(token);

        log.info("Channel administration");
    }

    public static void secureChannel(Principal principal) {
        log.info("Securing channel...");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);

        log.info("Channel administration");
    }

    public static void unsecureChannel() {
        log.info("Un-securing channel...");
        SecurityContextHolder.getContext().setAuthentication(null);
        log.info("Channel insecure");
    }

    /**
     * Retrieves the Principal from the spring security context. Null if Principal is not logged in.
     *
     * @return Return value
     */
    public static Principal getSecurityContextPrincipal() {
        Principal result = null;
        SecurityContext sc     = SecurityContextHolder.getContext();

        if (sc != null) {
            Authentication authentication = sc.getAuthentication();

            if (authentication != null) {
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                if ((principal != null) && (principal instanceof Principal)) {
                    result = (Principal) principal;
                }
            }
        }

        return result;
    }

    public static Authentication getSecurityContextAuthentication() {
        Authentication result = null;
        SecurityContext sc     = SecurityContextHolder.getContext();

        if (sc != null) {
            result = sc.getAuthentication();
        }

        return result;
    }

    public static SecurityContext getSecurityContext() {
        return SecurityContextHolder.getContext();
    }
}
