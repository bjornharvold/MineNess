package com.mineness.spring.security;

import com.mineness.domain.document.User;
import com.mineness.domain.dto.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: Chris Tallent
 * Date: 7/30/12
 * Time: 7:11 PM
 */
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final RememberMeServices rememberMeServices;

    public AuthenticationSuccessHandler(RememberMeServices rememberMeServices) {
        this.rememberMeServices = rememberMeServices;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // Track the login
        Object principal = authentication.getPrincipal();
        if (principal instanceof Principal) {
            User user = ((Principal)principal).getUser();

            // Merge the transient and persistent user
            rememberMeServices.loginSuccess(request, response, authentication);
        }

        // Allow the Spring implementation to redirect to the configured URL
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
