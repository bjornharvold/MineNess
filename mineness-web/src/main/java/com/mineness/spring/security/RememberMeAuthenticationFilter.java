package com.mineness.spring.security;

import com.mineness.domain.document.User;
import com.mineness.domain.dto.Principal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.RememberMeServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: Chris Tallent
 * Date: 7/31/12
 * Time: 10:43 AM
 */
public class RememberMeAuthenticationFilter extends org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter {

    public RememberMeAuthenticationFilter(AuthenticationManager authenticationManager,
                                          RememberMeServices rememberMeServices) {
        super(authenticationManager, rememberMeServices);
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        super.onSuccessfulAuthentication(request, response, authentication);
    }
}
